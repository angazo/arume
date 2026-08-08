## Context

Ver `proposal.md` — Why para la motivación. El estado del que partimos:

- Esquema core en tres migraciones (`V0.1.0.0` app_config, `V0.1.0.1` países y divisas, `V0.1.0.2` negocio) con tablas `t0`–`t7`.
- Esquema español en dos migraciones (`V0.1.0.0` series, `V0.1.0.3` formas jurídicas) con tablas `es1`–`es3`.
- El código generado por MyBatis Generator se produce contra una H2 en memoria a la que se aplican **solo las migraciones core** (`MbGeneratorMain`); el módulo español aplica core y luego las suyas (`EsMbGeneratorMain`).
- `MigrationOrchestrator` ejecuta siempre core antes que cada módulo nacional y valida `minimumCoreSchemaVersion`.
- La base de datos se recrea desde cero en cada arranque de esta fase: no hay datos de usuario que preservar.
- `arume-core` no conoce JavaFX ni `I18nManager`; la interfaz es quien sabe cuál es el idioma activo.

## Goals / Non-Goals

**Goals:**

- Fijar el patrón de contenido de catálogo traducible con una única tabla de idiomas y tablas de textos por idioma.
- Dejar el esquema core numerado de forma contigua (`t0`–`t9`) y con alpha-2 como código de país canónico en las referencias de negocio.
- Que un módulo de país nuevo pueda aportar sus formas jurídicas escribiendo únicamente filas en una migración, sin tabla, mapper ni adaptador propios.
- Que cualquier recurso indexado por país (empezando por las banderas) se resuelva directamente desde la clave del catálogo, sin tablas de conversión entre códigos.
- Reducir el esquema a una migración por módulo, aprovechando que no hay datos que migrar.

**Non-Goals:**

- Traducir los nombres de divisas ni las descripciones de formas jurídicas (ver decisión 4).
- Mostrar la bandera del país en ninguna vista: los PNG siguen siendo recursos inertes hasta el issue #38.
- Cargar el catálogo de países en el wizard de primera ejecución o en cualquier punto anterior al arranque de Spring: el catálogo vive en la base de datos y solo está disponible con el contexto levantado.
- Añadir más idiomas o más países al seed.

## Decisions

### 1. Una única migración por módulo en lugar de migraciones incrementales

El esquema entero se reescribe en `core/V0.1.0.0__init_schema.sql` y `es/V0.1.0.0__spain_schema.sql`, borrando el resto de ficheros.

- *Por qué*: renombrar diez tablas, cambiar la clave primaria de `t1_countries` y el tipo de cuatro columnas de jurisdicción mediante `ALTER TABLE` produciría una migración incremental larga, dependiente del orden de las claves ajenas y difícil de leer, para un producto sin instalaciones en producción. Un esquema de partida único es además la mejor documentación del modelo.
- *Alternativa descartada*: añadir `V0.1.0.3` core y `V0.1.0.4` es con `ALTER`/`RENAME`. Mantendría el historial, que hoy no aporta valor, a cambio de un esquema imposible de leer de un vistazo.
- *Consecuencia*: cualquier base de datos existente deja de ser válida y debe eliminarse antes del primer arranque. `SpainModuleDescriptor` pasa a exigir `minimumCoreSchemaVersion` `0.1.0.0` tanto en `FiscalModuleDescriptor` como en `MigrationModuleDescriptor`, y su propio `schemaVersion` de migración pasa a `0.1.0.0`.

### 2. `alpha2_code` como clave primaria de `t1_countries`

`t1_countries` queda con `alpha2_code` (PK), `alpha3_code` (UK) y `numeric_code` (UK), y todas las claves ajenas de negocio y de catálogo apuntan a `alpha2_code`.

