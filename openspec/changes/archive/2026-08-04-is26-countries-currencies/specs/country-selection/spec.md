## MODIFIED Requirements

### Requirement: Supported countries catalog
The system SHALL support a fixed catalog of seven countries, identified by their ISO 3166-1 alpha-3 code in lowercase, each with an
associated default official language code (`es` or `en`).

#### Scenario: Country catalog is enumerated
- **WHEN** a programmatic enumeration of supported countries is requested
- **THEN** the system SHALL expose exactly the entries: `esp` (Spanish), `gbr` (English), `usa` (English), `chl` (Spanish), `sgp`
  (English), `aus` (English), `zaf` (English)

#### Scenario: Each country maps to one official language
- **WHEN** the official language is queried for country `chl`
- **THEN** the system SHALL return `"es"` and NOT `"en"`
- **WHEN** the official language is queried for country `gbr`
- **THEN** the system SHALL return `"en"` and NOT `"es"`

### Requirement: Country default detection
The system SHALL detect a default country at startup from the operating system locale, mapping the locale's ISO 3166-1 alpha-2 code to the
alpha-3 catalog internally, falling back to `Spain` when the detected country is not supported.

#### Scenario: Detected country is supported
- **WHEN** the OS locale country is `CL` (Chile)
- **THEN** `Country.detectDefault()` SHALL return `chl`

#### Scenario: Detected country is not supported
- **WHEN** the OS locale country is `FR` (France) or any code not in the catalog
- **THEN** `Country.detectDefault()` SHALL return `esp` (Spain) as fallback

#### Scenario: Detected country code is case-insensitive
- **WHEN** `Locale.getDefault().getCountry()` returns `"US"`
- **THEN** `Country.detectDefault()` SHALL return `usa` (lowercased internally)

### Requirement: Country code resolution
The system SHALL resolve ISO 3166-1 alpha-3 country codes into `Country` instances and SHALL fall back to `Spain` for unknown codes
(including empty, null and legacy ISO-2 codes).

#### Scenario: Valid code resolves
- **WHEN** `Country.fromCode("gbr")` is called
- **THEN** the system SHALL return the `gbr` country instance

#### Scenario: Unknown code resolves to default
- **WHEN** `Country.fromCode("xxx")` or `Country.fromCode(null)` is called
- **THEN** the system SHALL return the `esp` country instance as fallback

#### Scenario: Legacy ISO-2 code no longer resolves
- **WHEN** `Country.fromCode("gb")` is called
- **THEN** the system SHALL return the `esp` country instance as fallback

### Requirement: Country internationalized label
Each country SHALL expose an i18n label key `wizard.country.<code>` where `<code>` is the lowercase ISO 3166-1 alpha-3 code, whose value
is the country name in the language of the active bundle.

#### Scenario: Label key format
- **WHEN** `country.getLabelKey()` is called for country `zaf`
- **THEN** the returned key SHALL be `"wizard.country.zaf"`

#### Scenario: Label resolved in English
- **WHEN** `I18nManager.getString("wizard.country.zaf")` is invoked while English is active
- **THEN** the value SHALL be `"South Africa"`

#### Scenario: Label resolved in Spanish
- **WHEN** `I18nManager.getString("wizard.country.zaf")` is invoked while Spanish is active
- **THEN** the value SHALL be `"Sudáfrica"`

### Requirement: Country persistence in arume.yml
The system SHALL persist the user-selected country under the key `arume.country` in `arume.yml` as a lowercase ISO 3166-1 alpha-3 code.
Legacy ISO-2 values are NOT supported: an unrecognized value SHALL fall back to Spain.

#### Scenario: Country is saved with configuration
- **WHEN** the user completes the first-run wizard with country `Chile` and saves
- **THEN** `arume.yml` SHALL contain `arume.country: chl`

#### Scenario: Country is read on subsequent startup
- **WHEN** the application starts and `arume.yml` exists with `arume.country: usa`
- **THEN** the `ArumeConfig` loaded SHALL have `country()` returning `"usa"`

#### Scenario: Missing country falls back to Spain
- **WHEN** `arume.yml` exists but does NOT contain the `arume.country` key
- **THEN** `ConfigManager.load()` SHALL return an `ArumeConfig` whose `country()` is `"esp"`

#### Scenario: Legacy ISO-2 value falls back to Spain
- **WHEN** `arume.yml` exists with `arume.country: cl` (legacy ISO-2)
- **THEN** the resolved country SHALL be `esp` (Spain)

#### Scenario: Country survives language/theme updates
- **WHEN** `ConfigManager.updateLanguage("en")` is called on a config whose `country` is `sgp`
- **THEN** the persisted `arume.yml` SHALL still contain `arume.country: sgp`

### Requirement: Country flag indicator in main window title bar
The main application window SHALL display a non-interactive flag image of the country chosen at setup, positioned in the title bar to the
left of the language selector button, with an internationalized tooltip.

#### Scenario: Flag indicator is visible
- **WHEN** the main window is displayed with country `Chile` configured
- **THEN** a 32×20 pixel PNG image of the flag of Chile SHALL be visible in the title bar before the language button

#### Scenario: Flag indicator is non-interactive
- **WHEN** the user clicks on the country flag indicator
- **THEN** no action SHALL be triggered and the indicator SHALL NOT respond to mouse events

#### Scenario: Flag indicator has internationalized tooltip
- **WHEN** the user hovers the mouse over the country flag indicator while Spanish is active
- **THEN** a tooltip SHALL appear with the text `"País: Chile"`

#### Scenario: Flag indicator has internationalized tooltip in English
- **WHEN** the user hovers the mouse over the country flag indicator while English is active
- **THEN** a tooltip SHALL appear with the text `"Country: Chile"`

#### Scenario: Flag indicator reflects loaded country
- **WHEN** the application starts with `arume.country: aus`
- **THEN** the flag indicator SHALL display the flag PNG of Australia

### Requirement: Country flags PNG assets
The system SHALL ship a PNG file per supported country at `arume-ui/src/main/resources/icons/flags/<code>.png`, with `<code>` the lowercase
ISO 3166-1 alpha-3 code.

#### Scenario: PNG exists for every supported country
- **WHEN** the resource bundle of `arume-ui` is inspected
- **THEN** PNG files SHALL exist for the codes `esp`, `gbr`, `usa`, `chl`, `sgp`, `aus`, `zaf`

#### Scenario: PNG dimensions
- **WHEN** a flag PNG is loaded
- **THEN** its dimensions SHALL be 32 pixels wide by 20 pixels tall (the source; the `ImageView` SHALL preserve ratio when resized)
