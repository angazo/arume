## Why

Las banderas de país se muestran como PNGs fijos de 32×20 px. En pantallas HiDPI/Retina (escalas 2×/3×) JavaFX redimensiona esos bitmaps y las
banderas se ven pixeladas. El proyecto ya dispone de un set vectorial de banderas (flag-icons, licencia MIT) en `docs/banderas/` desde el que
pre-rasterizar assets de alta resolución con nitidez en cualquier densidad de píxel.

## What Changes

- Sustituir los 7 PNGs 32×20 de `arume-ui/src/main/resources/icons/flags/<code>.png` por PNGs pre-rasterizados a **3× (96×72 px)** generados
  desde los SVGs del set **4×3 apaisado** de `docs/banderas/` (flag-icons) con `rsvg-convert`.
- El `ImageView` de la barra superior pasa a **fit 32×24 (4:3)**, ajustándose a la proporción natural apaisada de la bandera;
  el smoothing por defecto de JavaFX mantiene la nitidez en 1×/2×/3× **sin lógica de DPI ni dependencias nuevas**.
- **No se añade ninguna dependencia** (se descarta svgSalamander + módulo `javafx.swing`).
- Actualizar `CountryResourcesTest` para verificar que existe el PNG 96×72 de cada país y su relación de aspecto 4:3.
- Documentar en `AGENTS.md` la convención: PNGs 3× generados desde `docs/banderas/`, con el comando de regeneración.
- Conservar `docs/banderas/` como fuente de banderas futuras (no se elimina).

## Capabilities

### New Capabilities

_(Ninguna: la funcionalidad de banderas pertenece a la capability existente `country-selection`.)_

### Modified Capabilities

- `country-selection`: cambian los requisitos **Country flag indicator in main window title bar** (indicador nítido en alta DPI, tamaño
  lógico 32×24 apaisado) y **Country flags PNG assets** (PNGs de 32×20 → PNGs 3× de 96×72). Se mantiene el nombrado por código alpha-3 y
  la carga vía `ImageView`.

## Impact

- `arume-ui/src/main/resources/icons/flags/`: 7 PNGs sustituidos por versiones 96×72 (3×) generadas de `docs/banderas/`.
- `main.fxml` y `MainController.setupCountryFlag()`: fit del `ImageView` de la bandera pasa de 32×20 a 32×24 (4:3).
- `CountryResourcesTest`: comprobación de dimensión (96×72) y aspecto (4:3) de los PNGs.
- `AGENTS.md`: actualización de la convención de banderas (PNG 3× + comando de regeneración + procedencia flag-icons).
- `MainController.setupCountryFlag()`: sin cambios (la carga y el fit 32×20 son compatibles con el nuevo tamaño de origen).
- `docs/banderas/`: se conserva como fuente upstream de banderas futuras.
