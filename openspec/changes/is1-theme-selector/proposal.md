## Why

El usuario debe poder elegir el tema visual de la aplicación entre 3 opciones (Claro, Oscuro, Oscuro intenso) tanto en el primer arranque como posteriormente desde el menú principal. Actualmente el tema está hardcodeado a Dracula.

## What Changes

- Añadir campo `theme` al `ArumeConfig` record y persistirlo en `arume.yml`
- Añadir campo `theme` al `WizardResult` y incluirlo en el guardado del wizard
- Añadir un ComboBox de selección de tema en el wizard, en la misma fila que el de idioma
- Añadir un menú "Theme" en la barra de menú principal, a la derecha de "Language"
- Cambio en vivo: al seleccionar un tema, se aplica inmediatamente (wizard y menú)
- Mapeo semántico: light → PrimerLight, dark → Dracula, dark-intense → PrimerDark
- El tema se aplica al arranque desde la configuración persistida

## Capabilities

### New Capabilities
- `theme-selection`: Capacidad de seleccionar y persistir el tema visual de la aplicación entre 3 variantes

### Modified Capabilities
- `first-run-wizard`: El wizard incluye ahora un selector de tema
- `external-configuration`: El esquema de `arume.yml` incluye `arume.theme`

## Impact

- `arume-ui`: Modificaciones en `ArumeAppFX`, `ConfigManager`, `ArumeConfig`, `WizardResult`, `MainController`, wizard FXML y controller, i18n
- `arume-ui/src/main/resources/i18n/`: Nuevas claves de idioma para los temas
- No requiere cambios en BBDD, Flyway, ni módulos `arume-db` o `arume-app`
