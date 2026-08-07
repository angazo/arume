# company-management Specification

## Purpose

Define the common fiscal identity of a company while preserving the history of data that can evolve during the life of the legal entity and allowing local fiscal registrations in multiple jurisdictions.

## Requirements

### Requirement: Company has a protected fiscal identity

The system SHALL create each company with a primary fiscal identification code and a legal form. Once the company has been created, those two identity fields SHALL NOT be changeable through normal business operations.

#### Scenario: Create company with fiscal identity
- **WHEN** a user submits a company with a primary fiscal identification code, legal form, legal name, fiscal residence and domicile
- **THEN** the system SHALL create the company with those values and a unique internal identifier

#### Scenario: Attempt to change the fiscal identification code
- **WHEN** a user attempts to change the primary fiscal identification code of an existing company
- **THEN** the system SHALL reject the operation and SHALL preserve the original code

#### Scenario: Attempt to change the legal form
- **WHEN** a user attempts to change the legal form of an existing company
- **THEN** the system SHALL reject the operation and SHALL preserve the original legal form

### Requirement: Company preserves evolving data

The system SHALL allow evolutive company data, including legal name, domicile and fiscal residence, to change without destroying the previous value. Each changed value SHALL remain associated with an effective period or an equivalent historical record.

#### Scenario: Change company domicile
- **WHEN** a user registers a new domicile for an existing company
- **THEN** the new domicile SHALL become current and the previous domicile SHALL remain recoverable with its historical validity

#### Scenario: Change company fiscal residence
- **WHEN** a user registers a new fiscal residence for an existing company
- **THEN** the new residence SHALL become current and the previous residence SHALL remain recoverable with its historical validity

### Requirement: Company supports local fiscal registrations

The system SHALL allow a company to have zero or more local fiscal registrations associated with a jurisdiction and SHALL preserve the validity history of each registration. Country modules SHALL own the jurisdiction-specific validation rules for those registrations.

#### Scenario: Register a local fiscal code
- **WHEN** a country module accepts a local fiscal registration for a company and jurisdiction
- **THEN** the system SHALL store the registration without changing the company primary fiscal identity

#### Scenario: List companies
- **WHEN** the user opens the Companies view
- **THEN** the system SHALL display the companies available in the configured database

#### Scenario: Create company from the Companies view
- **WHEN** the user submits valid company data from the Companies view
- **THEN** the system SHALL create the company through the business use case and refresh the displayed list
