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
├── arume-app/    → Punto de entrada Spring Boot, composición y configuración
├── arume-core/   → Dominio común, casos de uso y puertos sin dependencias técnicas
├── arume-db/     → Persistencia core: mappers MyBatis, adaptadores y migraciones Flyway core
├── arume-es/     → Módulo español: dominio, persistencia y migraciones específicas
└── arume-ui/     → Interfaz JavaFX común (controladores, vistas FXML, recursos)

Paquete base: com.angazo.arume
```

**Módulos:**

| Módulo | Responsabilidad |
|---|---|
| `arume-app` | Arranque de la aplicación (`ArumeApp`), composición de módulos, configuración Spring Boot y recursos (`application.yml`) |
| `arume-core` | Dominio internacional, casos de uso, puertos de repositorio y contratos de capacidades fiscales; no depende de módulos técnicos o nacionales |
| `arume-db` | Persistencia core: mappers MyBatis generados, adaptadores de repositorio, infraestructura Flyway y migraciones en `db/migration/core/` |
| `arume-es` | Módulo español: series de facturación, capacidades fiscales, mappers/adaptadores propios y migraciones en `db/migration/es/` |
| `arume-ui` | Interfaz de usuario JavaFX: controladores, vistas FXML, tema AtlantaFX |

## Comandos de uso frecuente

```bash
./gradlew build        # Compilar y ejecutar tests
./gradlew test         # Solo tests
./gradlew bootRun      # Ejecutar la aplicación
./gradlew bootJar      # Generar JAR FAT para distribución
```

## Estado actual

- **Fase actual:** Fase 1 — arquitectura internacional del producto
- **Último hito:** Implementado y mergeado el issue #51: catálogo de idiomas (`t0_i18n`) y contenido multiidioma. Renumerado el esquema core a `t0`–`t9`; los códigos de jurisdicción son ahora ISO alpha-2 en mayúsculas (`JurisdictionCode` en core), con FKs de negocio `VARCHAR(2)` a `t1_countries(alpha2_code)`. El catálogo de formas jurídicas es único y de core (`t5_legal_forms`, PK triple país/tipo de sujeto/código, FK compuesta desde `t6_companies`), eliminando `es3_legal_forms` y `LegalFormsCapability` en favor de `LegalFormCatalogRepository`/`LegalFormCatalogService`. Los nombres de país se traducen en `t2_country_names` (respaldo a inglés) y la vista de Empresas usa un `ComboBox` de países localizado que se recarga al cambiar de idioma. Cada módulo nacional siembra sus datos de jurisdicción (asociación país↔divisa `t4_country_currency` y formas jurídicas) en su propia migración; el core solo siembra referencia universal. Las banderas PNG se nombran por alpha-2 en minúsculas. Change archivado como `2026-08-08-is51-multilingual-catalogs`. El patrón multiidioma y el catálogo se documentan en `docs/Product-Spec.md` (§8.3.2 y §8.3.3). El issue #50 quedó absorbido por #51; el issue #48 se actualizó al nuevo patrón de seed; se abrió el issue #53 (backlog) para nombres de divisa traducidos.
- **Próximo hito:** Definir el siguiente change de lógica de negocio sobre la base internacional, previsiblemente relacionado con facturas, clientes o la selección de empresa activa.

## Convenciones de código

- **Lombok**: se usa para reducir boilerplate (`@Slf4j`, `@Getter`, `@Setter`, etc.)
- **Java 25**: se aprovechan características modernas (`var`, text blocks, pattern matching, etc.)
- **UI JavaFX**: vistas definidas en FXML, controladores Java como `@Controller` de Spring
- **Arranque híbrido**: `ArumeAppFX.launch()` levanta JavaFX, que a su vez arranca Spring Boot vía `SpringApplication.run()`
- **Base de datos**: H2 en modo PostgreSQL (`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`); en desarrollo se usa `jdbc:h2:mem:arume`
- **Migraciones**: Flyway Community con historiales independientes (`flyway_core_schema_history` y `flyway_es_schema_history`). Las migraciones core están en `arume-db/src/main/resources/db/migration/core/` y las españolas en `arume-es/src/main/resources/db/migration/es/`; `arume-app` compone su ejecución en orden core → país. Mientras el producto no tenga instalaciones que preservar, cada módulo mantiene **una única
   migración de partida** (`V0.1.0.0`) que se reescribe en lugar de encadenar `ALTER TABLE`; la base actual se elimina antes del primer arranque y se
   inicializa desde cero.
 - **Datos por módulo**: el core solo siembra datos de referencia universales (idiomas, países, nombres de país por idioma y catálogos ISO como divisas). Los datos que dependen de una jurisdicción concreta —asociaciones país↔divisa (`t4_country_currency`), formas jurídicas, regímenes fiscales, etc.— los siembra el módulo nacional correspondiente en su propia migración, de modo que no se introducen en core filas para jurisdicciones que aún no tienen módulo.
- **Generador MyBatis (tarea `mbGenerator`)**: la tarea de `arume-db` corre en un JVM forkeado (`JavaExec`) y ejecuta `com.angazo.arume.db.generator.MbGeneratorMain`, que primero aplica las migraciones core a una BBDD H2 en memoria (`jdbc:h2:mem:mbgen;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`) y después MyBatis Generator, de modo que el código generado refleja siempre el esquema core. La conexión se define una sola vez en `arume-db/build.gradle` y se inyecta en `MyBatis/mbg.xml` como `${mbgen.url}` / `${mbgen.user}` / `${mbgen.password}` (además de `${projectDir}`). Código generado core: modelos en `com.angazo.arume.db.persistence.model`, mappers en `com.angazo.arume.db.persistence.mapper.generated`, mappers custom en `com.angazo.arume.db.persistence.mapper.custom`, repositorios globales en `com.angazo.arume.db.persistence.mapper` y adaptadores en `com.angazo.arume.db.persistence.adapter`. El módulo `arume-es` mantiene la misma convención de persistencia y dispone de su propio `mbGenerator`; los campos enumerados nacionales usan `columnOverride` con `javaType` y `typeHandler` propios.
- **Ventanas modales**: todas usan `StageStyle.UNDECORATED` con barra de título custom (`.title-bar`, 40px) y botón de cierre. Consistencia visual con la ventana principal.
- **Iconos**: Ikonli (`ikonli-javafx:12.3.1`) con packs FontAwesome5 y MaterialDesign2. Usar `FontIcon` para todos los iconos de la UI.
  - **Banderas de países**: Ikonli no cubre banderas por país. Se usan PNGs de 96×72 (3× del tamaño de visualización 32×24) en
    `arume-ui/src/main/resources/icons/flags/<iso2>.png` (ISO-2 minúsculas, misma clave que `t1_countries.alpha2_code`), cargados vía `ImageView`
    con fit 32×24; el downsampling de JavaFX mantiene la nitidez en pantallas HiDPI 1×/2×/3× sin lógica de DPI. Los PNGs se generan desde los SVGs
    de flag-icons (licencia MIT, set 4×3 apaisado) alojados en `docs/banderas/`, también nombrados por ISO-2. **Regeneración**:
    `rsvg-convert -w 96 -h 72 -o <iso2>.png <iso2>.svg` (p. ej. `rsvg-convert -w 96 -h 72 -o es.png es.svg`). Desde is23 los PNGs son
    **recursos inertes** (sin catálogo en la capa de aplicación; la bandera se mostrará a futuro con la empresa activa, issue #38) y
    `FlagResourcesTest` garantiza su existencia/dimensiones y que ninguno queda nombrado por alpha-3.
- **País vs idioma**: conceptos desacoplados y el país ya no es un dato de la aplicación: no se elige en el wizard ni se persiste en `arume.yml`
  (is23). La bandera del país aparecerá a futuro ligada a la empresa activa (issue #38). El idioma de la UI es la única preferencia global
  conmutable en cualquier momento (`arume.language`); el botón de idioma en la barra superior muestra el nombre del idioma activo en texto (sin bandera).
- **Códigos de país (ISO 3166-1)**: el código canónico es el **alpha-2 en mayúsculas** (`ES`), clave primaria de `t1_countries` y valor de
  `JurisdictionCode`; `alpha3_code` y `numeric_code` se conservan como claves únicas para interoperar con otros sistemas. Todas las columnas de
  jurisdicción de negocio son `VARCHAR(2)` con FK a `t1_countries(alpha2_code)`. Los PNGs de banderas usan el alpha-2 en **minúsculas**
  (`es.png`), de modo que el recurso se deriva de la clave del catálogo (`icons/flags/<alpha2 minúsculas>.png`, issue #38).
- **CSS**: `arume.css` en `src/arume-ui/src/main/resources/css/` extiende AtlantaFX con variables de acento verde. Cargar vía `scene.getStylesheets().add()`.
- **Temas**: solo Claro (PrimerLight) y Oscuro (Dracula). Paleta de acentos verde (`-color-accent-*`) overrida en `.root` de `arume.css`.
- **Nombrado de objetos de BBDD**: todo en minúsculas y en inglés, palabras separadas por guiones bajos. Las tablas core se prefijan con `t<n>_` y las tablas específicas de cada país con `<iso2><n>_`, donde `n` es un identificador numérico incremental dentro de cada espacio de nombres:
  - Tablas core actuales: `t0_i18n`, `t1_countries`, `t2_country_names`, `t3_currencies`, `t4_country_currency`, `t5_legal_forms`,
    `t6_companies`, `t7_company_profiles`, `t8_company_tax_registrations`, `t9_fiscal_years`
  - Tablas españolas: `es1_invoice_series`, `es2_invoice_series_fiscal_year`
  - PK core: `pk_t<n>`; PK nacional: `pk_<iso2><n>` (ej. `pk_t0`, `pk_es1`)
  - FK: `fk_<tabla_origen>_<tabla_destino>` (ej. `fk_t2_t1`, `fk_es2_t1`, `fk_es2_es1`). Si hay varias entre las mismas tablas, añadir sufijo incremental.
  - UK: `uk_<prefijo_tabla>_<descripción>` (ej. `uk_t0_key`, `uk_es1_code`). Si hay varias, añadir sufijo incremental.
  - Índices: `ix_<prefijo_tabla>_<descripción>` (ej. `ix_t1_date`, `ix_es1_code`). Si hay varios, añadir sufijo incremental.
- **Identificadores internos**: las PK de las entidades de negocio utilizan `BIGINT GENERATED BY DEFAULT AS IDENTITY`; las FK que las referencian utilizan `BIGINT`. La BBDD asigna estos IDs y los adaptadores los devuelven al dominio mediante claves generadas JDBC. Los códigos naturales de catálogos ISO mantienen sus tipos propios (`VARCHAR(2)`, `VARCHAR(3)`, `SMALLINT`).
- **Campos enumerados**: los enumerados persistidos como códigos numéricos utilizan `SMALLINT` y códigos explícitos estables. El `mbg.xml` del módulo propietario usa `columnOverride` con `javaType` y un `typeHandler` MyBatis propio para convertir el código a su enum Java; no se depende del ordinal accidental del enum.
- **Columnas de catálogos ISO**: en países, alpha-2 como `VARCHAR(2)` en **mayúsculas** y PK, con alpha-3 (`VARCHAR(3)`) y código numérico
  (`SMALLINT`, rango 0–999) como UK; en divisas, código numérico `SMALLINT` como PK y alpha-3 `VARCHAR(3)` con UK. Nombres como `VARCHAR(100)` y
  símbolo de divisa como `VARCHAR(8)`.
- **Contenido de catálogo multiidioma**: los idiomas soportados viven en `t0_i18n` (código ISO 639-1 de dos letras en minúsculas como PK y nombre
  en inglés) y deben coincidir con los bundles `messages*.properties`. El texto traducible de un catálogo se guarda en una tabla acompañante con
  PK compuesta `(<clave del catálogo>, language_code)` y FK a `t0_i18n`; el patrón de referencia es `t2_country_names`. La consulta resuelve el
  idioma activo con respaldo a inglés (`COALESCE`) y el idioma viaja como parámetro desde la UI hacia core (`CountryCatalogService.list(idioma)`),
  nunca como estado global de core. Los nombres propios legales (formas jurídicas) **no** se traducen: se guardan en el idioma de su jurisdicción.
- **Tipo de sujeto de empresa**: la distinción persona física/jurídica se persiste como boolean `is_legal_person` (true = jurídica) en `t6_companies`, y el dominio usa el enum `SubjectType` (`NATURAL_PERSON`/`LEGAL_PERSON`) con helper `isLegalPerson()`. El adaptador mapea entre ambos.
- **Catálogo de formas jurídicas**: es **único y de core** (`t5_legal_forms`), con PK triple `(country_alpha2_code, is_legal_person, code)`,
  `description` en el idioma de la jurisdicción y FK a `t1_countries`. Ningún módulo nacional crea tabla propia: cada país solo inserta sus filas
  desde su migración. Se lee mediante el puerto core `LegalFormFacade` y `LegalFormCatalogService`, filtrando por jurisdicción y
  `SubjectType`. `t6_companies` declara una FK compuesta `(legal_form_jurisdiction, is_legal_person, legal_form_code)` hacia el catálogo, de modo
  que la BBDD garantiza la coherencia entre forma jurídica y tipo de sujeto. La UI usa un `ComboBox` de países (nombre localizado) y un selector de
  tipo de sujeto que condicionan el combo de formas.
- **Columnas temporales con timezone**: las columnas que representan instantes (p. ej. `created_at`) se declaran `TIMESTAMP WITH TIME ZONE` y se mapean a `java.time.OffsetDateTime`; no usar `TIMESTAMP`/`LocalDateTime` para instantes.
- **Migraciones con datos**: el seed de catálogos va en la misma migración que crea las tablas; los tests de migración se ubican en `arume-app/src/test/` y ejecutan Flyway contra H2 en memoria (`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE`).
- **Idioma**: código fuente en inglés (nombres de clases, métodos, variables, comentarios y logs) para facilitar la participación de la comunidad. Documentación del proyecto (AGENTS.md, Product-Spec.md, openspec/) en español
- **Documentación OpenSpec**: la prosa de `proposal.md`, `design.md`, `tasks.md` y las especificaciones debe estar en español. Se mantienen en inglés únicamente las palabras, encabezados, etiquetas o marcadores que OpenSpec exija, además de nombres de módulos, clases, interfaces, rutas, códigos y otros identificadores técnicos.
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
