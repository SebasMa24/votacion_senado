package com.group1.votacion_senado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResultadosController {

    @GetMapping("/resultados")
    public String mostrarResultados(Model model) {
        // Si quisieras, aquí puedes cargar datos iniciales desde el backend
        // Ejemplo:
        // model.addAttribute("partidos", votacionService.getVotosPorPartido());
        return "resultados"; // busca templates/resultados.html
    }
}
