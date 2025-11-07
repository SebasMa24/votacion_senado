package com.group1.votacion_senado.configuration;

import com.group1.votacion_senado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String username = request.getParameter("username");

        // Verificar si el usuario existe en la base de datos
        boolean userExists = usuarioRepository.findByUsername(username).isPresent();

        if (!userExists) {
            response.sendRedirect("/login?userNotFound");
        } else {
            response.sendRedirect("/login?badCredentials");
        }
    }
}