- *Por qué*: el alpha-2 es el código que la aplicación va a manejar de extremo a extremo (dominio, interfaz, futuras integraciones y locales). Que la clave primaria coincida con el código de negocio evita joins innecesarios y hace legibles las filas de `t2_country_names`, `t4_country_currency`, `t5_legal_forms` y las tablas de empresa.
- *Alternativa descartada*: mantener `numeric_code` como PK y referenciar por alpha-2 mediante clave única. Funciona, pero deja dos formas de referirse a un país conviviendo en el mismo esquema.
- Se conservan `alpha3_code` y `numeric_code` porque son códigos ISO oficiales que otros sistemas (y los ficheros de banderas) siguen usando.

### 3. Nombres de país en tabla, no en los bundles de la interfaz

Los nombres de país se guardan en `t2_country_names` y no en `messages*.properties`.

- *Por qué*: son datos de catálogo, no literales de interfaz. Deben poder crecer con nuevos países sin recompilar, ser consultables desde SQL y servir a informes o documentos generados. El patrón se reutilizará en futuros catálogos (tipos de impuesto, formas de pago…).
- *Alternativa descartada*: claves `country.ES` en los bundles. Rompería la relación con el catálogo y obligaría a mantener sincronizados datos y traducciones en sitios distintos.
- La tabla de textos usa PK `(country_alpha2_code, language_code)` y FK a `t0_i18n`, lo que impide traducciones de idiomas no soportados y duplicados.

### 4. Las descripciones de formas jurídicas no se traducen

`t5_legal_forms.description` guarda un único texto en el idioma oficial de la jurisdicción propietaria de la forma.

- *Por qué*: «Sociedad Limitada» o «Limited Liability Partnership» son denominaciones legales, no etiquetas de interfaz; traducirlas induciría a error en documentos con valor fiscal.
- *Consecuencia*: la forma jurídica se muestra igual en cualquier idioma de la interfaz. Si en el futuro se quisieran descripciones traducidas para ayuda contextual, se añadiría una tabla de textos siguiendo el patrón de la decisión 3, sin tocar el catálogo.

### 5. Catálogo de formas jurídicas en core, sembrado por cada módulo de país

`t5_legal_forms` es una tabla core con PK `(country_alpha2_code, is_legal_person, code)`; el módulo español solo aporta `INSERT`s desde su migración. Desaparecen `LegalFormsCapability`, `SpainLegalFormsCapability`, `LegalFormsFacade`, `LegalFormsAdapter`, `LegalFormsMapper`, `LegalFormsRepository` y el modelo `Es3LegalForms`.

- *Por qué*: la estructura del catálogo es idéntica en todos los países; lo único nacional son los datos. Con una tabla por país, cada nuevo módulo tendría que replicar tabla, modelo generado, mapper, repositorio, adaptador y capacidad para no aportar nada nuevo.
- *Alternativa descartada*: mantener `LegalFormsCapability` con implementación que lee de la tabla core. Conserva un punto de extensión, pero a cambio de que cada módulo registre una capacidad idéntica a la de los demás; el punto de extensión real pasa a ser el seed.
- El orden core → nacional de `MigrationOrchestrator` garantiza que la tabla existe cuando el módulo español inserta sus filas.
- `is_legal_person` forma parte de la clave para poder declarar la FK compuesta de la decisión 6; el mismo código puede existir para persona física y jurídica sin colisión.

### 6. Clave ajena compuesta de empresa a forma jurídica

`t6_companies (legal_form_jurisdiction, is_legal_person, legal_form_code)` → `t5_legal_forms`.

- *Por qué*: hoy la coherencia entre forma jurídica y tipo de sujeto solo se valida en la capa de aplicación. Con la clave triple del catálogo, la base de datos puede garantizarla sin coste.
- *Trade-off*: la FK compuesta ata `is_legal_person` de la empresa al catálogo; para dar de alta una empresa en una jurisdicción todavía sin formas sembradas será obligatorio sembrar antes su catálogo. Es coherente con la spec vigente, que ya impide elegir forma jurídica sin catálogo.
- La validación de aplicación se mantiene: la FK es una red de seguridad, no el mecanismo de mensajes de error al usuario.

