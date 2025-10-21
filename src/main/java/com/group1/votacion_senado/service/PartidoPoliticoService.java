package com.group1.votacion_senado.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.repository.PartidoPoliticoRepository;

@Service
public class PartidoPoliticoService {
    @Autowired
    private PartidoPoliticoRepository partidoPoliticoRepository;

    public List<PartidoPolitico> obtenerTodos() {
        return partidoPoliticoRepository.findAll();
    }

    @Cacheable("partidosPorCircunscripcion")
    public List<PartidoPolitico> obtenerPorCircunscripcion(Circunscripcion tipo) {
        return partidoPoliticoRepository.findByTipoCircunscripcionP(tipo);
    }

    public Optional<PartidoPolitico> obtenerPorId(int id) {
        return partidoPoliticoRepository.findById(id);
    }

    public PartidoPolitico guardar(PartidoPolitico partido) {
        return partidoPoliticoRepository.save(partido);
    }

    public boolean eliminar(int id) {
        if (partidoPoliticoRepository.existsById(id)) {
            partidoPoliticoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
