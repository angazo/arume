## Context

El proyecto tiene actualmente tres módulos Gradle técnicos. `arume-db` es propietario de Flyway y de las clases generadas por MyBatis, `arume-ui` contiene los controladores JavaFX y los ficheros FXML, y `arume-app` compone Spring Boot y JavaFX. Todavía no existe una capa de dominio ni de aplicación. Véase `proposal.md` para la motivación y `specs/` para los contratos de comportamiento.

El primer vertical de negocio debe introducir los conceptos comunes de empresa y ejercicio fiscal, un concepto de serie de facturación específico de España y una vista temporal de Empresas, sin permitir que los modelos de persistencia o los controladores de UI se conviertan en la API del dominio.

## Goals / Non-Goals

**Goals:**

- Establecer `arume-core` como frontera de dominio y aplicación con pocas dependencias técnicas.
- Establecer `arume-es` como primer módulo nacional y validar la frontera del módulo con persistencia real.
- Mantener los modelos generados por MyBatis dentro de los adaptadores de persistencia.
- Preservar los campos inmutables de la identidad de la empresa y los datos históricos evolutivos.
- Modelar el estado de las series españolas por empresa, serie y ejercicio fiscal.
- Resolver las capacidades fiscales mediante contratos pequeños y un registro o factoría de módulos.
- Hacer explícita y comprobable la compatibilidad entre el esquema core y los módulos nacionales.
- Añadir una vista temporal de Empresas respaldada por casos de uso de negocio.

**Non-Goals:**

- Emisión completa de facturas o asignación de números en producción.
- VeriFactu, Facturae, cálculo de impuestos, documentos electrónicos o módulos nacionales distintos de España.
- Instalación o descubrimiento en tiempo de ejecución de JARs de plugins externos.
- Diseño final de todos los futuros módulos nacionales.
- Histórico de instantáneas de facturas antes de que exista persistencia de facturas.

## Decisions

### D1. Módulos Gradle separados desde el primer change de negocio

Se crearán `arume-core` y `arume-es` en lugar de simular la frontera mediante paquetes dentro de los módulos existentes.

```text
arume-core       ← sin dependencias de módulos técnicos
arume-db        → arume-core
arume-es        → arume-core + arume-db
arume-ui        → arume-core
arume-app       → todos los módulos instalados
```

Esto hace visibles para Gradle las dependencias accidentales. Se descarta por ahora la instalación de plugins en tiempo de ejecución porque añadiría problemas de carga de clases, empaquetado, compatibilidad y registro de UI antes de validar la frontera del dominio.

Alternativa considerada: mantener un único `arume-db` y colocar España bajo distintos paquetes. Se descarta porque las convenciones de paquetes no impiden que el código core dependa de España ni ejercitan la frontera de distribución prevista.

### D2. Tipos de dominio separados de los tipos de persistencia generados

`arume-core` definirá los tipos de dominio y los puertos de repositorio. `arume-db` implementará los puertos del core usando MyBatis y mapeará las clases generadas con forma de tabla a tipos de dominio. `arume-es` será propietario de sus adaptadores de persistencia y mappers españoles, reutilizando la infraestructura de base de datos de `arume-db`.

Alternativa considerada: exponer las clases generadas `T<n>...` como modelo de dominio. Se descarta porque acoplaría el código de negocio a los nombres de tablas, a la política de código generado y a la estructura del esquema.

### D3. La identidad de la empresa y los datos históricos tienen modelos diferentes

El agregado de empresa protegerá el código principal de identificación fiscal y la forma jurídica después de su creación. El nombre, el domicilio y la residencia fiscal se representarán como valores actuales con una vigencia histórica recuperable. Los registros fiscales locales se representarán separadamente de la identidad principal y se asociarán a una jurisdicción.

Alternativa considerada: hacer inmutable todo el registro de empresa. Se descarta porque el domicilio, la residencia y potencialmente la denominación social pueden evolucionar sin representar en todos los casos una nueva identidad fiscal.

### D4. Los ejercicios fiscales son entidades del core

Los ejercicios fiscales pertenecerán a una empresa y utilizarán fechas, estado y etiqueta explícitos. El core aplicará las reglas de orden de fechas y ausencia de solapamiento. Los módulos nacionales podrán asociar posteriormente su propia configuración fiscal.

Alternativa considerada: que España o la contabilidad sean propietarios de los ejercicios fiscales. Se descarta porque los periodos fiscales son necesarios para varios conceptos independientes del país y no son intrínsecamente españoles.