### 7. Esquema resultante

| Tabla | Contenido | Clave primaria |
|---|---|---|
| `t0_i18n` | Idiomas soportados | `language_code` |
| `t1_countries` | Países ISO 3166-1 | `alpha2_code` |
| `t2_country_names` | Nombre del país por idioma | `(country_alpha2_code, language_code)` |
| `t3_currencies` | Divisas ISO 4217 | `numeric_code` |
| `t4_country_currency` | Divisas por país | `(country_alpha2_code, currency_numeric_code)` |
| `t5_legal_forms` | Formas jurídicas por país y tipo de sujeto | `(country_alpha2_code, is_legal_person, code)` |
| `t6_companies` | Empresas | `id` |
| `t7_company_profiles` | Perfiles históricos de empresa | `id` |
| `t8_company_tax_registrations` | Registros fiscales locales | `id` |
| `t9_fiscal_years` | Ejercicios fiscales | `id` |

Las restricciones e índices se renombran siguiendo la convención (`pk_t6`, `fk_t7_t6`, `uk_t9_company_start`, `ix_t8_company_jurisdiction`…). En `arume-es`, `fk_es1_t4` pasa a `fk_es1_t6` y `fk_es2_t7` a `fk_es2_t9`. Las divisas mantienen `numeric_code` como PK: no son objeto de este change y no se referencian desde el negocio.

#### Corrección durante la implementación — datos dependientes de jurisdicción en módulos nacionales

El diseño original sembraba `t4_country_currency` en la migración core (ocho asociaciones país↔divisa, incluidas jurisdicciones sin módulo: GB, US, CL, SG, AU, ZA). Durante la implementación se corrigió: **core solo define la estructura de `t4_country_currency` y siembra el catálogo universal de divisas (`t3_currencies`); las asociaciones país↔divisa las siembra el módulo nacional de cada jurisdicción**. Con el único módulo existente (España) la migración `es/V0.1.0.0` inserta únicamente `('ES', 978)`. Esto evita filas de países que aún no tienen módulo y aplica la misma regla que las formas jurídicas: el core no introducen datos de jurisdicciones no implementadas. El test `CountriesCurrenciesMigrationTest` aplica ahora core y España por separado (historiales `flyway_core_schema_history` y `flyway_es_schema_history`, `baselineOnMigrate`/`baselineVersion=0.0.0.0` para la nacional) y espera una sola fila en `t4_country_currency`.

### 8. El idioma activo viaja como parámetro hacia core

El nuevo puerto de catálogo de países recibe el código de idioma como argumento (`findAll(String languageCode)`), y es `arume-ui` quien lo obtiene de `I18nManager` y lo pasa.

- *Por qué*: `arume-core` no debe conocer la interfaz ni mantener estado global de idioma. Pasarlo explícitamente mantiene los casos de uso puros y testables.
- *Alternativa descartada*: un `LanguageContext` de ámbito Spring poblado por la interfaz. Añade estado implícito y acoplamiento por hilo sin beneficio a este tamaño.
- El respaldo a inglés cuando falta la traducción se resuelve en la consulta SQL del adaptador (`COALESCE` sobre el idioma pedido y `en`), no en la interfaz.

### 9. Puertos y adaptadores nuevos

- `arume-core`: `CountryFacade` (puerto) con `List<CountryCatalogEntry> findAll(String languageCode)` y `CountryCatalogEntry(String alpha2Code, String name)`; `LegalFormFacade` (puerto) con `List<LegalFormItem> findByJurisdictionAndSubjectType(JurisdictionCode, SubjectType)`; servicios de aplicación finos sobre ambos.
- `arume-db`: mappers custom y adaptadores para los dos puertos, más los mappers generados de `t0_i18n`, `t2_country_names` y `t5_legal_forms`.
- `LegalFormItem` se mueve de `LegalFormsCapability` a un tipo de dominio propio del catálogo, ya que la capacidad desaparece.

