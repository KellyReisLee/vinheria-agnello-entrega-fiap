package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.service.OrcamentoService;
import com.agnello.util.IpCliente;
import com.agnello.util.LimiteTentativas;
import com.agnello.util.ProtecaoCsrf;
import com.agnello.util.RespostaJson;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * POST /orcamento — pedido de orçamento da Calculadora de Eventos (calculadora.js).
 * Responde em JSON: {"sucesso":true,"numero":12} ou {"sucesso":false,"mensagem":"…"}.
 *
 * Contra spam: campo "pote de mel" (site) e no máximo 3 pedidos a cada 10 min por IP.
 */
@WebServlet(name = "OrcamentoController", urlPatterns = {"/orcamento"})
public class OrcamentoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OrcamentoController.class.getName());
    private static final LimiteTentativas LIMITE = new LimiteTentativas(3, 10 * 60 * 1000L);

    private final OrcamentoService orcamentoService = new OrcamentoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        if (!ProtecaoCsrf.requisicaoValida(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Envio recusado. Recarregue a página.");
            return;
        }
        String site = request.getParameter("site"); // honeypot: pessoas não veem este campo
        if (site != null && !site.isEmpty()) {
            LOGGER.info("Orçamento descartado (honeypot preenchido).");
            RespostaJson.enviar(response, "sucesso", true);
            return;
        }
        if (!LIMITE.permitir(IpCliente.de(request))) {
            response.setStatus(429);
            RespostaJson.enviar(response, "sucesso", false, "mensagem",
                    "Você enviou vários pedidos seguidos. Aguarde alguns minutos e tente novamente.");
            return;
        }

        try {
            int numero = orcamentoService.enviar(SessaoCliente.atual(request),
                    request.getParameter("nome"), request.getParameter("email"), request.getParameter("telefone"),
                    request.getParameter("orcamento"), request.getParameter("observacoes"),
                    request.getParameter("estilo"), request.getParameter("convidados"),
                    request.getParameter("duracao"));
            RespostaJson.enviar(response, "sucesso", true, "numero", numero);
        } catch (RegraNegocioException e) {
            response.setStatus(422);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao gravar o pedido de orçamento", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RespostaJson.enviar(response, "sucesso", false, "mensagem",
                    "Não foi possível enviar agora. Tente novamente em alguns minutos ou fale conosco pelo WhatsApp.");
        }
    }
}
