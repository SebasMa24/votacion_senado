package com.group1.votacion_senado.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.Usuario;
import com.group1.votacion_senado.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public Usuario loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    public List<Usuario> cargarVotantesDesdeCSV(MultipartFile archivo) throws Exception {
        List<Usuario> usuarios = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                // Saltar encabezado
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String[] columnas = linea.split(",");

                if (columnas.length != 5) {
                    // Ignorar o lanzar excepción según necesidad
                    throw new RuntimeException("Formato inválido en línea: " + linea);
                }
                String correo = columnas[3].trim();
                Matcher matcher = EMAIL_PATTERN.matcher(correo);

                if (!matcher.matches()) {
                    throw new RuntimeException("Correo con formato inválido en línea: " + linea);
                }

                Usuario usuario = new Usuario(
                        Integer.parseInt(columnas[0].trim()),
                        columnas[1].trim(),
                        columnas[2].trim(),
                        columnas[3].trim(),
                        passwordEncoder.encode("1234"),
                        Circunscripcion.valueOf(columnas[4].trim().toUpperCase()),
                        columnas[5].trim());
                usuarios.add(usuario);
            }

            usuarioRepository.saveAll(usuarios);
        }

        return usuarios;
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Votante no encontrado"));
    }

    public void marcarComoVotado(String username) {
        Usuario votante = findByUsername(username);
        votante.setHaVotado(true);
        usuarioRepository.save(votante);

        refrescarAuthentication(votante);
    }

    public void refrescarAuthentication(Usuario votante) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName().equals(votante.getUsername())) {
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    votante,
                    auth.getCredentials(),
                    votante.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
