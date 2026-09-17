package com.agnello.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.ClientePF;
import com.agnello.model.ClientePJ;
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

    // 2. O doPost lida com as requisições AJAX do JavaScript (Verificação de E-mail, Validação de Senha e Novo Cadastro)
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
        // CASO 1: Requisição para validar a senha do cliente antigo (Login na etapa)
        // =========================================================================
        if (senhaInformada != null && !senhaInformada.trim().isEmpty() && (request.getParameter("nome") == null && request.getParameter("razao_social") == null)) {
            boolean senhaValida = false;
            
            try {
                ClienteDAO dao = new ClienteDAO();
                Usuario usuarioBanco = dao.buscarPorEmail(email);
                
                if (usuarioBanco != null) {
                    if (usuarioBanco.getSenha() != null && usuarioBanco.getSenha().trim().equals(senhaInformada.trim())) {
                        senhaValida = true;
                        
                        HttpSession session = request.getSession();
                        session.setAttribute("clienteLogado", usuarioBanco);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.getWriter().write("{\"sucesso\": " + senhaValida + "}");
            return;
        }

        // =========================================================================
        // CASO 2: Requisição para cadastrar novo cliente (Só executa se houver dados de cadastro completos)
        // =========================================================================
        boolean temDadosCadastro = (request.getParameter("nome") != null && !request.getParameter("nome").trim().isEmpty()) || 
                                   (request.getParameter("razao_social") != null && !request.getParameter("razao_social").trim().isEmpty());

        if (tipoCliente != null && !tipoCliente.trim().isEmpty() && temDadosCadastro) {
            boolean sucesso = false;
            String mensagemErro = "";

            try {
                ClienteDAO dao = new ClienteDAO();
                String telefone = request.getParameter("telefone");

                if ("PF".equalsIgnoreCase(tipoCliente)) {
                    ClientePF pf = new ClientePF();
                    pf.setNome(request.getParameter("nome"));
                    pf.setSobrenome(request.getParameter("sobrenome"));
                    pf.setCpf(request.getParameter("cpf"));
                    pf.setEmail(email);
                    pf.setTelefone(telefone);
                    pf.setSenha(senhaInformada);
                    
                    dao.cadastrarPF(pf);
                    sucesso = true;

                } else if ("PJ".equalsIgnoreCase(tipoCliente)) {
                    ClientePJ pj = new ClientePJ();
                    pj.setRazaoSocial(request.getParameter("razao_social"));
                    pj.setCnpj(request.getParameter("cnpj"));
                    pj.setEmail(email);
                    pj.setTelefone(telefone);
                    pj.setSenha(senhaInformada);
                    
                    dao.cadastrarPJ(pj);
                    sucesso = true;
                }

                // Após o cadastro, busca o usuário e o salva na sessão como "clienteLogado" (Login Automático)
                if (sucesso) {
                    Usuario usuarioCriado = dao.buscarPorEmail(email);
                    if (usuarioCriado != null) {
                        HttpSession session = request.getSession();
                        session.setAttribute("clienteLogado", usuarioCriado);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                sucesso = false;
                mensagemErro = e.getMessage().toLowerCase();
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
        // CASO 3: Apenas verificação inicial de e-mail (Não salva nada no banco!)
        // =========================================================================
        boolean existe = false;
        if (email != null && !email.trim().isEmpty()) {
            try {
                ClienteDAO dao = new ClienteDAO();
                Usuario usuario = dao.buscarPorEmail(email.trim());
                
                if (usuario != null) {
                    existe = true;
                    HttpSession session = request.getSession();
                    session.setAttribute("clienteLogado", usuario); 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.getWriter().write("{\"existe\": " + existe + "}");
    }
}