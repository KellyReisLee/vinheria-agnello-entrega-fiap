package com.agnello.service;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.ClientePF;
import com.agnello.model.ClientePJ;
import com.agnello.model.Usuario;

public class ClienteService {

    private ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Realiza o cadastro de Cliente PF ou PJ aplicando regras de negócio se necessário.
     */
    public void cadastrarCliente(String tipoCliente, String email, String senha, String telefone, 
                                 String nome, String sobrenome, String cpf, 
                                 String razaoSocial, String cnpj) throws Exception {
        
        if (tipoCliente == null || tipoCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo de cliente não foi informado.");
        }

        if ("PF".equalsIgnoreCase(tipoCliente)) {
            ClientePF pf = new ClientePF();
            pf.setNome(nome);
            pf.setSobrenome(sobrenome);
            pf.setCpf(cpf);
            pf.setEmail(email);
            pf.setTelefone(telefone);
            pf.setSenha(senha); // Dica futura: aqui você poderia aplicar criptografia de senha (ex: BCrypt)
            
            clienteDAO.cadastrarPF(pf);
            
        } else if ("PJ".equalsIgnoreCase(tipoCliente)) {
            ClientePJ pj = new ClientePJ();
            pj.setRazaoSocial(razaoSocial);
            pj.setCnpj(cnpj);
            pj.setEmail(email);
            pj.setTelefone(telefone);
            pj.setSenha(senha);
            
            clienteDAO.cadastrarPJ(pj);
        } else {
            throw new IllegalArgumentException("Tipo de cliente inválido.");
        }
    }

    /**
     * Valida as credenciais de login do usuário.
     */
    public Usuario autenticar(String email, String senhaInformada) throws Exception {
        Usuario usuarioBanco = clienteDAO.buscarPorEmail(email);
        
        if (usuarioBanco != null && usuarioBanco.getSenha() != null) {
            // Correção aqui: .trim() correto (sem o 's' no final)
            if (usuarioBanco.getSenha().trim().equals(senhaInformada.trim())) {
                return usuarioBanco; 
            }
        }
        return null; 
    }
    
    /**
     * Valida se o e-mail existe e gera um link/token de recuperação de senha.
     */
    public String solicitarRecuperacaoSenha(String email, String baseUrl) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Por favor, informe um e-mail válido.");
        }

        Usuario usuario = clienteDAO.buscarPorEmail(email.trim());
        if (usuario == null) {
            throw new Exception("E-mail não encontrado em nossa base de dados.");
        }

        // Gera um token seguro aleatório
        String token = java.util.UUID.randomUUID().toString();
        
        // Monta o link de redefinição
        String linkRedefinicao = baseUrl + "?token=" + token;
        
        return linkRedefinicao;
    }

    /**
     * Realiza a redefinição da senha após validar as regras de negócio.
     */
    public void redefinirSenha(String emailDoUsuario, String novaSenha, String confirmaSenha) throws Exception {
        if (emailDoUsuario == null || emailDoUsuario.trim().isEmpty()) {
            throw new Exception("O link de recuperação expirou ou é inválido. Solicite um novo.");
        }

        if (novaSenha == null || novaSenha.trim().isEmpty() || confirmaSenha == null || confirmaSenha.trim().isEmpty()) {
            throw new IllegalArgumentException("Por favor, preencha todos os campos de senha.");
        }

        if (!novaSenha.equals(confirmaSenha)) {
            throw new IllegalArgumentException("As senhas digitadas não coincidem. Tente novamente.");
        }

        if (novaSenha.length() < 6) {
            throw new IllegalArgumentException("A nova senha deve conter pelo menos 6 caracteres.");
        }

        // Executa a atualização no banco de dados via DAO
        clienteDAO.atualizarSenhaPorEmail(emailDoUsuario, novaSenha);
    }
    
    /**
     * 4. Lógica de negócio para buscar um usuário pelo e-mail
     */
    public Usuario buscarPorEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return clienteDAO.buscarPorEmail(email.trim());
    }
    
    /**
     * Valida o e-mail de confirmação e executa a exclusão da conta do utilizador.
     */
    public void deletarConta(String emailSessao, String emailConfirmacao) throws Exception {
        if (emailSessao == null || emailSessao.trim().isEmpty()) {
            throw new Exception("Sessão inválida. Por favor, faça login novamente.");
        }

        if (emailConfirmacao == null || emailConfirmacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Por favor, digite o seu e-mail para confirmar a exclusão.");
        }

        // Garante que o e-mail digitado confere com o utilizador logado
        if (!emailSessao.trim().equalsIgnoreCase(emailConfirmacao.trim())) {
            throw new IllegalArgumentException("O e-mail digitado não corresponde à sua conta ativa.");
        }

        // Executa a exclusão no banco de dados através do DAO
        boolean deletado = clienteDAO.deletarPorEmail(emailSessao.trim());
        
        if (!deletado) {
            throw new Exception("Não foi possível encontrar o registo para exclusão na base de dados.");
        }
    }
    
    
}