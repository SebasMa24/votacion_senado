package com.group1.votacion_senado.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group1.votacion_senado.model.Candidato;
import com.group1.votacion_senado.service.CandidatoService;

@RestController
@RequestMapping("/candidatos")
public class CandidatoController {
    @Autowired
    private CandidatoService candidatoService;

    @PostMapping
    public Candidato crear(@RequestBody Candidato candidato) {
        return candidatoService.crearCandidato(candidato);
    }

    @GetMapping
    public List<Candidato> listar() {
        return candidatoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Candidato obtenerPorId(@PathVariable int id) {
        return candidatoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado"));
    }

    @PutMapping("/{id}")
    public Candidato actualizar(@PathVariable int id, @RequestBody Candidato candidato) {
        return candidatoService.actualizarCandidato(id, candidato);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        candidatoService.eliminarCandidato(id);
    }
}
