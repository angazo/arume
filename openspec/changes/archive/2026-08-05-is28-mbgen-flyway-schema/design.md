# Design: Esquema migrado para el generador de entidades MyBatis

## Context

La tarea `mbGenerator` (creada en el change anterior como `JavaExec` con `com.angazo.arume.db.generator.MbGeneratorMain`) introspecta una base de datos H2 **en fichero** (`arume-db/docs/h2/mybatis.mv.db`) que no se actualiza sola: quedó desactualizada respecto a las migraciones (`t3_country_currency` no resuelve) y además, como MBG 2.0 no cierra su conexión JDBC, un proceso de larga vida (daemon de Gradle) podía dejarla bloqueada.

Hoy las migraciones viven en `arume-app/src/main/resources/db/migration/`, pero quien las ejecuta es `DatabaseConfiguration` (en `arume-db`, `locations("classpath:db/migration")`), y el test de migraciones de arume-app también resuelve `classpath:db/migration`. `arume-app` depende de `arume-db`, por lo que mover los scripts a arume-db no cambia ningún consumidor.

## Goals / Non-Goals

**Goals:**
- El generador trabaja siempre sobre un esquema limpio igual al de las migraciones del proyecto, sin ficheros de BBDD que mantener.
- La capa de datos (arume-db) es la dueña del esquema y de la herramienta de generación.
- La conexión del generador se define una sola vez.
- La tarea se re-ejecuta automáticamente cuando cambia el esquema.

**Non-Goals:**
- No se toca la base de datos de ejecución de la aplicación (`jdbc:h2:file:<path>/arume`); esta solo aplica para la BBDD de *scratch* del generador.
- No se cambia el contrato de arranque ni del test de migraciones (siguen resolviendo `classpath:db/migration`).
- No se genera código nuevo ni se decide qué hacer con el código generado (committearlo o no).

## Decisions

### D1. Las migraciones pasan a `arume-db/src/main/resources/db/migration/`
**Decisión:** mover `V0.1.0.0__init_schema.sql` y `V0.1.0.1__countries_and_currencies.sql` de arume-app a arume-db, y borrar las copias de arume-app (incluido `build/resources/main/db/migration/`).

**Por qué:** arume-db es la capa de datos y ya alberga el ejecutor de Flyway (`DatabaseConfiguration`) y la herramienta de generación. Al depender arume-app de arume-db, el arranque y el test siguen encontrando `classpath:db/migration`.

**Alternativa descartada:** dejar los scripts en arume-app y apuntar la tarea al directorio de migraciones de arume-app. Se descarta porque acopla la herramienta de arume-db a un módulo de aplicación y mantiene el esquema fuera de la capa que lo consume.

### D2. BBDD del generador en memoria, creada en cada ejecución
**Decisión:** la tarea aplica `Flyway.migrate()` sobre `jdbc:h2:mem:mbgen;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1` y luego genera contra ella. Se elimina `arume-db/docs/h2/` (fichero `.mv.db` y `.trace.db`).

**Por qué:** es limpia por construcción (el requisito de "siempre limpia"), sin ficheros que limpiar ni bloqueos posibles; el mismo modo H2 que usa la app garantiza compatibilidad de las migraciones.

**Alternativa descartada:** recrear el fichero en cada ejecución (borrar y migrar). Implica lógica de borrado, artefactos en disco y posibilidad de bloqueos residuales; solo aporta poder inspeccionar el esquema con DBeaver, que no es un objetivo.

**Nota técnica:** `DB_CLOSE_DELAY=-1` es imprescindible en la URL en memoria para que la BBDD sobreviva entre el cierre de la conexión de Flyway y la conexión que abre MBG dentro del mismo JVM forkeado.

### D3. Conexión del generador definida una sola vez en `build.gradle`
**Decisión:** `arume-db/build.gradle` define `mbgenUrl`, `mbgenUser` y `mbgenPassword`, los pasa a `MbGeneratorMain` como argumentos (Flyway) y los inyecta como propiedades en `mbg.xml` (`${mbgen.url}`, `${mbgen.user}`, `${mbgen.password}`), mismo mecanismo que el `${projectDir}` actual.

**Por qué:** una única fuente de verdad para la conexión, compartida entre el parseo de la configuración (MBG) y la migración previa (Flyway).

**Alternativa descartada:** fijar la URL tanto en `mbg.xml` como en la clase `main`. Duplica el dato y facilita desincronizaciones.

### D4. Flyway se ejecuta en el mismo JVM forkeado
**Decisión:** `MbGeneratorMain.main` recibe como argumentos la ruta del `mbg.xml`, `projectDir`, `mbgenUrl`, `mbgenUser` y `mbgenPassword`; primero ejecuta `Flyway.migrate()` con esos datos (`locations("classpath:db/migration")`) y después lanza MBG con las propiedades inyectadas.

**Por qué:** se reutiliza el `JavaExec` existente y su `runtimeClasspath` (ya incluye `flyway-core`, `h2` y los artefactos de MBG); no hace falta añadir dependencias. La conexión abierta por MBG se libera al terminar el JVM efímero.

## Risks / Trade-offs

- [La BBDD en memoria podría descartarse entre la conexión de Flyway y la de MBG] → `DB_CLOSE_DELAY=-1` en la URL del generador mantiene viva la BBDD dentro del JVM.
- [Mover las migraciones podría romper el arranque o el test si dejaran de resolverse en el classpath] → verificado: `arume-app` depende de `arume-db`; tanto `DatabaseConfiguration` como `CountriesCurrenciesMigrationTest` resuelven `classpath:db/migration`. Se comprobará con `./gradlew build` completo.
- [Las tablas generadas dependen del modo H2 (nombres en minúsculas, schema `public`)] → se valida en la implementación que `t3_country_currency` resuelve tras migrar (hoy no resolvía por estar el fichero desactualizado).
- [Gradle podría avisar de tarea sin outputs declarados al escribir en `src/main/java`] → aceptable para una herramienta de desarrollo; no bloquea.

## Migration Plan

1. Mover los dos scripts de migración a `arume-db/src/main/resources/db/migration/` y borrar las copias en arume-app.
2. Actualizar `arume-db/build.gradle` (argumentos y `inputs` de la tarea: `MyBatis/mbg.xml` + migraciones) y `MyBatis/mbg.xml` (propiedades `${mbgen.*}`).
3. Ampliar `MbGeneratorMain` para aplicar Flyway antes de generar.
4. Eliminar `arume-db/docs/h2/`.
5. Actualizar `AGENTS.md` (ubicación de migraciones, funcionamiento de `mbGenerator`).
6. Verificar con `./gradlew :arume-db:mbGenerator` (la tabla `t3_country_currency` debe resolver y generarse) y `./gradlew build`.
