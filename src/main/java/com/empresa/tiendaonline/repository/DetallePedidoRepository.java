package com.empresa.tiendaonline.repository;

import com.empresa.tiendaonline.entity.DetallePedido;
import com.empresa.tiendaonline.entity.Pedido;
import com.empresa.tiendaonline.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {

    Optional<DetallePedido> findByPedidoAndProducto(
            Pedido pedido,
            Producto producto
    );


    
}