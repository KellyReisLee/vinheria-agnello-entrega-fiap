package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.MensagemContato;

class ContatoServiceTest {

    private static final String TEXTO = "Gostaria de saber se o Malbec chega gelado.";

    @Test
    void mensagemValidaEhNormalizada() throws Exception {
        MensagemContato m = ContatoService.validar("  Ana   Lima ", " Ana@Exemplo.COM ",
                "Dúvida sobre um pedido", " ag-000012 ", TEXTO, true);

        assertEquals("Ana Lima", m.getNome());
        assertEquals("ana@exemplo.com", m.getEmail());
        assertEquals("AG-000012", m.getNumeroPedido());
    }

    @Test
    void numeroDoPedidoEhOpcional() throws Exception {
        MensagemContato m = ContatoService.validar("Ana", "ana@exemplo.com", "Outro assunto", "", TEXTO, true);
        assertNull(m.getNumeroPedido());
    }

    @Test
    void assuntoForaDaListaEhRecusado() {
        assertThrows(RegraNegocioException.class,
                () -> ContatoService.validar("Ana", "ana@exemplo.com", "Spam", null, TEXTO, true));
    }

    @Test
    void mensagemCurtaEhRecusada() {
        assertThrows(RegraNegocioException.class,
                () -> ContatoService.validar("Ana", "ana@exemplo.com", "Outro assunto", null, "Oi", true));
    }

    @Test
    void emailInvalidoEhRecusado() {
        assertThrows(RegraNegocioException.class,
                () -> ContatoService.validar("Ana", "ana@", "Outro assunto", null, TEXTO, true));
    }

    @Test
    void semAceiteDaPrivacidadeEhRecusado() {
        assertThrows(RegraNegocioException.class,
                () -> ContatoService.validar("Ana", "ana@exemplo.com", "Outro assunto", null, TEXTO, false));
    }
}
