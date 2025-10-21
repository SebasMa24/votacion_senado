package com.group1.votacion_senado.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.PartidoPolitico;

public interface PartidoPoliticoRepository extends JpaRepository<PartidoPolitico, Integer> {
    List<PartidoPolitico> findByTipoCircunscripcionP(Circunscripcion tipo);

    Optional<PartidoPolitico> findByNomPartido(String nombrePartido);

}
