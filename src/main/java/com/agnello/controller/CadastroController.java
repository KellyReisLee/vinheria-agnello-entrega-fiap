package com.agnello.controller;

import com.agnello.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CadastroController", urlPatterns = {"/cadastro"})
public class CadastroController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ClienteService clienteService = new ClienteService();

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

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String telefone = request.getParameter("telefone");
        
        // Dados específicos PF
        String nome = request.getParameter("nome");
        String sobrenome = request.getParameter("sobrenome");
        String cpf = request.getParameter("cpf");
        
        // Dados específicos PJ
        String razaoSocial = request.getParameter("razao_social");
        String cnpj = request.getParameter("cnpj");

        try {
            // Descobre a URL base dinamicamente para construir o link de ativação no e-mail
            String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "") + request.getContextPath();

            // Delega a regra de negócio e persistência para o Service, passando a baseUrl
            clienteService.cadastrarCliente(tipoCliente, email, senha, telefone, nome, sobrenome, cpf, razaoSocial, cnpj, baseUrl);

            request.setAttribute("sucesso", "Cadastro realizado com sucesso! Verifique o seu e-mail para ativar a conta.");
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