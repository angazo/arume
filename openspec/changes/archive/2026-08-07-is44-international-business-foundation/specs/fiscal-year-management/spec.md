## Purpose

Define fiscal years as explicit, company-owned periods that work with calendar years, non-calendar years and short initial or closing periods.

## ADDED Requirements

### Requirement: Fiscal year belongs to a company

The system SHALL create every fiscal year in association with exactly one company and SHALL store explicit start and end dates, a status and a user-facing label or short name.

#### Scenario: Create a fiscal year with explicit dates
- **WHEN** a valid fiscal year is created for an existing company
- **THEN** the system SHALL store its company, start date, end date, status and label

#### Scenario: Create a short fiscal year
- **WHEN** a fiscal year starts and ends within the same calendar year or covers a period shorter than twelve months
- **THEN** the system SHALL accept it when its dates satisfy the period rules

### Requirement: Fiscal years have valid non-overlapping periods

The system SHALL reject a fiscal year whose end date precedes its start date and SHALL reject a period that overlaps another fiscal year of the same company.

#### Scenario: End date precedes start date
- **WHEN** a user submits a fiscal year whose end date is before its start date
- **THEN** the system SHALL reject the fiscal year and SHALL not persist it

#### Scenario: Period overlaps an existing fiscal year
- **WHEN** a user submits a fiscal year whose period overlaps an existing period of the same company
- **THEN** the system SHALL reject the fiscal year and SHALL identify the period conflict

#### Scenario: Same dates for different companies
- **WHEN** a user creates fiscal years with equal dates for two different companies
- **THEN** the system SHALL allow both fiscal years

### Requirement: Fiscal year status is explicit

The system SHALL expose the status of each fiscal year and SHALL distinguish at least an open period from a closed period. The system SHALL prevent normal business operations from treating a closed period as open.

#### Scenario: Read a closed fiscal year
- **WHEN** a fiscal year has been closed and is requested by a business operation
- **THEN** the operation SHALL receive its closed status and SHALL not treat it as an open period
