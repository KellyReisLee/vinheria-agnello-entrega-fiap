package com.agnello.model;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.agnello.util.Parametros;

/**
 * O que o cliente escolheu no catálogo, lido da URL:
 *   /catalogo?busca=malbec&tipo=tinto&pais=Argentina&precoMin=100&precoMax=300&ordem=menor-preco&pagina=2
 *
 * Tudo é validado aqui: um valor desconhecido (ex.: ordem=DROP TABLE) é
 * simplesmente ignorado e volta ao padrão. Os valores NUNCA são colados no SQL;
 * vão sempre como parâmetros (?) do PreparedStatement.
 */
public class FiltroCatalogo {

    /** Tipos aceitos no filtro e o texto mostrado no botão. */
    public static final Map<String, String> TIPOS = new LinkedHashMap<>();
    /** Ordenações aceitas e o texto mostrado na lista. */
    public static final Map<String, String> ORDENS = new LinkedHashMap<>();

    static {
        TIPOS.put("tinto", "Tintos");
        TIPOS.put("branco", "Brancos");
        TIPOS.put("rose", "Rosés");
        TIPOS.put("espumante", "Espumantes");

        ORDENS.put("relevancia", "Relevância");
        ORDENS.put("menor-preco", "Menor preço");
        ORDENS.put("maior-preco", "Maior preço");
        ORDENS.put("pontuacao", "Melhor avaliados");
        ORDENS.put("novidades", "Novidades");
    }

    public static final int POR_PAGINA = 12;
    private static final int BUSCA_MAX = 80;

    private String busca = "";
    private String tipo = "";
    private String pais = "";
    private BigDecimal precoMin;
    private BigDecimal precoMax;
    private boolean soDisponiveis;
    private String ordem = "relevancia";
    private int pagina = 1;

    /** Lê e valida os parâmetros da URL. */
    public static FiltroCatalogo de(Map<String, String[]> parametros) {
        FiltroCatalogo f = new FiltroCatalogo();
        String busca = primeiro(parametros, "busca");
        if (busca.isEmpty()) {
            busca = primeiro(parametros, "q"); // endereços antigos usavam ?q=
        }
        f.busca = busca.length() > BUSCA_MAX ? busca.substring(0, BUSCA_MAX) : busca;
        String tipo = primeiro(parametros, "tipo").toLowerCase();
        f.tipo = TIPOS.containsKey(tipo) ? tipo : "";
        f.pais = primeiro(parametros, "pais");
        if (f.pais.length() > 60) {
            f.pais = "";
        }
        f.precoMin = preco(primeiro(parametros, "precoMin"));
        f.precoMax = preco(primeiro(parametros, "precoMax"));
        if (f.precoMin != null && f.precoMax != null && f.precoMin.compareTo(f.precoMax) > 0) {
            BigDecimal troca = f.precoMin; // "de 300 até 100" → entende como de 100 até 300
            f.precoMin = f.precoMax;
            f.precoMax = troca;
        }
        f.soDisponiveis = "1".equals(primeiro(parametros, "disponiveis"));
        String ordem = primeiro(parametros, "ordem");
        f.ordem = ORDENS.containsKey(ordem) ? ordem : "relevancia";
        f.pagina = Math.max(1, Math.min(10_000, Parametros.inteiro(primeiro(parametros, "pagina"), 1)));
        return f;
    }

    /** Palavras da busca ("malbec  chile" → [malbec, chile]); no máximo 5. */
    public List<String> getPalavras() {
        List<String> palavras = new ArrayList<>();
        for (String p : busca.trim().split("\\s+")) {
            if (!p.isEmpty() && palavras.size() < 5) {
                palavras.add(p);
            }
        }
        return palavras;
    }

    /** true quando há algum filtro além da ordenação (mostra "Limpar filtros"). */
    public boolean isFiltrando() {
        return !busca.isEmpty() || !tipo.isEmpty() || !pais.isEmpty() || precoMin != null || precoMax != null
                || soDisponiveis;
    }

    // ============================================================ links do JSP

    /**
     * Parâmetros desta busca prontos para um link ("?busca=malbec&tipo=tinto"),
     * sem a página e sem o filtro indicado em {@code sem} (ex.: "pais" gera o
     * link do "×" que remove o país). Devolve "" quando não sobra nada.
     */
    public String query(String sem) {
        StringBuilder q = new StringBuilder();
        anexar(q, "busca", busca, sem);
        anexar(q, "tipo", tipo, sem);
        anexar(q, "pais", pais, sem);
        anexar(q, "precoMin", texto(precoMin), sem);
        anexar(q, "precoMax", texto(precoMax), sem);
        anexar(q, "disponiveis", soDisponiveis ? "1" : "", sem);
        anexar(q, "ordem", "relevancia".equals(ordem) ? "" : ordem, sem);
        return q.toString();
    }

