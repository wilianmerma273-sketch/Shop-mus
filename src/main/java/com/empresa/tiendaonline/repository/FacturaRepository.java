package com.empresa.tiendaonline.repository;

import com.empresa.tiendaonline.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacturaRepository
        extends JpaRepository<Factura, Long> {

    Optional<Factura>
    findTopByOrderByIdFacturaDesc();
}