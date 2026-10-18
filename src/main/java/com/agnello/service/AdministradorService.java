package com.agnello.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.AdministradorDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Administrador;
import com.agnello.seguranca.HashSenha;
import com.agnello.util.Validacao;

/**
 * Login e criação de contas de administrador.
 *
 * Mesmas proteções do login de clientes (mensagem única para e-mail/senha
 * errados, tempo de resposta igual, bloqueio após 5 erros), mais uma regra:
 * a senha de administrador tem de ter pelo menos 12 caracteres, porque esta
 * conta pode alterar pedidos e produtos.
 */
public class AdministradorService {

    private static final Logger LOGGER = Logger.getLogger(AdministradorService.class.getName());

    public static final int SENHA_MIN_ADMIN = 12;
    public static final int MAX_TENTATIVAS = 5;
    public static final int MINUTOS_BLOQUEIO = 15;
    private static final String MSG_LOGIN_INVALIDO = "E-mail ou senha inválidos.";

    private final AdministradorDAO dao;

    public AdministradorService() {
        this(new AdministradorDAO());
    }

    /** Construtor para testes. */
    public AdministradorService(AdministradorDAO dao) {
        this.dao = dao;
    }

    public Administrador autenticar(String email, String senha) throws RegraNegocioException {
        String emailTratado = Validacao.normalizarEmail(email);
        if (Validacao.vazio(emailTratado) || senha == null || senha.isEmpty() || senha.length() > Validacao.SENHA_MAX) {
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        Administrador admin = dao.buscarPorEmail(emailTratado);
        if (admin == null || !admin.isAtivo()) {
            HashSenha.verificarSemUsuario(senha); // mesmo tempo de resposta
            LOGGER.warning("Tentativa de login no painel com e-mail desconhecido ou inativo.");
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        OffsetDateTime bloqueadoAte = admin.getBloqueadoAte();
        if (bloqueadoAte != null && bloqueadoAte.isAfter(OffsetDateTime.now())) {
            throw new RegraNegocioException("Muitas tentativas sem sucesso. Tente novamente depois das "
                    + bloqueadoAte.atZoneSameInstant(ZoneId.of("America/Sao_Paulo"))
                            .format(DateTimeFormatter.ofPattern("HH:mm")) + ".");
        }

        if (!HashSenha.verificar(senha, admin.getSenhaHash())) {
            dao.registrarFalhaLogin(admin.getId(), MAX_TENTATIVAS, MINUTOS_BLOQUEIO);
            LOGGER.log(Level.WARNING, "Senha incorreta no painel para o administrador id={0}", admin.getId());
            throw new RegraNegocioException(MSG_LOGIN_INVALIDO);
        }

        String novoHash = HashSenha.precisaAtualizar(admin.getSenhaHash()) ? HashSenha.gerar(senha) : null;
        dao.registrarLoginComSucesso(admin.getId(), novoHash);
        LOGGER.log(Level.INFO, "Administrador id={0} entrou no painel", admin.getId());

        admin.limparDadosDeAutenticacao();
        return admin;
    }

    /**
     * Cria a conta (ou, se o e-mail já existir, troca a senha e reativa).
     * Devolve true se criou, false se atualizou.
     */
    public boolean criarOuRedefinir(String nome, String email, String senha) throws RegraNegocioException {
        String emailTratado = Validacao.normalizarEmail(email);
        if (Validacao.vazio(nome)) {
            throw new RegraNegocioException("Informe o nome.");
        }
        if (!Validacao.emailValido(emailTratado)) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        String erro = validarSenhaAdmin(senha);
        if (erro != null) {
            throw new RegraNegocioException(erro);
        }

        Administrador existente = dao.buscarPorEmail(emailTratado);
        if (existente != null) {
            dao.atualizarSenha(existente.getId(), HashSenha.gerar(senha));
            return false;
        }
        dao.inserir(nome.trim(), emailTratado, HashSenha.gerar(senha));
        return true;
    }

    public static String validarSenhaAdmin(String senha) {
        if (senha == null || senha.length() < SENHA_MIN_ADMIN) {
            return "A senha de administrador deve ter pelo menos " + SENHA_MIN_ADMIN + " caracteres.";
        }
        if (senha.length() > Validacao.SENHA_MAX) {
            return "A senha pode ter no máximo " + Validacao.SENHA_MAX + " caracteres.";
        }
        return null;
    }
}
