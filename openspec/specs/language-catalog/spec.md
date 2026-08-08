## Purpose

Mantener en la base de datos el catálogo de idiomas soportados por el producto y establecer el patrón con el que cualquier catálogo puede almacenar sus textos traducidos, de modo que la interfaz muestre el contenido en el idioma activo del usuario.

### Requirement: Language catalog table
Language catalog table

The system SHALL maintain a `t0_i18n` table storing the languages supported by the product, with columns `language_code VARCHAR(2) NOT NULL` (the ISO 639-1 code in lowercase) and `name VARCHAR(100) NOT NULL` (the language name in English). The primary key `pk_t0` SHALL be `language_code`.

#### Scenario: Table exists after migrations are applied

- **WHEN** the Flyway core migrations have been executed against the application database
- **THEN** the table `t0_i18n` SHALL exist with the columns `language_code` and `name` and primary key `pk_t0` on `language_code`

#### Scenario: Duplicate language code is rejected

- **WHEN** an insert into `t0_i18n` attempts to add a row whose `language_code` already exists
- **THEN** the database SHALL reject the row violating `pk_t0`

### Requirement: Supported languages are seeded
Supported languages are seeded

The core migration SHALL seed `t0_i18n` with exactly the languages available in the user interface: `en` (English) and `es` (Spanish).

#### Scenario: Languages seeded after migration

- **WHEN** the Flyway core migrations have been executed
- **THEN** `t0_i18n` SHALL contain exactly the rows `en`/`English` and `es`/`Spanish`

### Requirement: Translatable catalog content is stored per language
Translatable catalog content is stored per language

Catalog text that must be shown to the user in their own language SHALL be stored in a companion table whose primary key is the composite of the catalog entry key and `language_code`, with a foreign key to `t0_i18n`. A catalog entry SHALL have at most one text per language.

#### Scenario: Translation references an existing language

- **WHEN** a row is inserted into a translatable catalog content table with a `language_code` that does not exist in `t0_i18n`
- **THEN** the database SHALL reject the row violating its foreign key to `t0_i18n`

#### Scenario: Duplicate translation for the same entry and language is rejected

- **WHEN** a row is inserted for a catalog entry and a language for which a text already exists
- **THEN** the database SHALL reject the row violating the composite primary key

### Requirement: Catalog text is resolved by the active language
Catalog text is resolved by the active language

The system SHALL resolve translatable catalog text using the language currently active in the user interface. When no text exists for the active language, the system SHALL fall back to English.

#### Scenario: Text is returned in the active language

- **WHEN** the application requests a translatable catalog text while Spanish is the active language and a Spanish text exists
- **THEN** the system SHALL return the Spanish text

#### Scenario: Fallback to English when the translation is missing

- **WHEN** the application requests a translatable catalog text for an active language that has no text for that entry
- **THEN** the system SHALL return the English text for that entry

#### Scenario: Content follows a language change

- **WHEN** the user changes the interface language while a view showing translatable catalog text is open
- **THEN** the displayed catalog text SHALL be reloaded in the newly active language
