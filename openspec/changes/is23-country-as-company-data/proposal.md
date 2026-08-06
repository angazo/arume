## Why

Arume quiere permitir a un usuario gestionar la facturación/contabilidad de empresas de **distintos países** (una misma instalación, varias
empresas). El país es un atributo de la empresa, no de la aplicación: elegirlo en el arranque y mostrarlo como bandera global en la barra superior
es un modelo incorrecto a futuro. Se elimina esa selección del primer arranque y el indicador de bandera; la bandera reaparecerá en el futuro
ligada a la **empresa activa**. Los PNGs de banderas se conservan como recursos inertes para ese futuro.

## What Changes

- **BREAKING** — Se elimina el enum `Country` de `arume-ui` (catálogo fijo de 7 países). El catálogo canónico de países pasa a ser
  exclusivamente la tabla `t1_countries` de la BBDD (introducida en is26).
- **BREAKING** — Se elimina el selector de país del wizard de primer arranque (combo de país). La fila superior vuelve a **Idioma → Tema** y el
  idioma por defecto vuelve a derivar de la detección del OS (`I18nManager.detectDefaultLanguage()`), no del idioma oficial del país detectado.
  El wizard se estrecha a la anchura previa a is21 (~494px).
- **BREAKING** — Se elimina `arume.country` de la configuración: `ArumeConfig` pierde el campo `country`, `ConfigManager` deja de leerlo/escribirlo
  y `WizardResult` pierde el campo. Un `arume.yml` preexistente con `arume.country` sigue cargando sin error (la clave se ignora al leer y
  desaparece del fichero en el siguiente guardado).
- Se elimina el indicador de bandera de la barra superior de la ventana principal (`countryFlag` en `main.fxml`, `MainController.setupCountryFlag()`
  y el tooltip `main.country.tooltip`).
- Se eliminan las claves i18n `wizard.country`, `wizard.country.<code>` y `main.country.tooltip` de los bundles.
- Se eliminan los tests del feature: `CountryTest`, los casos de `country` en `ConfigManagerTest` y las comprobaciones de claves de país en
  `CountryResourcesTest`.
- Se conservan los **7 PNGs de banderas** en `resources/icons/flags/` como recursos inertes para la futura feature de empresa activa, protegidos
  por un nuevo test de recursos (`FlagResourcesTest`) que verifica su existencia, dimensiones 96×72 y proporción 4:3.
- No hay cambios en `arume-db` (la tabla `t1_countries` y su catálogo se mantienen intactos) ni migraciones Flyway.

## Capabilities

### New Capabilities
_Ninguna._

### Modified Capabilities
- `country-selection`: **capability eliminada por completo** — se marcan como REMOVED todos sus requisitos (catálogo de países, detección por
  defecto, resolución de códigos, etiquetas i18n, mapeo idioma-oficial, persistencia `arume.country`, indicador de bandera y assets PNG). Tras el
  archive la spec queda vacía y se elimina.
- `first-run-wizard`: se elimina la selección de país del wizard; el idioma por defecto vuelve a la detección del OS; la anchura vuelve a acomodar
  dos selectores en la fila superior (Idioma → Tema).
- `ide-window-chrome`: se elimina el indicador de bandera de la barra de título de la ventana principal.
- `internationalization`: la detección de idioma por defecto deja de derivar del país y vuelve al `Locale` del OS; se eliminan las referencias al
  país en la persistencia y en la posición del botón de idioma.

## Impact

- **`arume-ui` (código)**:
  - `FirstRunWizardController`: eliminar combo/etiqueta de país, default de idioma por OS, `resolveCountryCode()`, bloque de `refreshTexts()`.
  - `MainController`: eliminar `countryFlag`, `countryFlagTooltip`, `loadConfiguredCountry()`, `setupCountryFlag()`, `updateCountryFlagTooltip()`.
  - `ArumeAppFX`: `runWizardFlow()` usa `I18nManager.detectDefaultLanguage()`; `buildConfigFromWizard()` deja de propagar el país; `Scene` del wizard
    a 494×840.
  - `WizardResult`, `ArumeConfig`: eliminar el campo `country`.
  - `ConfigManager`: eliminar lectura/escritura de `arume.country` y su propagación en `updateLanguage`/`updateTheme`.
- **`arume-ui` (recursos)**:
  - `main.fxml` y `first-run-wizard.fxml`: eliminación de nodos y ajuste de layout/anchuras.
  - i18n: eliminar `wizard.country.*` y `main.country.tooltip` de `messages.properties`, `messages_en.properties` y `messages_es.properties`.
  - PNGs de banderas en `resources/icons/flags/` se conservan sin cambios.
- **`arume-ui` (tests)**: eliminar `CountryTest`, `CountryResourcesTest`; actualizar `ConfigManagerTest`; crear `FlagResourcesTest`.
- **`arume-db`**: sin cambios. **`arume-app`**: sin cambios.
- **Compatibilidad**: `arume.yml` existentes con `arume.country` cargan sin error y la clave se autolimpia al guardar.
