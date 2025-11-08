package com.group1.votacion_senado.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group1.votacion_senado.model.Circunscripcion;
import com.group1.votacion_senado.model.PartidoPolitico;
import com.group1.votacion_senado.model.Usuario;
import com.group1.votacion_senado.service.PartidoPoliticoService;
import com.group1.votacion_senado.service.UsuarioService;
import com.group1.votacion_senado.service.VotacionService;

@Controller
@RequestMapping("/votacion")
public class VotacionController {
    @Autowired
    private PartidoPoliticoService partidoPoliticoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private VotacionService votacionService;

    @GetMapping("/candidatos/{circunscripcion}")
    public String vistaVotacion(@PathVariable String circunscripcion, Model model, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (!votacionService.votacionActiva()) {
            redirectAttributes.addFlashAttribute("error", "La votación no está activa en este momento.");
            return "redirect:/";
        }
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
    public String votar(
            @RequestParam("voto") String voto,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (!votacionService.votacionActiva()) {
            redirectAttributes.addAttribute("error", "La votación no está activa en este momento.");
            return "redirect:/";
        }
        if (authentication != null && authentication.isAuthenticated()) {
            Usuario votante = (Usuario) authentication.getPrincipal();
            if (voto.equals("BLANCO")) {
                votacionService.registrarVoto(null, null, true, votante.getTipoCircunscripcion());
            } else if (voto.startsWith("PARTIDO_")) {
                int idPartido = Integer.parseInt(voto.substring(8));
                votacionService.registrarVoto(idPartido, null, false, votante.getTipoCircunscripcion());
            } else if (voto.startsWith("CANDIDATO_")) {
                int idCandidato = Integer.parseInt(voto.substring(10));
                votacionService.registrarVoto(null, idCandidato, false, votante.getTipoCircunscripcion());
            }
            usuarioService.marcarComoVotado(votante.getUsername());
            model.addAttribute("currentVotante", votante);
        }

        return "redirect:/certificado";
    }

    @PostMapping("/actualizar-fechas")
    public String configurarFechas(
            @RequestParam String fechaHoraInicioVotacion,
            @RequestParam StringBuffer fechaHoraFinVotacion,
            RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime fechaHoraInicio = LocalDateTime.parse(fechaHoraInicioVotacion);
            LocalDateTime fechaHoraFin = LocalDateTime.parse(fechaHoraFinVotacion);

            votacionService.actualizarFechasVotacion(fechaHoraInicio, fechaHoraFin);
            redirectAttributes.addFlashAttribute("mensaje", "Fechas de votación actualizadas correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/resultados/partidos")
    public ResponseEntity<Map<Circunscripcion, Map<String, Integer>>> obtenerResultadosPorPartido() {
        return ResponseEntity.ok(votacionService.getVotosPorPartido());
    }

    @GetMapping("/resultados/candidatos")
    public ResponseEntity<Map<Circunscripcion, Map<String, Integer>>> obtenerResultadosPorCandidato() {
        return ResponseEntity.ok(votacionService.getVotosPorCandidato());
    }

    @GetMapping("/resultados/blancos")
    public ResponseEntity<Map<Circunscripcion, Integer>> obtenerVotosEnBlanco() {
        return ResponseEntity.ok(votacionService.getVotosEnBlanco());
    }
}
