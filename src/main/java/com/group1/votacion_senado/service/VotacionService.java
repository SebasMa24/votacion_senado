package com.group1.votacion_senado.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    private Map<String, Integer> aplicarDhondt(List<PartidoPolitico> partidos, int totalCurules) {
        Map<String, Integer> resultado = new ConcurrentHashMap<>();
        Map<String, List<Double>> tabla = new ConcurrentHashMap<>();

        partidos.forEach(p -> {
            resultado.put(p.getNomPartido(), 0);
            listaCocientes(p.getTotalVotosP(), totalCurules).forEach(
                    val -> tabla.computeIfAbsent(p.getNomPartido(), k -> new java.util.ArrayList<>()).add(val));
        });

        for (int i = 0; i < totalCurules; i++) {
            String ganador = tabla.entrySet().stream()
                    .filter(e -> !e.getValue().isEmpty())
                    .max(Map.Entry.comparingByValue((a, b) -> Double.compare(a.get(0), b.get(0))))
                    .map(Map.Entry::getKey).orElse(null);

            if (ganador == null)
                break;

            resultado.merge(ganador, 1, Integer::sum);
            tabla.get(ganador).remove(0);
        }

        return resultado;
    }

    private List<Double> listaCocientes(int votos, int curules) {
        List<Double> lista = new java.util.ArrayList<>();
        for (int i = 1; i <= curules; i++)
            lista.add(votos / (double) i);
        return lista;
    }

    public Map<String, Integer> calcularCurulesNacional() {
        List<PartidoPolitico> partidos = partidoService.obtenerTodos()
                .stream().filter(p -> p.getTipoCircunscripcionP() == Circunscripcion.NACIONAL).toList();
        return aplicarDhondt(partidos, 100);
    }

    public Map<String, Integer> calcularCurulesIndigena() {
        List<PartidoPolitico> partidos = partidoService.obtenerTodos()
                .stream().filter(p -> p.getTipoCircunscripcionP() == Circunscripcion.INDIGENA).toList();
        return aplicarDhondt(partidos, 2);
    }

    public Map<String, Integer> calcularCurulesTotales(String partidoOposicion) {
        Map<String, Integer> resultado = new ConcurrentHashMap<>();

        calcularCurulesNacional().forEach((p, c) -> resultado.merge(p, c, Integer::sum));
        calcularCurulesIndigena().forEach((p, c) -> resultado.merge(p, c, Integer::sum));

        partidoService.buscarPorNombre(partidoOposicion)
                .ifPresent(p -> resultado.merge(p.getNomPartido(), 1, Integer::sum));

        return resultado;
    }

    public Map<PartidoPolitico, List<Candidato>> asignarCandidatosGanadores(
            Map<PartidoPolitico, Integer> curulesPorPartido) {
        Map<PartidoPolitico, List<Candidato>> ganadores = new HashMap<>();

        for (Map.Entry<PartidoPolitico, Integer> entry : curulesPorPartido.entrySet()) {
            PartidoPolitico partido = entry.getKey();
            int curules = entry.getValue();

            // Si el partido no ganó curules → no se agrega
            if (curules <= 0)
                continue;

            List<Candidato> lista = new ArrayList<>(partido.getCandidatos());

            // Lista cerrada → el orden está predefinido por numeroLista
            if (partido.getTipoLista() == Lista.CERRADA) {
                lista.sort(Comparator.comparingInt(Candidato::getNumLista));
            }
            // Lista abierta → se ordena por votos de mayor a menor
            else if (partido.getTipoLista() == Lista.ABIERTA) {
                lista.sort((a, b) -> Integer.compare(b.getTotalVotosC(), a.getTotalVotosC()));
            }

            // Seleccionamos exactamente el número de curules asignadas
            ganadores.put(partido, lista.stream().limit(curules).toList());
        }
        return ganadores;
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
