package com.group1.votacion_senado.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.service.PartidoPoliticoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


@Controller
@RequestMapping("/partidos")
public class PartidoPoliticoController {
    @Autowired
    private PartidoPoliticoService partidoPoliticoService;

    
    @GetMapping("/candidatos")
    public String mostrarCandidatos(Model model) {
        List<PartidoPolitico> partidos = partidoPoliticoService.obtenerTodos();
        partidos.forEach(System.out::println);
        model.addAttribute("partidos", partidos);
        return "votacion";
    }

    // Obtener partido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PartidoPolitico> obtenerPorId(@PathVariable int id) {
        return partidoPoliticoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo partido
    @PostMapping
    public ResponseEntity<PartidoPolitico> crear(@RequestBody PartidoPolitico partido) {
        PartidoPolitico nuevo = partidoPoliticoService.guardar(partido);
        return ResponseEntity.ok(nuevo);
    }

    // Actualizar un partido existente
    @PutMapping("/{id}")
    public ResponseEntity<PartidoPolitico> actualizar(@PathVariable int id, @RequestBody PartidoPolitico partidoActualizado) {
        return partidoPoliticoService.obtenerPorId(id)
                .map(partidoExistente -> {
                    partidoActualizado.setIdPartido(partidoPoliticoService.obtenerPorId(id).get().getIdPartido());
                    PartidoPolitico actualizado = partidoPoliticoService.guardar(partidoActualizado);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar partido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        boolean eliminado = partidoPoliticoService.eliminar(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
