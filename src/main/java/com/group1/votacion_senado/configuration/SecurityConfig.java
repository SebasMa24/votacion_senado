package com.group1.votacion_senado.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            //  Permitir acceso público a todas las rutas
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            //  Desactivar formulario de login
            .formLogin(form -> form.disable())
            //  Desactivar login por HTTP Basic
            .httpBasic(httpBasic -> httpBasic.disable())
            //  Desactivar CSRF si no lo necesitas (para pruebas o APIs)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
