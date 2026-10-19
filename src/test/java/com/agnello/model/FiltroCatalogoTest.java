package com.agnello.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class FiltroCatalogoTest {

    private static FiltroCatalogo ler(String... paresNomeValor) {
        Map<String, String[]> p = new HashMap<>();
        for (int i = 0; i < paresNomeValor.length; i += 2) {
            p.put(paresNomeValor[i], new String[] {paresNomeValor[i + 1]});
        }
        return FiltroCatalogo.de(p);
    }

    @Test
    void semParametrosUsaOPadrao() {
        FiltroCatalogo f = ler();
        assertEquals("", f.getBusca());
        assertEquals("relevancia", f.getOrdem());
        assertEquals(1, f.getPagina());
        assertFalse(f.isFiltrando());
        assertEquals("", f.getQuery());
        assertEquals("?", f.getSeparador());
    }

    @Test
    void valoresDesconhecidosSaoIgnorados() {
        FiltroCatalogo f = ler("tipo", "laranja", "ordem", "preco; DROP TABLE produtos", "pagina", "-3");
        assertEquals("", f.getTipo());
        assertEquals("relevancia", f.getOrdem());
        assertEquals(1, f.getPagina());
    }

    @Test
    void tipoAceitaMaiusculas() {
        assertEquals("tinto", ler("tipo", "TINTO").getTipo());
    }

    @Test
    void enderecosAntigosComQContinuamAFuncionar() {
        assertEquals("malbec", ler("q", "malbec").getBusca());
    }

    @Test
    void precoInvertidoEhTrocado() {
        FiltroCatalogo f = ler("precoMin", "300", "precoMax", "100");
        assertEquals(0, new BigDecimal("100").compareTo(f.getPrecoMin()));
        assertEquals(0, new BigDecimal("300").compareTo(f.getPrecoMax()));
    }

    @Test
    void precoInvalidoOuNegativoEhIgnorado() {
        assertNull(ler("precoMin", "abc").getPrecoMin());
        assertNull(ler("precoMin", "-5").getPrecoMin());
        assertEquals("99.5", ler("precoMax", "99,50").getPrecoMaxTexto());
    }

    @Test
    void buscaGuardaNoMaximoCincoPalavras() {
        List<String> palavras = ler("busca", "  um  dois tres quatro cinco seis ").getPalavras();
        assertEquals(5, palavras.size());
        assertEquals("um", palavras.get(0));
    }

    @Test
    void queryPreservaFiltrosECodificaTexto() {
        FiltroCatalogo f = ler("busca", "rosé & cia", "tipo", "rose", "ordem", "menor-preco", "pagina", "3");
        assertEquals("?busca=ros%C3%A9+%26+cia&tipo=rose&ordem=menor-preco", f.getQuery());
        assertEquals("&", f.getSeparador());
        assertTrue(f.isFiltrando());
    }

    @Test
    void cadaEtiquetaRemoveSoOSeuFiltro() {
        FiltroCatalogo f = ler("tipo", "tinto", "pais", "Chile", "precoMin", "100", "precoMax", "200");
        List<FiltroCatalogo.FiltroAtivo> ativos = f.getAtivos();
        assertEquals(3, ativos.size());
        assertEquals("Tintos", ativos.get(0).getRotulo());
        assertEquals("?pais=Chile&precoMin=100&precoMax=200", ativos.get(0).getQuery());
        assertEquals("?tipo=tinto&precoMin=100&precoMax=200", ativos.get(1).getQuery());
        // a faixa de preço sai inteira com um só "×"
        assertEquals("R$ 100 a R$ 200", ativos.get(2).getRotulo());
        assertEquals("?tipo=tinto&pais=Chile", ativos.get(2).getQuery());
    }
}
