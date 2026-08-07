# spain-invoice-series Specification

## Purpose

Provide the Spanish configuration of invoice series while keeping Spanish numbering rules outside the international core and preserving the state of each series for every fiscal year.

## Requirements

### Requirement: Spanish invoice series belongs to a company

The Spain module SHALL create invoice series associated with exactly one company. A series code SHALL be unique within that company.

#### Scenario: Create a Spanish invoice series
- **WHEN** valid series data is submitted for an existing company
- **THEN** the Spain module SHALL create the series with its code, description and active status

#### Scenario: Duplicate series code in one company
- **WHEN** a user submits a series code already used by the same company
- **THEN** the Spain module SHALL reject the operation

#### Scenario: Same series code in different companies
- **WHEN** two companies create series with the same code
- **THEN** the Spain module SHALL allow both series

### Requirement: Spanish series has fiscal-year state

The Spain module SHALL associate a series with an explicit fiscal-year sequence state. The state SHALL record the numbering mode, active status and last assigned number for that company, series and fiscal year.

#### Scenario: Configure a continuing sequence
- **WHEN** a series is configured to continue numbering into a new fiscal year
- **THEN** the new fiscal-year state SHALL retain the continuation rule and the starting state needed to continue the sequence

#### Scenario: Configure a reset sequence
- **WHEN** a series is configured to restart numbering in a new fiscal year
- **THEN** the new fiscal-year state SHALL record the reset rule independently from the previous fiscal year

#### Scenario: Query historical series state
- **WHEN** a user or audit operation requests a series for a previous fiscal year
- **THEN** the system SHALL return the configuration and last known sequence state for that fiscal year without replacing it with the current state

### Requirement: Series state does not replace invoice history

The Spain module SHALL treat the last assigned number as sequence state and SHALL preserve the future ability to reconstruct assigned invoice numbers from immutable invoice or allocation records.

#### Scenario: Read current sequence state
- **WHEN** a sequence state is requested for the current fiscal year
- **THEN** the system SHALL return the last known assigned number and numbering mode

#### Scenario: No invoice emission in this capability
- **WHEN** the Spain invoice-series capability is used before invoice issuance is implemented
- **THEN** it SHALL configure and report series state without issuing a production invoice number
