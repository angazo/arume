## Context

Ver `proposal.md` — Why para la motivación. Lo relevante para el diseño es el estado de partida:

- `t5_legal_forms` tiene clave primaria triple `(country_alpha2_code, is_legal_person, code)` y `t6_companies` la referencia con una clave ajena compuesta de tres columnas que incluye su propia `is_legal_person`. La base de datos garantiza hoy la coherencia entre la forma legal de la empresa y su tipo de sujeto.
- El puerto core `LegalFormFacade` recibe `(JurisdictionCode, SubjectType)` y el mapper traduce el enum a un `boolean` en la cláusula `WHERE`.
- `CompaniesController` construye el selector de tipo de sujeto con dos literales traducidos y decide el valor comparando la cadena seleccionada con esos mismos literales, de modo que la lógica depende del idioma activo.
- Solo existe un módulo nacional. `SpainDatabaseConfiguration` declara su bean de Flyway con `@DependsOn("flyway")` y `MigrationOrchestrator.migrateNational` valida la versión de core antes de migrar. `FiscalModuleRegistry` indexa los módulos por alpha-2 y rechaza duplicados.
- El producto no tiene instalaciones que preservar: cada módulo mantiene una única migración `V0.1.0.0` que se reescribe y la base se recrea desde cero.

## Goals / Non-Goals

**Goals:**

- Que el catálogo de formas legales sea utilizable por una jurisdicción cuyas formas no encajan en la dicotomía persona física / persona jurídica.
- Que exista un segundo módulo nacional real que ejercite la convivencia de módulos, y que ese módulo sea el caso más simple posible: solo datos, sin esquema propio.
- Que la información de familia (organización / individual) no se pierda, y que siga siendo útil para guiar al usuario sin convertirse en un dato de la empresa.
- Que la selección de la interfaz deje de depender del texto traducido.

**Non-Goals:**

