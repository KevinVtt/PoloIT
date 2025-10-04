package com.acelerador.polo_it_acelerador.services.impl.email;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Envía un email simple en texto plano
     */
    public void enviarEmailSimple(String para, String asunto, String texto) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("sistematicketv1@gmail.com");
            mensaje.setTo(para);
            mensaje.setSubject(asunto);
            mensaje.setText(texto);
            
            javaMailSender.send(mensaje);
            log.info("Email simple enviado exitosamente a: {}", para);
        } catch (Exception e) {
            log.error("Error al enviar email simple a: {}", para, e);
            throw new RuntimeException("Error al enviar email: " + e.getMessage());
        }
    }


    /**
     * Envía un email con contenido HTML
     */
    public void enviarEmailHTML(String para, String asunto, String contenidoHTML) {
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            
            helper.setFrom("sistematicketv1@gmail.com");
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);
            
            javaMailSender.send(mensaje);
            log.info("Email HTML enviado exitosamente a: {}", para);
        } catch (MessagingException e) {
            log.error("Error al enviar email HTML a: {}", para, e);
            throw new RuntimeException("Error al enviar email HTML: " + e.getMessage());
        }
    }

    public void enviarEmailRecuperacion(String para, String nombreUsuario, String resetLink) {
        String contenidoHTML = construirEmailRecuperacion(nombreUsuario, resetLink);
        enviarEmailHTML(para, "Recuperación de contraseña", contenidoHTML);
    }

    private String construirEmailRecuperacion(String nombreUsuario, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #2196F3; 
                              color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .warning { color: #f44336; font-size: 14px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Recuperación de Contraseña</h1>
                    </div>
                    <div class="content">
                        <p>Hola %s,</p>
                        <p>Has solicitado recuperar tu contraseña.</p>
                        <p>Haz clic en el siguiente botón para restablecer tu contraseña:</p>
                        <a href="%s" class="button">Restablecer Contraseña</a>
                        <p>O copia y pega este enlace en tu navegador:</p>
                        <p><a href="%s">%s</a></p>
                        <p class="warning">⚠️ Este enlace expira en 15 minutos.</p>
                        <p>Si no solicitaste este cambio, ignora este email.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombreUsuario, resetLink, resetLink, resetLink);
    }

    /**
     * Envía un email con archivo adjunto
     */
    public void enviarEmailConAdjunto(String para, String asunto, String texto, 
                                      String rutaArchivo) {
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            
            helper.setFrom("noreply@tuapp.com");
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(texto);
            
            // Adjuntar archivo
            FileSystemResource archivo = new FileSystemResource(new File(rutaArchivo));
            helper.addAttachment(archivo.getFilename(), archivo);
            
            javaMailSender.send(mensaje);
            log.info("Email con adjunto enviado exitosamente a: {}", para);
        } catch (MessagingException e) {
            log.error("Error al enviar email con adjunto a: {}", para, e);
            throw new RuntimeException("Error al enviar email con adjunto: " + e.getMessage());
        }
    }

    /**
     * Envía un email a múltiples destinatarios
     */
    public void enviarEmailMultiple(String[] para, String asunto, String texto) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("noreply@tuapp.com");
            mensaje.setTo(para);
            mensaje.setSubject(asunto);
            mensaje.setText(texto);
            
            javaMailSender.send(mensaje);
            log.info("Email múltiple enviado exitosamente a {} destinatarios", para.length);
        } catch (Exception e) {
            log.error("Error al enviar email múltiple", e);
            throw new RuntimeException("Error al enviar email múltiple: " + e.getMessage());
        }
    }

    /**
     * Envía un email HTML completo con múltiples destinatarios y adjuntos
     */
    public void enviarEmailCompleto(String[] para, String[] cc, String[] cco,
                                    String asunto, String contenidoHTML, 
                                    String[] rutasArchivos) {
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            
            helper.setFrom("noreply@tuapp.com");
            helper.setTo(para);
            
            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            
            if (cco != null && cco.length > 0) {
                helper.setBcc(cco);
            }
            
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);
            
            // Adjuntar múltiples archivos
            if (rutasArchivos != null && rutasArchivos.length > 0) {
                for (String rutaArchivo : rutasArchivos) {
                    FileSystemResource archivo = new FileSystemResource(new File(rutaArchivo));
                    helper.addAttachment(archivo.getFilename(), archivo);
                }
            }
            
            javaMailSender.send(mensaje);
            log.info("Email completo enviado exitosamente");
        } catch (MessagingException e) {
            log.error("Error al enviar email completo", e);
            throw new RuntimeException("Error al enviar email completo: " + e.getMessage());
        }
    }

}
