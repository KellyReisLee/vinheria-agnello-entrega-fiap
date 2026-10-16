package com.agnello.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidacaoTest {

    @Test
    void cpf() {
        assertTrue(Validacao.cpfValido("529.982.247-25"));
        assertFalse(Validacao.cpfValido("529.982.247-24"), "dígito verificador errado");
        assertFalse(Validacao.cpfValido("111.111.111-11"), "todos os dígitos iguais");
        assertFalse(Validacao.cpfValido("123"));
    }

    @Test
    void cnpj() {
        assertTrue(Validacao.cnpjValido("11.222.333/0001-81"));
        assertFalse(Validacao.cnpjValido("11.222.333/0001-80"));
        assertFalse(Validacao.cnpjValido("00.000.000/0000-00"));
    }

    @Test
    void email() {
        assertEquals("ana@exemplo.com", Validacao.normalizarEmail("  Ana@Exemplo.com "));
        assertTrue(Validacao.emailValido("ana@exemplo.com"));
        assertFalse(Validacao.emailValido("ana@exemplo"));
        assertFalse(Validacao.emailValido("ana exemplo@x.com"));
    }

    @Test
    void senha() {
        assertNull(Validacao.validarSenha("12345678"));
        assertTrue(Validacao.validarSenha("1234567") != null);
    }
}
