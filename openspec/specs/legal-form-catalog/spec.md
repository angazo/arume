# legal-form-catalog Specification

## Purpose

Proveer un catálogo de formas jurídicas por jurisdicción y tipo de sujeto, expuesto como capacidad fiscal resoluble a través de `FiscalModuleRegistry`, permitiendo que el usuario seleccione la forma jurídica de una empresa desde una lista validada en lugar de teclear un código arbitrario.

## Requirements

### Requirement: Country modules can expose legal form catalogs
Country modules can expose legal form catalogs

The system SHALL keep the legal form catalog in core and SHALL allow each country module to contribute the legal forms of its jurisdiction by seeding the core catalog table from its own migration. Country modules SHALL NOT define their own legal form tables. The system SHALL read the catalog through a core service that resolves it by jurisdiction code and filters it by subject type (natural person or legal person).

#### Scenario: Resolve legal forms for a supported jurisdiction

- **WHEN** the application requests legal forms for a jurisdiction whose module has seeded the core catalog
- **THEN** the system SHALL return the legal form codes and descriptions of that jurisdiction for the requested subject type

#### Scenario: Resolve legal forms for an unsupported jurisdiction

- **WHEN** the application requests legal forms for a jurisdiction that has no rows in the core catalog
- **THEN** the system SHALL indicate that no legal form catalog is available for that jurisdiction

#### Scenario: Filter legal forms by subject type

- **WHEN** the application requests legal forms for a subject type
- **THEN** the system SHALL return only the legal forms applicable to that subject type

#### Scenario: Catalog is available without any country module capability

- **WHEN** the application resolves the legal form catalog for a jurisdiction
- **THEN** the resolution SHALL NOT depend on the country module exposing a fiscal capability, only on the seeded core catalog data

### Requirement: Spanish legal form catalog is seeded
Spanish legal form catalog is seeded

The Spanish module SHALL seed the core legal form catalog with the legal forms applicable in Spain, classified by subject type, from its own Flyway migration. The Spanish module SHALL NOT own a legal form table of its own.

#### Scenario: Seed data is available after migration

- **WHEN** the Spanish Flyway migration has been applied on top of the core schema
- **THEN** `t5_legal_forms` SHALL contain the legal forms recognized under Spanish law per the project's reference document for country `ES`, each with a unique code within its subject type, a human-readable Spanish description and its subject type

#### Scenario: Seed includes natural person forms

- **WHEN** the application lists natural person legal forms for Spain
- **THEN** the catalog SHALL include the entrepreneurial forms of a natural person (e.g. Empresario individual, Profesional autónomo, ERL)

#### Scenario: No Spanish legal form table exists

- **WHEN** the schema is inspected after core and Spanish migrations have been applied
- **THEN** no `es3_legal_forms` table SHALL exist

### Requirement: Legal form selection in UI is driven by jurisdiction and subject type
Legal form selection in UI is driven by jurisdiction and subject type

The user interface SHALL let the user choose a subject type (natural person or legal person) and SHALL display a combo box listing the legal forms available in the core catalog for the selected legal jurisdiction and subject type, with each entry showing both the code and its description.

#### Scenario: User selects a jurisdiction with legal form catalog

- **WHEN** the user selects a legal jurisdiction that has entries in the core legal form catalog and a subject type
- **THEN** the legal form combo SHALL be populated with the codes and descriptions from that jurisdiction's catalog for the selected subject type, sorted alphabetically by description

#### Scenario: User changes the subject type

- **WHEN** the user changes the subject type
- **THEN** the legal form combo SHALL be reloaded with the legal forms applicable to the newly selected subject type

#### Scenario: User selects a jurisdiction without legal form catalog

- **WHEN** the user selects a legal jurisdiction that has no entries in the core legal form catalog
- **THEN** the legal form combo SHALL be disabled and SHALL display a message indicating no forms are available

### Requirement: Core legal form catalog table
Core legal form catalog table

The system SHALL maintain a single core table `t5_legal_forms` holding the legal forms of every jurisdiction, with columns
`country_alpha2_code VARCHAR(2) NOT NULL`, `is_legal_person BOOLEAN NOT NULL`, `code VARCHAR(100) NOT NULL` and `description VARCHAR(255) NOT NULL`.
The primary key `pk_t5` SHALL be the composite of `country_alpha2_code`, `is_legal_person` and `code`, and `fk_t5_t1` SHALL reference
`t1_countries(alpha2_code)`. The `description` SHALL be written in the official language of the jurisdiction that owns the legal form.

#### Scenario: Table exists after core migrations are applied

- **WHEN** the Flyway core migrations have been executed against the application database
- **THEN** the table `t5_legal_forms` SHALL exist with primary key `pk_t5` on (`country_alpha2_code`, `is_legal_person`, `code`) and foreign key `fk_t5_t1` to `t1_countries`

#### Scenario: Legal form must reference an existing country

- **WHEN** an insert into `t5_legal_forms` references a country alpha-2 code that does not exist
- **THEN** the database SHALL reject the row violating `fk_t5_t1`

#### Scenario: Same code may exist for both subject types

- **WHEN** two rows share the same country and code but differ in `is_legal_person`
- **THEN** the database SHALL accept both rows

#### Scenario: Description is written in the language of the jurisdiction

- **WHEN** the legal forms of Spain are read from `t5_legal_forms`
- **THEN** their descriptions SHALL be written in Spanish regardless of the active interface language
