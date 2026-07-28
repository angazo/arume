## ADDED Requirements

### Requirement: First-run detection
The application SHALL detect whether it has been previously configured by checking for the presence of `arume.yml` in the directory containing the application JAR.

#### Scenario: First run — no config file found
- **WHEN** the application starts and `arume.yml` does NOT exist in the JAR directory
- **THEN** the system SHALL display the first-run wizard window

#### Scenario: Subsequent run — config file exists
- **WHEN** the application starts and `arume.yml` exists in the JAR directory
- **THEN** the system SHALL proceed directly to start Spring Boot without showing the wizard

#### Scenario: JAR location cannot be determined
- **WHEN** the application cannot determine the JAR directory via `ProtectionDomain.getCodeSource()`
- **THEN** the system SHALL fall back to using `user.dir` as the search directory for `arume.yml`

### Requirement: First-run wizard window
The system SHALL present a configuration wizard window on first run that allows the user to configure database connection settings.

#### Scenario: Wizard is displayed
- **WHEN** the first-run wizard is triggered
- **THEN** the system SHALL display a modal JavaFX window with title "Configuración inicial — Arume"

#### Scenario: Wizard blocks main window
- **WHEN** the wizard window is displayed
- **THEN** the main application window SHALL NOT be shown until the wizard is completed or cancelled

### Requirement: Database type selection
The wizard SHALL allow the user to select the database type from a dropdown.

#### Scenario: H2 is enabled
- **WHEN** the user opens the database type dropdown
- **THEN** "H2 (fichero local)" SHALL appear as an enabled option and be selected by default

#### Scenario: PostgreSQL is disabled
- **WHEN** the user opens the database type dropdown
- **THEN** "PostgreSQL (remoto)" SHALL appear as a disabled option with a "(próximamente)" indicator

#### Scenario: Selecting H2 shows H2-specific fields
- **WHEN** H2 is selected in the database type dropdown
- **THEN** the H2 configuration fields (storage path, username, password) SHALL be visible and enabled

### Requirement: Database storage path configuration
The wizard SHALL allow the user to configure the storage path for the H2 database file.

#### Scenario: Default storage path
- **WHEN** the wizard is first displayed
- **THEN** the storage path field SHALL default to `<JAR-directory>/data`

#### Scenario: Browse button opens directory chooser
- **WHEN** the user clicks the "Examinar" button next to the storage path
- **THEN** a native `DirectoryChooser` dialog SHALL open for the user to select a directory

#### Scenario: User selects a directory via chooser
- **WHEN** the user selects a directory and confirms the `DirectoryChooser` dialog
- **THEN** the storage path field SHALL be updated with the selected directory path

### Requirement: Credentials configuration
The wizard SHALL allow the user to set a username and password for H2 database access.

#### Scenario: Username field is present
- **WHEN** the wizard is displayed
- **THEN** a text field for username SHALL be visible and default to "admin"

#### Scenario: Password fields are present
- **WHEN** the wizard is displayed
- **THEN** a password field and a password confirmation field SHALL be visible, both with masked input

#### Scenario: Password validation — empty password
- **WHEN** the user submits the form with an empty password
- **THEN** the system SHALL display an error indicating that the password cannot be empty

#### Scenario: Password validation — password too short
- **WHEN** the user submits the form with a password shorter than 12 characters
- **THEN** the system SHALL display an error indicating that the password must be at least 12 characters

#### Scenario: Password validation — passwords do not match
- **WHEN** the user submits the form with mismatched password and confirmation fields
- **THEN** the system SHALL display an error indicating that passwords do not match

#### Scenario: Password validation — valid credentials
- **WHEN** the user submits the form with a non-empty username, a password of at least 12 characters, and matching confirmation
- **THEN** the system SHALL accept the credentials and proceed to save the configuration

### Requirement: Encryption placeholder
The wizard SHALL include a checkbox to indicate whether sensitive data should be encrypted in the configuration file.

#### Scenario: Encryption checkbox is visible
- **WHEN** the wizard is displayed
- **THEN** a checkbox labeled "Cifrar datos sensibles" SHALL be visible but its value SHALL NOT affect the saved output in this iteration (all data is stored in plain text)

### Requirement: Wizard cancellation
The user SHALL be able to cancel the wizard and exit the application.

#### Scenario: User cancels the wizard
- **WHEN** the user closes the wizard window or clicks a "Cancelar" button
- **THEN** the wizard SHALL close and the application SHALL terminate without starting Spring Boot
