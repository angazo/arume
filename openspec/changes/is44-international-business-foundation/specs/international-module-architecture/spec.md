## Purpose

Establish a country-neutral core and independently versioned national modules so that fiscal capabilities, persistence and user interface contributions can grow without embedding one jurisdiction's legislation in the core.

## ADDED Requirements

### Requirement: Core remains independent of national modules

The core SHALL expose only country-neutral domain concepts and capability contracts. National-specific rules, tables and user interface behavior SHALL be supplied by national modules.

#### Scenario: Core is used without Spanish capabilities
- **WHEN** the core is loaded without the Spain module
- **THEN** core company and fiscal-year behavior SHALL remain available and SHALL not require Spanish-specific services or data

#### Scenario: Spain capability is supplied by its module
- **WHEN** the Spain module is loaded
- **THEN** Spanish series capabilities SHALL be registered without adding Spanish rules to core business operations

### Requirement: Fiscal capabilities are resolved through a registry

The system SHALL resolve a fiscal capability through a registry or factory associated with a jurisdiction and SHALL reject a request when no installed module provides the requested capability.

#### Scenario: Resolve an installed capability
- **WHEN** an application requests a supported capability for a jurisdiction with an installed module
- **THEN** the registry SHALL return that module's implementation

#### Scenario: Resolve a missing capability
- **WHEN** an application requests a capability for a jurisdiction or feature that is not installed
- **THEN** the registry SHALL return a controlled unavailable-capability result and SHALL not execute a different country's implementation

### Requirement: National modules declare core compatibility

Each national module SHALL declare the minimum core schema and contract version it requires. Application startup SHALL validate that requirement before enabling the module.

#### Scenario: Module is compatible with core
- **WHEN** a national module declares a supported minimum core version
- **THEN** the application SHALL enable the module after the core schema is available

#### Scenario: Module requires a newer core
- **WHEN** a national module requires a core version newer than the installed one
- **THEN** the application SHALL refuse to enable that module and SHALL report the compatibility problem

### Requirement: Core migrations precede national migrations

The migration process SHALL apply or validate the core schema before applying a national module schema. A national migration SHALL be allowed to reference core tables only after its declared core dependency is satisfied.

#### Scenario: Fresh database with Spain installed
- **WHEN** a fresh database is initialized with the core and Spain modules
- **THEN** the core schema SHALL be available before Spain tables and foreign keys are created

#### Scenario: National migration dependency is not satisfied
- **WHEN** a national migration requires a core schema version that is not available
- **THEN** the migration process SHALL stop before enabling that national module
