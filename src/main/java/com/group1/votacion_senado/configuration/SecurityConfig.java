package com.group1.votacion_senado.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler customFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/login", "/").permitAll()
                        .requestMatchers("/admin/**", "/votacion/resultados/**").hasRole("ADMIN")
                        .requestMatchers("/votacion/**").hasRole("VOTANTE")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(customFailureHandler)
                        .successHandler((request, response, authentication) -> {
                            var user = (com.group1.votacion_senado.model.Usuario) authentication.getPrincipal();

                            String redirectUrl = "/";

                            if (user.getRol().name().equals("ADMIN")) {
                                redirectUrl = "/";
                            } else if (user.getRol().name().equals("VOTANTE")) {
                                switch (user.getTipoCircunscripcion()) {
                                    case NACIONAL -> redirectUrl = "/votacion/candidatos/nacional";
                                    case INDIGENA -> redirectUrl = "/votacion/candidatos/indigena";
                                }
                            }
                            response.sendRedirect(redirectUrl);
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}
