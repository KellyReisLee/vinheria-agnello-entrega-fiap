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

    // 2. O doPost lida com a requisição AJAX do JavaScript para verificar o e-mail
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        boolean existe = false;
        
        if (email != null && !email.trim().isEmpty()) {
            try {
                ClienteDAO dao = new ClienteDAO();
                Usuario usuario = dao.buscarPorEmail(email.trim());
                
                if (usuario != null) {
                    existe = true;
                    request.getSession().setAttribute("usuarioLogado", usuario);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"existe\": " + existe + "}");
    }
}