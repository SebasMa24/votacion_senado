package com.group1.votacion_senado.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    }
}

