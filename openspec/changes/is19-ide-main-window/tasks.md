## 1. Dependencies and build setup

- [x] 1.1 Add Ikonli version and library entries to `gradle/libs.versions.toml`
- [x] 1.2 Add `ikonli-javafx`, `ikonli-fontawesome5-pack`, and `ikonli-materialdesign2-pack` to `arume-ui/build.gradle`
- [x] 1.3 Verify build compiles with new dependencies: `./gradlew build`

## 2. Custom CSS for IDE layout

- [x] 2.1 Create `src/arume-ui/src/main/resources/css/arume.css` with styles for `.title-bar`, `.title-bar .button`, `.title-bar .window-close:hover`, `.sidebar`, `.status-bar`
- [x] 2.2 Load `arume.css` in `ArumeAppFX.replaceWithMainScene()` after setting the scene

## 3. FXML layout restructure (`main.fxml`)

- [x] 3.1 Rebuild `main.fxml` as `BorderPane` with `top` (HBox title bar), `left` (VBox sidebar), `center` (StackPane), `right` (VBox sidebar), `bottom` (HBox status bar)
- [x] 3.2 Define title bar nodes in FXML: logo `ImageView`, `MenuButton` Help with `MenuItem` "About...", spacer `Region`, `Button` language, `Button` theme, `Button` minimize, `Button` maximize, `Button` close; assign style classes (`title-bar`, `window-close`)
- [x] 3.3 Define left sidebar nodes in FXML: `ToggleButton` Dashboard, `ToggleButton` Invoices, `ToggleButton` Accounting (all in a `ToggleGroup`), spacer `Region`, `Button` Settings; assign style class `sidebar`
- [x] 3.4 Define right sidebar nodes in FXML: `Button` Help, spacer `Region`; assign style class `sidebar`
- [x] 3.5 Define center area: `StackPane` with 4 placeholder panes (dashboard, invoices, accounting, settings), each containing a `Label` with the view name
- [x] 3.6 Define status bar nodes in FXML: spacer `Region`, `Circle dbDot`, `Label` "H2"; assign style class `status-bar`
- [x] 3.7 Validate FXML is well-formed by compiling and loading

## 4. MainController rewrite

- [x] 4.1 Replace all `@FXML` fields: remove `MenuButton`/`RadioMenuItem`/`MenuItem` fields; add fields for title bar buttons, sidebars, center StackPane, status bar
- [x] 4.2 Add `setStage(Stage)` method and `Stage` field for window control operations
- [x] 4.3 Implement `initialize()`: set up `ToggleGroup` for navigation, set default view (Dashboard selected), wire up all button event handlers, register i18n listener
- [x] 4.4 Implement window control button handlers: minimize → `stage.setIconified(true)`, maximize → `stage.setMaximized(!stage.isMaximized())`, close → `stage.close()`
- [x] 4.5 Implement window drag via title bar mouse listeners (`MOUSE_PRESSED` store offset, `MOUSE_DRAGGED` move window, double-click → toggle maximize)
- [x] 4.6 Implement navigation view switching: `ToggleGroup` listener → `switchView(viewId)` that toggles visibility in the center `StackPane`
- [x] 4.7 Implement language button handler: toggle `en`/`es`, update flag icon, call `I18nManager.setLanguage()` and `ConfigManager.updateLanguage()`
- [x] 4.8 Implement theme button handler: cycle light → dark → dark-intense, update icon, call `ThemeConfig.fromId().apply()` and `ConfigManager.updateTheme()`
- [x] 4.9 Implement Help → About menu item handler: calls `showAboutDialog()`

## 5. About dialog

- [x] 5.1 Create `showAboutDialog()` method in `MainController` that builds a modal `Stage` with `VBox` containing logo `ImageView`, app name `Label`, version `Label`, description `Label`, and "Close" `Button`
- [x] 5.2 Wire Help button in right sidebar to also call `showAboutDialog()`
- [x] 5.3 Add i18n keys for "about.title", "about.version", "about.description", "about.close" in both `messages.properties` and `messages_es.properties`

## 6. Window chrome (undecorated stage)

- [x] 6.1 In `ArumeAppFX.replaceWithMainScene()`, set `stage.initStyle(StageStyle.UNDECORATED)`
- [x] 6.2 Set initial window size to 1200×800 and center on screen
- [x] 6.3 After FXML load, inject the `Stage` into `MainController` via setter

## 7. Status bar DB indicator

- [x] 7.1 Create a `DataSource` field in `MainController` injected by Spring via constructor or `@Autowired`
- [x] 7.2 Implement DB connection status check method that tests `dataSource.getConnection().isValid(2)` and updates the dot color (green/red)
- [x] 7.3 Set tooltip on the DB indicator HBox showing connection details
- [x] 7.4 Add i18n keys for "status.db.connected" and "status.db.disconnected" tooltips

## 8. Ikonli icon integration

- [x] 8.1 In `MainController.initialize()`, replace placeholder icons/text with `FontIcon` instances for all buttons: navigation (Dashboard, Invoices, Accounting, Settings), Help, language flags, theme sun/moon, window controls (min, max, close)
- [x] 8.2 Update language button icon dynamically when language changes (US/UK flag ↔ ES flag)
- [x] 8.3 Update theme button icon dynamically when theme changes (sun ↔ moon ↔ dark moon)

## 9. i18n integration

- [x] 9.1 Add all new i18n keys to `messages.properties`: navigation button labels, Help menu, About dialog, status bar, Settings
- [x] 9.2 Add corresponding Spanish translations to `messages_es.properties`
- [x] 9.3 In `MainController.refreshTexts()`, update all i18n-aware UI elements (navigation button text, Help menu, About dialog fields, status bar tooltip, Settings button text)
- [x] 9.4 Verify language switching updates all UI text in the redesigned window

## 10. Testing and verification

- [x] 10.1 Run `./gradlew build` to verify compilation and existing tests pass
- [ ] 10.2 Run `./gradlew bootRun` and manually verify: window is undecorated, custom title bar works, drag moves window, min/max/close behave correctly
- [ ] 10.3 Manually test navigation: clicking sidebar buttons switches center content
- [ ] 10.4 Manually test language switch: flag icon updates, all text updates
- [ ] 10.5 Manually test theme switch: icon updates, all themes applied (light, dark, dark-intense)
- [ ] 10.6 Manually test About dialog: opens from Help menu and Help button, shows info, closes
- [ ] 10.7 Manually verify status bar DB indicator shows green dot (H2 connected)
