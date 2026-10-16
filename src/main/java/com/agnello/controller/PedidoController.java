package com.agnello.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.ItemPedido;
import com.agnello.model.Pedido;
import com.agnello.model.Usuario;
import com.agnello.service.NovoPedido;
import com.agnello.service.PedidoService;
import com.agnello.util.ProtecaoCsrf;
import com.agnello.util.RespostaJson;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Recebe o pedido do checkout (POST /pedidos, enviado pelo checkout.js) e
 * responde em JSON:
 *   sucesso  → {"sucesso":true, "pedido":{numero, total, itens…}}
 *   problema → {"sucesso":false, "mensagem":"texto para o cliente"}
 *
 * Parâmetros recebidos (formulário):
 *   produto_id + quantidade   (repetidos, um par por vinho do carrinho)
 *   entrega        ENTREGA | RETIRADA
 *   pagamento      pix | credit | boleto
 *   parcelas       1 a 6 (só no cartão)
 *   cep, logradouro, numero, complemento, bairro, cidade
 *
 * Os dados do cartão NÃO são enviados: o servidor nunca deve receber o número
 * nem o CVV (norma PCI-DSS). Isso é trabalho do gateway de pagamento.
 */
@WebServlet(name = "PedidoController", urlPatterns = {"/pedidos"})
public class PedidoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PedidoController.class.getName());

    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        if (!ProtecaoCsrf.requisicaoValida(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Pedido recusado. Recarregue a página.");
            return;
        }

        Usuario cliente = SessaoCliente.atual(request);
        if (cliente == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            RespostaJson.enviar(response, "sucesso", false, "mensagem",
                    "A sua sessão expirou. Entre novamente para concluir o pedido.");
            return;
        }

        try {
            Pedido pedido = pedidoService.criarPedido(cliente, lerDados(request));
            response.setStatus(HttpServletResponse.SC_CREATED);
            RespostaJson.enviar(response, "sucesso", true, "pedido", paraMapa(pedido));

        } catch (RegraNegocioException e) {
            response.setStatus(422); // "Unprocessable Content": os dados chegaram, mas não são aceitáveis
            RespostaJson.enviar(response, "sucesso", false, "mensagem", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao gravar o pedido do cliente id=" + cliente.getId(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RespostaJson.enviar(response, "sucesso", false, "mensagem",
                    "Não foi possível registrar o pedido agora. Nenhum valor foi cobrado. Tente novamente.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Não existe página /pedidos: os pedidos aparecem na área pessoal
        response.sendRedirect(request.getContextPath() + "/area-pessoal#pedidos");
    }

    private static NovoPedido lerDados(HttpServletRequest request) throws RegraNegocioException {
        NovoPedido dados = new NovoPedido();
        String[] ids = request.getParameterValues("produto_id");
        String[] quantidades = request.getParameterValues("quantidade");
        if (ids != null) {
            if (quantidades == null || quantidades.length != ids.length) {
                throw new RegraNegocioException("Carrinho inválido. Recarregue a página.");
            }
            for (int i = 0; i < ids.length; i++) {
                dados.adicionarItem(numero(ids[i]), numero(quantidades[i]));
            }
        }
        dados.setTipoEntrega(request.getParameter("entrega"));
        dados.setFormaPagamento(request.getParameter("pagamento"));
        String parcelas = request.getParameter("parcelas");
        dados.setParcelas(parcelas == null || parcelas.isBlank() ? 1 : numero(parcelas));
        dados.setCep(request.getParameter("cep"));
        dados.setLogradouro(request.getParameter("logradouro"));
        dados.setNumero(request.getParameter("numero"));
        dados.setComplemento(request.getParameter("complemento"));
        dados.setBairro(request.getParameter("bairro"));
        dados.setCidade(request.getParameter("cidade"));
        return dados;
    }

    private static int numero(String texto) throws RegraNegocioException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new RegraNegocioException("Carrinho inválido. Recarregue a página.");
        }
    }

    /** O que o checkout.js precisa para mostrar a confirmação. */
    private static Map<String, Object> paraMapa(Pedido pedido) {
        List<Map<String, Object>> itens = new ArrayList<>();
        for (ItemPedido item : pedido.getItens()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("nome", item.getNomeProduto());
            m.put("quantidade", item.getQuantidade());
            m.put("total", item.getTotal());
            itens.add(m);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("numero", pedido.getNumero());
        m.put("status", pedido.getStatus().getDescricao());
        m.put("pagamento", pedido.getFormaPagamento().getCodigo());
        m.put("parcelas", pedido.getParcelas());
        m.put("valorParcela", pedido.getValorParcela());
        m.put("entrega", pedido.getTipoEntrega().getDescricao());
        m.put("endereco", pedido.getEndereco());
        m.put("subtotal", pedido.getSubtotal());
        m.put("frete", pedido.getFrete());
        m.put("desconto", pedido.getDesconto());
        m.put("total", pedido.getTotal());
        m.put("itens", itens);
        return m;
    }
}
