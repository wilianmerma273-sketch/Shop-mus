package com.empresa.tiendaonline.repository;




import com.empresa.tiendaonline.entity.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioRepository
        extends JpaRepository<Envio, Long> {
}