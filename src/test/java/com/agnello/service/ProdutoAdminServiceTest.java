package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Produto;

class ProdutoAdminServiceTest {

    private static FormularioProduto valido() {
        FormularioProduto f = new FormularioProduto();
        f.setNome("  Medalla   Cabernet ");
        f.setTipo("Tinto Encorpado");
        f.setPreco("189,90");
        f.setEstoque("10");
        f.setImagemAtual("/imagens/3");
        return f;
    }

    @Test
    void formularioValidoViraProduto() throws Exception {
        Produto p = ProdutoAdminService.validar(valido());
        assertEquals("Medalla Cabernet", p.getNome());
        assertEquals(189.90, p.getPreco());
        assertEquals(10, p.getEstoque());
        assertNull(p.getDesconto());
    }

    @Test
    void precoNoFormatoBrasileiro() throws Exception {
        assertEquals(new BigDecimal("1189.90"), ProdutoAdminService.dinheiro("R$ 1.189,90", "preço"));
        assertEquals(new BigDecimal("1189.90"), ProdutoAdminService.dinheiro("1189.9", "preço"));
        assertNull(ProdutoAdminService.dinheiro("  ", "preço"));
        assertThrows(RegraNegocioException.class, () -> ProdutoAdminService.dinheiro("189,9O", "preço"));
    }

    @Test
    void descontoCalculadoPeloPrecoAntigo() {
        assertEquals("17% OFF", ProdutoAdminService.calcularDesconto(new BigDecimal("189.90"), new BigDecimal("229.90")));
        assertNull(ProdutoAdminService.calcularDesconto(new BigDecimal("189.90"), null));
    }

    @Test
    void precoAntigoMenorQueOAtualEhRecusado() {
        FormularioProduto f = valido();
        f.setPrecoAntigo("150,00");
        assertThrows(RegraNegocioException.class, () -> ProdutoAdminService.validar(f));
    }

    @Test
    void camposObrigatoriosEEstoque() {
        FormularioProduto semNome = valido();
        semNome.setNome("");
        assertThrows(RegraNegocioException.class, () -> ProdutoAdminService.validar(semNome));

        FormularioProduto precoZero = valido();
        precoZero.setPreco("0");
        assertThrows(RegraNegocioException.class, () -> ProdutoAdminService.validar(precoZero));

        FormularioProduto estoqueNegativo = valido();
        estoqueNegativo.setEstoque("-1");
        assertThrows(RegraNegocioException.class, () -> ProdutoAdminService.validar(estoqueNegativo));
    }

    @Test
    void imagemConferidaPelaAssinaturaDoArquivo() throws Exception {
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        byte[] jpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00};
        byte[] webp = {'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P'};
        assertEquals("image/png", ProdutoAdminService.tipoDaImagem(png));
        assertEquals("image/jpeg", ProdutoAdminService.tipoDaImagem(jpg));
        assertEquals("image/webp", ProdutoAdminService.tipoDaImagem(webp));
        assertThrows(RegraNegocioException.class,
                () -> ProdutoAdminService.tipoDaImagem("<script>alert(1)</script>".getBytes()));
        assertThrows(RegraNegocioException.class,
                () -> ProdutoAdminService.tipoDaImagem(new byte[ProdutoAdminService.TAMANHO_MAXIMO_IMAGEM + 1]));
    }
}
