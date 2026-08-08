## Why

Los catálogos de la base de datos guardan hoy un único texto en inglés (`t1_countries.name`) y no existe ninguna tabla de idiomas, de modo que no es posible mostrar el contenido de catálogo en el idioma que el usuario ha elegido para la interfaz. Antes de seguir añadiendo catálogos (países, divisas, formas jurídicas, tipos de impuesto…) conviene fijar el patrón de contenido multiidioma. Además, el catálogo de formas jurídicas se creó como tabla propia del módulo español (`es3_legal_forms`), lo que obliga a duplicar tabla, mapper y adaptador en cada nuevo módulo de país. El producto está en fase inicial y la base de datos se recrea desde cero en cada arranque, así que este es el momento de reordenar el esquema core sin coste de migración de datos.

## What Changes

**Catálogo de idiomas y contenido traducible**

- **BREAKING**: se elimina `t0_app_config` (nunca se usó) y su hueco lo ocupa `t0_i18n`, con el código de idioma ISO 639-1 de dos letras como clave primaria y el nombre del idioma en inglés. Seed con los dos idiomas soportados por la interfaz: `en` y `es`.
- Nueva tabla `t2_country_names` con el nombre de cada país en cada idioma de `t0_i18n`, clave primaria compuesta (país, idioma) y claves ajenas a `t1_countries` y `t0_i18n`. Queda establecida como patrón para futuros catálogos traducibles.

**Códigos de país alpha-2 como referencia canónica**

- **BREAKING**: `t1_countries` pierde la columna `name` (pasa a `t2_country_names`) y gana `alpha2_code VARCHAR(2)`, que se convierte en su clave primaria; `numeric_code` y `alpha3_code` se conservan como claves únicas.
- **BREAKING**: las columnas de jurisdicción de negocio pasan de alpha-3 a alpha-2: `primary_fiscal_jurisdiction` y `legal_form_jurisdiction` en empresas, `fiscal_residence` en perfiles y `jurisdiction` en registros fiscales locales. `JurisdictionCode` valida `[A-Z]{2}` y el descriptor del módulo español declara `ES` en lugar de `ESP`.
- **BREAKING**: los PNG de banderas se renombran de alpha-3 a alpha-2 en minúsculas (`esp.png` → `es.png`, `gbr.png` → `gb.png`, `usa.png` → `us.png`, `chl.png` → `cl.png`, `sgp.png` → `sg.png`, `aus.png` → `au.png`, `zaf.png` → `za.png`), de modo que el recurso gráfico se resuelve directamente desde la clave primaria del catálogo de países sin traducción de códigos.

**Catálogo de formas jurídicas en core**

- **BREAKING**: se elimina `es3_legal_forms` y se crea `t5_legal_forms` en core, con clave primaria triple (país alpha-2, `is_legal_person`, `code`) y `description` en el idioma de la jurisdicción a la que pertenece la forma.
- La lectura del catálogo pasa a ser un servicio de core resoluble por jurisdicción y tipo de sujeto; desaparecen `LegalFormsCapability` y su implementación española. Cada módulo de país se limita a insertar sus formas jurídicas en `t5_legal_forms` desde su propia migración.
- Nueva clave ajena compuesta desde empresas (`legal_form_jurisdiction`, `is_legal_person`, `legal_form_code`) hacia `t5_legal_forms`, de modo que la base de datos garantiza que la forma jurídica existe y es coherente con el tipo de sujeto.

**Renumeración del esquema core**

- **BREAKING**: `t2_currencies` → `t3_currencies`, `t3_country_currency` → `t4_country_currency`, `t4_companies` → `t6_companies`, `t5_company_profiles` → `t7_company_profiles`, `t6_company_tax_registrations` → `t8_company_tax_registrations`, `t7_fiscal_years` → `t9_fiscal_years`, con renombrado consistente de restricciones e índices.

**Migraciones**

- **BREAKING**: las tres migraciones core actuales se refunden en una única `V0.1.0.0` que crea `t0`–`t9` y siembra idiomas, países, nombres de país, divisas y asociaciones.
- **BREAKING**: las dos migraciones españolas se refunden en una única `V0.1.0.0` que crea las series de facturación y siembra las 17 formas jurídicas españolas en `t5_legal_forms`.

**Interfaz**

