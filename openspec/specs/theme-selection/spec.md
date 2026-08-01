# Theme Selection

## Purpose

Allow the user to choose and persist the visual theme of the application among 2 variants: Claro (PrimerLight) and Oscuro (Dracula).

## Requirements

### Requirement: Theme is persisted in configuration
The selected theme SHALL be stored in `arume.yml` under `arume.theme` and loaded on application startup.

#### Scenario: Theme is saved to config
- **WHEN** the user selects "Oscuro" and completes the wizard
- **THEN** the resulting `arume.yml` SHALL contain `arume.theme: dark`

#### Scenario: Theme is loaded from config on startup
- **WHEN** the application starts and `arume.yml` contains `arume.theme: dark`
- **THEN** the system SHALL apply the Dracula theme

#### Scenario: Config without theme field defaults to light
- **WHEN** `arume.yml` is loaded without an `arume.theme` field
- **THEN** the system SHALL default to the `light` theme (PrimerLight)

### Requirement: Theme can be changed in wizard
The first-run wizard SHALL include a theme selector combo box next to the language selector.

#### Scenario: Theme combo is displayed in wizard
- **WHEN** the wizard is displayed
- **THEN** a combo box SHALL appear in the same HBox row as the language combo, showing "Claro" and "Oscuro"

#### Scenario: Theme combo defaults to Claro
- **WHEN** the wizard is first displayed
- **THEN** the theme combo SHALL have "Claro" selected by default and the PrimerLight theme SHALL be active

#### Scenario: Changing theme applies immediately in wizard
- **WHEN** the user selects "Oscuro" from the theme combo while the wizard is displayed
- **THEN** the wizard window SHALL immediately switch to Dracula theme without reloading

#### Scenario: Theme choice is included in wizard result
- **WHEN** the user completes the wizard with "Oscuro" selected
- **THEN** the `WizardResult` DTO SHALL include `theme` field with value `"dark"`

### Requirement: Theme can be changed from title bar
The main application window SHALL include a theme selector button in the custom title bar that toggles between light and dark themes.

#### Scenario: Theme button exists in title bar
- **WHEN** the main application window is displayed
- **THEN** a theme button with an icon representing the current theme SHALL appear in the custom title bar, positioned between the language button and the window control buttons

#### Scenario: Theme button shows sun icon for light theme
- **WHEN** the main window is displayed and the active theme is "light"
- **THEN** the theme button SHALL display a sun icon (FontIcon)

#### Scenario: Theme button shows moon icon for dark theme
- **WHEN** the main window is displayed and the active theme is "dark"
- **THEN** the theme button SHALL display a moon icon (FontIcon)

#### Scenario: Theme button toggles between light and dark
- **WHEN** the user clicks the theme button
- **THEN** the active theme SHALL toggle between light and dark

#### Scenario: Changing theme from button applies immediately
- **WHEN** the user clicks the theme button and the theme changes
- **THEN** the application SHALL apply the new AtlantaFX theme immediately via `Application.setUserAgentStylesheet()`

#### Scenario: Changing theme from button persists to config
- **WHEN** the user clicks the theme button and the theme changes
- **THEN** the `arume.yml` configuration file SHALL be updated with the new `arume.theme` value

### Requirement: Theme mapping
The system SHALL maintain a mapping between semantic theme identifiers and AtlantaFX theme classes.

#### Scenario: Light maps to PrimerLight
- **WHEN** the theme identifier is `"light"`
- **THEN** the system SHALL apply `PrimerLight().getUserAgentStylesheet()`

#### Scenario: Dark maps to Dracula
- **WHEN** the theme identifier is `"dark"`
- **THEN** the system SHALL apply `Dracula().getUserAgentStylesheet()`

