package com.group1.votacion_senado.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.group1.votacion_senado.model.Candidato;
import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.repository.CandidatoRepository;
import com.group1.votacion_senado.repository.PartidoPoliticoRepository;

@Service
public class CandidatoService {
    @Autowired
    private CandidatoRepository candidatoRepository;

    @Autowired
    private PartidoPoliticoRepository partidoPoliticoRepository;

    @Caching(evict = { @CacheEvict(value = "todosLosCandidatos", allEntries = true) })
    public Candidato crearCandidato(Candidato candidato) {
        PartidoPolitico partido = partidoPoliticoRepository.findById(candidato.getPartidoPolitico().getIdPartido())
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        int contador = candidatoRepository.countByPartidoPolitico(partido);
        candidato.setNumLista(contador + 1);

        candidato.setPartidoPolitico(partido);
        return candidatoRepository.save(candidato);
    }

    @Caching(evict = { @CacheEvict(value = "todosLosCandidatos", allEntries = true) })
    public List<Candidato> cargarCandidatosDesdeCSV(MultipartFile archivo) throws Exception {
        List<Candidato> candidatos = new ArrayList<>();
        Map<Integer, Integer> contadorPorPartido = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                // Saltar encabezado
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String[] columnas = linea.split(",");

                if (columnas.length != 3) {
                    throw new RuntimeException("Formato inválido en línea: " + linea);
                }

                String nombre = columnas[0].trim();
                String apellido = columnas[1].trim();
                String nombrePartido = columnas[2].trim();

                // Buscar partido por nombre
                PartidoPolitico partido = partidoPoliticoRepository.findByNomPartido(nombrePartido)
                        .orElseThrow(() -> new RuntimeException("Partido no encontrado: " + nombrePartido));

                int numLista = contadorPorPartido.getOrDefault(partido.getIdPartido(),
                        candidatoRepository.countByPartidoPolitico(partido)) + 1;
                contadorPorPartido.put(partido.getIdPartido(), numLista);

                Candidato candidato = new Candidato();
                candidato.setNombre(nombre);
                candidato.setApellido(apellido);
                candidato.setNumLista(numLista);
                candidato.setPartidoPolitico(partido);

                candidatos.add(candidato);
            }

            candidatoRepository.saveAll(candidatos);
        }

        return candidatos;
    }

    @Cacheable("todosLosCandidatos")
    public List<Candidato> obtenerTodos() {
        return candidatoRepository.findAll();
    }

    public Optional<Candidato> obtenerPorId(int id) {
        return candidatoRepository.findById(id);
    }

    @Caching(evict = { @CacheEvict(value = "todosLosCandidatos", allEntries = true) })
    public Candidato actualizarCandidato(int id, Candidato candidatoActualizado) {
        return candidatoRepository.findById(id)
                .map(candidato -> {
                    candidato.setNombre(candidatoActualizado.getNombre());
                    candidato.setApellido(candidatoActualizado.getApellido());
                    candidato.setPartidoPolitico(candidatoActualizado.getPartidoPolitico());
                    int contador = candidatoRepository.countByPartidoPolitico(candidato.getPartidoPolitico());
                    candidato.setNumLista(contador + 1);
                    return candidatoRepository.save(candidato);
                })
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con id: " + id));
    }

    @Caching(evict = { @CacheEvict(value = "todosLosCandidatos", allEntries = true) })
    public void eliminarCandidato(int id) {
        if (!candidatoRepository.existsById(id)) {
            throw new RuntimeException("Candidato no encontrado con id: " + id);
        }
        candidatoRepository.deleteById(id);
    }

    public Optional<Candidato> buscarCandidato(String nombre, String apellido, int numLista, String nomPartido) {
        return candidatoRepository.findByNombreAndApellidoAndNumListaAndPartidoPoliticoNomPartido(
                nombre, apellido, numLista, nomPartido);
    }

    public Candidato guardar(Candidato candidato) {
        return candidatoRepository.save(candidato);
    }

}
