## 1. Migraciones

- [x] 1.1 Reescribir `arume-db/src/main/resources/db/migration/core/V0.1.0.0__init_schema.sql` con `t0_i18n`, `t1_countries` (PK `alpha2_code`, UK alpha-3 y numérico, sin `name`) y `t2_country_names`, incluyendo el seed de idiomas (`en`, `es`), países y nombres de país en ambos idiomas.
- [x] 1.2 Añadir a esa misma migración `t3_currencies` y `t4_country_currency` (país por alpha-2) con sus seeds y restricciones renombradas.
- [x] 1.3 Añadir a esa misma migración `t5_legal_forms` con PK `(country_alpha2_code, is_legal_person, code)` y `fk_t5_t1`.
- [x] 1.4 Añadir a esa misma migración `t6_companies`, `t7_company_profiles`, `t8_company_tax_registrations` y `t9_fiscal_years` con jurisdicciones `VARCHAR(2)`, FKs a `t1_countries(alpha2_code)`, la FK compuesta de `t6_companies` a `t5_legal_forms` y los índices y restricciones renombrados.
- [x] 1.5 Eliminar `core/V0.1.0.1__countries_and_currencies.sql` y `core/V0.1.0.2__business_core.sql`.
- [x] 1.6 Reescribir la migración española como `es/V0.1.0.0__spain_schema.sql` con `es1_invoice_series` (FK `fk_es1_t6`), `es2_invoice_series_fiscal_year` (FK `fk_es2_t9`) y el seed de las 17 formas jurídicas españolas en `t5_legal_forms` para el país `ES`; eliminar `es/V0.1.0.3__legal_forms.sql`.

## 2. Generación MyBatis

- [x] 2.1 Actualizar `arume-db/MyBatis/mbg.xml` con las tablas `t0_i18n`, `t1_countries`, `t2_country_names`, `t3_currencies`, `t4_country_currency`, `t5_legal_forms`, `t6_companies`, `t7_company_profiles`, `t8_company_tax_registrations` y `t9_fiscal_years`.
- [x] 2.2 Actualizar `arume-es/MyBatis/mbg.xml` eliminando `es3_legal_forms`.
- [x] 2.3 Ejecutar `./gradlew :arume-db:mbGenerator :arume-es:mbGenerator`, revisar el código generado y borrar los modelos y mappers obsoletos (`T1Countries` antiguo, `T2Currencies`, `T3CountryCurrency`, `T4Companies`, `T5CompanyProfiles`, `T6CompanyTaxRegistrations`, `T7FiscalYears`, `Es3LegalForms` y sus XML).

## 3. Dominio y puertos en arume-core

- [x] 3.1 Cambiar la validación de `JurisdictionCode` a `[A-Z]{2}` y ajustar sus mensajes de error.
- [x] 3.2 Crear el catálogo de países en core: `CountryCatalogEntry` y el puerto `CountryFacade` con `findAll(String languageCode)`.
- [x] 3.3 Crear el catálogo de formas jurídicas en core: tipo de dominio `LegalFormItem` y puerto `LegalFormFacade` con `findByJurisdictionAndSubjectType(JurisdictionCode, SubjectType)`.
- [x] 3.4 Añadir los servicios de aplicación que exponen ambos catálogos a la interfaz.
- [ ] 3.5 Eliminar `LegalFormsCapability` y ajustar los tests de core afectados (`FiscalModuleRegistryTest`, `CompanyApplicationServiceTest`).

## 4. Persistencia en arume-db

- [x] 4.1 Implementar el mapper custom y el adaptador de `CountryFacade` leyendo `t1_countries` + `t2_country_names`, con respaldo a inglés y orden alfabético por nombre.
- [x] 4.2 Implementar el mapper custom y el adaptador de `LegalFormFacade` sobre `t5_legal_forms`, filtrando por país y `is_legal_person` y ordenando por descripción.
- [x] 4.3 Adaptar `CompanyAdapter`, `FiscalYearAdapter`, los repositorios y los mappers custom (`CompanyQueryMapper`, `FiscalYearQueryMapper`) a los nuevos nombres de tabla y a las jurisdicciones alpha-2.
- [x] 4.4 Revisar `DatabaseConfiguration` y el escaneo de mappers para incluir los nuevos componentes.

