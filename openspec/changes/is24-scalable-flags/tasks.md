## 1. Assets

- [x] 1.1 Rasterizar los 7 SVGs (`es`, `gb`, `us`, `cl`, `sg`, `au`, `za`.svg, set 4×3 apaisado) de `docs/banderas/` a PNGs de 96×72 (3×) con
      `rsvg-convert` y escribirlos sobre `arume-ui/src/main/resources/icons/flags/<alpha3>.png` (`esp`, `gbr`, `usa`, `chl`, `sgp`, `aus`, `zaf`)
- [x] 1.2 Verificar con `file` que los 7 PNGs son 96×72 y comprobar visualmente la nitidez de al menos las banderas complejas (`es`, `gb`, `za`)

## 2. Ajuste de visualización

- [x] 2.1 Cambiar en `main.fxml` el fit del `ImageView` de la bandera de `fitHeight="20"` a `fitHeight="24"` (proporción 4:3)
- [x] 2.2 Cambiar en `MainController.setupCountryFlag()` `setFitHeight(20)` a `setFitHeight(24)`, manteniendo `fitWidth=32` y `preserveRatio`

## 3. Tests

- [x] 3.1 Actualizar `CountryResourcesTest.flagPngExistsForEveryCountry` para verificar que el PNG de cada país existe y mide 96×72 px
- [x] 3.2 Añadir en `CountryResourcesTest` un test que compruebe que la relación de aspecto de cada PNG es 4:3 (igual a 32×24, sin distorsión)

## 4. Documentación y verificación

- [x] 4.1 Actualizar `AGENTS.md` con la nueva convención de banderas (PNGs 3× de 96×72 generados desde `docs/banderas/` con `rsvg-convert`,
      set 4×3 apaisado, licencia flag-icons MIT, y el comando de regeneración)
- [x] 4.2 Ejecutar `./gradlew :arume-ui:test` y `./gradlew build`
- [x] 4.3 Verificación manual: arrancar la app y comprobar la bandera nítida en 1× y en alta DPI (escala 2×)
