# Database Bootstrap (Delta)

## MODIFIED Requirements

### Requirement: Datasource configuration from external config
The system SHALL configure the Spring Boot datasource using values from `arume.yml`.

#### Scenario: H2 datasource URL is constructed without encryption
- **WHEN** the configuration specifies `arume.db.type: h2`, `arume.db.encrypt: false`, and a storage path with username and password
- **THEN** the system SHALL construct a JDBC URL in the format `jdbc:h2:file:<path>/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;USER=<username>;PASSWORD=<password>`

#### Scenario: H2 datasource URL is constructed with encryption
- **WHEN** the configuration specifies `arume.db.type: h2`, `arume.db.encrypt: true`, and a storage path with username and password
- **THEN** the system SHALL construct a JDBC URL in the format `jdbc:h2:file:<path>/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;CIPHER=AES;USER=<username>;PASSWORD=<password>`

#### Scenario: Credentials are embedded in the URL
- **WHEN** the configuration is saved after wizard completion
- **THEN** the username and password SHALL be embedded in the JDBC URL as `;USER=<username>;PASSWORD=<password>` parameters and SHALL NOT be stored as separate `spring.datasource.username` and `spring.datasource.password` properties in `arume.yml`

### Requirement: Config-to-properties bridge
The system SHALL bridge values from `arume.yml` to Spring Boot as system properties before starting the context.

#### Scenario: System properties are set before Spring Boot starts
- **WHEN** `arume.yml` is read successfully (with URL decrypted if necessary)
- **THEN** the system SHALL set `spring.datasource.url` (decrypted plain text) and `spring.datasource.driver-class-name` as system properties before calling `SpringApplication.run()`, and SHALL NOT set `spring.datasource.username` or `spring.datasource.password`

#### Scenario: Encrypted URL is decrypted before setting system properties
- **WHEN** `arume.yml` contains an encrypted URL (prefixed with `ENC(`)
- **THEN** the system SHALL decrypt the URL using the filesystem-derived key and set the plain text URL as `spring.datasource.url` system property

### Requirement: Graceful failure when config is missing or invalid
The system SHALL handle missing or unreadable configuration gracefully.

#### Scenario: Config file is missing and wizard is cancelled
- **WHEN** the wizard is displayed and the user cancels
- **THEN** Spring Boot SHALL NOT start and the application SHALL terminate

#### Scenario: Config file exists but is invalid YAML
- **WHEN** `arume.yml` exists but cannot be parsed as valid YAML
- **THEN** the system SHALL display an error dialog and SHALL NOT start Spring Boot

#### Scenario: Encrypted URL cannot be decrypted
- **WHEN** `arume.yml` contains an encrypted URL that cannot be decrypted (wrong key due to filesystem change, corrupted data, or tampering)
- **THEN** the system SHALL display an error dialog offering the user the choice to reconfigure or exit, and SHALL NOT start Spring Boot

## ADDED Requirements

### Requirement: H2 database encryption control
The system SHALL enable H2 native AES file encryption when the `encrypt` configuration flag is true.

#### Scenario: H2 AES cipher enabled
- **WHEN** `arume.db.encrypt` is `true` and the JDBC URL is constructed
- **THEN** the URL SHALL include `;CIPHER=AES` parameter, causing H2 to encrypt the database files using the connection password as the encryption key

#### Scenario: H2 AES cipher disabled
- **WHEN** `arume.db.encrypt` is `false` and the JDBC URL is constructed
- **THEN** the URL SHALL NOT include `;CIPHER=AES` parameter, and the database files SHALL remain unencrypted
