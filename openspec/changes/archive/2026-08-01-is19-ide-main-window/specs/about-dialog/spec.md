## ADDED Requirements

### Requirement: About dialog available from Help menu
The system SHALL provide an "About" dialog accessible from the Help menu in the title bar and from the Help button in the right sidebar.

#### Scenario: Help menu contains About item
- **WHEN** the user opens the Help menu from the title bar
- **THEN** an "About..." menu item SHALL be displayed

#### Scenario: Selecting About opens the dialog
- **WHEN** the user clicks "About..." from the Help menu
- **THEN** a modal dialog window SHALL open displaying application information

#### Scenario: Help button opens the same dialog
- **WHEN** the user clicks the Help button in the right sidebar
- **THEN** the same About dialog SHALL open

### Requirement: About dialog content
The About dialog SHALL display the application logo, name, version, and a brief description.

#### Scenario: About dialog shows app name
- **WHEN** the About dialog is displayed
- **THEN** the application name "Arume" SHALL be visible in the dialog

#### Scenario: About dialog shows version
- **WHEN** the About dialog is displayed
- **THEN** the application version SHALL be visible in the dialog

#### Scenario: About dialog shows description
- **WHEN** the About dialog is displayed
- **THEN** a brief description of the application purpose SHALL be visible in the dialog

#### Scenario: About dialog has a close button
- **WHEN** the About dialog is displayed
- **THEN** a "Close" button SHALL be visible that dismisses the dialog when clicked

### Requirement: About dialog is modal
The About dialog SHALL be modal, blocking interaction with the main window until dismissed.

#### Scenario: Main window is blocked while dialog is open
- **WHEN** the About dialog is displayed
- **THEN** the main application window SHALL not respond to mouse or keyboard input