### D5. España separa la definición de serie del estado de secuencia por ejercicio fiscal

El modelo español tratará la serie como propiedad de la empresa y mantendrá un estado separado para cada ejercicio fiscal. El estado registrará si la numeración continúa o se reinicia y cuál es el último número conocido. No se considerará el histórico legal de auditoría; ese histórico lo proporcionarán en el futuro registros inmutables de facturas o asignaciones.

Alternativa considerada: almacenar únicamente un `next_number` en la serie. Se descarta porque no permite representar las decisiones de reinicio o continuidad ni el estado histórico de cada ejercicio fiscal.

### D6. Contratos pequeños de capacidades y fachada en la frontera del módulo

El core definirá contratos de capacidades reducidos como `InvoiceNumberingPolicy` y `TaxValidationPolicy`. Un módulo nacional podrá exponer una fachada que agrupe sus implementaciones para registrarlas, mientras que los casos de uso dependerán de la capacidad concreta que necesiten. Un registro o factoría resolverá una implementación por jurisdicción y capacidad.

Alternativa considerada: un único `FiscalProvider` con todas las operaciones fiscales. Se descarta porque obligaría a cada módulo nacional a implementar operaciones irrelevantes y produciría una interfaz monolítica creciente.

### D7. La UI común utiliza casos de uso del core

La vista temporal de Empresas será una funcionalidad común de la UI. Su controlador llamará a casos de uso de empresas y no consultará MyBatis directamente. `MainController` seguirá siendo responsable de la navegación y la composición, no de las reglas de empresas.

Alternativa considerada: implementar la creación de empresas directamente dentro de `MainController`. Se descarta porque convertiría la primera pantalla en un atajo arquitectónico permanente e impediría probar el comportamiento de negocio sin UI.

### D8. La compatibilidad entre los esquemas core y nacionales es explícita

Las tablas core utilizarán nombres `t<n>_` y las tablas nacionales utilizarán el código ISO alpha-2 seguido de un número incremental, como `es1_` y `es2_`. Las claves y demás objetos nacionales conservarán el mismo criterio de nombrado, por ejemplo `pk_es1`, `fk_es2_t1`, `uk_es1_code` e `ix_es1_code`. Un módulo nacional declarará la versión mínima de contrato/esquema core que necesita. La aplicación validará esa declaración antes de activar el módulo y las migraciones core precederán a las migraciones nacionales.

El esquema core mantendrá una única línea monotónica compartida por todos los países. Las versiones y los esquemas de los módulos nacionales serán independientes. Cada módulo declarará su versión mínima compatible de contrato/esquema core y la aplicación validará esa declaración.

Alternativa considerada: permitir que cada módulo nacional modifique las migraciones core. Se descarta porque haría que el esquema core dependiera del país instalado e impediría mantener una base core compartida entre países.

### D9. Historiales Flyway independientes con orquestación de dependencias

El core y cada módulo nacional utilizarán una tabla de historial Flyway y una ubicación de migraciones independientes dentro de la misma base de datos física. Un orquestador central ejecutará primero el core y después los módulos nacionales en el orden de sus dependencias. Una migración nacional solo podrá referenciar una tabla core cuando su dependencia declarada del core ya haya sido migrada.

Esto conserva el modelo de versionado plataforma/módulos: el core tiene una línea de esquema compartida, mientras que España y los futuros países pueden evolucionar sus propios historiales de esquema. En este change la base actual se descarta y no se migran sus datos. La implementación debe incluir pruebas de integración para una base nueva, la dependencia entre módulos, una migración fallida y una dependencia mediante clave foránea.

Alternativa considerada: un único historial Flyway con versiones globales coordinadas. Se descarta como diseño objetivo porque acoplaría el versionado de módulos nacionales independientes y haría menos explícita la evolución de módulos opcionales. Se mantendrá como alternativa de contingencia si las pruebas de orquestación muestran problemas inaceptables de recuperación.

### D10. La BBDD asigna los identificadores internos

Las PK de las entidades de negocio se definirán como `BIGINT GENERATED BY DEFAULT AS IDENTITY` y las FK utilizarán `BIGINT`. Los tipos de dominio envolverán el valor numérico, pero no generarán IDs. Un agregado nuevo se persistirá sin identificador asignado; el adaptador utilizará las claves generadas JDBC y devolverá el agregado con el ID que haya asignado la BBDD.

