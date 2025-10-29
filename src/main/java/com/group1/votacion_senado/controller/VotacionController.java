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
import com.group1.votacion_senado.model.Usuario;
import com.group1.votacion_senado.service.PartidoPoliticoService;
import com.group1.votacion_senado.service.UsuarioService;

@Controller
@RequestMapping("/votacion")
public class VotacionController {
    @Autowired
    private PartidoPoliticoService partidoPoliticoService;

    @Autowired
    private UsuarioService usuarioService;

    
    @GetMapping("/candidatos/{circunscripcion}")
    public String vistaVotacion(@PathVariable String circunscripcion, Model model, Authentication authentication) {
        model.addAttribute("paginaActual", circunscripcion.toLowerCase());
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario votante = (Usuario) authentication.getPrincipal();
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
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario votante = (Usuario) authentication.getPrincipal();
            usuarioService.marcarComoVotado(votante.getUsername());
            model.addAttribute("currentVotante", votante);
        }
        
        return "redirect:/certificado";
    }
}
