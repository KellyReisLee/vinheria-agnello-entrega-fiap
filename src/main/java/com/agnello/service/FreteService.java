package com.agnello.service;

import java.math.BigDecimal;
import java.util.List;

import com.agnello.util.Validacao;

/**
 * Frete por região, decidido pelo primeiro dígito do CEP (a numeração dos
 * Correios vai de São Paulo, 0, até o Rio Grande do Sul, 9).
 *
 * Todas as entregas vão em embalagem térmica. Pedidos a partir de R$ 500
 * (só os vinhos, sem o frete) têm frete grátis para todo o Brasil.
 *
 * O checkout.js pede o valor em GET /frete só para mostrar ao cliente; o
 * valor cobrado é sempre recalculado aqui, ao gravar o pedido.
 */
public final class FreteService {

    public static final BigDecimal GRATIS_A_PARTIR_DE = new BigDecimal("500.00");

    /** Uma linha da tabela de frete. */
    public static final class Regiao {
        private final String digitos;
        private final String nome;
        private final BigDecimal valor;
        private final String prazo;

        Regiao(String digitos, String nome, String valor, String prazo) {
            this.digitos = digitos;
            this.nome = nome;
            this.valor = new BigDecimal(valor);
            this.prazo = prazo;
        }

        public String getNome() { return nome; }
        public BigDecimal getValor() { return valor; }
        public String getPrazo() { return prazo; }
    }

    /** A tabela (também mostrada na página Entregas e Trocas). */
    public static final List<Regiao> TABELA = List.of(
            new Regiao("0", "Grande São Paulo", "25.00", "até 2 dias úteis"),
            new Regiao("1", "Interior e litoral de São Paulo", "35.00", "2 a 3 dias úteis"),
            new Regiao("23", "Rio de Janeiro, Espírito Santo e Minas Gerais", "45.00", "3 a 5 dias úteis"),
            new Regiao("89", "Sul (PR, SC e RS)", "49.00", "4 a 6 dias úteis"),
            new Regiao("457", "Nordeste (BA, SE, PE, AL, PB, RN) e Centro-Oeste", "59.00", "5 a 8 dias úteis"),
            new Regiao("6", "Norte, Ceará, Piauí e Maranhão", "69.00", "6 a 10 dias úteis"));

    /** Resultado do cálculo para um CEP. */
    public static final class Cotacao {
        private final Regiao regiao;
        private final BigDecimal valor;
        private final boolean gratis;
        private final BigDecimal faltaParaGratis;

        Cotacao(Regiao regiao, BigDecimal valor, boolean gratis, BigDecimal faltaParaGratis) {
            this.regiao = regiao;
            this.valor = valor;
            this.gratis = gratis;
            this.faltaParaGratis = faltaParaGratis;
        }

        public Regiao getRegiao() { return regiao; }
        /** Valor a cobrar (zero se for grátis). */
        public BigDecimal getValor() { return valor; }
        public boolean isGratis() { return gratis; }
        /** Quanto falta em vinhos para o frete ficar grátis (zero se já é). */
        public BigDecimal getFaltaParaGratis() { return faltaParaGratis; }
    }

    private FreteService() {
    }

    /** Null se o CEP não tiver 8 dígitos. */
    public static Cotacao cotar(String cep, BigDecimal subtotal) {
        String d = Validacao.somenteDigitos(cep);
        if (d == null || d.length() != 8) {
            return null;
        }
        Regiao regiao = TABELA.get(0);
        for (Regiao r : TABELA) {
            if (r.digitos.indexOf(d.charAt(0)) >= 0) {
                regiao = r;
            }
        }
        BigDecimal vinhos = subtotal == null ? BigDecimal.ZERO : subtotal;
        boolean gratis = vinhos.compareTo(GRATIS_A_PARTIR_DE) >= 0;
        BigDecimal falta = gratis ? BigDecimal.ZERO : GRATIS_A_PARTIR_DE.subtract(vinhos).setScale(2, java.math.RoundingMode.HALF_UP);
        return new Cotacao(regiao, gratis ? BigDecimal.ZERO.setScale(2) : regiao.getValor(), gratis, falta);
    }
}
