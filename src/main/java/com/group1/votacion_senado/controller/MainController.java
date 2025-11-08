package com.group1.votacion_senado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group1.votacion_senado.model.Usuario;
import com.group1.votacion_senado.service.VotacionService;

@Controller
public class MainController {

    @Autowired
    private VotacionService votacionService;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        model.addAttribute("paginaActual", "index");
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario votante = (Usuario) authentication.getPrincipal();
            model.addAttribute("currentVotante", votante);
        }
        model.addAttribute("fechaInicio", votacionService.getFechaHoraInicioVotacion());
        model.addAttribute("fechaFin", votacionService.getFechaHoraFinVotacion());
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, Authentication authentication) {
        model.addAttribute("paginaActual", "login");
        if (authentication != null && authentication.isAuthenticated()) {
            var authorities = authentication.getAuthorities();
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_NACIONAL"))) {
                return "redirect:/votacion/candidatos/nacional";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_INDIGENA"))) {
                return "redirect:/votacion/candidatos/indigena";
            }
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/certificado")
    public String certificado(Model model, Authentication authentication){
        model.addAttribute("paginaActual", "certificado");
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario votante = (Usuario) authentication.getPrincipal();
            if(votante.isHaVotado()){
                model.addAttribute("currentVotante", votante);
            } else {
                return "redirect:/";
            }
        }
        return "certificado";
    }
    @GetMapping("/admin/resultados")
    public String resultados() {
        return "resultados";
    }
}
