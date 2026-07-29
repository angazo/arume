# External Configuration (Delta)

## MODIFIED Requirements

### Requirement: Config file format
The configuration file SHALL use YAML format compatible with Spring Boot property conventions.

#### Scenario: Config file contains database and settings
- **WHEN** `arume.yml` is written after wizard completion
- **THEN** it SHALL contain `arume.language` with the language code, `arume.db.type` with the database type identifier, `arume.db.encrypt` with the encryption flag, and `spring.datasource.*` properties with url and driver-class-name (without separate username and password fields)

#### Scenario: Config file is human-readable YAML for non-sensitive fields
- **WHEN** a user or administrator opens `arume.yml`
- **THEN** the file SHALL be valid YAML with readable key-value pairs for `arume.*` fields (language, theme, db type, db encrypt) and `spring.datasource.driver-class-name`, while `spring.datasource.url` MAY be encrypted (shown as `ENC(<base64>)`) if `arume.db.encrypt` is true

#### Scenario: Config save includes theme
- **WHEN** `ConfigManager.save()` is called with an `ArumeConfig` that has `theme` set to `"dark-intense"`
- **THEN** the resulting YAML SHALL contain `arume.theme: dark-intense`

### Requirement: Config persistence
The system SHALL persist database configuration after the wizard completes successfully.

#### Scenario: Save config from wizard
- **WHEN** the user completes the wizard with valid input and clicks save
- **THEN** the system SHALL write `arume.yml` with the selected language, database type, JDBC URL (with embedded credentials, optionally encrypted), driver class name, and theme

#### Scenario: Saved config can be read on next startup
- **WHEN** the application restarts after configuration has been saved
- **THEN** the system SHALL read `arume.yml`, decrypt the URL if encrypted, and use the stored values to configure the database connection

#### Scenario: Language can be updated independently
- **WHEN** the user changes language from the main application window
- **THEN** the system SHALL update `arume.language` in `arume.yml` without modifying other configuration values, preserving the encryption state of `spring.datasource.url`

## REMOVED Requirements

### Requirement: Plain text storage in initial iteration
**Reason**: Replaced by optional encryption of the JDBC URL when `arume.db.encrypt` is true. Non-sensitive fields (language, theme, db type, encrypt flag) remain in plain text.
**Migration**: Existing `arume.yml` files with plain text credentials must be deleted and recreated via the first-run wizard. This is acceptable because the project is in early development (no production data).