- En la vista de Empresas, el campo de texto de jurisdicción se sustituye por un `ComboBox` de países cuyo nombre se muestra en el idioma activo, leído de `t2_country_names`, y que se recarga al cambiar de idioma. Esto absorbe el trabajo previsto en el issue #50.

## Capabilities

### New Capabilities

- `language-catalog`: catálogo de idiomas soportados almacenado en base de datos (`t0_i18n`) y patrón para tablas de contenido traducible, con resolución del texto por el idioma activo de la interfaz y respaldo a inglés.

### Modified Capabilities

- `country-currency-catalog`: `t1_countries` pierde `name`, gana `alpha2_code` como clave primaria y sus nombres pasan a la nueva tabla de nombres por idioma; las tablas de divisas y de asociación se renumeran a `t3`/`t4` y la asociación referencia el país por alpha-2; los recursos de bandera pasan a nombrarse por alpha-2.
- `legal-form-catalog`: el catálogo deja de ser una tabla por módulo de país y pasa a la tabla core `t5_legal_forms` con clave triple; se lee desde un servicio de core y cada módulo de país solo aporta el seed de sus formas jurídicas.
- `company-management`: las jurisdicciones de empresa, perfil y registros fiscales se expresan en alpha-2 y se validan contra el catálogo de países; la forma jurídica se valida además por clave ajena compuesta con el tipo de sujeto; la jurisdicción se selecciona en la interfaz desde un catálogo de países con nombre localizado.
- `international-module-architecture`: los módulos nacionales se identifican por jurisdicción alpha-2 y contribuyen datos a catálogos core mediante sus migraciones, en lugar de crear tablas de catálogo propias.
- `internationalization`: los idiomas soportados por la interfaz deben existir en el catálogo de idiomas de la base de datos, y el idioma activo determina el texto de catálogo mostrado.

## Impact

- **Migraciones**: `arume-db/src/main/resources/db/migration/core/` queda con una única `V0.1.0.0__init_schema.sql`; `arume-es/src/main/resources/db/migration/es/` queda con una única `V0.1.0.0__spain_schema.sql`. Se eliminan `V0.1.0.1`, `V0.1.0.2` (core) y `V0.1.0.3` (es).
- **Generación MyBatis**: `arume-db/MyBatis/mbg.xml` y `arume-es/MyBatis/mbg.xml` actualizados; se regeneran modelos y mappers (`T0I18n`, `T1Countries`, `T2CountryNames`, `T3Currencies`, `T4CountryCurrency`, `T5LegalForms`, `T6Companies`, `T7CompanyProfiles`, `T8CompanyTaxRegistrations`, `T9FiscalYears`) y se elimina el modelo `Es3LegalForms`.
- **arume-core**: `JurisdictionCode` pasa a alpha-2; nuevos puertos de catálogo de países (con nombre localizado) y de formas jurídicas; se elimina `LegalFormsCapability`.
- **arume-db**: nuevos mappers, repositorios y adaptadores para `t0_i18n`, `t2_country_names` y `t5_legal_forms`; adaptadores de empresa y ejercicio fiscal adaptados a los nuevos nombres de tabla.
- **arume-es**: se eliminan `SpainLegalFormsCapability`, `LegalFormsFacade`, `LegalFormsAdapter`, `LegalFormsMapper`, `LegalFormsRepository` y el modelo generado `Es3LegalForms`; `SpainModuleDescriptor` declara la jurisdicción `ES`; `SpainFiscalModule` conserva solo la capacidad de series de facturación.
- **arume-ui**: `companies.fxml` y `CompaniesController` sustituyen el campo de jurisdicción por un `ComboBox` de países localizado; nuevas claves i18n; los PNG de `icons/flags/` se renombran a alpha-2 en minúsculas.
- **Pruebas**: se actualizan `CountriesCurrenciesMigrationTest`, `BusinessSchemaMigrationTest`, `BusinessPersistenceIntegrationTest`, `LegalFormsPersistenceIntegrationTest`, `SpainFiscalModuleTest` y `FlagResourcesTest`; se añaden pruebas del catálogo de idiomas, de los nombres de país por idioma y del combo de países en la interfaz.
- **Documentación**: convenciones de nombrado de tablas, de códigos de país y de ficheros de bandera en `AGENTS.md`, y registro del change en la especificación de producto.
- **Backlog**: el issue #50 queda absorbido; el issue #48 (formas jurídicas de otros países) sigue vigente con el nuevo patrón de seed sobre `t5_legal_forms`.
