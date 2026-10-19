package com.agnello.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Endereco;
import com.agnello.model.Usuario;
import com.agnello.service.ContaService;
import com.agnello.service.EnderecoService;
import com.agnello.service.PedidoService;
import com.agnello.service.RepeticaoPedido;
import com.agnello.util.MensagemFlash;
import com.agnello.util.Parametros;
import com.agnello.util.RespostaJson;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Ações da área pessoal. Todas exigem o cliente logado.
 *
 *   POST /conta/dados                 → editar nome/telefone
 *   POST /conta/senha                 → trocar a senha
 *   POST /conta/enderecos             → adicionar endereço
 *   POST /conta/enderecos/excluir     → excluir endereço
 *   POST /conta/enderecos/principal   → tornar principal
 *   POST /conta/pedidos/cancelar      → cancelar pedido não pago
 *   GET  /conta/pedidos/repetir?id=   → JSON com os vinhos para "Comprar novamente"
 *
 * Os POST são formulários normais com token CSRF; depois de cada um a página
 * é redirecionada de volta (padrão Post/Redirect/Get), com uma mensagem.
 */
@WebServlet(name = "ContaController", urlPatterns = {"/conta/dados", "/conta/senha", "/conta/enderecos",
        "/conta/enderecos/excluir", "/conta/enderecos/principal", "/conta/pedidos/cancelar", "/conta/pedidos/repetir"})
public class ContaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ContaController.class.getName());

    private final ContaService contaService = new ContaService();
    private final EnderecoService enderecoService = new EnderecoService();
    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario cliente = SessaoCliente.atual(request);
        if (!request.getServletPath().equals("/conta/pedidos/repetir")) {
            response.sendRedirect(request.getContextPath() + "/area-pessoal");
            return;
        }
        response.setHeader("Cache-Control", "no-store");
        if (cliente == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Entre na sua conta.");
            return;
        }
        try {
            RepeticaoPedido r = pedidoService.repetir(cliente, Parametros.inteiro(request.getParameter("id"), -1));
            List<Map<String, Object>> itens = new ArrayList<>();
            for (RepeticaoPedido.Item i : r.getItens()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", String.valueOf(i.getProduto().getId()));
                m.put("nome", i.getProduto().getNome());
                m.put("preco", i.getProduto().getPreco());
                m.put("origem", i.getProduto().getOrigem() + " • " + i.getProduto().getTipo());
                m.put("imagem", request.getContextPath() + i.getProduto().getImagemUrl());
                m.put("quantidade", i.getQuantidade());
                itens.add(m);
            }
            RespostaJson.enviar(response, "sucesso", true, "itens", itens, "indisponiveis", r.getIndisponiveis());
        } catch (RegraNegocioException e) {
            response.setStatus(422);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro no comprar novamente", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Não foi possível agora. Tente novamente.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Usuario cliente = SessaoCliente.atual(request);
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String rota = request.getServletPath();
        String secao = rota.contains("enderecos") ? "enderecos" : rota.contains("pedidos") ? "pedidos" : "dados";

        if (!SessaoCliente.csrfValido(request)) {
            MensagemFlash.erro(request, "O formulário expirou. Confira e tente novamente.");
            voltar(request, response, secao);
            return;
        }

        try {
            switch (rota) {
                case "/conta/dados":
                    Usuario atualizado = contaService.atualizarDados(cliente, request.getParameter("nome"),
                            request.getParameter("sobrenome"), request.getParameter("razaoSocial"),
                            request.getParameter("telefone"));
                    SessaoCliente.atualizar(request, atualizado);
                    MensagemFlash.sucesso(request, "Dados atualizados.");
                    break;
                case "/conta/senha":
                    contaService.trocarSenha(cliente, request.getParameter("senhaAtual"),
                            request.getParameter("novaSenha"), request.getParameter("confirmaSenha"));
                    MensagemFlash.sucesso(request, "Senha alterada. Enviamos um aviso para o seu e-mail.");
                    break;
                case "/conta/enderecos":
                    enderecoService.adicionar(cliente, lerEndereco(request));
                    MensagemFlash.sucesso(request, "Endereço guardado.");
                    break;
                case "/conta/enderecos/excluir":
                    enderecoService.excluir(cliente, Parametros.inteiro(request.getParameter("id"), -1));
                    MensagemFlash.sucesso(request, "Endereço excluído.");
                    break;
                case "/conta/enderecos/principal":
                    enderecoService.tornarPrincipal(cliente, Parametros.inteiro(request.getParameter("id"), -1));
                    MensagemFlash.sucesso(request, "Endereço principal atualizado.");
                    break;
                case "/conta/pedidos/cancelar":
                    pedidoService.cancelarPeloCliente(cliente, Parametros.inteiro(request.getParameter("id"), -1));
                    MensagemFlash.sucesso(request, "Pedido cancelado. Enviamos a confirmação para o seu e-mail.");
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
            }
        } catch (RegraNegocioException e) {
            MensagemFlash.erro(request, e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro na área pessoal: " + rota, e);
            MensagemFlash.erro(request, "Não foi possível concluir agora. Tente novamente.");
        }
        voltar(request, response, secao);
    }

    static Endereco lerEndereco(HttpServletRequest request) {
        Endereco e = new Endereco();
        e.setApelido(request.getParameter("apelido"));
        e.setCep(request.getParameter("cep"));
        e.setLogradouro(request.getParameter("logradouro"));
        e.setNumero(request.getParameter("numero"));
        e.setComplemento(request.getParameter("complemento"));
        e.setBairro(request.getParameter("bairro"));
        e.setCidade(request.getParameter("cidade"));
        e.setPrincipal(request.getParameter("principal") != null);
        return e;
    }

    private static void voltar(HttpServletRequest request, HttpServletResponse response, String secao)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/area-pessoal#" + secao);
    }
}
