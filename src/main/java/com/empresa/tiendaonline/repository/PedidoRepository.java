package com.empresa.tiendaonline.repository;

import com.empresa.tiendaonline.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.empresa.tiendaonline.entity.EstadoPedido;
import com.empresa.tiendaonline.entity.Usuario;

import java.util.List;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
Optional<Pedido> findByUsuarioAndEstado(
        Usuario usuario,
        EstadoPedido estado
);

Optional<Pedido>
findTopByUsuarioAndEstadoOrderByIdPedidoDesc(
        Usuario usuario,
        EstadoPedido estado
);

List<Pedido> findByUsuarioOrderByIdPedidoDesc(
        Usuario usuario
);

}