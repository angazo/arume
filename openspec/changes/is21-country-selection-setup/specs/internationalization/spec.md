## MODIFIED Requirements

### Requirement: OS language detection
The system SHALL detect the operating system locale to determine the default country on first run, and SHALL derive the default UI language from
the official language of the detected country.

#### Scenario: Spanish-speaking supported country defaults to Spanish
- **WHEN** the OS locale country resolves to `es` or `cl`
- **THEN** `Country.detectDefault().officialLanguage()` SHALL return `"es"` and the wizard SHALL initialize `I18nManager` with Spanish

#### Scenario: English-speaking supported country defaults to English
- **WHEN** the OS locale country resolves to `gb`, `us`, `sg`, `au`, or `za`
- **THEN** `Country.detectDefault().officialLanguage()` SHALL return `"en"` and the wizard SHALL initialize `I18nManager` with English

#### Scenario: Unsupported country defaults to Spain and Spanish
- **WHEN** the OS locale country does NOT match any supported country
- **THEN** `Country.detectDefault()` SHALL return the `es` country and the wizard SHALL initialize `I18nManager` with Spanish

#### Scenario: Co-official Spanish languages with country Spain still default to Spanish
- **WHEN** the OS locale language is `ca`, `gl`, or `eu` AND the country is `ES`
- **THEN** `Country.detectDefault()` SHALL return `es` and the official-language default SHALL remain `"es"`

### Requirement: Language change from main application window
The system SHALL provide a language selector button in the custom title bar allowing the user to toggle between English and Spanish, displaying the
name of the currently active language as its label.

#### Scenario: Language button is present in title bar
- **WHEN** the main application window is displayed
- **THEN** a language selector button SHALL appear in the custom title bar, positioned between the country flag indicator and the theme button

#### Scenario: Language button shows the name of the active language
- **WHEN** the main window is displayed with Spanish active
- **THEN** the language button SHALL display the text "Español" (and no flag icon)

#### Scenario: Language button toggles between English and Spanish
- **WHEN** the user clicks the language button while Spanish is active
- **THEN** the language SHALL change to English, the button text SHALL update to "English", and all UI text SHALL update accordingly

#### Scenario: Language button toggles from English to Spanish
- **WHEN** the user clicks the language button while English is active
- **THEN** the language SHALL change to Spanish, the button text SHALL update to "Español", and all UI text SHALL update accordingly

#### Scenario: Language button label refreshes on language change
- **WHEN** the language is changed from any source (e.g., wizard, code)
- **THEN** the language button text SHALL be re-set to the name of the now-active language

### Requirement: Language persistence in arume.yml
The system SHALL persist the user's language choice in the `arume.yml` configuration file under the key `arume.language`, and updates to other
keys (theme, country) SHALL NOT alter the persisted language value.

#### Scenario: Language is saved with configuration
- **WHEN** the user completes the first-run wizard with language set to Spanish
- **THEN** `arume.yml` SHALL contain `arume.language: es`

#### Scenario: Language is read on subsequent startup
- **WHEN** the application starts and `arume.yml` exists with `arume.language: en`
- **THEN** `I18nManager` SHALL be initialized with English

#### Scenario: Language can be updated in-app
- **WHEN** the user changes language from the title bar button
- **THEN** `arume.yml` SHALL be updated with the new `arume.language` value and other persisted keys (theme, country) SHALL remain unchanged