## 1. Configuración: eliminar el país de la persistencia

- [x] 1.1 `ArumeConfig`: eliminar el campo `country` del record y su default `"esp"` del constructor compacto.
- [x] 1.2 `WizardResult`: eliminar el campo `country`.
- [x] 1.3 `ConfigManager.load()`: eliminar la lectura de `arume.country` (el `getOrDefault("country", "esp")`); la clave sobrante en `arume.yml` se ignora.
- [x] 1.4 `ConfigManager.save()`: eliminar `arume.put("country", ...)`.
- [x] 1.5 `ConfigManager.updateLanguage(String)` y `updateTheme(String)`: reconstruir `ArumeConfig` sin `country`.
- [x] 1.6 `ConfigManagerTest`: quitar el primer argumento (país) de todas las construcciones de `ArumeConfig`; eliminar los tests de país
  (`shouldDefaultToSpainWhenCountryMissing`, `shouldSaveAndLoadCountry`, `shouldUpdateLanguagePreservingCountry`, `shouldUpdateThemePreservingCountry`)
  y la aserción de `country()` en `shouldDefaultToEnglishWhenLanguageMissing`; añadir test de carga tolerante de un `arume.yml` que aún contenga
  `arume.country`.

## 2. Wizard: quitar la selección de país

- [x] 2.1 `first-run-wizard.fxml`: eliminar la `VBox` de país (countryLabel + countryCombo) de la fila superior; dejar los combos Idioma (180) → Tema (150); ajustar `prefWidth` del root a ~494.
- [x] 2.2 `ArumeAppFX.showFirstRunWizard`: cambiar la `Scene` del wizard de `600×840` a `494×840`.
- [x] 2.3 `FirstRunWizardController`: eliminar los campos `countryCombo`/`countryLabel` y el bloque de inicialización del combo de país
  (bucle `Country.values()` y `Country.detectDefault()`).
- [x] 2.4 Default del combo de idioma: sustituir `Country.detectDefault().officialLanguage()` por `I18nManager.detectDefaultLanguage()`.
- [x] 2.5 `FirstRunWizardController.refreshTexts()`: eliminar el bloque de refresco del combo de país.
- [x] 2.6 `FirstRunWizardController.onSave()`: construir `WizardResult` sin `country`; eliminar `resolveCountryCode()` y el import de `Country`.
- [x] 2.7 `ArumeAppFX.runWizardFlow()`: `I18nManager.init(I18nManager.detectDefaultLanguage())` en lugar de `Country.detectDefault().officialLanguage()`.
- [x] 2.8 `ArumeAppFX.buildConfigFromWizard()`: eliminar `result.country()`.

## 3. Ventana principal: quitar la bandera

- [x] 3.1 `main.fxml`: eliminar el `ImageView fx:id="countryFlag"`.
- [x] 3.2 `MainController`: eliminar el campo `countryFlag`, `countryFlagTooltip` y los métodos `loadConfiguredCountry()`, `setupCountryFlag()`,
  `updateCountryFlagTooltip()`.
- [x] 3.3 `MainController`: eliminar las llamadas a esos métodos en `initialize()`, `onLanguageChanged()` y `refreshTexts()`.
- [x] 3.4 `MainController`: limpiar imports sin uso (`Country`, `Tooltip`; conservar `Image`/`ImageView` usados por el logo).

## 4. i18n y recursos

- [x] 4.1 `messages.properties`: eliminar `wizard.country`, `wizard.country.<code>` (7 claves) y `main.country.tooltip`.
- [x] 4.2 `messages_es.properties`: eliminar las mismas claves.
- [x] 4.3 `messages_en.properties`: eliminar las mismas claves.
- [x] 4.4 Conservar sin cambios los 7 PNGs en `arume-ui/src/main/resources/icons/flags/` (esp, gbr, usa, chl, sgp, aus, zaf).
- [x] 4.5 Eliminar `CountryTest` y `CountryResourcesTest`.
- [x] 4.6 Crear `FlagResourcesTest` (en `com.angazo.arume.ui.config`): con lista hardcodeada de los 7 códigos, verificar que `/icons/flags/<code>.png`
  existe, mide 96×72 y cumple la proporción 4:3.

## 5. Verificación y build

- [x] 5.1 Ejecutar `./gradlew build` (compila + tests) y confirmar todo en verde.
- [ ] 5.2 Ejecutar `./gradlew bootRun`: borrar `arume.yml`, abrir el wizard y comprobar que no hay combo de país, idioma por defecto según OS, guardar y
  ver la ventana principal sin bandera y con botones idioma/tema operativos.
- [ ] 5.3 Verificar compatibilidad: arrancar con un `arume.yml` preexistente que contenga `arume.country` → carga sin error y sin bandera; tras un
  cambio de idioma o tema la clave desaparece del fichero.
- [x] 5.4 Grep de restos en `src/`: sin referencias a `Country`, `countryFlag`, `arume.country` ni `wizard.country`/`main.country.tooltip`.
- [x] 5.5 Ejecutar `openspec validate is23-country-as-company-data` y `openspec status is23-country-as-company-data` en verde.
