## ADDED Requirements

### Requirement: Status bar displays at the bottom
The main window SHALL display a status bar in the `bottom` region of the `BorderPane`.

#### Scenario: Status bar is visible at the bottom
- **WHEN** the main window is displayed
- **THEN** a horizontal bar approximately 24 pixels tall SHALL be visible at the very bottom of the window

#### Scenario: Status bar has a top border
- **WHEN** the main window is displayed
- **THEN** the status bar SHALL have a thin border line separating it from the content area above

### Requirement: Database connection indicator
The status bar SHALL display a database connection indicator at its right side, showing the database engine name and connection status.

#### Scenario: H2 label is displayed
- **WHEN** the main window is displayed
- **THEN** the text "H2" SHALL be visible at the right side of the status bar

#### Scenario: Green dot indicates connected
- **WHEN** the application is connected to the H2 database
- **THEN** a small green circle SHALL appear to the left of the "H2" label in the status bar

#### Scenario: Red dot indicates disconnected
- **WHEN** the application cannot connect to the H2 database
- **THEN** a small red circle SHALL appear to the left of the "H2" label in the status bar

#### Scenario: Connection indicator has a tooltip with details
- **WHEN** the user hovers the mouse over the connection indicator
- **THEN** a tooltip SHALL appear showing the database engine and connection status (e.g., "H2 (connected)" or "H2 (disconnected)")
