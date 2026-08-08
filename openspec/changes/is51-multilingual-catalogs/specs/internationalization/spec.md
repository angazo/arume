## ADDED Requirements

### Requirement: Interface languages match the database language catalog

Every language selectable in the user interface SHALL exist in the database language catalog, and every language in that catalog SHALL have its resource bundle available in the application.

#### Scenario: Selectable languages exist in the catalog

- **WHEN** the language catalog is read after the core migrations have been applied
- **THEN** it SHALL contain exactly the languages the title bar language selector can activate (`en` and `es`)

#### Scenario: Every catalog language has a resource bundle

- **WHEN** the application is built
- **THEN** a resource bundle SHALL exist for each language code present in the language catalog

### Requirement: Active language applies to database catalog content

The active interface language SHALL determine not only the resource bundle texts but also the language used to read translatable catalog content from the database.

#### Scenario: Catalog content follows the active language

- **WHEN** the user changes the interface language while a view displaying database catalog content is open
- **THEN** that content SHALL be reloaded in the newly active language together with the resource bundle texts
