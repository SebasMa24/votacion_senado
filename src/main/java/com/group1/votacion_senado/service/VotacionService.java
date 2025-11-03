package com.group1.votacion_senado.service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group1.votacion_senado.model.Candidato;
import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.Lista;
import com.group1.votacion_senado.model.PartidoPolitico;

@Service
public class VotacionService {

    @Autowired
    private PartidoPoliticoService partidoService;

    @Autowired
    private CandidatoService candidatoService;

    private final Map<Circunscripcion, Map<String, Integer>> votosPorPartido = new ConcurrentHashMap<>();
    private final Map<Circunscripcion, Map<String, Integer>> votosPorCandidato = new ConcurrentHashMap<>();
    private final Map<Circunscripcion, Integer> votosEnBlanco = new ConcurrentHashMap<>();

    public synchronized void registrarVoto(Integer idPartido, Integer idCandidato, boolean votoBlanco,
            Circunscripcion circunscripcion) {
        if (votoBlanco) {
            if (circunscripcion == null) {
                throw new IllegalArgumentException("Debe especificar la circunscripción del voto en blanco.");
            }
            votosEnBlanco.merge(circunscripcion, 1, Integer::sum);
            return;
        }

        if (idCandidato != null) {
            Candidato candidato = candidatoService.obtenerPorId(idCandidato)
                    .orElseThrow(() -> new IllegalArgumentException("Candidato no encontrado con id: " + idCandidato));
            PartidoPolitico partido = candidato.getPartidoPolitico();

            if (partido.getTipoLista() == Lista.CERRADA) {
                throw new IllegalArgumentException("No se puede votar por candidato en una lista cerrada.");
            }
            votosPorCandidato
                    .computeIfAbsent(circunscripcion, k -> new ConcurrentHashMap<>())
                    .merge(candidato.getNombre(), 1, Integer::sum);

            votosPorPartido
                    .computeIfAbsent(circunscripcion, k -> new ConcurrentHashMap<>())
                    .merge(partido.getNomPartido(), 1, Integer::sum);
            return;
        }
        if (idPartido != null) {

            PartidoPolitico partido = partidoService.obtenerPorId(idPartido)
                    .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado con id: " + idPartido));

             votosPorPartido
                    .computeIfAbsent(circunscripcion, k -> new ConcurrentHashMap<>())
                    .merge(partido.getNomPartido(), 1, Integer::sum);
            return;
        }

        throw new IllegalArgumentException("Debe seleccionarse un partido, candidato o voto en blanco");
    }

    public Map<Circunscripcion, Map<String, Integer>> getVotosPorPartido() {
        return Collections.unmodifiableMap(votosPorPartido);
    }

    public Map<Circunscripcion, Map<String, Integer>> getVotosPorCandidato() {
        return Collections.unmodifiableMap(votosPorCandidato);
    }

    public Map<Circunscripcion, Integer> getVotosEnBlanco() {
        return votosEnBlanco;
    }
}
