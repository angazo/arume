# Internationalization

## Purpose

Provide multi-language support for the application UI, allowing users to choose between English and Spanish with automatic OS language detection and runtime language switching.

## Requirements

### Requirement: I18n Manager singleton
The system SHALL provide an `I18nManager` class that manages the current language and provides translated strings from resource bundles.

#### Scenario: Manager initializes with a language
- **WHEN** `I18nManager.init("es")` is called
- **THEN** the manager SHALL load the resource bundle for Spanish (`messages_es.properties`) and `getString()` SHALL return Spanish translations

#### Scenario: Manager falls back to English for missing locale
- **WHEN** `I18nManager.init("xx")` is called with an unsupported language
- **THEN** the manager SHALL fall back to the default bundle (`messages.properties`, English) via Java's `ResourceBundle` fallback mechanism

#### Scenario: getString returns translated text
- **WHEN** `I18nManager.getString("wizard.save")` is called while Spanish is active
- **THEN** the method SHALL return "Guardar"

#### Scenario: getString handles missing key gracefully
- **WHEN** `I18nManager.getString("nonexistent.key")` is called and the key does not exist in any bundle
- **THEN** the method SHALL return "!nonexistent.key!" as a visible indicator of the missing key

### Requirement: Language change with listener notification
The system SHALL allow changing the active language at runtime and SHALL notify registered listeners.

#### Scenario: Language changes and listeners are notified
- **WHEN** `I18nManager.setLanguage("en")` is called while Spanish is active
- **THEN** the active language SHALL change to English, the resource bundle SHALL be reloaded, and all registered listeners SHALL be invoked

#### Scenario: Setting same language does not trigger reload
- **WHEN** `I18nManager.setLanguage("en")` is called while English is already active
- **THEN** the bundle SHALL NOT be reloaded and listeners SHALL NOT be invoked

#### Scenario: Controller registers for language change
- **WHEN** a controller calls `I18nManager.onLanguageChange(callback)`
- **THEN** the callback SHALL be invoked on every subsequent language change

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

### Requirement: Resource bundle files
The system SHALL provide resource bundle files for each supported language.

#### Scenario: English bundle exists
- **WHEN** the application is built
- **THEN** `i18n/messages.properties` SHALL exist as the default (English) bundle in `arume-ui/src/main/resources/`

#### Scenario: Spanish bundle exists
- **WHEN** the application is built
- **THEN** `i18n/messages_es.properties` SHALL exist in `arume-ui/src/main/resources/i18n/`

#### Scenario: All keys exist in both bundles
- **WHEN** the Spanish bundle is loaded
- **THEN** every key present in the English bundle SHALL also exist in the Spanish bundle

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
