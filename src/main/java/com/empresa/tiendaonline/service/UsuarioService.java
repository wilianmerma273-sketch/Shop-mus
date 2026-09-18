package com.empresa.tiendaonline.service;

import com.empresa.tiendaonline.entity.Usuario;
import com.empresa.tiendaonline.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Método necesario para buscar por ID en PUT y GET por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Método necesario para el DELETE
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}