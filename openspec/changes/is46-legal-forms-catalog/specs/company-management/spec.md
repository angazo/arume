## MODIFIED Requirements

### Requirement: Company has a protected fiscal identity

The system SHALL create each company with a subject type (natural person or legal person), a primary fiscal identification code and a legal form. The primary fiscal jurisdiction and legal form jurisdiction SHALL be validated against the countries catalog (`t1_countries`). The legal form code SHALL be validated against the legal form catalog of the selected legal form jurisdiction and subject type. Once the company has been created, those identity fields SHALL NOT be changeable through normal business operations.

#### Scenario: Create company with fiscal identity

- **WHEN** a user submits a company with a subject type, a primary fiscal identification code, a legal form selected from the jurisdiction's catalog, legal name, fiscal residence and domicile
- **THEN** the system SHALL create the company with those values and a unique internal identifier

#### Scenario: Create a natural person company

- **WHEN** a user submits a natural person company with a primary fiscal identification code, a natural-person legal form selected from the jurisdiction's catalog, legal name, fiscal residence and domicile
- **THEN** the system SHALL create the company with `is_legal_person = false` and a unique internal identifier

#### Scenario: Create a legal person company

- **WHEN** a user submits a legal person company with a primary fiscal identification code, a legal-person legal form selected from the jurisdiction's catalog, legal name, fiscal residence and domicile
- **THEN** the system SHALL create the company with `is_legal_person = true` and a unique internal identifier

#### Scenario: Attempt to change the subject type

- **WHEN** a user attempts to change the subject type of an existing company
- **THEN** the system SHALL reject the operation and SHALL preserve the original subject type

#### Scenario: Attempt to change the fiscal identification code

- **WHEN** a user attempts to change the primary fiscal identification code of an existing company
- **THEN** the system SHALL reject the operation and SHALL preserve the original code

#### Scenario: Attempt to change the legal form

- **WHEN** a user attempts to change the legal form of an existing company
- **THEN** the system SHALL reject the operation and SHALL preserve the original legal form

#### Scenario: Jurisdiction references an unknown country

- **WHEN** a user submits a company with a primary fiscal jurisdiction or legal form jurisdiction that does not exist in the countries catalog
- **THEN** the system SHALL reject the operation and SHALL indicate the jurisdiction is not recognized

#### Scenario: Legal form not in jurisdiction catalog

- **WHEN** a user submits a company with a legal form code that is not registered in the selected legal form jurisdiction's catalog
- **THEN** the system SHALL reject the operation and SHALL indicate the legal form is not valid for that jurisdiction

#### Scenario: Legal form incompatible with subject type

- **WHEN** a user submits a company whose legal form code is not registered for the company's subject type in the selected legal form jurisdiction's catalog
- **THEN** the system SHALL reject the operation and SHALL indicate the legal form is not valid for that subject type
