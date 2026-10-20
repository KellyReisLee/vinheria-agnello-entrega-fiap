package com.agnello.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.agnello.dao.ContatoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.MensagemContato;
import com.agnello.model.OrcamentoEvento;
import com.agnello.util.EmailTemplateUtil;

class OrcamentoServiceTest {

    static class ContatoFalso extends ContatoDAO {
        final List<MensagemContato> gravadas = new ArrayList<>();

        @Override
        public int inserir(MensagemContato m) {
            gravadas.add(m);
            return 41 + gravadas.size();
        }
    }

    static class EmailFalso extends EmailService {
        final List<String> para = new ArrayList<>();
        final List<String> corpos = new ArrayList<>();

        @Override
        public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml) {
            para.add(destinatario);
            corpos.add(corpoHtml);
        }

        @Override
        public void enviarEmailAssincrono(String destinatario, String assunto, String corpoHtml, String responderPara) {
            enviarEmailAssincrono(destinatario, assunto, corpoHtml);
        }
    }

    private static OrcamentoEvento valido(String estilo, String convidados, String duracao) throws Exception {
        return OrcamentoService.validar("Joana", "joana@exemplo.com", "", "", "", estilo, convidados, duracao);
    }

    @Test
    void calculoIgualAoDaCalculadoraDoSite() throws Exception {
        // Valores conferidos com o calculadora.js (12 convidados, 4 horas)
        OrcamentoEvento jantar = valido("jantar", "12", "4");   // 16,8 L → 22,4 → 23 garrafas
        assertEquals(23, jantar.getGarrafas());
        assertEquals(14, jantar.getTintos());
        assertEquals(7, jantar.getBrancos());
        assertEquals(2, jantar.getEspumantes());

        OrcamentoEvento festa = valido("festa", "12", "4");     // 24 L → 32 garrafas exatas
        assertEquals(32, festa.getGarrafas());
        assertEquals(32, festa.getTintos() + festa.getBrancos() + festa.getEspumantes());

        OrcamentoEvento um = valido("coquetel", "1", "1");
        assertEquals(1, um.getGarrafas());
    }

    @Test
    void dadosInvalidosSaoRecusados() {
        assertThrows(RegraNegocioException.class, () -> valido("churrasco", "12", "4"));
        assertThrows(RegraNegocioException.class, () -> valido("jantar", "0", "4"));
        assertThrows(RegraNegocioException.class, () -> valido("jantar", "501", "4"));
        assertThrows(RegraNegocioException.class, () -> valido("jantar", "12", "25"));
        assertThrows(RegraNegocioException.class, () -> valido("jantar", "doze", "4"));
        assertThrows(RegraNegocioException.class, () -> OrcamentoService.validar("", "j@x.com", null, null, null,
                "jantar", "5", "2"));
        assertThrows(RegraNegocioException.class, () -> OrcamentoService.validar("Joana", "joana@", null, null, null,
                "jantar", "5", "2"));
        assertThrows(RegraNegocioException.class, () -> OrcamentoService.validar("Joana", "j@x.com", "1234", null, null,
                "jantar", "5", "2"));
        assertThrows(RegraNegocioException.class, () -> OrcamentoService.validar("Joana", "j@x.com", null, null,
                "x".repeat(801), "jantar", "5", "2"));
    }

    @Test
    void opcionaisFicamNulosETelefoneSoComDigitos() throws Exception {
        OrcamentoEvento o = OrcamentoService.validar(" Joana  Silva ", " Joana@Exemplo.COM ", "(11) 98765-4321",
                " R$ 2.000 ", "  ", "festa", "30", "5");
        assertEquals("Joana Silva", o.getNome());
        assertEquals("joana@exemplo.com", o.getEmail());
        assertEquals("11987654321", o.getTelefone());
        assertEquals("R$ 2.000", o.getOrcamentoEstimado());
        assertNull(o.getObservacoes());
    }

    @Test
    void enviarGravaNoPainelEConfirmaAoCliente() throws Exception {
        ContatoFalso banco = new ContatoFalso();
        EmailFalso email = new EmailFalso();
        int numero = new OrcamentoService(banco, email).enviar(null, "Joana", "joana@exemplo.com", "11987654321",
                null, "Jantar no dia 20, massa e carne.", "jantar", "12", "4");

        assertEquals(42, numero);
        MensagemContato m = banco.gravadas.get(0);
        assertEquals(OrcamentoService.ASSUNTO, m.getAssunto());
        assertTrue(m.getMensagem().contains("23 garrafas"));
        assertTrue(m.getMensagem().contains("(11) 98765-4321"));
        assertTrue(m.getMensagem().length() <= 2000); // limite da tabela mensagens_contato
        assertTrue(email.para.contains("joana@exemplo.com"));
    }

    @Test
    void emailsEscapamOQueOClienteEscreve() throws Exception {
        OrcamentoEvento o = OrcamentoService.validar("<b>Joana</b>", "joana@exemplo.com", null, null,
                "<script>alert(1)</script>", "jantar", "12", "4");
        String loja = EmailTemplateUtil.getCorpoOrcamentoLoja(o, 7);
        String cliente = EmailTemplateUtil.getCorpoOrcamentoCliente(o, 7);
        assertFalse(loja.contains("<script>"));
        assertFalse(cliente.contains("<b>Joana</b>"));
        assertTrue(loja.contains("&lt;script&gt;"));
        assertTrue(loja.contains("Orçamento #7"));
        assertTrue(cliente.contains("23 <span"));
    }

    @Test
    void textoMaisLongoPossivelCabeNaTabela() throws Exception {
        OrcamentoEvento o = OrcamentoService.validar("N".repeat(80), "joana@exemplo.com", "11987654321",
                "R".repeat(30), "x".repeat(800), "coquetel", "500", "24");
        assertTrue(OrcamentoService.resumoTexto(o).length() <= 2000);
    }
}
