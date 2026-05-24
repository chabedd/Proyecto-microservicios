# Proyecto Microservicios

Proyecto académico basado en Spring Boot con microservicios para inventario, proveedor, producto, bodega, abastecimiento, movimiento, configuración central y eureka.

## Arquitectura

- `config-microservice`: configuración centralizada con Spring Cloud Config.
- `eureka-microservice`: descubrimiento de servicios.
- `inventario-service`: gestión de stock e integración con producto y bodega.
- `proveedor-service`: gestión de proveedores.
- `producto-service`: gestión de productos.
- `bodega-service`: gestión de bodegas.
- `movimiento-service`: movimientos de inventario.
- `abastecimiento-service`: abastecimiento.

## Persistencia

Cada microservicio usa Spring Data JPA, Hibernate y PostgreSQL. La base se organiza por esquemas separados.

- `inventario_schema`
- `proveedor_schema`
- `producto_schema`
- `bodega_schema`

Las migraciones iniciales están en `src/main/resources/db/migration`.

## Endpoints principales

### Inventario
- `POST /api/inventarios`
- `GET /api/inventarios`
- `GET /api/inventarios/{id}`
- `GET /api/inventarios/producto/{productoId}`
- `GET /api/inventarios/bodega/{bodegaId}`
- `POST /api/inventarios/ajustar`
- `DELETE /api/inventarios/{id}`

### Proveedor
- `POST /api/proveedores`
- `GET /api/proveedores`
- `GET /api/proveedores/{id}`
- `GET /api/proveedores/rut/{rut}`
- `GET /api/proveedores/activos/listar`
- `PUT /api/proveedores/{id}`
- `PUT /api/proveedores/{id}/activar`
- `PUT /api/proveedores/{id}/desactivar`
- `DELETE /api/proveedores/{id}`

### Producto
- `POST /api/productos`
- `GET /api/productos`
- `GET /api/productos/{id}`
- `GET /api/productos/categoria/{categoria}`
- `PUT /api/productos/{id}`
- `DELETE /api/productos/{id}`

### Bodega
- `POST /api/bodegas`
- `GET /api/bodegas`
- `GET /api/bodegas/{id}`
- `PUT /api/bodegas/{id}`
- `DELETE /api/bodegas/{id}`

### Movimiento (movimiento-service)
- `POST /api/movimientos`  — crear movimiento de inventario (entrada/salida)
- `GET /api/movimientos`
- `GET /api/movimientos/{id}`
- `GET /api/movimientos/producto/{productoId}`
- `GET /api/movimientos/bodega/{bodegaId}`

### Abastecimiento (abastecimiento-service)
- `POST /api/abastecimientos` — solicitar abastecimiento
- `GET /api/abastecimientos`
- `GET /api/abastecimientos/{id}`
- `PUT /api/abastecimientos/{id}/procesar`

### Configuración central (config-microservice)
- Provee archivos de configuración por aplicación/ambiente a través de Spring Cloud Config.

### Descubrimiento (eureka-microservice)
- Servicio de registro y descubrimiento (Eureka). Todos los microservicios se registran aquí.

> Nota: este README resume los endpoints más utilizados; los servicios contienen más rutas internas y DTOs.

## Reglas relevantes

- Inventario valida stock mínimo, máximo, reposición y unicidad por `productoId + bodegaId`.
- Inventario verifica existencia remota de producto y bodega mediante Feign.
- Proveedor valida campos con Bean Validation y maneja RUT duplicado.


El proyecto permite explicar modelado de datos, reglas de negocio, manejo de errores, persistencia con JPA y comunicación entre microservicios.

## Orden de arranque recomendado

- Pre-requisitos: levantar PostgreSQL y el Config Server.
- `config-microservice`: servidor de configuración (proporciona propiedades a los microservicios).
- `eureka-microservice`: servidor de registro(Eureka).
- Servicios que exponen datos referenciados por otros servicios:
	- `producto-service`
	- `bodega-service`
	- `proveedor-service`
- `inventario-service`: depende de `producto` y `bodega` para validaciones remotas.
- `movimiento-service`: registra movimientos y depende de `inventario`, `producto` y `bodega`.
- `abastecimiento-service`: procesa solicitudes de abastecimiento y depende de `proveedor` e `inventario`.

