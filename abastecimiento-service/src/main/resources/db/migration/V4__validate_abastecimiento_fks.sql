-- Valida las FKs agregadas previamente como NOT VALID
ALTER TABLE abastecimiento_schema.ordenes_compra
    VALIDATE CONSTRAINT fk_oc_proveedor;

ALTER TABLE abastecimiento_schema.detalle_orden_compra
    VALIDATE CONSTRAINT fk_doc_producto;
