INSERT INTO inventario_schema.inventarios
    (producto_id, bodega_id, stock_actual, stock_minimo, stock_maximo, punto_reposicion)
VALUES
    (1, 1, 150,  50, 500, 100),  -- Tornillo M8     en Bodega Central Santiago
    (2, 1, 200,  30, 400,  80),  -- Tuerca M8       en Bodega Central Santiago
    (3, 2,  75,  20, 300,  50),  -- Cable Eléctrico en Bodega Valparaíso
    (4, 2, 120,  40, 350,  70),  -- Soldadura 60/40 en Bodega Valparaíso
    (5, 3,  90,  25, 250,  60),  -- Pintura Acrílica en Bodega Temuco Sur
    (1, 2, 110,  50, 500, 100),  -- Tornillo M8     en Bodega Valparaíso
    (2, 3, 160,  30, 400,  80),  -- Tuerca M8       en Bodega Temuco Sur
    (3, 1,  45,  20, 300,  50);  -- Cable Eléctrico en Bodega Central Santiago
;

