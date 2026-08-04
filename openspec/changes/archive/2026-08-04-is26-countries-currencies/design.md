## Context

Hoy el catálogo de países vive únicamente en el enum `Country` del módulo `arume-ui`: siete países identificados con códigos ISO 3166-1 alpha-2 en minúsculas (`es`, `gb`, `us`, `cl`, `sg`, `au`, `za`), cada uno con su idioma oficial. La base de datos (H2 en modo PostgreSQL, migraciones Flyway) solo contiene la tabla `t0_app_config` creada por `V0.1.0.0__init_schema.sql`. No existe catálogo de divisas en ninguna parte del producto.

La futura funcionalidad de facturación necesitará países y divisas como datos maestros en la base de datos (país del cliente, divisa de la factura, símbolo monetario para mostrar importes). Este change crea esos catálogos en BBDD con los estándares ISO 3166-1 e ISO 4217 y homogeneiza el identificador de país en todo el producto en torno al código alpha-3.

Restricciones y estado actual relevantes:

- Nomenclatura BBDD del proyecto: tablas `t<n>_<nombre>`, PK `pk_t<n>`, UK `uk_t<n>_<desc>`, FK `fk_t<origen>_t<destino>`. El primer identificador libre es `t1`.
- El versionado de migraciones usa 4 segmentos (`V0.1.0.0__`); la siguiente migración será `V0.1.0.1__` (parche menor sobre la misma versión de producto).
- El país elegido en el wizard de primer arranque se persiste en `arume.yml` (clave `arume.country`), no en BBDD.
- El enum `Country` se usa en: combo del wizard (`FirstRunWizardController`), detección de país por locale del SO, bandera de la barra de título (`MainController`, PNGs `icons/flags/<code>.png`), claves i18n `wizard.country.<code>` y valores por defecto de `ArumeConfig`/`ConfigManager`.
- `arume-db` no tiene todavía entidades ni mappers; su paquete de mappers previsto es `com.angazo.arume.db.repository`.

## Goals / Non-Goals

**Goals:**

- Crear en la base de datos los catálogos de países (ISO 3166-1) y divisas (ISO 4217) y su relación N:M, rellenados con los 7 países que soporta el producto y sus divisas.
- Homogeneizar el identificador de país en todo el producto a ISO-3: enum, persistencia en `arume.yml`, banderas y claves i18n.

**Non-Goals:**

- Crear entidades o mappers MyBatis para las nuevas tablas: se añadirán cuando una funcionalidad los consuma.
- Compatibilidad hacia atrás con `arume.yml` existentes en ISO-2: se asume su regeneración (el usuario borra el fichero actual y el wizard vuelve a ejecutarse).
- Insertar el catálogo completo ISO 3166-1/ISO 4217: solo los países soportados por el producto.
- Cambios en la selección de idioma o de tema.

## Decisions

### D1 — `SMALLINT` para los códigos numéricos

Los códigos numéricos de ISO 3166-1 e ISO 4217 tienen 3 dígitos (0–999). `SMALLINT` es SQL estándar, cubre el rango de sobra y es el tipo más ajustado. Alternativa considerada: `INTEGER` (funciona pero desperdicia precisión frente al requisito explícito de "0 a 999"). No se añade restricción `CHECK` de rango: el tipo es suficiente y mantiene el DDL minimalista.

### D2 — Tres tablas con relación N:M

`t1_countries`, `t2_currencies` y `t3_country_currency` (tabla intermedia con FK a cada catálogo y PK compuesta). Alternativa descartada: FK de divisa hacia país en `t2_currencies`, porque impediría modelar la realidad (el euro o el dólar los usan varios países; un país puede admitir varias divisas). La tabla intermedia deja el modelo preparado para catálogos futuros sin migraciones de esquema.

### D3 — Alpha-3 en mayúsculas en la BBDD

La BBDD almacena el alpha-3 en mayúsculas (`ESP`, `GBR`…), como publican los estándares ISO. La capa de aplicación (enum `Country`, `arume.yml`, PNGs, claves i18n) sigue usando minúsculas (`esp`, `gbr`…). `Country.fromCode` ya normaliza a minúsculas al resolver, por lo que un código leído de BBDD en mayúsculas se convierte automáticamente al formato que espera el resto del producto sin lógica adicional.

### D4 — Nombres en inglés en los catálogos

Las columnas `name` almacenan el nombre en inglés (`Spain`, `Euro`). La UI ya muestra etiquetas traducidas vía i18n; la BBDD actúa como fuente de datos neutra. Alternativa descartada: nombres locales (`España`), inconsistentes entre filas y redundantes con el i18n existente.

### D5 — El enum `Country` conserva un campo auxiliar alpha-2 solo para la detección por locale del SO