- Modelar varios identificadores fiscales por emisor (UTR, VAT Registration Number, Company Number). Va en el issue #54.
- Dotar al módulo británico de capacidades fiscales (series de facturación, regímenes, validación del formato del identificador). El módulo nace sin capacidades a propósito.
- Traducir las descripciones de las formas legales: siguen escritas en el idioma de su jurisdicción.
- Añadir jurisdicciones más allá de `ES` y `GB` (issue #48).
- Introducir un concepto de "emisor" separado de "empresa". El documento de análisis británico lo sugiere, pero esa reorganización del dominio no es necesaria para este change y se afrontará, si procede, junto con las identidades fiscales.

## Decisions

### D1 — `is_organization` como columna informativa, no como clave

`t5_legal_forms` queda con clave primaria `(country_alpha2_code, code)` y una columna `is_organization BOOLEAN NOT NULL` fuera de cualquier clave.

*Por qué*: el problema del diseño actual no es que la clasificación exista, sino que forme parte de la identidad de la forma legal. Como clave, obliga a que toda jurisdicción se pronuncie sobre la personalidad jurídica de cada forma y admite el mismo código dos veces con significados distintos. Como atributo, la clasificación es una etiqueta que la interfaz puede usar y que ninguna regla de integridad depende de ella.

*Alternativas descartadas*:
- **Eliminarla del todo**: dejaría al usuario ante una lista larga y heterogénea (17 formas en España) sin ninguna ayuda para orientarse.
- **`SMALLINT` con códigos explícitos**, como marca la convención del proyecto para enumerados persistidos: aquí el dominio es binario y no se prevé un tercer valor, y el proyecto ya usa `BOOLEAN` para esta misma distinción. Un `BOOLEAN` evita además el `typeHandler` de MyBatis.
- **Tabla de familias con clave ajena**: sobredimensionado para dos valores fijos.

*Nombre*: `is_organization`, no `is_legal_person`, precisamente para no arrastrar la connotación jurídica. Una Partnership británica se marca como organización sin que eso afirme nada sobre la personalidad jurídica de sus socios.

### D2 — El filtrado por familia se hace en memoria, no en SQL

El puerto core devuelve todas las formas legales de la jurisdicción, ordenadas por descripción, y la interfaz filtra por familia sobre la lista ya cargada.

*Por qué*: el catálogo de una jurisdicción es diminuto (17 filas en España, 7 en Reino Unido) y una sola consulta sirve para ambas familias; cambiar el filtro deja de golpear la base de datos. Además, la familia es una ayuda de presentación: mantenerla fuera del puerto evita que un concepto de interfaz condicione el contrato de core.

*Consecuencia en el contrato*: `LegalFormFacade` pasa de `findByJurisdictionAndSubjectType(JurisdictionCode, SubjectType)` a `findByJurisdiction(JurisdictionCode)`, y `LegalFormCatalogService` expone `list(JurisdictionCode)` y `hasCatalog(JurisdictionCode)`. `LegalFormItem` gana la marca de familia: `LegalFormItem(String code, String description, boolean organization)`.

*Alternativa descartada*: conservar el parámetro en la consulta. Mantiene el filtro en SQL pero obliga a core a hablar de una distinción que ha dejado de ser suya y duplica consultas cada vez que el usuario cambia de familia.

### D3 — `SubjectType` desaparece de core; la familia vive en la interfaz

Se elimina el enum `SubjectType` de `com.angazo.arume.core.domain.company` y con él el atributo de `Company`, `CompanySummary` y `CreateCompanyCommand`. La familia se representa en `arume-ui` con un enum propio (`ORGANIZATION` / `INDIVIDUAL`) que solo sirve para filtrar y traducir su etiqueta.

*Por qué*: un enum en core sugiere que el dominio necesita la distinción, y no la necesita: ninguna regla de negocio ni de persistencia depende ya de ella. Core se queda con un `boolean` en `LegalFormItem`, que es exactamente lo que la base de datos almacena. El enum de interfaz existe únicamente para que el control sea tipado (ver D4).

### D4 — El selector de familia es un control tipado situado junto al combo de formas

`companies.fxml` sustituye el `ComboBox<String>` de tipo de sujeto por un `ComboBox` del enum de familia con `StringConverter`, siguiendo el mismo patrón que el combo de países ya existente, y lo coloca **inmediatamente antes** del combo de formas legales en lugar de encabezar el formulario.

*Por qué el control tipado*: hoy `selectedSubjectType()` compara la cadena seleccionada con los literales traducidos; si cambia una traducción o el idioma activo, la lógica se rompe en silencio. Con el enum como valor del combo, la selección es independiente del texto mostrado y el escenario "el filtro sobrevive a un cambio de idioma" es directamente comprobable.

*Por qué moverlo de sitio*: encabezando el formulario parecía un atributo de la empresa. Junto al combo que filtra, se lee como lo que es.

*Renombrados asociados*: `fx:id` `subjectTypeCombo` → `legalFormFamilyCombo`; id CSS `companies-subject-type-combo` → `companies-legal-form-family-combo`; claves i18n `companies.subjectType*` → `companies.legalFormFamily`, `companies.legalFormFamily.organization`, `companies.legalFormFamily.individual`. El valor por defecto sigue siendo la familia de organizaciones, que es la que hoy se preselecciona.

*Alternativa descartada*: dos `RadioButton` en un `ToggleGroup`. Comunica mejor que es un filtro, pero rompe la uniformidad visual del formulario, que es una rejilla de combos y campos.

### D5 — El módulo británico solo aporta datos

`arume-uk` se compone de `UkModuleDescriptor`, `UkDatabaseConfiguration`, `UkFiscalModule` y una migración `V0.1.0.0`. No declara tablas, no tiene mappers, no lleva `@MapperScan`, no depende de MyBatis y no define tarea `mbGenerator`.

*Por qué*: es la prueba más limpia de que la arquitectura admite incorporar una jurisdicción sin esquema propio, y evita crear tablas vacías "por simetría" con España. Si en el futuro Reino Unido necesita tablas, se le añade su `mbGenerator` entonces, siguiendo el patrón de `arume-es`.

*Descriptores*: `FiscalModuleDescriptor("arume-uk", "GB", "0.1.0", "0.1.0.0")` y `MigrationModuleDescriptor("arume-uk", 0.1.0.0, 0.1.0.0, "classpath:db/migration/uk", "flyway_uk_schema_history")`.

*`UkFiscalModule`*: implementa `FiscalModule` devolviendo una colección de capacidades vacía. `FiscalModuleRegistry` lo indexa por `GB` y `resolve` devuelve `Optional.empty()` para cualquier capacidad, que es justo el resultado controlado de no disponibilidad que exige la especificación. No hace falta ningún caso especial en el registro.

*Dependencias Gradle*: `arume-core`, `arume-db` (por `MigrationOrchestrator`), Spring context y Flyway. Se añade `arume-uk` a `settings.gradle`, a las dependencias de `arume-app` y a la lista de dependencias prohibidas de la comprobación de aislamiento de `arume-core`.

### D6 — Los dos módulos nacionales migran en paralelo respecto a core, no en cadena entre sí

`UkDatabaseConfiguration` declara su bean `ukFlyway` con `@DependsOn("flyway")`, igual que el español, y **no** depende de `spainFlyway`.

*Por qué*: encadenarlos (`ukFlyway` dependiendo de `spainFlyway`) daría un orden determinista, pero introduciría exactamente el acoplamiento entre módulos nacionales que la arquitectura quiere evitar: el orden de arranque pasaría a depender de qué países estén instalados. Cada módulo declara su dependencia con core y nada más; el orden relativo entre módulos nacionales queda indefinido a propósito, y las migraciones se escriben para no necesitarlo.

*Riesgo asumido*: dos módulos que sembraran la misma fila de un catálogo core chocarían. Aquí no ocurre —cada uno inserta filas de su propia jurisdicción— y esa es precisamente la regla que el catálogo de datos por módulo ya impone.

### D7 — Códigos y descripciones de las formas legales británicas

Se siembran siete filas para `GB`, con la abreviatura de uso corriente como código y la descripción en inglés:

| `code` | `description` | `is_organization` |
|---|---|---|
| `SoleTrader` | Sole Trader | `FALSE` |
| `Partnership` | Partnership | `TRUE` |
| `LLP` | Limited Liability Partnership | `TRUE` |
| `Ltd` | Private Limited Company | `TRUE` |
| `PLC` | Public Limited Company | `TRUE` |
| `CLG` | Company Limited by Guarantee | `TRUE` |
| `CIC` | Community Interest Company | `TRUE` |

*Por qué estos códigos*: siguen el criterio ya usado en España, donde el código es la abreviatura con la que la forma se conoce en su jurisdicción (`SL`, `SA`, `SCoop`) y no un identificador de un registro oficial. `Ltd`, `PLC`, `LLP` y `CIC` son sufijos societarios reales; `CLG` es la abreviatura habitual de Company Limited by Guarantee; `SoleTrader` y `Partnership` no tienen sufijo y se codifican con su propio nombre.

*Por qué solo `SoleTrader` no es organización*: es la única forma en la que el emisor de la factura es la persona física que ejerce la actividad. En todas las demás el emisor es la organización, incluida la Partnership, cuyos socios pueden ser personas físicas, jurídicas o ambas.

*Charity no se incluye*: es un estatus que puede acompañar a varias formas (típicamente Company Limited by Guarantee) y no cambia quién emite la factura ni bajo qué identificador.

### D8 — La asociación país↔divisa se confirma como dato del módulo nacional

La migración británica siembra `('GB', 826)` en `t4_country_currency`. Core sigue sin sembrar ninguna asociación.

*Por qué*: la regla ya estaba establecida desde is51, pero la especificación de `country-currency-catalog` seguía describiendo las ocho asociaciones como seed de core, en contradicción con el código y con su propio test. Este change alinea la especificación con la realidad: el catálogo solo relaciona un país con su divisa cuando ese país tiene módulo.

### D9 — La divisa asociada es el indicador de "jurisdicción soportada"

El selector de jurisdicción de la vista de Empresas deja de listar los siete países del catálogo y lista solo aquellos con al menos una fila en `t4_country_currency`. Se añade al puerto `CountryFacade` una consulta separada (`findSupportedJurisdictions(languageCode)`), expuesta por `CountryCatalogService`, que reutiliza la resolución de nombre localizado con respaldo a inglés y el orden alfabético; `findAll` conserva su significado de catálogo completo.

*Por qué la divisa como indicador*: la asociación país↔divisa la siembra exclusivamente la migración del módulo nacional (D8), de modo que su presencia es una consecuencia directa y verificable en la propia base de datos de que el módulo está instalado. No hace falta un mecanismo de registro adicional ni que la interfaz consulte el registro de módulos.

*Por qué una consulta aparte y no filtrar `findAll`*: el catálogo completo de países es un dato de referencia con sentido propio y otros usos futuros (por ejemplo el domicilio o la residencia fiscal de una empresa, que pueden estar en un país sin módulo). Lo que se restringe es la elección de **jurisdicción**, no la de país.

*Alternativa descartada*: derivar la lista de `FiscalModuleRegistry`. Es la fuente más directa de "módulo cargado", pero obligaría a inyectar el registro en la interfaz y a cruzarlo con el catálogo de países para obtener los nombres localizados, y dejaría fuera de la base de datos una restricción que la base de datos ya puede responder con una consulta.

*Consecuencia*: con `ES` y `GB` instalados, el usuario solo puede crear empresas en esas dos jurisdicciones, y toda jurisdicción ofrecida tiene garantizado su catálogo de formas legales. El estado de "combo de formas deshabilitado" pasa a ser una salvaguarda defensiva —solo alcanzable si un módulo sembrara su divisa pero no sus formas— y se conserva en la especificación y en la prueba de interfaz precisamente para que esa inconsistencia se vea en lugar de producir una lista vacía.

## Risks / Trade-offs

- **La base de datos deja de garantizar la coherencia entre forma legal y tipo de sujeto** → Es intencionado: esa coherencia dejó de ser un invariante porque el tipo de sujeto ya no existe en la empresa. La integridad que queda —que la forma legal exista en la jurisdicción declarada— se mantiene con la clave ajena de dos columnas.
- **Se pierde la capacidad de tener el mismo código en dos familias dentro de una jurisdicción** → Ninguna fila actual lo aprovecha (los 17 códigos españoles son distintos entre sí) y admitirlo era una consecuencia accidental de la clave triple, no un requisito. La nueva clave lo convierte en un error detectado por la base de datos.
- **Reordenar y renombrar controles rompe las pruebas de interfaz existentes** → `CompaniesUiTest` se actualiza en el mismo change; los ids CSS estables hacen que el ajuste sea mecánico. La prueba de "jurisdicción sin catálogo" debe dejar de usar Reino Unido, que pasa a tener catálogo, y usar un país sin módulo nacional (`US`).
- **El orden entre `spainFlyway` y `ukFlyway` es indeterminado** → Aceptado por D6. Se cubre con una prueba que aplica core y ambos módulos y verifica los dos historiales y los datos sembrados, sin asumir ningún orden.
- **Un módulo sin capacidades podría parecer un módulo roto** → Se cubre con una prueba explícita: el registro resuelve `GB` y devuelve no disponibilidad para cualquier capacidad, sin caer en la implementación española.
- **La marca `is_organization` puede quedarse corta en alguna jurisdicción futura** → Al no formar parte de ninguna clave, ampliarla (a un enumerado, por ejemplo) es un cambio local en una columna y en el filtro de la interfaz, no un cambio de identidad del catálogo.
- **Un módulo que sembrara su divisa pero olvidase sus formas legales aparecería como jurisdicción soportada sin serlo del todo** → Se cubre con la salvaguarda del combo deshabilitado (D9) y con las pruebas de seed de cada módulo, que verifican ambas contribuciones.
- **Restringir el selector reduce lo que el usuario puede hacer respecto a hoy** → Es una restricción deliberada: hoy podía elegir un país sin módulo y quedarse en un formulario sin salida. Al añadirse nuevos módulos nacionales la lista crece sola, sin tocar la interfaz.

## Migration Plan

No hay migración de datos. La base de datos se recrea desde cero y cada módulo conserva **una única** migración de partida `V0.1.0.0`, que se reescribe en lugar de encadenar `ALTER TABLE`.

Orden de ejecución previsto:

1. Reescribir la migración core (`t5_legal_forms` y `t6_companies`) y la española (seed adaptado).
2. Ajustar `arume-db/MyBatis/mbg.xml` y regenerar modelos y mappers de las dos tablas afectadas.
3. Adaptar core (`LegalFormItem`, `LegalFormFacade`, `LegalFormCatalogService`, `Company` y compañía) y después `arume-db` (adaptadores y mapper de consulta), de modo que el compilador vaya señalando los puntos pendientes.
4. Crear `arume-uk` con su migración y engancharlo en `settings.gradle`, en `arume-app` y en la composición de beans.
5. Adaptar la vista de Empresas.
6. Actualizar pruebas y documentación.

*Rollback*: al no haber datos que preservar, revertir el change es revertir el commit y volver a arrancar con la base recreada.
