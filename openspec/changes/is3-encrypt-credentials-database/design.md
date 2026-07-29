## Context

Actualmente `arume.yml` almacena credenciales en texto plano (`spring.datasource.username`, `spring.datasource.password`) y la URL JDBC sin encriptar. La aplicación usa `ConfigManager` con SnakeYAML para leer/escribir `arume.yml`, y aplica las propiedades como system properties antes de arrancar Spring Boot. Existe un checkbox "Encrypt sensitive data" en el wizard que se persiste como booleano `arume.db.encrypt` pero no tiene efecto real.

El objetivo es cerrar dos vectores de exposición:
1. **Fichero `arume.yml`**: cualquiera con acceso al sistema de ficheros puede leer las credenciales
2. **Ficheros `.mv.db` de H2**: los datos de clientes viajan sin cifrar en disco

## Goals / Non-Goals

**Goals:**
- Eliminar `username`/`password` de `arume.yml` y embeberlos en la URL JDBC
- Encriptar la URL JDBC en `arume.yml` cuando `encrypt=true`, usando clave derivada del sistema de ficheros
- Activar cifrado AES nativo de H2 (`CIPHER=AES`) cuando `encrypt=true`
- Detectar fallos de descifrado (JAR movido de máquina/partición) y ofrecer reconfiguración
- Funcionar en Linux, macOS y Windows con un mecanismo de derivación de clave multiplataforma
- Eliminar la dependencia de jasypt del build

**Non-Goals:**
- Cifrado de otros campos de `arume.yml` (language, theme) — siguen en texto plano
- `AUTO_SERVER=TRUE` — no se aborda en este cambio
- Migración automática de `arume.yml` de formato antiguo — el usuario borra y reconfigura
- Cifrado de BBDD para motores distintos de H2
- Gestión de múltiples usuarios con distintas contraseñas de BBDD

## Decisions

### 1. Algoritmo de cifrado: AES-256/GCM

**Elección**: `AES/GCM/NoPadding` con IV aleatorio de 12 bytes y tag de 128 bits, usando `javax.crypto` directamente.

**Alternativas consideradas**:
- **Jasypt**: ya estaba en el build pero no se usaba. Añade una dependencia externa para una necesidad simple (~30 líneas con `javax.crypto`). Descartado.
- **AES/CBC**: requiere padding y no ofrece autenticación (vulnerable a padding oracle attacks). GCM es AEAD: cifra y autentica en un solo paso.
- **ChaCha20-Poly1305**: excelente pero añade complejidad innecesaria; AES-GCM tiene aceleración hardware en CPUs modernas y es el estándar de facto.

### 2. Derivación de clave: atributos del FileStore

**Elección**: Derivar una clave AES-256 mediante PBKDF2 con salt fijo a partir de un identificador único del sistema de ficheros donde reside el JAR.

**Estrategia multiplataforma** (en orden de preferencia):

| Plataforma | Método primario | Fallback |
|---|---|---|
| Linux | Leer UUID del dispositivo vía `blkid -s UUID -o value <dev>` o resolviendo symlinks en `/dev/disk/by-uuid/` | `FileStore.name()` (ej. `/dev/sda1`) + `FileStore.type()` (ej. `ext4`) |
| macOS | Ejecutar `diskutil info <mountpoint>` y parsear "Volume UUID" | `FileStore.name()` + `FileStore.type()` |
| Windows | `FileStore.getAttribute("volume:vsn")` (volume serial number) | `FileStore.name()` + `FileStore.type()` |
| Fallback universal | Hash de `FileStore.name() + type() + getTotalSpace()` | — |

**Material de clave**:
```
keyMaterial = platformId + ":" + arumeSalt
secretKey   = PBKDF2WithHmacSHA256(keyMaterial, "arume-keygen", 10000, 256)
```

**Racionales**:
- Usar el `FileStore` del directorio del JAR vincula la clave al disco/partición: si el JAR se copia a otra máquina o disco, la clave cambia y el descifrado falla → el usuario debe borrar `arume.yml` y reconfigurar
- El UUID del sistema de ficheros (Linux/macOS) y el volume serial number (Windows) son estables: no cambian al reinstalar la app, solo al reformatear o cambiar de disco
- PBKDF2 con 10000 iteraciones es suficiente para derivar una clave a partir de material de baja entropía; no es una contraseña de usuario, es un identificador de sistema

### 3. Formato del valor encriptado en `arume.yml`

**Elección**: `ENC(<base64>)` donde `<base64>` es la codificación Base64 de `IV (12 bytes) + ciphertext + GCM tag (16 bytes)`.

```
arume.yml:
  spring:
    datasource:
      url: ENC(AQIDBAUGBwgJCgsMDQ4PEBESExQVFhcYGRobHB0eHyA=)
```

