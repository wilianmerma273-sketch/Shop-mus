package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.entity.Producto;
import com.empresa.tiendaonline.service.CategoriaService;
import com.empresa.tiendaonline.service.ProductoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CrudController {
private final CategoriaService categoriaService;



    private final ProductoService productoService;

    public CrudController(
        ProductoService productoService,
        CategoriaService categoriaService) {

    this.productoService = productoService;
    this.categoriaService = categoriaService;
}

    @GetMapping("/crud")
public String crud(Model model){

    model.addAttribute(
            "productos",
            productoService.listarProductos());

    model.addAttribute(
            "producto",
            new Producto());

    model.addAttribute(
            "categorias",
            categoriaService.listarCategorias());

    return "crud";
}
public String nuevoProducto(Model model) {

    model.addAttribute(
            "producto",
            new Producto()
    );

    model.addAttribute(
            "categorias",
            categoriaService.listarCategorias()
    );

    return "producto-form";
}

@PostMapping("/crud/guardar")
public String guardarProducto(
        @ModelAttribute Producto producto,
        @RequestParam("archivoImagen")
        MultipartFile archivoImagen)
        throws IOException {

    if(!archivoImagen.isEmpty()){

        
        
        
        
        
        
        String tipo = archivoImagen.getContentType();

        if(tipo == null ||
           (!tipo.equals("image/png") &&
            !tipo.equals("image/jpeg") &&
            !tipo.equals("image/webp"))) {

            throw new IllegalArgumentException(
                    "Solo se permiten imágenes PNG, JPG o WEBP");
        }

        String nombreArchivo =
                UUID.randomUUID()
                + "_"
                + archivoImagen.getOriginalFilename();

        Path ruta =
                Paths.get("uploads/productos");

        Files.createDirectories(ruta);

        Files.copy(
                archivoImagen.getInputStream(),
                ruta.resolve(nombreArchivo),
                StandardCopyOption.REPLACE_EXISTING
        );

        producto.setImagen(nombreArchivo);
    }

    productoService.guardarProducto(producto);

    return "redirect:/crud";
}


@GetMapping("/crud/eliminar/{id}")
public String eliminarProducto(
        @PathVariable Long id) {

    productoService.eliminarProducto(id);

    return "redirect:/crud";
}
@GetMapping("/crud/editar/{id}")
public String editarProducto(
        @PathVariable Long id,
        Model model) {

    Producto producto =
            productoService.buscarPorId(id)
                    .orElseThrow();

    model.addAttribute(
            "producto",
            producto
    );

    model.addAttribute(
            "categorias",
            categoriaService.listarCategorias()
    );

    return "producto-form";
}
@GetMapping("/crud/producto/{id}")
@ResponseBody
public Producto obtenerProducto(@PathVariable Long id) {

    return productoService
            .buscarPorId(id)
            .orElseThrow();
}

}