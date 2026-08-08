## ADDED Requirements

### Requirement: Country modules contribute legal forms to the core catalog

The system SHALL keep the legal form catalog in core and SHALL allow each country module to contribute the legal forms of its jurisdiction by seeding the core catalog table from its own migration. Country modules SHALL NOT define their own legal form tables. The system SHALL read the catalog through a core service that resolves it by jurisdiction code and returns every legal form of that jurisdiction together with the family it belongs to. Resolving the catalog SHALL NOT require classifying the company as a natural person or a legal person.

#### Scenario: Resolve legal forms for a supported jurisdiction

- **WHEN** the application requests legal forms for a jurisdiction whose module has seeded the core catalog
- **THEN** the system SHALL return the legal form codes and descriptions of that jurisdiction, each one indicating whether it is an organization form

#### Scenario: Resolve legal forms for an unsupported jurisdiction

- **WHEN** the application requests legal forms for a jurisdiction that has no rows in the core catalog
- **THEN** the system SHALL indicate that no legal form catalog is available for that jurisdiction

#### Scenario: Catalog resolution does not require a subject type

- **WHEN** the application requests the legal forms of a jurisdiction
- **THEN** the request SHALL NOT require declaring whether the company is a natural person or a legal person, and the response SHALL include every legal form of the jurisdiction

#### Scenario: Catalog is available without any country module capability

- **WHEN** the application resolves the legal form catalog for a jurisdiction
- **THEN** the resolution SHALL NOT depend on the country module exposing a fiscal capability, only on the seeded core catalog data

### Requirement: Core legal form catalog table keyed by jurisdiction and code

The system SHALL maintain a single core table `t5_legal_forms` holding the legal forms of every jurisdiction, with columns
`country_alpha2_code VARCHAR(2) NOT NULL`, `code VARCHAR(100) NOT NULL`, `description VARCHAR(255) NOT NULL` and
`is_organization BOOLEAN NOT NULL`. The primary key `pk_t5` SHALL be the composite of `country_alpha2_code` and `code`, and `fk_t5_t1`
SHALL reference `t1_countries(alpha2_code)`. The `description` SHALL be written in the official language of the jurisdiction that owns the
legal form. The `is_organization` column SHALL be informative only: it SHALL NOT belong to any key and SHALL NOT be interpreted as a
statement about the legal personality of the form or of its members.

#### Scenario: Table exists after core migrations are applied

- **WHEN** the Flyway core migrations have been executed against the application database
- **THEN** the table `t5_legal_forms` SHALL exist with primary key `pk_t5` on (`country_alpha2_code`, `code`) and foreign key `fk_t5_t1` to `t1_countries`

#### Scenario: Legal form must reference an existing country

- **WHEN** an insert into `t5_legal_forms` references a country alpha-2 code that does not exist
- **THEN** the database SHALL reject the row violating `fk_t5_t1`

#### Scenario: Code is unique within a jurisdiction

- **WHEN** two rows share the same country alpha-2 code and the same legal form code
- **THEN** the database SHALL reject the second row violating `pk_t5`, regardless of the value of `is_organization`

#### Scenario: Same code may exist in different jurisdictions

- **WHEN** two rows share the same legal form code but belong to different countries
- **THEN** the database SHALL accept both rows

#### Scenario: Description is written in the language of the jurisdiction

- **WHEN** the legal forms of Spain are read from `t5_legal_forms`
- **THEN** their descriptions SHALL be written in Spanish regardless of the active interface language

### Requirement: United Kingdom legal form catalog is seeded

The United Kingdom module SHALL seed the core legal form catalog with the legal forms under which a business issues invoices in the United Kingdom, from its own Flyway migration, marking each one as an organization form or as a form exercised by an individual. The descriptions SHALL be written in English, the language of the jurisdiction. The United Kingdom module SHALL NOT own a legal form table of its own.

#### Scenario: Seed data is available after migration

- **WHEN** the United Kingdom Flyway migration has been applied on top of the core schema
- **THEN** `t5_legal_forms` SHALL contain, for country `GB`, the forms Sole Trader, Partnership, Limited Liability Partnership, Private Limited Company, Public Limited Company, Company Limited by Guarantee and Community Interest Company, each with a code unique within the jurisdiction and an English description

#### Scenario: Only the individual form is not an organization

- **WHEN** the United Kingdom legal forms are read from the catalog
- **THEN** Sole Trader SHALL be the only form marked as exercised by an individual, and every other form SHALL be marked as an organization

