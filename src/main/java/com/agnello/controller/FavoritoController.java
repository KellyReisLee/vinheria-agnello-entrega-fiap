package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Usuario;
import com.agnello.service.FavoritoService;
import com.agnello.util.Parametros;
import com.agnello.util.ProtecaoCsrf;
import com.agnello.util.RespostaJson;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Coração dos cards (favoritos.js):
 *   POST /favoritos  produto_id=12&favorito=true|false  →  {"sucesso":true,"favorito":true}
 * Sem sessão → 401 com {"login":true}: o JavaScript manda para a página de login.
 */
@WebServlet(name = "FavoritoController", urlPatterns = {"/favoritos"})
public class FavoritoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FavoritoController.class.getName());

    private final FavoritoService service = new FavoritoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        if (!ProtecaoCsrf.requisicaoValida(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Pedido recusado. Recarregue a página.");
            return;
        }
        Usuario cliente = SessaoCliente.atual(request);
        if (cliente == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            RespostaJson.enviar(response, "sucesso", false, "login", true,
                    "mensagem", "Entre na sua conta para guardar favoritos.");
            return;
        }
        try {
            boolean favorito = service.definir(cliente, Parametros.inteiro(request.getParameter("produto_id"), -1),
                    "true".equals(request.getParameter("favorito")));
            RespostaJson.enviar(response, "sucesso", true, "favorito", favorito);
        } catch (RegraNegocioException e) {
            response.setStatus(422);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao alterar favorito", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Não foi possível agora. Tente novamente.");
        }
    }
}
