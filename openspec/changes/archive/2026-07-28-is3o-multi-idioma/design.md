## Context

Actualmente la aplicación tiene todos los textos de UI hardcodeados en español en FXMLs y controladores. No existe ningún mecanismo de internacionalización. El wizard de primer arranque (implementado en `is1`) se muestra en español fijo, sin opción de cambio.

Necesitamos que el usuario pueda elegir idioma (inglés/español) desde el primer arranque y también cambiarlo en caliente desde la aplicación. La detección automática del idioma del SO debe cubrir español y lenguas cooficiales de España con fallback a inglés.

El ecosistema es JavaFX + Spring Boot, con un arranque híbrido donde JavaFX se inicia primero y Spring Boot se difiere hasta que la configuración externa (`arume.yml`) está disponible. Esto implica que la capa i18n debe funcionar **antes** de que Spring Boot esté activo (para el wizard de primer arranque) y también **después** (para la ventana principal y futuras vistas).

## Goals / Non-Goals

**Goals:**
- Sistema de bundles de recursos con `ResourceBundle` de Java (sin dependencias externas).
- Dos idiomas iniciales: inglés (`en`) y español (`es`).
- Detección automática del idioma del SO: `es` → español; `ca`/`gl`/`eu` con país `ES` → español; resto → inglés.
- Selector de idioma en el wizard de primer arranque, con cambio en caliente (refresca todos los textos del wizard al cambiar el combo).
- Selector de idioma en la ventana principal (barra de menú), permitiendo alternar idioma en cualquier momento.
- Persistencia del idioma elegido en `arume.yml` (campo `arume.language`).
- Todos los textos visibles se establecen programáticamente desde los controladores (no via FXML `%key`), para facilitar el refresco en caliente.

**Non-Goals:**
- Más de dos idiomas (la arquitectura lo soporta, pero solo se crean bundles para EN y ES).
- Formateo de números/fechas/monedas según locale (fuera de scope).
- Internacionalización en ventanas modales que no sean el wizard (fuera de scope; se hará cuando esas ventanas se creen).
- Traducción de mensajes de log o excepciones (los logs permanecen en inglés como establece la convención del proyecto).

## Decisions

### 1. `I18nManager` como singleton estático (no bean Spring)

**Alternativa considerada:** Crear un bean Spring `@Component` para `I18nManager`.

**Decisión:** Singleton Java estático con inicialización explícita, sin depender del contexto de Spring.

**Justificación:** El wizard de primer arranque se ejecuta antes de que Spring Boot arranque. En ese momento no hay `ApplicationContext`. Un singleton estático funciona en ambas fases (pre-Spring y post-Spring). Los controladores posteriores (que sí son beans Spring) pueden usar el singleton igualmente.

### 2. Notificación de cambio de idioma por listener (no eventos de JavaFX)

**Alternativa considerada:** Usar `Observable`/`Property` de JavaFX para propagar cambios de idioma.

**Decisión:** Una lista simple de `Runnable` listeners. `I18nManager.onLanguageChange(Runnable)` registra callbacks que se ejecutan al cambiar idioma. Cada controlador registra un callback que refresca sus textos.

**Justificación:** Más simple, sin dependencia de JavaFX en la capa i18n. Los controladores JavaFX pueden llamar a `Platform.runLater()` dentro de su listener si necesitan estar en el FX thread (aunque `I18nManager.setLanguage()` ya debería llamarse desde el FX thread).

### 3. Ubicación de los bundles: `arume-ui/src/main/resources/i18n/`

**Decisión:** `messages.properties` (inglés, default) y `messages_es.properties` en el directorio `i18n/` dentro de los recursos de `arume-ui`.

**Justificación:** `arume-ui` es el módulo que contiene toda la UI y es donde se consumen los textos. `ResourceBundle.getBundle("i18n/messages", locale)` carga desde el classpath, accesible desde cualquier módulo.

### 4. FXML sin textos hardcodeados; asignación programática en controlador

**Alternativa considerada:** Usar `%key` en FXML + `FXMLLoader.setResources()` para carga inicial, y override programático solo en cambios de idioma en caliente.

**Decisión:** Eliminar todos los atributos `text` del FXML (excepto prompts, que no son parte de i18n). Añadir `fx:id` a todos los nodos con texto visible. En `initialize()`, el controlador llama a un método `refreshTexts()` que asigna todos los textos desde `I18nManager`. Al cambiar idioma, se vuelve a llamar a `refreshTexts()`.

