package com.group1.votacion_senado.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

import com.group1.votacion_senado.model.Usuario;
import com.group1.votacion_senado.service.UsuarioService;

@Controller
@Slf4j
@RequestMapping("/admin/votantes")
public class VotanteController {
    @Autowired
    private UsuarioService votanteService;

    @PostMapping("cargar")
    public String procesarCarga(@RequestParam("archivo") MultipartFile archivo, 
                               Model model, 
                               RedirectAttributes redirectAttributes) {
        
        // Validaciones iniciales
        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El archivo está vacío");
            return "redirect:/admin/votantes";
        }
        
        if (!archivo.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("error", "Solo se permiten archivos CSV");
            return "redirect:/admin/votantes";
        }
        
        try {
            long startTime = System.currentTimeMillis();
            List<Usuario> usuarios = votanteService.cargarVotantesDesdeCSV(archivo);
            long endTime = System.currentTimeMillis();
            
            redirectAttributes.addFlashAttribute("mensaje", 
                String.format("%,d votantes cargados correctamente en %d ms", 
                    usuarios.size(), (endTime - startTime)));
            
        } catch (Exception e) {
            log.error("Error al cargar votantes", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al cargar votantes: " + e.getMessage());
        }
        
        return "redirect:/admin/votantes";
    }

    @GetMapping("")
    public String gestionarVotantes() {
        return "admin/votantes";
    }

}
