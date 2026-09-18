package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.entity.Pedido;
import com.empresa.tiendaonline.repository.FacturaRepository;
import com.empresa.tiendaonline.repository.PedidoRepository;
import com.empresa.tiendaonline.service.PedidoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PedidoController {

private final PedidoService pedidoService;
private final PedidoRepository pedidoRepository;
private final FacturaRepository facturaRepository;
public PedidoController(
        PedidoService pedidoService,
        PedidoRepository pedidoRepository,
        FacturaRepository facturaRepository) {

    this.pedidoService = pedidoService;
    this.pedidoRepository = pedidoRepository;
    this.facturaRepository = facturaRepository;
}

    @GetMapping("/pedidos")
    public String pedidos(
            Authentication authentication,
            Model model){

        model.addAttribute(
                "pedidos",
                pedidoService.obtenerPedidosUsuario(
                        authentication.getName()
                )
        );

        return "pedidos";
    }

@GetMapping("/exito/{idPedido}")
public String exito(
        @PathVariable Long idPedido,
        Model model){

    Pedido pedido =
            pedidoRepository
                    .findById(idPedido)
                    .orElseThrow();

    model.addAttribute(
            "pedido",
            pedido
    );

    return "exito";
}



@GetMapping("/factura/{idPedido}")
public String verFactura(
        @PathVariable Long idPedido,
        Model model) {

    Pedido pedido =
            pedidoRepository
                    .findById(idPedido)
                    .orElseThrow();

    model.addAttribute(
            "pedido",
            pedido
    );

    model.addAttribute(
            "factura",
            pedido.getFactura()
    );

    return "factura";
}

}