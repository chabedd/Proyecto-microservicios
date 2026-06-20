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

## Endpoints principales (Consultas REST)

Todas las consultas REST deben realizarse preferentemente a través del **API Gateway** (puerto `8080`), el cual redirige las peticiones internamente a los microservicios correspondientes de forma balanceada.

### Puertos y Mapeo de Rutas

| Servicio / Microservicio | URL a través del Gateway | Puerto Directo (Local) |
| :--- | :--- | :--- |
| **API Gateway** | `http://localhost:8080` | `8080` |
| **Eureka Server (Dashboard)** | — | `8761` |
| **Config Server** | — | `8888` |
| **producto-service** | `http://localhost:8080/api/productos` | `8081` |
| **inventario-service** | `http://localhost:8080/api/inventarios` | `8082` |
| **bodega-service** | `http://localhost:8080/api/bodegas` | `8083` |
| **abastecimiento-service**| `http://localhost:8080/api/abastecimientos` | `8084` |
| **movimiento-service** | `http://localhost:8080/api/movimientos` | `8085` |
| **proveedor-service** | `http://localhost:8080/api/proveedores` | `8086` |

### Swagger / OpenAPI UI Centralizado

Puedes acceder a la interfaz de Swagger UI unificada de todos los microservicios a través de la dirección del API Gateway:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

> **Instrucciones:** En la parte superior derecha de la página de Swagger, verás un menú desplegable (un selector llamado *"Select a definition"*) que te permitirá alternar entre las documentaciones de **Productos**, **Inventarios**, **Bodegas**, **Abastecimientos**, **Movimientos** y **Proveedores**.

---

### Detalle de Endpoints y Ejemplos de Carga (Payloads)

#### 1. Proveedores (`proveedor-service`)
* `GET /api/proveedores` - Listar todos los proveedores
* `GET /api/proveedores/{id}` - Obtener un proveedor por ID
* `GET /api/proveedores/rut/{rut}` - Obtener un proveedor por su RUT
* `GET /api/proveedores/activos/listar` - Listar proveedores activos
* `POST /api/proveedores` - Registrar un nuevo proveedor
  * **Ejemplo JSON:**
    ```json
    {
      "nombre": "Distribuidora Industrial S.A.",
      "rut": "77.654.321-0",
      "email": "contacto@distribuidorasa.cl",
      "telefono": "+56912345678",
      "direccion": "Av. Providencia 1234, Oficina 501, Santiago"
    }
    ```
* `PUT /api/proveedores/{id}` - Actualizar datos del proveedor
* `PUT /api/proveedores/{id}/activar` - Activar proveedor
* `PUT /api/proveedores/{id}/desactivar` - Desactivar proveedor
* `DELETE /api/proveedores/{id}` - Eliminar un proveedor

#### 2. Productos (`producto-service`)
* `GET /api/productos` - Listar todos los productos
* `GET /api/productos/{id}` - Obtener un producto por ID
* `GET /api/productos/categoria/{categoria}` - Listar productos por categoría
* `POST /api/productos` - Registrar un nuevo producto
  * **Ejemplo JSON:**
    ```json
    {
      "codigo": "PROD-100",
      "nombre": "Tornillo de Acero 1/2",
      "precioBase": 1250.0,
      "proveedorId": 1
    }
    ```
* `PUT /api/productos/{id}` - Actualizar un producto
* `DELETE /api/productos/{id}` - Eliminar un producto

#### 3. Bodegas (`bodega-service`)
* `GET /api/bodegas` - Listar todas las bodegas
* `GET /api/bodegas/{id}` - Obtener bodega por ID
* `POST /api/bodegas` - Registrar una nueva bodega
  * **Ejemplo JSON:**
    ```json
    {
      "nombre": "Bodega Central Norte",
      "ubicacion": "Avenida Industrial 450, Quilicura",
      "capacidadMaximaItems": 5000
    }
    ```
* `PUT /api/bodegas/{id}` - Actualizar bodega
* `DELETE /api/bodegas/{id}` - Eliminar bodega

#### 4. Inventarios (`inventario-service`)
* `GET /api/inventarios` - Listar todos los registros de inventario
* `GET /api/inventarios/{id}` - Obtener inventario por ID
* `GET /api/inventarios/producto/{productoId}` - Consultar inventario por producto
* `GET /api/inventarios/bodega/{bodegaId}` - Consultar inventario de una bodega
* `POST /api/inventarios` - Inicializar inventario de un producto en una bodega
  * **Ejemplo JSON:**
    ```json
    {
      "productoId": 1,
      "bodegaId": 2,
      "stockActual": 100
    }
    ```
* `POST /api/inventarios/ajustar` - Realizar un ajuste manual de stock (incrementar/decrementar)
  * **Ejemplo JSON:**
    ```json
    {
      "productoId": 1,
      "bodegaId": 2,
      "delta": 50
    }
    ```
* `DELETE /api/inventarios/{id}` - Eliminar un registro de inventario

#### 5. Movimientos de Stock (`movimiento-service`)
* `GET /api/movimientos` - Listar historial de movimientos
* `GET /api/movimientos/{id}` - Obtener movimiento por ID
* `GET /api/movimientos/producto/{productoId}` - Listar movimientos de un producto
* `GET /api/movimientos/bodega/{bodegaId}` - Listar movimientos de una bodega
* `POST /api/movimientos` - Registrar un movimiento físico (ENTRADA, SALIDA, TRANSFERENCIA)
  * **Ejemplo JSON:**
    ```json
    {
      "productoId": 1,
      "bodegaOrigenId": 1,
      "bodegaDestinoId": 2,
      "tipo": "TRANSFERENCIA",
      "cantidad": 50,
      "motivo": "Reubicación de stock estacional"
    }
    ```

#### 6. Abastecimiento (`abastecimiento-service`)
* `GET /api/abastecimientos` - Listar todas las solicitudes/órdenes de compra
* `GET /api/abastecimientos/{id}` - Obtener orden de compra por ID
* `POST /api/abastecimientos` - Solicitar abastecimiento (Generar orden de compra)
  * **Ejemplo JSON:**
    ```json
    {
      "proveedorId": 1,
      "detalles": [
        {
          "productoId": 1,
          "cantidad": 100,
          "precioUnitario": 1150.0
        }
      ]
    }
    ```
* `PUT /api/abastecimientos/{id}/procesar` - Procesar/aprobar una orden de compra para ingresar los productos al inventario

---

> Nota: Este listado detalla los endpoints principales y sus firmas más comunes. Asegúrate de iniciar primero el `config-microservice` y el `eureka-microservice` antes de consumir los endpoints a través del puerto `8080`.

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

