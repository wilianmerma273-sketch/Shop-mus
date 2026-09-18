package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.entity.Pedido;
import com.empresa.tiendaonline.service.PedidoService;
import com.empresa.tiendaonline.entity.Factura;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.empresa.tiendaonline.repository.FacturaRepository;

@Controller
public class PagoController {

    private final PedidoService pedidoService;
private final FacturaRepository facturaRepository;
    public PagoController(
        PedidoService pedidoService,
        FacturaRepository facturaRepository) {

    this.pedidoService = pedidoService;
    this.facturaRepository = facturaRepository;
}

    @GetMapping("/pago")
    public String pago(
            Authentication authentication,
            Model model){

        Pedido pedido =
                pedidoService
                        .obtenerUltimoPendiente(
                                authentication.getName()
                        );

        model.addAttribute(
                "pedido",
                pedido
        );

        model.addAttribute(
                "total",
                pedido.getTotal()
        );

        return "pago";
    }



@PostMapping("/pago/confirmar")
public String confirmarPago(
        Authentication authentication,
        Model model){

    Pedido pedido =
            pedidoService.completarPedido(
                    authentication.getName()
            );

    Factura factura =
        Factura.builder()
                .numeroFactura(
                        generarNumeroFactura()
                )
                .pedido(pedido)
                .tipoPago("PAGO ONLINE")
                .total(pedido.getTotalFinal())
                .build();

facturaRepository.save(factura);

    model.addAttribute(
            "pedido",
            pedido
    );

    return "exito";
}

@GetMapping("/pedido-exitoso")
public String exito(){
    return "pedido-exitoso";
}

private String generarNumeroFactura() {

    Long siguienteNumero =
            facturaRepository
                    .findTopByOrderByIdFacturaDesc()
                    .map(f -> f.getIdFactura() + 1)
                    .orElse(1L);

    return String.format(
            "FAC-%06d",
            siguienteNumero
    );
}

}