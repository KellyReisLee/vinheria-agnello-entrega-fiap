package com.agnello.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ClienteDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.ClientePF;
import com.agnello.model.ClientePJ;
import com.agnello.model.Usuario;
import com.agnello.seguranca.HashSenha;
import com.agnello.seguranca.TokenSeguro;
import com.agnello.util.EmailTemplateUtil;
import com.agnello.util.Validacao;

/**
 * Regras de negócio das contas de cliente: cadastro, ativação, login,
 * recuperação de senha e exclusão.
 *
 * Os Controllers só tratam de HTTP (ler parâmetros, escolher a página); tudo o
 * que envolve segurança e validação está aqui, num único lugar.
 */
public class ClienteService {

    private static final Logger LOGGER = Logger.getLogger(ClienteService.class.getName());

    /** Validade do link de ativação enviado por e-mail. */
    public static final int VALIDADE_ATIVACAO_HORAS = 48;
    /** Validade do link de recuperação de senha (o texto do e-mail fala em 10 minutos). */
    public static final int VALIDADE_RECUPERACAO_MINUTOS = 10;
    /** Tentativas de senha erradas seguidas antes do bloqueio temporário. */
    public static final int MAX_TENTATIVAS_LOGIN = 5;
    public static final int MINUTOS_BLOQUEIO = 15;

    private static final String MSG_LOGIN_INVALIDO = "E-mail ou senha inválidos.";

    private final ClienteDAO clienteDAO;
    private final EmailService emailService;

    public ClienteService() {
        this(new ClienteDAO(), new EmailService());
    }

    /** Construtor para testes (permite usar versões falsas do DAO e do e-mail). */
    public ClienteService(ClienteDAO clienteDAO, EmailService emailService) {
        this.clienteDAO = clienteDAO;
        this.emailService = emailService;
    }

    // ===================================================================== cadastro

    /**
     * Valida os dados, guarda a conta (senha em hash, CPF e telefone cifrados) e
     * envia o e-mail de ativação. Devolve o cliente criado (sem dados de autenticação).
     */
    public Usuario cadastrarCliente(String tipoCliente, String email, String senha, String telefone,
            String nome, String sobrenome, String cpf, String razaoSocial, String cnpj, String urlBase)
            throws RegraNegocioException {

        String emailTratado = Validacao.normalizarEmail(email);
        if (!Validacao.emailValido(emailTratado)) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        String erroSenha = Validacao.validarSenha(senha);
        if (erroSenha != null) {
            throw new RegraNegocioException(erroSenha);
        }
        String telefoneDigitos = Validacao.somenteDigitos(telefone);
        if (!Validacao.vazio(telefoneDigitos) && !Validacao.telefoneValido(telefoneDigitos)) {
            throw new RegraNegocioException("Informe um telefone com DDD (10 ou 11 dígitos).");
        }

        String token = TokenSeguro.gerar();
        String senhaHash = HashSenha.gerar(senha);
        Usuario criado;

        if ("PF".equalsIgnoreCase(tipoCliente)) {
            if (Validacao.vazio(nome) || Validacao.vazio(sobrenome)) {
                throw new RegraNegocioException("Informe o nome e o sobrenome.");
            }
            if (!Validacao.cpfValido(cpf)) {
                throw new RegraNegocioException("Informe um CPF válido.");
            }
            ClientePF pf = new ClientePF();
            pf.setNome(nome.trim());
            pf.setSobrenome(sobrenome.trim());
            pf.setCpf(Validacao.somenteDigitos(cpf));
            pf.setEmail(emailTratado);
            pf.setTelefone(Validacao.vazio(telefoneDigitos) ? null : telefoneDigitos);
            pf.setId(clienteDAO.inserirPF(pf, senhaHash, TokenSeguro.hash(token), VALIDADE_ATIVACAO_HORAS));
            criado = pf;

        } else if ("PJ".equalsIgnoreCase(tipoCliente)) {
            if (Validacao.vazio(razaoSocial)) {
                throw new RegraNegocioException("Informe a razão social.");
            }
            if (!Validacao.cnpjValido(cnpj)) {
                throw new RegraNegocioException("Informe um CNPJ válido.");
            }
            ClientePJ pj = new ClientePJ();
            pj.setRazaoSocial(razaoSocial.trim());
            pj.setCnpj(Validacao.somenteDigitos(cnpj));
            pj.setEmail(emailTratado);
            pj.setTelefone(Validacao.vazio(telefoneDigitos) ? null : telefoneDigitos);
            pj.setId(clienteDAO.inserirPJ(pj, senhaHash, TokenSeguro.hash(token), VALIDADE_ATIVACAO_HORAS));
            criado = pj;

        } else {
            throw new RegraNegocioException("Escolha o tipo de conta: pessoa física ou jurídica.");
        }

        String link = urlBase + "/ativar?token=" + token;
        emailService.enviarEmailAssincrono(emailTratado, "Confirme o seu cadastro - Vinheria Agnello",
                EmailTemplateUtil.getCorpoAtivacaoConta(link));
        return criado;
    }

    public boolean ativarConta(String token) {
        if (Validacao.vazio(token)) {
            return false;
        }
        return clienteDAO.ativarPorToken(TokenSeguro.hash(token));
    }

    // ===================================================================== login

