package com.group1.votacion_senado.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group1.votacion_senado.model.Candidato;
import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.Lista;
import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.model.Usuario;
import com.group1.votacion_senado.repository.UsuarioRepository;

@Service
public class SimulacionService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PartidoPoliticoService partidoPoliticoService;

    @Autowired
    private VotacionService votacionService;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public int simularVotacion(double porcentaje) {

        List<Usuario> todos = usuarioRepository.findAll();
        todos.removeFirst(); // Remover admin
        int totalAVotar = (int) (todos.size() * porcentaje);
        if (totalAVotar <= 0)
            return 0;

        Collections.shuffle(todos);
        List<Usuario> seleccionados = todos.subList(0, Math.min(totalAVotar, todos.size()));
        List<PartidoPolitico> partidosNacional = partidoPoliticoService
                .obtenerPorCircunscripcion(Circunscripcion.NACIONAL);
        List<PartidoPolitico> partidosIndigena = partidoPoliticoService
                .obtenerPorCircunscripcion(Circunscripcion.INDIGENA);

        for (PartidoPolitico p : partidosNacional) {
            p.getCandidatos().size();
        }
        for (PartidoPolitico p : partidosIndigena) {
            p.getCandidatos().size();
        }
        List<Future<Void>> futures = new ArrayList<>(seleccionados.size());

        for (Usuario u : seleccionados) {
            // evita volver a votar si ya lo hizo (doble-check)
            if (u.isHaVotado())
                continue;

            Callable<Void> tarea = () -> {
                votarSegunCircunscripcion(u, u.getTipoCircunscripcion() == Circunscripcion.NACIONAL ? partidosNacional
                        : partidosIndigena);
                // marcar como votado (persistente)
                try {
                    usuarioService.marcarComoVotado(u.getUsername());
                } catch (Exception e) {
                    System.out.println("Error marcando como votado " + u.getUsername() + ": " + e.getMessage());
                }
                return null;
            };

            futures.add(executor.submit(tarea));
        }

        // esperar a que finalicen todas (si quieres no bloquear aquí, puedes no hacer
        // get() y devolver totalAVotar)
        for (Future<Void> f : futures) {
            try {
                f.get(); // bloqueante hasta que la tarea termine
            } catch (Exception e) {
                System.out.println("Tarea de simulación falló: " + e.getMessage());
            }
        }

        return totalAVotar;
    }

    private int elegirTipoDeVoto() {
        int valor = new Random().nextInt(100);
        if (valor < 5)
            return 0; // blanco
        else if (valor < 30)
            return 1; // partido
        else
            return 2; // candidato
    }

    public void votarSegunCircunscripcion(Usuario u, List<PartidoPolitico> partidos) {
        Circunscripcion tipo = u.getTipoCircunscripcion();
        Random random = new Random();
        int opcion = elegirTipoDeVoto();

        switch (opcion) {
            case 0:
                votacionService.registrarVoto(null, null, true, tipo);
                break;
            case 1:
                PartidoPolitico partido = partidos.get(random.nextInt(partidos.size()));
                votacionService.registrarVoto(partido.getIdPartido(), null, false, tipo);
                break;
            case 2:
                PartidoPolitico partidoConCandidato = partidos.get(random.nextInt(partidos.size()));
                List<Candidato> candidatos = partidoConCandidato.getCandidatos();

                if (partidoConCandidato.getTipoLista() == Lista.CERRADA || candidatos == null || candidatos.isEmpty()) {
                    votacionService.registrarVoto(partidoConCandidato.getIdPartido(), null, false, tipo);
                } else {
                    Candidato candidato = candidatos.get(random.nextInt(candidatos.size()));
                    votacionService.registrarVoto(null, candidato.getIdCandidato(), false, tipo);
                }
                break;
        }
    }
}
