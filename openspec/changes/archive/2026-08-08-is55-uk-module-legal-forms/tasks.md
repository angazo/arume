## 1. Esquema core y seed español

- [ ] 1.1 Reescribir `t5_legal_forms` en `arume-db/src/main/resources/db/migration/core/V0.1.0.0__init_schema.sql`: eliminar `is_legal_person`, añadir `is_organization BOOLEAN NOT NULL` y dejar `pk_t5` sobre `(country_alpha2_code, code)`, conservando `fk_t5_t1`.
- [ ] 1.2 En la misma migración, eliminar la columna `is_legal_person` de `t6_companies` y reducir `fk_t6_t5` a `(legal_form_jurisdiction, legal_form_code)` → `t5_legal_forms (country_alpha2_code, code)`.
- [ ] 1.3 Adaptar el seed de las 17 formas españolas en `arume-es/src/main/resources/db/migration/es/V0.1.0.0__spain_schema.sql`, sustituyendo `is_legal_person` por `is_organization` (`FALSE` para `EI`, `PA` y `ERL`; `TRUE` para el resto) sin cambiar códigos ni descripciones.
- [ ] 1.4 Eliminar la base de datos local antes del primer arranque y verificar que las migraciones core + ES aplican sobre H2 en memoria.

## 2. Generación MyBatis

- [ ] 2.1 Ajustar `arume-db/MyBatis/mbg.xml` a las nuevas columnas de `t5_legal_forms` y `t6_companies`.
- [ ] 2.2 Ejecutar `:arume-db:mbGenerator` y regenerar `T5LegalForms`, `T6Companies`, sus mappers Java y sus XML, comprobando que no quedan referencias a `is_legal_person`.

## 3. Dominio core

- [ ] 3.1 Añadir la marca de familia a `LegalFormItem` (`code`, `description`, `organization`).
- [ ] 3.2 Cambiar el puerto `LegalFormFacade` a `findByJurisdiction(JurisdictionCode)` y adaptar `LegalFormCatalogService` a `list(jurisdiction)` / `hasCatalog(jurisdiction)`.
- [ ] 3.3 Eliminar el enum `SubjectType` y retirar el atributo de `Company` (factorías `create`, `restore`, `withId`, `summary`), `CompanySummary` y `CreateCompanyCommand`, incluidas sus validaciones.
- [ ] 3.4 Añadir a `CountryFacade` la consulta de jurisdicciones soportadas y exponerla en `CountryCatalogService`, conservando `findAll` como catálogo completo.
- [ ] 3.5 Adaptar `CompanyApplicationService` y comprobar que `:arume-core:verifyCoreIsolation` sigue pasando.

## 4. Persistencia

- [ ] 4.1 Adaptar `LegalFormCatalogQueryMapper` para consultar por jurisdicción, devolver `is_organization` y mantener el orden por descripción.
- [ ] 4.2 Adaptar `LegalFormCatalogAdapter` al nuevo puerto.
- [ ] 4.3 Retirar de `CompanyAdapter` el mapeo de `is_legal_person` en ambos sentidos.
- [ ] 4.4 Añadir a `CountryCatalogQueryMapper` y `CountryAdapter` la consulta de países con al menos una fila en `t4_country_currency`, con nombre localizado, respaldo a inglés y orden alfabético.

## 5. Módulo `arume-uk`

- [ ] 5.1 Crear el módulo Gradle `arume-uk` con dependencias a `arume-core`, `arume-db`, Spring context y Flyway; sin MyBatis ni tarea `mbGenerator`.
- [ ] 5.2 Registrarlo en `src/settings.gradle` y añadirlo a la lista de dependencias prohibidas de `verifyCoreIsolation` en `arume-core/build.gradle`.
- [ ] 5.3 Implementar `UkModuleDescriptor` con el descriptor fiscal (`arume-uk`, `GB`) y el descriptor de migración (`classpath:db/migration/uk`, `flyway_uk_schema_history`).
- [ ] 5.4 Implementar `UkDatabaseConfiguration` con el bean `ukFlyway` anotado `@DependsOn("flyway")`, sin depender de `spainFlyway` ni declarar `@MapperScan`.
- [ ] 5.5 Implementar `UkFiscalModule` devolviendo una colección de capacidades vacía.
- [ ] 5.6 Escribir `arume-uk/src/main/resources/db/migration/uk/V0.1.0.0__uk_schema.sql` sembrando `t4_country_currency ('GB', 826)` y las siete formas legales británicas de la tabla de decisiones del diseño, sin crear tabla alguna.

