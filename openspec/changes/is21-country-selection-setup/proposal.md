## Why

Arume aspira a usarse en distintos países. Hoy el setup inicial solo captura idioma y tema, pero no el país, que es la dimensión que condicionará
formatos contables, divisas, fiscalidad futura y otros aspectos de negocio. Necesitamos pedir el país en el primer arranque, mostrarlo de forma
permanente en la ventana principal y **desacoplarlo del idioma de la UI**: alguien puede vivir en un país y preferir la interfaz en otro idioma.
Aprovechamos este change para corregir la identificación actual idioma↔bandera (que no es correcta para países no hispanos anglófonos) y reemplazarla
por el nombre del idioma en texto.

## What Changes

- **Nuevo selector de país en el wizard inicial**, colocado en la primera fila junto a idioma y tema, en ese orden (País → Idioma → Tema). El wizard
  se ensancha para acomodar los tres combos sin comprimir.
- **Países soportados**: España (ES), Reino Unido (GB), Estados Unidos (US), Chile (CL), Singapur (SG), Australia (AU), Sudáfrica (ZA). Codificación
  en `arume.yml` mediante **ISO-2** en minúsculas (clave `arume.country`).
- **Idioma oficial por país** persistido como recurso de la app (mapeo estático ISO-2 → código de idioma). Al abrir el wizard el combo de idioma toma
  por defecto el idioma oficial del país detectado; el usuario puede cambiarlo libremente.
- **Detección de país por defecto** a partir de `Locale.getDefault().getCountry()` cuando esté entre los soportados; fallback a `ES`.
- **Persistencia y propagación del país** en `WizardResult`, `ArumeConfig` y `ConfigManager` (load/save/updateLanguage/updateTheme). Compatibilidad
  hacia atrás: una `arume.yml` preexistente sin `country` se carga con valor por defecto `ES`.
- **Barra superior de la ventana principal**:
  - Se añade un **indicador no interactivo** con la bandera (PNG) del país elegido y tooltip i18n, situado antes del botón de idioma.
  - El **botón de idioma** deja de mostrar una bandera (`FontAwesomeSolid.FLAG` / `FLAG_USA`) y pasa a mostrar el **nombre del idioma activo** en texto
    (p. ej. "English" / "Español"), manteniendo el toggle es↔en y la persistencia en `arume.yml`.
- El país **no es editable** tras el setup (Non-Goal de este change; futura edición se abordaría borrando `arume.yml` o vía un diálogo de Ajustes).

## Capabilities

### New Capabilities
- `country-selection`: Captura del país en el primer arranque, persistencia, detección por defecto, idioma oficial asociado, e indicador de bandera
  en la barra superior de la ventana principal.

### Modified Capabilities
- `first-run-wizard`: Nuevo requisito de selección de país; ajuste del layout de la primera fila (País → Idioma → Tema) y ensanchado del wizard.
- `internationalization`: Eliminación del icono de bandera en el botón de idioma de la barra superior; el botón pasa a mostrar el nombre del idioma
  en texto. El idioma por defecto del wizard pasa a derivar del país detectado (vía recurso de idiomas oficiales), no directamente del `Locale` del OS.
- `ide-window-chrome`: El botón de idioma de la barra superior pasa a mostrar texto en lugar de icono de bandera; se añade un indicador de país
  (bandera, no interactivo) en la barra.

## Impact

- **`arume-ui` (controladores / FXML)**:
  - `FirstRunWizardController`: nuevo combo de país, listener para reajustar el combo de idioma al idioma oficial del país al cambiar de país, ensanchado
    del wizard en `ArumeAppFX.showFirstRunWizard`, propagación en `onSave`.
  - `FirstRunWizardController` y `MainController`: ajustes de `refreshTexts` y reorganización del FXML superior.
  - `MainController`: nuevo `countryIndicator` (ImageView no interactivo con tooltip i18n); `selectLanguageIcon()` se reemplaza por un método que
    fija el texto del idioma activo (`selectLanguageText()`); carga de la bandera PNG correspondiente al país de la config.
  - `main.fxml` y `first-run-wizard.fxml`: actualización de nodos y layout.
- **Configuración (`arume-ui/config`)**:
  - `WizardResult` y `ArumeConfig` añaden `String country`.
  - `ConfigManager`: load (default `ES`), save, `updateLanguage`/`updateTheme` propagan `country`.
- **Recursos i18n**: nuevas claves para etiquetas de selección de país, nombres de país por idioma, tooltip del indicador, y nombre del idioma activo
  para el botón; ambos bundles (`messages.properties`, `messages_es.properties`) se mantienen sincronizados.
- **Recursos gráficos**: PNGs 32×20 de bandera en `arume-ui/src/main/resources/icons/flags/<iso2>.png` (es, gb, us, cl, sg, au, za).
- **Recurso de datos**: mapeo estático ISO-2 → idioma oficial (p. ej. archivo `countries.yml` en `arume-ui/src/main/resources/` o clase enum
  `Country` con el mapeo; decisión técnica en `design.md`).
- **No requiere migraciones Flyway** (el país se persiste en `arume.yml`, no en H2).
- **Compat hacia atrás**: `arume.yml` sin `country` se carga con `ES`.
- **No hay cambios en `arume-db` ni en `arume-app`** salvo propagación de campos en flujos de arranque existentes en `ArumeAppFX`.