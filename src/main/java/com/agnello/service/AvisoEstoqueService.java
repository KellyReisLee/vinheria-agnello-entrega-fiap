package com.agnello.service;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.AvisoEstoqueDAO;
import com.agnello.dao.ProdutoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Produto;
import com.agnello.model.Usuario;
import com.agnello.util.EmailTemplateUtil;
import com.agnello.util.Validacao;

/**
 * "Avise-me quando chegar": o cliente deixa o e-mail num vinho esgotado e,
 * quando o estoque volta (painel, ou um pedido cancelado devolve garrafas),
 * recebe um e-mail. Cada pedido de aviso é enviado uma só vez.
 */
public class AvisoEstoqueService {

    private static final Logger LOGGER = Logger.getLogger(AvisoEstoqueService.class.getName());
    /** Máximo de e-mails por reposição (protege a conta de e-mail de um envio enorme de uma vez). */
    static final int MAXIMO_POR_VEZ = 300;

    private final AvisoEstoqueDAO avisoDAO;
    private final ProdutoDAO produtoDAO;
    private final EmailService emailService;

    public AvisoEstoqueService() {
        this(new AvisoEstoqueDAO(), new ProdutoDAO(), new EmailService());
    }

    /** Construtor para testes. */
    public AvisoEstoqueService(AvisoEstoqueDAO avisoDAO, ProdutoDAO produtoDAO, EmailService emailService) {
        this.avisoDAO = avisoDAO;
        this.produtoDAO = produtoDAO;
        this.emailService = emailService;
    }

    /** O cliente pede o aviso. @return true se é um pedido novo; false se já tinha pedido */
    public boolean registrar(Usuario clienteLogado, int produtoId, String email) throws RegraNegocioException {
        String e = Validacao.normalizarEmail(email);
        if (!Validacao.emailValido(e)) {
            throw new RegraNegocioException("Informe um e-mail válido, como nome@exemplo.com.");
        }
        Produto p = produtoId <= 0 ? null : produtoDAO.buscarPorId(produtoId);
        if (p == null || !p.isAtivo()) {
            throw new RegraNegocioException("Este vinho não está mais na adega.");
        }
        if (p.getEstoque() > 0) {
            throw new RegraNegocioException("Boa notícia: este vinho já está disponível! Recarregue a página.");
        }
        return avisoDAO.registrar(p.getId(), e, clienteLogado == null ? null : clienteLogado.getId());
    }

    /**
     * Se o vinho está à venda e com estoque, avisa quem estava à espera.
     * Nunca lança erro: um problema aqui não pode impedir o painel de salvar o estoque.
     * @return quantas pessoas foram avisadas
     */
    public int avisarSeDisponivel(int produtoId) {
        try {
            Produto p = produtoDAO.buscarPorId(produtoId);
            if (p == null || !p.isAtivo() || p.getEstoque() <= 0) {
                return 0;
            }
            Map<Integer, String> pendentes = avisoDAO.retirarPendentes(produtoId, MAXIMO_POR_VEZ);
            for (String email : pendentes.values()) {
                try {
                    emailService.enviarEmailAssincrono(email, p.getNome() + " voltou à adega - Vinheria Agnello",
                            EmailTemplateUtil.getCorpoVoltouAoEstoque(p));
                } catch (RuntimeException ex) {
                    LOGGER.log(Level.WARNING, "Falha ao enviar um aviso de estoque do produto id=" + produtoId, ex);
                }
            }
            if (!pendentes.isEmpty()) {
                LOGGER.log(Level.INFO, "{0} cliente(s) avisado(s): {1} voltou ao estoque",
                        new Object[] {pendentes.size(), p.getNome()});
            }
            return pendentes.size();
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Não foi possível processar os avisos do produto id=" + produtoId, e);
            return 0;
        }
    }

    /** Pessoas à espera de cada vinho (vazio se o banco falhar). Aproveita para apagar avisos antigos. */
    public Map<Integer, Integer> pendentesPorProduto() {
        try {
            avisoDAO.limparAntigos();
            return avisoDAO.contarPendentesPorProduto();
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Não foi possível contar os avisos de estoque", e);
            return Map.of();
        }
    }
}