**Alternativas consideradas**:
- **Prefijo `{aes-gcm}`** al estilo Spring Security: más verboso pero estándar. `ENC()` es más corto y evoca jasypt (familiaridad).
- **Sin prefijo, forzar siempre cifrado**: rompe la retrocompatibilidad con el flag `encrypt=false` y complica el debugging.

### 4. Detección de URL encriptada vs plain

Cuando `ConfigManager.load()` lee `spring.datasource.url`:
- Si el valor empieza por `ENC(` → descifrar y devolver el texto plano
- Si no → devolver tal cual (compatible con `encrypt=false`)

No se confía en el flag `arume.db.encrypt` para decidir si descifrar; el prefijo en el valor es la fuente de verdad. Esto evita desincronización si alguien edita el YAML a mano.

### 5. Manejo de errores de descifrado

Si `decrypt()` falla (bad padding, bad tag, clave incorrecta por cambio de partición):
1. Se muestra un diálogo de error con el mensaje: "La configuración cifrada no puede descifrarse. Esto ocurre si el JAR se ha movido a otra ubicación o equipo. ¿Desea reconfigurar la aplicación?"
2. Opciones: "Reconfigurar" (borra `arume.yml`, cierra, el usuario debe reiniciar) / "Salir"
3. Esto se implementa en la capa de arranque (antes de Spring Boot), usando JavaFX `Alert`

### 6. Simplificación de `ArumeConfig`

Se eliminan `username` y `password` del record. Las credenciales se pasan a `ConfigManager.buildH2Url()` que las embeberá en la URL. El record queda con 5 campos:

```java
public record ArumeConfig(
    String language,
    String dbType,
    boolean encrypt,
    String url,            // ya incluye USER=...;PASSWORD=...;CIPHER=AES si encrypt=true
    String driverClassName,
    String theme
) {}
```

### 7. Construcción de URL H2

El nuevo método `ConfigManager.buildH2Url(Path storagePath, String username, String password, boolean encrypt)`:

```
Sin encrypt:
  jdbc:h2:file:<path>/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;USER=<user>;PASSWORD=<pass>

Con encrypt:
  jdbc:h2:file:<path>/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;CIPHER=AES;USER=<user>;PASSWORD=<pass>
```

### 8. Ubicación de `EncryptionService`

Se crea en `com.angazo.arume.ui.config` (mismo paquete que `ConfigManager`), ya que:
- La encriptación/desencriptación ocurre en la capa de configuración (antes de Spring Boot)
- `ConfigManager` es su único consumidor
- No depende de Spring

### 9. Flujo de system properties simplificado

`ConfigManager.applyToSystemProperties()` pasa de setear 4 propiedades a solo 2:

```
Antes:                          Después:
  spring.datasource.url           spring.datasource.url         (desencriptada si procede)
  spring.datasource.driver-class  spring.datasource.driver-class
  spring.datasource.username      — eliminada —
  spring.datasource.password      — eliminada —
```

Spring Boot extraerá las credenciales directamente de la URL JDBC (el driver H2 las reconoce como parámetros `USER`/`PASSWORD`).

## Risks / Trade-offs

- **[Riesgo] Clave derivada de FileStore no disponible en todos los SO**: Si `FileStore.name()` devuelve algo poco estable (ej. label de volumen en Windows), el descifrado fallará tras un reinicio. → **Mitigación**: estrategia en cascada con múltiples fallbacks; el último es un hash combinado de name+type+totalSpace que es estable dentro de la misma partición.

- **[Riesgo] `CIPHER=AES` + Flyway en BD nueva**: Al activar cifrado H2 en una BD que ya existe sin cifrar, H2 no puede "cifrar sobre la marcha" — habría que exportar/reimportar. Pero en la práctica, el usuario que active `encrypt=true` lo hará en el wizard inicial, con una BD vacía. Si alguien edita `arume.yml` a mano para añadir `CIPHER=AES` a una BD existente, H2 fallará al conectar. → **Mitigación**: documentar en el diálogo de error. Non-goal para este change.

- **[Trade-off] El flag `encrypt` es binario y global**: O todo cifrado o nada. En el futuro se eliminará el flag y el cifrado será obligatorio. Por ahora es aceptable como mecanismo de transición.

- **[Riesgo] Pérdida de datos si el usuario olvida la contraseña de BBDD**: Con `CIPHER=AES`, la contraseña ES la clave de cifrado del fichero. Si se pierde, los datos son irrecuperables. → **Mitigación**: esto es inherente al cifrado; el wizard ya exige contraseña de ≥12 caracteres. Futuro: sistema de backup de clave.

- **[Trade-off] `arume.yml` parcialmente legible**: `language`, `theme`, `db.type` y `db.encrypt` siguen en texto plano. Solo la URL está cifrada. Es un equilibrio entre seguridad y depurabilidad.
