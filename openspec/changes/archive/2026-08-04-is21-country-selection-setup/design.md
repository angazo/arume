## Context

El primer arranque de Arume (hoy recogido por el spec `first-run-wizard`) pide idioma, tema y datos de la BBDD H2. La configuración se persiste en
`arume.yml` dentro del directorio del JAR (`ConfigManager`), y los DTO/records involucrados son `WizardResult` y `ArumeConfig`, ambos inmwidus;

La UI principal (`ide-window-chrome`) muestra en la barra superior un logo, menú Help, y botones de **idioma** (con icono de bandera `FontAwesomeSolid.FLAG`
o `FLAG_USA`) y de **tema** (sol/luna), más los controles de ventana.

El botón de idioma actual mezcla dos conceptos que la issue #21 quiere separar:
- **País**: dimensión financiera (divisa, formatos, fiscalidad futura). Se elige una vez y no es editable después.
- **Idioma de la UI**: preferencia personal, conmutable en cualquier momento.

Esto requiere tocar tres capabilities (`first-run-wizard`, `internationalization`, `ide-window-chrome`) y crear una cuarta (`country-selection`).

Stack relevante: JavaFX 25 + AtlantaFX, Ikonli (FontAwesome5 / MaterialDesign2), SnakeYAML para `arume.yml`, `I18nManager` como singleton estático con
listeners de cambio de idioma. El wizard carga el FXML `first-run-wizard.fxml` (anchura actual 494px) y `main.fxml` define la barra superior.

## Goals / Non-Goals

**Goals:**
- Capturar el país en el wizard inicial y persistirlo en `arume.yml` (`arume.country`, ISO-2 minúsculas).
- Hacer que el idioma por defecto del wizardderive del país detectado, vía un mapeo estático ISO-2 → idioma oficial.
- Mostrar la bandera del país (PNG) en la barra superior, no editable.
- Cambiar el botón de idioma para que muestre el nombre del idioma activo en texto (en lugar de una bandera).
- Mantener compatibilidad hacia atrás con `arume.yml` pre-existentes (sin `country`).

**Non-Goals:**
- **Edición del país tras el setup.** No se podrá cambiar desde la UI principal ni desde Ajustes. Se registrará como issue de Backlog para un diálogo
  de reconfiguración futuro.
- **Asociar divisa al país.** La divisa se asociará a la empresa cuando se cree (issue #17 sigue vivo; el cambio de país de Argentina a Chile
  resalta exactamente esta separación país↔divisa).
- **Soporte de más idiomas além de en/es.** Cubierto por el issue #16.
- **Migración de `I18nManager` a bean Spring.** Issue #6.
- **Modificaciones en H2 / Flyway.** El país vive en `arume.yml`, no en BBDD.

## Decisions

### D1 — Modelo de país: enum `Country` en `arume-ui`

En lugar de cargar `countries.yml` como recurso, definir un **enum Java** `com.angazo.arume.ui.config.Country` con las 7 instancias soportadas.

```java
public enum Country {
    ES("es", "es"),
    GB("gb", "en"),
    US("us", "en"),
    CL("cl", "es"),
    SG("sg", "en"),
    AU("au", "en"),
    ZA("za", "en");

    private final String code;        // ISO-2 minúsculas, persistido en arume.yml
    private final String officialLanguage;  // código de idioma para default del wizard
    // + getLabelKey() -> "wizard.country.<code>": nombre i18n del país
}
```

**Por qué enum vs `countries.yml`**: dominio cerrado y pequeño (7 entradas), los datos son estables, fosterea compiler-check (si añaden un país se
controla en compilación), y evita introducir otro parser de recursos. Un YAML añadiría un modo más de fallar (parseo, typos, missing keys) sinBeneficio
real. Si el catálogo creciera a decenas de países o requiriera datos por moneda fiscal, sí merecería un resource externo — anotado como Non-Goal.

**Métodos del enum**:
- `Country.fromCode(String)` → resuelve o devuelve `ES` (default).
- `Country.values()` → para llenar el combo.
- `country.getLabelKey()` → `"wizard.country." + code` (p. ej. `wizard.country.es`), puntuado en ambos bundles.

### D2 — Mapeo idioma-oficial-by-country vive dentro del propio enum

`Country.officialLanguage()` retorna el código de idioma (`es`/`en`). El wizard lo usa para inicializar el combo de idioma al cambiar de país. Si
un país tuviera varios idiomas oficiales (Non-Goal de momento), se ampliaría a `List<String> officialLanguages()` con un primero por defecto.

### D3 — Detección del país por defecto

`Country.detectDefault()`:
1. `Locale.getDefault().getCountry()` → lowercased → `Country.fromCode(...)` si existe.
2. Si no, fallback `ES`.

