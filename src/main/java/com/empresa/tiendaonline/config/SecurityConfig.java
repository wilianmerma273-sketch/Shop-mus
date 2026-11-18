package com.empresa.tiendaonline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.empresa.tiendaonline.security.JwtAuthFilter;
import com.empresa.tiendaonline.security.JwtService;
import com.empresa.tiendaonline.service.CustomUserDetailsService;
import com.empresa.tiendaonline.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final PedidoService pedidoService;

    public SecurityConfig(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ------------------------------------------------------------------
    // CADENA 1: API REST para Angular (stateless, JWT). Solo aplica a estas rutas.
    // ------------------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                      JwtService jwtService,
                                                      CustomUserDetailsService userDetailsService) throws Exception {
        http
                .securityMatcher("/api/**", "/categorias/**", "/usuarios/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers("/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/categorias/**").authenticated()
                        .requestMatchers("/categorias/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write("{\"error\":\"No autenticado o token inválido\"}");
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write("{\"error\":\"No tienes permisos para esta acción\"}");
                        })
                )
                .addFilterBefore(new JwtAuthFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ------------------------------------------------------------------
    // CADENA 2: vistas Thymeleaf con login por sesion (SIN CAMBIOS respecto al original)
    // ------------------------------------------------------------------
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/checkout",
                                "/index",
                                "/productos",
                                "/login",
                                "/registro",
                                "/uploads/**",
                                "/css/**",
                                "/img/**",
                                "/carrito/**",
                                "/test-publico"
                        ).permitAll()

                        .requestMatchers("/crud/**", "/reportes/**").hasRole("ADMIN")
                        .requestMatchers("/pedidos/**").hasAnyRole("ADMIN", "CLIENTE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);
                            if(session != null){
                                @SuppressWarnings("unchecked")
                                Map<Long,Integer> carritoSesion = (Map<Long,Integer>) session.getAttribute("carrito");
                                if(carritoSesion != null && !carritoSesion.isEmpty()){
                                    pedidoService.importarCarritoSesion(authentication.getName(), carritoSesion);
                                    session.removeAttribute("carrito");
                                }
                            }
                            response.sendRedirect("/index");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    // CORS: permite que Angular (localhost:4200) llame a la API y envie Authorization
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> origenes) {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenes);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }
}
