# Country Currency Catalog

## Purpose

Store the master catalogs of supported countries (ISO 3166-1) and currencies (ISO 4217) in the application database, together with the
many-to-many relationship between countries and their available currencies. The catalogs are seeded with the countries currently supported
by the product and act as the authoritative data source for future invoicing and accounting features.

## Requirements

### Requirement: Countries catalog table
Countries catalog table
The system SHALL maintain a `t1_countries` table in the application database storing the supported countries according to ISO 3166-1, with
columns: `alpha2_code VARCHAR(2) NOT NULL` (the ISO 3166-1 alpha-2 code in canonical uppercase), `alpha3_code VARCHAR(3) NOT NULL` (the ISO
3166-1 alpha-3 code in canonical uppercase) and `numeric_code SMALLINT NOT NULL` (the ISO 3166-1 numeric code). The primary key `pk_t1` SHALL
be `alpha2_code`, and unique constraints `uk_t1_alpha3` and `uk_t1_numeric` SHALL enforce uniqueness of `alpha3_code` and `numeric_code`
respectively. The table SHALL NOT store country names; they are held in the localized country names table.

#### Scenario: Table exists after migrations are applied
- **WHEN** the Flyway migrations have been executed against the application database
- **THEN** the table `t1_countries` SHALL exist with the columns `alpha2_code`, `alpha3_code` and `numeric_code`, primary key `pk_t1` on
  `alpha2_code` and unique constraints `uk_t1_alpha3` and `uk_t1_numeric`

#### Scenario: Duplicate alpha-3 code is rejected
- **WHEN** an insert into `t1_countries` attempts to add a row whose `alpha3_code` already exists
- **THEN** the database SHALL reject the row violating `uk_t1_alpha3`

#### Scenario: No name column in the countries table
- **WHEN** the schema of `t1_countries` is inspected
- **THEN** it SHALL NOT contain a `name` column

### Requirement: Currencies catalog table
Currencies catalog table
The system SHALL maintain a `t3_currencies` table in the application database storing the available currencies according to ISO 4217, with
columns: `numeric_code SMALLINT NOT NULL` (the ISO 4217 numeric code), `alpha3_code VARCHAR(3) NOT NULL` (the ISO 4217 alpha-3 code in
canonical uppercase), `name VARCHAR(100) NOT NULL` (currency name in English) and `symbol VARCHAR(8) NOT NULL` (the currency symbol, e.g. `€`). The
primary key `pk_t3` SHALL be `numeric_code`, and a unique constraint `uk_t3_alpha3` SHALL enforce uniqueness of `alpha3_code`.

#### Scenario: Table exists after migrations are applied
- **WHEN** the Flyway migrations have been executed against the application database
- **THEN** the table `t3_currencies` SHALL exist with the columns `numeric_code`, `alpha3_code`, `name` and `symbol`, primary key `pk_t3`
  on `numeric_code` and unique constraint `uk_t3_alpha3` on `alpha3_code`

#### Scenario: Currency symbol is stored
- **WHEN** the currency with `alpha3_code` `EUR` is read from `t3_currencies`
- **THEN** its `symbol` SHALL be `€`

### Requirement: Country-currency association table
Country-currency association table
The system SHALL maintain a `t4_country_currency` table relating countries and currencies as many-to-many, with columns
`country_alpha2_code VARCHAR(2) NOT NULL` and `currency_numeric_code SMALLINT NOT NULL`. The primary key `pk_t4` SHALL be the composite of
both columns, `fk_t4_t1` SHALL reference `t1_countries(alpha2_code)` and `fk_t4_t3` SHALL reference `t3_currencies(numeric_code)`.

#### Scenario: Association table exists after migrations are applied
- **WHEN** the Flyway migrations have been executed against the application database
- **THEN** the table `t4_country_currency` SHALL exist with primary key `pk_t4` on (`country_alpha2_code`, `currency_numeric_code`) and
  foreign keys `fk_t4_t1` and `fk_t4_t3` to `t1_countries` and `t3_currencies` respectively

#### Scenario: Model supports shared currencies and multiple currencies per country
- **WHEN** the schema of `t4_country_currency` is inspected
- **THEN** nothing SHALL prevent the same `currency_numeric_code` from appearing in several rows, nor the same `country_alpha2_code` from
  being associated with several currencies

#### Scenario: Association must reference existing catalog entries
- **WHEN** an insert into `t4_country_currency` references a country alpha-2 code or a currency numeric code that does not exist
- **THEN** the database SHALL reject the row violating `fk_t4_t1` or `fk_t4_t3`

### Requirement: Seed data for supported countries and currencies
Seed data for supported countries and currencies
The database migration SHALL insert the seven countries currently supported by the product, their names in every supported language, their
currencies, and the country-currency associations, using the official ISO codes:

- Countries (`alpha2_code`, `alpha3_code`, `numeric_code`): `ES/ESP/724`, `GB/GBR/826`, `US/USA/840`, `CL/CHL/152`, `SG/SGP/702`,
  `AU/AUS/36`, `ZA/ZAF/710`.
