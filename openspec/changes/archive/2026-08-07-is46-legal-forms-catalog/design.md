## Context

`t4_companies` define la identidad fiscal de una empresa con dos columnas de jurisdicción (`primary_fiscal_jurisdiction`, `legal_form_jurisdiction`) y un código de forma jurídica (`legal_form_code`). Actualmente las jurisdicciones son `VARCHAR(3)` sin FK hacia `t1_countries`, y `legal_form_code` es texto libre sin catálogo. La vista de Empresas precarga "ESP" y "SL" como valores por defecto en `TextField`. El programa debe soportar tanto personas físicas como jurídicas, por lo que falta distinguir el tipo de sujeto.

Véase `proposal.md` para la motivación y `specs/` para los contratos de comportamiento. El documento `docs/tipos-de-empresas.md` (sección 10) define el catálogo conceptual de formas jurídicas españolas.

## Goals / Non-Goals

**Goals:**

- Añadir integridad referencial desde las jurisdicciones de empresa hacia el catálogo de países.
- Distinguir persona física de persona jurídica en el modelo core mediante `is_legal_person`.
- Crear el catálogo español de formas jurídicas como tabla `es3_legal_forms` con seed clasificado por tipo de sujeto.
- Exponer `LegalFormsCapability` como capacidad fiscal resoluble por jurisdicción y filtrable por tipo de sujeto.
- Sustituir el `TextField` de forma jurídica por un `ComboBox` condicionado por el tipo de sujeto y la jurisdicción.
- Mantener la inmutabilidad de la identidad fiscal (incluido el tipo de sujeto) tras la creación de la empresa.

**Non-Goals:**

- Catálogos de formas jurídicas para países distintos de España.
- Validación de formato del identificador fiscal primario (NIF, UTR, EIN...).
- Selector de jurisdicción en UI (sigue siendo `TextField`); solo se añade el selector de tipo de sujeto y de forma jurídica.

## Decisions

### D1. Fusionar el nuevo alcance en las migraciones core y es existentes

El proyecto está en sus inicios: la BBDD es en memoria (`jdbc:h2:mem:arume`) y se recrea desde cero en cada arranque. Las migraciones de este change no se han aplicado a ningún entorno persistente. Por ello se fusiona el nuevo alcance en las migraciones ya existentes en lugar de crear V0.1.0.4/V0.1.0.5:

- `V0.1.0.2__business_core.sql` (core) absorbe el contenido de la antigua `V0.1.0.3__company_jurisdiction_fks.sql`: las FKs a `t1_countries.alpha3_code` y la columna `is_legal_person` pasan a formar parte del `CREATE TABLE t4_companies`, con `is_legal_person` como segunda columna. La migración V0.1.0.3 core se elimina.
- `t4_companies.created_at` se declara `TIMESTAMP WITH TIME ZONE` (no `TIMESTAMP`) para que la fecha de creación de la empresa conserve el offset de zona horaria. El modelo MyBatis lo mapea a `java.time.OffsetDateTime` y el adaptador persiste `OffsetDateTime.now()`.
- `V0.1.0.3__legal_forms.sql` (es) mantiene la columna `is_legal_person` y el seed ampliado.
- `SpainModuleDescriptor.minimumCoreSchemaVersion()` vuelve a `0.1.0.2`, ya que el esquema core vuelve a terminar en esa versión.

Alternativa considerada: crear migraciones nuevas. Se descarta porque el usuario prefiere mantener el historial compacto mientras el esquema aún no está publicado.

### D2. FK sobre `alpha3_code`, no sobre `numeric_code`

`t4_companies.primary_fiscal_jurisdiction` y `legal_form_jurisdiction` son `VARCHAR(3)` y almacenan el código ISO alpha-3. La FK apunta a `t1_countries.alpha3_code`, que tiene `UNIQUE`. Aunque la PK de `t1_countries` es `numeric_code`, el tipo ya coincide y evita una migración de datos o un cambio de tipo de columna.

