package com.agnello.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.util.EmailTemplateUtil;

public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    public void enviarEmailAssincrono(String destinatario, String linkRedefinicao) {
        CompletableFuture.runAsync(() -> {
            try {
                enviarEmail(destinatario, linkRedefinicao);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro no envio assíncrono para o destinatário: " + destinatario, e);
            }
        });
    }

    public void enviarEmail(String destinatario, String linkRedefinicao) {
        // Variáveis de ambiente configuradas no Render (ex: EMAIL_USER e EMAIL_PASSWORD)
        String remetente = System.getenv("EMAIL_USER");
        String senha = System.getenv("EMAIL_PASSWORD");

        if (remetente == null || remetente.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            LOGGER.severe("As variáveis de ambiente EMAIL_USER ou EMAIL_PASSWORD não estão configuradas.");
            throw new RuntimeException("Erro de configuração de e-mail no servidor.");
        }

        // Configurações para a porta 465 (SSL/TLS)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true"); // Ativa o SSL direto para a porta 465
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senha);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remetente, "Vinheria Agnello"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Redefinição de Senha - Vinheria Agnello");
            
            // Utiliza o template HTML refinado que criámos anteriormente
            String htmlContent = EmailTemplateUtil.getCorpoRecuperacaoSenha(linkRedefinicao);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("E-mail enviado com sucesso para: " + destinatario);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro crítico ao tentar enviar e-mail via porta 465 para: " + destinatario, e);
            throw new RuntimeException("Não foi possível enviar o e-mail. Tente novamente mais tarde.", e);
        }
    }
}