## Context

Actualmente Spring Boot arranca dentro del método `init()` de `ApplicationLoader` (JavaFX), antes de que se muestre ninguna ventana. La URL de datasource está hardcodeada en `application.yml` como `jdbc:h2:mem:arume`. No hay mecanismo para que el usuario configure la persistencia.

Necesitamos un flujo donde JavaFX tome el control primero, detecte si hay configuración previa, muestre un wizard si no la hay, y solo entonces arranque Spring Boot con la configuración adecuada.

## Goals / Non-Goals

**Goals:**
- Detectar primera ejecución comprobando la existencia de `arume.yml` en el directorio del JAR.
- Mostrar un wizard JavaFX que recoja: tipo de BBDD (H2/PostgreSQL), ruta de almacenamiento, usuario y contraseña.
- Persistir la configuración en `arume.yml` junto al JAR.
- Arrancar Spring Boot con la configuración de datasource leída de `arume.yml`.
- Proteger el acceso a la BBDD H2 con credenciales definidas por el usuario.
- Dejar espacio en el wizard para PostgreSQL (deshabilitado) y cifrado de datos sensibles (checkbox sin efecto en esta iteración).

**Non-Goals:**
- Implementar PostgreSQL real (solo placeholder en UI).
- Implementar cifrado de datos en `arume.yml` (solo checkbox visual).
- Modo multiusuario con `AUTO_SERVER=TRUE`.
- Validación de credenciales contra una BBDD existente (solo se validan reglas sintácticas: no vacío, coincidencia de contraseñas, longitud mínima de 12 caracteres).
- Migración desde una configuración previa (no hay nada que migrar aún).

## Decisions

### 1. Diferir Spring Boot en lugar de rearrancar

**Alternativa considerada:** Iniciar Spring Boot con una BBDD temporal, mostrar wizard, guardar config, rearrancar contexto.

**Decisión:** No arrancar Spring Boot hasta que la configuración esté disponible. El wizard es JavaFX puro y no necesita beans de Spring.

**Justificación:** Evita la complejidad de cerrar y recrear un `ApplicationContext` con JavaFX en medio. El `init()` de JavaFX queda vacío; toda la lógica va en `start()`.

### 2. Pasar configuración a Spring Boot vía System.setProperty()

**Alternativa considerada:** Usar `SpringApplication.setDefaultProperties()` o `spring.config.additional-location`.

**Decisión:** Leer `arume.yml`, extraer `spring.datasource.*`, llamar a `System.setProperty(...)` antes de `SpringApplication.run()`.

**Justificación:** Las propiedades de sistema tienen la máxima precedencia (salvo command-line args). Simple, sin dependencias extras. `application.yml` interno no llevará datasource, evitando conflictos.

### 3. Localización del JAR para `arume.yml`

**Decisión:** Usar `getProtectionDomain().getCodeSource().getLocation().toURI()` sobre la clase `ArumeApp` para obtener la ruta del JAR, y resolver `arume.yml` en el mismo directorio.

**Justificación:** Funciona independientemente de cómo se lance la aplicación (doble clic, `java -jar`, script con `cd` previo). Alternativas como `user.dir` dependen del directorio de trabajo, que puede no coincidir.

### 4. Formato YAML para la configuración externa

**Decisión:** `arume.yml` usa el mismo formato que Spring Boot (`spring.datasource.*`) más una sección propia `arume.db.*` para metadatos.

**Justificación:** Familiaridad con el ecosistema Spring, SnakeYAML ya está en el classpath. La sección `arume.db.*` permite añadir campos propios (tipo de BBDD, flags de cifrado) sin colisionar con namespaces de Spring.

### 5. Wizard como Stage modal, no como FileChooser nativo

**Decisión:** Ventana JavaFX con FXML + controlador propio, mostrada con `Stage.showAndWait()` desde `start()` de `ApplicationLoader`.

**Justificación:** Permite un diseño rico con combo de selección de motor, campos de credenciales, checkbox de cifrado. Extensible a futuro. `showAndWait()` bloquea el hilo de JavaFX hasta que el usuario cierra el wizard, permitiendo un flujo secuencial limpio.

### 6. Estructura del wizard

**Alternativa considerada:** Un solo FXML con visibilidad condicional de paneles según tipo de BBDD.

**Decisión:** Un solo FXML con todos los campos visibles, pero los campos específicos se habilitan/deshabilitan según selección. PostgreSQL aparece en el combo como `disabled`.

**Justificación:** Más simple que paneles intercambiables. Para dos opciones (una deshabilitada), no merece la pena complejidad adicional.

### 7. Dependencia entre módulos

**Decisión:** La lógica de lectura/escritura de `arume.yml` va en `arume-app` (contiene SnakeYAML de Spring Boot). El wizard FXML + controlador va en `arume-ui`. La detección de first-run se hace en `ArumeAppFX`, que coordina ambos.

**Justificación:** `arume-app` es el módulo de configuración y arranque. `arume-ui` contiene todas las vistas. La coordinación en `ArumeAppFX` (módulo `arume-ui`) es natural porque JavaFX controla el flujo de ventanas.

## Risks / Trade-offs

- **[Riesgo]**: Si el usuario borra `arume.yml`, la app vuelve a mostrar el wizard. Si el usuario apunta a una BBDD distinta, Flyway aplicará migraciones sobre una BBDD vacía → inconsistencia silenciosa.
  - **Mitigación**: A futuro, añadir un flag `arume.db.initialized: true` y detección de BBDD ya existente. Fuera de scope para este change.

- **[Riesgo]**: `getProtectionDomain().getCodeSource()` puede devolver `null` en entornos no estándar (ej. classloader custom, módulos JPMS).
  - **Mitigación**: Fallback a `user.dir` si la detección del JAR falla. Loguear warning.

- **[Riesgo]**: El wizard se ejecuta en el JavaFX Application Thread. Si la validación o escritura del fichero bloquea, la UI se congela.
  - **Mitigación**: Las operaciones son triviales (validación de campos, escritura de un YAML pequeño). No se justifica async en esta iteración.

- **[Trade-off]**: PostgreSQL deshabilitado en el combo puede confundir a usuarios que esperen usarlo.
  - **Mitigación**: Mostrar texto "(próximamente)" junto a la opción. Clara indicación visual de que no está disponible aún.

- **[Trade-off]**: Leer y escribir `arume.yml` manualmente en lugar de usar `@ConfigurationProperties`.
  - **Justificación**: `@ConfigurationProperties` requiere un `ApplicationContext` activo, que no existe antes del wizard. En arranques posteriores (cuando el contexto ya está activo), podríamos migrar. Para este change, lectura manual con SnakeYAML es suficiente.
