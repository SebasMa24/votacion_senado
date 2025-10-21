package com.group1.votacion_senado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.group1.votacion_senado.model.Votante;
import com.group1.votacion_senado.repository.VotanteRepository;

@Service
public class VotanteService implements UserDetailsService {
    @Autowired
    private VotanteRepository votanteRepository;

    @Override
    public Votante loadUserByUsername(String username) throws UsernameNotFoundException{
        return findByUsername(username);
    }

    public Votante findByUsername(String username) {
        return votanteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Votante no encontrado"));
    }

    public void marcarComoVotado(String username) {
        Votante votante = findByUsername(username);
        votante.setHaVotado(true);
        votanteRepository.save(votante);

        refrescarAuthentication(votante);
    }

    public void refrescarAuthentication(Votante votante) {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       if(auth != null && auth.isAuthenticated() && auth.getName().equals(votante.getUsername())) {
           Authentication newAuth = new UsernamePasswordAuthenticationToken(
               votante,
               auth.getCredentials(),
               votante.getAuthorities()
           );
           SecurityContextHolder.getContext().setAuthentication(newAuth);
       }
    }
}