#### Scenario: Partnership is catalogued without stating legal personality

- **WHEN** the Partnership form of the United Kingdom is read from the catalog
- **THEN** it SHALL be present and marked as an organization, even though its members may be natural persons, legal persons or a combination of both

#### Scenario: Charity is not a legal form

- **WHEN** the United Kingdom legal forms are read from the catalog
- **THEN** no entry named Charity SHALL exist, because charitable status can accompany several legal forms and does not change who issues the invoice

### Requirement: Legal form selection in UI is driven by jurisdiction and legal form family

The user interface SHALL display a combo box listing the legal forms available in the core catalog for the selected legal jurisdiction, with each entry showing both the code and its description. The interface SHALL also offer a family filter that narrows that list to organization forms or to forms exercised by an individual. The family filter SHALL be a navigation aid only: it SHALL NOT be stored as part of the company data, and its current value SHALL be determined independently of the text displayed in the active interface language.

#### Scenario: User selects a jurisdiction with legal form catalog

- **WHEN** the user selects a legal jurisdiction that has entries in the core legal form catalog
- **THEN** the legal form combo SHALL be populated with the codes and descriptions of that jurisdiction's catalog for the active family filter, sorted alphabetically by description

#### Scenario: User changes the family filter

- **WHEN** the user changes the family filter
- **THEN** the legal form combo SHALL be reloaded with only the legal forms of the selected family

#### Scenario: Family filter is not stored with the company

- **WHEN** the user creates a company after having used the family filter
- **THEN** the stored company SHALL record the selected legal form and SHALL NOT record any family or subject type of its own

#### Scenario: Family filter survives a language change

- **WHEN** the interface language changes while the Companies view is open
- **THEN** the family filter SHALL show its labels in the new language and SHALL keep its current selection, and the legal form combo SHALL keep listing the same forms

#### Scenario: Selected jurisdiction has no legal forms

- **WHEN** the selected legal jurisdiction has no entries in the core legal form catalog
- **THEN** the legal form combo SHALL be disabled and SHALL display a message indicating no forms are available, so that an incompletely seeded jurisdiction degrades visibly instead of offering an empty list

## MODIFIED Requirements

### Requirement: Spanish legal form catalog is seeded

The Spanish module SHALL seed the core legal form catalog with the legal forms applicable in Spain from its own Flyway migration, marking each one as an organization form or as a form exercised by an individual. The Spanish module SHALL NOT own a legal form table of its own.

#### Scenario: Seed data is available after migration

- **WHEN** the Spanish Flyway migration has been applied on top of the core schema
- **THEN** `t5_legal_forms` SHALL contain the legal forms recognized under Spanish law for country `ES`, each with a code unique within the jurisdiction, a human-readable Spanish description and its family

#### Scenario: Seed includes natural person forms

- **WHEN** the application lists the Spanish legal forms filtered to forms exercised by an individual
- **THEN** the catalog SHALL include the entrepreneurial forms of a natural person (e.g. Empresario individual, Profesional autónomo, ERL), marked as not being organizations

#### Scenario: No Spanish legal form table exists

- **WHEN** the schema is inspected after core and Spanish migrations have been applied
- **THEN** no `es3_legal_forms` table SHALL exist

## REMOVED Requirements

### Requirement: Country modules can expose legal form catalogs

**Reason**: Replaced by "Country modules contribute legal forms to the core catalog". The catalog is no longer resolved by jurisdiction *and subject type*: the natural person / legal person classification cannot be applied consistently across jurisdictions, so the lookup is now driven by jurisdiction alone and each returned form declares which family it belongs to.

**Migration**: No data migration is needed. The application database is recreated from scratch and each module keeps a single starting migration.

### Requirement: Core legal form catalog table

**Reason**: Replaced by "Core legal form catalog table keyed by jurisdiction and code". The subject type left the primary key, so the same code can no longer exist twice within a jurisdiction, and the classification survives only as the informative `is_organization` column.

**Migration**: No data migration is needed. The core migration is rewritten in place and each national module re-seeds its own rows.

### Requirement: Legal form selection in UI is driven by jurisdiction and subject type

**Reason**: Replaced by "Legal form selection in UI is driven by jurisdiction and legal form family". The subject type stopped being a company attribute, so the same control survives as a filter over the legal form list instead of as a value stored with the company.

**Migration**: No data migration is needed. No company row carries a subject type to convert.
