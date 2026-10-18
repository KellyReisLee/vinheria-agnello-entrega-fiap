package com.agnello.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ContatoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Administrador;
import com.agnello.model.MensagemContato;
import com.agnello.model.Usuario;
import com.agnello.util.EmailTemplateUtil;
import com.agnello.util.Validacao;

/**
 * Formulário de contato: valida, grava no banco (para nada se perder) e avisa
 * a loja por e-mail. O "Responder" do e-mail vai direto para o cliente.
 */
public class ContatoService {

    private static final Logger LOGGER = Logger.getLogger(ContatoService.class.getName());

    /** Os mesmos assuntos do &lt;select&gt; do contato.jsp: nada fora desta lista é aceito. */
    public static final List<String> ASSUNTOS = List.of(
            "Dúvida sobre um pedido", "Trocas e devoluções", "Indicação de vinho",
            "Eventos e empresas", "Outro assunto");

    public static final int MENSAGEM_MIN = 10;
    public static final int MENSAGEM_MAX = 1000;

    private final ContatoDAO contatoDAO;
    private final EmailService emailService;

    public ContatoService() {
        this(new ContatoDAO(), new EmailService());
    }

    /** Construtor para testes. */
    public ContatoService(ContatoDAO contatoDAO, EmailService emailService) {
        this.contatoDAO = contatoDAO;
        this.emailService = emailService;
    }

    public void enviar(Usuario clienteLogado, String nome, String email, String assunto, String numeroPedido,
            String mensagem, boolean aceitouPrivacidade) throws RegraNegocioException {

        MensagemContato m = validar(nome, email, assunto, numeroPedido, mensagem, aceitouPrivacidade);
        m.setClienteId(clienteLogado == null ? null : clienteLogado.getId());
        m.setId(contatoDAO.inserir(m));

        String destino = EmailService.emailDaLoja();
        if (destino == null) {
            LOGGER.warning("Mensagem de contato id=" + m.getId() + " gravada, mas EMAIL_USER não está configurado.");
            return;
        }
        try {
            String titulo = "[Contato #" + m.getId() + "] " + m.getAssunto()
                    + (m.getNumeroPedido() == null ? "" : " — pedido " + m.getNumeroPedido());
            emailService.enviarEmailAssincrono(destino, titulo,
                    EmailTemplateUtil.getCorpoMensagemContato(m.getNome(), m.getEmail(),
                            titulo, m.getMensagem()),
                    m.getEmail());
        } catch (RuntimeException e) {
            // A mensagem já está no banco: o aviso por e-mail falhar não é grave
            LOGGER.log(Level.WARNING, "Falha ao avisar a loja sobre o contato id=" + m.getId(), e);
        }
    }

    /** Validação no servidor (o JavaScript valida antes, mas pode ser contornado). */
    static MensagemContato validar(String nome, String email, String assunto, String numeroPedido,
            String mensagem, boolean aceitouPrivacidade) throws RegraNegocioException {
        if (Validacao.vazio(nome) || nome.trim().length() > 80) {
            throw new RegraNegocioException("Informe o seu nome (até 80 caracteres).");
        }
        String emailTratado = Validacao.normalizarEmail(email);
        if (!Validacao.emailValido(emailTratado)) {
            throw new RegraNegocioException("Informe um e-mail válido, como nome@exemplo.com.");
        }
        if (assunto == null || !ASSUNTOS.contains(assunto.trim())) {
            throw new RegraNegocioException("Selecione o assunto.");
        }
        String texto = mensagem == null ? "" : mensagem.trim();
        if (texto.length() < MENSAGEM_MIN || texto.length() > MENSAGEM_MAX) {
            throw new RegraNegocioException("A mensagem deve ter entre " + MENSAGEM_MIN + " e " + MENSAGEM_MAX
                    + " caracteres.");
        }
        if (!aceitouPrivacidade) {
            throw new RegraNegocioException("Confirme o aceite da Política de Privacidade.");
        }
        String pedido = Validacao.vazio(numeroPedido) ? null : numeroPedido.trim().toUpperCase();
        if (pedido != null && !pedido.matches("[A-Z0-9-]{1,20}")) {
            throw new RegraNegocioException("O número do pedido tem o formato AG-000123.");
        }

        MensagemContato m = new MensagemContato();
        m.setNome(nome.trim().replaceAll("\\s+", " "));
        m.setEmail(emailTratado);
        m.setAssunto(assunto.trim());
        m.setNumeroPedido(pedido);
        m.setMensagem(texto);
        return m;
    }

    // ===================================================================== painel administrativo

    public static final int RESPOSTA_MAX = 5000;

    public List<MensagemContato> listar(boolean pendentes) {
        return contatoDAO.listar(pendentes, 200);
    }

    public int contarPendentes() {
        return contatoDAO.contarPendentes();
    }

    public MensagemContato buscar(int id) {
        return id <= 0 ? null : contatoDAO.buscarPorId(id);
    }

    /**
     * Envia a resposta ao cliente por e-mail e guarda-a. O e-mail é enviado
     * ANTES de marcar como respondida (e sem ser em segundo plano): se o envio
     * falhar, a mensagem continua pendente e o administrador vê o erro.
     */
    public void responder(Administrador admin, int id, String resposta) throws RegraNegocioException {
        String texto = resposta == null ? "" : resposta.trim();
        if (texto.length() < MENSAGEM_MIN) {
            throw new RegraNegocioException("Escreva a resposta (pelo menos " + MENSAGEM_MIN + " caracteres).");
        }
        if (texto.length() > RESPOSTA_MAX) {
            throw new RegraNegocioException("A resposta pode ter no máximo " + RESPOSTA_MAX + " caracteres.");
        }
        MensagemContato m = buscar(id);
        if (m == null) {
            throw new RegraNegocioException("Mensagem não encontrada.");
        }
        if (m.isRespondida()) {
            throw new RegraNegocioException("Esta mensagem já foi respondida.");
        }

        try {
            emailService.enviarEmail(m.getEmail(), "Re: " + m.getAssunto() + " - Vinheria Agnello",
                    EmailTemplateUtil.getCorpoRespostaContato(m.getNome(), m.getMensagem(), texto, admin.getNome()),
                    EmailService.emailDaLoja());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Falha ao enviar a resposta da mensagem id=" + id, e);
            throw new RegraNegocioException("O e-mail não pôde ser enviado agora. A mensagem continua pendente; "
                    + "tente novamente em alguns minutos.");
        }

        if (!contatoDAO.registrarResposta(id, texto, admin.getId())) {
            LOGGER.warning("Mensagem id=" + id + " respondida em duplicado (outra pessoa respondeu ao mesmo tempo).");
        }
        LOGGER.log(Level.INFO, "Mensagem id={0} respondida pelo administrador id={1}", new Object[] {id, admin.getId()});
    }

    /** Fecha a mensagem sem enviar e-mail. */
    public void marcarResolvida(Administrador admin, int id) throws RegraNegocioException {
        if (!contatoDAO.marcarResolvida(id, admin.getId())) {
            throw new RegraNegocioException("Esta mensagem já estava resolvida.");
        }
    }
}
