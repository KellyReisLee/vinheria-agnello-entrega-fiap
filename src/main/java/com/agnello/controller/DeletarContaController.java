package com.agnello.controller;

import com.agnello.service.ClienteService;
import com.agnello.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/deletar-conta")
public class DeletarContaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DeletarContaController.class.getName());
    private ClienteService clienteService = new ClienteService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Verifica se existe uma sessão ativa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Sessao expirada.");
            return;
        }

        // Obtém o e-mail do utilizador diretamente da sessão (segurança no backend)
        Usuario usuarioLogado = (Usuario) session.getAttribute("clienteLogado");
        String emailSessao = usuarioLogado.getEmail();
        
        // Obtém o e-mail que o utilizador digitou no campo de confirmação do modal
        String emailConfirmacao = request.getParameter("emailConfirmacao");

        try {
            // Chama o service para validar e executar a exclusão
            clienteService.deletarConta(emailSessao, emailConfirmacao);
            
            // Se correu tudo bem, destrói a sessão completamente (Desloga o cliente)
            session.invalidate();
            
            // Retorna sucesso 200 OK para o AJAX do frontend acionar o modal de sucesso
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Conta encerrada com sucesso.");

        } catch (IllegalArgumentException e) {
            // Captura erros de validação (ex: e-mails não coincidem)
            LOGGER.log(Level.WARNING, "Falha ao tentar excluir conta: {0}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
            
        } catch (Exception e) {
            // Captura erros críticos de banco de dados ou sistema
            LOGGER.log(Level.SEVERE, "Erro crítico ao apagar conta do e-mail: " + emailSessao, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Ocorreu um erro interno ao processar a exclusão da conta.");
        }
    }
}