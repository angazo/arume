## MODIFIED Requirements

### Requirement: Language change from main application window
The system SHALL provide a language selector button in the custom title bar allowing the user to toggle between English and Spanish.

#### Scenario: Language button is present in title bar
- **WHEN** the main application window is displayed
- **THEN** a language button with a flag icon SHALL appear in the custom title bar, positioned before the theme button

#### Scenario: Language button shows flag for current language
- **WHEN** the main window is displayed with English active
- **THEN** the language button SHALL display a flag icon representing English (e.g., US/UK flag FontIcon)

#### Scenario: Language button toggles between English and Spanish
- **WHEN** the user clicks the language button while Spanish is active
- **THEN** the language SHALL change to English, the button icon SHALL update to the English flag, and all UI text SHALL update

#### Scenario: Language button toggles from English to Spanish
- **WHEN** the user clicks the language button while English is active
- **THEN** the language SHALL change to Spanish, the button icon SHALL update to the Spanish flag, and all UI text SHALL update

#### Scenario: Language change from button updates the UI
- **WHEN** the user changes language via the title bar button
- **THEN** all UI text in the main window (including title bar, sidebars, and status bar) SHALL update to reflect the new language

#### Scenario: Language change from button persists to config
- **WHEN** the user changes language via the title bar button
- **THEN** the `arume.yml` configuration file SHALL be updated with the new `arume.language` value
