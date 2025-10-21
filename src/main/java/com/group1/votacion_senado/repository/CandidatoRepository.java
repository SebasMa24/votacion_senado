package com.group1.votacion_senado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group1.votacion_senado.model.Candidato;

public interface CandidatoRepository extends JpaRepository<Candidato, Integer> {
    
}
