## Context

Hoy el país se captura en el wizard de primer arranque (combo con 7 países), se persiste como `arume.country` en `arume.yml` (`ArumeConfig` /
`ConfigManager` / `WizardResult`) y se muestra como bandera no interactiva en la barra superior de la ventana principal
(`MainController.countryFlag` + tooltip `main.country.tooltip`). El enum `com.angazo.arume.ui.config.Country` (7 instancias, ISO-3 minúsculas,
idioma oficial asociado) es el catálogo que alimenta combo, bandera y tests (`CountryTest`, `CountryResourcesTest`, casos de `country` en
`ConfigManagerTest`).

Desde is26, el catálogo canónico de países vive en la BBDD (`t1_countries`, alpha-3 en mayúsculas, seed con los mismos 7 países). La issue #23
hace que el país pase a ser un atributo de la empresa: la app deja de tener "país" a nivel global y la bandera reaparecerá ligada a la empresa
activa en el futuro. Ver `proposal.md — Why` para la motivación completa.

Restricciones relevantes del stack: SnakeYAML para `arume.yml` (dump escribe solo las claves del `Map` construido), `I18nManager` singleton con
`detectDefaultLanguage()` (detección por `Locale.getDefault().getLanguage()`), wizard FXML con fila superior de combos, `main.fxml` define la barra
superior.

## Goals / Non-Goals

**Goals:**
- Eliminar el enum `Country` y el campo `country` de `ArumeConfig`, `WizardResult` y `ConfigManager` (load/save/updateLanguage/updateTheme).
- Eliminar el selector de país del wizard y la bandera de la barra superior de la ventana principal.
- Devolver la detección del idioma por defecto del wizard al `Locale` del OS (`I18nManager.detectDefaultLanguage()`).
- Conservar los PNGs de banderas como recursos inertes y protegerlos con un test de recursos.
- Mantener la carga tolerante de `arume.yml` preexistentes con `arume.country`.

**Non-Goals:**
- **Crear la entidad "empresa" ni el concepto de "empresa activa".** Es un issue futuro (la bandera de la empresa activa aparecerá entonces). Este
  change solo retira el país a nivel de aplicación y deja los PNGs listos.
- **Modificar `t1_countries` ni ningún catálogo de BBDD.** El catálogo canónico queda donde está.
- **Cambios en `arume-db` o `arume-app`** más allá de los flujos de arranque ya existentes en `ArumeAppFX`.
- **Migraciones Flyway**: no se requieren.

## Decisions

### D1 — Eliminar el enum `Country` y el campo `country` de la configuración
Decisión del usuario (exploración). El enum duplicaba el catálogo de `t1_countries` y su único consumidor era la selección de país del arranque,
que desaparece. Se elimina `Country`, el campo `country` de `ArumeConfig`/`WizardResult` y toda la lógica asociada en `ConfigManager`
(`getOrDefault("country", "esp")` en load, `arume.put("country", ...)` en save, y la propagación en `updateLanguage`/`updateTheme`).

Alternativa descartada: conservar el enum como catálogo latente. Se rechazó por duplicidad de fuente de verdad (enum + `t1_countries`) y por
acoplar el futuro mapeo empresa→bandera a un catálogo fijo de 7 países.

### D2 — Wizard: eliminar el combo de país y volver el idioma por defecto al OS
- `first-run-wizard.fxml`: se elimina la `VBox` (countryLabel + countryCombo); la fila superior queda **Idioma → Tema**.
- Default del combo de idioma: `I18nManager.detectDefaultLanguage()` (sustituye a `Country.detectDefault().officialLanguage()`), coherente con el
  spec `internationalization` y con la implementación pre-is21.
- `ArumeAppFX.runWizardFlow()`: `I18nManager.init(I18nManager.detectDefaultLanguage())`.
- Anchura: se revierte de 600 → ~494px (escena `494×840`, `prefWidth` 494), la anchura previa a is21, suficiente para dos combos.

### D3 — Persistencia: compatibilidad con `arume.country` sobrante
`ConfigManager.load()` lee claves concretas del YAML; al eliminar la lectura de `arume.country`, un fichero existente con esa clave carga sin error
(simplemente no se lee). `ConfigManager.save()` reconstruye el `Map` completo: como el dump escribe solo las claves del `Map`, la clave sobrante
**desaparece automáticamente** en el siguiente guardado (cambio de idioma o tema). No hace falta lógica de limpieza adicional ni migración de
configuración.

