package com.agnello.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ClienteDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.ClientePF;
import com.agnello.model.Usuario;
import com.agnello.seguranca.HashSenha;
import com.agnello.util.EmailTemplateUtil;
import com.agnello.util.Validacao;

/**
 * Alterações que o próprio cliente faz na área pessoal: dados e senha.
 *
 * E-mail, CPF e CNPJ não mudam por aqui: são a identidade da conta e a base
 * da nota fiscal. Trocar o e-mail exigiria confirmar o endereço novo.
 */
public class ContaService {

    private static final Logger LOGGER = Logger.getLogger(ContaService.class.getName());

    private final ClienteDAO clienteDAO;
    private final EmailService emailService;

    public ContaService() {
        this(new ClienteDAO(), new EmailService());
    }

    /** Construtor para testes. */
    public ContaService(ClienteDAO clienteDAO, EmailService emailService) {
        this.clienteDAO = clienteDAO;
        this.emailService = emailService;
    }

    /**
     * Atualiza os dados e devolve o cliente atualizado (para pôr de novo na sessão).
     * Para PF usa nome e sobrenome; para PJ, a razão social.
     */
    public Usuario atualizarDados(Usuario cliente, String nome, String sobrenome, String razaoSocial,
            String telefone) throws RegraNegocioException {
        String tel = Validacao.somenteDigitos(telefone);
        if (!Validacao.vazio(tel) && !Validacao.telefoneValido(tel)) {
            throw new RegraNegocioException("Informe um telefone com DDD (10 ou 11 dígitos).");
        }
        String telefoneFinal = Validacao.vazio(tel) ? null : tel;

        if (cliente instanceof ClientePF) {
            String n = texto(nome, 100, "o nome");
            String s = texto(sobrenome, 100, "o sobrenome");
            clienteDAO.atualizarDadosPF(cliente.getId(), n, s, telefoneFinal);
        } else {
            String r = texto(razaoSocial, 150, "a razão social");
            clienteDAO.atualizarDadosPJ(cliente.getId(), r, telefoneFinal);
        }
        LOGGER.log(Level.INFO, "Cliente id={0} atualizou os dados", cliente.getId());

        Usuario atualizado = clienteDAO.buscarPorId(cliente.getId());
        atualizado.limparDadosDeAutenticacao();
        return atualizado;
    }

    /**
     * Troca a senha. Exige a senha atual: se alguém usar um computador com a
     * sessão aberta, não consegue tomar a conta trocando a senha.
     */
    public void trocarSenha(Usuario cliente, String atual, String nova, String confirmacao)
            throws RegraNegocioException {
        if (Validacao.vazio(atual) || Validacao.vazio(nova) || Validacao.vazio(confirmacao)) {
            throw new RegraNegocioException("Preencha os três campos de senha.");
        }
        if (!nova.equals(confirmacao)) {
            throw new RegraNegocioException("A nova senha e a confirmação não coincidem.");
        }
        String erro = Validacao.validarSenha(nova);
        if (erro != null) {
            throw new RegraNegocioException(erro);
        }
        Usuario doBanco = clienteDAO.buscarPorId(cliente.getId());
        if (doBanco == null || !HashSenha.verificar(atual, doBanco.getSenhaHash())) {
            throw new RegraNegocioException("A senha atual está incorreta.");
        }
        if (HashSenha.verificar(nova, doBanco.getSenhaHash())) {
            throw new RegraNegocioException("A nova senha tem de ser diferente da atual.");
        }

        clienteDAO.atualizarSenha(cliente.getId(), HashSenha.gerar(nova));
        LOGGER.log(Level.INFO, "Cliente id={0} trocou a senha", cliente.getId());

        // Aviso por e-mail: se não foi a própria pessoa, ela fica sabendo na hora
        try {
            emailService.enviarEmailAssincrono(cliente.getEmail(), "A sua senha foi alterada - Vinheria Agnello",
                    EmailTemplateUtil.getCorpoSenhaAlterada(cliente.getNomeExibicao()));
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Falha ao enviar o aviso de senha alterada", e);
        }
    }

    private static String texto(String valor, int maximo, String campo) throws RegraNegocioException {
        String v = valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) {
            throw new RegraNegocioException("Informe " + campo + ".");
        }
        if (v.length() > maximo) {
            throw new RegraNegocioException("Revise " + campo + " (máximo de " + maximo + " caracteres).");
        }
        return v;
    }
}
