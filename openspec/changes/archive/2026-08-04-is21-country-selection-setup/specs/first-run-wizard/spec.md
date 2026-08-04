## MODIFIED Requirements

### Requirement: Language selection in wizard
The wizard SHALL include a language selector in the top row of the form allowing the user to choose between English and Spanish, positioned
between the country selector and the theme selector.

#### Scenario: Language combo is displayed
- **WHEN** the first-run wizard is displayed
- **THEN** a combo box with language options SHALL appear in the top row, in the middle position (between the country combo and the theme combo),
  showing "English" and "Español"

#### Scenario: Language combo defaults to the official language of the detected country
- **WHEN** the wizard is first displayed on a system whose `Locale.getDefault().getCountry()` resolves to a supported country `cl`
- **THEN** the language combo SHALL have "Español" selected by default (the official language of Chile) and all wizard texts SHALL appear in Spanish

#### Scenario: Language combo defaults to English for English-speaking default country
- **WHEN** the wizard is first displayed on a system whose detected country is `us`
- **THEN** the language combo SHALL have "English" selected by default and wizard texts SHALL appear in English

#### Scenario: Changing language refreshes all wizard texts immediately
- **WHEN** the user selects "English" from the language combo while the wizard is displayed in Spanish
- **THEN** all visible labels, button texts, combo items, and checkbox text SHALL update to English immediately without reloading the window

#### Scenario: Changing country does NOT override the user's language choice
- **WHEN** the user has manually selected "English" and then changes the country combo from "Spain" to "Chile"
- **THEN** the language combo SHALL remain on "English" (only the initial defaults are coupled; manual choices are respected)

#### Scenario: Language choice is included in wizard result
- **WHEN** the user completes the wizard with "English" selected and clicks save
- **THEN** the `WizardResult` DTO SHALL include `language` field with value `"en"`

## ADDED Requirements

### Requirement: Country selection in wizard
The wizard SHALL include a country selector as the leftmost control of the top row, offering the seven supported countries, with its default
derived from OS locale detection, and the choice SHALL be included in the wizard result.

#### Scenario: Country combo is displayed as first control of the top row
- **WHEN** the first-run wizard is displayed
- **THEN** a combo box with country options SHALL appear as the leftmost control of the top row, ahead of the language and theme combos

#### Scenario: Country combo lists exactly the supported countries
- **WHEN** the user opens the country combo
- **THEN** the listed items SHALL be exactly the seven supported countries, with their names shown in the currently active language

#### Scenario: Country combo defaults to detected OS country
- **WHEN** the wizard is first displayed on a system whose `Locale.getDefault().getCountry()` is `US`
- **THEN** the country combo SHALL have "United States" (or its Spanish equivalent) selected by default

#### Scenario: Country combo defaults to Spain when OS country is unsupported
- **WHEN** the wizard is first displayed on a system whose detected country code is not among the seven supported (e.g. `FR`)
- **THEN** the country combo SHALL have "Spain" (or its Spanish equivalent) selected by default

#### Scenario: Country combo items update on language change
- **WHEN** the user changes the language combo while the wizard is displayed
- **THEN** the country combo items SHALL refresh to show country names in the new active language, and the previously selected country SHALL remain
  selected (matched by code, not by label text)

#### Scenario: Country choice is included in wizard result
- **WHEN** the user completes the wizard with country "Chile" and clicks save
- **THEN** the `WizardResult` DTO SHALL include `country` field with value `"cl"`

### Requirement: Wizard window width
The wizard window SHALL be wide enough to comfortably host the three top-row selectors (Country, Language, Theme) without compressing them.

#### Scenario: Wizard width accommodates three top-row selectors
- **WHEN** the first-run wizard is displayed
- **THEN** the wizard root pane SHALL have a preferred width of approximately 600 pixels and the three top-row combos SHALL each be visible
  without truncation