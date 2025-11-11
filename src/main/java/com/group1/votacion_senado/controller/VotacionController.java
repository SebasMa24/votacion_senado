package com.group1.votacion_senado.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.group1.votacion_senado.model.Candidato;
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

    @GetMapping("/resultados")
    public String resultados(Model model, RedirectAttributes redirectAttributes) {

        if (votacionService.votacionActiva()) {
            redirectAttributes.addFlashAttribute("error",
                    "La votación está en curso. Los resultados estarán disponibles cuando finalice.");
            return "redirect:/";
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(votacionService.getFechaHoraInicioVotacion())) {
            redirectAttributes.addFlashAttribute("error", "La votación aún no ha comenzado.");
            return "redirect:/";
        }

        // Curules
        Map<String, Integer> curulesNacional = votacionService.calcularCurulesNacional();
        Map<String, Integer> curulesIndigena = votacionService.calcularCurulesIndigena();

        StringBuilder nacionalesBuilder = new StringBuilder();
        curulesNacional.forEach((partido, curules) -> {
            nacionalesBuilder.append(partido).append("=").append(curules).append(",");
        });

        StringBuilder indigenasBuilder = new StringBuilder();
        curulesIndigena.forEach((partido, curules) -> {
            indigenasBuilder.append(partido).append("=").append(curules).append(",");
        });

        // Oposición
        String partidoOposicion = "Segunda Fuerza Presidencial";
        Map<String, Integer> curulesTotales = votacionService.calcularCurulesTotales(partidoOposicion);

        Map<PartidoPolitico, Integer> curulesPorPartido = new HashMap<>();
        curulesTotales.forEach((nombrePartido, curules) -> {
            partidoPoliticoService.buscarPorNombre(nombrePartido.trim())
                    .ifPresent(p -> curulesPorPartido.put(p, curules));
        });

        Map<PartidoPolitico, Integer> curulesPorPartidoOrdenado = curulesPorPartido.entrySet()
                .stream()
                .sorted(Map.Entry.<PartidoPolitico, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new // Para mantener el orden
                ));

        // Candidatos ganadores
        Map<PartidoPolitico, List<Candidato>> candidatosGanadores = votacionService
                .asignarCandidatosGanadores(curulesPorPartido);

        // Votos en blanco
        Map<Circunscripcion, Integer> votosBlancos = votacionService.getVotosEnBlanco();

        Integer votosBlancoNacional = 0;
        Integer votosBlancoIndigena = 0;

        for (Map.Entry<Circunscripcion, Integer> entry : votosBlancos.entrySet()) {
            if (entry.getKey().toString().equals("NACIONAL")) {
                votosBlancoNacional = entry.getValue();
            } else if (entry.getKey().toString().equals("INDIGENA")) {
                votosBlancoIndigena = entry.getValue();
            }
        }

        // -------------------------
        // Listas completas de partidos
        // -------------------------
        List<PartidoPolitico> partidosNacionales = partidoPoliticoService
                .obtenerPorCircunscripcion(Circunscripcion.NACIONAL);
        List<PartidoPolitico> partidosIndigenas = partidoPoliticoService
                .obtenerPorCircunscripcion(Circunscripcion.INDIGENA);

        partidosNacionales.sort((p1, p2) -> Integer.compare(p2.getTotalVotosP(), p1.getTotalVotosP()));
        partidosIndigenas.sort((p1, p2) -> Integer.compare(p2.getTotalVotosP(), p1.getTotalVotosP()));

        partidosNacionales.forEach(
                p -> p.getCandidatos().sort((c1, c2) -> Integer.compare(c2.getTotalVotosC(), c1.getTotalVotosC())));
        partidosIndigenas.forEach(
                p -> p.getCandidatos().sort((c1, c2) -> Integer.compare(c2.getTotalVotosC(), c1.getTotalVotosC())));

        // -------------------------
        // DATOS PARA LOS GRÁFICOS - NUEVO
        // -------------------------

        // Procesar datos para gráfico nacional
        List<String> nombresNacionales = partidosNacionales.stream()
                .map(PartidoPolitico::getNomPartido)
                .collect(Collectors.toList());

        List<Integer> votosNacionalesList = partidosNacionales.stream()
                .map(PartidoPolitico::getTotalVotosP)
                .collect(Collectors.toList());

        String nombresNacionalesStr = String.join(",", nombresNacionales);
        String votosNacionalesStr = votosNacionalesList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Procesar datos para gráfico indígena
        List<String> nombresIndigenas = partidosIndigenas.stream()
                .map(PartidoPolitico::getNomPartido)
                .collect(Collectors.toList());

        List<Integer> votosIndigenasList = partidosIndigenas.stream()
                .map(PartidoPolitico::getTotalVotosP)
                .collect(Collectors.toList());

        String nombresIndigenasStr = String.join(",", nombresIndigenas);
        String votosIndigenasStr = votosIndigenasList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Participación ciudadana
        int habilitados = usuarioService.obtenerHabilitados();
        int votantes = partidosNacionales.stream().mapToInt(PartidoPolitico::getTotalVotosP).sum()
                + partidosIndigenas.stream().mapToInt(PartidoPolitico::getTotalVotosP).sum()
                + votosBlancos.values().stream().mapToInt(Integer::intValue).sum();
        double porcentaje = ((double) votantes / habilitados) * 100;

        // Pasar todo al modelo
        model.addAttribute("curulesNacionales", curulesNacional);
        model.addAttribute("curulesIndigenas", curulesIndigena);
        model.addAttribute("curulesTotales", curulesPorPartidoOrdenado);
        model.addAttribute("candidatosGanadores", candidatosGanadores);
        model.addAttribute("votosBlancoNacional", votosBlancoNacional);
        model.addAttribute("votosBlancoIndigena", votosBlancoIndigena);
        model.addAttribute("partidoOposicion", partidoOposicion);

        model.addAttribute("partidosNacionales", partidosNacionales);
        model.addAttribute("partidosIndigenas", partidosIndigenas);

        // NUEVOS ATRIBUTOS PARA GRÁFICOS
        model.addAttribute("nombresNacionales", nombresNacionalesStr);
        model.addAttribute("votosNacionales", votosNacionalesStr);
        model.addAttribute("nombresIndigenas", nombresIndigenasStr);
        model.addAttribute("votosIndigenas", votosIndigenasStr);
        model.addAttribute("curulesNacionalesStr", nacionalesBuilder.toString());
        model.addAttribute("curulesIndigenasStr", indigenasBuilder.toString());

        model.addAttribute("habilitados", habilitados);
        model.addAttribute("votantes", votantes);
        model.addAttribute("porcentaje", String.format("%.1f", porcentaje));

        return "resultados";
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
