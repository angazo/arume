## MODIFIED Requirements

### Requirement: Country flag indicator in main window title bar

The main application window SHALL display a non-interactive flag image of the country chosen at setup, positioned in the title bar to the
left of the language selector button, with an internationalized tooltip. The flag SHALL be served as a high-resolution PNG (3× the display
size) with landscape 4:3 proportions, so it renders sharply on HiDPI (2×/3×) displays while its logical display size stays at 32×24 px.

#### Scenario: Flag indicator is visible
- **WHEN** the main window is displayed with country `Chile` configured
- **THEN** a flag image of Chile SHALL be visible in the title bar before the language button with a logical size of 32×24 px

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
- **THEN** the flag indicator SHALL display the flag of Australia

#### Scenario: Flag indicator is crisp at high device pixel scale
- **WHEN** the application is displayed on a monitor whose device pixel scale is `2.0`
- **THEN** the flag of the configured country SHALL be displayed from the high-resolution (96×72) PNG asset while keeping the 32×24
  logical size, so it renders sharply

### Requirement: Country flags PNG assets

The system SHALL ship a high-resolution PNG file per supported country at `arume-ui/src/main/resources/icons/flags/<code>.png`, with
`<code>` the lowercase ISO 3166-1 alpha-3 code, rasterized at 3× the display size (96×72 px) with landscape 4:3 proportions from the
`flag-icons` SVG set (MIT license).

#### Scenario: PNG exists for every supported country
- **WHEN** the resource bundle of `arume-ui` is inspected
- **THEN** PNG files SHALL exist for the codes `esp`, `gbr`, `usa`, `chl`, `sgp`, `aus`, `zaf`

#### Scenario: PNG dimensions
- **WHEN** a flag PNG is loaded
- **THEN** its dimensions SHALL be 96 pixels wide by 72 pixels tall (3× the 32×24 logical display size)

#### Scenario: PNG aspect ratio matches the ImageView
- **WHEN** the aspect ratio of a flag PNG is computed
- **THEN** it SHALL be 4:3, equal to the 32×24 display size, so `preserveRatio` introduces no distortion
