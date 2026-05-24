INSERT INTO movimiento_schema.movimientos
    (producto_id, bodega_origen_id, bodega_destino_id, tipo, cantidad, motivo,fecha)
VALUES
    (1, NULL, 1,    'ENTRADA',        50, 'Recepción inicial de stock — proveedor Distribuciones Andina', '2025-01-12 09:15:00'),
    (2, 1,    NULL, 'SALIDA',         20, 'Despacho a cliente — pedido interno', '2025-01-18 14:40:00'),
    (3, 1,    2,    'TRANSFERENCIA',  30, 'Redistribución entre Bodega Central y Bodega Valparaíso', '2025-02-03 11:25:00'),
    (4, NULL, 3,    'ENTRADA',       100, 'Ingreso por orden de compra OC-003 recibida', '2025-02-20 08:50:00'),
    (5, 2,    NULL, 'SALIDA',         15, 'Despacho pedido externo — salida bodega Valparaíso', '2025-03-01 16:10:00');
