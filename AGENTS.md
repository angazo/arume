# AGENTS.md — Contexto del proyecto para agentes de IA

> Este fichero es la fuente de verdad del contexto de trabajo. Si trabajas como agente en este
> repositorio, lee esto primero. 

## Objetivo del proyecto

Crear una aplicación que permita a un particular o empresa llevar su facturación y contabilidad.

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Build | Gradle 9.6 (multimódulo) |
| Framework | Spring Boot 4.1 |
| ORM / acceso a datos | MyBatis Spring Boot 4.0 |
| Base de datos | H2 (modo compatibilidad PostgreSQL) |
| Migraciones BBDD | Flyway Community |
| UI | JavaFX 25 + AtlantaFX 2.1 |
| Testing | JUnit 5 (JUnitPlatform) |
| Utilidades | Lombok, Logback |

## Estructura del proyecto

```
src/
├── arume-app/    → Punto de entrada Spring Boot, configuración
├── arume-db/     → Capa de datos: mappers MyBatis, entidades, repositorios, migraciones Flyway
└── arume-ui/     → Interfaz JavaFX (controladores, vistas FXML, recursos)

Paquete base: com.angazo.arume
```

**Módulos:**

| Módulo | Responsabilidad |
|---|---|
| `arume-app` | Arranque de la aplicación (`ArumeApp`), configuración Spring Boot, recursos (`application.yml`) |
| `arume-db` | Acceso a datos: mappers MyBatis, entidades, configuración de base de datos, migraciones Flyway en `db/migration/` |
| `arume-ui` | Interfaz de usuario JavaFX: controladores, vistas FXML, tema AtlantaFX |

## Comandos de uso frecuente

```bash
./gradlew build        # Compilar y ejecutar tests
./gradlew test         # Solo tests
./gradlew bootRun      # Ejecutar la aplicación
./gradlew bootJar      # Generar JAR FAT para distribución
```

## Estado actual

