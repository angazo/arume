## 1. Capa de configuración externa (`arume-app`)

- [x] 1.1 Crear clase `ArumeConfig` (record) en `arume-app` con la estructura: `ArumeDbConfig db` (con campos `type`, `encrypt`) y `SpringDatasourceConfig datasource` (con campos `url`, `driverClassName`, `username`, `password`), usando anotaciones de SnakeYAML
- [x] 1.2 Crear `ConfigManager` en `arume-app` con métodos: `exists()` (comprueba si `arume.yml` existe en el dir del JAR), `load()` (lee y parsea `arume.yml` a `ArumeConfig`), `save(ArumeConfig)` (serializa y escribe `arume.yml`), `resolveJarDir()` (obtiene el directorio del JAR usando `ProtectionDomain`, con fallback a `user.dir`)
- [x] 1.3 Tests unitarios para `ConfigManager` (lectura, escritura, detección de existencia, fallback de directorio)

## 2. Wizard de primera ejecución (`arume-ui`)

- [x] 2.1 Crear `first-run-wizard.fxml` en `arume-ui/src/main/resources/fxml/` con: ComboBox para tipo BBDD (H2 enabled, PostgreSQL disabled), campo de texto para ruta, botón "Examinar", campos usuario/contraseña/confirmar-contraseña, checkbox "Cifrar datos sensibles", botones "Cancelar" y "Guardar"
- [x] 2.2 Crear `FirstRunWizardController` en `arume-ui` anotado con `@Component`: bindings de campos FXML, validación de formulario (usuario no vacío, contraseña >= 12 chars, contraseñas coinciden, ruta no vacía), listener del combo que muestra/oculta campos según tipo seleccionado, apertura de `DirectoryChooser` en botón "Examinar", empaquetado de datos en un DTO `WizardResult` (record)
- [x] 2.3 Definir record `WizardResult` con campos: `dbType`, `storagePath`, `username`, `password`, `encrypt`

## 3. Reestructuración del flujo de arranque (`arume-ui` + `arume-app`)

- [x] 3.1 Refactorizar `ArumeAppFX.ApplicationLoader`: mover `SpringApplication.run()` de `init()` a un nuevo método `startSpringBoot(ArumeConfig)`; `init()` queda vacío
- [x] 3.2 En `start(Stage)`: instanciar `ConfigManager` → comprobar `exists()` → si existe, cargar config y llamar a `startSpringBoot()` directamente; si no existe, mostrar wizard con `showAndWait()`, guardar config, luego `startSpringBoot()`
- [x] 3.3 Si el usuario cancela el wizard, llamar a `Platform.exit()` y terminar la aplicación sin arrancar Spring Boot
- [x] 3.4 Implementar método `configToSystemProperties(ArumeConfig)` en `ConfigManager`
- [x] 3.5 Quitar la configuración de datasource del `application.yml` interno (eliminar `spring.datasource.url` y `spring.datasource.driver-class-name`)

## 4. Integración y verificación

- [x] 4.1 Añadir una migración Flyway dummy `V0.1.0.0__init_placeholder.sql` (solo `SELECT 1`) para verificar que Flyway se ejecuta correctamente en el arranque
- [x] 4.2 Verificar compilación: `./gradlew build` pasa sin errores
- [x] 4.3 Prueba manual: borrar `arume.yml`, arrancar, completar wizard, verificar que `arume.yml` se crea, que `arume.mv.db` aparece en la ruta elegida y que la ventana principal se muestra
- [x] 4.4 Prueba manual: rearrancar sin borrar `arume.yml`, verificar que el wizard NO se muestra y la aplicación arranca directamente
- [x] 4.5 Prueba manual: cancelar el wizard, verificar que la aplicación termina sin crear ficheros
