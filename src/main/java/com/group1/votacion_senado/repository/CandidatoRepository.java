package com.group1.votacion_senado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group1.votacion_senado.model.Candidato;
import com.group1.votacion_senado.model.PartidoPolitico;

public interface CandidatoRepository extends JpaRepository<Candidato, Integer> {
    int countByPartidoPolitico(PartidoPolitico partido);
}
