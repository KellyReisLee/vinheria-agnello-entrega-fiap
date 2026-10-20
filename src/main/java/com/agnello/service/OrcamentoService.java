package com.agnello.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ContatoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.MensagemContato;
import com.agnello.model.OrcamentoEvento;
import com.agnello.model.Usuario;
import com.agnello.util.EmailTemplateUtil;
import com.agnello.util.Validacao;

/**
 * Pedido de orçamento da Calculadora de Eventos:
 *   1. valida os dados e refaz o cálculo das garrafas;
 *   2. grava nas Mensagens do painel (assunto "Orçamento de evento"), para
 *      nada se perder e a equipe poder responder de lá;
 *   3. envia um e-mail estilizado à loja (o "Responder" vai para o cliente)
 *      e outro de confirmação ao cliente.
 * Se um e-mail falhar, o pedido continua gravado: nada se perde.
 */
public class OrcamentoService {

    private static final Logger LOGGER = Logger.getLogger(OrcamentoService.class.getName());

    public static final String ASSUNTO = "Orçamento de evento";
    public static final int OBSERVACOES_MAX = 800;

    private final ContatoDAO contatoDAO;
    private final EmailService emailService;

    public OrcamentoService() {
        this(new ContatoDAO(), new EmailService());
    }

    /** Construtor para testes. */
    public OrcamentoService(ContatoDAO contatoDAO, EmailService emailService) {
        this.contatoDAO = contatoDAO;
        this.emailService = emailService;
    }

    /** @return o número do pedido de orçamento (id da mensagem no painel) */
    public int enviar(Usuario clienteLogado, String nome, String email, String telefone, String orcamento,
            String observacoes, String estilo, String convidados, String duracao) throws RegraNegocioException {

        OrcamentoEvento o = validar(nome, email, telefone, orcamento, observacoes, estilo, convidados, duracao);
        o.setClienteId(clienteLogado == null ? null : clienteLogado.getId());

        MensagemContato m = new MensagemContato();
        m.setClienteId(o.getClienteId());
        m.setNome(o.getNome());
        m.setEmail(o.getEmail());
        m.setAssunto(ASSUNTO);
        m.setMensagem(resumoTexto(o));
        int id = contatoDAO.inserir(m);

        String loja = EmailService.emailDaLoja();
        if (loja == null) {
            LOGGER.warning("Orçamento #" + id + " gravado, mas EMAIL_USER não está configurado.");
        } else {
            try {
                emailService.enviarEmailAssincrono(loja,
                        "[Orçamento #" + id + "] " + o.getEstilo().getNome() + " para " + o.getConvidados()
                                + " convidados — " + o.getNome(),
                        EmailTemplateUtil.getCorpoOrcamentoLoja(o, id), o.getEmail());
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "Falha ao avisar a loja sobre o orçamento #" + id, e);
            }
        }
        try {
            emailService.enviarEmailAssincrono(o.getEmail(),
                    "Recebemos o seu pedido de orçamento - Vinheria Agnello",
                    EmailTemplateUtil.getCorpoOrcamentoCliente(o, id), loja);
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Falha ao confirmar o orçamento #" + id + " ao cliente", e);
        }
        LOGGER.log(Level.INFO, "Orçamento de evento #{0} recebido ({1} convidados)",
                new Object[] {id, o.getConvidados()});
        return id;
    }

    /** Validação no servidor (o JavaScript valida antes, mas pode ser contornado). */
    static OrcamentoEvento validar(String nome, String email, String telefone, String orcamento, String observacoes,
            String estilo, String convidados, String duracao) throws RegraNegocioException {
        String n = Validacao.vazio(nome) ? "" : nome.trim().replaceAll("\\s+", " ");
        if (n.isEmpty() || n.length() > 80) {
            throw new RegraNegocioException("Informe o seu nome (até 80 caracteres).");
        }
        String e = Validacao.normalizarEmail(email);
        if (!Validacao.emailValido(e)) {
            throw new RegraNegocioException("Informe um e-mail válido, como nome@exemplo.com.");
        }
        String tel = Validacao.somenteDigitos(telefone);
        if (!Validacao.vazio(tel) && !Validacao.telefoneValido(tel)) {
            throw new RegraNegocioException("Informe o telefone com DDD (10 ou 11 dígitos) ou deixe em branco.");
        }
        String orc = Validacao.vazio(orcamento) ? null : orcamento.trim().replaceAll("\\s+", " ");
        if (orc != null && orc.length() > 30) {
            throw new RegraNegocioException("O orçamento estimado pode ter no máximo 30 caracteres.");
        }
        String obs = Validacao.vazio(observacoes) ? null : observacoes.trim();
        if (obs != null && obs.length() > OBSERVACOES_MAX) {
            throw new RegraNegocioException("Os detalhes do evento podem ter no máximo " + OBSERVACOES_MAX
                    + " caracteres.");
        }
        OrcamentoEvento.Estilo est = estilo == null ? null : OrcamentoEvento.ESTILOS.get(estilo.trim());
        if (est == null) {
            throw new RegraNegocioException("Escolha o tipo de evento.");
        }
        int conv = inteiro(convidados, OrcamentoEvento.CONVIDADOS_MIN, OrcamentoEvento.CONVIDADOS_MAX,
                "O número de convidados deve ser de 1 a 500.");
        int dur = inteiro(duracao, OrcamentoEvento.DURACAO_MIN, OrcamentoEvento.DURACAO_MAX,
                "A duração deve ser de 1 a 24 horas.");

        OrcamentoEvento o = new OrcamentoEvento();
        o.setNome(n);
        o.setEmail(e);
        o.setTelefone(Validacao.vazio(tel) ? null : tel);
        o.setOrcamentoEstimado(orc);
        o.setObservacoes(obs);
        o.setEstilo(est);
        o.setConvidados(conv);
        o.setDuracaoHoras(dur);
        o.calcular();
        return o;
    }

    /** Texto guardado no painel (Mensagens). */
    static String resumoTexto(OrcamentoEvento o) {
        StringBuilder s = new StringBuilder();
        s.append("Evento: ").append(o.getEstilo().getNome()).append(" · ").append(o.getConvidados())
                .append(" convidados · ").append(o.getDuracaoHoras()).append(o.getDuracaoHoras() == 1 ? " hora" : " horas")
                .append('\n');
        s.append("Recomendação: ").append(o.getGarrafas()).append(" garrafas (")
                .append(o.getTintos()).append(" tintos, ").append(o.getBrancos()).append(" brancos, ")
                .append(o.getEspumantes()).append(" espumantes)\n");
        if (o.getTelefone() != null) {
            s.append("Telefone/WhatsApp: ").append(formatarTelefone(o.getTelefone())).append('\n');
        }
        if (o.getOrcamentoEstimado() != null) {
            s.append("Orçamento estimado: ").append(o.getOrcamentoEstimado()).append('\n');
        }
        if (o.getObservacoes() != null) {
            s.append("\nDetalhes do cliente:\n").append(o.getObservacoes());
        }
        return s.toString();
    }

    public static String formatarTelefone(String d) {
        if (d == null) {
            return "";
        }
        if (d.length() == 11) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);
        }
        if (d.length() == 10) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 6) + "-" + d.substring(6);
        }
        return d;
    }

    private static int inteiro(String texto, int min, int max, String mensagem) throws RegraNegocioException {
        try {
            int v = Integer.parseInt(texto == null ? "" : texto.trim());
            if (v < min || v > max) {
                throw new RegraNegocioException(mensagem);
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new RegraNegocioException(mensagem);
        }
    }
}
