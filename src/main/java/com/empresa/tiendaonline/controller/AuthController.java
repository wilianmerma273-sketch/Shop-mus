package com.empresa.tiendaonline.controller;

import com.empresa.tiendaonline.dto.LoginRequest;
import com.empresa.tiendaonline.dto.LoginResponse;
import com.empresa.tiendaonline.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final DaoAuthenticationProvider authenticationProvider;
    private final JwtService jwtService;

    public AuthController(DaoAuthenticationProvider authenticationProvider, JwtService jwtService) {
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.contrasena()));

            UserDetails user = (UserDetails) auth.getPrincipal();
            String rol = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("ROLE_CLIENTE")
                    .replace("ROLE_", "");

            return ResponseEntity.ok(new LoginResponse(
                    jwtService.generarToken(user), "Bearer", user.getUsername(),
                    rol, jwtService.getExpirationMs()));

        } catch (AuthenticationException e) {
            // Mismo mensaje para email inexistente y clave incorrecta (no filtra que cuentas existen)
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }
    }
}
