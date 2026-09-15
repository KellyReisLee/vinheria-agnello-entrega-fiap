package com.agnello.controller;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.ClientePF;
import com.agnello.model.ClientePJ;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CadastroController", urlPatterns = {"/cadastro"})
public class CadastroController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(request, response);
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String tipoCliente = request.getParameter("tipo_cliente"); 
        
        System.out.println("DEBUG - Tipo de Cliente recebido: " + tipoCliente);

        if (tipoCliente == null || tipoCliente.trim().isEmpty()) {
            request.setAttribute("erro", "Erro: O tipo de cliente não foi informado pelo formulário.");
            request.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String telefone = request.getParameter("telefone");

        try {
            ClienteDAO dao = new ClienteDAO();

            if ("PF".equalsIgnoreCase(tipoCliente)) {
                ClientePF pf = new ClientePF();
                pf.setNome(request.getParameter("nome"));
                pf.setSobrenome(request.getParameter("sobrenome"));
                pf.setCpf(request.getParameter("cpf"));
                pf.setEmail(email);
                pf.setTelefone(telefone);
                pf.setSenha(senha);
                
                dao.cadastrarPF(pf);
            } else if ("PJ".equalsIgnoreCase(tipoCliente)) {
                ClientePJ pj = new ClientePJ();
                pj.setRazaoSocial(request.getParameter("razao_social"));
                pj.setCnpj(request.getParameter("cnpj"));
                pj.setEmail(email);
                pj.setTelefone(telefone);
                pj.setSenha(senha);
                
                dao.cadastrarPJ(pj);
            }

            // Define a mensagem de sucesso e retorna para o cadastro.jsp exibir a mensagem e aguardar o timer
            request.setAttribute("sucesso", "Cadastro realizado com sucesso! Redirecionando para o login em instantes...");
            request.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace(); 
            
            String mensagemErro = e.getMessage().toLowerCase();
            
            if (mensagemErro.contains("violates unique constraint") || mensagemErro.contains("duplicate key")) {
                request.setAttribute("erro", "Este CPF, CNPJ ou E-mail já está cadastrado em nossa base.");
            } else {
                request.setAttribute("erro", "Erro ao processar o cadastro: " + e.getMessage());
            }
            
            request.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(request, response);
        }
    }
}