-- Valida las FKs agregadas previamente como NOT VALID
ALTER TABLE movimiento_schema.movimientos
    VALIDATE CONSTRAINT fk_movimientos_producto;

ALTER TABLE movimiento_schema.movimientos
    VALIDATE CONSTRAINT fk_movimientos_bodega_origen;

ALTER TABLE movimiento_schema.movimientos
    VALIDATE CONSTRAINT fk_movimientos_bodega_destino;