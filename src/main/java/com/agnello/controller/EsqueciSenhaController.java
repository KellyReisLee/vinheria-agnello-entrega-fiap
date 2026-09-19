package com.agnello.controller;

import com.agnello.service.ClienteService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/esqueci-senha")
public class EsqueciSenhaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");

        try {
            // Descobre a URL base para montar o link dinamicamente
            String baseUrl = request.getRequestURL().toString().replace("esqueci-senha", "redefinir-senha");
            
            // O service valida o e-mail e retorna o link formatado
            String linkRedefinicao = clienteService.solicitarRecuperacaoSenha(email, baseUrl);

            // Extrai o token gerado para salvar na sessão (mantido na camada web por questões de sessão HTTP)
            String token = linkRedefinicao.substring(linkRedefinicao.indexOf("?token=") + 7);
            request.getSession().setAttribute("token_" + token, email.trim());

            String mensagemHtml = "Link de recuperação gerado com sucesso! <br><a href='" + linkRedefinicao + "'>Clique aqui para redefinir a senha</a>";
            request.setAttribute("mensagemSucesso", mensagemHtml);

        } catch (IllegalArgumentException e) {
            request.setAttribute("mensagemErro", e.getMessage());
        } catch (Exception e) {
            request.setAttribute("mensagemErro", e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/esqueci-senha.jsp").forward(request, response);
    }
}