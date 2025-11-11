package com.group1.votacion_senado.controller;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.Base64;

@Controller
@RequestMapping("/certificado")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/enviar")
    public String enviarPdf(@RequestParam("toEmail") String toEmail,
                            @RequestParam("pdfBase64") String pdfBase64,
                            Model model) {
        try {
            // Decodificar el PDF desde Base64
            String base64Data = pdfBase64.split(",")[1]; // quitar prefijo
            byte[] pdfBytes = Base64.getDecoder().decode(base64Data);

            // Crear correo
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("votacion.senado.gr1.is1@outlook.com");
            helper.setTo(toEmail);
            helper.setSubject("Certificado Electoral");
            helper.setText("Adjunto encontrarás tu certificado electoral en PDF.");
            helper.addAttachment("Certificado_Electoral.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);

            model.addAttribute("mensaje", "Correo enviado con PDF a " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error al enviar el correo: " + e.getMessage());
        }

        return "certificado";
    }
}
