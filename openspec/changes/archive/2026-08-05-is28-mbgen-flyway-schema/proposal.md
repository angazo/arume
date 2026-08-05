# Proposal: Esquema migrado para el generador de entidades MyBatis

## Why

La tarea `mbGenerator` introspecta una base de datos H2 en fichero (`docs/h2/mybatis.mv.db`) que hay que mantener a mano y que con frecuencia queda desactualizada respecto a las migraciones Flyway (o bloqueada por procesos abiertos). El generador debe trabajar siempre sobre un esquema limpio y fiel a las migraciones del proyecto.

## What Changes

- **Mover las migraciones Flyway** de `arume-app/src/main/resources/db/migration/` a `arume-db/src/main/resources/db/migration/` (la capa de datos pasa a ser la dueña del esquema). `arume-app` depende de `arume-db`, por lo que tanto el arranque (`DatabaseConfiguration`, ya ubicado en arume-db) como el test de migraciones siguen resolviendo `classpath:db/migration`.
- **La tarea `mbGenerator` prepara la base de datos antes de generar**: en su JVM forkeado, aplica primero `Flyway.migrate()` sobre una **base de datos H2 en memoria** y a continuación ejecuta MyBatis Generator contra ese esquema recién creado.
- **La conexión del generador se define en un solo sitio**: `mbg.xml` referencia la URL mediante la propiedad `${mbgen.url}` que la tarea inyecta (mismo mecanismo que `${projectDir}`). Se elimina el fichero `docs/h2/mybatis.mv.db` (y sus `*.trace.db`), que deja de ser necesario.
- **La tarea declara como entradas** el `mbg.xml` y los scripts de migración, de modo que Gradle la re-ejecuta cuando cambia el esquema.

## Capabilities

### New Capabilities
- `mbg-code-generation`: la tarea `mbGenerator` genera entidades y mappers MyBatis a partir de la base de datos definida por las migraciones Flyway del proyecto, aplicadas sobre una BBDD H2 en memoria limpia en cada ejecución.

### Modified Capabilities
- *(ninguna: no cambia comportamiento de producto; el arranque y el test de migraciones conservan su contrato)*

## Impact

- **Ficheros**: se mueven `V0.1.0.0__init_schema.sql` y `V0.1.0.1__countries_and_currencies.sql` a `arume-db/src/main/resources/db/migration/`; se borran las copias en arume-app (y en `arume-app/build/resources`). Se elimina `arume-db/docs/h2/`.
- **Código**: se amplía `com.angazo.arume.db.generator.MbGeneratorMain` para aplicar Flyway antes de generar; se ajusta `arume-db/build.gradle` (entradas de la tarea y argumentos) y `arume-db/MyBatis/mbg.xml` (`connectionURL` con `${mbgen.url}`).
- **Dependencias**: ninguna nueva; `arume-db` ya declara `flyway-core`, `h2` y los artefactos de MyBatis Generator.
- **Documentación**: se actualiza `AGENTS.md` (ubicación de las migraciones y funcionamiento de `mbGenerator`).
