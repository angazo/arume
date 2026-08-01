# Right Sidebar

## Purpose

Display a right sidebar with a Help button that opens the About dialog.

## Requirements

### Requirement: Right sidebar with help button
The main window SHALL display a right sidebar containing a Help button positioned at the top.

#### Scenario: Right sidebar is visible
- **WHEN** the main window is displayed
- **THEN** a vertical sidebar with a fixed width of approximately 48 pixels SHALL be visible in the right region of the `BorderPane`

#### Scenario: Help button is at the top of the right sidebar
- **WHEN** the main window is displayed
- **THEN** a Help button with a question mark icon SHALL be visible at the top of the right sidebar

#### Scenario: Right sidebar contains a spacer below the help button
- **WHEN** the main window is displayed
- **THEN** the right sidebar SHALL contain vertical space below the Help button, pushing it to the top

### Requirement: Help button opens About dialog
Clicking the Help button in the right sidebar SHALL open the About dialog.

#### Scenario: Help button click shows About dialog
- **WHEN** the user clicks the Help button in the right sidebar
- **THEN** the About dialog SHALL be displayed as a modal window
