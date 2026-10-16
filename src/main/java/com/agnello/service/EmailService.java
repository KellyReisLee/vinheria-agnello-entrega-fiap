package com.agnello.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.util.RespostaJson;

/**
 * Envio de e-mails pela API do Brevo.
 *
 * Variáveis de ambiente:
 *   EMAIL_USER      remetente (e-mail verificado no Brevo)
 *   EMAIL_PASSWORD  chave de API do Brevo
 *   EMAIL_CONTATO   (opcional) caixa que recebe as mensagens do formulário de
 *                   contato; se não existir, usa o próprio EMAIL_USER
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private static final URI API_BREVO = URI.create("https://api.brevo.com/v3/smtp/email");

    /** Um único cliente HTTP para a aplicação inteira (é caro criar um por e-mail). */
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Envia em segundo plano: a página responde ao cliente sem esperar pelo Brevo. */
    public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
        enviarEmailAssincrono(destinatario, assunto, corpoHtml, null);
    }

    /** Igual ao anterior, com "Responder para" (usado no contato: a resposta vai para o cliente). */
    public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml, String responderPara) {
        CompletableFuture.runAsync(() -> {
            try {
                enviarEmail(destinatario, assunto, corpoHtml, responderPara);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Erro no envio assíncrono de e-mail: " + assunto, e);
            }
        });
    }

    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {
        enviarEmail(destinatario, assunto, corpoHtml, null);
    }

    public void enviarEmail(String destinatario, String assunto, String corpoHtml, String responderPara) {
        String remetente = System.getenv("EMAIL_USER");
        String apiKey = System.getenv("EMAIL_PASSWORD"); // chave de API do Brevo

        if (vazio(remetente) || vazio(apiKey)) {
            LOGGER.severe("As variáveis de ambiente EMAIL_USER ou EMAIL_PASSWORD não estão configuradas.");
            throw new IllegalStateException("Erro de configuração de e-mail no servidor.");
        }

        // Montamos o JSON com o RespostaJson, que escapa aspas, barras e quebras de
        // linha: um assunto ou mensagem com " não quebra o pedido ao Brevo.
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("sender", Map.of("email", remetente, "name", "Vinheria Agnello"));
        corpo.put("to", List.of(Map.of("email", destinatario)));
        if (!vazio(responderPara)) {
            corpo.put("replyTo", Map.of("email", responderPara));
        }
        corpo.put("subject", assunto);
        corpo.put("htmlContent", corpoHtml);

        HttpRequest request = HttpRequest.newBuilder(API_BREVO)
                .timeout(Duration.ofSeconds(20))
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(RespostaJson.paraJson(corpo)))
                .build();

        try {
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                LOGGER.severe("Falha ao enviar e-mail via Brevo. Código: " + response.statusCode()
                        + " - Resposta: " + response.body());
                throw new IllegalStateException("O Brevo recusou o e-mail (código " + response.statusCode() + ").");
            }
            LOGGER.info("E-mail enviado via Brevo: " + assunto);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Sem comunicação com a API do Brevo.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envio de e-mail interrompido.", e);
        }
    }

    /** Caixa de entrada da loja para o formulário de contato. */
    public static String emailDaLoja() {
        String contato = System.getenv("EMAIL_CONTATO");
        return vazio(contato) ? System.getenv("EMAIL_USER") : contato.trim();
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
