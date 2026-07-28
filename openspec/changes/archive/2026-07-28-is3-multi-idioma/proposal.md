## Why

La aplicación está actualmente hardcodeada en español: todos los textos de la UI (etiquetas, botones, mensajes de validación, títulos de ventana) son strings literales en castellano. Para que la aplicación sea accesible a usuarios de otras regiones y para sentar las bases de una arquitectura preparada para múltiples idiomas, necesitamos una capa de internacionalización (i18n) que permita al usuario elegir entre inglés y español, con detección automática del idioma del sistema operativo como valor por defecto.

## What Changes

- Nueva infraestructura i18n: clase `I18nManager` (singleton) que gestiona bundles de recursos (`messages.properties` y `messages_es.properties`) y permite cambio de idioma en caliente notificando a los controladores.
- **`arume.yml`** incorpora un nuevo campo `arume.language` que persiste la preferencia de idioma entre sesiones.
- **Wizard de primer arranque** enriquecido con un selector de idioma (ComboBox "English"/"Español") al inicio del formulario. El cambio de idioma en el combo refresca todos los textos de la ventana inmediatamente.
- **Ventana principal** (`main.fxml`) añade una barra de menú con opción de cambio de idioma, permitiendo al usuario alternar entre inglés y español desde la propia aplicación en cualquier momento.
- Todos los textos del wizard y de la ventana principal se extraen de los bundles de i18n y se establecen programáticamente desde los controladores, eliminando los textos hardcodeados del FXML.
- **Detección de idioma por defecto**: al arrancar por primera vez, se detecta el locale del SO. Si el idioma es español, o si es una lengua cooficial del estado español (catalán, gallego, euskera) con país España, el idioma por defecto es español. En cualquier otro caso, inglés.

## Capabilities

### New Capabilities

- `internationalization`: Sistema de internacionalización con ResourceBundle de Java que permite mostrar la UI en inglés o español, con detección automática del idioma del SO y cambio de idioma en caliente desde la aplicación.

### Modified Capabilities

- `first-run-wizard`: El wizard de primera ejecución incluye ahora un selector de idioma al inicio del formulario. Todos los textos del wizard (etiquetas, botones, mensajes de validación) responden al idioma seleccionado.
- `external-configuration`: El fichero `arume.yml` incluye ahora el campo `arume.language` con el código de idioma seleccionado (`en` o `es`), que se persiste y se lee en arranques posteriores.

## Impact

- **`arume-ui`**: Nuevo paquete `com.angazo.arume.ui.i18n` con `I18nManager`. Nuevos ficheros de bundle `i18n/messages.properties` y `i18n/messages_es.properties`. FXMLs (`first-run-wizard.fxml`, `main.fxml`) pierden textos hardcodeados y ganan `fx:id` para asignación programática. Controladores (`FirstRunWizardController`, nuevo `MainController`) se actualizan para usar `I18nManager`.
- **`arume-app`**: Sin cambios, salvo que `ArumeApp` recibe indirectamente el idioma a través de `arume.yml`.
- **`arume-db`**: Sin cambios.
- **Dependencias nuevas**: Ninguna. Se usa `java.util.ResourceBundle` (JDK estándar).
- **`ArumeConfig`**: Añade campo `language` (String).
- **`WizardResult`**: Añade campo `language` (String).
- **`ConfigManager`**: `save()` y `load()` manejan el nuevo campo `arume.language` en el YAML.
- **Tests**: Nuevos tests para `I18nManager` (detección de idioma, carga de bundles, cambio de idioma).
