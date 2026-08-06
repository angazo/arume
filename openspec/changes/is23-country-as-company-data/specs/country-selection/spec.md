# Country Selection (REMOVED)

## REMOVED Requirements

### Requirement: Supported countries catalog
**Reason**: El país pasa a ser un atributo de la empresa (issue #23); el catálogo canónico de países vive ahora en la tabla `t1_countries` de la
BBDD (is26), no en un catálogo fijo de la aplicación. El enum `Country` de `arume-ui` se elimina.
**Migration**: El catálogo de países se consulta desde la BBDD. Los usuarios no pierden datos; el país no se persistía en BBDD a nivel de aplicación.

### Requirement: Country default detection
**Reason**: La detección de país por defecto solo servía al wizard de primer arranque, que ya no captura el país. La detección del idioma por
defecto vuelve a derivar del `Locale` del OS (spec `internationalization`).
**Migration**: Ninguna: la funcionalidad de detección de país por defecto desaparece con la selección de país del wizard.

### Requirement: Country code resolution
**Reason**: `Country.fromCode()` resolvía códigos ISO-3166-1 alpha-3 al enum de la aplicación, que se elimina.
**Migration**: Los códigos de país se leen de la BBDD (`t1_countries`) cuando la feature de empresa los requiera.

### Requirement: Country internationalized label
**Reason**: Las claves i18n `wizard.country.<code>` solo las consumía el combo de país del wizard y el tooltip de la bandera, ambos eliminados.
**Migration**: Se eliminan las claves de los bundles; si la futura feature de empresa activa necesita nombres de país, se traducirán desde el
catálogo de BBDD con claves propias.

### Requirement: Official-language-to-country mapping resource
**Reason**: El mapeo idioma-oficial ↔ país (enum `Country.officialLanguage()`) solo se usaba para los defaults del wizard de país.
**Migration**: El default de idioma del wizard vuelve a derivar del OS (`I18nManager.detectDefaultLanguage()`).

### Requirement: Country persistence in arume.yml
**Reason**: `arume.country` ya no tiene sentido: el país es un dato de la empresa, no de la aplicación. Se elimina del modelo de configuración.
**Migration**: Un `arume.yml` existente con `arume.country` se carga sin error (la clave se ignora al leer) y desaparece del fichero en el siguiente
guardado de configuración (cambio de idioma o tema). No requiere acción del usuario.

### Requirement: Country flag indicator in main window title bar
**Reason**: La bandera global en la barra superior mostraba el país de la aplicación, concepto que desaparece. La bandera se mostrará en el futuro
ligada a la empresa activa.
**Migration**: Se elimina el indicador de la barra superior. Los PNGs de banderas se conservan como recursos inertes en
`resources/icons/flags/<code>.png` para la futura feature de empresa activa.

### Requirement: Country flags PNG assets
**Reason**: La capability se elimina porque el catálogo en el enum que la respaldaba desaparece, pero **los PNGs se conservan** como recursos
inertes para el futuro (empresa activa), donde se mapearán desde el alpha-3 de `t1_countries`.
**Migration**: Los 7 PNGs (`esp`, `gbr`, `usa`, `chl`, `sgp`, `aus`, `zaf`) permanecen en `arume-ui/src/main/resources/icons/flags/`. Un test de
recursos (`FlagResourcesTest`) garantiza su existencia, dimensiones 96×72 y proporción 4:3 mientras no tengan consumidor en la UI.
