## 1. Mover las migraciones a arume-db

- [ ] 1.1 Mover `V0.1.0.0__init_schema.sql` y `V0.1.0.1__countries_and_currencies.sql` a `arume-db/src/main/resources/db/migration/`
- [ ] 1.2 Eliminar las copias de `arume-app/src/main/resources/db/migration/` y `arume-app/build/resources/main/db/migration/`

## 2. Configurar la tarea mbGenerator

- [ ] 2.1 Definir en `arume-db/build.gradle` `mbgenUrl`, `mbgenUser` y `mbgenPassword` y pasarlos como argumentos a `MbGeneratorMain`
- [ ] 2.2 Añadir las migraciones como `inputs` de la tarea `mbGenerator` (junto a `MyBatis/mbg.xml`)
- [ ] 2.3 Cambiar en `MyBatis/mbg.xml` la conexión a propiedades inyectadas `${mbgen.url}`, `${mbgen.user}` y `${mbgen.password}`

## 3. Aplicar Flyway antes de generar

- [ ] 3.1 Ampliar `MbGeneratorMain.main` para ejecutar `Flyway.migrate()` (con `mbgenUrl`/`mbgenUser`/`mbgenPassword` y `locations("classpath:db/migration")`) antes de lanzar MyBatis Generator
- [ ] 3.2 Inyectar las propiedades `${mbgen.*}` en el `ConfigurationParser` junto a `projectDir`

## 4. Limpieza y documentación

- [ ] 4.1 Eliminar `arume-db/docs/h2/` (ficheros `mybatis.mv.db` y `mybatis.trace.db`)
- [ ] 4.2 Actualizar `AGENTS.md`: ubicación de las migraciones y funcionamiento de `mbGenerator` (BBDD en memoria + Flyway)

## 5. Verificación

- [ ] 5.1 `./gradlew :arume-db:mbGenerator` genera las entidades/mappers y la tabla `t3_country_currency` resuelve
- [ ] 5.2 `./gradlew build` pasa (arranque, test de migraciones y módulos)
- [ ] 5.3 Ejecutar dos veces seguidas `mbGenerator` y confirmar que no deja BBDD abierta (sin bloqueos)
