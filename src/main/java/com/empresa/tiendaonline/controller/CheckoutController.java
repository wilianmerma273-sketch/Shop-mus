package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.entity.Envio;
import com.empresa.tiendaonline.entity.EstadoPedido;
import com.empresa.tiendaonline.entity.Pedido;
import com.empresa.tiendaonline.repository.PedidoRepository;
import com.empresa.tiendaonline.service.EnvioService;
import com.empresa.tiendaonline.service.PedidoService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CheckoutController {

    private final EnvioService envioService;
    private final PedidoService pedidoService;
private final PedidoRepository pedidoRepository;


    public CheckoutController(
        EnvioService envioService,
        PedidoService pedidoService,
        PedidoRepository pedidoRepository) {

    this.envioService = envioService;
    this.pedidoService = pedidoService;
    this.pedidoRepository = pedidoRepository;
}

    @GetMapping("/checkout")
    public String checkout(Model model){

        model.addAttribute(
                "envio",
                new Envio()
        );

        return "checkout";
    }

    @PostMapping("/checkout")
    public String guardarEnvio(
            @Valid Envio envio,
            BindingResult result,
            Authentication authentication,
            Model model){

        if(result.hasErrors()){

            model.addAttribute(
                    "envio",
                    envio
            );

            return "checkout";
        }

        Pedido carrito =
                pedidoService.obtenerCarritoActivo(
                        authentication.getName()
                );

        envio.setPedido(carrito);

        envioService.guardar(envio);

        carrito.setEstado(
                EstadoPedido.PENDIENTE
        );
pedidoService.guardar(carrito);

        return "redirect:/pago";
    }



}