Se sigue conservando `I18nManager.detectDefaultLanguage()` para casos puntuales, pero el wizard ahora calcula primero **país default** y luego
**idioma default = país.officialLanguage()**. `I18nManager.init(...)` del wizard se inicializa con ese idioma oficial (en vez de usar directamente el
locale del OS). El usuario puede cambiar idioma y país libremente; el combo de idioma no es afectado por selección posterior de país (solo al iniciar).

**Decisión: no re-sincronizar el idioma al cambiar el país manualmente.** Si el usuario cambia Chile→Reino Unido manualmente, no forzamos inglés;
respeta su preferencia previa. La sincronización país→idioma solo aplica al abrir el wizard (defaults iniciales). Evita interrupciones imprevistas del
usuario mientras configura.

### D4 — País↔Combo: items internacionales a partir de `getLabelKey()`

El combo de país se llena con `I18nManager.getString(country.getLabelKey())` para cada `Country` value, en el idioma activo del combo momento de
mostrarse; `refreshTexts()` rellena los items en el idioma actual (igual que se hace hoy con el combo de theme). Para distinguir identidad alsetear
el seleccionado usamos `Country.fromCode(...)` y comparamos por `code` (no por texto i18n), para evitar ambiguedad de nombres entre bundles.

### D5 — Persistencia y propagación

- `WizardResult`: añadir `String country` (primer campo, por orden de captura).
- `ArumeConfig`: añadir `String country` (entre `language` y `dbType`).
- `ConfigManager.load()`: `arume.getOrDefault("country", "es").toString()` (lowercased por robustez).
- `ConfigManager.save()`: `arume.put("country", config.country())`.
- `ConfigManager.updateLanguage(String)` y `updateTheme(String)` deben propagar `config.country()` intacto (hoy reconstruyen `ArumeConfig`; tras el
  cambio no pueden perderlo).
- `ArumeAppFX.buildConfigFromWizard(WizardResult)`: propagar `result.country()`.
- `ArumeAppFX.runWizardFlow(...)`: `I18nManager.init(<idioma oficial del país detectado>)` antes de mostrar el wizard, en vez de
  `I18nManager.detectDefaultLanguage()`.

### D6 — Banderas PNG

Ubicación: `arume-ui/src/main/resources/icons/flags/<iso2>.png` con `<iso2>` minúsculas (`es.png`, `gb.png`, `us.png`, `cl.png`, `sg.png`, `au.png`,
`za.png`). Tamaño 32×20 píxeles.

- Wizard: no se muestra bandera (el combo usa solo texto i18n del nombre del país).
- Barra superior: `ImageView` 32×20 cargado vía ` getClass().getResourceAsStream("/icons/flags/" + country.code() + ".png")`. Se añadirían a
  `arume-flags`未来es PNGs adicionales tanto como sea necesario.

**Por qué PNG vs SVG**: AtlantaFX y JavaFX no traen un font de banderas por país (Ikonli FontAwesome5/MaterialDesign2 no cubren); añadir parser SVG o
depender de librerías externas es excesivo. PNG 32×20 es suficiente a escala 1:1, ocupa pocos bytes, y encaja con el `ImageView` ya usado para el
logo de la app. Si se requiere escalado responsivo en alta DPI, se reconsidera en el issue de Backlog.

### D7 — Indicador de país en la barra principal (no interactivo)

Se añade un `ImageView countryFlag` con `setMouseTransparent(true)` y un `Tooltip` i18n (`main.country.tooltip` -> "País: España" / "Country: Spain").
**Node**: un `ImageView` directo en el FXML (estilo plano) — no un botón deshabilitado. Más simple, sin hover state, sin styling AtlantaFX no
aplicable.

En `main.fxml`, la sección de la barra superior (zona derecha) queda en este orden:

```
[logo] [Help]  ........spacer........  [countryFlag] [languageBtn:texto] [themeBtn] [_] [▢] [✕]
```

### D8 — Botón de idioma pasa a texto

Se sustituye `selectLanguageIcon()` por `selectLanguageText()`:
- `languageBtn.setGraphic(null)`.
- `languageBtn.setText(I18nManager.getString("main.language." + currentLanguage))` (nueva clave bundle: `main.language.en` = "English",
  `main.language.es` = "Español").
- `onLanguageToggle()` mantiene el toggle es↔en. Persistencia `configManager.updateLanguage(next)`.
- `refreshTexts()` y `onLanguageChanged()` invocan `selectLanguageText()`.

**Decisión: muestra el idioma activo**, no el que se aplica al pulsar. Razón: identificable, sin sorpresa; el botón "es un toggle" lo deja claro el
cursor y el feedback al click. (Alternativa rechazada: label + flecha tipo "ES → EN"; más ruidoso.)

