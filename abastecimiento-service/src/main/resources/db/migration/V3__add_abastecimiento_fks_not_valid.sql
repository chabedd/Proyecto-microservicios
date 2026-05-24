-- Añade las claves foráneas del módulo abastecimiento como NOT VALID
ALTER TABLE abastecimiento_schema.ordenes_compra
    ADD CONSTRAINT fk_oc_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor_schema.proveedores(id) NOT VALID;

ALTER TABLE abastecimiento_schema.detalle_orden_compra
    ADD CONSTRAINT fk_doc_producto FOREIGN KEY (producto_id) REFERENCES producto_schema.productos(id) NOT VALID;

-- Opcionalmente validar más adelante:
-- ALTER TABLE abastecimiento_schema.ordenes_compra VALIDATE CONSTRAINT fk_oc_proveedor;
-- ALTER TABLE abastecimiento_schema.detalle_orden_compra VALIDATE CONSTRAINT fk_doc_producto;
