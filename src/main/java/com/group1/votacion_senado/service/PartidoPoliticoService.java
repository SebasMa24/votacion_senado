package com.group1.votacion_senado.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.repository.PartidoPoliticoRepository;

@Service
public class PartidoPoliticoService {
    @Autowired
    private PartidoPoliticoRepository partidoPoliticoRepository;

    @Cacheable("todosLosPartidos")
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

    @Caching(evict = { @CacheEvict(value = "todosLosPartidos", allEntries = true),
            @CacheEvict(value = "partidosPorCircunscripcion", allEntries = true)
    })
    public PartidoPolitico crearPartidoPolitico(PartidoPolitico partido) {
        try {
            Optional<PartidoPolitico> existente = partidoPoliticoRepository.findByNomPartido(partido.getNomPartido());
            if (existente.isPresent()) {
                throw new RuntimeException("El nombre del partido político ya existe: " + partido.getNomPartido());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar la existencia del partido político: " + e.getMessage());
        }
        return partidoPoliticoRepository.save(partido);
    }

    @Caching(evict = { @CacheEvict(value = "todosLosPartidos", allEntries = true),
            @CacheEvict(value = "partidosPorCircunscripcion", allEntries = true)
    })
    public PartidoPolitico actualizarPartidoPolitico(PartidoPolitico partidoActualizado) {
        return partidoPoliticoRepository.findById(partidoActualizado.getIdPartido())
                .map(partido -> {
                    partido.setNomPartido(partidoActualizado.getNomPartido());
                    partido.setTipoCircunscripcionP(partidoActualizado.getTipoCircunscripcionP());
                    return partidoPoliticoRepository.save(partido);
                })
                .orElseThrow(() -> new RuntimeException(
                        "Partido político no encontrado con id: " + partidoActualizado.getIdPartido()));
    }

    @Caching(evict = { @CacheEvict(value = "todosLosPartidos", allEntries = true),
            @CacheEvict(value = "partidosPorCircunscripcion", allEntries = true)
    })
    public boolean eliminar(int id) {
        if (partidoPoliticoRepository.existsById(id)) {
            partidoPoliticoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<PartidoPolitico> buscarPorNombre(String nombre) {
        return partidoPoliticoRepository.findByNomPartido(nombre);
    }

    public PartidoPolitico guardar(PartidoPolitico partido) {
        return partidoPoliticoRepository.save(partido);
    }
}
