package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agnello.dao.ClienteDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.ClientePF;
import com.agnello.model.Usuario;
import com.agnello.seguranca.HashSenha;

/** Testa as regras da área pessoal com um "banco" e um e-mail falsos (sem rede). */
class ContaServiceTest {

    private static final String SENHA = "uma-senha-antiga";
    /** Gerado uma vez só: o PBKDF2 é propositadamente lento. */
    private static final String HASH = HashSenha.gerar(SENHA);

    private ClienteFalso dao;
    private EmailFalso email;
    private ContaService service;
    private ClientePF cliente;

    static class ClienteFalso extends ClienteDAO {
        ClientePF guardado;
        String novoHash;
        String telefone;

        @Override
        public Usuario buscarPorId(int id) {
            ClientePF c = new ClientePF();
            c.setId(id);
            c.setEmail(guardado.getEmail());
            c.setNome(guardado.getNome());
            c.setSobrenome(guardado.getSobrenome());
            c.setSenhaHash(novoHash != null ? novoHash : HASH);
            return c;
        }

        @Override
        public boolean atualizarDadosPF(int id, String nome, String sobrenome, String telefone) {
            guardado.setNome(nome);
            guardado.setSobrenome(sobrenome);
            this.telefone = telefone;
            return true;
        }

        @Override
        public void atualizarSenha(int id, String hash) {
            novoHash = hash;
        }
    }

    static class EmailFalso extends EmailService {
        String para;

        @Override
        public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
            para = destinatario;
        }
    }

    @BeforeEach
    void preparar() {
        cliente = new ClientePF();
        cliente.setId(7);
        cliente.setEmail("ana@exemplo.com");
        cliente.setNome("Ana");
        cliente.setSobrenome("Lima");
        dao = new ClienteFalso();
        dao.guardado = cliente;
        email = new EmailFalso();
        service = new ContaService(dao, email);
    }

    @Test
    void atualizaNomeETelefone() throws Exception {
        Usuario novo = service.atualizarDados(cliente, "  Ana  Maria ", "Lima", null, "(11) 98765-4321");
        assertEquals("Ana Maria", ((ClientePF) novo).getNome());
        assertEquals("11987654321", dao.telefone);
        assertNull(novo.getSenhaHash()); // o hash nunca volta para a sessão
    }

    @Test
    void telefoneEhOpcionalMasTemDeSerValido() throws Exception {
        service.atualizarDados(cliente, "Ana", "Lima", null, "");
        assertNull(dao.telefone);
        assertThrows(RegraNegocioException.class,
                () -> service.atualizarDados(cliente, "Ana", "Lima", null, "1234"));
    }

    @Test
    void nomeVazioEhRecusado() {
        assertThrows(RegraNegocioException.class,
                () -> service.atualizarDados(cliente, " ", "Lima", null, null));
    }

    @Test
    void trocaASenhaEAvisaPorEmail() throws Exception {
        service.trocarSenha(cliente, SENHA, "uma-senha-nova-123", "uma-senha-nova-123");
        assertNotNull(dao.novoHash);
        assertTrue(HashSenha.verificar("uma-senha-nova-123", dao.novoHash));
        assertEquals("ana@exemplo.com", email.para);
    }

    @Test
    void senhaAtualErradaEhRecusada() {
        assertThrows(RegraNegocioException.class,
                () -> service.trocarSenha(cliente, "errada-errada", "uma-senha-nova-123", "uma-senha-nova-123"));
        assertNull(dao.novoHash);
    }

    @Test
    void confirmacaoDiferenteOuCurtaEhRecusada() {
        assertThrows(RegraNegocioException.class,
                () -> service.trocarSenha(cliente, SENHA, "uma-senha-nova-123", "outra-coisa-123"));
        assertThrows(RegraNegocioException.class,
                () -> service.trocarSenha(cliente, SENHA, "curta", "curta"));
    }

    @Test
    void novaSenhaIgualAAtualEhRecusada() {
        assertThrows(RegraNegocioException.class,
                () -> service.trocarSenha(cliente, SENHA, SENHA, SENHA));
    }
}
