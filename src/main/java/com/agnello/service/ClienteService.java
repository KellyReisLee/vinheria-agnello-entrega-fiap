package com.agnello.service;

import com.agnello.dao.ClienteDAO;
import com.agnello.model.ClientePF;
import com.agnello.model.ClientePJ;
import com.agnello.model.Usuario;

public class ClienteService {

    private ClienteDAO clienteDAO;
    private EmailService emailService;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
        this.emailService = new EmailService();
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
            pf.setSenha(senha);
            
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
            if (usuarioBanco.getSenha().trim().equals(senhaInformada.trim())) {
                return usuarioBanco; 
            }
        }
        return null; 
    }
    
    /**
     * Valida o e-mail, gera um token seguro de recuperação, define uma expiração de 30 minutos,
     * persiste os dados e dispara o e-mail de forma assíncrona.
     */
    public String solicitarRecuperacaoSenha(String email, String baseUrl) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Por favor, informe um e-mail válido.");
        }

        String emailTratado = email.trim().toLowerCase();
        Usuario usuario = clienteDAO.buscarPorEmail(emailTratado);

        // SEGURANÇA: Se o e-mail não existir, retorna null (proteção contra User Enumeration)
        if (usuario == null) {
            return null;
        }

        String token = java.util.UUID.randomUUID().toString();
        java.time.LocalDateTime expiracao = java.time.LocalDateTime.now().plusMinutes(10);

        // Persiste o token e a expiração na base de dados
        clienteDAO.salvarTokenRecuperacao(emailTratado, token, expiracao);

        String baseLimpa = baseUrl != null ? baseUrl.replaceAll("/$", "") : "";
        String linkRedefinicao = baseLimpa + "?token=" + token;

        // DISPARO DO E-MAIL DE RECUPERAÇÃO
        String assunto = "Recuperação de Senha - Vinheria Agnello";
        String corpoHtml = com.agnello.util.EmailTemplateUtil.getCorpoRecuperacaoSenha(linkRedefinicao);

        emailService.enviarEmailAssincrono(emailTratado, assunto, corpoHtml);

        return linkRedefinicao;
    }
    
    /**
     * Valida se o token existe e ainda está dentro do prazo de 30 minutos.
     */
    public boolean validarTokenRecuperacao(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return clienteDAO.validarToken(token.trim());
    }

    /**
     * Realiza a redefinição da senha utilizando o token de forma segura e o invalida após o uso.
     */
    public void redefinirSenhaComToken(String token, String novaSenha, String confirmaSenha) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Sessão de recuperação inválida. Solicite um novo link.");
        }

        // Validação rigorosa de tempo/existência antes de prosseguir
        if (!validarTokenRecuperacao(token)) {
            throw new Exception("Este link de recuperação expirou (limite de 10 minutos) ou já foi utilizado. Por favor, solicite um novo.");
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

        // Atualiza a senha e limpa o token na base de dados (Garante o uso único)
        clienteDAO.atualizarSenhaEInvalidarToken(token.trim(), novaSenha);
    }
    /**
     * Busca um usuário pelo e-mail com validação prévia.
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

        if (!emailSessao.trim().equalsIgnoreCase(emailConfirmacao.trim())) {
            throw new IllegalArgumentException("O e-mail digitado não corresponde à sua conta ativa.");
        }

        boolean deletado = clienteDAO.deletarPorEmail(emailSessao.trim());
        
        if (!deletado) {
            throw new Exception("Não foi possível encontrar o registo para exclusão na base de dados.");
        }
    }
}