Alternativa considerada: migrar las columnas a `SMALLINT` y referenciar `numeric_code`. Se descarta por ser más invasivo sin beneficio: el alpha-3 es el formato natural para jurisdicciones y ya se usa en `t5_company_profiles.fiscal_residence` y `t6_company_tax_registrations.jurisdiction`.

### D3. Tabla `es3_legal_forms` con FK a `t1_countries.numeric_code` y tipo de sujeto

La tabla de formas jurídicas españolas referencia el país vía `country_numeric_code SMALLINT`, siguiendo el patrón establecido por `t3_country_currency`, e incluye `is_legal_person BOOLEAN NOT NULL` para clasificar cada forma por tipo de sujeto.

```sql
CREATE TABLE es3_legal_forms (
    code                  VARCHAR(100) NOT NULL,
    country_numeric_code  SMALLINT     NOT NULL,
    description           VARCHAR(255) NOT NULL,
    is_legal_person       BOOLEAN      NOT NULL,
    CONSTRAINT pk_es3 PRIMARY KEY (code, country_numeric_code),
    CONSTRAINT fk_es3_t1 FOREIGN KEY (country_numeric_code) REFERENCES t1_countries (numeric_code)
);
```

Alternativa considerada: `subject_type VARCHAR(16)` con CHECK. Se descarta en favor del boolean `is_legal_person` por decisión del usuario, que lo considera más simple para un concepto binario.

### D4. Seed español según `docs/tipos-de-empresas.md`, sección 10

El seed se alinea con la clasificación conceptual del documento y elimina SLP, Asociación y Fundación (no contempladas). Quedan 17 formas:

- `is_legal_person = false` (persona física): EI (Empresario individual), PA (Profesional autónomo), ERL (Emprendedor de Responsabilidad Limitada).
- `is_legal_person = true` (persona jurídica): SL, SLU, SA, SAU, SColl (Sociedad Colectiva), SCom (Comanditaria Simple), SComA (Comanditaria por Acciones), SCoop, SLL, SAL, SC (Sociedad Civil), CB (Comunidad de Bienes), AIE, SAT.

Las descripciones se almacenan en la BBDD en el idioma de la jurisdicción (español para España) porque son denominaciones legales, no texto de UI.

### D5. `SubjectType` como enum en el dominio, `is_legal_person` como boolean en BBDD

`arume-core` define `SubjectType { NATURAL_PERSON, LEGAL_PERSON }` con un helper `isLegalPerson()`. El dominio usa el enum; los adaptadores de persistencia mapean `LEGAL_PERSON ↔ true` / `NATURAL_PERSON ↔ false`. `Company` gana `subjectType` como campo de identidad inmutable y `CreateCompanyCommand` lo incorpora.

```java
public enum SubjectType {
    NATURAL_PERSON(false),
    LEGAL_PERSON(true);

    private final boolean legalPerson;

    public boolean isLegalPerson() {
        return legalPerson;
    }
}
```

Alternativa considerada: boolean directamente en dominio. Se descarta porque el enum da expresividad (`SubjectType.LEGAL_PERSON`) sin riesgo de booleanos voladores en el código de negocio.

### D6. `LegalFormsCapability` como interfaz en `arume-core`

Se define `LegalFormsCapability extends FiscalCapability` en `arume-core` con un método `List<LegalFormItem> getLegalForms(SubjectType subjectType)`. El registro `LegalFormItem` contiene `code` y `description`. La implementación española (`SpainLegalFormsCapability`) consulta el repositorio de `es3_legal_forms` filtrando por país y `is_legal_person`.

```java
public interface LegalFormsCapability extends FiscalCapability {
    List<LegalFormItem> getLegalForms(SubjectType subjectType);
    record LegalFormItem(String code, String description) {}
}
```

Alternativa considerada: que `LegalFormsCapability` esté en `arume-es`. Se descarta porque el core necesita conocer el contrato para que `CompaniesController` pueda resolverlo sin depender de `arume-es`.

