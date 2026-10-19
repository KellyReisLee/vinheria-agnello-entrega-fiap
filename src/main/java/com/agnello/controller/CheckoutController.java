package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Usuario;
import com.agnello.service.ClienteService;
import com.agnello.service.EnderecoService;
import com.agnello.util.RespostaJson;
import com.agnello.util.SessaoCliente;
import com.agnello.util.UrlAplicacao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Página de checkout + pedidos AJAX do passo "Identificação" (checkout.js):
 *  1. só e-mail                → {"existe": true|false}
 *  2. e-mail + senha           → login       → {"sucesso": …, "mensagem": …}
 *  3. e-mail + senha + dados   → cadastro    → {"sucesso": …, "mensagem": …}
 */
@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CheckoutController.class.getName());
    private static final String MSG_ERRO_INTERNO = "Não foi possível concluir agora. Tente novamente em alguns instantes.";

    private final ClienteService clienteService = new ClienteService();
    private final EnderecoService enderecoService = new EnderecoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = SessaoCliente.atual(request);
        if (usuario != null) {
            request.setAttribute("usuarioLogado", usuario);
            try {
                // Endereços guardados: o cliente escolhe um em vez de digitar de novo
                request.setAttribute("enderecos", enderecoService.listar(usuario));
                request.setAttribute("maxEnderecos", EnderecoService.MAX_ENDERECOS);
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "Não foi possível carregar os endereços no checkout", e);
            }
        }
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        boolean temDadosCadastro = !vazio(request.getParameter("nome")) || !vazio(request.getParameter("razao_social"));

        try {
            if (!vazio(senha) && temDadosCadastro) {
                cadastrar(request, response, email, senha);
            } else if (!vazio(senha)) {
                Usuario usuario = clienteService.autenticar(email, senha);
                SessaoCliente.iniciar(request, usuario);
                RespostaJson.enviar(response, "sucesso", true, "mensagem", "");
            } else {
                RespostaJson.enviar(response, "existe", clienteService.emailCadastrado(email));
            }

        } catch (RegraNegocioException e) {
            RespostaJson.enviar(response, "sucesso", false, "mensagem", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no checkout", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", MSG_ERRO_INTERNO);
        }
    }

    /**
     * Cria a conta e já inicia a sessão, para o cliente terminar a compra sem sair
     * do checkout. O e-mail de ativação é enviado na mesma; sem ativar, o próximo
     * login (fora desta sessão) pede a confirmação do e-mail.
     */
    private void cadastrar(HttpServletRequest request, HttpServletResponse response, String email, String senha)
            throws RegraNegocioException, IOException {
        Usuario criado = clienteService.cadastrarCliente(
                request.getParameter("tipo_cliente"), email, senha,
                request.getParameter("telefone"),
                request.getParameter("nome"), request.getParameter("sobrenome"), request.getParameter("cpf"),
                request.getParameter("razao_social"), request.getParameter("cnpj"),
                UrlAplicacao.base(request));

        SessaoCliente.iniciar(request, clienteService.buscarParaSessao(criado.getId()));
        RespostaJson.enviar(response, "sucesso", true);
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
