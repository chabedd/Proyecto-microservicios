package com.microservice.producto_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Producto {
    @Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true,name="sku")
    private String codigo;
    @Column(nullable=false)
    private String nombre;
    @Column(nullable=false)
    private Double precioBase;
    @Column(nullable=false)
    private Long proveedorId;

}