## 5. Módulo español

- [x] 5.1 Eliminar `SpainLegalFormsCapability`, `LegalFormsFacade`, `LegalFormsAdapter`, `LegalFormsMapper` y `LegalFormsRepository`.
- [x] 5.2 Actualizar `SpainFiscalModule` para registrar solo la capacidad de series de facturación y ajustar su construcción en la configuración de la aplicación.
- [x] 5.3 Actualizar `SpainModuleDescriptor`: jurisdicción `ES`, `minimumCoreSchemaVersion` `0.1.0.0` y `schemaVersion` de migración `0.1.0.0`.
- [x] 5.4 Adaptar los mappers custom de series (`InvoiceSeriesMapper`, `InvoiceSeriesFiscalYearMapper`) a los nuevos nombres de tabla core y actualizar `SpainFiscalModuleTest`.

## 6. Interfaz

- [x] 6.1 Sustituir en `companies.fxml` el `TextField` de jurisdicción por un `ComboBox` con `id` CSS estable, manteniendo el resto del formulario.
- [x] 6.2 En `CompaniesController`, cargar el catálogo de países con el idioma activo de `I18nManager`, mostrar el nombre localizado, y recargar el combo de formas jurídicas al cambiar de país o de tipo de sujeto.
- [x] 6.3 Recargar el catálogo de países y conservar la selección cuando cambie el idioma (`I18nManager.onLanguageChange`).
- [x] 6.4 Añadir o ajustar las claves i18n necesarias en `messages.properties` y `messages_es.properties`.
- [x] 6.5 Renombrar con `git mv` los PNG de `arume-ui/src/main/resources/icons/flags/` a alpha-2 en minúsculas (`esp→es`, `gbr→gb`, `usa→us`, `chl→cl`, `sgp→sg`, `aus→au`, `zaf→za`).

## 7. Pruebas

- [x] 7.1 Actualizar `CountriesCurrenciesMigrationTest` a `t1_countries` sin `name`, con PK alpha-2, y añadir cobertura de `t0_i18n` y `t2_country_names` (existencia, seeds y FKs).
- [x] 7.2 Actualizar `BusinessSchemaMigrationTest` a las tablas `t5`–`t9`, las jurisdicciones `VARCHAR(2)` y la FK compuesta de forma jurídica.
- [x] 7.3 Actualizar `LegalFormsPersistenceIntegrationTest` para leer el catálogo core sembrado por España y renombrarlo acorde al nuevo alcance.
- [x] 7.4 Actualizar `BusinessPersistenceIntegrationTest` y `BusinessApplicationContextTest` a las jurisdicciones alpha-2.
- [x] 7.5 Añadir una prueba de integración del catálogo de países que verifique nombre en español, nombre en inglés y respaldo a inglés.
- [x] 7.6 Actualizar `FlagResourcesTest` para iterar los códigos alpha-2 y comprobar que no queda ningún PNG nombrado por alpha-3.
- [x] 7.7 Añadir o ampliar la prueba de interfaz de la vista de Empresas para el combo de países localizado y su recarga al cambiar de idioma.

## 8. Documentación y cierre

- [x] 8.1 Actualizar `AGENTS.md`: nueva numeración de tablas core, alpha-2 como código de jurisdicción canónico, patrón de contenido multiidioma, catálogo de formas jurídicas en core y nombrado de las banderas por alpha-2 (incluido el comando de regeneración `rsvg-convert -w 96 -h 72 -o es.png es.svg`).
- [x] 8.2 Registrar el change en `docs/Product-Spec.md` y documentar el patrón de tablas de textos por idioma.
- [x] 8.3 Ejecutar `./gradlew build` (bajo Xvfb si no hay entorno gráfico) y comprobar el arranque de la aplicación con la base de datos borrada.
- [ ] 8.4 Al cerrar el change: cerrar el issue #50 como absorbido, actualizar el issue #48 con el nuevo patrón de seed y abrir un issue de backlog para los nombres de divisa traducidos.
