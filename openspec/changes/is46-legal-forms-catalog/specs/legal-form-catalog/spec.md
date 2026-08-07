## Purpose

Proveer un catálogo de formas jurídicas por jurisdicción y tipo de sujeto, expuesto como capacidad fiscal resoluble a través de `FiscalModuleRegistry`, permitiendo que el usuario seleccione la forma jurídica de una empresa desde una lista validada en lugar de teclear un código arbitrario.

## ADDED Requirements

### Requirement: Country modules can expose legal form catalogs

The system SHALL allow each country module to expose a catalog of legal form codes and descriptions for its jurisdiction through the `FiscalCapability` contract. The system SHALL resolve the catalog by jurisdiction code at runtime through `FiscalModuleRegistry` and SHALL filter it by subject type (natural person or legal person).

#### Scenario: Resolve legal forms for a supported jurisdiction

- **WHEN** the application requests legal forms for a jurisdiction with an installed module that implements `LegalFormsCapability`
- **THEN** the system SHALL return the legal form codes and descriptions registered in that module for the requested subject type

#### Scenario: Resolve legal forms for an unsupported jurisdiction

- **WHEN** the application requests legal forms for a jurisdiction without an installed module implementing `LegalFormsCapability`
- **THEN** the system SHALL indicate that no legal form catalog is available for that jurisdiction

#### Scenario: Filter legal forms by subject type

- **WHEN** the application requests legal forms for a subject type
- **THEN** the system SHALL return only the legal forms applicable to that subject type

### Requirement: Spanish legal form catalog is seeded

The Spanish module SHALL include a catalog of legal forms applicable in Spain, classified by subject type and stored in a migration-seeded table with code, description, a foreign key to the countries catalog and a boolean marking whether the form applies to a legal person.

#### Scenario: Seed data is available after migration

- **WHEN** the Spanish Flyway migration `es3_legal_forms` is applied
- **THEN** the table SHALL contain the legal forms recognized under Spanish law per the project's reference document, each with a unique code, a human-readable description and its subject type

#### Scenario: Seed includes natural person forms

- **WHEN** the application lists natural person legal forms for Spain
- **THEN** the catalog SHALL include the entrepreneurial forms of a natural person (e.g. Empresario individual, Profesional autónomo, ERL)

### Requirement: Legal form selection in UI is driven by jurisdiction and subject type

The user interface SHALL let the user choose a subject type (natural person or legal person) and SHALL display a combo box listing the legal forms available for the selected legal jurisdiction and subject type, with each entry showing both the code and its description.

#### Scenario: User selects a jurisdiction with legal form catalog

- **WHEN** the user selects a legal jurisdiction that has a `LegalFormsCapability` registered and a subject type
- **THEN** the legal form combo SHALL be populated with the codes and descriptions from that jurisdiction's catalog for the selected subject type, sorted alphabetically by description

#### Scenario: User changes the subject type

- **WHEN** the user changes the subject type
- **THEN** the legal form combo SHALL be reloaded with the legal forms applicable to the newly selected subject type

#### Scenario: User selects a jurisdiction without legal form catalog

- **WHEN** the user selects a legal jurisdiction that does not have a `LegalFormsCapability` registered
- **THEN** the legal form combo SHALL be disabled and SHALL display a message indicating no forms are available
