-- Añade FKs de inventario como NOT VALID para evitar fallos si hay datos previos
ALTER TABLE inventario_schema.inventarios
    ADD CONSTRAINT fk_inventarios_producto FOREIGN KEY (producto_id) REFERENCES producto_schema.productos(id) NOT VALID;

ALTER TABLE inventario_schema.inventarios
    ADD CONSTRAINT fk_inventarios_bodega FOREIGN KEY (bodega_id) REFERENCES bodega_schema.bodegas(id) NOT VALID;

-- Para validar en una migración posterior:
-- ALTER TABLE inventario_schema.inventarios VALIDATE CONSTRAINT fk_inventarios_producto;
-- ALTER TABLE inventario_schema.inventarios VALIDATE CONSTRAINT fk_inventarios_bodega;
