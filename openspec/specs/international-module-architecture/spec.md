# international-module-architecture Specification

## Purpose

Establish a country-neutral core and independently versioned national modules so that fiscal capabilities, persistence and user interface contributions can grow without embedding one jurisdiction's legislation in the core.

## Requirements

### Requirement: Core remains independent of national modules
Core remains independent of national modules

The core SHALL expose only country-neutral domain concepts and capability contracts. National-specific rules, tables and user interface behavior SHALL be supplied by national modules.

#### Scenario: Core is used without Spanish capabilities
- **WHEN** the core is loaded without the Spain module
- **THEN** core company and fiscal-year behavior SHALL remain available and SHALL not require Spanish-specific services or data

#### Scenario: Spain capability is supplied by its module
- **WHEN** the Spain module is loaded
- **THEN** Spanish series capabilities SHALL be registered without adding Spanish rules to core business operations

### Requirement: Fiscal capabilities are resolved through a registry
Fiscal capabilities are resolved through a registry

The system SHALL resolve a fiscal capability through a registry or factory associated with a jurisdiction and SHALL reject a request when no installed module provides the requested capability.

#### Scenario: Resolve an installed capability
- **WHEN** an application requests a supported capability for a jurisdiction with an installed module
- **THEN** the registry SHALL return that module's implementation

#### Scenario: Resolve a missing capability
- **WHEN** an application requests a capability for a jurisdiction or feature that is not installed
- **THEN** the registry SHALL return a controlled unavailable-capability result and SHALL not execute a different country's implementation

### Requirement: National modules declare core compatibility
National modules declare core compatibility

Each national module SHALL declare the minimum core schema and contract version it requires. Application startup SHALL validate that requirement before enabling the module.

#### Scenario: Module is compatible with core
- **WHEN** a national module declares a supported minimum core version
- **THEN** the application SHALL enable the module after the core schema is available

#### Scenario: Module requires a newer core
- **WHEN** a national module requires a core version newer than the installed one
- **THEN** the application SHALL refuse to enable that module and SHALL report the compatibility problem

### Requirement: Core migrations precede national migrations
Core migrations precede national migrations

The migration process SHALL apply or validate the core schema before applying a national module schema. A national migration SHALL be allowed to reference core tables only after its declared core dependency is satisfied.

#### Scenario: Fresh database with Spain installed
- **WHEN** a fresh database is initialized with the core and Spain modules
- **THEN** the core schema SHALL be available before Spain tables and foreign keys are created

#### Scenario: National migration dependency is not satisfied
- **WHEN** a national migration requires a core schema version that is not available
- **THEN** the migration process SHALL stop before enabling that national module

### Requirement: National modules are identified by an alpha-2 jurisdiction
National modules are identified by an alpha-2 jurisdiction

Each national module SHALL declare its jurisdiction as an ISO 3166-1 alpha-2 code, and capability resolution SHALL use that code.

#### Scenario: Spain module declares its jurisdiction

- **WHEN** the Spain module descriptor is read
- **THEN** its jurisdiction SHALL be `ES`

#### Scenario: Capability resolution uses alpha-2

- **WHEN** an application requests a capability for jurisdiction `ES`
- **THEN** the registry SHALL return the Spain module implementation

### Requirement: National modules contribute data to core catalogs
National modules contribute data to core catalogs

Core catalogs that are shared by all jurisdictions SHALL live in core tables, and a national module SHALL contribute its rows through its own migration instead of creating a parallel national table.

#### Scenario: Spain seeds the core legal form catalog

- **WHEN** the Spanish migration is applied on top of the core schema
- **THEN** the Spanish legal forms SHALL be inserted into the core legal form catalog and no national legal form table SHALL be created

#### Scenario: Core catalog is queried without national code

- **WHEN** the application reads a core catalog for a jurisdiction
- **THEN** it SHALL do so through core services only, without requiring code from the national module
