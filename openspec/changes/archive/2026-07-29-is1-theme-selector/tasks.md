## 1. ThemeConfig enum and data model

- [x] 1.1 Create `ThemeConfig` enum in `com.angazo.arume.ui.config` with values `LIGHT`, `DARK`, `DARK_INTENSE`, each holding its semantic id, display label, and AtlantaFX theme supplier
- [x] 1.2 Add `theme` field to `ArumeConfig` record with default `"light"`
- [x] 1.3 Add `theme` field to `WizardResult` record
- [x] 1.4 Add `theme` persistence to `ConfigManager.save()` and `ConfigManager.load()`, with backward-compatible default `"light"`
- [x] 1.5 Add `updateTheme()` method to `ConfigManager` (analogous to `updateLanguage()`)
- [x] 1.6 Add i18n keys for theme labels: `wizard.theme`, `wizard.theme.light`, `wizard.theme.dark`, `wizard.theme.darkIntense`, `main.menu.theme`, `main.menu.theme.light`, `main.menu.theme.dark`, `main.menu.theme.darkIntense` in both English and Spanish bundles

## 2. Wizard theme selector

- [x] 2.1 Modify `first-run-wizard.fxml`: wrap `languageLabel` + `languageCombo` in an HBox row, add `themeCombo` next to language combo, add `themeLabel`
- [x] 2.2 Add `@FXML themeCombo` and `@FXML themeLabel` to `FirstRunWizardController`
- [x] 2.3 Populate theme combo in `initialize()` with 3 options, select "Claro" by default
- [x] 2.4 Add listener to `themeCombo.valueProperty()` that applies the selected theme via `Application.setUserAgentStylesheet()` immediately
- [x] 2.5 Include `theme` in `WizardResult` on save
- [x] 2.6 Include `theme` in `buildConfigFromWizard()` in `ArumeAppFX`
- [x] 2.7 Refresh theme combo text on language change in `refreshTexts()`

## 3. Main menu theme selector

- [x] 3.1 Add Theme `Menu` and 3 `RadioMenuItem`s to `main.fxml` inside a `ToggleGroup`
- [x] 3.2 Add `@FXML` fields for theme menu, items, and toggle group in `MainController`
- [x] 3.3 Add `onLightSelected()`, `onDarkSelected()`, `onDarkIntenseSelected()` handlers that apply theme and persist via `ConfigManager`
- [x] 3.4 Set current theme selection on initialization from config
- [x] 3.5 Refresh menu texts on language change
- [x] 3.6 Inject `ConfigManager` as Spring bean or instantiate it (follow existing pattern)

## 4. Apply theme on startup

- [x] 4.1 In `ArumeAppFX.start()`, read `theme` from loaded `ArumeConfig` and apply it instead of hardcoded Dracula
- [x] 4.2 Remove hardcoded `PrimerLight`/`Dracula` import from `ArumeAppFX` — use `ThemeConfig` enum for mapping
