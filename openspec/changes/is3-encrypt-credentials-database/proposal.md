## Why

Actualmente `arume.yml` almacena credenciales de base de datos en texto plano y los ficheros H2 no están cifrados. Cualquier acceso no autorizado al fichero de configuración o a los ficheros `.mv.db` expone datos sensibles de clientes. Este cambio introduce cifrado en ambas capas: la URL JDBC con credenciales se encripta en `arume.yml` usando una clave derivada de la partición donde reside el JAR, y los ficheros H2 se protegen con cifrado AES nativo.

## What Changes

- **Eliminar `username` y `password` del registro `ArumeConfig`** y de `arume.yml`. Las credenciales se embeberán directamente en la URL JDBC (`;USER=...;PASSWORD=...`)
- **Encriptar el valor de `spring.datasource.url` en `arume.yml`** cuando `encrypt=true`, usando AES-256/GCM con clave derivada de atributos del sistema de ficheros donde se ejecuta el JAR. El valor encriptado se almacena con formato `ENC(<base64>)`. **BREAKING**: el formato de `arume.yml` cambia; las instalaciones existentes deben borrar `arume.yml` y reconfigurar
- **Activar `CIPHER=AES` en la URL JDBC de H2** cuando `encrypt=true`, para que los ficheros `.mv.db` se almacenen cifrados
- **Eliminar la dependencia de jasypt** del build (no se usaba; se reemplaza por `javax.crypto`)
- **Nuevo diálogo de error** cuando el descifrado de la URL falla (por cambio de máquina/partición), con opción de reconfigurar

## Capabilities

### New Capabilities
- `configuration-encryption`: Servicio de cifrado/descifrado de valores de configuración mediante AES-256/GCM, con clave derivada de atributos del `FileStore` del directorio del JAR. Detección multiplataforma del identificador de partición (Linux: UUID vía `/dev/disk/by-uuid/`, macOS: UUID vía `diskutil`, Windows: volume serial number vía `FileStore.getAttribute("volume:vsn")`, fallback: hash de `FileStore.name() + type + totalSpace`)

### Modified Capabilities
- `external-configuration`: El requisito de almacenamiento en texto plano se reemplaza por almacenamiento cifrado opcional. Los campos `spring.datasource.username` y `spring.datasource.password` se eliminan de `arume.yml`; las credenciales van embebidas en la URL JDBC
- `database-bootstrap`: La construcción de la URL H2 incluye ahora `CIPHER=AES` cuando `encrypt=true`, y las credenciales se embeberán como parámetros `USER`/`PASSWORD` en la propia URL. El puente a system properties deja de setear `spring.datasource.username` y `spring.datasource.password`

## Impact

- `ArumeConfig` record: eliminados `username`, `password` (5 campos en vez de 7)
- `ConfigManager`: nuevo método `buildH2Url(Path, String, String, boolean)`; `save()` encripta URL si `encrypt=true`; `load()` desencripta URL si detecta prefijo `ENC(`; `applyToSystemProperties()` deja de setear username/password
- `arume.yml`: formato cambia (sin `username`/`password`, URL encriptada si procede)
- `EncryptionService` (nuevo): clase con `encrypt()`, `decrypt()`, `isEncrypted()`, y derivación de clave
- `FirstRunWizardController` / `WizardResult` / `ArumeAppFX.buildConfigFromWizard()`: sin cambios en la UI; `buildConfigFromWizard()` pasa credenciales a `buildH2Url()` en vez de al constructor de `ArumeConfig`
- `ConfigManagerTest`: actualizar para nuevo formato y nuevo comportamiento de cifrado
- `EncryptionServiceTest` (nuevo): tests de cifrado/descifrado y derivación de clave
- `libs.versions.toml`: eliminada entrada jasypt (plugin + versión)
- `arume-app/build.gradle`: eliminado plugin jasypt
- i18n (`messages*.properties`): nuevos mensajes para el diálogo de error de descifrado
