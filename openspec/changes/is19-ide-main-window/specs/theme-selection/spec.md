## MODIFIED Requirements

### Requirement: Theme can be changed from main window
The main application window SHALL include a theme selector button in the custom title bar that allows rotating between the three theme variants (Light, Dark, Dark Intense).

#### Scenario: Theme button exists in title bar
- **WHEN** the main application window is displayed
- **THEN** a theme button with an icon representing the current theme SHALL appear in the custom title bar, positioned between the language button and the window control buttons

#### Scenario: Theme button icon reflects current theme
- **WHEN** the main window is displayed and the active theme is "dark"
- **THEN** the theme button SHALL display a moon icon (FontIcon)

#### Scenario: Theme button icon updates on theme change
- **WHEN** the user clicks the theme button and the theme changes from "light" to "dark"
- **THEN** the theme button SHALL update its icon from sun to moon

#### Scenario: Theme button cycles through all three themes
- **WHEN** the user repeatedly clicks the theme button
- **THEN** the active theme SHALL cycle through light → dark → dark-intense → light in that order

#### Scenario: Changing theme from button applies immediately
- **WHEN** the user clicks the theme button and the theme changes
- **THEN** the application SHALL apply the new AtlantaFX theme immediately via `Application.setUserAgentStylesheet()`

#### Scenario: Changing theme from button persists to config
- **WHEN** the user clicks the theme button and the theme changes
- **THEN** the `arume.yml` configuration file SHALL be updated with the new `arume.theme` value
