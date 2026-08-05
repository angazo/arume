# MBG Code Generation

## ADDED Requirements

### Requirement: Generator runs against a migrated clean schema
The `mbGenerator` task SHALL generate MyBatis entities and mappers from the schema defined by the project's Flyway migrations, applied to a clean H2 in-memory database before generation.

#### Scenario: Fresh in-memory database is migrated before generation
- **WHEN** the `mbGenerator` task runs
- **THEN** the task SHALL apply all pending Flyway migrations from `classpath:db/migration` to a clean H2 in-memory database and SHALL run MyBatis Generator against that database

#### Scenario: Every run starts from an empty database
- **WHEN** the `mbGenerator` task runs more than once
- **THEN** each run SHALL generate against a newly created in-memory database with no leftover state from previous runs

### Requirement: Migrations live in the data layer
The Flyway migration scripts SHALL reside in `arume-db/src/main/resources/db/migration/` and SHALL be resolvable as `classpath:db/migration` by both the application bootstrap and the generator task.

#### Scenario: Application bootstrap resolves migrations
- **WHEN** the application starts
- **THEN** the Flyway bean SHALL execute the migrations from `classpath:db/migration` provided by the `arume-db` module

#### Scenario: Migration test resolves migrations
- **WHEN** the migration integration test runs
- **THEN** it SHALL apply `classpath:db/migration` and assert the seeded catalog data

### Requirement: Generator connection is defined in one place
The JDBC connection used by MyBatis Generator SHALL be defined as the `mbgen.url` property and SHALL be injected into `mbg.xml` by the task, together with the existing `projectDir` property.

#### Scenario: Configuration file uses injected connection
- **WHEN** the `mbGenerator` task parses `MyBatis/mbg.xml`
- **THEN** the `connectionURL` SHALL resolve `${mbgen.url}` to the in-memory database URL provided by the task

### Requirement: Task is incremental
The `mbGenerator` task SHALL declare its inputs so that Gradle re-executes it when the generation configuration or the migrations change.

#### Scenario: Task re-runs when migrations change
- **WHEN** a Flyway migration script changes
- **THEN** the `mbGenerator` task SHALL NOT be considered up-to-date and SHALL re-run

#### Scenario: Task skips when nothing changes
- **WHEN** neither the generation configuration nor the migrations change
- **THEN** the `mbGenerator` task MAY be considered up-to-date and skipped by Gradle
