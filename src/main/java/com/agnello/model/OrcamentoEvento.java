package com.agnello.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pedido de orçamento da Calculadora de Eventos (página inicial).
 *
 * O cálculo das garrafas é refeito AQUI, com as mesmas métricas do
 * calculadora.js: os números que chegam do navegador não são usados.
 */
public class OrcamentoEvento implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CONVIDADOS_MIN = 1;
    public static final int CONVIDADOS_MAX = 500;
    public static final int DURACAO_MIN = 1;
    public static final int DURACAO_MAX = 24;
    /** Garrafa padrão de 750 ml. */
    private static final double LITROS_POR_GARRAFA = 0.75;

    /** Estilo de evento → [nome, consumo por pessoa/hora (L), % tinto, % branco, % espumante]. */
    public static final class Estilo {
        private final String codigo;
        private final String nome;
        private final double consumoPorHora;
        private final double tinto;
        private final double branco;
        private final String descricao;

        Estilo(String codigo, String nome, double consumoPorHora, double tinto, double branco, String descricao) {
            this.codigo = codigo;
            this.nome = nome;
            this.consumoPorHora = consumoPorHora;
            this.tinto = tinto;
            this.branco = branco;
            this.descricao = descricao;
        }

        public String getCodigo() { return codigo; }
        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
    }

    public static final Map<String, Estilo> ESTILOS = new LinkedHashMap<>();

    static {
        ESTILOS.put("jantar", new Estilo("jantar", "Jantar", 0.35, 0.60, 0.30,
                "Harmonizações estruturadas com pratos principais, valorizando tintos encorpados e brancos elegantes."));
        ESTILOS.put("festa", new Estilo("festa", "Festa", 0.50, 0.30, 0.20,
                "Celebração dinâmica, com alta rotação de espumantes para brindar e rótulos refrescantes."));
        ESTILOS.put("coquetel", new Estilo("coquetel", "Coquetel", 0.40, 0.20, 0.35,
                "Recepção sofisticada, com espumantes e brancos aromáticos que harmonizam com finger foods."));
    }

    // dados do cliente
    private String nome;
    private String email;
    private String telefone;
    private String orcamentoEstimado;
    private String observacoes;
    private Integer clienteId;
    // evento
    private Estilo estilo;
    private int convidados;
    private int duracaoHoras;
    // resultado
    private int garrafas;
    private int tintos;
    private int brancos;
    private int espumantes;

    /** Garrafas totais e por tipo (mesma conta do calculadora.js). */
    public void calcular() {
        double litros = convidados * duracaoHoras * estilo.consumoPorHora;
        // Arredonda antes do ceil para evitar 12.000000001 → 13 por causa de ponto flutuante
        garrafas = (int) Math.ceil(Math.round(litros / LITROS_POR_GARRAFA * 1e6) / 1e6);
        tintos = (int) Math.round(garrafas * estilo.tinto);
        brancos = (int) Math.round(garrafas * estilo.branco);
        espumantes = Math.max(0, garrafas - (tintos + brancos));
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getOrcamentoEstimado() { return orcamentoEstimado; }
    public void setOrcamentoEstimado(String orcamentoEstimado) { this.orcamentoEstimado = orcamentoEstimado; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public Estilo getEstilo() { return estilo; }
    public void setEstilo(Estilo estilo) { this.estilo = estilo; }
    public int getConvidados() { return convidados; }
    public void setConvidados(int convidados) { this.convidados = convidados; }
    public int getDuracaoHoras() { return duracaoHoras; }
    public void setDuracaoHoras(int duracaoHoras) { this.duracaoHoras = duracaoHoras; }
    public int getGarrafas() { return garrafas; }
    public int getTintos() { return tintos; }
    public int getBrancos() { return brancos; }
    public int getEspumantes() { return espumantes; }
}
