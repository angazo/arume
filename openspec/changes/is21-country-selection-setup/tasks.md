## 1. Catálogo y persistencia de país

- [x] 1.1 Crear enum `com.angazo.arume.ui.config.Country` con las 7 entradas (`ES`, `GB`, `US`, `CL`, `SG`, `AU`, `ZA`), campos `code` (ISO-2
  minúsculas) y `officialLanguage` (`es`/`en`), y métodos `fromCode(String)`, `detectDefault()`, `getLabelKey()`.
- [x] 1.2 Añadir campo `String country` a `WizardResult` (primer campo, por orden de captura).
- [x] 1.3 Añadir campo `String country` a `ArumeConfig` (entre `language` y `dbType`).
- [x] 1.4 Actualizar `ConfigManager.load()`: leer `arume.country` con `getOrDefault("country", "es")` en minúsculas.
- [x] 1.5 Actualizar `ConfigManager.save()` para escribir `arume.country` (minúsculas).
- [x] 1.6 Actualizar `ConfigManager.updateLanguage(String)` y `updateTheme(String)` para propagar `config.country()` intacto al reconstruir
  `ArumeConfig`.
- [x] 1.7 Test `ConfigManagerTest`: cubrir ronda load→save con country, fallback a `es` cuando `arume.yml` no trae `country`, y preservación de
  country tras `updateLanguage`/`updateTheme`.
- [x] 1.8 Test unitario `CountryTest`: `fromCode` (válido, inválido, null), `detectDefault` con varios `Locale`, `getLabelKey`.

## 2. Recursos i18n y banderas

- [x] 2.1 Añadir a `messages.properties` claves: `wizard.country`, `wizard.country.es|gb|us|cl|sg|au|za`, `main.language.en`,
  `main.language.es`, `main.country.tooltip=Country: {0}`.
- [x] 2.2 Añadir a `messages_es.properties` las mismas claves con traducción al español, manteniendo paridad 1:1 con el bundle English.
- [x] 2.3 Crear `arume-ui/src/main/resources/icons/flags/` con los 7 PNGs 32×20: `es.png`, `gb.png`, `us.png`, `cl.png`, `sg.png`, `au.png`,
  `za.png`.
- [x] 2.4 Test de recursos: verificar que para cada `Country.values()` existe `/icons/flags/<code>.png` y que todos los keys i18n nuevos
  existen en ambos bundles.

## 3. Wizard — combo de país y layout

- [x] 3.1 En `first-run-wizard.fxml`: reordenar el `HBox` superior a tres `VBox` en orden País → Idioma → Tema, con `prefWidth` de combos 240/180/150.
- [x] 3.2 En `ArumeAppFX.showFirstRunWizard`: cambiar tamaño de la `Scene` de 494×840 a 600×840 y `prefWidth` del wizard root a 600.
- [x] 3.3 En `FirstRunWizardController`: añadir `@FXML ComboBox<String> countryCombo` + `countryLabel`, inicializar items con
  `I18nManager.getString(country.getLabelKey())` para cada `Country`.
- [x] 3.4 Default del combo de país: `Country.detectDefault()`, seleccionado por código (no por texto).
- [x] 3.5 Default inicial del combo de idioma: `Country.detectDefault().officialLanguage()`, en vez de `I18nManager.detectDefaultLanguage()`.
- [x] 3.6 En `refreshTexts()`: refrescar items del combo de país en el idioma activo y re-seleccionar el país previamente seleccionado (memoria
  por código).
- [x] 3.7 Evitar override del idioma al cambiar país manualmente (solo el default inicial está acoplado).
- [x] 3.8 En `onSave()`: construir `WizardResult` con `Country.fromCode(...)` del item seleccionado (resolución por match de label vs enumerado);
  pasar el `code` al DTO.
- [x] 3.9 En `ArumeAppFX.runWizardFlow`: `I18nManager.init(Country.detectDefault().officialLanguage())` antes de abrir el wizard (sustituye al
  `detectDefaultLanguage()` actual).
- [x] 3.10 En `ArumeAppFX.buildConfigFromWizard`: propagar `result.country()` al nuevo `ArumeConfig`.

## 4. Ventana principal — bandera e idioma en texto

- [x] 4.1 En `main.fxml`: añadir `@FXML ImageView countryFlag` entre el `Region` spacer y `languageBtn` (antes del botón de idioma).
- [x] 4.2 En `MainController`: inyectar `countryFlag`; cargar `Image` desde `/icons/flags/<country.code>.png` (resuelto vía `ConfigManager.load()`
  + `Country.fromCode`); `setFitWidth(32)`, `setFitHeight(20)`, `setPreserveRatio(true)`.
- [x] 4.3 Configurar `countryFlag.setMouseTransparent(true)` para garantizar no interactividad.
- [x] 4.4 Añadir `Tooltip` a `countryFlag` con texto `I18nManager.getString("main.country.tooltip", countryLabel)` (donde `countryLabel` es el
  nombre del país en el idioma activo vía `country.getLabelKey()`); refrescarlo en `refreshTexts()` y al cambiar idioma.
- [x] 4.5 Sustituir `MainController.selectLanguageIcon()` por `selectLanguageText()`: `languageBtn.setGraphic(null)` y
  `languageBtn.setText(I18nManager.getString("main.language." + currentLanguage))`.
- [x] 4.6 Actualizar `onLanguageChanged()` y `refreshTexts()` para invocar `selectLanguageText()` y refrescar el tooltip del país.
- [x] 4.7 Limpiar imports de `FontAwesomeSolid.FLAG` / `FLAG_USA` en `MainController` (si quedan sin uso).
- [x] 4.8 Confirmar que `languageBtn` mantiene el toggle es↔en y la persistencia vía `configManager.updateLanguage(next)`.

## 5. Verificación y build

- [x] 5.1 Ejecutar `./gradlew build` (compila + tests) y confirmar todo en verde.
- [x] 5.2 Ejecutar `./gradlew bootRun` y verificar el flujo end-to-end: borrar `arume.yml`, abrir wizard, comprobar defaults (país OS, idioma
  oficial), cambiar país a Chile, guardar, ventana principal muestra bandera CL + tooltip + botón idioma en texto.
- [x] 5.3 Verificar carga de `arume.yml` pre-existente sin `country` (default `es`).
- [x] 5.4 Validar `openspec validate is21-country-selection-setup` y `openspec status is21-country-selection-setup`.