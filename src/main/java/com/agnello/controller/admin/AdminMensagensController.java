package com.agnello.controller.admin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.MensagemContato;
import com.agnello.service.ContatoService;
import com.agnello.util.MensagemFlash;
import com.agnello.util.Parametros;
import com.agnello.util.SessaoAdmin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Mensagens do formulário de contato.
 *   GET  /admin/mensagens?todas        → lista (por padrão só as pendentes)
 *   GET  /admin/mensagens/ver?id=3     → mensagem + formulário de resposta
 *   POST /admin/mensagens/responder    → envia a resposta por e-mail
 *   POST /admin/mensagens/resolver     → fecha sem enviar e-mail
 */
@WebServlet(name = "AdminMensagensController", urlPatterns = {"/admin/mensagens", "/admin/mensagens/ver",
        "/admin/mensagens/responder", "/admin/mensagens/resolver"})
public class AdminMensagensController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminMensagensController.class.getName());

    private final ContatoService service = new ContatoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("secaoAdmin", "mensagens");
        if (request.getServletPath().endsWith("/ver")) {
            MensagemContato m = service.buscar(Parametros.inteiro(request.getParameter("id"), -1));
            if (m == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            request.setAttribute("mensagem", m);
            request.getRequestDispatcher("/WEB-INF/views/admin/mensagem.jsp").forward(request, response);
            return;
        }
        boolean todas = request.getParameter("todas") != null;
        request.setAttribute("mensagens", service.listar(!todas));
        request.setAttribute("todas", todas);
        request.getRequestDispatcher("/WEB-INF/views/admin/mensagens.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        int id = Parametros.inteiro(request.getParameter("id"), -1);
        String rota = request.getServletPath();
        try {
            if (rota.endsWith("/responder")) {
                service.responder(SessaoAdmin.atual(request), id, request.getParameter("resposta"));
                MensagemFlash.sucesso(request, "Resposta enviada por e-mail.");
            } else if (rota.endsWith("/resolver")) {
                service.marcarResolvida(SessaoAdmin.atual(request), id);
                MensagemFlash.sucesso(request, "Mensagem marcada como resolvida.");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } catch (RegraNegocioException e) {
            MensagemFlash.erro(request, e.getMessage());
            // Guarda o rascunho para não perder o que foi escrito
            request.getSession().setAttribute("rascunhoResposta", request.getParameter("resposta"));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao tratar a mensagem id=" + id, e);
            MensagemFlash.erro(request, "Não foi possível concluir agora. Tente novamente.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/mensagens/ver?id=" + id);
    }
}
