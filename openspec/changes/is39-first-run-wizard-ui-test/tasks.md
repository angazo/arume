## 1. Integración de TestFX

- [x] 1.1 Añadir las versiones y librerías de TestFX 4.0.18 y Hamcrest al Version Catalog de Gradle.
- [x] 1.2 Añadir TestFX como dependencia de test del módulo `arume-ui` y conservar la ejecución con JUnit Platform.

## 2. Selectores estables del wizard

- [x] 2.1 Añadir identificadores CSS explícitos a los campos y botones que intervienen en el flujo válido del wizard, manteniendo sus `fx:id` existentes.
- [x] 2.2 Comprobar que los identificadores no dependen de los textos traducidos ni entran en conflicto dentro de la escena.

## 3. Primera prueba funcional de interfaz

- [x] 3.1 Crear `FirstRunWizardUiTest` en el paquete de pruebas UI de `arume-ui` usando `ApplicationExtension`, `@Start` y `FxRobot`.
- [x] 3.2 Cargar `/fxml/first-run-wizard.fxml` con `FXMLLoader`, aplicar la hoja de estilos real, inicializar el idioma de forma determinista y preparar la ruta bajo `@TempDir`.
- [x] 3.3 Automatizar el flujo válido escribiendo la ruta, el usuario y las contraseñas mediante selectores CSS estables, y pulsar el botón de guardado.
- [x] 3.4 Verificar `isSaved()`, todos los campos relevantes de `WizardResult` y que el `Stage` se cierra tras guardar.
- [x] 3.5 Garantizar que la prueba no arranca `ArumeAppFX` ni Spring Boot, no usa la configuración persistente del usuario y deja cerradas las ventanas creadas.

## 4. Ejecución en integración continua

- [x] 4.1 Modificar `project-ci.yml` para ejecutar `./gradlew build` dentro de `xvfb-run` con una pantalla virtual de 1920x1080x24.
- [x] 4.2 Ejecutar `./gradlew :arume-ui:test` en una sesión gráfica local y corregir los problemas de carga, interacción o sincronización que aparezcan.
- [x] 4.3 Ejecutar la compilación completa mediante Xvfb y verificar que la prueba UI funciona junto con las pruebas unitarias y de integración existentes.
