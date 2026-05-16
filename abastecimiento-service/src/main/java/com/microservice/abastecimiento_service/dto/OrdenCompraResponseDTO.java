package com.microservice.abastecimiento_service.dto;




import com.microservice.abastecimiento_service.model.TipoEstado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenCompraResponseDTO {
    private Long id; 
    private Long productoId; 
    private TipoEstado estado;
    private int cantidad;

}
