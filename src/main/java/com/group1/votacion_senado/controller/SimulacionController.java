package com.group1.votacion_senado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.group1.votacion_senado.model.Usuario;
import com.group1.votacion_senado.service.SimulacionService;

@Controller
@RequestMapping("/simulacion")
public class SimulacionController {
    @Autowired
    private SimulacionService simulacionService;

    @PostMapping("/votar-automatico")
    public String simular(@RequestParam(defaultValue = "0.70") double porcentaje, Model model,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario votante = (Usuario) authentication.getPrincipal();
            model.addAttribute("currentVotante", votante);
        }
        long inicio = System.currentTimeMillis();
        int totalVotaron = simulacionService.simularVotacion(porcentaje);
        long fin = System.currentTimeMillis();
        long duracionMs = fin - inicio;
        double duracionSeg = duracionMs / 1000.0;
        redirectAttributes.addFlashAttribute("mensaje", "Simulación realizada con éxito.");
        redirectAttributes.addFlashAttribute("totalVotaron", totalVotaron);
        redirectAttributes.addFlashAttribute("porcentaje", porcentaje * 100);
        redirectAttributes.addFlashAttribute("duracion", String.format("%.2f", duracionSeg));
        return "redirect:/";
    }
}