package com.agnello.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    // Configurações carregadas com segurança do ambiente do servidor (com fallbacks para desenvolvimento)
    private static final String SMTP_HOST = getEnvOrDefault("SMTP_HOST", "smtp.gmail.com");
    private static final String SMTP_PORT = getEnvOrDefault("SMTP_PORT", "587");
    private static final String REMETENTE_EMAIL = System.getenv("MAIL_USER");
    private static final String REMETENTE_SENHA = System.getenv("MAIL_PASSWORD");

    /**
     * Método auxiliar para ler variáveis de ambiente de forma segura.
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    /**
     * Envia um e-mail em HTML de forma ASSÍNCRONA. 
     * Melhora drasticamente a performance, pois o utilizador não fica à espera do envio.
     */
    public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
        CompletableFuture.runAsync(() -> {
            try {
                enviarEmail(destinatario, assunto, corpoHtml);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro no envio assíncrono para o destinatário: " + destinatario, e);
            }
        });
    }

    /**
     * Envia um e-mail formatado em HTML de forma síncrona.
     */
    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {
        // 1. Validação defensiva de parâmetros
        if (destinatario == null || destinatario.trim().isEmpty()) {
            throw new IllegalArgumentException("O destinatário do e-mail não pode ser vazio.");
        }
        if (assunto == null || assunto.trim().isEmpty()) {
            throw new IllegalArgumentException("O assunto do e-mail não pode ser vazio.");
        }

        // 2. Proteção de credenciais: Garante que as variáveis de ambiente estão definidas
        if (REMETENTE_EMAIL == null || REMETENTE_SENHA == null) {
            LOGGER.log(Level.SEVERE, "CRÍTICO: As variáveis de ambiente MAIL_USER ou MAIL_PASSWORD não estão configuradas!");
            throw new IllegalStateException("Serviço de e-mail temporariamente indisponível por falha de configuração.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMETENTE_EMAIL, REMETENTE_SENHA);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMETENTE_EMAIL, "Vinheria Agnello"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);
            message.setContent(corpoHtml, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.log(Level.INFO, "E-mail enviado com sucesso para: {0}", destinatario);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Erro crítico ao tentar enviar e-mail para: " + destinatario, e);
            throw new RuntimeException("Não foi possível enviar o e-mail. Tente novamente mais tarde.", e);
        }
    }
}