## Why

Las jurisdicciones de `t4_companies` son hoy `VARCHAR(3)` sin restricción de integridad referencial hacia el catálogo de países, y la forma jurídica (`legal_form_code`) es un campo de texto libre sin validación ni catálogo. Esto permite datos inconsistentes y obliga al usuario a teclear códigos que desconoce. Además, la aplicación debe soportar tanto actividades de personas físicas (autónomos, profesionales) como de personas jurídicas (sociedades), distinción que el modelo actual no contempla. El proyecto necesita que las jurisdicciones estén validadas contra `t1_countries` y que la forma jurídica se seleccione de un catálogo estructurado por país y por tipo de sujeto, empezando por España.

## What Changes

- Añadir FK desde `t4_companies.primary_fiscal_jurisdiction` y `t4_companies.legal_form_jurisdiction` hacia `t1_countries.alpha3_code` (migración core).
- Añadir `is_legal_person BOOLEAN NOT NULL` a `t4_companies` para distinguir persona jurídica (`true`) de persona física (`false`).
- Crear tabla `es3_legal_forms` en `arume-es` con código, descripción, `is_legal_person` y FK hacia `t1_countries`; seed con las formas jurídicas españolas de la sección 10 de `docs/tipos-de-empresas.md`, clasificadas por tipo de sujeto.
- Exponer el catálogo de formas jurídicas como capacidad fiscal (`LegalFormsCapability`) resoluble por jurisdicción y filtrable por tipo de sujeto.
- **BREAKING**: `legal_form_code` deja de ser texto libre y pasa a estar validado contra el catálogo de la jurisdicción y del tipo de sujeto seleccionados. Los módulos de país que no provean catálogo no permitirán seleccionar forma jurídica en esa jurisdicción.
- Añadir un selector de tipo de sujeto (persona física / jurídica) y sustituir el `TextField` de forma jurídica por un `ComboBox` que se filtra según jurisdicción y tipo de sujeto, mostrando `código — descripción`.

## Fuera de alcance

- Catálogos de formas jurídicas para países distintos de España.
- Validación del identificador fiscal primario según reglas de cada jurisdicción (formato NIF/CIF, UTR, EIN, etc.).
- Migración de datos existentes (la BBDD actual se recrea desde cero en cada arranque; el nuevo alcance se fusiona en la migración core V0.1.0.2 y en la es V0.1.0.3).

## Capabilities

### New Capabilities

- `legal-form-catalog`: catálogo de formas jurídicas por jurisdicción y tipo de sujeto, expuesto como capacidad fiscal resoluble a través de `FiscalModuleRegistry`, con seed español y posibilidad de extensión por otros módulos de país.

### Modified Capabilities

- `company-management`: la forma jurídica deja de ser un `VARCHAR` libre y pasa a estar validada contra el catálogo de la jurisdicción y del tipo de sujeto correspondiente. Las columnas de jurisdicción ganan FK hacia `t1_countries.alpha3_code`, y la empresa gana un tipo de sujeto (`is_legal_person`) inmutable que forma parte de su identidad fiscal.

## Impact

- Migración core V0.1.0.2 ampliada: FKs a `t1_countries.alpha3_code` + columna `is_legal_person` como segunda columna de `t4_companies` (la antigua V0.1.0.3 core se fusiona y elimina).
- Migración en `arume-es` V0.1.0.3 completada (`es3_legal_forms`) con catálogo clasificado por tipo de sujeto y seed español.
- Nuevo enum `SubjectType` (`NATURAL_PERSON` / `LEGAL_PERSON`) en `arume-core`, mapeado a `is_legal_person` en persistencia.
- Nueva interfaz `LegalFormsCapability` en `arume-core` (contrato de capacidad fiscal) con filtro por tipo de sujeto.
- Implementación de `LegalFormsCapability` en `arume-es` (`SpainLegalFormsCapability`).
- Nuevo mapper MyBatis, repositorio y adaptador en `arume-es` para `es3_legal_forms`.
- Actualización de `SpainFiscalModule` para registrar la nueva capacidad.
- Actualización del modelo generado de `t4_companies` y `es3_legal_forms` (MyBatis Generator) para reflejar la columna boolean.
- Refactorización del formulario de creación de empresa en `companies.fxml` y `CompaniesController`: selector de tipo de sujeto + `ComboBox` de forma jurídica.
- Nuevas claves i18n para el tipo de sujeto, las descripciones del catálogo y los mensajes de validación.
- Pruebas de migración (Flyway + H2), de integración (catálogo + capacidad) y de UI (selector en el formulario).
