# First-Run Wizard (delta)

## MODIFIED Requirements

### Requirement: Language selection in wizard
The wizard SHALL include a language selector in the top row of the form allowing the user to choose between English and Spanish, positioned as
the leftmost control ahead of the theme selector.

#### Scenario: Language combo is displayed
- **WHEN** the first-run wizard is displayed
- **THEN** a combo box with language options SHALL appear in the top row, in the first position (left of the theme combo), showing "English" and
  "Español"

#### Scenario: Language combo defaults to the OS language
- **WHEN** the wizard is first displayed on a system whose `Locale.getDefault().getLanguage()` is `es`
- **THEN** the language combo SHALL have "Español" selected by default and all wizard texts SHALL appear in Spanish

#### Scenario: Language combo defaults to English for non-Spanish OS language
- **WHEN** the wizard is first displayed on a system whose `Locale.getDefault().getLanguage()` is not `es` (e.g., `en`)
- **THEN** the language combo SHALL have "English" selected by default and wizard texts SHALL appear in English

#### Scenario: Changing language refreshes all wizard texts immediately
- **WHEN** the user selects "English" from the language combo while the wizard is displayed in Spanish
- **THEN** all visible labels, button texts, combo items, and checkbox text SHALL update to English immediately without reloading the window

#### Scenario: Language choice is included in wizard result
- **WHEN** the user completes the wizard with "English" selected and clicks save
- **THEN** the `WizardResult` DTO SHALL include `language` field with value `"en"`

### Requirement: Wizard window width
The wizard window SHALL be wide enough to comfortably host the two top-row selectors (Language, Theme) without compressing them.

#### Scenario: Wizard width accommodates two top-row selectors
- **WHEN** the first-run wizard is displayed
- **THEN** the wizard root pane SHALL have a preferred width of approximately 494 pixels and the two top-row combos SHALL each be visible without
  truncation

## REMOVED Requirements

### Requirement: Country selection in wizard
**Reason**: El país pasa a ser un atributo de la empresa (issue #23); ya no se elige a nivel de aplicación en el primer arranque. El combo de país
se elimina de la fila superior del wizard.
**Migration**: El wizard ya no captura país. El idioma por defecto vuelve a derivar del `Locale` del OS. Los usuarios no pierden configuración (el
país no se persistía en BBDD; la clave sobrante `arume.country` se limpia al guardar).
