package com.group1.votacion_senado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.service.PartidoPoliticoService;

@Controller
@RequestMapping("/admin/partidos")
public class PartidoPoliticoController {
    @Autowired
    private PartidoPoliticoService partidoPoliticoService;

    // Mostrar todos los partidos en la página admin/partidos
    @GetMapping
    public String listarPartidos(Model model) {
        model.addAttribute("partidos", partidoPoliticoService.obtenerTodos());
        model.addAttribute("partido", new PartidoPolitico()); // formulario vacío por defecto
        return "admin/partidos";
    }

    // Crear un nuevo partido
    @PostMapping("/nuevo")
    public String crear(@ModelAttribute PartidoPolitico partido, Model model) {
        partidoPoliticoService.crearPartidoPolitico(partido);
        return "redirect:/admin/partidos";
    }

    // Mostrar formulario de edición en la misma página
    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable int id, Model model) {
        partidoPoliticoService.obtenerPorId(id).ifPresentOrElse(
                partido -> model.addAttribute("partido", partido),
                () -> model.addAttribute("error", "Partido no encontrado"));
        model.addAttribute("partidos", partidoPoliticoService.obtenerTodos());
        return "admin/partidos";
    }

    // Actualizar un partido existente
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute PartidoPolitico partido) {
        partido.setIdPartido(id);
        partidoPoliticoService.actualizarPartidoPolitico(partido);
        return "redirect:/admin/partidos";
    }

    // Eliminar un partido
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id) {
        partidoPoliticoService.eliminar(id);
        return "redirect:/admin/partidos";
    }
}
