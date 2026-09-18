package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.entity.Rol;
import com.empresa.tiendaonline.entity.Usuario;
import com.empresa.tiendaonline.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {

        model.addAttribute("usuario", new Usuario());

        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute Usuario usuario) {

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return "redirect:/registro?error";
        }

        usuario.setRol(Rol.CLIENTE);

        usuario.setContrasena(
                passwordEncoder.encode(usuario.getContrasena()));

        usuarioRepository.save(usuario);

        return "redirect:/login?registrado";
    }
}