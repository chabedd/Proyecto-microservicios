package com.microservice.inventario_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "bodega_id", nullable = false)
    private Long bodegaId;

    @Column(nullable = false)
    private int stockActual;

    @Column(nullable = false)
    private int stockMinimo;

    @Column(nullable = false)
    private int stockMaximo;

    @Column(nullable = false)
    private int puntoReposicion;

    @Column(nullable = false)
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (this.ultimaActualizacion == null) {
            this.ultimaActualizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
    }

}
