## 1. Base de los módulos

- [x] 1.1 Añadir `arume-core` y `arume-es` al build Gradle multimódulo con la dirección de dependencias definida en el diseño.
- [x] 1.2 Crear las fronteras de paquetes del core para tipos de dominio, casos de uso y puertos de repositorio/capacidades sin dependencias de Spring, MyBatis ni JavaFX.
- [x] 1.3 Crear la frontera del módulo español y su descriptor de módulo con la identidad de jurisdicción y la versión mínima compatible del contrato/esquema core.
- [x] 1.4 Añadir comprobaciones arquitectónicas o pruebas de dependencias que impidan que `arume-core` dependa de `arume-es` o de módulos técnicos de UI/persistencia.

## 2. Dominio core de empresa

- [x] 2.1 Definir los tipos de identidad de empresa con un código opaco de forma jurídica cualificado por jurisdicción y un código fiscal principal protegido.
- [x] 2.2 Implementar los casos de uso de creación y listado de empresas con puertos de repositorio y validación de la identidad y los datos evolutivos actuales obligatorios.
- [x] 2.3 Implementar el histórico de nombre legal, domicilio y residencia fiscal mediante vigencias o registros históricos equivalentes.
- [x] 2.4 Implementar los registros fiscales locales asociados a una jurisdicción sin permitir que modifiquen la identidad fiscal principal de la empresa.
- [x] 2.5 Añadir pruebas unitarias de creación de empresa, rechazo de identidad duplicada, campos de identidad inmutables, histórico de datos evolutivos y registros locales.

## 3. Dominio core de ejercicio fiscal

- [x] 3.1 Definir el tipo de dominio de ejercicio fiscal, su estado y el puerto de repositorio asociado a una empresa.
- [x] 3.2 Implementar la creación de ejercicios fiscales con fechas explícitas, etiquetas y soporte para periodos cortos.
- [x] 3.3 Aplicar las reglas de fechas inválidas y solapamiento dentro de una misma empresa, permitiendo periodos iguales para empresas diferentes.
- [x] 3.4 Añadir pruebas unitarias del estado abierto/cerrado y de las invariantes de los periodos fiscales.

## 4. Fronteras de persistencia y migraciones

- [x] 4.1 Definir el contrato de módulo de migraciones, incluyendo identificador de módulo, versión de esquema, dependencia mínima del core y metadatos de ubicación/historial de migraciones.
- [x] 4.2 Implementar el orquestador de migraciones con historiales Flyway independientes para core y módulos nacionales, ejecutando primero el core y después los países dependientes.
- [x] 4.3 Añadir las migraciones core de empresas, histórico/registros locales y ejercicios fiscales utilizando la convención de nombres `t<n>_`.
- [x] 4.4 Añadir las migraciones españolas de series y estado de secuencia por ejercicio fiscal utilizando la convención `es<n>_` y los nombres correspondientes de PK/FK/UK/índices hacia tablas core.
- [x] 4.5 Generar o crear modelos y mappers de persistencia por módulo, manteniendo los tipos generados de MyBatis detrás de los adaptadores de repositorio.
- [x] 4.6 Implementar los adaptadores de repositorio core que mapeen los modelos de persistencia a tipos de dominio.
- [x] 4.7 Implementar los adaptadores de repositorio españoles para series y estado de secuencia por ejercicio fiscal.
- [x] 4.8 Añadir pruebas de integración H2/Flyway para instalación nueva, orden core antes que España, compatibilidad declarada, migración fallida y claves foráneas entre módulos.

## 5. Registro de capacidades fiscales

- [x] 5.1 Definir los contratos reducidos de capacidades core y el registro/factoría de módulos utilizado para resolver una capacidad por jurisdicción.
- [x] 5.2 Implementar la fachada del módulo español y registrar sus capacidades disponibles sin introducir condicionales específicos de España en los casos de uso core.
- [x] 5.3 Añadir pruebas de resolución de una capacidad instalada y de devolución controlada de capacidad no disponible para un módulo no instalado.

## 6. Series españolas de facturación

- [x] 6.1 Definir el dominio español de series de facturación y las reglas de unicidad del código dentro de una empresa.
- [x] 6.2 Definir el estado de serie por ejercicio fiscal con modos de continuidad y reinicio, estado activo y último número conocido asignado.
- [x] 6.3 Implementar los casos de uso y puertos de repositorio para crear y configurar series y consultar su estado histórico por ejercicio.
- [x] 6.4 Garantizar que este change no emite números de factura en producción ni trata el estado de secuencia como el futuro histórico inmutable de facturas.
- [x] 6.5 Añadir pruebas unitarias y de integración de códigos duplicados, empresas diferentes, continuidad, reinicio y estado histórico por ejercicio.

## 7. UI de Empresas

- [x] 7.1 Añadir la entrada temporal Empresas en la barra lateral derecha de la ventana principal y una vista central de Empresas cargada mediante el controller factory de Spring existente.
- [x] 7.2 Implementar el FXML y el controlador de Empresas para listar y crear empresas mediante los casos de uso core.
- [x] 7.3 Añadir ids CSS estables, etiquetas internacionalizadas y mensajes de validación sin introducir lógica de persistencia en el controlador JavaFX.
- [x] 7.4 Añadir cobertura TestFX para cargar la vista real de Empresas, crear una empresa válida y mostrarla en la lista resultante.

## 8. Verificación y documentación

- [x] 8.1 Actualizar el escaneo de componentes Spring y mappers para que `arume-app` componga los beans core, de persistencia y españoles sin romper el flujo existente de primer arranque.
- [x] 8.2 Verificar las tareas de generación de código y los recursos de migración de todos los módulos afectados.
- [x] 8.3 Ejecutar la suite completa de pruebas unitarias, de integración y de UI en el entorno gráfico/Xvfb soportado.
- [x] 8.4 Validar el change OpenSpec y reconciliar `Product-Spec.md` con las decisiones de implementación que difieran del diseño aprobado.
- [x] 8.5 Ajustar los identificadores internos de entidades y FK a `BIGINT GENERATED BY DEFAULT AS IDENTITY`, recuperar las claves generadas mediante MyBatis y verificar la persistencia concurrente a nivel de motor.
- [x] 8.6 Mapear `numbering_mode` como `SMALLINT` mediante un enum Java con códigos explícitos, `columnOverride` y un `typeHandler` MyBatis propio en el módulo español.
- [x] 8.7 Homogeneizar `arume-db` con la estructura `persistence/model`, `persistence/mapper` y `persistence/adapter`, manteniendo repositorios globales separados cuando las tablas de un agregado tengan mappers incompatibles.