    /** Base dos links de paginação (todos os filtros, sem a página). */
    public String getQuery() {
        return query("");
    }

    /** Separador para acrescentar "pagina=N" ao fim de {@link #getQuery()}. */
    public String getSeparador() {
        return getQuery().isEmpty() ? "?" : "&";
    }

    /** Etiquetas "Tinto ×", "Argentina ×"... com o link que remove cada uma. */
    public List<FiltroAtivo> getAtivos() {
        List<FiltroAtivo> ativos = new ArrayList<>();
        if (!busca.isEmpty()) {
            ativos.add(new FiltroAtivo("“" + busca + "”", query("busca")));
        }
        if (!tipo.isEmpty()) {
            ativos.add(new FiltroAtivo(TIPOS.get(tipo), query("tipo")));
        }
        if (!pais.isEmpty()) {
            ativos.add(new FiltroAtivo(pais, query("pais")));
        }
        if (precoMin != null && precoMax != null) {
            String semPreco = new FiltroCatalogo(this, true).query("");
            ativos.add(new FiltroAtivo("R$ " + texto(precoMin) + " a R$ " + texto(precoMax), semPreco));
        } else if (precoMin != null) {
            ativos.add(new FiltroAtivo("A partir de R$ " + texto(precoMin), query("precoMin")));
        } else if (precoMax != null) {
            ativos.add(new FiltroAtivo("Até R$ " + texto(precoMax), query("precoMax")));
        }
        if (soDisponiveis) {
            ativos.add(new FiltroAtivo("Só disponíveis", query("disponiveis")));
        }
        return ativos;
    }

    /** Cópia sem a faixa de preço (para o "×" que remove o mínimo e o máximo juntos). */
    private FiltroCatalogo(FiltroCatalogo outro, boolean semPreco) {
        this.busca = outro.busca;
        this.tipo = outro.tipo;
        this.pais = outro.pais;
        this.precoMin = semPreco ? null : outro.precoMin;
        this.precoMax = semPreco ? null : outro.precoMax;
        this.soDisponiveis = outro.soDisponiveis;
        this.ordem = outro.ordem;
    }

    public FiltroCatalogo() {
    }

    private static void anexar(StringBuilder q, String nome, String valor, String sem) {
        if (valor == null || valor.isEmpty() || nome.equals(sem)) {
            return;
        }
        q.append(q.length() == 0 ? '?' : '&').append(nome).append('=')
                .append(URLEncoder.encode(valor, StandardCharsets.UTF_8));
    }

    /** 150.00 → "150"; 99.5 → "99.5" (para o campo e para o link). */
    private static String texto(BigDecimal valor) {
        return valor == null ? "" : valor.stripTrailingZeros().toPlainString();
    }

    /** Uma etiqueta de filtro ativo no topo dos resultados. */
    public static final class FiltroAtivo {
        private final String rotulo;
        private final String query;

        FiltroAtivo(String rotulo, String query) {
            this.rotulo = rotulo;
            this.query = query;
        }

        public String getRotulo() { return rotulo; }
        public String getQuery() { return query; }
    }

    private static String primeiro(Map<String, String[]> parametros, String nome) {
        String[] valores = parametros.get(nome);
        return valores == null || valores.length == 0 || valores[0] == null ? "" : valores[0].trim();
    }

    private static BigDecimal preco(String texto) {
        if (texto.isEmpty()) {
            return null;
        }
        try {
            BigDecimal v = new BigDecimal(texto.replace(",", "."));
            return v.signum() < 0 || v.compareTo(new BigDecimal("1000000")) > 0 ? null : v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getBusca() { return busca; }
    public String getTipo() { return tipo; }
    public String getPais() { return pais; }
    public BigDecimal getPrecoMin() { return precoMin; }
    public BigDecimal getPrecoMax() { return precoMax; }
    /** Valores para preencher os campos de preço (sem ".00"). */
    public String getPrecoMinTexto() { return texto(precoMin); }
    public String getPrecoMaxTexto() { return texto(precoMax); }
    public boolean isSoDisponiveis() { return soDisponiveis; }
    public String getOrdem() { return ordem; }
    public int getPagina() { return pagina; }

    /** Para os botões de tipo e a lista de ordenação no JSP. */
    public Map<String, String> getTipos() { return TIPOS; }
    public Map<String, String> getOrdens() { return ORDENS; }
}
