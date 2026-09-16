package com.agnello.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.Usuario;

@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 1. O doGet renderiza a página do checkout quando o cliente entra na URL /checkout
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    // 2. O doPost lida com as requisições AJAX do JavaScript (Verificação de E-mail e Validação de Senha)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String senhaInformada = request.getParameter("senha");
        String email = request.getParameter("email");

        // LOGS DE DEPURAÇÃO (Olhe o console do Eclipse/IntelliJ quando clicar no botão)
        System.out.println("--- REQUISIÇÃO POST RECEBIDA ---");
        System.out.println("Email recebido: " + email);
        System.out.println("Senha recebida: " + senhaInformada);

        // CASO A: Requisição para validar a senha do cliente antigo
        if (senhaInformada != null) {
            Usuario usuarioSessao = (Usuario) request.getSession().getAttribute("usuarioLogado");
            boolean senhaValida = false;
            
            if (usuarioSessao != null) {
                System.out.println("Usuário encontrado na sessão: " + usuarioSessao.getEmail());
                if (usuarioSessao.getSenha() != null && usuarioSessao.getSenha().equals(senhaInformada)) {
                    senhaValida = true;
                } else {
                    System.out.println("Aviso: A senha informada não confere com a do objeto.");
                }
            } else {
                System.out.println("Erro: Nenhum usuário encontrado na sessão!");
            }

            response.getWriter().write("{\"sucesso\": " + senhaValida + "}");
            return;
        }

        // CASO B: Requisição padrão para verificar o e-mail
        boolean existe = false;
        if (email != null && !email.trim().isEmpty()) {
            try {
                ClienteDAO dao = new ClienteDAO();
                Usuario usuario = dao.buscarPorEmail(email.trim());
                
                if (usuario != null) {
                    existe = true;
                    request.getSession().getAttribute("usuarioLogado"); // Atenção aqui, o correto é setAttribute:
                    request.getSession().setAttribute("usuarioLogado", usuario);
                    System.out.println("Usuário salvo na sessão com sucesso.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.getWriter().write("{\"existe\": " + existe + "}");
    }
}