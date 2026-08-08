## Why

El producto solo tiene un módulo nacional, España. Con un único módulo es imposible saber si la arquitectura internacional aguanta de verdad: los historiales de Flyway independientes, el orden de las migraciones respecto a core, el registro de varios módulos en el registro de capacidades fiscales y una interfaz que no dé por supuesta una única jurisdicción solo se validan cuando conviven dos países. Incorporar Reino Unido es la forma más barata de comprobarlo.

Al estudiar cómo factura un negocio británico aparece además un defecto de modelado que ya está en el código. El catálogo `t5_legal_forms` clasifica cada forma como **persona física o persona jurídica** (`is_legal_person`), y esa clasificación forma parte de su clave primaria y de una clave ajena compuesta desde `t6_companies`. En Reino Unido la distinción no se sostiene: una **Partnership** puede estar formada por personas físicas, por personas jurídicas o por una combinación de ambas, y aun así emite facturas con su propio identificador fiscal. La personalidad jurídica pertenece al ámbito de la constitución de la sociedad, no al de la facturación: para facturar solo importa quién emite, con qué nombre y con qué identificador fiscal.

Como el producto todavía no tiene instalaciones que preservar, el refactor se afronta sin migración de datos: cada módulo mantiene su única migración de partida `V0.1.0.0`, que se reescribe.

## What Changes

**El catálogo de formas legales deja de clasificar por personalidad jurídica**

- **BREAKING**: la clave primaria de `t5_legal_forms` pasa de la clave triple `(country_alpha2_code, is_legal_person, code)` a la clave doble `(country_alpha2_code, code)`. El código de una forma legal es único dentro de su jurisdicción.
- **BREAKING**: `is_legal_person` desaparece y se sustituye por `is_organization BOOLEAN NOT NULL`, una columna **informativa** que no forma parte de la clave. No expresa personalidad jurídica: solo agrupa las formas en dos familias —las que ejerce una persona a título individual y las que constituyen una organización— para que la interfaz pueda guiar al usuario en la elección sin obligarle a recorrer una lista larga.
- El catálogo se lee por jurisdicción, sin filtro obligatorio por tipo de sujeto, y cada forma devuelta indica a qué familia pertenece.

**La empresa deja de tener tipo de sujeto**

- **BREAKING**: `t6_companies` pierde la columna `is_legal_person` y la clave ajena compuesta hacia el catálogo se reduce a `(legal_form_jurisdiction, legal_form_code)`. La base de datos sigue garantizando que la forma legal existe en la jurisdicción declarada.
- **BREAKING**: el enum `SubjectType` desaparece del dominio de empresa. Crear una empresa deja de exigir un tipo de sujeto: basta la forma legal, que ya lleva implícita esa información en el catálogo.
- En la vista de Empresas, el selector de tipo de sujeto deja de aportar un dato de la empresa y se convierte en un **filtro de ayuda** sobre el combo de formas legales. Además se sustituye la selección actual, que compara la etiqueta traducida del combo, por un control tipado que no depende del idioma activo.

**Nuevo módulo nacional de Reino Unido**

- Nuevo módulo `arume-uk` con jurisdicción `GB`, siguiendo el patrón de `arume-es`: descriptor de módulo, configuración de Flyway con historial y ubicación propios, y módulo fiscal registrado en el registro de capacidades.
- El módulo **no crea tablas propias** y, por tanto, no necesita su propia generación de código MyBatis. Es el primer módulo nacional que solo aporta datos, lo que demuestra que un país puede incorporarse al producto sin esquema propio.
- Su migración única `V0.1.0.0` siembra la asociación país↔divisa de Reino Unido y sus formas legales: Sole Trader, Partnership, Limited Liability Partnership, Private Limited Company, Public Limited Company, Company Limited by Guarantee y Community Interest Company. Solo Sole Trader se marca como forma de persona a título individual.
- No se modela `Charity` como forma legal: es un estatus que puede acompañar a distintas estructuras jurídicas y no cambia quién emite la factura.

**Convivencia de dos módulos nacionales**

