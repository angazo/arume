## ADDED Requirements

### Requirement: Deferred Spring Boot startup
Spring Boot SHALL NOT start until database configuration is available.

#### Scenario: First run — Spring Boot starts after wizard
- **WHEN** the application is launched for the first time and the user completes the wizard
- **THEN** Spring Boot SHALL start AFTER the wizard saves the configuration

#### Scenario: Subsequent run — Spring Boot starts immediately
- **WHEN** the application is launched and `arume.yml` already exists
- **THEN** Spring Boot SHALL start with the configuration from `arume.yml` without showing the wizard

### Requirement: Datasource configuration from external config
The system SHALL configure the Spring Boot datasource using values from `arume.yml`.

#### Scenario: H2 datasource URL is constructed
- **WHEN** the configuration specifies `arume.db.type: h2` and a storage path
- **THEN** the system SHALL construct a JDBC URL in the format `jdbc:h2:file:<path>/arume;MODE=PostgreSQL;DB_CLOSE_DELAY=-1`

#### Scenario: Credentials are passed to datasource
- **WHEN** the configuration contains username and password
- **THEN** the system SHALL configure `spring.datasource.username` and `spring.datasource.password` with those values

### Requirement: Flyway integration
Flyway SHALL run automatically when Spring Boot starts with the configured datasource.

#### Scenario: Flyway creates schema on fresh database
- **WHEN** Spring Boot starts and the H2 database file does not exist or is empty
- **THEN** Flyway SHALL execute all pending migrations and create the schema

#### Scenario: Flyway skips already applied migrations
- **WHEN** Spring Boot starts and all migrations have already been applied
- **THEN** Flyway SHALL detect the current state and apply zero migrations

### Requirement: Config-to-properties bridge
The system SHALL bridge values from `arume.yml` to Spring Boot as system properties before starting the context.

#### Scenario: System properties are set before Spring Boot starts
- **WHEN** `arume.yml` is read successfully
- **THEN** the system SHALL set `spring.datasource.url`, `spring.datasource.driver-class-name`, `spring.datasource.username`, and `spring.datasource.password` as system properties before calling `SpringApplication.run()`

### Requirement: Graceful failure when config is missing or invalid
The system SHALL handle missing or unreadable configuration gracefully.

#### Scenario: Config file is missing and wizard is cancelled
- **WHEN** the wizard is displayed and the user cancels
- **THEN** Spring Boot SHALL NOT start and the application SHALL terminate

#### Scenario: Config file exists but is invalid YAML
- **WHEN** `arume.yml` exists but cannot be parsed as valid YAML
- **THEN** the system SHALL display an error dialog and SHALL NOT start Spring Boot
