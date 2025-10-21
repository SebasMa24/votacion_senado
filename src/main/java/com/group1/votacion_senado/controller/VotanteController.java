package com.group1.votacion_senado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.group1.votacion_senado.service.VotanteService;

@Controller
@RequestMapping("/admin/votantes")
public class VotanteController {
    @Autowired
    private VotanteService votanteService;

    @PostMapping("cargar")
    public String procesarCarga(MultipartFile archivo, Model model) {
        try {
            votanteService.cargarVotantesDesdeCSV(archivo);
            model.addAttribute("mensaje", "Votantes cargados correctamente");
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar votantes: " + e.getMessage());
        }
        return "admin/votantes";
    }

    @GetMapping("")
    public String gestionarVotantes() {
        return "admin/votantes";
    }

}
