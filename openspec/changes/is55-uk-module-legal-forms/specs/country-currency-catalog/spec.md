## ADDED Requirements

### Requirement: Supported jurisdictions are derived from the country-currency catalog

The system SHALL be able to list, separately from the full countries catalog, the subset of countries whose national module is installed. A country SHALL be considered to have its national module installed when it has at least one association in `t4_country_currency`, because that association is contributed exclusively by the country's own module migration. That subset SHALL carry the same localized names, fallback to English and alphabetical ordering as the full countries catalog.

#### Scenario: Supported jurisdictions reflect the installed modules

- **WHEN** the application requests the supported jurisdictions after the core, Spanish and United Kingdom migrations have been executed
- **THEN** it SHALL return exactly Spain and the United Kingdom

#### Scenario: A country without a national module is not supported

- **WHEN** the application requests the supported jurisdictions
- **THEN** the countries `US`, `CL`, `SG`, `AU` and `ZA` SHALL NOT be returned, because no module has contributed their currency association

#### Scenario: Full countries catalog is unaffected

- **WHEN** the application requests the full countries catalog
- **THEN** it SHALL still return the seven catalog countries, regardless of which national modules are installed

#### Scenario: Supported jurisdictions are localized and sorted

- **WHEN** the application requests the supported jurisdictions while Spanish is the active language
- **THEN** the returned entries SHALL carry their Spanish names (`España`, `Reino Unido`) sorted alphabetically by that name

## MODIFIED Requirements

### Requirement: Seed data for supported countries and currencies

The core database migration SHALL insert the seven countries currently supported by the product, their names in every supported language and
their currencies, using the official ISO codes. Country-currency associations SHALL NOT be seeded by core: each national module SHALL insert
the association of its own jurisdiction from its own migration, so that the catalog only relates a country to a currency once that country
has a national module.

- Countries (`alpha2_code`, `alpha3_code`, `numeric_code`): `ES/ESP/724`, `GB/GBR/826`, `US/USA/840`, `CL/CHL/152`, `SG/SGP/702`,
  `AU/AUS/36`, `ZA/ZAF/710`.
- Country names in English: `Spain`, `United Kingdom`, `United States`, `Chile`, `Singapore`, `Australia`, `South Africa`.
- Country names in Spanish: `España`, `Reino Unido`, `Estados Unidos`, `Chile`, `Singapur`, `Australia`, `Sudáfrica`.
- Currencies (`numeric_code`, `alpha3_code`, `name`, `symbol`): `978/EUR/Euro/€`, `826/GBP/Pound Sterling/£`, `840/USD/US Dollar/$`,
  `152/CLP/Chilean Peso/$`, `990/CLF/Unidad de Fomento/UF`, `702/SGD/Singapore Dollar/$`, `36/AUD/Australian Dollar/$`, `710/ZAR/Rand/R`.
- Associations contributed by national modules: `ES↔EUR` by the Spanish module and `GB↔GBP` by the United Kingdom module.

#### Scenario: Countries seeded after migration
- **WHEN** the Flyway core migrations have been executed
- **THEN** `t1_countries` SHALL contain exactly the seven rows listed above with their ISO 3166-1 alpha-2, alpha-3 and numeric codes

#### Scenario: Country names seeded after migration
- **WHEN** the Flyway core migrations have been executed
- **THEN** `t2_country_names` SHALL contain exactly fourteen rows: the seven English names and the seven Spanish names listed above

#### Scenario: Currencies seeded after migration
- **WHEN** the Flyway core migrations have been executed
- **THEN** `t3_currencies` SHALL contain exactly the eight rows listed above with their ISO 4217 numeric code, alpha-3 code, English name
  and symbol

#### Scenario: Core seeds no country-currency association
- **WHEN** only the Flyway core migrations have been executed
- **THEN** `t4_country_currency` SHALL be empty

#### Scenario: Associations seeded after migration
- **WHEN** the core, Spanish and United Kingdom migrations have been executed
- **THEN** `t4_country_currency` SHALL contain exactly the two country-currency pairs contributed by the installed national modules,
  `ES↔EUR` and `GB↔GBP`

#### Scenario: A country without a national module has no currency association
- **WHEN** the core, Spanish and United Kingdom migrations have been executed
- **THEN** the countries `US`, `CL`, `SG`, `AU` and `ZA` SHALL have no rows in `t4_country_currency`
