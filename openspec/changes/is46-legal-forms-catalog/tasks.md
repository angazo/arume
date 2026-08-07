## 1. Integridad referencial y tipo de sujeto (core)

- [x] 1.1 Fusionar el alcance en la migración core `V0.1.0.2__business_core.sql`: FKs `fk_t4_t1` (primary_fiscal_jurisdiction → t1_countries.alpha3_code) y `fk_t4_t1_2` (legal_form_jurisdiction → t1_countries.alpha3_code) dentro del CREATE TABLE, y columna `is_legal_person` como segunda columna; eliminar la antigua `V0.1.0.3__company_jurisdiction_fks.sql`.
- [x] 1.2 Verificar que la migración se aplica correctamente con `./gradlew test` sobre H2 en memoria.
- [x] 1.3 Añadir `is_legal_person BOOLEAN NOT NULL` como segunda columna de `t4_companies` en la migración core V0.1.0.2.
- [x] 1.4 Definir enum `SubjectType { NATURAL_PERSON, LEGAL_PERSON }` en `arume-core` con helper `isLegalPerson()`.
- [x] 1.5 Incorporar `subjectType` a `Company` (identidad inmutable), `CreateCompanyCommand`, `restore()`, `withId()` y el adaptador de persistencia core, mapeando `is_legal_person`.
- [x] 1.6 Ajustar `SpainModuleDescriptor.minimumCoreSchemaVersion()` a `0.1.0.2` tras fusionar el esquema core.

## 2. Catálogo español de formas jurídicas (arume-es)

- [x] 2.1 Crear migración `V0.1.0.3__legal_forms.sql` en `arume-es` con la tabla `es3_legal_forms` (code, country_numeric_code, description, PK compuesta, FK a `t1_countries.numeric_code`).
- [x] 2.2 Completar la migración con la columna `is_legal_person BOOLEAN NOT NULL` y el seed de 17 formas jurídicas españolas de `docs/tipos-de-empresas.md` (EI, PA, ERL → persona física; SL, SLU, SA, SAU, SColl, SCom, SComA, SCoop, SLL, SAL, SC, CB, AIE, SAT → persona jurídica).
- [x] 2.3 Actualizar `SpainModuleDescriptor` para reflejar la versión de esquema `0.1.0.3`.
- [x] 2.4 Crear modelo de persistencia `Es3LegalForms`, mapper MyBatis generado y repositorio en `arume-es`.
- [x] 2.5 Actualizar mapper custom y `LegalFormsFacade` para consultar formas jurídicas por `country_numeric_code` y `is_legal_person`.
- [x] 2.6 Añadir prueba de integración que verifique que la migración se aplica y el repositorio devuelve las formas jurídicas esperadas.

## 3. Capacidad fiscal LegalFormsCapability

- [x] 3.1 Definir `LegalFormsCapability extends FiscalCapability` en `arume-core` (`com.angazo.arume.core.module`).
- [x] 3.2 Actualizar `LegalFormsCapability.getLegalForms(SubjectType subjectType)` y el registro `LegalFormItem(String code, String description)`.
- [x] 3.3 Implementar `SpainLegalFormsCapability` en `arume-es` filtrando por `is_legal_person` según el `SubjectType`.
- [x] 3.4 Registrar `SpainLegalFormsCapability` en `SpainFiscalModule.capabilities()`.
- [x] 3.5 Añadir prueba unitaria de resolución de `LegalFormsCapability` para `ESP` (persona física y jurídica) y de ausencia para una jurisdicción no instalada.

## 4. UI: tipo de sujeto y ComboBox de forma jurídica

- [x] 4.1 Modificar `companies.fxml`: sustituir `TextField fx:id="legalFormField"` por `ComboBox fx:id="legalFormCombo"` con `id="companies-legal-form-combo"`.
- [x] 4.2 Añadir `ComboBox<String> subjectTypeCombo` con `id="companies-subject-type-combo"` en `companies.fxml`.
- [x] 4.3 Modificar `CompaniesController`: inyectar `FiscalModuleRegistry`, poblar el combo de tipo de sujeto (persona física/jurídica, default persona jurídica) y recargar el combo de formas jurídicas con el filtro por tipo de sujeto y jurisdicción.
- [x] 4.4 Añadir listener de tipo de sujeto y de jurisdicción que repueble el combo de formas; deshabilitarlo si la jurisdicción no tiene catálogo.
- [x] 4.5 Actualizar `onCreate()` para construir `SubjectType` desde el selector y extraer el código del string del combo, validando pertenencia al catálogo.
- [x] 4.6 Actualizar claves i18n: `companies.subjectType`, `companies.subjectType.naturalPerson`, `companies.subjectType.legalPerson`, renombrar `companies.legalForm` y añadir `companies.legalForm.noCatalog`.
- [x] 4.7 Actualizar prueba TestFX: seleccionar tipo de sujeto, comprobar que el combo muestra las formas correctas, crear empresa y verificar que aparece en la lista.

## 5. Verificación y documentación

- [x] 5.1 Ejecutar `./gradlew build` para validar compilación, tests unitarios, de integración y de UI.
- [x] 5.2 Ejecutar `openspec validate --change is46-legal-forms-catalog` y corregir incidencias.
