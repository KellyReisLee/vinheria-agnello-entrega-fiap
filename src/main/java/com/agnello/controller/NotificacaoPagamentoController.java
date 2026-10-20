package com.agnello.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.pagamento.AssinaturaWebhook;
import com.agnello.pagamento.ConfigPagamento;
import com.agnello.pagamento.ErroGateway;
import com.agnello.service.PagamentoService;
import com.agnello.util.Json;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Aviso automático do Mercado Pago (webhook): POST /pagamento/notificacao
 *
 *   ?data.id=123456789&type=payment   +   corpo {"type":"payment","data":{"id":"123456789"},…}
 *
 * Responde 200 depressa (o Mercado Pago espera até 22 s e depois repete o aviso).
 * Responde 500 se não conseguiu falar com o Mercado Pago, para ele tentar de novo.
 *
 * Não tem proteção CSRF de propósito: quem chama é o servidor do Mercado Pago.
 * A proteção é outra: a assinatura (MP_WEBHOOK_SECRET) e, sobretudo, a loja
 * nunca confia no aviso — vai buscar o pagamento à API antes de mudar o pedido.
 */
@WebServlet(name = "NotificacaoPagamentoController", urlPatterns = {"/pagamento/notificacao"})
public class NotificacaoPagamentoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(NotificacaoPagamentoController.class.getName());
    private static final int TAMANHO_MAXIMO = 16 * 1024;

    private final PagamentoService pagamentoService = new PagamentoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> corpo = lerCorpo(request);

        String tipo = primeiro(request.getParameter("type"), request.getParameter("topic"),
                corpo == null ? null : Json.texto(corpo, "type"));
        String idDaUrl = primeiro(request.getParameter("data.id"), request.getParameter("id"), null);
        String pagamentoId = primeiro(idDaUrl, corpo == null ? null : Json.texto(corpo, "data", "id"), null);

        if (!"payment".equals(tipo) || pagamentoId == null) {
            response.setStatus(HttpServletResponse.SC_OK); // outros avisos (ex.: merchant_order) não interessam
            return;
        }
        if (!pagamentoService.isAtivo()) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String segredo = ConfigPagamento.segredoWebhook();
        if (segredo != null) {
            // A assinatura é calculada sobre o data.id da URL
            String idAssinado = idDaUrl != null ? idDaUrl : pagamentoId;
            if (!AssinaturaWebhook.valida(request.getHeader("x-signature"), request.getHeader("x-request-id"),
                    idAssinado, segredo)) {
                LOGGER.log(Level.WARNING, "Aviso de pagamento com assinatura inválida (pagamento {0}) — ignorado",
                        pagamentoId);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        try {
            pagamentoService.processar(pagamentoId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ErroGateway | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Falha ao processar o aviso do pagamento " + pagamentoId
                    + " (o Mercado Pago vai tentar de novo)", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static Map<String, Object> lerCorpo(HttpServletRequest request) {
        try (InputStream in = request.getInputStream()) {
            byte[] bytes = in.readNBytes(TAMANHO_MAXIMO + 1);
            if (bytes.length == 0 || bytes.length > TAMANHO_MAXIMO) {
                return null;
            }
            return Json.lerObjeto(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    private static String primeiro(String a, String b, String c) {
        for (String v : new String[] {a, b, c}) {
            if (v != null && !v.isBlank()) {
                return v.trim();
            }
        }
        return null;
    }
}
