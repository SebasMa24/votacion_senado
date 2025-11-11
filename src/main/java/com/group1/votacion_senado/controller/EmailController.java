package com.group1.votacion_senado.controller;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Base64;

@RestController
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/enviar-pdf")
    public String enviarPdf(@RequestBody PdfRequest request) throws MessagingException {
        // Decodificar Base64
        String base64Data = request.getPdfBase64().split(",")[1]; // quitar prefijo data:application/pdf;base64,
        byte[] pdfBytes = Base64.getDecoder().decode(base64Data);

        //  Crear correo con adjunto
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("REMOVED_EMAIL");
        helper.setSubject("Certificado Electoral");
        helper.setText("Adjunto encontrarás tu certificado electoral en PDF.");
        helper.addAttachment("Certificado_Electoral.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);

        return "Correo enviado con PDF!";
    }

    public static class PdfRequest {
        private String pdfBase64;
        public String getPdfBase64() { return pdfBase64; }
        public void setPdfBase64(String pdfBase64) { this.pdfBase64 = pdfBase64; }
    }
}
