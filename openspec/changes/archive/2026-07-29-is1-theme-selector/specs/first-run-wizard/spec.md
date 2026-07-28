# First-Run Wizard

## Purpose

Detect whether the application is running for the first time and present a configuration wizard to collect language preference, theme preference, and database connection settings before starting Spring Boot.

## MODIFIED Requirements

### Requirement: Theme selection in wizard
The wizard SHALL include a theme selector combo box next to the language selector at the top of the form, allowing the user to choose between Claro, Oscuro, and Oscuro intenso.

#### Scenario: Theme combo is displayed alongside language combo
- **WHEN** the first-run wizard is displayed
- **THEN** a combo box with theme options SHALL appear in the same HBox row as the language combo, showing "Claro", "Oscuro", and "Oscuro intenso"

#### Scenario: Theme combo defaults to Claro
- **WHEN** the wizard is first displayed
- **THEN** the theme combo SHALL have "Claro" selected by default and the PrimerLight theme SHALL be active

#### Scenario: Changing theme refreshes wizard appearance immediately
- **WHEN** the user selects "Oscuro intenso" from the theme combo while the wizard is displayed
- **THEN** all visible controls SHALL update to the PrimerDark theme immediately without reloading the window

#### Scenario: Theme choice is included in wizard result
- **WHEN** the user completes the wizard with "Oscuro" selected and clicks save
- **THEN** the `WizardResult` DTO SHALL include a `theme` field with value `"dark"`
