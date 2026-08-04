## ADDED Requirements

### Requirement: Supported countries catalog
The system SHALL support a fixed catalog of seven countries, identified by their ISO-2 code in lowercase, each with an associated default official
language code (`es` or `en`).

#### Scenario: Country catalog is enumerated
- **WHEN** a programmatic enumeration of supported countries is requested
- **THEN** the system SHALL expose exactly the entries: `es` (Spanish), `gb` (English), `us` (English), `cl` (Spanish), `sg` (English),
  `au` (English), `za` (English)

#### Scenario: Each country maps to one official language
- **WHEN** the official language is queried for country `cl`
- **THEN** the system SHALL return `"es"` and NOT `"en"`
- **WHEN** the official language is queried for country `gb`
- **THEN** the system SHALL return `"en"` and NOT `"es"`

### Requirement: Country default detection
The system SHALL detect a default country at startup from the operating system locale, falling back to `Spain` when the detected country is not
supported.

#### Scenario: Detected country is supported
- **WHEN** the OS locale country is `CL` (Chile)
- **THEN** `Country.detectDefault()` SHALL return `cl`

#### Scenario: Detected country is not supported
- **WHEN** the OS locale country is `FR` (France) or any code not in the catalog
- **THEN** `Country.detectDefault()` SHALL return `es` (Spain) as fallback

#### Scenario: Detected country code is case-insensitive
- **WHEN** `Locale.getDefault().getCountry()` returns `"US"`
- **THEN** `Country.detectDefault()` SHALL return `us` (lowercased internally)

### Requirement: Country code resolution
The system SHALL resolve country codes into `Country` instances and SHALL fall back to `Spain` for unknown codes (including empty and null).

#### Scenario: Valid code resolves
- **WHEN** `Country.fromCode("gb")` is called
- **THEN** the system SHALL return the `gb` country instance

#### Scenario: Unknown code resolves to default
- **WHEN** `Country.fromCode("xx")` or `Country.fromCode(null)` is called
- **THEN** the system SHALL return the `es` country instance as fallback

### Requirement: Country internationalized label
Each country SHALL expose an i18n label key `wizard.country.<code>` whose value is the country name in the language of the active bundle.

#### Scenario: Label key format
- **WHEN** `country.getLabelKey()` is called for country `za`
- **THEN** the returned key SHALL be `"wizard.country.za"`

#### Scenario: Label resolved in English
- **WHEN** `I18nManager.getString("wizard.country.za")` is invoked while English is active
- **THEN** the value SHALL be `"South Africa"`

#### Scenario: Label resolved in Spanish
- **WHEN** `I18nManager.getString("wizard.country.za")` is invoked while Spanish is active
- **THEN** the value SHALL be `"Sudáfrica"`

### Requirement: Official-language-to-country mapping resource
The mapping between country code and its official language SHALL be encoded in the codebase (enum `Country`), not loaded from an external
configuration file.

#### Scenario: Country enum holds official language
- **WHEN** the `Country` enum is inspected
- **THEN** each constant SHALL carry a non-null `officialLanguage` field of value either `"es"` or `"en"`

### Requirement: Country persistence in arume.yml
The system SHALL persist the user-selected country under the key `arume.country` in `arume.yml` as a lowercase ISO-2 code.

#### Scenario: Country is saved with configuration
- **WHEN** the user completes the first-run wizard with country `Chile` and saves
- **THEN** `arume.yml` SHALL contain `arume.country: cl`

#### Scenario: Country is read on subsequent startup
- **WHEN** the application starts and `arume.yml` exists with `arume.country: us`
- **THEN** the `ArumeConfig` loaded SHALL have `country()` returning `"us"`

#### Scenario: Missing country falls back to Spain
- **WHEN** `arume.yml` exists but does NOT contain the `arume.country` key
- **THEN** `ConfigManager.load()` SHALL return an `ArumeConfig` whose `country()` is `"es"`

#### Scenario: Country survives language/theme updates
- **WHEN** `ConfigManager.updateLanguage("en")` is called on a config whose `country` is `sg`
- **THEN** the persisted `arume.yml` SHALL still contain `arume.country: sg`

### Requirement: Country flag indicator in main window title bar
The main application window SHALL display a non-interactive flag image of the country chosen at setup, positioned in the title bar to the left of the
language selector button, with an internationalized tooltip.

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
- **WHEN** the application starts with `arume.country: au`
- **THEN** the flag indicator SHALL display the flag PNG of Australia

### Requirement: Country flags PNG assets
The system SHALL ship a PNG file per supported country at `arume-ui/src/main/resources/icons/flags/<code>.png`, with `<code>` lowercase ISO-2.

#### Scenario: PNG exists for every supported country
- **WHEN** the resource bundle of `arume-ui` is inspected
- **THEN** PNG files SHALL exist for the codes `es`, `gb`, `us`, `cl`, `sg`, `au`, `za`

#### Scenario: PNG dimensions
- **WHEN** a flag PNG is loaded
- **THEN** its dimensions SHALL be 32 pixels wide by 20 pixels tall (the source; the `ImageView` SHALL preserve ratio when resized)