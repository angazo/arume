# External Configuration

## Purpose

Manage the `arume.yml` configuration file that stores database connection settings and language preference alongside the application JAR.

## Requirements

### Requirement: Config file location
The system SHALL store configuration in a file named `arume.yml` located in the same directory as the application JAR.

#### Scenario: Config file is written to JAR directory
- **WHEN** the configuration is saved
- **THEN** a file named `arume.yml` SHALL be created in the directory containing the JAR

#### Scenario: Config file is read from JAR directory
- **WHEN** the application starts and needs to read configuration
- **THEN** the system SHALL look for `arume.yml` in the directory containing the JAR

### Requirement: Config file format
The configuration file SHALL use YAML format compatible with Spring Boot property conventions.

#### Scenario: Config file contains database and language settings
- **WHEN** `arume.yml` is written after wizard completion
- **THEN** it SHALL contain `arume.language` with the language code, `arume.db.type` with the database type identifier, and `spring.datasource.*` properties with url, driver-class-name, username, and password

#### Scenario: Config file is human-readable YAML
- **WHEN** a user or administrator opens `arume.yml`
- **THEN** the file SHALL be valid YAML with readable key-value pairs

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

### Requirement: Config persistence
The system SHALL persist database configuration after the wizard completes successfully.

#### Scenario: Save config from wizard
- **WHEN** the user completes the wizard with valid input and clicks save
- **THEN** the system SHALL write `arume.yml` with the selected language, database type, storage path, username, and password

#### Scenario: Saved config can be read on next startup
- **WHEN** the application restarts after configuration has been saved
- **THEN** the system SHALL read `arume.yml` and use the stored values to configure the database connection

### Requirement: Config overwrite protection
The system SHALL NOT overwrite an existing `arume.yml` without explicit user action.

#### Scenario: Config already exists at startup
- **WHEN** the application starts and `arume.yml` already exists
- **THEN** the wizard SHALL NOT be displayed and the existing config SHALL be used

### Requirement: Plain text storage in initial iteration
In this iteration, all configuration values SHALL be stored in plain text within `arume.yml`.

#### Scenario: Credentials are stored in plain text
- **WHEN** `arume.yml` is written after wizard completion
- **THEN** username and password SHALL be stored as plain text strings in the YAML file
