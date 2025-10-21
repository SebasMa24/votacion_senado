package com.group1.votacion_senado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group1.votacion_senado.model.Votante;

public interface VotanteRepository extends JpaRepository<Votante, Integer> {
    Optional<Votante> findByUsername(String username);
}