**Justificación:** Fuente única de verdad: todos los textos vienen del bundle. No hay discrepancia entre lo que muestra el FXML y lo que gestiona el controlador. En modo desarrollo, si un bundle no tiene una clave, el FXML muestra el nodo sin texto (en vez de mostrar texto incorrecto). Además, `%key` no soporta cambio de ResourceBundle en caliente sin recargar el FXML completo.

### 5. El combo de idioma muestra el nombre del idioma en su **propio** idioma

**Decisión:** "English" y "Español" se muestran en el combo con esos literales fijos. No se traducen dinámicamente (no tendría sentido: si el usuario no entiende el idioma actual, necesita ver el nombre de su idioma en su propio idioma para identificarlo).

**Justificación:** Es la práctica estándar en selectores de idioma (cada opción se muestra en el idioma que representa). Esto implica que "English" y "Español" son valores display fijos, no claves de bundle.

### 6. Campo `arume.language` en YAML con código ISO 639-1

**Decisión:** El campo se llama `arume.language` y almacena `en` o `es`.

**Justificación:** Códigos de dos letras estándar. Compatible con `Locale.forLanguageTag()`. Más simple que locales completos con país (no necesitamos distinguir `es_ES` vs `es_MX` en esta iteración).

### 7. `ConfigManager` gestiona el campo `language` pero no la inicialización de `I18nManager`

**Decisión:** `ConfigManager` lee/escribe `arume.language` como parte del YAML. `ArumeAppFX` es responsable de leer el idioma de la config (o detectarlo del SO) e inicializar `I18nManager.init()`.

**Justificación:** Separación de responsabilidades. `ConfigManager` es serialización/deserialización de YAML. `I18nManager` es gestión de bundles. `ArumeAppFX` orquesta el arranque y decide qué idioma usar.

### 8. Menú de idioma en la ventana principal con RadioMenuItem

**Decisión:** Añadir una `MenuBar` a `main.fxml` con un menú "Language" / "Idioma" que contiene `RadioMenuItem` para "English" y "Español". El item seleccionado refleja el idioma actual.

**Justificación:** Patrón UI estándar para cambio de idioma (similar a aplicaciones como IntelliJ, Eclipse). `RadioMenuItem` en un `ToggleGroup` da feedback visual claro del idioma activo. El texto del menú mismo ("Language"/"Idioma") también debe actualizarse al cambiar idioma.

## Risks / Trade-offs

- **[Riesgo]**: Si `ResourceBundle.getBundle()` no encuentra una clave en ningún bundle, lanza `MissingResourceException` y la UI puede romperse.
  - **Mitigación**: `I18nManager.getString()` captura la excepción y devuelve `!key!` como indicador visible de clave faltante. En tests, verificar que todas las claves existen en ambos bundles.

- **[Riesgo]**: La llamada a `ConfigManager.save()` para actualizar solo el idioma desde la ventana principal requiere leer el fichero completo, modificar un campo, y reescribir. Si el fichero se corrompe en el proceso, se pierde la configuración de BBDD.
  - **Mitigación**: Añadir método `ConfigManager.updateLanguage(String language)` que lee, modifica solo el campo language, y escribe. Operación atómica en la práctica (el fichero es pequeño). En el futuro se podría añadir escritura atómica con fichero temporal + rename.

- **[Trade-off]**: La detección de lenguas cooficiales (`ca`/`gl`/`eu` + país `ES` → español) asume que un usuario con locale catalán en España prefiere español. Esto es una simplificación razonable como default, pero el usuario puede cambiar a inglés en el wizard si lo desea.

- **[Trade-off]**: El menú de idioma en `main.fxml` es el primer elemento de UI "funcional" de la ventana principal. Establece el patrón de `MenuBar` que futuras features (archivo, edición, ayuda) deberán seguir.

- **[Trade-off]**: Usar singleton estático en lugar de inyección de dependencias para `I18nManager` va contra la filosofía Spring, pero es necesario por el arranque híbrido (pre-Spring). En el futuro, si todos los controladores son beans Spring, se podría migrar `I18nManager` a bean con scope singleton y eliminar el estático.
