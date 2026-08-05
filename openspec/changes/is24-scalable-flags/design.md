# Design: Banderas nítidas en alta DPI (PNGs 3× pre-rasterizados)

## Context

Hoy el indicador de país en la barra superior (`MainController.setupCountryFlag()`) carga un PNG de 32×20 px desde
`arume-ui/src/main/resources/icons/flags/<alpha3>.png` y lo muestra en un `ImageView` con `fitWidth=32`, `fitHeight=20`,
`preserveRatio=true`.

El problema: al escalar el `ImageView` a factores 2×/3× (HiDPI/Retina), JavaFX estira el bitmap de 32×20 y la bandera se
pixela. El usuario dispone de un set vectorial [flag-icons](https://github.com/lipis/flag-icons) (licencia MIT) en
`docs/banderas/` desde el que pre-rasterizar assets de alta resolución.

Detalles constatados del set descargado:
- Los SVGs usan `viewBox="0 0 640 480"` (set **4×3 apaisado**), que es el estándar de flag-icons y se ve natural en la UI.
- Los ficheros se nombran por código **ISO-2** (`es.svg`, `gb.svg`, `us.svg`, `cl.svg`, `sg.svg`, `au.svg`, `za.svg`).
- La rasterización a PNG con `rsvg-convert` (librsvg 2.50.7) es correcta y produce imágenes opacas (validado).

Restricciones:
- El `ImageView` de la barra superior debe conservar su `preserveRatio` y mostrar la bandera en su proporción natural (4:3).
- La bandera solo se muestra en ese tamaño estático (barra superior y futuras ventanas): no hay necesidad de escalar a otros tamaños.
- El proyecto prioriza dependencias ligeras y cero fricción en runtime/CI.

## Goals / Non-Goals

**Goals**
- Bandera nítida en 1×/2×/3× y con proporción apaisada natural (4:3) en la barra superior.
- Cero dependencias nuevas y sin lógica de DPI en el código de la UI.
- `docs/banderas/` (SVGs flag-icons, MIT) como fuente única desde la que regenerar los assets.

**Non-Goals**
- Renderizar SVG en runtime: para un asset estático de 32×24 es sobre-ingeniería (dependencias + puente AWT↔JavaFX).
- Banderas de tamaño variable (p. ej. 256 px o más): quedaría fuera del alcance y requeriría re-evaluar la resolución del asset.
- Soporte de banderas por cada país del mundo: solo las 7 del catálogo actual.
- Migrar otras imágenes del proyecto (logos, iconos) a otra técnica.

## Decisions

### D1 — Técnica: PNGs 3× pre-rasterizados en build-time (no render en runtime)

**Decisión**: los SVGs de `docs/banderas/` se rasterizan **una vez** a PNGs de **96×72 px (3× de 32×24)** con `rsvg-convert`
y los resultados se commitean en `arume-ui/src/main/resources/icons/flags/<code>.png`. El `ImageView` pasa a
`fitWidth=32`, `fitHeight=24`, `preserveRatio=true` (proporción 4:3 apaisada) y mantiene el smoothing por defecto de JavaFX.

**Racional**: como el tamaño de visualización es fijo y conocido (32×24), una fuente de 3× cubre las densidades 1×/2×/3×:
JavaFX hace downsampling (1.5×–3× según DPI), que es suave y nítido, y a 3× el mapeo es 1:1. No hay código de DPI, no hay
dependencias runtime y el consumo de memoria es mínimo (7 PNGs de ~1–3 KB).

**Alternativas descartadas**:
- **Render SVG en runtime (svgSalamander + `javafx.swing`)**: resuelve el problema pero añade una dependencia, un módulo
  JavaFX extra y código de conversión AWT→FX para un resultado que un PNG de 3× consigue de forma idéntica. **Descartada** por
  complejidad innecesaria para un asset estático.
- **PNGs multi-resolución @1x/@2x/@3x con selección por `windowPixelScaleFactor`**: obliga a generar 21 ficheros y a adaptar
  `ImageView.setFitWidth/setFitHeight` por DPI (lo que el propio issue cita como trabajo extra). Con un solo asset 3× el
  downsampling de JavaFX logra el mismo resultado visual sin lógica de selección. **Descartada**.
- **Dibujar banderas con `Canvas` por código**: coste de mantenimiento alto y duplicaría el set flag-icons. **Descartada**.

### D2 — Factor de resolución del asset: 3× (96×72)

**Decisión**: un único asset de 96×72 por bandera, es decir, 3× el tamaño lógico de visualización (32×24).

**Racional**: el issue fija el objetivo en HiDPI 2×/3×. A 3× el asset se muestra 1:1; a 1× y 2× hay downsampling suave. Es el
máximo exigido sin desperdiciar memoria ni tamaño de repo.

**Alternativa**: 4× (128×96) daría algo más de margen futuro a costa de apenas unos cientos de bytes por fichero; no aporta
beneficio perceptible en el rango de DPI objetivo. Se queda en 3×.

### D3 — Nombrado de assets: se mantiene el alpha-3 actual

**Decisión**: los PNGs conservan el nombrado actual por código alpha-3 (`esp.png`, `gbr.png`, …) y la ruta
`icons/flags/<code>.png` que ya usa `MainController` (`country.code()`).

**Racional**: no hay cambio de código en `MainController` ni en `Country` (no hace falta exponer `alpha2`). El mapeo
SVG(ISO-2) → PNG(alpha-3) se hace solo en el momento de regenerar, de forma manual y documentada.

### D4 — Sin lógica de DPI en la UI

**Decisión**: `setupCountryFlag()` solo cambia el fit vertical (32×20 → 32×24) para acomodar la proporción apaisada 4:3. El
downsampling del asset 3× al tamaño lógico 32×24 lo hace JavaFX con el `ImageView.smooth` por defecto (`true`), que usa
filtrado bicúbico/bilineal de alta calidad.

**Racional**: elimina el `windowPixelScaleFactor`/`Screen.getOutputScaleX()` del código. La nitidez en 2× se debe a que la
fuente (96×72) supera la resolución física (64×48).

## Risks / Trade-offs

- **Regeneración manual al añadir países**: al incorporar un país nuevo hay que rasterizar su SVG con `rsvg-convert` →
  Mitigación: comando documentado en `AGENTS.md` y `docs/banderas/` conservado como fuente.
- **Calidad del rasterizador**: `rsvg-convert` (librsvg) es fiable; a 96×72 los detalles del set 4×3 son nítidos →
  Mitigación: verificación visual en el arranque manual y test de dimensiones.
- **DPI > 3× (poco común en escritorio)**: el asset se escalaría ligeramente →
  Mitigación: aceptado; se regenera en 4× si alguna vez hiciera falta.
- **Aspecto apaisado en la barra**: la bandera 4×3 ocupa 32×24 en una barra de 40 px → Mitigación: encaja con holgura; la
  anchura lógica (32 px) es la misma que antes, solo crece 4 px el alto.

## Migration Plan

1. Rasterizar los 7 SVGs (`es, gb, us, cl, sg, au, za`.svg) a 96×72 con `rsvg-convert` y escribir sobre
   `arume-ui/src/main/resources/icons/flags/<alpha3>.png`.
2. `main.fxml` y `MainController.setupCountryFlag()`: cambiar el fit del `ImageView` de 32×20 a 32×24 (4:3).
3. `CountryResourcesTest`: actualizar `flagPngExistsForEveryCountry` para verificar dimensión 96×72 y añadir comprobación de
   relación de aspecto 4:3.
4. Actualizar `AGENTS.md` con la convención (PNG 3× 96×72, fuente `docs/banderas/` set 4×3, comando de regeneración, licencia flag-icons).
5. `./gradlew :arume-ui:test` y `./gradlew build` + arranque manual para verificar la bandera en 1×/2×.

**Rollback**: revertir el commit; los PNGs 32×20 y el fit 32×20 permanecen en el historial de git y bastan para restablecer el comportamiento anterior.

## Open Questions

- _(Resuelto)_ `docs/banderas/` se conserva como fuente upstream. Se descarta el render en runtime (svgSalamander +
  `javafx.swing`) en favor de PNGs 3× pre-rasterizados. Se usa el set **4×3 apaisado** de flag-icons: el `ImageView` pasa a
  fit 32×24 (proporción natural), eliminando la compresión vertical que producía el set 1×1.
