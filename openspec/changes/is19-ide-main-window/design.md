## Context

La ventana principal actual es un `BorderPane` con solo un `ToolBar` en la zona `top` que contiene dos `MenuButton`s (idioma y tema). El resto de zonas (`left`, `right`, `center`, `bottom`) están vacías. La ventana usa las decoraciones nativas del SO. Necesitamos transformarla en un shell tipo IDE.

## Goals / Non-Goals

**Goals:**
- Ventana sin decoraciones nativas (`StageStyle.UNDECORATED`) con barra de título propia que soporte arrastre, minimizar, maximizar y cerrar.
- Sidebar izquierda con iconos de navegación (`ToggleButton` en `ToggleGroup`) y Settings fijado abajo.
- Sidebar derecha con botón de Help arriba.
- Barra de estado inferior con indicador de conexión H2 (punto verde/rojo).
- Área central `StackPane` para cambio de vistas según navegación.
- Menú Help con opción "About..." → diálogo modal con info de la app.
- Botones de idioma (bandera) y tema (icono) en la title bar, reemplazando los `MenuButton` actuales.
- Iconos profesionales vía Ikonli (FontAwesome5 + MaterialDesign2).

**Non-Goals:**
- Menús contextuales por vista (File, Edit, etc.) — se añadirán en el futuro cuando cada vista lo necesite.
- Sidebars colapsables — se mantienen siempre visibles.
- Redimensionamiento de ventana desde bordes custom — solo min/max/close por ahora.
- Tool windows flotantes o anclables.
- Los paneles del `center` son placeholders vacíos, no vistas funcionales.

## Decisions

### 1. Layout: `BorderPane` como contenedor raíz

Se mantiene `BorderPane` porque encaja perfectamente con la estructura deseada:

```
BorderPane
  top    → TitleBar (HBox)
  left   → LeftSidebar (VBox)
  center → ContentArea (StackPane)
  right  → RightSidebar (VBox)
  bottom → StatusBar (HBox)
```

Alternativa considerada: `GridPane`. Descartada porque `BorderPane` gestiona automáticamente el resize de la zona central y el ancho/alto de los bordes. Con `GridPane` habría que bindear manualmente columnas y filas.

### 2. `StageStyle.UNDECORATED` en `ArumeAppFX.replaceWithMainScene()`

Se aplica justo antes de cargar el `main.fxml`, para que la ventana pierda las decoraciones nativas antes de mostrar el nuevo layout. En `main.fxml` se define un tamaño inicial mayor (1200×800) para aprovechar la ausencia de bordes del SO.

El arrastre de ventana se implementa con listeners de mouse en el nodo de la title bar:
- `MOUSE_PRESSED` → guardar offset (`stage.getX() - event.getScreenX()`, `stage.getY() - event.getScreenY()`)
- `MOUSE_DRAGGED` → `stage.setX/Y(event.getScreenX/Y() + offset)`
- Doble click en title bar → `stage.setMaximized(!stage.isMaximized())`

El `Stage` se pasa al `MainController` vía método setter después de la carga del FXML (el controlador es Spring `@Component`, por lo que no se puede pasar en el constructor de FXMLLoader).

### 3. Title bar: HBox con iconos Ikonli

Estructura del HBox:

```
[ImageView/logo] [MenuButton "Help"] [spacer Region Hgrow] [Button lang] [Button theme] [Button min] [Button max] [Button close]
```

- **Logo**: `ImageView` cargando un PNG/SVG desde resources (mismo icono de la app).
- **Help**: `MenuButton` con un `MenuItem` "About...". Al hacer click → abre el `AboutDialog`.
- **Lang button**: `Button` con un `FontIcon` de bandera. Click → rota entre `en`/`es`. El icono cambia a la bandera correspondiente.
- **Theme button**: `Button` con `FontIcon` de sol/luna. Click → rota entre los 3 temas. El icono cambia según el tema activo.
- **Window buttons**: `Button` con `FontIcon` de minimizar (dash), maximizar (square/restore), cerrar (X). Estilo: plano con `-fx-background-color: transparent`. Close tiene clase CSS `window-close` para hover rojo.

### 4. Sidebar izquierda: VBox con `ToggleButton` + `ToggleGroup`

```java
ToggleGroup navGroup = new ToggleGroup();

ToggleButton dashboardBtn = new ToggleButton();
dashboardBtn.setGraphic(FontIcon(MDI_DESKTOP_MONITOR));
dashboardBtn.setToggleGroup(navGroup);

ToggleButton invoicesBtn = ...;    // MDI_RECEIPT
ToggleButton accountingBtn = ...;  // MDI_BOOKKEEPING

Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

Button settingsBtn = new Button(); // MDI_COG (fuera del ToggleGroup)
```

Los `ToggleButton` de navegación tienen ancho fijo (~48px), texto debajo del icono (`setContentDisplay(ContentDisplay.BOTTOM)`) o solo icono con tooltip. El `ToggleGroup` garantiza selección exclusiva.

El botón Settings está fuera del `ToggleGroup`: al pulsarlo, deselecciona cualquier ToggleButton del grupo (Settings no es una "vista" más del mismo nivel, es configuración).

La navegación se implementa con un listener en el `ToggleGroup.selectedToggleProperty()` que llama al método `switchView(viewId)` del controlador, que intercambia el nodo visible en el `StackPane` del center.

### 5. Sidebar derecha: VBox con botón Help

Misma anchura fija (~48px). Botón Help con icono `MDI_HELP_CIRCLE` arriba, spacer debajo. Al click → mismo comportamiento que Help → About del menú de la title bar (abre `AboutDialog`).

### 6. Center: StackPane para cambio de vistas

