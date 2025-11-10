package com.group1.votacion_senado.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    private boolean resultadosGuardados = false;

    private final Map<Circunscripcion, Map<String, Integer>> votosPorPartido = new ConcurrentHashMap<>();
    private final Map<Circunscripcion, Map<String, Integer>> votosPorCandidato = new ConcurrentHashMap<>();
    private final Map<Circunscripcion, Integer> votosEnBlanco = new ConcurrentHashMap<>();

    private LocalDateTime fechaHoraInicioVotacion = LocalDateTime.of(2026, 3, 8, 8, 0);
    private LocalDateTime fechaHoraFinVotacion = LocalDateTime.of(2026, 3, 8, 16, 0);

    public void actualizarFechasVotacion(LocalDateTime horaInicio, LocalDateTime horaFin) {
        if (!horaInicio.toLocalDate().equals(horaFin.toLocalDate())) {
            throw new IllegalArgumentException("La fecha de inicio y fin deben ser el mismo día.");
        }
        if (horaInicio.isAfter(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin.");
        }
        this.fechaHoraInicioVotacion = horaInicio;
        this.fechaHoraFinVotacion = horaFin;
    }

    public boolean votacionActiva() {
        LocalDateTime ahora = LocalDateTime.now();
        return (ahora.isAfter(fechaHoraInicioVotacion) && ahora.isBefore(fechaHoraFinVotacion));
    }

    public synchronized void registrarVoto(Integer idPartido, Integer idCandidato, boolean votoBlanco,
            Circunscripcion circunscripcion) {
        if (!votacionActiva()) {
            throw new IllegalStateException("La votación no está activa en este momento.");
        }

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
                    .merge(candidato.getNombre() + " " + candidato.getApellido() + "_"
                            + candidato.getPartidoPolitico().getNomPartido() + "_" + candidato.getNumLista(), 1,
                            Integer::sum);

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

    public synchronized void finalizarVotacion() {
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(fechaHoraFinVotacion)) {
            throw new IllegalStateException("La votación aún no ha terminado.");
        }

        // Registrar votos por partido
        votosPorPartido.forEach((circ, mapaPartidos) -> {
            mapaPartidos.forEach((nombrePartido, votos) -> {
                partidoService.buscarPorNombre(nombrePartido).ifPresent(partido -> {
                    partido.setTotalVotosP(partido.getTotalVotosP() + votos);
                    partidoService.guardar(partido);
                });
            });
        });

        votosPorCandidato.forEach((circ, mapaCandidatos) -> {
            mapaCandidatos.forEach((clave, votos) -> {
                String[] partes = clave.split("_");
                String[] nombreAp = partes[0].split(" ");
                String nombre = nombreAp[0];
                String apellido = nombreAp[1];
                String nombrePartido = partes[1];
                int numLista = Integer.parseInt(partes[2]);

                candidatoService.buscarCandidato(nombre, apellido, numLista, nombrePartido)
                        .ifPresent(candidato -> {
                            candidato.setTotalVotosC(candidato.getTotalVotosC() + votos);
                            candidatoService.guardar(candidato);
                        });
            });
        });
        // Limpiar los conteos temporales
        //votosPorPartido.clear();
        //votosPorCandidato.clear();
        //votosEnBlanco.clear();
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void verificarFin() {
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(fechaHoraInicioVotacion)) {
            return;
        }
        if (votacionActiva()) {
            return;
        }
        if (!resultadosGuardados && ahora.isAfter(fechaHoraFinVotacion)) {
            finalizarVotacion();
            resultadosGuardados = true;
        }
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

    public LocalDateTime getFechaHoraInicioVotacion() {
        return fechaHoraInicioVotacion;
    }

    public LocalDateTime getFechaHoraFinVotacion() {
        return fechaHoraFinVotacion;
    }
}