- **Fase actual:** Fase 0 — configuración en el arranque
- **Último hito:** Primera prueba automatizada de interfaz (issue #39): integrada la dependencia TestFX 4.0.18 con Hamcrest, creada
  `FirstRunWizardUiTest` sobre el FXML y controlador reales, añadidos identificadores CSS estables al wizard y configurada la ejecución de la
  CI dentro de Xvfb. Capabilities afectadas: cobertura automatizada de `first-run-wizard`.
- **Próximo hito:** Ampliar la cobertura UI con validaciones del wizard (issue #41), smoke test del arranque completo (issue #43) e investigación de
  Monocle compatible con JavaFX 25 (issue #42).

## Convenciones de código

- **Lombok**: se usa para reducir boilerplate (`@Slf4j`, `@Getter`, `@Setter`, etc.)
- **Java 25**: se aprovechan características modernas (`var`, text blocks, pattern matching, etc.)
- **UI JavaFX**: vistas definidas en FXML, controladores Java como `@Controller` de Spring
- **Arranque híbrido**: `ArumeAppFX.launch()` levanta JavaFX, que a su vez arranca Spring Boot vía `SpringApplication.run()`
- **Base de datos**: H2 en modo PostgreSQL (`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`); en desarrollo se usa `jdbc:h2:mem:arume`
- **Migraciones**: Flyway Community, scripts SQL versionados en `arume-db/src/main/resources/db/migration/`
- **Generador MyBatis (tarea `mbGenerator`)**: la tarea de `arume-db` corre en un JVM forkeado (`JavaExec`) y ejecuta `com.angazo.arume.db.generator.MbGeneratorMain`, que primero aplica `Flyway.migrate()` a una BBDD H2 en memoria (`jdbc:h2:mem:mbgen;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`) y después MyBatis Generator, de modo que el código generado refleja siempre las migraciones. La conexión se define una sola vez en `arume-db/build.gradle` y se inyecta en `MyBatis/mbg.xml` como `${mbgen.url}` / `${mbgen.user}` / `${mbgen.password}` (además de `${projectDir}`). Código generado: entidades en `com.angazo.arume.db.model`, repositorios en `com.angazo.arume.db.repository.generated` y mappers XML en `src/main/resources/mappers/`.
- **Ventanas modales**: todas usan `StageStyle.UNDECORATED` con barra de título custom (`.title-bar`, 40px) y botón de cierre. Consistencia visual con la ventana principal.
- **Iconos**: Ikonli (`ikonli-javafx:12.3.1`) con packs FontAwesome5 y MaterialDesign2. Usar `FontIcon` para todos los iconos de la UI.
  - **Banderas de países**: Ikonli no cubre banderas por país. Se usan PNGs de 96×72 (3× del tamaño de visualización 32×24) en
    `arume-ui/src/main/resources/icons/flags/<iso3>.png` (ISO-3 minúsculas), cargados vía `ImageView` con fit 32×24; el downsampling de
    JavaFX mantiene la nitidez en pantallas HiDPI 1×/2×/3× sin lógica de DPI. Los PNGs se generan desde los SVGs de flag-icons
    (licencia MIT, set 4×3 apaisado) alojados en `docs/banderas/` (nombrados por ISO-2, p. ej. `es.svg`). **Regeneración**: `rsvg-convert -w 96 -h 72 -o <iso3>.png <iso2>.svg`
    (p. ej. `rsvg-convert -w 96 -h 72 -o esp.png es.svg`). Desde is23 los PNGs son **recursos inertes** (sin catálogo en la capa de aplicación; la
    bandera se mostrará a futuro con la empresa activa, issue #38) y `FlagResourcesTest` garantiza su existencia/dimensiones.
- **País vs idioma**: conceptos desacoplados y el país ya no es un dato de la aplicación: no se elige en el wizard ni se persiste en `arume.yml`
  (is23). La bandera del país aparecerá a futuro ligada a la empresa activa (issue #38). El idioma de la UI es la única preferencia global
  conmutable en cualquier momento (`arume.language`); el botón de idioma en la barra superior muestra el nombre del idioma activo en texto (sin bandera).
- **Códigos de país (ISO 3166-1)**: representación canónica en la **BBDD** (`t1_countries`) con alpha-3 en **mayúsculas** (`ESP`). Los PNGs de
  banderas usan **minúsculas** (`esp.png`). El futuro mapeo empresa→bandera (issue #38) resolverá `t1_countries.alpha_3` →
  `icons/flags/<alpha3 minúsculas>.png`.
- **CSS**: `arume.css` en `src/arume-ui/src/main/resources/css/` extiende AtlantaFX con variables de acento verde. Cargar vía `scene.getStylesheets().add()`.
- **Temas**: solo Claro (PrimerLight) y Oscuro (Dracula). Paleta de acentos verde (`-color-accent-*`) overrida en `.root` de `arume.css`.
- **Nombrado de objetos de BBDD**: todo en minúsculas y en inglés, palabras separadas por guiones bajos. Cada tabla se prefija con `t<n>_` donde `n` es un identificador numérico incremental (0, 1, 2…):
  - Tablas: `t0_app_config`, `t1_invoices`, `t2_invoice_lines`
  - PK: `pk_t<n>` (ej. `pk_t0`, `pk_t1`)
  - FK: `fk_t<origen>_t<destino>` (ej. `fk_t2_t1`). Si hay varias entre las mismas tablas: `fk_t2_t1_1`, `fk_t2_t1_2`
  - UK: `uk_t<n>_<descripción>` (ej. `uk_t0_key`). Si hay varias: `uk_t0_key_1`, `uk_t0_key_2`
  - Índices: `ix_t<n>_<descripción>` (ej. `ix_t1_date`). Si hay varios: `ix_t1_date_1`
- **Columnas de catálogos ISO**: códigos numéricos (país/divisa) como `SMALLINT` (rango 0–999, SQL estándar) y PK; alpha-3 como `VARCHAR(3)` en **mayúsculas** con UK; nombres en inglés como `VARCHAR(100)`; símbolo de divisa como `VARCHAR(8)`.
- **Migraciones con datos**: el seed de catálogos va en la misma migración que crea las tablas; los tests de migración se ubican en `arume-app/src/test/` y ejecutan Flyway contra H2 en memoria (`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE`).
- **Idioma**: código fuente en inglés (nombres de clases, métodos, variables, comentarios y logs) para facilitar la participación de la comunidad. Documentación del proyecto (AGENTS.md, Product-Spec.md, openspec/) en español
- **Commits**: mensajes de commit en inglés, siguiendo conventional commits (feat:, fix:, docs:, etc.)
- **Paquete base**: `com.angazo.arume`

## Pruebas de la aplicación

- **Capas**: mantener separadas las pruebas unitarias, las pruebas de integración con H2/Flyway/MyBatis y las pruebas funcionales de interfaz JavaFX. Las primeras deben cubrir la lógica aislada; las segundas, la persistencia; las terceras, los flujos que realiza el usuario.
- **Pruebas UI**: usar TestFX con JUnit 5 (`org.testfx:testfx-junit5:4.0.18`) cuando se implementen las primeras pruebas de interfaz. AssertJ es opcional; no es necesario añadirlo para empezar.
- **Hilo JavaFX**: las pruebas UI deben usar `ApplicationExtension` de TestFX y su `FxRobot`; no manipular escenas o controles desde un hilo JUnit arbitrario ni resolver la sincronización con `Thread.sleep()`.
- **Vistas reales**: cargar el FXML real mediante `FXMLLoader` y utilizar el controlador real. No reconstruir dentro del test una versión simplificada de la vista.
- **Primer test UI**: el primer caso previsto es `FirstRunWizardUiTest`, sobre `arume-ui/src/main/resources/fxml/first-run-wizard.fxml` y `FirstRunWizardController`. Debe probar una configuración válida: cargar el wizard, escribir ruta temporal, usuario y contraseñas, guardar y verificar el `WizardResult` y el cierre de la ventana.
- **Aislamiento**: no arrancar `ArumeAppFX` ni Spring Boot para probar inicialmente el wizard. Ese arranque accede a `arume.yml`, al filesystem, al datasource y a ventanas modales; se reservará para futuros smoke tests. Usar `@TempDir`, no modificar la configuración real del usuario y cerrar todas las ventanas y diálogos.
- **Estado global**: fijar explícitamente el idioma y el tema en cada prueba. Controlar los listeners estáticos de `I18nManager` para que una prueba no afecte a otra.
- **Selectores**: los controles importantes deben tener un `id` CSS estable para TestFX, además de `fx:id` cuando el controlador necesite inyección. No usar el texto visible como selector principal porque cambia con la internacionalización.
- **Orden recomendado**: wizard válido, validaciones del wizard, navegación de la ventana principal, cambio de idioma, cambio de tema, diálogo About y, finalmente, pocos smoke tests del arranque completo.
- **Entorno gráfico local**: JavaFX necesita un `DISPLAY` accesible. La ejecución local se hace desde `src` con `./gradlew :arume-ui:test` o `./gradlew test` en una sesión gráfica.
- **CI**: GitHub Actions ejecuta Linux sin monitor. Las pruebas UI deben ejecutarse dentro de Xvfb, por ejemplo `xvfb-run --auto-servernum --server-args="-screen 0 1920x1080x24" ./gradlew build` desde `src`. Xvfb proporciona una pantalla virtual en memoria y no necesita un monitor físico.
- **Monocle**: no añadir automáticamente `openjfx-monocle` 21 a este proyecto, que usa JavaFX 25. Las versiones disponibles para TestFX están orientadas principalmente a JavaFX 21 o anteriores y podrían no ser compatibles. La estrategia inicial es usar Xvfb en CI e investigar Monocle solo si fuese necesario.
- **Referencia**: `docs/test-JavaFX.md` contiene la explicación completa y para principiantes de esta estrategia.

## Ficheros clave

| Fichero | Contenido |
|---|---|
| `docs/Product-Spec.md` | Especificación completa de producto y decisiones de diseño |
| `openspec/specs/` | Baseline de specs (spec-driven) |
| `openspec/changes/` | Changes activos y archivados |
| `openspec/config.yaml` | Configuración de OpenSpec |
| `src/arume-app/src/main/resources/application.yml` | Configuración Spring Boot |
| `src/arume-db/src/main/resources/db/migration/` | Migraciones Flyway |
| `src/build.gradle` | Configuración raíz de Gradle (subproyectos, Java 25, JUnit) |

## Hoja de ruta (fases)

> Haciendo uso de GitHub y sus issues y milestones, iremos definiendo de forma incremental
> las funcionalidades del proyecto. Nos ayudaremos de agentes de IA y de OpenSpec para
> ir definiendo cada "change" e implementándolo.

## Preferencias de trabajo del usuario

- **Comunicación en español.**
- Todo análisis y decisión relevante se documenta en `docs/Product-Spec.md`
  , mantenerlo actualizado a medida que se cierren temas.
- El usuario prefiere contexto durable en ficheros del repo (este AGENTS.md) antes que en
  memoria interna del agente, para que sobreviva a clones/moves del repositorio.
- **`openspec/` es público a propósito**: el usuario lo publica como
  registro didáctico de cómo se desarrolla el proyecto asistido por agentes de IA. Al escribir
  proposals/designs/specs/tasks: audiencia pública — autocontenidos, sin referencias a los
  ficheros privados (docs/Product-Spec.md) y manteniendo el tono didáctico.

## Flujo de trabajo (OpenSpec) — regla importante

El flujo de trabajo usa **OpenSpec** (`openspec/`): cada cambio se propone, se
implementa y se archiva. El baseline de specs vive en `openspec/specs/`
y los cambios archivados en`openspec/changes/archive/`.

Para cada change: crear primero el **`proposal.md`** y el **`design.md`** (y
`tasks.md` + `specs/`) y **DETENERSE**. **No pasar a ejecutar/implementar las
tareas hasta que el usuario haya revisado y aprobado el proposal y el design.**
Tras implementar **archivar solo cuando el usuario lo confirme**.

Tras cada archivado, revisar este **AGENTS.md** y actualizarlo si procede: estado actual
(fase, último hito, próximo hito), nuevas convenciones surgidas durante el change, o cualquier
apunte relevante que ayude a futuros agentes a entender el contexto del proyecto sin tener que
rastrearlo.

Tras cada archivado, extraer los items pendientes (Non-Goals, placeholders, "próximamente",
"futuro", riesgos pospuestos) y crear un **issue de GitHub** en el milestone **Backlog**
para cada uno, con descripción, origen del change y tareas previstas en el body.
Esto asegura que nada se pierda al cerrar el change y mantiene el backlog como fuente única en GitHub Issues.

Tras cada archivado, se debe actualizar el change en la tabla del documento
`docs/Product-Spec.md` con (fecha, fase, descripción, capabilities afectadas).

### Nomenclatura de changes

Cada change de OpenSpec se nombra con el prefijo del issue de GitHub que lo origina:
`is<nº-issue>-<slug>`. Ejemplo: `is1-first-run-wizard` para el issue #1.
El nombre de la carpeta del change es `openspec/changes/is<nº-issue>-<slug>/`.

## Flujo de trabajo con GitHub (issues, ramas, PRs)

Cada change de OpenSpec se rastrea en GitHub con este ciclo:

1. Propuesta OpenSpec aprobada por el usuario.
2. **Issue en GitHub** para el change, con enlace a su carpeta `openspec/changes/<nombre>/`
   y **milestone de su fase** (Fase 0, Fase 1…). Los milestones dan la vista de progreso
   por fase.
3. **Rama creada desde el issue** (panel *Development* → "Create a branch"; nombre tipo
   `<nº>-<slug>`), partiendo de `main`.
4. Implementación en la rama + push (los push los hace el usuario; el agente no tiene
   SSH hacia `origin` desde su shell).
5. **PR hacia `main`** con `Closes #<nº>` en la descripción → la CI proyecto
   (`project-ci.yml`) valida la PR → revisión del diff por el usuario.
6. **Squash merge** como norma (un change = un commit limpio en `main`).
   Excepción: PRs cuyos commits intermedios tengan valor propio.
7. Una vez el usuario mergea el PR y borra la rama de trabajo, nos propondrá el **archivado del change** 
   , esto se trasformará en un nuevo commit que el usuario hará push sobre la rama `main`.
   El CI de GitHub se ha configurado para que solo los PR lancen el compilado y testing
   pero no lo hará un push directo.