## 6. Composición de la aplicación

- [ ] 6.1 Añadir `arume-uk` a las dependencias de `arume-app`.
- [ ] 6.2 Registrar el bean `UkFiscalModule` en `BusinessApplicationConfiguration` para que entre en el `FiscalModuleRegistry`.
- [ ] 6.3 Verificar el arranque real de la aplicación con los dos módulos nacionales instalados.

## 7. Vista de Empresas

- [ ] 7.1 Crear el enum de familia de forma legal en `arume-ui` (organización / individual) con su resolución de etiqueta i18n.
- [ ] 7.2 Sustituir en `companies.fxml` el combo de tipo de sujeto por el combo tipado de familia, renombrando `fx:id` a `legalFormFamilyCombo` y el id CSS a `companies-legal-form-family-combo`, y recolocarlo justo antes del combo de formas legales.
- [ ] 7.3 Adaptar `CompaniesController`: eliminar `selectedSubjectType()` y la comparación por etiqueta traducida, cargar el catálogo completo de la jurisdicción y filtrar en memoria por la familia seleccionada.
- [ ] 7.4 Poblar el combo de jurisdicción con las jurisdicciones soportadas en lugar del catálogo completo, manteniendo la recarga al cambiar de idioma y la conservación de la selección.
- [ ] 7.5 Retirar el tipo de sujeto de la construcción de `CreateCompanyCommand`.
- [ ] 7.6 Renombrar las claves i18n `companies.subjectType*` a `companies.legalFormFamily*` en `messages.properties`, `messages_en.properties` y `messages_es.properties`.

## 8. Pruebas

- [ ] 8.1 Actualizar `BusinessSchemaMigrationTest`: nueva `pk_t5`, nueva `fk_t6_t5` de dos columnas, rechazo de una forma legal inexistente y recuento del catálogo tras core + ES + UK.
- [ ] 8.2 Actualizar `CountriesCurrenciesMigrationTest`: core no siembra asociaciones, y tras los dos módulos nacionales `t4_country_currency` contiene exactamente `ES↔EUR` y `GB↔GBP`, sin filas para `US`, `CL`, `SG`, `AU` ni `ZA`.
- [ ] 8.3 Actualizar `LegalFormsPersistenceIntegrationTest`: 17 formas para `ES`, 7 para `GB`, orden por descripción, marca de familia correcta y ausencia de catálogo para un país sin módulo (`US`).
- [ ] 8.4 Actualizar `BusinessPersistenceIntegrationTest` y `CompanyApplicationServiceTest` a la creación de empresa sin tipo de sujeto, y añadir un caso de empresa británica.
- [ ] 8.5 Añadir cobertura de la convivencia de módulos: existencia de los dos historiales de Flyway, ausencia de tablas propias del módulo británico y resolución de `ES` y `GB` en `FiscalModuleRegistry` con no disponibilidad controlada para una capacidad que `GB` no ofrece.
- [ ] 8.6 Actualizar `CountryCatalogPersistenceIntegrationTest`: el catálogo completo sigue devolviendo siete países y las jurisdicciones soportadas devuelven exactamente España y Reino Unido, localizadas y ordenadas.
- [ ] 8.7 Actualizar `CompaniesUiTest`: nuevos ids, filtro de familia tipado, selector de jurisdicción restringido a las soportadas, catálogo británico disponible y stubs adaptados a la marca de familia y a la nueva consulta del puerto, conservando un caso que ejercite la salvaguarda del combo deshabilitado.
- [ ] 8.8 Ejecutar `./gradlew build` desde `src` (con Xvfb si no hay sesión gráfica) y dejar la suite en verde.

## 9. Documentación

- [ ] 9.1 Actualizar en `AGENTS.md` la convención del catálogo de formas legales, la desaparición del tipo de sujeto de la empresa, el nuevo módulo `arume-uk` y el estado actual (último hito / próximo hito).
- [ ] 9.2 Actualizar el `## Purpose` de `openspec/specs/legal-form-catalog/spec.md`, que todavía menciona la resolución por tipo de sujeto.
- [ ] 9.3 Registrar el change en la tabla de la especificación de producto con fecha, fase, descripción y capabilities afectadas.
