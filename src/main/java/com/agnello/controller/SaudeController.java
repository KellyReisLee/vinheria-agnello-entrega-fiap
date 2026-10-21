package com.agnello.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.connection.ConnectionFactory;
import com.agnello.dao.ClienteDAO;
import com.agnello.pagamento.ConfigPagamento;
import com.agnello.seguranca.ChavesAplicacao;
import com.agnello.seguranca.CifraDados;
import com.agnello.util.RespostaJson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /saude — "o site está bem configurado?"
 *
 * Responde em JSON se o banco responde, se as chaves de criptografia existem e
 * abrem os dados já gravados, se as migrações foram rodadas e se o e-mail e o
 * Mercado Pago estão configurados. NUNCA mostra valores (senhas, chaves, tokens):
 * só "ok", "falta" ou "erro".
 *
 * Também serve de "Health Check Path" no Render: enquanto responder 503, o
 * Render não manda visitantes para uma versão nova que ainda não está pronta.
 */
@WebServlet(name = "SaudeController", urlPatterns = {"/saude"})
public class SaudeController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SaudeController.class.getName());

    static final String OK = "ok";
    static final String FALTA = "falta configurar";
    static final String ERRO = "erro";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Robots-Tag", "noindex");

        Map<String, Object> itens = new LinkedHashMap<>();
        String chaves = verificarChaves();
        itens.put("chaves", chaves);

        String banco;
        try (Connection conn = ConnectionFactory.getConnection()) {
            banco = OK;
            itens.put("migracaoV8", existeColuna(conn, "pedidos", "pagamento_id") ? OK : "falta rodar no Neon");
            itens.put("migracaoV9", existeTabela(conn, "avisos_estoque") ? OK : "falta rodar no Neon");
            if (OK.equals(chaves)) {
                itens.put("chavesAbremOsDados", chavesAbremOsDados(conn));
            }
        } catch (RuntimeException | java.sql.SQLException e) {
            LOGGER.log(Level.WARNING, "Saúde: o banco não respondeu", e);
            banco = ERRO;
        }
        itens.put("banco", banco);
        itens.put("email", configurado("EMAIL_USER") && configurado("EMAIL_PASSWORD") ? OK : FALTA);
        itens.put("enderecoDoSite", configurado("AGNELLO_URL_BASE") ? OK : FALTA);
        itens.put("mercadoPago", ConfigPagamento.token() == null ? FALTA : OK);
        itens.put("webhookMercadoPago", ConfigPagamento.segredoWebhook() == null ? FALTA : OK);

        // O site só está "saudável" se o banco e as chaves funcionam (sem isso, login e compras falham)
        boolean saudavel = OK.equals(banco) && OK.equals(chaves)
                && !ERRO.equals(itens.get("chavesAbremOsDados"));
        response.setStatus(saudavel ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("status", saudavel ? "ok" : "com problemas");
        corpo.putAll(itens);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RespostaJson.paraJson(corpo));
    }

    private static String verificarChaves() {
        try {
            ChavesAplicacao.validar();
            return OK;
        } catch (IllegalStateException e) {
            return FALTA;
        }
    }

    /**
     * Tenta abrir um CPF ou telefone já gravado. Se falhar, as chaves deste servidor
     * são diferentes das usadas quando as contas foram criadas.
     */
    static String chavesAbremOsDados(Connection conn) {
        String sql = "SELECT cpf_cifrado, telefone_cifrado FROM clientes "
                + "WHERE cpf_cifrado IS NOT NULL OR telefone_cifrado IS NOT NULL ORDER BY id LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                return "sem contas para conferir";
            }
            String cpf = rs.getString("cpf_cifrado");
            if (cpf != null) {
                CifraDados.decifrar(ClienteDAO.CAMPO_CPF, cpf);
            } else {
                CifraDados.decifrar(ClienteDAO.CAMPO_TELEFONE, rs.getString("telefone_cifrado"));
            }
            return OK;
        } catch (java.sql.SQLException e) {
            return "não foi possível conferir";
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Saúde: as chaves deste servidor não abrem os dados gravados", e);
            return ERRO;
        }
    }

    private static boolean existeTabela(Connection conn, String tabela) throws java.sql.SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT to_regclass(?) IS NOT NULL")) {
            stmt.setString(1, tabela);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    private static boolean existeColuna(Connection conn, String tabela, String coluna) throws java.sql.SQLException {
        String sql = "SELECT 1 FROM information_schema.columns WHERE table_name = ? AND column_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tabela);
            stmt.setString(2, coluna);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean configurado(String variavel) {
        String v = System.getenv(variavel);
        return v != null && !v.isBlank();
    }
}
