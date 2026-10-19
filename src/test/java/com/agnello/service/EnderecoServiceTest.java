package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Endereco;

class EnderecoServiceTest {

    private static Endereco endereco() {
        Endereco e = new Endereco();
        e.setApelido("  Casa  ");
        e.setCep("01310-100");
        e.setLogradouro(" Avenida   Paulista ");
        e.setNumero("1578");
        e.setComplemento("");
        e.setBairro("Bela Vista");
        e.setCidade("São Paulo - SP");
        return e;
    }

    @Test
    void enderecoValidoEhNormalizado() throws Exception {
        Endereco e = endereco();
        EnderecoService.validar(e);
        assertEquals("01310100", e.getCep());
        assertEquals("Casa", e.getApelido());
        assertEquals("Avenida Paulista", e.getLogradouro());
        assertNull(e.getComplemento());
        assertEquals("01310-100", e.getCepFormatado());
    }

    @Test
    void cepIncompletoEhRecusado() {
        Endereco e = endereco();
        e.setCep("0131");
        assertThrows(RegraNegocioException.class, () -> EnderecoService.validar(e));
    }

    @Test
    void campoObrigatorioVazioEhRecusado() {
        Endereco e = endereco();
        e.setNumero("   ");
        assertThrows(RegraNegocioException.class, () -> EnderecoService.validar(e));
    }

    @Test
    void textoLongoDemaisEhRecusado() {
        Endereco e = endereco();
        e.setApelido("x".repeat(41));
        assertThrows(RegraNegocioException.class, () -> EnderecoService.validar(e));
    }

    @Test
    void semApelidoRecebeUmNomePadrao() throws Exception {
        Endereco e = endereco();
        e.setApelido(" ");
        EnderecoService.validar(e);
        EnderecoService.nomearSeVazio(e, 0);
        assertEquals("Principal", e.getApelido());
        e.setApelido(null);
        EnderecoService.nomearSeVazio(e, 2);
        assertEquals("Endereço 3", e.getApelido());
    }
}
