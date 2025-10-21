package com.group1.votacion_senado.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.model.Votante;
import com.group1.votacion_senado.service.PartidoPoliticoService;
import com.group1.votacion_senado.service.VotanteService;

@Controller
@RequestMapping("/votacion")
public class VotacionController {
    @Autowired
    private PartidoPoliticoService partidoPoliticoService;

    @Autowired
    private VotanteService votanteService;

    
    @GetMapping("/candidatos/{circunscripcion}")
    public String vistaVotacion(@PathVariable String circunscripcion, Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Votante votante = (Votante) authentication.getPrincipal();
            if (votante.isHaVotado()) {
                return "redirect:/certificado";
            }
            model.addAttribute("currentVotante", votante);
        }
        Circunscripcion tipoCircunscripcion = Circunscripcion.valueOf(circunscripcion.toUpperCase());
        List<PartidoPolitico> partidos = partidoPoliticoService.obtenerPorCircunscripcion(tipoCircunscripcion);
        model.addAttribute("partidos", partidos);
        return "votacion_" + circunscripcion.toLowerCase();
    }

    @PostMapping("/votar")
    public String votar(Model model, Authentication authentication){
        Votante votante = (Votante) authentication.getPrincipal();
        votanteService.marcarComoVotado(votante.getUsername());
        model.addAttribute("currentVotante", votante);
        return "certificado";
    }
}
