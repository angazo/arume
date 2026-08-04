# Country Currency Catalog

## Purpose

Store the master catalogs of supported countries (ISO 3166-1) and currencies (ISO 4217) in the application database, together with the
many-to-many relationship between countries and their available currencies. The catalogs are seeded with the countries currently supported
by the product and act as the authoritative data source for future invoicing and accounting features.

## Requirements

### Requirement: Countries catalog table
The system SHALL maintain a `t1_countries` table in the application database storing the supported countries according to ISO 3166-1, with
columns: `numeric_code SMALLINT NOT NULL` (the ISO 3166-1 numeric code), `alpha3_code VARCHAR(3) NOT NULL` (the ISO 3166-1 alpha-3 code in
canonical uppercase) and `name VARCHAR(100) NOT NULL` (country name in English). The primary key `pk_t1` SHALL be `numeric_code`, and a unique
constraint `uk_t1_alpha3` SHALL enforce uniqueness of `alpha3_code`.

#### Scenario: Table exists after migrations are applied
- **WHEN** the Flyway migrations have been executed against the application database
- **THEN** the table `t1_countries` SHALL exist with the columns `numeric_code`, `alpha3_code` and `name`, primary key `pk_t1` on
  `numeric_code` and unique constraint `uk_t1_alpha3` on `alpha3_code`

#### Scenario: Duplicate alpha-3 code is rejected
- **WHEN** an insert into `t1_countries` attempts to add a row whose `alpha3_code` already exists
- **THEN** the database SHALL reject the row violating `uk_t1_alpha3`

### Requirement: Currencies catalog table
The system SHALL maintain a `t2_currencies` table in the application database storing the available currencies according to ISO 4217, with
columns: `numeric_code SMALLINT NOT NULL` (the ISO 4217 numeric code), `alpha3_code VARCHAR(3) NOT NULL` (the ISO 4217 alpha-3 code in
canonical uppercase), `name VARCHAR(100) NOT NULL` (currency name in English) and `symbol VARCHAR(8) NOT NULL` (the currency symbol, e.g. `€`). The
primary key `pk_t2` SHALL be `numeric_code`, and a unique constraint `uk_t2_alpha3` SHALL enforce uniqueness of `alpha3_code`.

#### Scenario: Table exists after migrations are applied
- **WHEN** the Flyway migrations have been executed against the application database
- **THEN** the table `t2_currencies` SHALL exist with the columns `numeric_code`, `alpha3_code`, `name` and `symbol`, primary key `pk_t2`
  on `numeric_code` and unique constraint `uk_t2_alpha3` on `alpha3_code`

#### Scenario: Currency symbol is stored
- **WHEN** the currency with `alpha3_code` `EUR` is read from `t2_currencies`
- **THEN** its `symbol` SHALL be `€`

### Requirement: Country-currency association table
The system SHALL maintain a `t3_country_currency` table relating countries and currencies as many-to-many, with columns
`country_numeric_code SMALLINT NOT NULL` and `currency_numeric_code SMALLINT NOT NULL`. The primary key `pk_t3` SHALL be the composite of
both columns, `fk_t3_t1` SHALL reference `t1_countries(numeric_code)` and `fk_t3_t2` SHALL reference `t2_currencies(numeric_code)`.

#### Scenario: Association table exists after migrations are applied
- **WHEN** the Flyway migrations have been executed against the application database
- **THEN** the table `t3_country_currency` SHALL exist with primary key `pk_t3` on (`country_numeric_code`, `currency_numeric_code`) and
  foreign keys `fk_t3_t1` and `fk_t3_t2` to `t1_countries` and `t2_currencies` respectively

#### Scenario: Model supports shared currencies and multiple currencies per country
- **WHEN** the schema of `t3_country_currency` is inspected
- **THEN** nothing SHALL prevent the same `currency_numeric_code` from appearing in several rows, nor the same `country_numeric_code` from
  being associated with several currencies

#### Scenario: Association must reference existing catalog entries
- **WHEN** an insert into `t3_country_currency` references a country or currency numeric code that does not exist
- **THEN** the database SHALL reject the row violating `fk_t3_t1` or `fk_t3_t2`

### Requirement: Seed data for supported countries and currencies
The database migration SHALL insert the seven countries currently supported by the product, their currencies, and the country-currency
associations, using the official ISO codes:

- Countries (`numeric_code`, `alpha3_code`, `name`): `724/ESP/Spain`, `826/GBR/United Kingdom`, `840/USA/United States`, `152/CHL/Chile`,
  `702/SGP/Singapore`, `36/AUS/Australia`, `710/ZAF/South Africa`.
- Currencies (`numeric_code`, `alpha3_code`, `name`, `symbol`): `978/EUR/Euro/€`, `826/GBP/Pound Sterling/£`, `840/USD/US Dollar/$`,
  `152/CLP/Chilean Peso/$`, `990/CLF/Unidad de Fomento/UF`, `702/SGD/Singapore Dollar/$`, `36/AUD/Australian Dollar/$`, `710/ZAR/Rand/R`.
- Associations: `ESP↔EUR`, `GBR↔GBP`, `USA↔USD`, `CHL↔CLP`, `CHL↔CLF`, `SGP↔SGD`, `AUS↔AUD`, `ZAF↔ZAR`.

#### Scenario: Countries seeded after migration
- **WHEN** the Flyway migrations have been executed
- **THEN** `t1_countries` SHALL contain exactly the seven rows listed above with their ISO 3166-1 numeric and alpha-3 codes

#### Scenario: Currencies seeded after migration
- **WHEN** the Flyway migrations have been executed
- **THEN** `t2_currencies` SHALL contain exactly the eight rows listed above with their ISO 4217 numeric code, alpha-3 code, English name
  and symbol

#### Scenario: Associations seeded after migration
- **WHEN** the Flyway migrations have been executed
- **THEN** `t3_country_currency` SHALL contain exactly the eight country-currency pairs listed above

### Requirement: Catalog names in English
The `name` columns of `t1_countries` and `t2_currencies` SHALL store values in English; translated display names remain the responsibility
of the UI internationalization layer.

#### Scenario: Country name stored in English
- **WHEN** the country with `alpha3_code` `ESP` is read from `t1_countries`
- **THEN** its `name` SHALL be `Spain`

#### Scenario: Currency name stored in English
- **WHEN** the currency with `alpha3_code` `GBP` is read from `t2_currencies`
- **THEN** its `name` SHALL be `Pound Sterling`
