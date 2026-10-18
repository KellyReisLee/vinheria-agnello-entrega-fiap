package com.agnello.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.model.Pedido;
import com.agnello.model.StatusPedido;
import com.agnello.service.ContatoService;
import com.agnello.service.PedidoService;
import com.agnello.service.ProdutoAdminService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Página inicial do painel: indicadores do dia e últimos pedidos. */
@WebServlet(name = "AdminPainelController", urlPatterns = {"/admin", "/admin/"})
public class AdminPainelController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminPainelController.class.getName());

    private final PedidoService pedidoService = new PedidoService();
    private final ProdutoAdminService produtoService = new ProdutoAdminService();
    private final ContatoService contatoService = new ContatoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("secaoAdmin", "painel");
        try {
            Map<StatusPedido, Integer> contagem = pedidoService.contarPorStatus();
            request.setAttribute("aguardandoPagamento", contagem.get(StatusPedido.AGUARDANDO_PAGAMENTO));
            // "Para preparar": pagos e em separação
            request.setAttribute("paraPreparar",
                    contagem.get(StatusPedido.PAGO) + contagem.get(StatusPedido.EM_SEPARACAO));
            request.setAttribute("faturamentoMes", pedidoService.faturamentoDoMes());
            request.setAttribute("mensagensPendentes", contatoService.contarPendentes());
            request.setAttribute("estoqueBaixo", produtoService.contarEstoqueBaixo());
            request.setAttribute("limiteEstoqueBaixo", ProdutoAdminService.LIMITE_ESTOQUE_BAIXO);

            List<Pedido> ultimos = pedidoService.listarParaAdmin(null, null, 1);
            request.setAttribute("ultimosPedidos", ultimos.subList(0, Math.min(8, ultimos.size())));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar os indicadores do painel", e);
            request.setAttribute("erroPainel", true);
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/painel.jsp").forward(request, response);
    }
}
