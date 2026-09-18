package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.entity.Categoria;
import com.empresa.tiendaonline.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/crud/categorias")
public class CrudCategoriaController {

    private final CategoriaService categoriaService;

    public CrudCategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias",
                categoriaService.listarCategorias());

        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("categoria") Categoria categoria,
            BindingResult result) {

        if (result.hasErrors()) {
            return "categorias/formulario";
        }

        categoriaService.guardarCategoria(categoria);

        return "redirect:/crud/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Categoria categoria = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        model.addAttribute("categoria", categoria);

        return "categorias/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        categoriaService.eliminarCategoria(id);

        return "redirect:/crud/categorias";
    }
}