## Why

Arume todavía no tiene una prueba automatizada de interfaz gráfica. Esto deja sin protección el primer flujo visual de la aplicación: la configuración inicial mediante el wizard. El cambio incorpora esa primera prueba ahora, antes de que la interfaz y sus flujos crezcan, para detectar regresiones en el FXML, el controlador y la interacción del usuario.

## What Changes

- Añadir TestFX 4.0.18 con soporte para JUnit 5 en el módulo `arume-ui`.
- Crear `FirstRunWizardUiTest` usando la extensión `ApplicationExtension` y `FxRobot`.
- Cargar el FXML real `first-run-wizard.fxml` mediante `FXMLLoader`.
- Probar una configuración válida introduciendo ruta temporal, usuario y contraseñas.
- Verificar el `WizardResult` generado y el cierre de la ventana después de guardar.
- Mantener la prueba aislada de `ArumeAppFX`, Spring Boot, el datasource real y el `arume.yml` del usuario.
- Preparar la ejecución de las pruebas UI en integración continua mediante Xvfb.
- Mantener documentadas las convenciones para futuras pruebas de interfaz.

## Capabilities

### New Capabilities

No se introduce una capability funcional nueva. Este cambio incorpora infraestructura y cobertura de pruebas sobre una capability existente.

### Modified Capabilities

No se modifican requisitos funcionales de `first-run-wizard`. La capability existente ya define el comportamiento que debe probarse.

Este change usa `skip_specs: true` porque no cambia el comportamiento de la aplicación ni añade requisitos funcionales; únicamente añade una prueba automatizada y su infraestructura.

## Impact

- `src/arume-ui/build.gradle`: nueva dependencia de test de TestFX.
- `src/arume-ui/src/test/java/`: nuevo test funcional de interfaz.
- `src/arume-ui/src/main/resources/fxml/first-run-wizard.fxml`: posible incorporación de identificadores CSS estables para los controles utilizados por TestFX.
- `.github/workflows/project-ci.yml`: ejecución de la compilación dentro de Xvfb para proporcionar una pantalla virtual a JavaFX.
- `docs/test-JavaFX.md`, `AGENTS.md` y `openspec/config.yaml`: documentación de la estrategia de pruebas UI.

La prueba utilizará directorios temporales y no debe crear ni modificar la configuración persistente del usuario.
