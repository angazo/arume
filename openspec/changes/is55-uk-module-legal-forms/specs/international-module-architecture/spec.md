## ADDED Requirements

### Requirement: Several national modules coexist

The system SHALL support more than one national module installed at the same time. Each national module SHALL keep its own migration history, independent from core's and from every other national module's. The migration of one national module SHALL NOT depend on another national module having been applied, and a failure in one national module SHALL NOT prevent the others from having been applied correctly.

#### Scenario: Fresh database with Spain and the United Kingdom installed

- **WHEN** a fresh database is initialized with the core, Spain and United Kingdom modules
- **THEN** the core schema SHALL be applied first and each national module SHALL record its own migration history in its own history table

#### Scenario: National modules do not depend on each other

- **WHEN** a national module migration is applied
- **THEN** it SHALL only require the core schema version it declares, and SHALL NOT require any other national module to be installed

#### Scenario: Registry keeps jurisdictions separate

- **WHEN** a capability is requested for a jurisdiction whose module does not provide it
- **THEN** the registry SHALL return a controlled unavailable-capability result and SHALL NOT return another jurisdiction's implementation

#### Scenario: Each installed jurisdiction is resolvable

- **WHEN** the installed national modules declare the jurisdictions `ES` and `GB`
- **THEN** the registry SHALL resolve each jurisdiction to its own module and SHALL reject a duplicate registration for the same jurisdiction

## MODIFIED Requirements

### Requirement: National modules contribute data to core catalogs

Core catalogs that are shared by all jurisdictions SHALL live in core tables, and a national module SHALL contribute its rows through its own migration instead of creating a parallel national table. A national module SHALL be allowed to consist only of such data contributions: owning tables of its own SHALL NOT be a condition for a jurisdiction to be supported.

#### Scenario: Spain seeds the core legal form catalog

- **WHEN** the Spanish migration is applied on top of the core schema
- **THEN** the Spanish legal forms SHALL be inserted into the core legal form catalog and no national legal form table SHALL be created

#### Scenario: A national module contributes data without owning tables

- **WHEN** the United Kingdom migration is applied on top of the core schema
- **THEN** it SHALL only insert rows into core catalogs, SHALL NOT create any table of its own, and the module SHALL still be enabled and resolvable by its jurisdiction

#### Scenario: Core catalog is queried without national code

- **WHEN** the application reads a core catalog for a jurisdiction
- **THEN** it SHALL do so through core services only, without requiring code from the national module
