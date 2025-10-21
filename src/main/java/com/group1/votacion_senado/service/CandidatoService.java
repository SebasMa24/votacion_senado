package com.group1.votacion_senado.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group1.votacion_senado.model.Candidato;
import com.group1.votacion_senado.repository.CandidatoRepository;

@Service
public class CandidatoService {
    @Autowired
    private CandidatoRepository candidatoRepository;

    public Candidato crearCandidato(Candidato candidato) {
        return candidatoRepository.save(candidato);
    }

    public List<Candidato> obtenerTodos() {
        return candidatoRepository.findAll();
    }

    public Optional<Candidato> obtenerPorId(int id) {
        return candidatoRepository.findById(id);
    }

    public Candidato actualizarCandidato(int id, Candidato candidatoActualizado) {
        return candidatoRepository.findById(id)
                .map(candidato -> {
                    candidato.setNombre(candidatoActualizado.getNombre());
                    candidato.setNumLista(candidatoActualizado.getNumLista());
                    candidato.setPartidoPolitico(candidatoActualizado.getPartidoPolitico());
                    return candidatoRepository.save(candidato);
                })
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con id: " + id));
    }

    public void eliminarCandidato(int id) {
        if (!candidatoRepository.existsById(id)) {
            throw new RuntimeException("Candidato no encontrado con id: " + id);
        }
        candidatoRepository.deleteById(id);
    }

}