### D4 — Barra superior sin bandera
Se elimina el `ImageView countryFlag` de `main.fxml` y en `MainController`: el campo, `loadConfiguredCountry()`, `setupCountryFlag()`,
`updateCountryFlagTooltip()` y las llamadas en `initialize()`/`onLanguageChanged()`/`refreshTexts()`. El orden de la barra queda:
`[logo] [Help] ... [languageBtn] [themeBtn] [_] [▢] [✕]`. Los imports de `Tooltip` y `Country` se limpian; `Image`/`ImageView` se conservan (los
usa el logo).

### D5 — PNGs conservados como recursos inertes + `FlagResourcesTest`
Los 7 PNGs (`esp`, `gbr`, `usa`, `chl`, `sgp`, `aus`, `zaf` — 96×72, 4:3) se mantienen en `resources/icons/flags/` sin cambios. Como no hay enum
que los recorra, se crea `FlagResourcesTest` con la lista de códigos hardcodeada para verificar existencia, dimensiones 96×72 y proporción 4:3.
Esto preserva el requisito del issue ("dejaremos los png para usarlos a futuro") y evita regresiones silenciosas (borrado/corrupción).

En el futuro, la feature de empresa activa mapeará `t1_countries.alpha_3` (mayúsculas) → `icons/flags/<alpha3 minúsculas>.png`; por eso el código
en minúsculas de los PNGs ya es el formato correcto.

### D6 — i18n: limpieza de claves
Se eliminan de `messages.properties`, `messages_en.properties` y `messages_es.properties`: `wizard.country`, `wizard.country.<code>` (7) y
`main.country.tooltip`. Se mantiene paridad de claves restantes entre bundles. No se toca `wizard.lang.*`, `wizard.language`, `main.language.*`.

### D7 — Specs: eliminación de la capability `country-selection`
El delta spec `specs/country-selection/spec.md` marca todos sus requisitos como REMOVED (con Reason/Migration). Al archivar, el spec principal
queda sin requisitos (solo el `Purpose`); tras el archive se elimina `openspec/specs/country-selection/`. Las capabilities
`first-run-wizard`, `ide-window-chrome` e `internationalization` se actualizan con sus deltas MODIFIED/REMOVED.

## Risks / Trade-offs

- **[Riesgo] `arume.yml` con `arume.country` de instalaciones previas** → **Mitigación**: load ignora la clave; save la autolimpia. Test de carga
  tolerante en `ConfigManagerTest` (5.3 en tasks).
- **[Riesgo] Quedan referencias residuales a `Country`/`countryFlag`/`wizard.country`** → **Mitigación**: tarea de grep de restos en `src/` y
  limpieza de imports; `./gradlew build` falla si algo quedó sin compilar.
- **[Riesgo] PNGs borrados o corruptos sin consumidor** → **Mitigación**: `FlagResourcesTest` (existencia, 96×72, 4:3) rompe la build ante
  cualquier regresión.
- **[Trade-off] Sin país en el arranque, el idioma por defecto puede no coincidir con el idioma de negocios esperado** → aceptable: la detección OS
  cubre es/en y el usuario elige libremente en el wizard; el idioma es independiente del país desde is21.
- **[Trade-off] La bandera desaparece de la UI antes de que exista la empresa activa** → es el objetivo explícito de la issue #23; se recuperará
  con datos reales de la empresa (no un placeholder global).

## Migration Plan

1. **BBDD**: ninguna migración. `t1_countries` y su catálogo no cambian.
2. **Usuarios existentes**: al arrancar con un `arume.yml` que contenga `arume.country`, la app carga normal, sin bandera ni selector de país. En
   el siguiente cambio de idioma o tema la clave sobrante desaparece del fichero.
3. **Primer arranque limpio**: el wizard ya no ofrece país; idioma por defecto según OS.
4. **Rollback**: revertir el commit de implementación. La versión anterior restaura el enum y lee `arume.country` con default `esp` para ficheros
   sin la clave (compat is21/is26), y la bandera vuelve a mostrarse. Los `arume.yml` sin `arume.country` cargan con `esp` como antes.
5. **Post-archive**: tras `openspec archive is23-country-as-company-data`, eliminar `openspec/specs/country-selection/` (queda vacía) y crear el
   issue de Backlog para la feature de empresa activa + bandera.

## Open Questions

Ninguna abierta — las decisiones de alcance se cerraron con el usuario en la fase de exploración (eliminar el enum `Country`). Eventuales
refinamientos de anchura exacta del wizard o nombres de claves i18n se cierran durante la implementación.
