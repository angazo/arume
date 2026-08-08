## ADDED Requirements

### Requirement: National modules are identified by an alpha-2 jurisdiction

Each national module SHALL declare its jurisdiction as an ISO 3166-1 alpha-2 code, and capability resolution SHALL use that code.

#### Scenario: Spain module declares its jurisdiction

- **WHEN** the Spain module descriptor is read
- **THEN** its jurisdiction SHALL be `ES`

#### Scenario: Capability resolution uses alpha-2

- **WHEN** an application requests a capability for jurisdiction `ES`
- **THEN** the registry SHALL return the Spain module implementation

### Requirement: National modules contribute data to core catalogs

Core catalogs that are shared by all jurisdictions SHALL live in core tables, and a national module SHALL contribute its rows through its own migration instead of creating a parallel national table.

#### Scenario: Spain seeds the core legal form catalog

- **WHEN** the Spanish migration is applied on top of the core schema
- **THEN** the Spanish legal forms SHALL be inserted into the core legal form catalog and no national legal form table SHALL be created

#### Scenario: Core catalog is queried without national code

- **WHEN** the application reads a core catalog for a jurisdiction
- **THEN** it SHALL do so through core services only, without requiring code from the national module