Alternativa considerada: generar UUID desde la aplicación. Se descarta porque desplaza al código una responsabilidad que ya resuelve la BBDD, complica la representación SQL y no aporta una ventaja necesaria para la BBDD compartida prevista.

### D12. La persistencia core usa la misma estructura de paquetes que los módulos nacionales

`arume-db` seguirá la convención de `arume-es`: los modelos generados vivirán en `persistence.model`, los mappers generados en `persistence.mapper.generated`, los mappers custom en `persistence.mapper.custom`, los repositorios globales que agregan ambos tipos en `persistence.mapper` y los adaptadores de dominio en `persistence.adapter`. Cuando una entidad de dominio se apoye en varias tablas con métodos generados incompatibles entre sí, se utilizarán repositorios globales separados por tabla y un único adaptador agregador.

Alternativa considerada: mantener `model`, `repository.generated` y `repository.adapter` directamente bajo el paquete técnico `db`. Se descarta porque mezcla niveles de persistencia y hace que core y módulos nacionales utilicen convenciones distintas.

### D11. Los enumerados nacionales usan códigos numéricos explícitos

Los estados enumerados propios de un módulo nacional se almacenarán como `SMALLINT` con códigos explícitos y estables. El `mbg.xml` del módulo utilizará `columnOverride` para indicar el `javaType` del enum y su `typeHandler` MyBatis. El handler será responsable de convertir el código SQL al enum y del enum al código SQL.

Para `NumberingMode`, los códigos son `1=CONTINUE` y `2=RESET_EACH_FISCAL_YEAR`. No se utilizará `EnumOrdinalTypeHandler` como contrato de persistencia porque el ordinal depende del orden de declaración del enum; se usará un handler propio con códigos definidos por el dominio.

## Risks / Trade-offs

- **[Risk]** La separación de módulos puede aumentar inicialmente la complejidad de Gradle y Spring → Mantener `arume-app` como único punto de composición y añadir pruebas de contrato de módulos antes de incorporar más países.
- **[Risk]** El histórico de la empresa puede quedar incompleto si solo se almacenan valores actuales → Exigir vigencias o registros históricos para los campos evolutivos y probar la reconstrucción de valores anteriores.
- **[Risk]** Un contador de secuencia puede divergir del histórico de facturas por concurrencia o fallos → Tratar el contador como estado operativo y diseñar registros inmutables de facturas/asignaciones antes de implementar la emisión de números.
- **[Risk]** Los historiales Flyway independientes pueden dejar los esquemas core y nacional en estados distintos después de un fallo → Validar el orquestador y el bloqueo de arranque; no activar un módulo si su dependencia no está satisfecha.
- **[Trade-off]** Los módulos nacionales compilados no permiten instalar un país sin distribuir una nueva versión de la aplicación → Preferir ahora la simplicidad operativa y posponer los plugins dinámicos hasta que varios módulos demuestren la necesidad.
- **[Trade-off]** Los registros fiscales locales del core almacenan valores independientes de la jurisdicción → Los módulos nacionales son propietarios del formato y la validación legal, lo que evita reglas nacionales en el core pero requiere rutas de validación específicas por módulo.

## Migration Plan

1. Añadir `arume-core` y `arume-es` a la configuración de Gradle y establecer la dirección de dependencias.
2. Introducir los puertos de dominio core y los adaptadores de persistencia sin modificar el flujo existente de configuración inicial.
3. Añadir el esquema core de empresas y ejercicios fiscales y el esquema español de series, conservando las fronteras de nombres `t<n>_` y `es<n>_`.
4. Añadir metadatos de compatibilidad y pruebas del orden de migración para una base nueva y sus dependencias entre módulos.
5. Añadir la vista de Empresas y las pruebas de negocio.
6. Verificar la estrategia de historiales Flyway independientes con escenarios de creación desde cero, dependencias entre módulos y fallo de migración.

No se migrará la base de datos actual. Durante este change se eliminará la base existente y la aplicación se inicializará sobre una base nueva, aplicando desde cero las migraciones core y nacionales en el orden definido. No se contempla rollback destructivo de esta base inicial.

## Open Questions

Ninguna. La implementación utilizará códigos opacos de forma jurídica cualificados por jurisdicción e historiales Flyway independientes coordinados por orden de dependencias. Los detalles de los catálogos de formas jurídicas de cada país quedan fuera de este change.
