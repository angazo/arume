# First-Run Wizard

## Purpose

Detect whether the application is running for the first time and present a configuration wizard to collect language preference, theme preference, and database connection settings before starting Spring Boot.

## Requirements

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

### Requirement: Language selection in wizard
The wizard SHALL include a language selector at the top of the form allowing the user to choose between English and Spanish before configuring the database.

#### Scenario: Language combo is displayed
- **WHEN** the first-run wizard is displayed
- **THEN** a combo box with language options SHALL appear at the top of the wizard, showing "English" and "Español"

#### Scenario: Language combo defaults to detected OS language
- **WHEN** the wizard is first displayed on a system with Spanish locale
- **THEN** the language combo SHALL have "Español" selected by default and all wizard texts SHALL appear in Spanish

#### Scenario: Changing language refreshes all wizard texts immediately
- **WHEN** the user selects "English" from the language combo while the wizard is displayed in Spanish
- **THEN** all visible labels, button texts, combo items, and checkbox text SHALL update to English immediately without reloading the window

#### Scenario: Language choice is included in wizard result
- **WHEN** the user completes the wizard with "English" selected and clicks save
- **THEN** the `WizardResult` DTO SHALL include `language` field with value `"en"`

### Requirement: Theme selection in wizard
The wizard SHALL include a theme selector combo box next to the language selector at the top of the form, allowing the user to choose between Claro, Oscuro, and Oscuro intenso.

#### Scenario: Theme combo is displayed alongside language combo
- **WHEN** the first-run wizard is displayed
- **THEN** a combo box with theme options SHALL appear alongside the language combo, showing "Claro", "Oscuro", and "Oscuro intenso"

#### Scenario: Theme combo defaults to Claro
- **WHEN** the wizard is first displayed
- **THEN** the theme combo SHALL have "Claro" selected by default and the PrimerLight theme SHALL be active

#### Scenario: Changing theme refreshes wizard appearance immediately
- **WHEN** the user selects "Oscuro intenso" from the theme combo while the wizard is displayed
- **THEN** all visible controls SHALL update to the PrimerDark theme immediately without reloading the window

#### Scenario: Theme choice is included in wizard result
- **WHEN** the user completes the wizard with "Oscuro" selected and clicks save
- **THEN** the `WizardResult` DTO SHALL include a `theme` field with value `"dark"`

### Requirement: Internationalized wizard labels and buttons
All user-facing text in the wizard SHALL be sourced from the i18n resource bundles and set programmatically from the controller.

#### Scenario: Wizard texts are set from resource bundle
- **WHEN** the wizard controller initializes
- **THEN** all labels, button texts, and the window title SHALL be set by calling `I18nManager.getString()` with the appropriate key for each text element

#### Scenario: Validation messages are internationalized
- **WHEN** form validation fails (e.g., empty password)
- **THEN** the error alert title and message SHALL display in the currently active language

#### Scenario: Directory chooser title is internationalized
- **WHEN** the user clicks the browse button
- **THEN** the `DirectoryChooser` dialog title SHALL be set from the i18n bundle for the current language

### Requirement: First-run wizard window
The system SHALL present a configuration wizard window on first run that allows the user to configure language preference and database connection settings.

#### Scenario: Wizard is displayed
- **WHEN** the first-run wizard is triggered
- **THEN** the system SHALL display a modal JavaFX window whose title is sourced from the i18n bundle (e.g., "Configuración inicial — Arume" in Spanish, "Initial Setup — Arume" in English)

#### Scenario: Wizard blocks main window
- **WHEN** the wizard window is displayed
- **THEN** the main application window SHALL NOT be shown until the wizard is completed or cancelled

### Requirement: Database type selection
The wizard SHALL allow the user to select the database type from a dropdown with internationalized labels.

#### Scenario: H2 is enabled
- **WHEN** the user opens the database type dropdown
- **THEN** a localized string for H2 (e.g., "H2 (fichero local)" in Spanish, "H2 (local file)" in English) SHALL appear as an enabled option and be selected by default

#### Scenario: PostgreSQL is disabled
- **WHEN** the user opens the database type dropdown
- **THEN** a localized string for PostgreSQL SHALL appear as a disabled option with a localized "(próximamente)" / "(coming soon)" indicator

#### Scenario: Selecting H2 shows H2-specific fields
- **WHEN** H2 is selected in the database type dropdown
- **THEN** the H2 configuration fields (storage path, username, password) SHALL be visible and enabled

### Requirement: Database storage path configuration
The wizard SHALL allow the user to configure the storage path for the H2 database file.

#### Scenario: Default storage path
- **WHEN** the wizard is first displayed
- **THEN** the storage path field SHALL default to `<JAR-directory>/data`

#### Scenario: Browse button opens directory chooser
- **WHEN** the user clicks the browse button
- **THEN** a native `DirectoryChooser` dialog SHALL open with its title set from the i18n bundle for the current language

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
- **THEN** the system SHALL display an i18n error in the current language indicating that the password cannot be empty

#### Scenario: Password validation — password too short
- **WHEN** the user submits the form with a password shorter than 12 characters
- **THEN** the system SHALL display an i18n error in the current language indicating that the password must be at least 12 characters

#### Scenario: Password validation — passwords do not match
- **WHEN** the user submits the form with mismatched password and confirmation fields
- **THEN** the system SHALL display an i18n error in the current language indicating that passwords do not match

#### Scenario: Password validation — valid credentials
- **WHEN** the user submits the form with a non-empty username, a password of at least 12 characters, and matching confirmation
- **THEN** the system SHALL accept the credentials and proceed to save the configuration

### Requirement: Encryption placeholder
The wizard SHALL include a checkbox to indicate whether sensitive data should be encrypted in the configuration file.

#### Scenario: Encryption checkbox is visible
- **WHEN** the wizard is displayed
- **THEN** a checkbox with i18n text (e.g., "Cifrar datos sensibles" in Spanish, "Encrypt sensitive data" in English) SHALL be visible but its value SHALL NOT affect the saved output in this iteration (all data is stored in plain text)

### Requirement: Wizard cancellation
The user SHALL be able to cancel the wizard and exit the application.

#### Scenario: User cancels the wizard
- **WHEN** the user closes the wizard window or clicks a button whose text is sourced from the i18n bundle (e.g., "Cancelar" / "Cancel")
- **THEN** the wizard SHALL close and the application SHALL terminate without starting Spring Boot
