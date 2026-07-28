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
The system SHALL detect the operating system locale to determine the default language on first run.

#### Scenario: Spanish locale defaults to Spanish
- **WHEN** the OS locale language is `es`
- **THEN** `I18nManager.detectDefaultLanguage()` SHALL return `es`

#### Scenario: Co-official Spanish languages with country Spain default to Spanish
- **WHEN** the OS locale language is `ca`, `gl`, or `eu` AND the country is `ES`
- **THEN** `I18nManager.detectDefaultLanguage()` SHALL return `es`

#### Scenario: Unsupported locale defaults to English
- **WHEN** the OS locale does not match any supported language (e.g., `fr`, `de`, `ja`)
- **THEN** `I18nManager.detectDefaultLanguage()` SHALL return `en`

#### Scenario: Co-official language outside Spain defaults to English
- **WHEN** the OS locale language is `ca` AND the country is NOT `ES` (e.g., `AD`, `FR`)
- **THEN** `I18nManager.detectDefaultLanguage()` SHALL return `en`

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
The system SHALL persist the user's language choice in the `arume.yml` configuration file under the key `arume.language`.

#### Scenario: Language is saved with configuration
- **WHEN** the user completes the first-run wizard with language set to Spanish
- **THEN** `arume.yml` SHALL contain `arume.language: es`

#### Scenario: Language is read on subsequent startup
- **WHEN** the application starts and `arume.yml` exists with `arume.language: en`
- **THEN** `I18nManager` SHALL be initialized with English

#### Scenario: Language can be updated in-app
- **WHEN** the user changes language from the main window menu
- **THEN** `arume.yml` SHALL be updated with the new `arume.language` value

### Requirement: Language change from main application window
The system SHALL provide a menu in the main application window allowing the user to change the language at any time.

#### Scenario: Language menu is present in main window
- **WHEN** the main application window is displayed
- **THEN** a menu bar SHALL contain a language menu with "English" and "Español" options using radio-style selection

#### Scenario: Current language is reflected in the menu
- **WHEN** the main window is displayed with Spanish active
- **THEN** the "Español" menu item SHALL be selected in the language menu

#### Scenario: Changing language from menu updates the UI
- **WHEN** the user selects "English" from the language menu
- **THEN** all UI text in the main window SHALL update to English and the menu labels SHALL reflect the new language