- Ambos módulos migran después de core, con historiales independientes y sin acoplarse entre sí, y el registro resuelve `ES` y `GB` sin colisiones.
- La asociación país↔divisa se confirma como dato de jurisdicción: la siembra cada módulo nacional, no core.
- **BREAKING**: el selector de jurisdicción de la vista de Empresas deja de ofrecer los siete países del catálogo y pasa a ofrecer solo aquellos que tienen divisa asociada, es decir, aquellos cuyo módulo nacional está instalado. Con dos módulos cargados quedan España y Reino Unido. Hasta ahora el usuario podía elegir un país sin módulo y llegar a un formulario que no le permitía avanzar; ahora esa situación no se le presenta.

**España**

- Su migración adapta el seed de las 17 formas legales a la nueva columna, sin cambios en códigos ni descripciones.

## Capabilities

### New Capabilities

Ninguna. El módulo británico no aporta capacidades fiscales todavía; su contribución se describe en las capacidades existentes.

### Modified Capabilities

- `legal-form-catalog`: el catálogo pierde la clasificación por personalidad jurídica de su clave primaria y gana una marca informativa de organización; la consulta deja de filtrar por tipo de sujeto; la interfaz pasa a filtrar por familia de forma legal; se añade el catálogo de Reino Unido.
- `company-management`: la empresa deja de tener tipo de sujeto y la clave ajena hacia el catálogo de formas legales se reduce a jurisdicción y código.
- `international-module-architecture`: un módulo nacional puede limitarse a sembrar datos en catálogos core sin crear tablas propias, y varios módulos nacionales conviven con historiales de migración independientes.
- `country-currency-catalog`: las asociaciones país↔divisa las siembra el módulo nacional de cada jurisdicción, no la migración core, Reino Unido aporta la suya, y el catálogo gana la consulta de jurisdicciones soportadas derivada de esas asociaciones.

## Impact

- **Migraciones**: se reescriben `arume-db/src/main/resources/db/migration/core/V0.1.0.0__init_schema.sql` y `arume-es/src/main/resources/db/migration/es/V0.1.0.0__spain_schema.sql`; se añade `arume-uk/src/main/resources/db/migration/uk/V0.1.0.0__uk_schema.sql`.
- **Generación MyBatis**: `arume-db/MyBatis/mbg.xml` se ajusta a las nuevas columnas y se regeneran `T5LegalForms`, `T6Companies` y sus mappers. `arume-uk` no necesita generador porque no tiene tablas.
- **arume-core**: se elimina `SubjectType`; `Company`, `CompanySummary` y `CreateCompanyCommand` pierden ese atributo; `LegalFormItem` gana la marca de organización; `LegalFormFacade` y `LegalFormCatalogService` dejan de recibir el tipo de sujeto; `CountryFacade` y `CountryCatalogService` ganan la consulta de jurisdicciones soportadas.
- **arume-db**: `LegalFormCatalogAdapter`, `LegalFormCatalogQueryMapper` y `CompanyAdapter` se adaptan a las nuevas columnas; `CountryCatalogQueryMapper` y `CountryAdapter` añaden la consulta de países con divisa asociada.
- **arume-es**: sin cambios de código; solo el seed de su migración.
- **arume-uk** (nuevo): módulo Gradle con descriptor, configuración de Flyway, módulo fiscal y migración de seed.
- **arume-app**: incorpora el nuevo módulo a sus dependencias y a la composición de beans.
- **arume-ui**: `companies.fxml` y `CompaniesController` convierten el selector de tipo de sujeto en un filtro tipado del combo de formas legales y restringen el selector de jurisdicción a las soportadas; se revisan las claves i18n asociadas.
- **Gradle**: `settings.gradle` incluye `arume-uk` y la comprobación de aislamiento de core lo añade a su lista de dependencias prohibidas.
- **Pruebas**: se actualizan las de migración de esquema, las de catálogo de formas legales, las de persistencia de empresa, las del registro de módulos y las de la vista de Empresas; se añade cobertura de la convivencia de dos módulos nacionales.
- **Documentación**: convenciones de catálogo de formas legales y de datos por módulo en `AGENTS.md`, y registro del change en la especificación de producto.
- **Backlog**: el modelado de varios identificadores fiscales por emisor (UTR, VAT Registration Number, Company Number) queda fuera y se sigue en el issue #54; el issue #48 conserva solo las jurisdicciones sin módulo nacional.
