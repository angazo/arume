## 1. Infraestructura i18n (`arume-ui`)

- [x] 1.1 Crear `I18nManager` en `com.angazo.arume.ui.i18n` como singleton estático con: `init(language)`, `setLanguage(language)`, `getString(key)`, `getCurrentLanguage()`, `detectDefaultLanguage()`, `onLanguageChange(Runnable)`. `getString` captura `MissingResourceException` y devuelve `!key!`.
- [x] 1.2 Implementar `detectDefaultLanguage()`: si `Locale.getDefault().getLanguage()` es `es` → `"es"`; si es `ca`/`gl`/`eu` y el país es `ES` → `"es"`; resto → `"en"`.
- [x] 1.3 Crear `arume-ui/src/main/resources/i18n/messages.properties` (bundle inglés, default) con todas las claves necesarias para wizard y ventana principal.
- [x] 1.4 Crear `arume-ui/src/main/resources/i18n/messages_es.properties` (bundle español) con todos los textos traducidos. Todas las claves del bundle inglés deben estar presentes.
- [x] 1.5 Tests unitarios para `I18nManager`: detección de idioma (es, ca+ES, gl+ES, eu+ES, fr, en, ca+FR), carga de bundles, cambio de idioma, notificación de listeners, fallback de clave inexistente.

## 2. Actualización de modelos de configuración (`arume-ui`)

- [x] 2.1 Añadir campo `String language` a `ArumeConfig` (record), como primer campo.
- [x] 2.2 Añadir campo `String language` a `WizardResult` (record), como primer campo.
- [x] 2.3 Actualizar `ConfigManager.save()`: escribir `arume.language` en el YAML bajo la clave `arume`.
- [x] 2.4 Actualizar `ConfigManager.load()`: leer `arume.language` del YAML. Si no existe (backward compat), usar `"en"` como valor por defecto.
- [x] 2.5 Añadir método `ConfigManager.updateLanguage(String language)` que lee el fichero, actualiza solo el campo `arume.language`, y guarda.
- [x] 2.6 Actualizar tests de `ConfigManagerTest` para cubrir el nuevo campo (save con language, load con language, load sin language → default "en").

## 3. Actualización del wizard de primer arranque (`arume-ui`)

- [x] 3.1 Modificar `first-run-wizard.fxml`: añadir ComboBox `languageCombo` al inicio (antes de dbTypeCombo). Eliminar todos los atributos `text` hardcodeados de labels y botones. Añadir `fx:id` a todos los Labels: `dbTypeLabel`, `h2SettingsLabel`, `storagePathLabel`, `credentialsLabel`, `usernameLabel`, `passwordLabel`, `confirmPasswordLabel`. Los `fx:id` de botones, combo y checkbox ya existen.
- [x] 3.2 Actualizar `FirstRunWizardController`: añadir campo `languageCombo` y los nuevos labels. En `initialize()`, poblar el combo con "English" y "Español", seleccionar el idioma actual de `I18nManager`, y llamar a `refreshTexts()`.
- [x] 3.3 Implementar método `refreshTexts()` en el controlador: asigna todos los textos de labels, botones, combo items, checkbox, y prompt del usernameField desde `I18nManager`. Registra un listener en `I18nManager.onLanguageChange()` que vuelve a llamar `refreshTexts()`.
- [x] 3.4 Añadir listener al `languageCombo` que al cambiar llame a `I18nManager.setLanguage()`, lo cual dispara el listener y refresca todos los textos.
- [x] 3.5 Actualizar `validateForm()`: usar `I18nManager.getString()` para los mensajes de validación en lugar de strings hardcodeados.
- [x] 3.6 Actualizar `onSave()`: incluir el idioma seleccionado (`I18nManager.getCurrentLanguage()`) en el `WizardResult`.
- [x] 3.7 Actualizar `onBrowse()`: usar `I18nManager.getString()` para el título del `DirectoryChooser`.
- [x] 3.8 Actualizar `showAlert()`: usar `I18nManager.getString()` para el título de la alerta.

## 4. Actualización del flujo de arranque (`arume-ui`)

- [x] 4.1 En `ArumeAppFX.ApplicationLoader.start()`: si `configManager.exists()`, inicializar `I18nManager.init(config.language())`. Si no existe, inicializar `I18nManager.init(I18nManager.detectDefaultLanguage())`.
- [x] 4.2 Pasar el idioma por defecto al wizard controller: `controller.setDefaultLanguage(I18nManager.getCurrentLanguage())` (si es necesario; si no, el combo ya se inicializa con el valor de `I18nManager`).
- [x] 4.3 En `buildConfigFromWizard()`: pasar el campo `language` del `WizardResult` al `ArumeConfig`.
- [x] 4.4 Tras guardar config, usar el idioma guardado para actualizar el título de la ventana principal (`primaryStage.setTitle(...)`).

## 5. Ventana principal con cambio de idioma (`arume-ui`)

- [x] 5.1 Modificar `main.fxml`: añadir `MenuBar` con un `Menu` para idioma que contenga dos `RadioMenuItem` ("English", "Español") en un `ToggleGroup`. Añadir `fx:id` a los nodos relevantes: `languageMenu`, `englishItem`, `spanishItem`.
- [x] 5.2 Actualizar `MainController`: implementar `initialize()` que selecciona el `RadioMenuItem` correspondiente al idioma actual de `I18nManager`. Añadir listener de cambio de idioma que refresca el texto del menú y la selección.
- [x] 5.3 Añadir handlers `onEnglishSelected` / `onSpanishSelected` que llamen a `I18nManager.setLanguage()` y luego a `ConfigManager.updateLanguage()` para persistir el cambio.
- [x] 5.4 Registrar `MainController` como listener de `I18nManager.onLanguageChange()` para refrescar el texto del menú de idioma cuando el idioma cambie (por si en el futuro hay otro punto de cambio).

## 6. Integración y verificación

- [x] 6.1 Verificar compilación: `./gradlew build` pasa sin errores.
- [x] 6.2 Verificar tests: `./gradlew test` pasa todos los tests (nuevos y existentes).
- [x] 6.3 Prueba manual: borrar `arume.yml`, arrancar, verificar que el wizard se muestra en el idioma del SO. Cambiar idioma en el combo del wizard y verificar refresco inmediato de todos los textos. Completar wizard y verificar que `arume.yml` contiene `arume.language`.
- [x] 6.4 Prueba manual: rearrancar sin borrar config, verificar que la ventana principal se muestra en el idioma guardado y que el menú de idioma refleja la selección correcta.
- [x] 6.5 Prueba manual: cambiar idioma desde el menú de la ventana principal, verificar que el menú se actualiza y que `arume.yml` se actualiza con el nuevo valor de `arume.language`.
- [x] 6.6 Prueba manual: rearrancar tras cambiar idioma desde la ventana principal, verificar que el nuevo idioma persiste.
