## Why

La aplicación necesitará muy pronto datos maestros de países y divisas (p. ej. al crear facturas: país del cliente, divisa, símbolo monetario para mostrar importes). Hoy el catálogo de países solo existe como un enum dentro del módulo de UI, codificado con códigos ISO 3166-1 alpha-2, y no hay ninguna tabla en la base de datos ni rastro de divisas. Queremos que la base de datos sea la fuente autoritativa de estos catálogos, usando los estándares ISO 3166-1 e ISO 4217, y homogeneizar todo el producto en torno al código alpha-3 de país.

## What Changes

- Nueva migración Flyway que crea tres tablas:
  - `t1_countries`: países según ISO 3166-1. Código numérico de 3 dígitos como PK (`SMALLINT`, rango 0–999), código alpha-3 con restricción UNIQUE, y nombre del país en inglés.
  - `t2_currencies`: divisas según ISO 4217. Código numérico como PK (`SMALLINT`), código alpha-3 UNIQUE, nombre en inglés y símbolo monetario (p. ej. `€`).
  - `t3_country_currency`: relación N:M entre países y divisas (un país puede tener varias divisas y una divisa puede usarse en varios países).
- La migración inserta como datos semilla los 7 países que soporta actualmente el producto (España, Reino Unido, Estados Unidos, Chile, Singapur, Australia y Sudáfrica), sus divisas (EUR, GBP, USD, CLP, SGD, AUD, ZAR) y la asociación país↔divisa correspondiente.
- **BREAKING**: el código de país pasa de ISO-2 a ISO-3 en todo el producto:
  - El enum `Country` pasa a usar códigos alpha-3 en minúsculas (`esp`, `gbr`, `usa`, `chl`, `sgp`, `aus`, `zaf`).
  - La clave `arume.country` de `arume.yml` se persiste en ISO-3. No se ofrece compatibilidad hacia atrás con valores ISO-2: los ficheros de configuración existentes deberán regenerarse (el wizard de primer arranque vuelve a ejecutarse).
  - Se renombran los PNGs de banderas (`icons/flags/es.png` → `icons/flags/esp.png`, etc.) y las claves i18n (`wizard.country.es` → `wizard.country.esp`, etc.) para mantener la homogeneidad.
- No se crean todavía entidades ni mappers MyBatis para las nuevas tablas: este change se limita al esquema y a los datos. El acceso a datos se añadirá cuando una funcionalidad lo necesite.

## Capabilities

### New Capabilities

- `country-currency-catalog`: catálogos de países (ISO 3166-1) y divisas (ISO 4217) en la base de datos, su relación N:M, y los datos semilla con los países soportados por el producto.

### Modified Capabilities

- `country-selection`: el identificador de país pasa de ISO-2 a ISO-3 en el enum `Country`, en la persistencia (`arume.country`), en los recursos de banderas y en las claves i18n; se elimina la compatibilidad con valores ISO-2 previos.

## Impact

- **Migraciones**: nuevo script `V0.2.0.0__*.sql` en `arume-app/src/main/resources/db/migration/` (DDL + datos semilla).
- **arume-ui**: enum `Country` (`ui/config/Country.java`), `FirstRunWizardController` (resolución de código de país), `MainController` (carga de la bandera), `ConfigManager`/`ArumeConfig` (valor por defecto de `country`), bundles i18n (`messages.properties`, `messages_es.properties`) y PNGs de banderas en `icons/flags/`.
- **Tests**: actualización de `CountryTest`, `CountryResourcesTest` y `ConfigManagerTest` a los nuevos códigos alpha-3.
- **Configuración de usuarios existentes**: los `arume.yml` con país en ISO-2 dejan de ser compatibles; se asume su regeneración mediante el wizard.
