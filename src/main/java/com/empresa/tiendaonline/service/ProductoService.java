package com.empresa.tiendaonline.service;

import com.empresa.tiendaonline.entity.Producto;
import com.empresa.tiendaonline.repository.CategoriaRepository;
import com.empresa.tiendaonline.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.Collections;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        // Verificamos si la categoría asociada existe antes de guardar
        if (producto.getCategoria() != null && producto.getCategoria().getIdCategoria() != null) {
            boolean existeCategoria = categoriaRepository.existsById(producto.getCategoria().getIdCategoria());
            if (!existeCategoria) {
                throw new IllegalArgumentException("La categoría especificada no existe.");
            }
        }
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

public List<Producto> obtenerProductosDestacados() {

    List<Producto> productos = productoRepository.findAll();

    Collections.shuffle(productos);

    return productos.stream()
            .limit(4)
            .toList();
}



}