`Locale.getCountry()` devuelve códigos ISO 3166-1 alpha-2, así que la detección del país por defecto necesita seguir entendiendo alpha-2 internamente. El enum pasa a tener `alpha3` (identificador principal, usado en `code()`, persistencia, PNGs e i18n) y `alpha2` (solo para `detectDefault`). `fromCode` acepta únicamente alpha-3: un valor ISO-2 persistido por versiones anteriores no resuelve y cae al fallback `esp`. Alternativa considerada: convertir con `new Locale("", alpha2).getISO3Country()`; descartada porque lanza `MissingResourceException` para códigos desconocidos y hace el flujo menos explícito.

### D6 — Sin compatibilidad hacia atrás en `arume.yml`

El cambio de ISO-2 a ISO-3 es rupturista y no se añade traducción de valores antiguos: un `arume.yml` con `country: cl` trataría el valor como desconocido y caería en `esp`. El usuario regenera su configuración (borra el fichero y vuelve a pasar el wizard). Se acepta el trade-off a cambio de no arrastrar lógica de compatibilidad.

### D7 — Una única migración `V0.1.0.1__countries_and_currencies.sql` con DDL y datos

El esquema y el seed van en el mismo script: los catálogos nacen poblados y ningún código depende de ellos todavía, así que no hay valor en separarlos. El script crea las tres tablas con sus constraints (`pk_t1`, `uk_t1_alpha3`, `pk_t2`, `uk_t2_alpha3`, `pk_t3`, `fk_t3_t1`, `fk_t3_t2`) e inserta:

| País | Numérico | Alpha-3 | Divisa | Num. ISO 4217 | Símbolo |
|---|---|---|---|---|---|
| Spain | 724 | ESP | Euro | 978 | € |
| United Kingdom | 826 | GBR | Pound Sterling | 826 | £ |
| United States | 840 | USA | US Dollar | 840 | $ |
| Chile | 152 | CHL | Chilean Peso | 152 | $ |
| Chile | 152 | CHL | Unidad de Fomento | 990 | UF |
| Singapore | 702 | SGP | Singapore Dollar | 702 | $ |
| Australia | 36 | AUS | Australian Dollar | 36 | $ |
| South Africa | 710 | ZAF | Rand | 710 | R |

Chile tiene dos divisas asociadas (CLP y CLF), por lo que `t3_country_currency` contiene dos filas para `country_numeric_code=152`. El resto de países tienen una única asociación.

### D8 — Test de migración sobre H2 en memoria

Se añade un test JUnit que ejecuta Flyway contra H2 en memoria (modo PostgreSQL, como producción) y verifica que la migración aplica y que el seed contiene exactamente los países, divisas y asociaciones esperados. Es el primer test de BBDD del proyecto; vive en `arume-app` (módulo donde reside el script de migración).

## Risks / Trade-offs

- [Usuarios con `arume.yml` ISO-2 que no lo borren] → Su país caerá al fallback `esp` sin aviso. Mitigación: decisión aceptada explícitamente; se documenta en el plan de migración que debe eliminarse el fichero de configuración antes de ejecutar esta versión.
- [Símbolos monetarios ambiguos: CLP, SGD y AUD comparten `$`] → La columna `symbol` guarda el símbolo oficial sin desambiguar. Mitigación: cuando la UI muestre importes combinará símbolo y código alpha-3 según convenga; no es un problema de este change.
- [El enum y la BBDD contienen el mismo catálogo duplicado] → Durante un tiempo el enum (UI) y las tablas (BBDD) conviven como dos fuentes. Mitigación: se acepta hasta que una funcionalidad lea los catálogos de BBDD; el seed y el enum deben mantenerse alineados (los tests lo cubren).
- [Primera migración con datos: si el seed tuviera un código ISO erróneo quedaría grabado en el historial Flyway] → Mitigación: códigos verificados contra ISO 3166-1/ISO 4217 y test de migración (D8) que valida el contenido exacto.

## Migration Plan

1. La migración se aplica automáticamente al arrancar (el bean `Flyway` de `DatabaseConfiguration` ya ejecuta `flyway.migrate()`); no requiere intervención manual.
2. Los usuarios con instalación previa deben eliminar su `arume.yml` (decisión D6) y volver a completar el wizard de primer arranque.
3. Rollback: las tres tablas son nuevas y ninguna funcionalidad las usa aún; si fuera necesario revertir, basta con borrar las tablas `t3_country_currency`, `t2_currencies` y `t1_countries` y la entrada del historial Flyway.

## Open Questions

Ninguna. Las dudas de diseño se resolvieron con el usuario antes de redactar esta propuesta: relación N:M, seed limitado a los 7 países soportados, sin compatibilidad ISO-2, renombrado completo de recursos a alpha-3, sin entidades/mappers y nombres en inglés.
