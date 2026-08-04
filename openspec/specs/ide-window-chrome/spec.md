# IDE Window Chrome

## Purpose

Provide an undecorated main window with a custom title bar containing window controls, app logo, Help menu, and theme/language selector buttons.

## Requirements

### Requirement: Undecorated main window
The main application window SHALL use `StageStyle.UNDECORATED` to remove native OS window decorations and SHALL provide a custom title bar as replacement.

#### Scenario: Window starts without OS decorations
- **WHEN** the main window is displayed
- **THEN** no native title bar, borders, or window control buttons from the operating system SHALL be visible

#### Scenario: Window is displayed at 1200×800
- **WHEN** the main window is first shown after Spring Boot starts
- **THEN** the window SHALL have dimensions of 1200 pixels wide by 800 pixels tall

### Requirement: Custom title bar
The main window SHALL display a custom title bar in the `top` region of the `BorderPane` containing a logo, a Help menu, a non-interactive country
flag indicator, a language selector button with text label, a theme selector button, and window control buttons (minimize, maximize/restore, close).

#### Scenario: Title bar contains app logo
- **WHEN** the main window is displayed
- **THEN** an app logo image SHALL be visible at the left side of the custom title bar

#### Scenario: Title bar contains Help menu
- **WHEN** the main window is displayed
- **THEN** a "Help" menu SHALL be visible in the title bar to the right of the logo, containing a single "About..." menu item

#### Scenario: Title bar contains non-interactive country flag indicator
- **WHEN** the main window is displayed with a configured country
- **THEN** a non-interactive `ImageView` showing the flag of the configured country SHALL be visible in the title bar, positioned before the
  language selector button, and SHALL be transparent to mouse events

#### Scenario: Title bar contains language selector button with text label
- **WHEN** the main window is displayed
- **THEN** a language selector button SHALL be visible in the title bar, positioned between the country flag indicator and the theme button, and
  its label SHALL be the name of the currently active language in text (no flag icon)

#### Scenario: Title bar contains theme selector button
- **WHEN** the main window is displayed
- **THEN** a theme selector button with a theme icon (sun/moon) SHALL be visible in the title bar, positioned between the language button and the
  window control buttons

#### Scenario: Title bar contains minimize button
- **WHEN** the main window is displayed
- **THEN** a minimize button with a dash icon SHALL be visible at the rightmost area of the title bar, before the maximize button

#### Scenario: Title bar contains maximize/restore button
- **WHEN** the main window is displayed
- **THEN** a maximize/restore button with a square icon SHALL be visible at the rightmost area of the title bar, between the minimize and close
  buttons

#### Scenario: Title bar contains close button
- **WHEN** the main window is displayed
- **THEN** a close button with an X icon SHALL be visible at the rightmost area of the title bar

#### Scenario: Window buttons do not trigger drag
- **WHEN** the user presses a button on the title bar (minimize, maximize, close, language, theme, country flag indicator, or logo)
- **THEN** the window SHALL NOT start dragging

### Requirement: Minimize button behavior
The minimize button SHALL iconify the application window.

#### Scenario: Clicking minimize iconifies the window
- **WHEN** the user clicks the minimize button in the title bar
- **THEN** the application window SHALL be minimized (iconified)

### Requirement: Maximize/Restore button behavior
The maximize button SHALL toggle between maximized and normal window state.

#### Scenario: Clicking maximize when windowed maximizes the window
- **WHEN** the window is in normal state AND the user clicks the maximize button
- **THEN** the window SHALL become maximized to fill the entire screen

#### Scenario: Clicking restore when maximized restores the window
- **WHEN** the window is maximized AND the user clicks the restore button
- **THEN** the window SHALL return to its previous normal size and position

#### Scenario: Double-clicking title bar toggles maximize
- **WHEN** the user double-clicks on the title bar (not on any button)
- **THEN** the window SHALL toggle between maximized and normal state

### Requirement: Close button behavior
The close button SHALL close the application window and exit the application.

#### Scenario: Clicking close exits the application
- **WHEN** the user clicks the close button in the title bar
- **THEN** the application window SHALL close AND the Spring context SHALL shut down
