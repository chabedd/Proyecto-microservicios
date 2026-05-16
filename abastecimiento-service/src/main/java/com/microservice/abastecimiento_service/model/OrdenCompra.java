package com.microservice.abastecimiento_service.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity 
@Data
@AllArgsConstructor
@NoArgsConstructor 
public class OrdenCompra {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    private Long productoId; 
    @Enumerated(EnumType.STRING) 
    private TipoEstado estado;  
    private LocalDateTime fechaCreacion = LocalDateTime.now(); 
    private int cantidad;
}


