## ADDED Requirements

### Requirement: Company identity is a fiscal identification and a legal form

The system SHALL create each company with a primary fiscal identification code and a legal form. The primary fiscal jurisdiction and legal form jurisdiction SHALL be expressed as ISO alpha-2 codes and validated against the countries catalog (`t1_countries`). The combination of legal form jurisdiction and legal form code SHALL be validated against the core legal form catalog, both by the application and by a database foreign key. A company SHALL NOT carry a subject type of its own: the distinction between natural person and legal person describes how the business was incorporated, not who issues an invoice, and it cannot be applied consistently across jurisdictions. Once the company has been created, those identity fields SHALL NOT be changeable through normal business operations.

#### Scenario: Create company with fiscal identity
- **WHEN** a user submits a company with a primary fiscal identification code, a legal form selected from the jurisdiction's catalog, legal name, fiscal residence and domicile
- **THEN** the system SHALL create the company with those values and a unique internal identifier

#### Scenario: Company is created without declaring a subject type
- **WHEN** a company is created
- **THEN** the system SHALL NOT require, store or expose a natural person / legal person classification for that company

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

#### Scenario: Database rejects an inconsistent legal form
- **WHEN** a company row is written with a combination of legal form jurisdiction and legal form code that does not exist in the core legal form catalog
- **THEN** the database SHALL reject the row violating the foreign key to the legal form catalog

#### Scenario: Company whose legal form has mixed membership
- **WHEN** a user submits a company whose legal form may be composed of natural persons, legal persons or both, such as a United Kingdom Partnership
- **THEN** the system SHALL create the company from its legal form and fiscal identification alone, without requiring the composition of its members to be classified

## MODIFIED Requirements

### Requirement: Jurisdiction is selected from the localized countries catalog

The Companies view SHALL let the user pick the jurisdiction from a list of countries showing the country name in the active interface language, instead of typing a code. The list SHALL be restricted to the jurisdictions whose national module is installed, so the user cannot create a company in a jurisdiction the product does not support yet. The selected jurisdiction SHALL drive the legal form catalog lookup, and the list SHALL be refreshed when the interface language changes. The view SHALL NOT privilege any single jurisdiction: every supported country SHALL be usable on equal terms.

#### Scenario: Jurisdiction list shows localized names

- **WHEN** the user opens the Companies view while Spanish is the active language
- **THEN** the jurisdiction selector SHALL list the supported jurisdictions with their Spanish names

#### Scenario: Only supported jurisdictions are offered

- **WHEN** the user opens the Companies view with the Spanish and United Kingdom modules installed
- **THEN** the jurisdiction selector SHALL offer exactly Spain and the United Kingdom, and SHALL NOT offer catalog countries without a national module

#### Scenario: Jurisdiction selection drives the legal form catalog

- **WHEN** the user selects a country in the jurisdiction selector
- **THEN** the legal form combo SHALL be reloaded with the legal forms of the selected jurisdiction under the active family filter

#### Scenario: Jurisdiction list follows a language change

- **WHEN** the interface language changes while the Companies view is open
- **THEN** the jurisdiction selector SHALL show the country names in the new language and SHALL keep the current selection

#### Scenario: Jurisdiction other than the default is fully usable

- **WHEN** the user selects the United Kingdom in the jurisdiction selector
- **THEN** the legal form combo SHALL be populated with the United Kingdom catalog and the company SHALL be creatable without any Spanish data or default

## REMOVED Requirements

### Requirement: Company has a protected fiscal identity

**Reason**: Replaced by "Company identity is a fiscal identification and a legal form". The company no longer has a subject type, so the scenarios that created a natural person company, created a legal person company, protected the subject type from changes and validated the legal form against the subject type no longer describe any behavior of the system. The rest of the identity protection is preserved verbatim in the replacement requirement.

**Migration**: No data migration is needed. The application database is recreated from scratch and each module keeps a single starting migration, so no company row carries a subject type to convert.
