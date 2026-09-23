package com.agnello.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
        CompletableFuture.runAsync(() -> {
            try {
                enviarEmail(destinatario, assunto, corpoHtml);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro no envio assíncrono para o destinatário: " + destinatario, e);
            }
        });
    }

    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {
        // Variáveis de ambiente configuradas no Render
        String remetente = System.getenv("EMAIL_USER");
        String apiKey = System.getenv("EMAIL_PASSWORD"); // Agora guarda a Chave de API do Brevo

        if (remetente == null || remetente.trim().isEmpty() || apiKey == null || apiKey.trim().isEmpty()) {
            LOGGER.severe("As variáveis de ambiente EMAIL_USER ou EMAIL_PASSWORD não estão configuradas.");
            throw new RuntimeException("Erro de configuração de e-mail no servidor.");
        }

        try {
            // Formatação do JSON exigido pela API v3 do Brevo
            String jsonBody = String.format(
                "{\"sender\":{\"email\":\"%s\",\"name\":\"Vinheria Agnello\"},\"to\":[{\"email\":\"%s\"}],\"subject\":\"%s\",\"htmlContent\":\"%s\"}",
                remetente, destinatario, assunto, corpoHtml.replace("\"", "\\\"").replace("\n", " ")
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                LOGGER.info("E-mail enviado com sucesso via Brevo API para: " + destinatario);
            } else {
                LOGGER.severe("Falha ao enviar e-mail via Brevo. Código: " + response.statusCode() + " - Resposta: " + response.body());
                throw new RuntimeException("Não foi possível enviar o e-mail via API do Brevo.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro crítico ao tentar comunicar com a API do Brevo para: " + destinatario, e);
            throw new RuntimeException("Não foi possível enviar o e-mail. Tente novamente mais tarde.", e);
        }
    }
}