### 10. Ficheros de bandera indexados por alpha-2

Los PNG de `arume-ui/src/main/resources/icons/flags/` se renombran de alpha-3 a alpha-2 en minúsculas: `esp→es`, `gbr→gb`, `usa→us`, `chl→cl`, `sgp→sg`, `aus→au`, `zaf→za`.

- *Por qué*: con `alpha2_code` como clave primaria del catálogo (decisión 2), el nombre del fichero se deriva de la clave con `alpha2Code.toLowerCase()`. Mantener alpha-3 obligaría a arrastrar `alpha3_code` en cada consulta o a mantener un mapa de conversión solo para las imágenes.
- *Cómo*: `git mv` de los siete ficheros; los PNG no se regeneran, siguen siendo los mismos 96×72 generados con `rsvg-convert` desde los SVG de flag-icons de `docs/banderas/`, que ya están nombrados por alpha-2 (`es.svg`, `gb.svg`…). Es decir, el nombre del PNG pasa a coincidir con el del SVG de origen y el comando de regeneración se simplifica a `rsvg-convert -w 96 -h 72 -o es.png es.svg`.
- `FlagResourcesTest` pasa a iterar los códigos alpha-2 y sigue verificando existencia, tamaño y proporción 4:3. Los PNG siguen siendo recursos inertes: nadie los muestra todavía (issue #38).

### 11. Regeneración MyBatis

Se actualizan ambos `mbg.xml` con los nuevos nombres de tabla; el de `arume-es` pierde `es3_legal_forms`. No hacen falta `typeHandler` nuevos: `is_legal_person` es boolean y no hay enums nuevos persistidos. Como el generador aplica las migraciones core antes de generar, basta con reescribir la migración para que el código generado quede alineado.

## Risks / Trade-offs

- **Base de datos existente inservible** → El change se documenta como ruptura; el usuario debe borrar el fichero H2 antes del primer arranque, igual que en la fase anterior. Ninguna instalación en producción está afectada.
- **Cambio amplio y transversal en una sola PR** (esquema, generación, dominio, persistencia, módulo español e interfaz) → El orden de tareas empieza por migraciones y generación para que el compilador señale todos los puntos afectados; la compilación y las pruebas actúan de red.
- **FK compuesta demasiado rígida si un país no siembra formas** → Aceptado: sin catálogo tampoco se puede elegir forma jurídica en la interfaz. Queda registrado en el issue #48.
- **Divisas siguen con nombre único en inglés**, incoherente con países → Aceptado en este change; el patrón de la decisión 3 permite añadir `t?_currency_names` cuando haga falta, y se dejará como issue de backlog.
- **Ficheros de bandera y filas de catálogo pueden desincronizarse** → `FlagResourcesTest` recorre los códigos soportados y falla si falta un PNG; al añadir un país habrá que añadir su bandera en el mismo change.
- **El combo de países exige contexto Spring levantado** → La vista de Empresas ya se usa con Spring activo; ninguna pantalla previa al arranque necesita el catálogo.

## Migration Plan

1. Reescribir `core/V0.1.0.0__init_schema.sql` con `t0`–`t9` y sus seeds; borrar `V0.1.0.1` y `V0.1.0.2`.
2. Reescribir `es/V0.1.0.0__spain_schema.sql` con `es1`, `es2` y el seed de formas jurídicas españolas en `t5_legal_forms`; borrar `V0.1.0.3`.
3. Actualizar `mbg.xml` de ambos módulos y regenerar modelos y mappers; eliminar los artefactos generados obsoletos.
4. Adaptar dominio, puertos, adaptadores, módulo español e interfaz; renombrar los PNG de bandera a alpha-2; actualizar pruebas.
5. Verificar con `./gradlew build` en entorno gráfico (o bajo Xvfb) y arrancando la aplicación con la base de datos borrada.

Rollback: revertir el commit y restaurar la base de datos anterior desde una copia; no hay migración de datos que deshacer.
