package com.empresa.tiendaonline.service;

import com.empresa.tiendaonline.entity.DetallePedido;
import com.empresa.tiendaonline.entity.EstadoPedido;
import com.empresa.tiendaonline.entity.Pedido;
import com.empresa.tiendaonline.entity.Producto;
import com.empresa.tiendaonline.entity.Usuario;
import com.empresa.tiendaonline.repository.DetallePedidoRepository;
import com.empresa.tiendaonline.repository.PedidoRepository;
import com.empresa.tiendaonline.repository.ProductoRepository;
import com.empresa.tiendaonline.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {

        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Pedido obtenerOCrearCarrito(
            String emailUsuario) {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(emailUsuario)
                        .orElseThrow();

        return pedidoRepository
                .findByUsuarioAndEstado(
                        usuario,
                        EstadoPedido.CARRITO
                )
                .orElseGet(() -> {

                    Pedido pedido =
        Pedido.builder()
                .usuario(usuario)
                .estado(EstadoPedido.CARRITO)
                .total(BigDecimal.ZERO)
                .totalFinal(BigDecimal.ZERO)
                .build();

                    return pedidoRepository.save(pedido);
                });
    }


   
@Transactional
public void agregarProducto(
        Pedido pedido,
        Long idProducto) {

    Producto producto =
            productoRepository
                    .findById(idProducto)
                    .orElseThrow();

    Optional<DetallePedido> detalleExistente =
            detallePedidoRepository
                    .findByPedidoAndProducto(
                            pedido,
                            producto
                    );

    if (detalleExistente.isPresent()) {

        DetallePedido detalle =
                detalleExistente.get();

        if(detalle.getCantidad() >= producto.getStock()){
            throw new RuntimeException(
                    "Stock insuficiente"
            );
        }

        detalle.setCantidad(
                detalle.getCantidad() + 1
        );

        detallePedidoRepository.save(detalle);

    } else {

        if(producto.getStock() <= 0){
            throw new RuntimeException(
                    "Producto sin stock"
            );
        }

        DetallePedido detalle =
                DetallePedido.builder()
                        .pedido(pedido)
                        .producto(producto)
                        .cantidad(1)
                        .precioUnitario(producto.getPrecio())
                        .build();

        detallePedidoRepository.save(detalle);
    }

    recalcularTotal(pedido);
}
    @Transactional(readOnly = true)
public Pedido obtenerCarrito(String emailUsuario) {

    Usuario usuario =
            usuarioRepository
                    .findByEmail(emailUsuario)
                    .orElseThrow();

    return pedidoRepository
            .findByUsuarioAndEstado(
                    usuario,
                    EstadoPedido.CARRITO
            )
            .orElse(
                    Pedido.builder()
                            .usuario(usuario)
                            .estado(EstadoPedido.CARRITO)
                            .build()
            );
}
public java.math.BigDecimal calcularTotal(
        Pedido pedido) {

    return pedido.getDetalles()
            .stream()
            .map(detalle ->
                    detalle.getPrecioUnitario()
                            .multiply(
                                    java.math.BigDecimal.valueOf(
                                            detalle.getCantidad()
                                    )
                            )
            )
            .reduce(
                    java.math.BigDecimal.ZERO,
                    java.math.BigDecimal::add
            );
}


@Transactional
public void aumentarCantidad(Long idDetalle) {

    DetallePedido detalle =
            detallePedidoRepository
                    .findById(idDetalle)
                    .orElseThrow();



Producto producto =
        detalle.getProducto();

if(detalle.getCantidad() >= producto.getStock()){
    throw new RuntimeException(
            "Stock insuficiente"
    );
}


    detalle.setCantidad(
            detalle.getCantidad() + 1
    );

    detallePedidoRepository.save(detalle);

    recalcularTotal(detalle.getPedido());
}


@Transactional
public void disminuirCantidad(Long idDetalle) {

    DetallePedido detalle =
            detallePedidoRepository
                    .findById(idDetalle)
                    .orElseThrow();

    Pedido pedido = detalle.getPedido();

    if(detalle.getCantidad() > 1){

        detalle.setCantidad(
                detalle.getCantidad() - 1
        );

        detallePedidoRepository.save(detalle);

    } else {

        detallePedidoRepository.delete(detalle);
    }

    recalcularTotal(pedido);
}

@Transactional
public void eliminarDetalle(Long idDetalle){

    DetallePedido detalle =
            detallePedidoRepository
                    .findById(idDetalle)
                    .orElseThrow();

    Pedido pedido = detalle.getPedido();

    pedido.getDetalles().remove(detalle);

    detallePedidoRepository.delete(detalle);

    recalcularTotal(pedido);
}

private void recalcularTotal(Pedido pedido){

    BigDecimal total = pedido.getDetalles()
            .stream()
            .map(detalle ->
                    detalle.getPrecioUnitario()
                           .multiply(
                                   BigDecimal.valueOf(
                                           detalle.getCantidad()
                                   )
                           )
            )
            .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
            );

    pedido.setTotal(total);

    pedidoRepository.save(pedido);
}
public BigDecimal calcularDescuento(Pedido pedido){

    return calcularTotal(pedido)
            .multiply(new BigDecimal("0.20"));
}

