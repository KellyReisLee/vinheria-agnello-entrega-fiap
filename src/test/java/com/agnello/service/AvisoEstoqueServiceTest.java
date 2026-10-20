package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agnello.dao.AvisoEstoqueDAO;
import com.agnello.dao.ProdutoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.ClientePF;
import com.agnello.model.Produto;

class AvisoEstoqueServiceTest {

    /** Banco falso de avisos: guarda os pedidos pendentes por vinho. */
    static class AvisoDAOFalso extends AvisoEstoqueDAO {
        final Map<Integer, List<String>> pendentes = new HashMap<>();
        Integer ultimoClienteId;
        boolean falhar;

        @Override
        public boolean registrar(int produtoId, String email, Integer clienteId) {
            ultimoClienteId = clienteId;
            List<String> lista = pendentes.computeIfAbsent(produtoId, k -> new ArrayList<>());
            if (lista.contains(email)) {
                return false;
            }
            return lista.add(email);
        }

        @Override
        public Map<Integer, String> retirarPendentes(int produtoId, int limite) {
            if (falhar) {
                throw new RuntimeException("banco fora do ar");
            }
            Map<Integer, String> saida = new LinkedHashMap<>();
            List<String> lista = pendentes.getOrDefault(produtoId, new ArrayList<>());
            int i = 0;
            while (!lista.isEmpty() && i < limite) {
                saida.put(++i, lista.remove(0));
            }
            return saida;
        }
    }

    static class ProdutoDAOFalso extends ProdutoDAO {
        final Map<Integer, Produto> produtos = new HashMap<>();

        @Override
        public Produto buscarPorId(int id) {
            return produtos.get(id);
        }
    }

    static class EmailFalso extends EmailService {
        final List<String> destinatarios = new ArrayList<>();

        @Override
        public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
            destinatarios.add(destinatario);
        }
    }

    private AvisoDAOFalso avisos;
    private ProdutoDAOFalso produtos;
    private EmailFalso email;
    private AvisoEstoqueService service;

    @BeforeEach
    void preparar() {
        avisos = new AvisoDAOFalso();
        produtos = new ProdutoDAOFalso();
        email = new EmailFalso();
        service = new AvisoEstoqueService(avisos, produtos, email);
        produtos.produtos.put(1, produto(1, 0, true));   // esgotado
        produtos.produtos.put(2, produto(2, 4, true));   // disponível
        produtos.produtos.put(3, produto(3, 0, false));  // fora do catálogo
    }

    private static Produto produto(int id, int estoque, boolean ativo) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome("Vinho " + id);
        p.setPreco(99.90);
        p.setEstoque(estoque);
        p.setAtivo(ativo);
        return p;
    }

    @Test
    void registraUmaVezPorEmailEVinho() throws Exception {
        assertTrue(service.registrar(null, 1, "  Ana@Exemplo.com "));
        assertFalse(service.registrar(null, 1, "ana@exemplo.com"));
        assertEquals(List.of("ana@exemplo.com"), avisos.pendentes.get(1));
    }

    @Test
    void guardaOClienteQuandoEstaLogado() throws Exception {
        ClientePF c = new ClientePF();
        c.setId(42);
        service.registrar(c, 1, "ana@exemplo.com");
        assertEquals(Integer.valueOf(42), avisos.ultimoClienteId);
    }

    @Test
    void recusaEmailInvalidoVinhoDisponivelOuForaDoCatalogo() {
        assertThrows(RegraNegocioException.class, () -> service.registrar(null, 1, "ana@"));
        assertThrows(RegraNegocioException.class, () -> service.registrar(null, 2, "ana@exemplo.com"));
        assertThrows(RegraNegocioException.class, () -> service.registrar(null, 3, "ana@exemplo.com"));
        assertThrows(RegraNegocioException.class, () -> service.registrar(null, 99, "ana@exemplo.com"));
        assertTrue(avisos.pendentes.isEmpty());
    }

    @Test
    void avisaTodosQuandoOEstoqueVoltaEDepoisNaoRepete() throws Exception {
        service.registrar(null, 1, "ana@exemplo.com");
        service.registrar(null, 1, "bia@exemplo.com");

        assertEquals(0, service.avisarSeDisponivel(1)); // ainda esgotado: ninguém é avisado
        assertTrue(email.destinatarios.isEmpty());

        produtos.produtos.get(1).setEstoque(6);
        assertEquals(2, service.avisarSeDisponivel(1));
        assertEquals(List.of("ana@exemplo.com", "bia@exemplo.com"), email.destinatarios);

        assertEquals(0, service.avisarSeDisponivel(1)); // cada aviso sai uma vez só
        assertEquals(2, email.destinatarios.size());
    }

    @Test
    void erroNoBancoNaoAtrapalhaQuemChamou() {
        avisos.falhar = true;
        assertEquals(0, service.avisarSeDisponivel(2));
    }
}
