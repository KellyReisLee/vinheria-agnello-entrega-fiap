package com.agnello.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Pedido;
import com.agnello.model.StatusPedido;
import com.agnello.service.PedidoService;
import com.agnello.util.MensagemFlash;
import com.agnello.util.Parametros;
import com.agnello.util.SessaoAdmin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Pedidos no painel.
 *   GET  /admin/pedidos?status=PAGO&busca=AG-0001&pagina=2  → lista
 *   GET  /admin/pedidos/ver?id=12                            → detalhe
 *   POST /admin/pedidos/status                               → muda o status
 */
@WebServlet(name = "AdminPedidosController",
        urlPatterns = {"/admin/pedidos", "/admin/pedidos/ver", "/admin/pedidos/status"})
public class AdminPedidosController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminPedidosController.class.getName());

    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().endsWith("/ver")) {
            detalhe(request, response);
        } else {
            lista(request, response);
        }
    }

    private void lista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StatusPedido filtro = StatusPedido.doCodigo(request.getParameter("status"));
        String busca = request.getParameter("busca");
        int pagina = Math.max(1, Parametros.inteiro(request.getParameter("pagina"), 1));

        List<Pedido> pedidos = pedidoService.listarParaAdmin(filtro, busca, pagina);
        boolean temProxima = pedidos.size() > PedidoService.PEDIDOS_POR_PAGINA;
        if (temProxima) {
            pedidos = pedidos.subList(0, PedidoService.PEDIDOS_POR_PAGINA);
        }

        request.setAttribute("pedidos", pedidos);
        request.setAttribute("contagem", pedidoService.contarPorStatus());
        request.setAttribute("statusLista", StatusPedido.values());
        request.setAttribute("filtro", filtro);
        request.setAttribute("busca", busca);
        request.setAttribute("pagina", pagina);
        request.setAttribute("temProxima", temProxima);
        request.setAttribute("secaoAdmin", "pedidos");
        request.getRequestDispatcher("/WEB-INF/views/admin/pedidos.jsp").forward(request, response);
    }

    private void detalhe(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Pedido pedido = pedidoService.buscarParaAdmin(Parametros.inteiro(request.getParameter("id"), -1));
        if (pedido == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        request.setAttribute("pedido", pedido);
        request.setAttribute("secaoAdmin", "pedidos");
        request.getRequestDispatcher("/WEB-INF/views/admin/pedido.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!request.getServletPath().endsWith("/status")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int id = Parametros.inteiro(request.getParameter("id"), -1);
        try {
            Pedido pedido = pedidoService.mudarStatus(SessaoAdmin.atual(request), id,
                    request.getParameter("statusAtual"), request.getParameter("statusNovo"));
            MensagemFlash.sucesso(request, "Pedido " + pedido.getNumero() + " agora está \""
                    + pedido.getStatus().getDescricao() + "\"."
                    + (pedido.getEmailCliente() == null ? "" : " O cliente foi avisado por e-mail."));
        } catch (RegraNegocioException e) {
            MensagemFlash.erro(request, e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao mudar o status do pedido id=" + id, e);
            MensagemFlash.erro(request, "Não foi possível mudar o status agora. Nada foi alterado.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/pedidos/ver?id=" + id);
    }
}
