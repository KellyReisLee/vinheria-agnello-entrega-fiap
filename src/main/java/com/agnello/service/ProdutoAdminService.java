package com.agnello.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ImagemDAO;
import com.agnello.dao.ProdutoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Administrador;
import com.agnello.model.Produto;

/**
 * Cadastro e edição de vinhos pelo painel.
 *
 * O desconto ("17% OFF") é calculado aqui a partir do preço antigo, para
 * nunca ficar diferente dos preços. A foto enviada é conferida pelos
 * primeiros bytes do arquivo (a "assinatura" do PNG, JPEG ou WebP), e não
 * pelo nome: renomear "virus.exe" para "foto.png" não engana esta verificação.
 */
public class ProdutoAdminService {

    private static final Logger LOGGER = Logger.getLogger(ProdutoAdminService.class.getName());

    public static final int TAMANHO_MAXIMO_IMAGEM = 2 * 1024 * 1024; // 2 MB
    public static final int LIMITE_ESTOQUE_BAIXO = 5;
    private static final BigDecimal PRECO_MAXIMO = new BigDecimal("99999999.99");

    private final ProdutoDAO produtoDAO;
    private final ImagemDAO imagemDAO;

    public ProdutoAdminService() {
        this(new ProdutoDAO(), new ImagemDAO());
    }

    /** Construtor para testes. */
    public ProdutoAdminService(ProdutoDAO produtoDAO, ImagemDAO imagemDAO) {
        this.produtoDAO = produtoDAO;
        this.imagemDAO = imagemDAO;
    }

    public List<Produto> listar(String busca, Boolean ativos) {
        return produtoDAO.listarParaAdmin(busca, ativos);
    }

    public List<String> listarTipos() {
        return produtoDAO.listarTipos();
    }

    public int contarEstoqueBaixo() {
        return produtoDAO.contarEstoqueBaixo(LIMITE_ESTOQUE_BAIXO);
    }

    public Produto buscar(int id) {
        return id <= 0 ? null : produtoDAO.buscarPorId(id);
    }

    /**
     * Valida e grava (cria se não tem id, atualiza se tem). Devolve o id.
     * @param imagem bytes da foto nova, ou null para manter a atual
     */
    public int salvar(Administrador admin, FormularioProduto form, byte[] imagem) throws RegraNegocioException {
        Produto p = validar(form);

        if (imagem != null && imagem.length > 0) {
            String tipoConteudo = tipoDaImagem(imagem);
            int idImagem = imagemDAO.inserir(tipoConteudo, imagem);
            p.setImagem("/imagens/" + idImagem);
        } else if (form.getImagemAtual().isEmpty()) {
            throw new RegraNegocioException("Envie uma foto do vinho (PNG, JPG ou WebP).");
        }

        if (form.isNovo()) {
            int id = produtoDAO.inserir(p);
            LOGGER.log(Level.INFO, "Produto id={0} criado pelo administrador id={1}", new Object[] {id, admin.getId()});
            return id;
        }
        if (!produtoDAO.atualizar(p)) {
            throw new RegraNegocioException("Este vinho não existe mais.");
        }
        LOGGER.log(Level.INFO, "Produto id={0} alterado pelo administrador id={1}", new Object[] {p.getId(), admin.getId()});
        return p.getId();
    }

    public void definirAtivo(Administrador admin, int id, boolean ativo) throws RegraNegocioException {
        if (!produtoDAO.definirAtivo(id, ativo)) {
            throw new RegraNegocioException("Vinho não encontrado.");
        }
        LOGGER.log(Level.INFO, "Produto id={0} {1} pelo administrador id={2}",
                new Object[] {id, ativo ? "ativado" : "desativado", admin.getId()});
    }

    public void definirEstoque(Administrador admin, int id, String estoqueTexto) throws RegraNegocioException {
        int estoque = inteiro(estoqueTexto, "estoque");
        validarEstoque(estoque);
        if (!produtoDAO.definirEstoque(id, estoque)) {
            throw new RegraNegocioException("Vinho não encontrado.");
        }
        LOGGER.log(Level.INFO, "Estoque do produto id={0} = {1} (administrador id={2})",
                new Object[] {id, estoque, admin.getId()});
    }

    // ------------------------------------------------------------------ validação

