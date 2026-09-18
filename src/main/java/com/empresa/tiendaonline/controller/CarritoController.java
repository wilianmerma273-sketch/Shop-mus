package com.empresa.tiendaonline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import com.empresa.tiendaonline.entity.Pedido;
import com.empresa.tiendaonline.service.PedidoService;
import com.empresa.tiendaonline.repository.ProductoRepository;
import com.empresa.tiendaonline.entity.DetallePedido;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {


private final PedidoService pedidoService;
private final ProductoRepository productoRepository;


public CarritoController(
        PedidoService pedidoService,
        ProductoRepository productoRepository) {

    this.pedidoService = pedidoService;
    this.productoRepository = productoRepository;
}

@PostMapping("/agregar/{idProducto}")
public String agregarProducto(
        @PathVariable Long idProducto,
        Authentication authentication,
        HttpSession session) {

    if(authentication == null ||
   authentication.getName().equals("anonymousUser")){

       Map<Long,Integer> carritoSesion =
        (Map<Long,Integer>) session.getAttribute("carrito");

if(carritoSesion == null){
    carritoSesion = new HashMap<>();
}

carritoSesion.put(
        idProducto,
        carritoSesion.getOrDefault(idProducto,0) + 1
);

session.setAttribute(
        "carrito",
        carritoSesion
);

        return "redirect:/productos";
    }

    Pedido carrito =
            pedidoService.obtenerOCrearCarrito(
                    authentication.getName()
            );

    pedidoService.agregarProducto(
            carrito,
            idProducto
    );

    return "redirect:/productos";
}




@GetMapping
public String verCarrito(
        Authentication authentication,
        HttpSession session,
        Model model) {

    // Usuario NO logueado
    if (authentication == null ||
        authentication.getName().equals("anonymousUser")) {

        Pedido carritoSesion = new Pedido();

        List<DetallePedido> detalles = new ArrayList<>();

        Map<Long, Integer> productosSesion =
                (Map<Long, Integer>) session.getAttribute("carrito");

        if (productosSesion != null) {

            for (Map.Entry<Long, Integer> item : productosSesion.entrySet()) {

                Long idProducto = item.getKey();
                Integer cantidad = item.getValue();

                productoRepository.findById(idProducto)
                        .ifPresent(producto -> {

                            DetallePedido detalle =
                                    DetallePedido.builder()
                                            .producto(producto)
                                            .cantidad(cantidad)
                                            .precioUnitario(producto.getPrecio())
                                            .build();

                            detalles.add(detalle);
                        });
            }
        }

        carritoSesion.setDetalles(detalles);

        BigDecimal subtotal = detalles.stream()
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

        model.addAttribute(
                "carrito",
                carritoSesion
        );

        model.addAttribute(
                "descuento",
                BigDecimal.ZERO
        );

        model.addAttribute(
                "envio",
                BigDecimal.ZERO
        );

        model.addAttribute(
                "totalFinal",
                subtotal
        );

        return "carrito";
    }

    // Usuario logueado
    Pedido carrito =
            pedidoService.obtenerCarrito(
                    authentication.getName()
            );

    if (carrito.getDetalles() == null) {
        carrito.setDetalles(new ArrayList<>());
    }

    model.addAttribute(
            "carrito",
            carrito
    );

    model.addAttribute(
            "descuento",
            pedidoService.calcularDescuento(carrito)
    );

    model.addAttribute(
            "envio",
            pedidoService.calcularEnvio()
    );

    model.addAttribute(
            "totalFinal",
            pedidoService.calcularTotalFinal(carrito)
    );

    return "carrito";
}




@GetMapping("/aumentar/{id}")
public String aumentar(@PathVariable Long id){

    pedidoService.aumentarCantidad(id);

    return "redirect:/carrito";
}

@GetMapping("/disminuir/{id}")
public String disminuir(@PathVariable Long id){

    pedidoService.disminuirCantidad(id);

    return "redirect:/carrito";
}

@GetMapping("/eliminar/{id}")
public String eliminar(@PathVariable Long id){

    pedidoService.eliminarDetalle(id);

    return "redirect:/carrito";
}


@GetMapping("/sesion/aumentar/{idProducto}")
public String aumentarSesion(
        @PathVariable Long idProducto,
        HttpSession session) {

    Map<Long,Integer> carrito =
            (Map<Long,Integer>) session.getAttribute("carrito");

    if(carrito != null){

        carrito.put(
                idProducto,
                carrito.getOrDefault(idProducto,0) + 1
        );

        session.setAttribute(
                "carrito",
                carrito
        );
    }

    return "redirect:/carrito";
}

@GetMapping("/sesion/disminuir/{idProducto}")
public String disminuirSesion(
        @PathVariable Long idProducto,
        HttpSession session) {

    Map<Long,Integer> carrito =
            (Map<Long,Integer>) session.getAttribute("carrito");

    if(carrito != null &&
       carrito.containsKey(idProducto)) {

        int cantidad = carrito.get(idProducto);

        if(cantidad > 1){

            carrito.put(
                    idProducto,
                    cantidad - 1
            );

        } else {

            carrito.remove(idProducto);
        }

        session.setAttribute(
                "carrito",
                carrito
        );
    }

    return "redirect:/carrito";
}

@GetMapping("/sesion/eliminar/{idProducto}")
public String eliminarSesion(
        @PathVariable Long idProducto,
        HttpSession session) {

    Map<Long,Integer> carrito =
            (Map<Long,Integer>) session.getAttribute("carrito");

    if(carrito != null){

        carrito.remove(idProducto);

        session.setAttribute(
                "carrito",
                carrito
        );
    }

    return "redirect:/carrito";
}




}