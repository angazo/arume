## ADDED Requirements

### Requirement: Jurisdiction codes are ISO alpha-2

The system SHALL express every business jurisdiction code — the company primary fiscal jurisdiction, the legal form jurisdiction, the profile fiscal residence and the jurisdiction of a local fiscal registration — as an ISO 3166-1 alpha-2 code in uppercase, and SHALL reject any other format.

#### Scenario: Alpha-2 code is accepted

- **WHEN** a jurisdiction code `ES` is supplied to a business operation
- **THEN** the system SHALL accept it

#### Scenario: Alpha-3 code is rejected

- **WHEN** a jurisdiction code `ESP` is supplied to a business operation
- **THEN** the system SHALL reject it and SHALL indicate that the jurisdiction code format is invalid

#### Scenario: Lowercase code is rejected

- **WHEN** a jurisdiction code `es` is supplied to a business operation
- **THEN** the system SHALL reject it and SHALL indicate that the jurisdiction code format is invalid

### Requirement: Jurisdiction is selected from the localized countries catalog

The Companies view SHALL let the user pick the jurisdiction from a list of catalog countries showing the country name in the active interface language, instead of typing a code. The selected jurisdiction SHALL drive the legal form catalog lookup, and the list SHALL be refreshed when the interface language changes.

#### Scenario: Jurisdiction list shows localized names

- **WHEN** the user opens the Companies view while Spanish is the active language
- **THEN** the jurisdiction selector SHALL list the catalog countries with their Spanish names

#### Scenario: Jurisdiction selection drives the legal form catalog

- **WHEN** the user selects a country in the jurisdiction selector
- **THEN** the legal form combo SHALL be reloaded with the legal forms of the selected jurisdiction and the current subject type

#### Scenario: Jurisdiction list follows a language change

- **WHEN** the interface language changes while the Companies view is open
- **THEN** the jurisdiction selector SHALL show the country names in the new language and SHALL keep the current selection

## MODIFIED Requirements

### Requirement: Company has a protected fiscal identity

The system SHALL create each company with a subject type (natural person or legal person), a primary fiscal identification code and a legal form. The primary fiscal jurisdiction and legal form jurisdiction SHALL be expressed as ISO alpha-2 codes and validated against the countries catalog (`t1_countries`). The combination of legal form jurisdiction, subject type and legal form code SHALL be validated against the core legal form catalog, both by the application and by a database foreign key. Once the company has been created, those identity fields SHALL NOT be changeable through normal business operations.

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

#### Scenario: Database rejects an inconsistent legal form
- **WHEN** a company row is written with a combination of legal form jurisdiction, subject type and legal form code that does not exist in the core legal form catalog
- **THEN** the database SHALL reject the row violating the composite foreign key to the legal form catalog
