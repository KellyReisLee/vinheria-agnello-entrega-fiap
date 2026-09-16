package com.agnello.controller;

import java.io.IOException;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Pega o e-mail enviado via AJAX pelo JavaScript
        String email = request.getParameter("email");
        
        boolean existe = false;
        
        if (email != null && !email.trim().isEmpty()) {
            try {
                // Instancia o DAO e utiliza o método que você já tem implementado
                ClienteDAO dao = new ClienteDAO();
                Usuario usuario = dao.buscarPorEmail(email.trim());
                
                // Se o usuário não for nulo, significa que o e-mail já está cadastrado no Supabase
                if (usuario != null) {
                    existe = true;
                    
                    // Opcional: Guarda o usuário na Sessão para adiantar os dados no checkout
                    request.getSession().setAttribute("usuarioLogado", usuario);
                }
            } catch (Exception e) {
                // Loga o erro caso ocorra algum problema de conexão com o Supabase
                e.printStackTrace();
            }
        }

        // Configura a resposta como JSON puro para o JavaScript do checkout ler
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Retorna o JSON indicando o resultado real do banco de dados
        response.getWriter().write("{\"existe\": " + existe + "}");
    }
}