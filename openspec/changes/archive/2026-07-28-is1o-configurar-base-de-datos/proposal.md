## Why

La aplicación arranca actualmente con una base de datos H2 en memoria (`jdbc:h2:mem:arume`), lo que significa que los datos se pierden al cerrar la aplicación. Es necesario que el usuario pueda configurar una base de datos persistente antes de empezar a usar el aplicativo. Este es el primer paso funcional real del proyecto: sin persistencia, ninguna feature posterior (facturación, contabilidad) tiene sentido.

## What Changes

- **BREAKING**: Se elimina la configuración de datasource en memoria de `application.yml`. Sin `arume.yml` externo, la aplicación no arrancará Spring Boot (mostrará el wizard en su lugar).
- Nuevo mecanismo de detección de primera ejecución: se comprueba si existe `arume.yml` en el directorio del JAR. Si no existe, se muestra el wizard.
- Nuevo wizard de configuración inicial como ventana JavaFX, que permite seleccionar tipo de base de datos (H2 habilitado, PostgreSQL deshabilitado con indicador "próximamente"), ruta de almacenamiento, usuario y contraseña. Incluye opción de cifrado (solo visible, funcionalidad futura).
- Reestructuración del flujo de arranque: Spring Boot se difiere hasta que la configuración externa está disponible, en lugar de arrancar en `init()` de JavaFX.
- Nuevo fichero `arume.yml` con la configuración de base de datos, almacenado junto al JAR de la aplicación.
- H2 pasa a modo fichero en lugar de memoria, con credenciales definidas por el usuario para proteger el acceso.

## Capabilities

### New Capabilities

- `first-run-wizard`: Ventana JavaFX de configuración inicial que se muestra en el primer arranque para recoger tipo de BBDD, ruta de almacenamiento, usuario y contraseña.
- `external-configuration`: Gestión del fichero `arume.yml` junto al JAR — detección de existencia, lectura y escritura de la configuración de base de datos.
- `database-bootstrap`: Arranque diferido de Spring Boot con la configuración de datasource leída de `arume.yml`, permitiendo que Flyway cree o actualice el esquema automáticamente.

### Modified Capabilities

Ninguna — no hay capabilities existentes en el baseline.

## Impact

- **`arume-app`**: `application.yml` pierde la definición de datasource por defecto. `ArumeApp` se modifica para aceptar configuración externa como propiedades de sistema antes de lanzar Spring Boot.
- **`arume-ui`**: Nueva ventana FXML + controlador para el wizard. `ArumeAppFX` se reestructura para diferir Spring Boot y mostrar el wizard condicionalmente.
- **`arume-db`**: Sin cambios estructurales; `DatabaseConfiguration` permanece igual.
- **Dependencias nuevas**: Ninguna. Se usa JavaFX estándar para el wizard y SnakeYAML (incluido en Spring Boot) para leer/escribir `arume.yml`.
- **Flyway**: Las migraciones se aplicarán automáticamente al arrancar Spring Boot con la BBDD configurada, sin cambios en la configuración de Flyway.
