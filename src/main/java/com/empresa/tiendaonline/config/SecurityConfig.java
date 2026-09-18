package com.empresa.tiendaonline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.empresa.tiendaonline.service.CustomUserDetailsService;
import com.empresa.tiendaonline.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
@Configuration
public class SecurityConfig {



 private final PedidoService pedidoService;

    public SecurityConfig(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

    .requestMatchers(
            "/",
            "/checkout",
            "/index",
            "/carrito",
            "/productos",
            "/login",
            "/registro",
            "/uploads/**",
            "/css/**",
            "/img/**",
            "/carrito",
        "/carrito/**",
                   "/test-publico" 


    ).permitAll()

    .requestMatchers("/crud/**")
    .hasRole("ADMIN")

.requestMatchers("/reportes/**")
.hasRole("ADMIN")


    .requestMatchers(
            "/pedidos/**"
    )
    .hasAnyRole("ADMIN", "CLIENTE")

    .anyRequest()
    .authenticated()
)

                .formLogin(form -> form
        .loginPage("/login")
        .successHandler((request, response, authentication) -> {

            HttpSession session =
                    request.getSession(false);

            if(session != null){

                Map<Long,Integer> carritoSesion =
                        (Map<Long,Integer>)
                                session.getAttribute("carrito");

                if(carritoSesion != null &&
                   !carritoSesion.isEmpty()){

                    pedidoService.importarCarritoSesion(
                            authentication.getName(),
                            carritoSesion
                    );

                    session.removeAttribute(
                            "carrito"
                    );
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

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}



@Bean
public DaoAuthenticationProvider authenticationProvider(
        CustomUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {

    DaoAuthenticationProvider auth =
            new DaoAuthenticationProvider(userDetailsService);

    auth.setPasswordEncoder(passwordEncoder);

    return auth;
}


}