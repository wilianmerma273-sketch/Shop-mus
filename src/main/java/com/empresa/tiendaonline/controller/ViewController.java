package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.service.ProductoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class ViewController {

    private final ProductoService productoService;

    public ViewController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())
                ? "redirect:/index"
                : "login";
    }

    @GetMapping("/index")
    public String index(Model model) {
model.addAttribute(
            "productosDestacados",
            productoService.obtenerProductosDestacados()
    );



        return "index";
    }

    @GetMapping("/productos")
    public String productos(Model model) {

        model.addAttribute(
                "productos",
                productoService.listarProductos()
        );

        return "productos";
    }


@GetMapping("/test-publico")
@ResponseBody
public String testPublico() {
    return "OK";
}
  

    
}