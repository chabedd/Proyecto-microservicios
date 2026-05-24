-- Valida la FK agregada previamente como NOT VALID
ALTER TABLE producto_schema.productos
    VALIDATE CONSTRAINT fk_productos_proveedor;
