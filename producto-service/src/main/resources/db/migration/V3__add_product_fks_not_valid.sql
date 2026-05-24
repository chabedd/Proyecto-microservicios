-- Añade claves foráneas como NOT VALID para evitar errores si existen filas previas
ALTER TABLE producto_schema.productos
    ADD CONSTRAINT fk_productos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor_schema.proveedores(id) NOT VALID;

-- Validar la restricción más adelante (opcional):
-- ALTER TABLE producto_schema.productos VALIDATE CONSTRAINT fk_productos_proveedor;