    /**
     * Confere e-mail e senha. Devolve o usuário (pronto para a sessão) ou lança
     * RegraNegocioException com a mensagem a mostrar.
     *
     * Cuidados de segurança:
     * - a mensagem é a mesma para "e-mail não existe" e "senha errada";
     * - se o e-mail não existe, ainda assim gastamos o tempo de um hash
     *   (o tempo de resposta não revela quais e-mails estão cadastrados);
     * - 5 erros seguidos bloqueiam a conta por 15 minutos;
     * - hashes antigos/fracos são refeitos automaticamente no login.
     */
    public Usuario autenticar(String email, String senha) throws RegraNegocioException {
        String emailTratado = Validacao.normalizarEmail(email);
        if (Validacao.vazio(emailTratado) || senha == null || senha.isEmpty() || senha.length() > Validacao.SENHA_MAX) {
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        Usuario usuario = clienteDAO.buscarPorEmail(emailTratado);
        if (usuario == null) {
            HashSenha.verificarSemUsuario(senha);
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        OffsetDateTime bloqueadoAte = usuario.getBloqueadoAte();
        if (bloqueadoAte != null && bloqueadoAte.isAfter(OffsetDateTime.now())) {
            throw new RegraNegocioException("Muitas tentativas sem sucesso. Por segurança, tente novamente depois das "
                    + bloqueadoAte.atZoneSameInstant(java.time.ZoneId.of("America/Sao_Paulo"))
                            .format(DateTimeFormatter.ofPattern("HH:mm"))
                    + " ou redefina a sua senha.");
        }

        if (!HashSenha.verificar(senha, usuario.getSenhaHash())) {
            clienteDAO.registrarFalhaLogin(usuario.getId(), MAX_TENTATIVAS_LOGIN, MINUTOS_BLOQUEIO);
            LOGGER.log(Level.WARNING, "Senha incorreta para o cliente id={0}", usuario.getId());
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        if (!usuario.isEmailVerificado()) {
            throw new RegraNegocioException("A sua conta ainda não foi ativada. Verifique o seu e-mail e clique "
                    + "no link de confirmação.");
        }

        String novoHash = HashSenha.precisaAtualizar(usuario.getSenhaHash()) ? HashSenha.gerar(senha) : null;
        clienteDAO.registrarLoginComSucesso(usuario.getId(), novoHash);

        usuario.limparDadosDeAutenticacao();
        return usuario;
    }

    // ===================================================================== recuperação de senha

    /**
     * Gera o link de recuperação e envia por e-mail. Não diz se o e-mail existe
     * (o Controller mostra sempre a mesma mensagem), para não revelar quem é cliente.
     */
    public void solicitarRecuperacaoSenha(String email, String urlBase) throws RegraNegocioException {
        String emailTratado = Validacao.normalizarEmail(email);
        if (!Validacao.emailValido(emailTratado)) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }

        Usuario usuario = clienteDAO.buscarPorEmail(emailTratado);
        if (usuario == null) {
            return;
        }

        String token = TokenSeguro.gerar();
        clienteDAO.salvarTokenRecuperacao(usuario.getId(), TokenSeguro.hash(token), VALIDADE_RECUPERACAO_MINUTOS);

        String link = urlBase + "/redefinir-senha?token=" + token;
        emailService.enviarEmailAssincrono(emailTratado, "Recuperação de senha - Vinheria Agnello",
                EmailTemplateUtil.getCorpoRecuperacaoSenha(link));
    }

    public boolean validarTokenRecuperacao(String token) {
        return !Validacao.vazio(token) && clienteDAO.tokenRecuperacaoValido(TokenSeguro.hash(token));
    }

    public void redefinirSenhaComToken(String token, String novaSenha, String confirmaSenha)
            throws RegraNegocioException {
        if (Validacao.vazio(novaSenha) || Validacao.vazio(confirmaSenha)) {
            throw new RegraNegocioException("Preencha os dois campos de senha.");
        }
        if (!novaSenha.equals(confirmaSenha)) {
            throw new RegraNegocioException("As senhas digitadas não coincidem.");
        }
        String erroSenha = Validacao.validarSenha(novaSenha);
        if (erroSenha != null) {
            throw new RegraNegocioException(erroSenha);
        }
        if (Validacao.vazio(token)
                || !clienteDAO.redefinirSenhaPorToken(TokenSeguro.hash(token), HashSenha.gerar(novaSenha))) {
            throw new TokenInvalidoException();
        }
    }

    /** Link de recuperação expirado ou já usado: o Controller manda pedir um novo. */
    public static class TokenInvalidoException extends RegraNegocioException {
        private static final long serialVersionUID = 1L;

        public TokenInvalidoException() {
            super("Este link de recuperação expirou ou já foi usado. Peça um novo.");
        }
    }

    // ===================================================================== consultas

    public boolean emailCadastrado(String email) {
        String emailTratado = Validacao.normalizarEmail(email);
        return Validacao.emailValido(emailTratado) && clienteDAO.existeEmail(emailTratado);
    }

    /** Usuário pronto para a sessão (sem hash de senha). */
    public Usuario buscarParaSessao(int id) {
        Usuario usuario = clienteDAO.buscarPorId(id);
        if (usuario != null) {
            usuario.limparDadosDeAutenticacao();
        }
        return usuario;
    }

    // ===================================================================== exclusão

    /** Exclui a conta do usuário da sessão, depois de ele confirmar o próprio e-mail. */
    public void deletarConta(Usuario usuarioSessao, String emailConfirmacao) throws RegraNegocioException {
        if (usuarioSessao == null) {
            throw new RegraNegocioException("Sessão expirada. Entre novamente.");
        }
        if (Validacao.vazio(emailConfirmacao)) {
            throw new RegraNegocioException("Digite o seu e-mail para confirmar a exclusão.");
        }
        if (!usuarioSessao.getEmail().equalsIgnoreCase(emailConfirmacao.trim())) {
            throw new RegraNegocioException("O e-mail digitado não corresponde à sua conta.");
        }
        if (!clienteDAO.deletarPorId(usuarioSessao.getId())) {
            throw new RegraNegocioException("Esta conta já não existe.");
        }
    }
}
