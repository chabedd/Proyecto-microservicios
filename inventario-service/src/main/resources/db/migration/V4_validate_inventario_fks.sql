-- Valida las FKs agregadas previamente como NOT VALID
ALTER TABLE inventario_schema.inventarios
    VALIDATE CONSTRAINT fk_inventarios_producto;

ALTER TABLE inventario_schema.inventarios
    VALIDATE CONSTRAINT fk_inventarios_bodega;
