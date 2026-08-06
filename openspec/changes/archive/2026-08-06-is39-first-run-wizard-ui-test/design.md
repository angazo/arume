## Context

El módulo `arume-ui` ya contiene las vistas FXML y los controladores del wizard, además de pruebas unitarias ejecutadas con JUnit 5. Todavía no tiene una dependencia ni una infraestructura para pruebas JavaFX.

La prueba se ejecutará contra JavaFX 25. En desarrollo puede utilizar el `DISPLAY` de la sesión gráfica. En la CI Linux se proporcionará una pantalla virtual mediante Xvfb. No se utilizará `openjfx-monocle` 21 porque el proyecto usa JavaFX 25 y no se ha verificado la compatibilidad entre ambas versiones.

## Goals / Non-Goals

**Goals:**

- Integrar TestFX 4.0.18 con el sistema de dependencias existente basado en Gradle Version Catalogs.
- Cargar `first-run-wizard.fxml` y ejecutar `FirstRunWizardController` sin Spring Boot.
- Automatizar un flujo válido completo mediante `ApplicationExtension` y `FxRobot`.
- Mantener la prueba aislada con `@TempDir` y un estado de idioma conocido.
- Utilizar selectores CSS estables para los controles interactivos del escenario.
- Permitir que `./gradlew build` ejecute la prueba en GitHub Actions dentro de Xvfb.

**Non-Goals:**

- Cambiar el comportamiento funcional del wizard.
- Probar el arranque completo de `ArumeAppFX` o la integración con Spring Boot.
- Abrir una base de datos H2 real o guardar un `arume.yml` durante la prueba.
- Cubrir en este change todos los errores de validación, el selector de directorios, la navegación principal o los diálogos.
- Añadir Monocle o crear una solución headless específica para JavaFX 25.

## Decisions

### Usar TestFX con la extensión de JUnit 5

Se añadirá `org.testfx:testfx-junit5:4.0.18` como dependencia de test del módulo `arume-ui`, junto con Hamcrest como dependencia explícita de compilación porque la API de TestFX expone tipos `org.hamcrest` en sus métodos de interacción. La prueba utilizará `ApplicationExtension`, `@Start` y `FxRobot`, que es el patrón recomendado por TestFX para JUnit 5.

Se descartan JemmyFX y el Robot de bajo nivel de JavaFX porque no ofrecen una ventaja necesaria para este escenario y exigirían más código de infraestructura.

### Mantener la prueba en `arume-ui`

El test se ubicará en `src/arume-ui/src/test/java/com/angazo/arume/ui/ui/FirstRunWizardUiTest.java`. La vista, el controlador y los recursos que necesita pertenecen a `arume-ui`; no se debe mover la prueba a `arume-app` ni arrancar el módulo de aplicación para probarla.

### Cargar el FXML real

El método `@Start` cargará `/fxml/first-run-wizard.fxml` con `FXMLLoader`, añadirá la hoja de estilos real `/css/arume.css` y mostrará el resultado en el `Stage` que proporciona TestFX.

El controlador se obtendrá con `loader.getController()`. El test utilizará su API pública `setDefaultStoragePath`, `getResult` e `isSaved` para preparar y comprobar el flujo, sin acceder a campos privados mediante reflexión.

### Usar una configuración válida y temporal

Antes de cargar el FXML se inicializará `I18nManager` con inglés para que el test no dependa del locale del sistema. La ruta de almacenamiento se construirá bajo `@TempDir` y se introducirán valores de prueba con contraseñas de al menos doce caracteres.

El caso principal escribirá en los controles de ruta, usuario, contraseña y contraseña de cifrado, pulsará el botón de guardado y comprobará el `WizardResult`, el estado `saved` y que el `Stage` deja de estar mostrando la ventana.

No se pulsará el botón de exploración de directorios: `DirectoryChooser` es un diálogo nativo y no forma parte del objetivo del primer caso.

### Añadir identificadores CSS explícitos

Los controles usados por el test recibirán un atributo `id` estable además de su `fx:id`, por ejemplo `id="saveButton"`. Como mínimo se identificarán los campos de entrada y los botones de guardado y cancelación.

El test localizará los controles mediante `#storagePathField`, `#usernameField`, `#passwordField`, `#dbEncryptPasswordField` y `#saveButton`, evitando depender de textos traducibles.

### Ejecutar la CI dentro de Xvfb

El paso de compilación de `.github/workflows/project-ci.yml` se envolverá con:

```bash
xvfb-run --auto-servernum --server-args="-screen 0 1920x1080x24" ./gradlew build
```

Xvfb proporciona a JavaFX un servidor gráfico virtual sin necesitar un monitor físico. Se prefiere esta solución a mezclar una distribución de Monocle para JavaFX 21 con una aplicación que utiliza JavaFX 25.

### No usar esperas fijas

Las acciones se realizarán mediante `FxRobot` y las comprobaciones se harán después de cada interacción. No se añadirá `Thread.sleep()` para sincronizar el test. Si una futura funcionalidad asíncrona lo requiere, deberá incorporar una condición observable o una espera específica y acotada.

## Risks / Trade-offs

- **[La prueba puede fallar en una máquina sin servidor gráfico]** → documentar la necesidad de `DISPLAY` en local y ejecutar la CI dentro de Xvfb.
- **[TestFX 4.0.18 es una librería con soporte legacy]** → limitar inicialmente el uso a la API estable de JUnit 5 y reevaluar la herramienta si aparecen incompatibilidades con JavaFX 25.
- **[Los listeners estáticos de `I18nManager` pueden sobrevivir entre pruebas]** → el primer test fijará el idioma antes de cargar el controlador y no cambiará el idioma durante el escenario; los futuros tests deberán controlar explícitamente ese estado global.
- **[Los identificadores CSS pueden divergir del `fx:id`]** → declarar ambos identificadores de forma explícita en el FXML y utilizar el `id` CSS en los selectores TestFX.
- **[Los cambios en la estructura del FXML pueden romper los selectores]** → mantener identificadores estables y probar comportamientos de usuario, no clases concretas de layout ni posiciones de píxel.
- **[Xvfb puede no estar disponible en una imagen futura del runner]** → verificar la herramienta en CI y añadir su instalación si `ubuntu-latest` deja de incluirla.
