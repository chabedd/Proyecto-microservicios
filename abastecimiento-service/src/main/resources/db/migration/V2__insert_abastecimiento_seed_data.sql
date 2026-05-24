
INSERT INTO abastecimiento_schema.ordenes_compra(proveedor_id, estado, fecha_creacion)
VALUES
(1, 'PENDIENTE', '2025-01-10 09:30:00'),
(2, 'APROBADA',  '2025-01-15 14:20:00'),
(3, 'RECIBIDA',  '2025-02-01 11:45:00'),
(1, 'CANCELADA', '2025-02-10 16:00:00'),
(4, 'PENDIENTE', '2025-03-05 08:15:00');

-- Detalles de la OC 1 (PENDIENTE — proveedor 1)
INSERT INTO abastecimiento_schema.detalle_orden_compra(orden_compra_id, producto_id, cantidad, precio_unitario)
VALUES
(1, 1,  50,  1.20),
(1, 2, 100,  0.85);

-- Detalles de la OC 2 (APROBADA — proveedor 2)
INSERT INTO abastecimiento_schema.detalle_orden_compra(orden_compra_id, producto_id, cantidad, precio_unitario)
VALUES
(2, 3,   5, 125.00),
(2, 4,  10,  22.50);

-- Detalles de la OC 3 (RECIBIDA — proveedor 3)
INSERT INTO abastecimiento_schema.detalle_orden_compra(orden_compra_id, producto_id, cantidad, precio_unitario)
VALUES
(3, 5,   3,  75.00),
(3, 1, 200,   1.20);

-- Detalles de la OC 4 (CANCELADA — proveedor 1)
INSERT INTO abastecimiento_schema.detalle_orden_compra(orden_compra_id, producto_id, cantidad, precio_unitario)
VALUES(4, 2,  80,  0.85);

-- Detalles de la OC 5 (PENDIENTE — proveedor 4)
INSERT INTO abastecimiento_schema.detalle_orden_compra(orden_compra_id, producto_id, cantidad, precio_unitario)
VALUES(5, 4,  20,  22.50);