- Country names in English: `Spain`, `United Kingdom`, `United States`, `Chile`, `Singapore`, `Australia`, `South Africa`.
- Country names in Spanish: `España`, `Reino Unido`, `Estados Unidos`, `Chile`, `Singapur`, `Australia`, `Sudáfrica`.
- Currencies (`numeric_code`, `alpha3_code`, `name`, `symbol`): `978/EUR/Euro/€`, `826/GBP/Pound Sterling/£`, `840/USD/US Dollar/$`,
  `152/CLP/Chilean Peso/$`, `990/CLF/Unidad de Fomento/UF`, `702/SGD/Singapore Dollar/$`, `36/AUD/Australian Dollar/$`, `710/ZAR/Rand/R`.
- Associations: `ES↔EUR`, `GB↔GBP`, `US↔USD`, `CL↔CLP`, `CL↔CLF`, `SG↔SGD`, `AU↔AUD`, `ZA↔ZAR`.

#### Scenario: Countries seeded after migration
- **WHEN** the Flyway migrations have been executed
- **THEN** `t1_countries` SHALL contain exactly the seven rows listed above with their ISO 3166-1 alpha-2, alpha-3 and numeric codes

#### Scenario: Country names seeded after migration
- **WHEN** the Flyway migrations have been executed
- **THEN** `t2_country_names` SHALL contain exactly fourteen rows: the seven English names and the seven Spanish names listed above

#### Scenario: Currencies seeded after migration
- **WHEN** the Flyway migrations have been executed
- **THEN** `t3_currencies` SHALL contain exactly the eight rows listed above with their ISO 4217 numeric code, alpha-3 code, English name
  and symbol

#### Scenario: Associations seeded after migration
- **WHEN** the Flyway migrations have been executed
- **THEN** `t4_country_currency` SHALL contain exactly the eight country-currency pairs listed above

### Requirement: Catalog names in English
Catalog names in English
The `name` column of `t3_currencies` SHALL store values in English. Country names SHALL be stored per language in `t2_country_names`, whose
English row is the fallback used when a translation is missing.

#### Scenario: Country name stored in English
- **WHEN** the name of the country `ES` is read from `t2_country_names` for language `en`
- **THEN** its value SHALL be `Spain`

#### Scenario: Currency name stored in English
- **WHEN** the currency with `alpha3_code` `GBP` is read from `t3_currencies`
- **THEN** its `name` SHALL be `Pound Sterling`

### Requirement: Localized country names table
Localized country names table

The system SHALL maintain a `t2_country_names` table storing the name of every country in every supported language, with columns `country_alpha2_code VARCHAR(2) NOT NULL`, `language_code VARCHAR(2) NOT NULL` and `name VARCHAR(100) NOT NULL`. The primary key `pk_t2` SHALL be the composite of `country_alpha2_code` and `language_code`, `fk_t2_t1` SHALL reference `t1_countries(alpha2_code)` and `fk_t2_t0` SHALL reference `t0_i18n(language_code)`.

#### Scenario: Table exists after migrations are applied

- **WHEN** the Flyway core migrations have been executed against the application database
- **THEN** the table `t2_country_names` SHALL exist with primary key `pk_t2` on (`country_alpha2_code`, `language_code`) and foreign keys `fk_t2_t1` and `fk_t2_t0`

#### Scenario: Every country has a name in every supported language

- **WHEN** the Flyway core migrations have been executed
- **THEN** `t2_country_names` SHALL contain one row for each combination of a country in `t1_countries` and a language in `t0_i18n`

#### Scenario: Country names are seeded in English and Spanish

- **WHEN** the name of the country `ES` is read for language `en` and for language `es`
- **THEN** the values SHALL be `Spain` and `España` respectively

#### Scenario: Name must reference an existing country

- **WHEN** an insert into `t2_country_names` references a country alpha-2 code that does not exist
- **THEN** the database SHALL reject the row violating `fk_t2_t1`

### Requirement: Country flag resources are keyed by alpha-2
Country flag resources are keyed by alpha-2

Every country in the catalog SHALL have a flag image resource named after its alpha-2 code in lowercase (`icons/flags/<alpha2>.png`), so that the resource is derived directly from the catalog key. Each image SHALL be 96x72 pixels with a 4:3 landscape aspect ratio.

#### Scenario: Flag resource exists for every catalog country

- **WHEN** the application is built
- **THEN** a PNG resource SHALL exist at `icons/flags/<alpha2>.png` for each of the seven catalog countries (`es`, `gb`, `us`, `cl`, `sg`, `au`, `za`)

#### Scenario: No flag resource is keyed by alpha-3

- **WHEN** the flags resource directory is inspected
- **THEN** it SHALL NOT contain files named after alpha-3 codes (e.g. `esp.png`)

#### Scenario: Flag resources keep their resolution

- **WHEN** a flag PNG is read
- **THEN** it SHALL be 96 pixels wide and 72 pixels high

### Requirement: Countries are listed with localized names
Countries are listed with localized names

The system SHALL provide the list of catalog countries with their name resolved in the active interface language, ordered alphabetically by that name.

#### Scenario: Country list in Spanish

- **WHEN** the application requests the countries catalog while Spanish is the active language
- **THEN** the returned entries SHALL carry the Spanish names (e.g. `Reino Unido` for `GB`) sorted alphabetically

#### Scenario: Country list in English

- **WHEN** the application requests the countries catalog while English is the active language
- **THEN** the returned entries SHALL carry the English names (e.g. `United Kingdom` for `GB`) sorted alphabetically
