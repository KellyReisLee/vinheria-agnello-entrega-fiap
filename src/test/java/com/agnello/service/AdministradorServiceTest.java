package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agnello.dao.AdministradorDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Administrador;
import com.agnello.seguranca.HashSenha;

class AdministradorServiceTest {

    private static final String SENHA = "uma-senha-bem-longa";

    /** Banco falso com um único administrador. */
    static class DaoFalso extends AdministradorDAO {
        Administrador admin;
        int falhas;

        @Override
        public Administrador buscarPorEmail(String email) {
            if (admin == null || !admin.getEmail().equalsIgnoreCase(email)) {
                return null;
            }
            Administrador copia = new Administrador();
            copia.setId(admin.getId());
            copia.setNome(admin.getNome());
            copia.setEmail(admin.getEmail());
            copia.setAtivo(admin.isAtivo());
            copia.setSenhaHash(admin.getSenhaHash());
            copia.setBloqueadoAte(admin.getBloqueadoAte());
            return copia;
        }

        @Override
        public void registrarFalhaLogin(int id, int max, int minutos) {
            falhas++;
        }

        @Override
        public void registrarLoginComSucesso(int id, String novoHash) {
            falhas = 0;
        }
    }

    private DaoFalso dao;
    private AdministradorService service;

    @BeforeEach
    void preparar() {
        dao = new DaoFalso();
        dao.admin = new Administrador();
        dao.admin.setId(1);
        dao.admin.setNome("Kelly Reis");
        dao.admin.setEmail("kelly@agnello.com");
        dao.admin.setSenhaHash(HashSenha.gerar(SENHA));
        service = new AdministradorService(dao);
    }

    @Test
    void loginCorretoNaoLevaOHashParaASessao() throws Exception {
        Administrador a = service.autenticar(" KELLY@agnello.com ", SENHA);
        assertEquals("Kelly", a.getPrimeiroNome());
        assertNull(a.getSenhaHash());
    }

    @Test
    void mesmaMensagemParaEmailOuSenhaErrados() {
        RegraNegocioException senhaErrada = assertThrows(RegraNegocioException.class,
                () -> service.autenticar("kelly@agnello.com", "errada-errada-errada"));
        RegraNegocioException emailErrado = assertThrows(RegraNegocioException.class,
                () -> service.autenticar("outra@agnello.com", SENHA));
        assertEquals(senhaErrada.getMessage(), emailErrado.getMessage());
        assertEquals(1, dao.falhas);
    }

    @Test
    void contaDesativadaNaoEntra() {
        dao.admin.setAtivo(false);
        assertThrows(RegraNegocioException.class, () -> service.autenticar("kelly@agnello.com", SENHA));
    }

    @Test
    void contaBloqueadaNaoEntraNemComASenhaCerta() {
        dao.admin.setBloqueadoAte(OffsetDateTime.now().plusMinutes(10));
        RegraNegocioException e = assertThrows(RegraNegocioException.class,
                () -> service.autenticar("kelly@agnello.com", SENHA));
        assertTrue(e.getMessage().contains("Muitas tentativas"));
    }

    @Test
    void senhaDeAdministradorTemPeloMenos12Caracteres() {
        assertTrue(AdministradorService.validarSenhaAdmin("curta-11ch") != null);
        assertNull(AdministradorService.validarSenhaAdmin("exatamente12"));
    }
}