### D9 — Layout FXML del wizard

Reorganizar el `HBox` superior de `first-run-wizard.fxml` en tres `VBox` (cada una con Label + ComboBox) en el orden **País → Idioma → Tema**.

Ensanchado del wizard:
- `prefWidth` del `VBox` raíz: 494 → **600** (aprox.).
- `Scene` en `ArumeAppFX.showFirstRunWizard`: 494, 840 → **600, 840** (la altura no cambia).
- `prefWidth` de los combos:
  - country: **240**
  - language: **180**
  - theme: **150**
  Suma + spacings + padding: ~600. Queda aire para los tres y no se ve apretado.

### D10 — I18n: claves nuevas

En ambos bundles (`messages.properties`, `messages_es.properties`):

```
wizard.country=Country:                    | wizard.country=País:
wizard.country.es=Spain                    | wizard.country.es=España
wizard.country.gb=United Kingdom           | wizard.country.gb=Reino Unido
wizard.country.us=United States            | wizard.country.us=Estados Unidos
wizard.country.cl=Chile                    | wizard.country.cl=Chile
wizard.country.sg=Singapore                | wizard.country.sg=Singapur
wizard.country.au=Australia                 | wizard.country.au=Australia
wizard.country.za=South Africa            | wizard.country.za=Sudáfrica
main.language.en=English                    | main.language.en=English
main.language.es=Español                    | main.language.es=Español
main.country.tooltip=Country: {0}           | main.country.tooltip=País: {0}
```

`messages.properties` queda como default English; `messages_es.properties` con las traducciones. Los nombres de país (`wizard.country.*`) se traducen
al idioma activo (no es "español" ni "english" de manera固定; ej. Chile aparece como "Chile" en ambos, pero España como "Spain"/"España"). El
`main.country.tooltip` se formatea con `MessageFormat` para inserción del nombre del país ya en el idioma activo (`I18nManager.getString(country.getLabelKey())`).

## Risks / Trade-offs

- **[Riesgo] `arume.yml` cypher URL cifrada** (`ConfigManager.load` no bootea si el descifrado falla existe flujo de reconfig) — el cambio añade
  `country` default. → **Mitigación**:`getOrDefault("country", "es")`, el path de reconfigure genera un wizard completo con país y se persiste
  correctamente. Test de registro + load/save de round-trip.
- **[Riesgo] Item `FLAG_USA` eliminado rompe imports** — el cambio borra usos de `FontAwesomeSolid.FLAG`/`FLAG_USA` en `MainController`. →
  **Mitigación**: limpiar imports en la misma PR; revisión con `./gradlew build` (compila + test).
- **[Riesgo] PNGs faltantes por país** — si se añade un `Country` sin PNG, la barra principal fallaría al cargar la imagen (NPE/`Image` vacío). →
  **Mitigación**: test que para cada `Country.values()` existe `/icons/flags/<code>.png`. Si falla, se rompe la build.
- **[Riesgo] `selectLanguageText()` quita identificación visual rápida** del idioma (bandera era más rápida) → **Mitigación**: atenuada por texto
  auto-explicativo; para usuarios simultáneos puede añadirse icono mundo delante (`FontAwesomeSolid.GLOBE`) en futura iteración. Aqui fuera de scope.
- **[Trade-off] Idioma-oficial-por-país simplificado** a un único idioma por país; si país con varios oficiales (futuro) se amplía a lista, Non-Goal
  ahora. Documentado en el enum.
- **[Riesgo] Items del combo de país se reordenan al cambiar idioma** (los items se reconstruyen en el idioma activo) → **Mitigación**: el
  seleccionado se memoriza por `Country.code` (no por texto), y se re-selecciona tras reconstruir items.

## Open Questions

Ninguna abierta — todas las decisiones se cerraron en la fase de exploración con el usuario. Eventuales refinamientos sobre nombres de claves i18n
se cierran durante la implementación siguiendo las existentes del proyecto.

## Migration Plan

No hay migración de BBDD. Para usuarios existentes:

1. Al iniciar la nueva versión, `ConfigManager.load()` encuentra `arume.yml` sin `arume.country` y aplica default `es`.
2. El wizard no se muestra (config existe). La barra superior carga la bandera `es.png`.
3. Si el usuario posteriormente quiere cambiar de país, debe re-ejecutar el flujo de wizard (el flujo existente al error de descifrado
   `handleDecryptError` ya se beneficia del nuevo wizard).

Rollback: con revertir el commit y mantener una `arume.yml` vieja, todo sigue funcional (la `arume.yml` nueva con clave `country` extra es
ignorada por la versión vieja porque `ArumeConfig` no la lee, no rompe nada — la clave se queda en YAML sin usar).