public BigDecimal calcularEnvio(){

    return new BigDecimal("20");
}

public BigDecimal calcularTotalFinal(Pedido pedido){

    BigDecimal subtotal = calcularTotal(pedido);

    BigDecimal descuento =
            calcularDescuento(pedido);

    BigDecimal envio =
            calcularEnvio();

    return subtotal
            .subtract(descuento)
            .add(envio);
}


@Transactional
public Pedido obtenerCarritoActivo(
        String emailUsuario){

    Usuario usuario =
            usuarioRepository
                    .findByEmail(emailUsuario)
                    .orElseThrow();

    return pedidoRepository
            .findByUsuarioAndEstado(
                    usuario,
                    EstadoPedido.CARRITO
            )
            .orElseThrow();
}
@Transactional
public Pedido guardar(Pedido pedido){

    return pedidoRepository.save(pedido);
}

@Transactional(readOnly = true)
public Pedido obtenerUltimoPendiente(
        String emailUsuario){

    Usuario usuario =
            usuarioRepository
                    .findByEmail(emailUsuario)
                    .orElseThrow();

    return pedidoRepository
            .findTopByUsuarioAndEstadoOrderByIdPedidoDesc(
                    usuario,
                    EstadoPedido.PENDIENTE
            )
            .orElseThrow();
}
@Transactional
public Pedido completarPedido(
        String emailUsuario){

    Pedido pedido =
            obtenerUltimoPendiente(emailUsuario);

    BigDecimal subtotal =
            calcularTotal(pedido);

    BigDecimal descuento =
            calcularDescuento(pedido);

    BigDecimal envio =
            calcularEnvio();

    BigDecimal totalFinal =
            subtotal
                    .subtract(descuento)
                    .add(envio);

    pedido.setTotal(subtotal);

    pedido.setTotalFinal(totalFinal);
for(DetallePedido detalle
        : pedido.getDetalles()) {

    Producto producto =
            detalle.getProducto();

    if(producto.getStock()
            < detalle.getCantidad()) {

        throw new RuntimeException(
                "Stock insuficiente para "
                + producto.getNombre()
        );
    }

    producto.setStock(
            producto.getStock()
            - detalle.getCantidad()
    );

    productoRepository.save(producto);
}
    pedido.setEstado(
            EstadoPedido.COMPLETADO
    );

    return pedidoRepository.save(pedido);
}

@Transactional(readOnly = true)
public List<Pedido> obtenerPedidosUsuario(
        String emailUsuario){

    Usuario usuario =
            usuarioRepository
                    .findByEmail(emailUsuario)
                    .orElseThrow();

    return pedidoRepository
            .findByUsuarioOrderByIdPedidoDesc(
                    usuario
            );
}


@Transactional
public void importarCarritoSesion(
        String emailUsuario,
        List<Long> productos) {

    Pedido carrito =
            obtenerOCrearCarrito(
                    emailUsuario
            );

    for(Long idProducto : productos){

        agregarProducto(
                carrito,
                idProducto
        );
    }
}

@Transactional
public void importarCarritoSesion(
        String emailUsuario,
        Map<Long,Integer> productosSesion) {

    Pedido carrito =
            obtenerOCrearCarrito(
                    emailUsuario
            );

    for(Map.Entry<Long,Integer> item
            : productosSesion.entrySet()) {

        Long idProducto = item.getKey();
        Integer cantidad = item.getValue();

        for(int i = 0; i < cantidad; i++) {

            agregarProducto(
                    carrito,
                    idProducto
            );
        }
    }
}

}