```java
StackPane contentArea = new StackPane();
contentArea.getChildren().addAll(
    dashboardPane,    // inicialmente visible
    invoicesPane,     // oculto
    accountingPane,   // oculto
    settingsPane      // oculto
);
```

Cada pane es un `BorderPane` o `VBox` con un `Label` placeholder. El cambio se hace con `pane.setVisible(true/false)` y `pane.setManaged(true/false)` para que el `StackPane` ignore los no visibles en el layout.

### 7. Status bar: HBox con indicador H2

```java
HBox statusBar = new HBox();
Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

Circle dbDot = new Circle(4);  // verde = conectado, rojo = desconectado
Label dbLabel = new Label("H2");
HBox dbIndicator = new HBox(4, dbDot, dbLabel);

statusBar.getChildren().addAll(spacer, dbIndicator);
```

El estado de conexión se consulta vía `DataSource` de Spring (`springContext.getBean(DataSource.class).getConnection().isValid(2)`). Si no hay contexto Spring aún o falla la conexión → rojo. Se actualiza periódicamente o bajo demanda.

### 8. Ikonli: dependencias e integración

Se añaden 3 dependencias a `libs.versions.toml`:

```toml
[versions]
ikonli = "12.3.1"

[libraries]
ikonli-javafx = { module = "org.kordamp.ikonli:ikonli-javafx", version.ref = "ikonli" }
ikonli-fontawesome5 = { module = "org.kordamp.ikonli:ikonli-fontawesome5-pack", version.ref = "ikonli" }
ikonli-materialdesign2 = { module = "org.kordamp.ikonli:ikonli-materialdesign2-pack", version.ref = "ikonli" }
```

En `arume-ui/build.gradle` se añaden las 3 dependencias.

Uso típico:
```java
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;

FontIcon icon = new FontIcon(FontAwesomeSolid.GLOBE);
icon.setIconSize(18);
Button langBtn = new Button();
langBtn.setGraphic(icon);
```

### 9. CSS personalizado sobre AtlantaFX

Se crea `src/arume-ui/src/main/resources/css/arume.css` con estilos mínimos que extienden AtlantaFX usando sus variables CSS:

```css
.title-bar {
    -fx-background-color: -color-bg-default;
    -fx-padding: 0 8 0 8;
    -fx-min-height: 36;
}

.title-bar .button {
    -fx-background-color: transparent;
    -fx-padding: 4 10;
}

.title-bar .button:hover {
    -fx-background-color: -color-neutral-muted;
}

.title-bar .window-close:hover {
    -fx-background-color: #e81123;
    -fx-text-fill: white;
}

.sidebar {
    -fx-background-color: -color-bg-subtle;
    -fx-min-width: 48;
    -fx-padding: 4 0;
}

.status-bar {
    -fx-background-color: -color-bg-subtle;
    -fx-border-color: -color-border-default;
    -fx-border-width: 1 0 0 0;
    -fx-min-height: 24;
    -fx-padding: 0 8;
}
```

Se carga en `replaceWithMainScene()` después de aplicar el theme: `scene.getStylesheets().add(getClass().getResource("/css/arume.css").toExternalForm())`.

### 10. Diálogo "About"

Se implementa como un `Stage` modal (`Modality.APPLICATION_MODAL`) creado programáticamente en el controlador (no vía FXML por simplicidad). Contenido:
- `VBox` con logo (`ImageView`), nombre "Arume", versión, descripción corta, botón "Close".
- La versión se obtiene de `getClass().getPackage().getImplementationVersion()` (rellenado por Gradle al empaquetar con `bootJar`).

### 11. Refactor de MainController

El `MainController` actual se reescribe sustancialmente:

| Campo actual | Destino |
|---|---|
| `MenuButton languageButton` + `RadioMenuItem`s | `Button langButton` con flag FontIcon |
| `MenuButton themeButton` + `MenuItem`s | `Button themeButton` con sun/moon FontIcon |
| `Region toolBarSpacer` | Se elimina (la title bar usa su propio spacer) |

Nuevas responsabilidades:
- `@FXML` fields para todos los nodos del nuevo layout (title bar, sidebars, status bar, center).
- Método `setStage(Stage)` inyectado por `ArumeAppFX` para los listeners de arrastre y window controls.
- Método `switchView(String viewId)` para cambiar el panel visible del `StackPane`.
- Método `initWindowDrag(Stage)` para configurar arrastre de la title bar.
- Método `showAboutDialog()` para el diálogo About.
- Mantener la lógica de cambio de idioma y tema (persistencia + aplicación), adaptada a los nuevos `Button`s.

## Risks / Trade-offs

- **[Linux/Wayland] Ventanas `UNDECORATED` pueden no ser arrastrables**: en Wayland las coordenadas de ventana no siempre son accesibles. Mitigación: probar en X11 (Plasma) donde sí funciona; para Wayland se puede caer en `StageStyle.DECORATED` como fallback detectando la plataforma.
- **[AtlantaFX upgrade] Cambio de versión de AtlantaFX podría romper las variables CSS usadas**: mitigación: limitar el CSS a variables bien documentadas (`-color-bg-default`, `-color-neutral-muted`, `-color-border-default`) que son estables.
- **[Ikonli] Una dependencia más**: Ikonli es mantenido activamente (12.x es la línea actual), tiene buen soporte para JavaFX y Java 21+. No se prevén problemas de compatibilidad.
- **[Window resize] Sin bordes de resize custom, el usuario depende de maximizar**: aceptable como primera iteración. En Linux/X11, muchas veces `UNDECORATED` sigue permitiendo resize nativo en los bordes invisibles de la ventana.
