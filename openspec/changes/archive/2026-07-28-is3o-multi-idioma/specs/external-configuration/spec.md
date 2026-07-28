## ADDED Requirements

### Requirement: Language field in configuration
The `arume.yml` configuration file SHALL include a language preference field under the `arume` key.

#### Scenario: Config save includes language
- **WHEN** `ConfigManager.save()` is called with an `ArumeConfig` that has `language` set to `"es"`
- **THEN** the resulting YAML SHALL contain `arume.language: es`

#### Scenario: Config load reads language
- **WHEN** `ConfigManager.load()` reads an `arume.yml` containing `arume.language: en`
- **THEN** the returned `ArumeConfig` SHALL have `language()` equal to `"en"`

#### Scenario: Config file without language field defaults to English
- **WHEN** `ConfigManager.load()` reads an `arume.yml` created by a previous version that does NOT contain `arume.language`
- **THEN** the returned `ArumeConfig` SHALL have `language()` equal to `"en"` (backward compatibility)

#### Scenario: Language can be updated independently
- **WHEN** the user changes language from the main application window
- **THEN** the system SHALL update `arume.language` in `arume.yml` without modifying other configuration values

## MODIFIED Requirements

### Requirement: Config file contains database settings
The configuration file SHALL contain database connection settings and language preference.

#### Scenario: Config file contains database and language settings
- **WHEN** `arume.yml` is written after wizard completion
- **THEN** it SHALL contain `arume.language` with the language code, `arume.db.type` with the database type identifier, and `spring.datasource.*` properties with url, driver-class-name, username, and password
