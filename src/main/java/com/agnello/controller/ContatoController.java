package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.service.ContatoService;
import com.agnello.util.IpCliente;
import com.agnello.util.LimiteTentativas;
import com.agnello.util.ProtecaoCsrf;
import com.agnello.util.RespostaJson;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Página Contato.
 *   GET  /contato → mostra o formulário (contato.jsp)
 *   POST /contato → recebe a mensagem (contato.js, via fetch) e responde em JSON
 *
 * Proteções contra spam:
 *   - "pote de mel" (honeypot): um campo escondido que pessoas não veem e não
 *     preenchem; robôs preenchem tudo. Se vier preenchido, fingimos sucesso;
 *   - limite de 5 mensagens a cada 10 minutos por endereço IP.
 */
@WebServlet(name = "ContatoController", urlPatterns = {"/contato"})
public class ContatoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ContatoController.class.getName());
    private static final LimiteTentativas LIMITE = new LimiteTentativas(5, 10 * 60 * 1000L);

    private final ContatoService contatoService = new ContatoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/contato.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (!ProtecaoCsrf.requisicaoValida(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Envio recusado. Recarregue a página.");
            return;
        }

        String site = request.getParameter("site"); // o honeypot
        if (site != null && !site.isEmpty()) {
            LOGGER.info("Contato descartado (honeypot preenchido).");
            RespostaJson.enviar(response, "sucesso", true);
            return;
        }

        if (!LIMITE.permitir(IpCliente.de(request))) {
            response.setStatus(429); // Too Many Requests
            RespostaJson.enviar(response, "sucesso", false, "mensagem",
                    "Você enviou várias mensagens seguidas. Aguarde alguns minutos e tente novamente.");
            return;
        }

        try {
            contatoService.enviar(SessaoCliente.atual(request),
                    request.getParameter("nome"),
                    request.getParameter("email"),
                    request.getParameter("assunto"),
                    request.getParameter("pedido"),
                    request.getParameter("mensagem"),
                    request.getParameter("aceite") != null);
            RespostaJson.enviar(response, "sucesso", true);

        } catch (RegraNegocioException e) {
            response.setStatus(422);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao gravar a mensagem de contato", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RespostaJson.enviar(response, "sucesso", false, "mensagem",
                    "Não foi possível enviar agora. Tente novamente ou fale conosco pelo WhatsApp.");
        }
    }
}
