# Sidebar Navigation

## Purpose

Provide a left sidebar with navigation buttons (Dashboard, Invoices, Accounting) using exclusive ToggleButton selection, plus a Settings button fixed at the bottom, to switch center content views.

## Requirements

### Requirement: Left sidebar with navigation buttons
The main window SHALL display a left sidebar containing navigation buttons organized as large icons, with a Settings button fixed at the bottom.

#### Scenario: Left sidebar is visible
- **WHEN** the main window is displayed
- **THEN** a vertical sidebar with a fixed width of approximately 48 pixels SHALL be visible in the left region of the `BorderPane`

#### Scenario: Dashboard navigation button exists
- **WHEN** the main window is displayed
- **THEN** a navigation button labeled "Dashboard" with an appropriate icon SHALL be visible at the top of the left sidebar

#### Scenario: Invoices navigation button exists
- **WHEN** the main window is displayed
- **THEN** a navigation button labeled "Invoices" with an appropriate icon SHALL be visible in the left sidebar below the Dashboard button

#### Scenario: Accounting navigation button exists
- **WHEN** the main window is displayed
- **THEN** a navigation button labeled "Accounting" with an appropriate icon SHALL be visible in the left sidebar below the Invoices button

#### Scenario: Settings button is at the bottom of the sidebar
- **WHEN** the main window is displayed
- **THEN** a Settings button with a gear icon SHALL be visible at the very bottom of the left sidebar, visually separated from the navigation buttons by a spacer

### Requirement: Navigation buttons use exclusive selection
The navigation buttons (Dashboard, Invoices, Accounting) SHALL be `ToggleButton`s grouped in a `ToggleGroup`, ensuring only one is selected at a time.

#### Scenario: Only one navigation button is selected at a time
- **WHEN** the user clicks the "Invoices" button while "Dashboard" is selected
- **THEN** the "Invoices" button SHALL become selected AND the "Dashboard" button SHALL become deselected

#### Scenario: Settings button is not part of the navigation group
- **WHEN** the user clicks the Settings button
- **THEN** no navigation button SHALL be selected in the `ToggleGroup`

### Requirement: Center view changes on navigation
Selecting a navigation button in the left sidebar SHALL switch the content displayed in the center area.

#### Scenario: Dashboard button shows dashboard view
- **WHEN** the user selects the "Dashboard" navigation button
- **THEN** the center area SHALL display the dashboard placeholder view

#### Scenario: Invoices button shows invoices view
- **WHEN** the user selects the "Invoices" navigation button
- **THEN** the center area SHALL display the invoices placeholder view

#### Scenario: Accounting button shows accounting view
- **WHEN** the user selects the "Accounting" navigation button
- **THEN** the center area SHALL display the accounting placeholder view

#### Scenario: Settings button shows settings view
- **WHEN** the user clicks the Settings button
- **THEN** the center area SHALL display the settings placeholder view

### Requirement: Dashboard is selected by default
The Dashboard navigation button SHALL be selected when the main window first appears.

#### Scenario: Dashboard is selected on initial load
- **WHEN** the main window is displayed for the first time after startup
- **THEN** the Dashboard button SHALL be selected AND the dashboard placeholder view SHALL be visible in the center area
