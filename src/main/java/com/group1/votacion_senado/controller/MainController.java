package com.group1.votacion_senado.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group1.votacion_senado.model.Votante;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Votante votante = (Votante) authentication.getPrincipal();
            model.addAttribute("currentVotante", votante);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
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
    public String certificado() {
        return "certificado";
    }

    @GetMapping("/admin/votantes")
    public String gestionarVotantes() {
        return "admin/votantes";
    }

    @GetMapping("/admin/candidatos")
    public String gestionarCandidatos() {
        return "admin/candidatos";
    }

    @GetMapping("/admin/resultados")
    public String resultados() {
        return "resultados";
    }
    
}
