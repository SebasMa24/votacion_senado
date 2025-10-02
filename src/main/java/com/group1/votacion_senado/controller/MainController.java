package com.group1.votacion_senado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/votacion")
    public String votacion() {
        return "votacion";
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
    
}
