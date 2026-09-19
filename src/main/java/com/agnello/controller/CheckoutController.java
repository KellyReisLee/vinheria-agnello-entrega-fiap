package com.agnello.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.agnello.model.Usuario;
import com.agnello.service.ClienteService;

@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ClienteService clienteService = new ClienteService();

    // 1. O doGet renderiza a página do checkout
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("clienteLogado") : null;
        
        if (usuario != null) {
            request.setAttribute("usuarioLogado", usuario);
        }
        
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    // 2. O doPost lida com as requisições AJAX (Login por etapa, Cadastro e Verificação de E-mail)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String senhaInformada = request.getParameter("senha");
        String email = request.getParameter("email");
        String tipoCliente = request.getParameter("tipo_cliente"); 

        // =========================================================================
        // CASO 1: Validação de senha do cliente existente
        // =========================================================================
        if (senhaInformada != null && !senhaInformada.trim().isEmpty() && 
            (request.getParameter("nome") == null && request.getParameter("razao_social") == null)) {
            
            boolean senhaValida = false;
            
            try {
                Usuario usuarioBanco = clienteService.autenticar(email, senhaInformada);
                
                if (usuarioBanco != null) {
                    senhaValida = true;
                    HttpSession session = request.getSession();
                    session.setAttribute("clienteLogado", usuarioBanco);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.getWriter().write("{\"sucesso\": " + senhaValida + "}");
            return;
        }

        // =========================================================================
        // CASO 2: Cadastro de novo cliente (Utilizando o método unificado do Service)
        // =========================================================================
        boolean temDadosCadastro = (request.getParameter("nome") != null && !request.getParameter("nome").trim().isEmpty()) || 
                                   (request.getParameter("razao_social") != null && !request.getParameter("razao_social").trim().isEmpty());

        if (tipoCliente != null && !tipoCliente.trim().isEmpty() && temDadosCadastro) {
            boolean sucesso = false;
            String mensagemErro = "";

            try {
                String telefone = request.getParameter("telefone");
                String nome = request.getParameter("nome");
                String sobrenome = request.getParameter("sobrenome");
                String cpf = request.getParameter("cpf");
                String razaoSocial = request.getParameter("razao_social");
                String cnpj = request.getParameter("cnpj");

                // Chamada limpa utilizando o método unificado do ClienteService
                clienteService.cadastrarCliente(tipoCliente, email, senhaInformada, telefone, nome, sobrenome, cpf, razaoSocial, cnpj);
                sucesso = true;

                // Login automático pós-cadastro
                if (sucesso) {
                    Usuario usuarioCriado = clienteService.buscarPorEmail(email);
                    if (usuarioCriado != null) {
                        HttpSession session = request.getSession();
                        session.setAttribute("clienteLogado", usuarioCriado);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                sucesso = false;
                mensagemErro = e.getMessage() != null ? e.getMessage().toLowerCase() : "erro desconhecido";
                if (mensagemErro.contains("violates unique constraint") || mensagemErro.contains("duplicate key")) {
                    mensagemErro = "Este CPF, CNPJ ou E-mail já está cadastrado em nossa base.";
                } else {
                    mensagemErro = "Erro ao processar o cadastro: " + e.getMessage();
                }
            }

            if (sucesso) {
                response.getWriter().write("{\"sucesso\": true}");
            } else {
                response.getWriter().write("{\"sucesso\": false, \"mensagem\": \"" + mensagemErro + "\"}");
            }
            return;
        }

        // =========================================================================
        // CASO 3: Verificação inicial de e-mail
        // =========================================================================
        boolean existe = false;
        if (email != null && !email.trim().isEmpty()) {
            try {
                Usuario usuario = clienteService.buscarPorEmail(email);
                if (usuario != null) {
                    existe = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.getWriter().write("{\"existe\": " + existe + "}");
    }
}