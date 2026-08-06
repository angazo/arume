# IDE Window Chrome (delta)

## MODIFIED Requirements

### Requirement: Custom title bar
The main window SHALL display a custom title bar in the `top` region of the `BorderPane` containing a logo, a Help menu, a language selector
button with text label, a theme selector button, and window control buttons (minimize, maximize/restore, close).

#### Scenario: Title bar contains app logo
- **WHEN** the main window is displayed
- **THEN** an app logo image SHALL be visible at the left side of the custom title bar

#### Scenario: Title bar contains Help menu
- **WHEN** the main window is displayed
- **THEN** a "Help" menu SHALL be visible in the title bar to the right of the logo, containing a single "About..." menu item

#### Scenario: Title bar contains language selector button with text label
- **WHEN** the main window is displayed
- **THEN** a language selector button SHALL be visible in the title bar, positioned before the theme button, and its label SHALL be the name of the
  currently active language in text (no flag icon)

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
- **WHEN** the user presses a button on the title bar (minimize, maximize, close, language, theme, or logo)
- **THEN** the window SHALL NOT start dragging
