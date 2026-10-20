package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.service.AvisoEstoqueService;
import com.agnello.util.IpCliente;
import com.agnello.util.LimiteTentativas;
import com.agnello.util.Parametros;
import com.agnello.util.ProtecaoCsrf;
import com.agnello.util.RespostaJson;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * POST /avise-me  (produto_id, email) — "Avise-me quando chegar" na página do vinho.
 * Contra spam: honeypot ("site") e no máximo 10 pedidos a cada 10 minutos por IP.
 */
@WebServlet(name = "AvisoEstoqueController", urlPatterns = {"/avise-me"})
public class AvisoEstoqueController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AvisoEstoqueController.class.getName());
    private static final LimiteTentativas LIMITE = new LimiteTentativas(10, 10 * 60 * 1000L);

    private final AvisoEstoqueService service = new AvisoEstoqueService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        if (!ProtecaoCsrf.requisicaoValida(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Pedido recusado. Recarregue a página.");
            return;
        }
        String site = request.getParameter("site");
        if (site != null && !site.isEmpty()) {
            RespostaJson.enviar(response, "sucesso", true); // robô: finge que deu certo
            return;
        }
        if (!LIMITE.permitir(IpCliente.de(request))) {
            response.setStatus(429);
            RespostaJson.enviar(response, "sucesso", false, "mensagem",
                    "Muitos pedidos seguidos. Aguarde alguns minutos e tente novamente.");
            return;
        }
        try {
            boolean novo = service.registrar(SessaoCliente.atual(request),
                    Parametros.inteiro(request.getParameter("produto_id"), -1), request.getParameter("email"));
            RespostaJson.enviar(response, "sucesso", true, "novo", novo);
        } catch (RegraNegocioException e) {
            response.setStatus(422);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar aviso de estoque", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Não foi possível agora. Tente novamente.");
        }
    }
}
