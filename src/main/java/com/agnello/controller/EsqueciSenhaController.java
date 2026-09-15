package com.agnello.controller;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/esqueci-senha")
public class EsqueciSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Exibe a tela de solicitação de e-mail
        request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "Por favor, informe um e-mail válido.");
            request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
            return;
        }

        try {
            ClienteDAO dao = new ClienteDAO();
            // 1. Valida se o cliente realmente existe no banco de dados pelo e-mail
            Usuario usuario = dao.buscarPorEmail(email.trim());

            if (usuario != null) {
                // 2. Gera um token seguro aleatório
                String token = UUID.randomUUID().toString();
                
                // 💡 Nota para simulação local:
                // Se o seu RedefinirSenhaController precisa do e-mail para atualizar o banco via DAO, 
                // você pode guardar temporariamente na sessão do HttpSession associado a esse token, 
                // ou simplificar passando o e-mail no token se preferir. 
                // Para eliminar o aviso de variável não utilizada e usar o token de verdade:
                request.getSession().setAttribute("token_" + token, email.trim());

                // 3. Simulação do envio de e-mail utilizando o token gerado
                String linkRedefinicao = request.getRequestURL().toString().replace("esqueci-senha", "redefinir-senha") + "?token=" + token;
                
                // Mensagem formatada integrada com o design da Agnello
                String mensagemHtml = "Link de recuperação gerado com sucesso! <br><a href='" + linkRedefinicao + "'>Clique aqui para redefinir a senha</a>";
                
                request.setAttribute("mensagemSucesso", mensagemHtml);
            } else {
                request.setAttribute("mensagemErro", "E-mail não encontrado em nossa base de dados.");
            }

        } catch (Exception e) {
            request.setAttribute("mensagemErro", "Erro ao processar a solicitação: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
    }
}