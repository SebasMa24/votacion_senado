package com.group1.votacion_senado.controller;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;


@Controller
@RequestMapping("/certificado")
public class EmailController {

    private static final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");

    @PostMapping("/enviar")
    public String enviarPdf(@RequestParam("toEmail") String toEmail,
            @RequestParam("pdfBase64") String pdfBase64,
            Model model) {
        try {
            // 🔹 Decodificar el PDF desde Base64
            String base64Data = pdfBase64.split(",")[1]; // quitar prefijo
            byte[] pdfBytes = Base64.getDecoder().decode(base64Data);

            // 🔹 Crear contenido del correo
            Email from = new Email("votacion.senado.gr1.is1@outlook.com", "Votación Senado");
            Email to = new Email(toEmail);
            String subject = "Certificado Electoral";
            Content content = new Content("text/plain", "Adjunto encontrarás tu certificado electoral en PDF.");

            Mail mail = new Mail(from, subject, to, content);

            // 🔹 Adjuntar el PDF
            Attachments attachment = new Attachments();
            attachment.setContent(Base64.getEncoder().encodeToString(pdfBytes));
            attachment.setType("application/pdf");
            attachment.setFilename("Certificado_Electoral.pdf");
            attachment.setDisposition("attachment");
            mail.addAttachments(attachment);

            // 🔹 Enviar usando SendGrid API
            SendGrid sg = new SendGrid(SENDGRID_API_KEY);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            // 🔹 Verificar respuesta
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                model.addAttribute("mensaje", "Correo enviado con PDF a " + toEmail);
            } else {
                model.addAttribute("mensaje", "Error al enviar el correo: " + response.getBody());
            }

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error al enviar el correo: " + e.getMessage());
        }

        return "certificado";
    }
}
