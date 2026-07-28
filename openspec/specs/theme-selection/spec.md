# Theme Selection

## Purpose

Allow the user to choose and persist the visual theme of the application among 3 variants: Claro (PrimerLight), Oscuro (Dracula), and Oscuro intenso (PrimerDark).

## Requirements

### Requirement: Theme is persisted in configuration
The selected theme SHALL be stored in `arume.yml` under `arume.theme` and loaded on application startup.

#### Scenario: Theme is saved to config
- **WHEN** the user selects "Oscuro" and completes the wizard
- **THEN** the resulting `arume.yml` SHALL contain `arume.theme: dark`

#### Scenario: Theme is loaded from config on startup
- **WHEN** the application starts and `arume.yml` contains `arume.theme: dark-intense`
- **THEN** the system SHALL apply the PrimerDark theme

#### Scenario: Config without theme field defaults to light
- **WHEN** `arume.yml` is loaded without an `arume.theme` field
- **THEN** the system SHALL default to the `light` theme (PrimerLight)

### Requirement: Theme can be changed in wizard
The first-run wizard SHALL include a theme selector combo box next to the language selector.

#### Scenario: Theme combo is displayed in wizard
- **WHEN** the wizard is displayed
- **THEN** a combo box SHALL appear in the same HBox row as the language combo, showing "Claro", "Oscuro", and "Oscuro intenso"

#### Scenario: Theme combo defaults to Claro
- **WHEN** the wizard is first displayed
- **THEN** the theme combo SHALL have "Claro" selected by default and the PrimerLight theme SHALL be active

#### Scenario: Changing theme applies immediately in wizard
- **WHEN** the user selects "Oscuro intenso" from the theme combo while the wizard is displayed
- **THEN** the wizard window SHALL immediately switch to PrimerDark theme without reloading

#### Scenario: Theme choice is included in wizard result
- **WHEN** the user completes the wizard with "Oscuro" selected
- **THEN** the `WizardResult` DTO SHALL include `theme` field with value `"dark"`

### Requirement: Theme can be changed from main menu
The main application window SHALL include a Theme menu with radio items for each theme variant.

#### Scenario: Theme menu items exist
- **WHEN** the main application window is displayed
- **THEN** a "Theme" menu SHALL appear in the menu bar to the right of the "Language" menu, containing three RadioMenuItems: "Claro", "Oscuro", and "Oscuro intenso"

#### Scenario: Current theme is selected in menu
- **WHEN** the main window is displayed and the active theme is "Oscuro intenso"
- **THEN** the "Oscuro intenso" RadioMenuItem SHALL be selected

#### Scenario: Changing theme from menu applies immediately
- **WHEN** the user selects "Claro" from the Theme menu while "Oscuro intenso" is active
- **THEN** the application SHALL switch to PrimerLight theme immediately and persist the change to `arume.yml`

### Requirement: Theme mapping
The system SHALL maintain a mapping between semantic theme identifiers and AtlantaFX theme classes.

#### Scenario: Light maps to PrimerLight
- **WHEN** the theme identifier is `"light"`
- **THEN** the system SHALL apply `PrimerLight().getUserAgentStylesheet()`

#### Scenario: Dark maps to Dracula
- **WHEN** the theme identifier is `"dark"`
- **THEN** the system SHALL apply `Dracula().getUserAgentStylesheet()`

#### Scenario: Dark-intense maps to PrimerDark
- **WHEN** the theme identifier is `"dark-intense"`
- **THEN** the system SHALL apply `PrimerDark().getUserAgentStylesheet()`
