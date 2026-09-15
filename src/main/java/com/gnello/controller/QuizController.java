package com.gnello.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/quiz")
public class QuizController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String VIEW_PATH = "/WEB-INF/views/quiz.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Define atributos iniciais se necessário (ex: passo padrão)
        request.setAttribute("passoAtual", 1);
        
        // Encaminha para a view protegida em WEB-INF
        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }
}

