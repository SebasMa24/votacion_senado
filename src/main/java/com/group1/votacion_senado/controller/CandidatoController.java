package com.group1.votacion_senado.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group1.votacion_senado.model.Candidato;
import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.service.CandidatoService;
import com.group1.votacion_senado.service.PartidoPoliticoService;

@Controller
@RequestMapping("/admin/candidatos")
public class CandidatoController {

    @Autowired
    private CandidatoService candidatoService;

    @Autowired
    private PartidoPoliticoService partidoPoliticoService;

    // Mostrar todos los candidatos y formulario de creación vacío
    @GetMapping
    public String gestionarCandidatos(Model model) {
        model.addAttribute("candidatos", candidatoService.obtenerTodos());
        model.addAttribute("candidato", new Candidato());
        model.addAttribute("partidos", partidoPoliticoService.obtenerTodos());
        return "admin/candidatos";
    }

    // Crear un nuevo candidato
    @PostMapping("/nuevo")
    public String crear(@ModelAttribute Candidato candidato, Model model) {
        try {
            candidatoService.crearCandidato(candidato);
            return "redirect:/admin/candidatos"; // si todo va bien, redirige a la lista
        } catch (RuntimeException e) {
            // En caso de error, mostrar mensaje en la misma página
            model.addAttribute("error", e.getMessage());
            model.addAttribute("candidatos", candidatoService.obtenerTodos());
            model.addAttribute("partidos", partidoPoliticoService.obtenerTodos());
            return "admin/candidatos";
        }
    }

    @PostMapping("/cargar")
    public String cargarCandidatos(MultipartFile archivo, Model model, RedirectAttributes redirectAttributes) {
        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar un archivo CSV.");
        } else {
            try {
                List<Candidato> candidatos = candidatoService.cargarCandidatosDesdeCSV(archivo);
                redirectAttributes.addFlashAttribute("mensaje", "Se cargaron " + candidatos.size() + " candidatos correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al cargar candidatos: " + e.getMessage());
            }
        }

        List<Candidato> candidatos = candidatoService.obtenerTodos();
        List<PartidoPolitico> partidos = partidoPoliticoService.obtenerTodos();
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("partidos", partidos);

        return "redirect:/admin/candidatos";
    }

    // Mostrar datos del candidato en el popup de edición
    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable int id, Model model) {
        candidatoService.obtenerPorId(id).ifPresentOrElse(
                candidato -> model.addAttribute("candidato", candidato),
                () -> model.addAttribute("error", "Candidato no encontrado"));
        model.addAttribute("candidatos", candidatoService.obtenerTodos()); // para seguir mostrando la lista
        return "admin/candidatos"; // misma plantilla, se muestra popup con JS
    }

    // Actualizar un candidato existente
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Candidato candidato) {
        candidato.setIdCandidato(id);
        candidatoService.actualizarCandidato(id, candidato);
        return "redirect:/admin/candidatos";
    }

    // Eliminar un candidato
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id) {
        candidatoService.eliminarCandidato(id);
        return "redirect:/admin/candidatos";
    }
}
