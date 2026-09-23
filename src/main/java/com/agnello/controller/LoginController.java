package com.agnello.controller;

import com.agnello.model.Usuario;
import com.agnello.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ClienteService clienteService = new ClienteService();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Apenas exibe a página de login normalmente
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try {
            // Delega a autenticação para a camada de serviço
            Usuario usuario = clienteService.autenticar(email, senha);

            if (usuario != null) {
                HttpSession session = request.getSession();
                session.setAttribute("clienteLogado", usuario);
                response.sendRedirect(request.getContextPath() + "/area-pessoal?id=" + usuario.getId());
            } else {
                request.setAttribute("erro", "E-mail ou senha inválidos.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Apanha a mensagem específica lançada pelo Service (ex: conta não ativada)
            // ou exibe "E-mail ou senha inválidos" se for outro problema
            String mensagemErro = e.getMessage();
            
            if (mensagemErro == null || mensagemErro.trim().isEmpty()) {
                mensagemErro = "E-mail ou senha inválidos.";
            }
            
            request.setAttribute("erro", mensagemErro);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}