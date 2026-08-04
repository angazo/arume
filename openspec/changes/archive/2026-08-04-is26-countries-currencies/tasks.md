## 1. Migración Flyway (esquema + seed)

- [x] 1.1 Crear `src/arume-app/src/main/resources/db/migration/V0.1.0.1__countries_and_currencies.sql` con el DDL de `t1_countries`, `t2_currencies` y `t3_country_currency` (columnas y constraints según spec: `pk_t1`, `uk_t1_alpha3`, `pk_t2`, `uk_t2_alpha3`, `pk_t3`, `fk_t3_t1`, `fk_t3_t2`)
- [x] 1.2 Añadir en el mismo script los INSERTs del seed: 7 países con alpha-3 en mayúsculas (724/ESP, 826/GBR, 840/USA, 152/CHL, 702/SGP, 36/AUS, 710/ZAF), 8 divisas (978/EUR/€, 826/GBP/£, 840/USD/$, 152/CLP/$, 990/CLF/UF, 702/SGD/$, 36/AUD/$, 710/ZAR/R) y las 8 asociaciones país↔divisa (CHL con CLP y CLF)

## 2. Test de la migración

- [x] 2.1 Añadir en `arume-app` un test JUnit 5 que ejecute Flyway contra H2 en memoria (`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE`) y verifique que la migración aplica sin errores
- [x] 2.2 Verificar en el test el contenido exacto del seed: filas de `t1_countries`, `t2_currencies` y `t3_country_currency` (códigos, nombres en inglés y símbolos)
- [x] 2.3 Ajustar dependencias de test en `arume-app/build.gradle` si fuera necesario (Flyway y H2 visibles en el classpath de test)

## 3. Enum Country y código de país ISO-3

- [x] 3.1 Reescribir `Country` (`arume-ui`, `ui/config/Country.java`): constantes con alpha-3 minúsculas (`ESP("esp", "es", "es")`, etc.), campos `alpha3` (identificador expuesto por `code()`) y `alpha2` (auxiliar solo para detección por locale)
- [x] 3.2 Actualizar `fromCode` para aceptar solo alpha-3 (con fallback `esp`, incluido el caso de códigos ISO-2 legacy) y `detectDefault` para mapear el alpha-2 del locale al catálogo alpha-3
- [x] 3.3 Actualizar `getLabelKey()` para generar `wizard.country.<alpha3>`

## 4. Recursos de UI (banderas e i18n)

- [x] 4.1 Renombrar los PNGs de banderas en `arume-ui/src/main/resources/icons/flags/` de ISO-2 a ISO-3: `es.png`→`esp.png`, `gb.png`→`gbr.png`, `us.png`→`usa.png`, `cl.png`→`chl.png`, `sg.png`→`sgp.png`, `au.png`→`aus.png`, `za.png`→`zaf.png`
- [x] 4.2 Renombrar las claves `wizard.country.*` a alpha-3 en `messages.properties` y `messages_es.properties`

## 5. Configuración y controladores

- [x] 5.1 Cambiar el valor por defecto de `country` de `"es"` a `"esp"` en `ArumeConfig` (constructor compacto) y `ConfigManager` (load y fallbacks)
- [x] 5.2 Revisar `FirstRunWizardController` (`resolveCountryCode`, preselección por `detectDefault`) y `MainController` (`loadConfiguredCountry`, `setupCountryFlag`) para confirmar que funcionan con alpha-3 sin cambios adicionales, ajustando lo necesario

## 6. Tests existentes

- [x] 6.1 Actualizar `CountryTest` a los códigos alpha-3 (catálogo, `fromCode` con fallback para ISO-2 legacy, `detectDefault`, `getLabelKey`)
- [x] 6.2 Actualizar `CountryResourcesTest` (PNGs y claves i18n con códigos alpha-3)
- [x] 6.3 Actualizar `ConfigManagerTest` (persistencia y fallbacks con `"esp"`, `"chl"`, `"sgp"`, etc.)

## 7. Verificación final

- [x] 7.1 Ejecutar `./gradlew build` y confirmar que compila y todos los tests pasan
- [x] 7.2 Arrancar la aplicación (`./gradlew bootRun`), borrar previamente el `arume.yml` local, completar el wizard y comprobar: país persistido en ISO-3, bandera correcta en la barra de título y migración aplicada en el log de Flyway
