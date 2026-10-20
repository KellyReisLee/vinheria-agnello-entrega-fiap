package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Pedido;
import com.agnello.model.Usuario;
import com.agnello.pagamento.ErroGateway;
import com.agnello.pagamento.PagamentoGateway;
import com.agnello.service.PagamentoService;
import com.agnello.util.MensagemFlash;
import com.agnello.util.Parametros;
import com.agnello.util.SessaoCliente;
import com.agnello.util.UrlAplicacao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Páginas do pagamento online:
 *
 *   POST /pagamento/pagar     "Pagar agora" na área pessoal → vai para o Mercado Pago
 *   GET  /pagamento/retorno   o Mercado Pago devolve o cliente aqui depois de pagar
 *
 * O aviso automático do Mercado Pago (webhook) fica no NotificacaoPagamentoController.
 */
@WebServlet(name = "PagamentoController", urlPatterns = {"/pagamento/pagar", "/pagamento/retorno"})
public class PagamentoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PagamentoController.class.getName());

    private final PagamentoService pagamentoService = new PagamentoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ctx = request.getContextPath();
        if (!"/pagamento/pagar".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Usuario cliente = SessaoCliente.atual(request);
        if (cliente == null) {
            response.sendRedirect(ctx + "/login?voltar=" + java.net.URLEncoder.encode("/area-pessoal",
                    java.nio.charset.StandardCharsets.UTF_8));
            return;
        }
        if (!SessaoCliente.csrfValido(request)) {
            MensagemFlash.erro(request, "O formulário expirou. Tente novamente.");
            response.sendRedirect(ctx + "/area-pessoal#pedidos");
            return;
        }
        try {
            String url = pagamentoService.iniciarPorId(cliente, Parametros.inteiro(request.getParameter("id"), -1),
                    UrlAplicacao.base(request));
            response.sendRedirect(url);
            return;
        } catch (RegraNegocioException e) {
            MensagemFlash.erro(request, e.getMessage());
        } catch (ErroGateway | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Não foi possível abrir o pagamento (Pagar agora)", e);
            MensagemFlash.erro(request, "Não conseguimos abrir o pagamento agora. Tente de novo em alguns minutos.");
        }
        response.sendRedirect(ctx + "/area-pessoal#pedidos");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/pagamento/retorno".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setHeader("Cache-Control", "no-store");
        String ctx = request.getContextPath();
        Usuario cliente = SessaoCliente.atual(request);
        if (cliente == null) {
            // Volta para esta mesma página depois de entrar
            String aqui = "/pagamento/retorno" + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
            response.sendRedirect(ctx + "/login?voltar=" + java.net.URLEncoder.encode(aqui,
                    java.nio.charset.StandardCharsets.UTF_8));
            return;
        }

        // O Mercado Pago acrescenta ?payment_id=…&status=… (nomes antigos: collection_id/collection_status)
        String pagamentoId = primeiro(request.getParameter("payment_id"), request.getParameter("collection_id"));
        String statusInformado = primeiro(request.getParameter("status"), request.getParameter("collection_status"));

        PagamentoService.Resultado r;
        try {
            r = pagamentoService.processarRetorno(cliente, Parametros.inteiro(request.getParameter("pedido"), -1),
                    pagamentoId);
        } catch (RegraNegocioException e) {
            response.sendRedirect(ctx + "/area-pessoal#pedidos");
            return;
        }

        request.setAttribute("pedido", r.getPedido());
        request.setAttribute("situacao", situacao(r.getPedido(), r.getPagamento(), statusInformado));
        request.setAttribute("pagamentoOnline", pagamentoService.isAtivo());
        request.setAttribute("csrf", SessaoCliente.tokenCsrf(request));
        request.getRequestDispatcher("/WEB-INF/views/pagamento-retorno.jsp").forward(request, response);
    }

    /**
     * O que mostrar ao cliente. O que vale é o status do pedido no NOSSO banco
     * (confirmado com a API); o "status" do endereço só escolhe a mensagem
     * enquanto a confirmação não chega — nunca muda o pedido.
     */
    static String situacao(Pedido pedido, PagamentoGateway pg, String statusInformado) {
        if (pedido.getStatus().isPositivo()) {
            return "aprovado";
        }
        if (pedido.getStatus() == com.agnello.model.StatusPedido.CANCELADO) {
            return "cancelado";
        }
        String status = pg != null ? pg.getStatus() : statusInformado;
        if (PagamentoGateway.APROVADO.equals(status)) {
            return "confirmando"; // aprovado lá, ainda não confirmado aqui
        }
        if (PagamentoGateway.PENDENTE.equals(status) || PagamentoGateway.EM_ANALISE.equals(status)) {
            return "pendente";
        }
        if (PagamentoGateway.RECUSADO.equals(status)) {
            return "recusado";
        }
        return "nao_concluido";
    }

    private static String primeiro(String a, String b) {
        if (a != null && !a.isBlank() && !"null".equals(a)) {
            return a;
        }
        return b != null && !b.isBlank() && !"null".equals(b) ? b : null;
    }
}
