## Why

La ventana principal de la aplicación actualmente es un `BorderPane` casi vacío con un `ToolBar` superior que aloja menús de idioma y tema. Necesitamos una interfaz con aspecto de IDE moderno (tipo IntelliJ IDEA / VS Code) que sirva como shell para toda la navegación futura de la aplicación, con barra de título personalizada, sidebars con iconos grandes, barra de estado y capacidad de cambiar vistas en el área central.

## What Changes

- **Nueva barra de título personalizada**: ventana sin decoraciones nativas (`StageStyle.UNDECORATED`), con logo de la app, menú Help → About, botones de idioma y tema con iconos, y controles de ventana (minimizar, maximizar, cerrar).
- **Sidebar izquierda de navegación**: `VBox` con botones de tipo `ToggleButton` en un `ToggleGroup` (Dashboard, Invoices, Accounting) e icono de Settings fijado abajo.
- **Sidebar derecha**: `VBox` con botón de Help arriba y spacer.
- **Barra de estado inferior**: `HBox` con indicador de conexión a base de datos (punto verde + "H2").
- **Área central con vistas intercambiables**: `StackPane` que alterna paneles según la selección en la barra lateral.
- **Diálogo "About"**: ventana modal con logo, nombre, versión y descripción de la app.
- **Nueva dependencia Ikonli**: `ikonli-javafx` + `ikonli-fontawesome5-pack` y `ikonli-materialdesign2-pack` para iconos profesionales.
- **CSS complementario a AtlantaFX**: estilos mínimos para title bar, sidebars y status bar que usan variables de AtlantaFX.
- **Modificación de los specs de `theme-selection` e `internationalization`**: el widget de UI pasa de `MenuButton` en un `ToolBar` a `Button` con icono en la title bar. La semántica de persistencia y aplicación de idioma/tema no cambia.

## Capabilities

### New Capabilities
- `ide-window-chrome`: ventana sin decoraciones nativas, barra de título personalizada con logo, menú Help → About, botones de idioma y tema con iconos, y controles de ventana (minimizar, maximizar, cerrar). Soporte para arrastre de ventana.
- `sidebar-navigation`: barra lateral izquierda con botones de navegación (`ToggleButton` en `ToggleGroup`) para cambiar vistas en el área central, y botón de Settings fijado en la parte inferior.
- `right-sidebar`: barra lateral derecha con botón de Help en la parte superior y spacer.
- `status-bar`: barra inferior con indicador de conexión a base de datos (H2, estado conectado/desconectado).
- `about-dialog`: diálogo modal con información de la aplicación (logo, nombre, versión, descripción).
- `icon-library`: integración de Ikonli con packs FontAwesome5 y MaterialDesign2 para iconos vectoriales en toda la UI.

### Modified Capabilities
- `theme-selection`: el widget de cambio de tema se traslada del `MenuButton` en el `ToolBar` superior a un `Button` con icono en la barra de título personalizada. Los requisitos de persistencia, aplicación inmediata y mapeo de temas no cambian.
- `internationalization`: el widget de cambio de idioma se traslada del `MenuButton` en el `ToolBar` superior a un `Button` con icono de bandera en la barra de título personalizada. Los requisitos de persistencia, detección de SO y cambio en runtime no cambian.

## Impact

- **Dependencias nuevas**: `org.kordamp.ikonli:ikonli-javafx:12.3.1`, `org.kordamp.ikonli:ikonli-fontawesome5-pack:12.3.1`, `org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1` en `arume-ui/build.gradle`. Actualizar `libs.versions.toml`.
- **Ficheros afectados**: `main.fxml` (reestructuración completa del layout), `MainController.java` (ampliación significativa), `ArumeAppFX.java` (añadir `StageStyle.UNDECORATED` + listeners de arrastre), `arume-ui/src/main/resources/` (nuevo CSS, posibles SVG/recursos de logo).
- **Sin impacto** en `arume-app` ni `arume-db`. El wizard de primer arranque no se modifica.