### D7. El controlador resuelve la capacidad y valida antes de crear la empresa

`CompaniesController` inyecta `FiscalModuleRegistry` y, cuando el usuario selecciona jurisdicción y tipo de sujeto, resuelve `LegalFormsCapability` para poblar el `ComboBox`. Antes de llamar a `CompanyApplicationService.create()`, valida que el código seleccionado pertenezca al catálogo filtrado por tipo de sujeto. El servicio de aplicación mantiene su firma actual (`CreateCompanyCommand` con `LegalFormCode` y `SubjectType`) porque la validación de pertenencia al catálogo es una preocupación de UI + capacidad, no del dominio puro.

Alternativa considerada: validar dentro de `CompanyApplicationService`. Se descarta porque requeriría inyectar `FiscalModuleRegistry` en el servicio de aplicación del core, acoplando el core a la infraestructura de módulos.

### D8. ComboBox con `code — description` sin celda personalizada

El `ComboBox<String>` se pobla con strings formateados `"SL — Sociedad Limitada"`. Se usa `valueProperty().addListener()` para extraer el código cuando el usuario selecciona. No se usa una celda personalizada con objeto `LegalFormItem` porque añadiría complejidad innecesaria: el `String` ya contiene toda la información visible y el código es extraíble con `split(" — ")[0]`.

Alternativa considerada: `ComboBox<LegalFormItem>` con `cellFactory` y `buttonCell`. Se descarta por ser más código sin beneficio funcional en este caso.

### D9. Selector de tipo de sujeto en la UI

El formulario de Empresas gana un `ComboBox<String>` de tipo de sujeto (persona física / jurídica) con claves i18n. Por defecto se preselecciona persona jurídica para preservar el comportamiento actual (SL). Al cambiar la selección, se recarga el combo de formas jurídicas con el catálogo filtrado por el nuevo tipo de sujeto.

Alternativa considerada: un `ToggleGroup` con dos `RadioButton`. Se descarta por consistencia con el patrón de `ComboBox` ya usado en el wizard y en la vista de Empresas.

### D10. Actualizar `SpainModuleDescriptor` a versión 0.1.0.3

`SpainModuleDescriptor.minimumCoreSchemaVersion()` y `migrationDescriptor().maxSchemaVersion()` se mantienen en `0.1.0.3`, ya que las migraciones core y es se completan en esas versiones y no se crean versiones nuevas.

## Risks / Trade-offs

- **[Riesgo] La FK de alpha3_code requiere que t1_countries esté poblada**: la migración core V0.1.0.2 debe ejecutarse después de V0.1.0.1 (países). → **Mitigación**: Flyway garantiza el orden numérico; V0.1.0.1 < V0.1.0.2.
- **[Riesgo] Modificar migraciones rompería un entorno persistente**: no aplica hoy porque la BBDD es en memoria y se recrea desde cero. → **Mitigación**: se documenta que esta decisión es válida solo mientras no existan entornos publicados.
- **[Riesgo] Sin catálogo para otras jurisdicciones, el combo quedará vacío**: si un usuario teclea "GBR" como jurisdicción, no habrá formas jurídicas disponibles. → **Mitigación**: el spec contempla este caso: el combo se deshabilita con mensaje informativo. Las futuras ampliaciones de catálogo resolverán otras jurisdicciones.
- **[Trade-off] Validación en el controlador, no en el servicio de aplicación**: el servicio de aplicación acepta cualquier `LegalFormCode`. Si en el futuro otro punto de entrada (API REST, batch) crea empresas, la validación no se aplicará automáticamente. → Se acepta porque el contrato de capacidad es la frontera correcta y una API REST futura tendría su propia capa de validación.

## Open Questions

- ¿Deben las descripciones de formas jurídicas ser i18nizables o fijas en el idioma de la jurisdicción? Se asume fijas en el idioma de la jurisdicción (español para España) porque son denominaciones legales. Revisitable si se añaden jurisdicciones con varios idiomas oficiales.