    static Produto validar(FormularioProduto f) throws RegraNegocioException {
        obrigatorio(f.getNome(), 150, "o nome");
        obrigatorio(f.getTipo(), 50, "o tipo (ex.: Tinto Encorpado)");
        if (f.getOrigem().length() > 80) {
            throw new RegraNegocioException("A origem pode ter no máximo 80 caracteres.");
        }
        if (f.getDescricao().length() > 2000) {
            throw new RegraNegocioException("A descrição pode ter no máximo 2000 caracteres.");
        }
        if (f.getPontuacao().length() > 80) {
            throw new RegraNegocioException("A pontuação pode ter no máximo 80 caracteres.");
        }

        BigDecimal preco = dinheiro(f.getPreco(), "o preço");
        if (preco == null || preco.signum() <= 0) {
            throw new RegraNegocioException("Informe o preço (maior que zero).");
        }
        BigDecimal precoAntigo = dinheiro(f.getPrecoAntigo(), "o preço antigo");
        if (precoAntigo != null && precoAntigo.compareTo(preco) <= 0) {
            throw new RegraNegocioException("O preço antigo (\"De\") tem de ser maior que o preço atual. "
                    + "Deixe em branco se o vinho não está em promoção.");
        }
        int estoque = inteiro(f.getEstoque(), "o estoque");
        validarEstoque(estoque);

        Produto p = new Produto();
        p.setId(f.getId() == null ? 0 : f.getId());
        p.setNome(f.getNome());
        p.setTipo(f.getTipo());
        p.setOrigem(f.getOrigem().isEmpty() ? null : f.getOrigem());
        p.setDescricao(f.getDescricao().isEmpty() ? null : f.getDescricao());
        p.setPreco(preco.doubleValue());
        p.setPrecoAntigo(precoAntigo == null ? 0 : precoAntigo.doubleValue());
        p.setDesconto(calcularDesconto(preco, precoAntigo));
        p.setPontuacao(f.getPontuacao().isEmpty() ? null : f.getPontuacao());
        p.setEstoque(estoque);
        p.setImagem(f.getImagemAtual().isEmpty() ? null : f.getImagemAtual());
        p.setAtivo(f.isAtivo());
        return p;
    }

    /** De R$ 229,90 por R$ 189,90 → "17% OFF". Sem preço antigo → null. */
    public static String calcularDesconto(BigDecimal preco, BigDecimal precoAntigo) {
        if (precoAntigo == null || precoAntigo.compareTo(preco) <= 0) {
            return null;
        }
        BigDecimal percentual = BigDecimal.ONE.subtract(preco.divide(precoAntigo, 4, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
        return percentual.signum() > 0 ? percentual.toPlainString() + "% OFF" : null;
    }

    /**
     * Confere a assinatura do arquivo e devolve o tipo (image/png…).
     * Recusa arquivos grandes demais ou que não sejam PNG, JPEG ou WebP.
     */
    public static String tipoDaImagem(byte[] b) throws RegraNegocioException {
        if (b.length > TAMANHO_MAXIMO_IMAGEM) {
            throw new RegraNegocioException("A foto pode ter no máximo 2 MB. Reduza o tamanho e envie de novo.");
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "image/png";
        }
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "image/webp";
        }
        throw new RegraNegocioException("A foto precisa ser PNG, JPG ou WebP.");
    }

    private static void obrigatorio(String valor, int maximo, String campo) throws RegraNegocioException {
        if (valor.isEmpty()) {
            throw new RegraNegocioException("Informe " + campo + ".");
        }
        if (valor.length() > maximo) {
            throw new RegraNegocioException("O campo " + campo.replaceFirst("^o ", "") + " pode ter no máximo "
                    + maximo + " caracteres.");
        }
    }

    /** "1.189,90", "1189.90", "R$ 189,9" → BigDecimal. Vazio → null. */
    static BigDecimal dinheiro(String texto, String campo) throws RegraNegocioException {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String t = texto.replace("R$", "").replace(" ", "").trim();
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", "."); // formato brasileiro: 1.189,90
        }
        try {
            BigDecimal valor = new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
            if (valor.signum() < 0 || valor.compareTo(PRECO_MAXIMO) > 0) {
                throw new RegraNegocioException("Valor inválido em " + campo + ".");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Valor inválido em " + campo + ". Use, por exemplo, 189,90.");
        }
    }

    private static int inteiro(String texto, String campo) throws RegraNegocioException {
        try {
            return Integer.parseInt(texto == null ? "" : texto.trim());
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Informe " + campo + " como um número inteiro.");
        }
    }

    private static void validarEstoque(int estoque) throws RegraNegocioException {
        if (estoque < 0 || estoque > 100_000) {
            throw new RegraNegocioException("O estoque tem de estar entre 0 e 100 000 garrafas.");
        }
    }
}
