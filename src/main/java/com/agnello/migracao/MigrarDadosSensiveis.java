package com.agnello.migracao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.agnello.connection.ConnectionFactory;
import com.agnello.dao.ClienteDAO;
import com.agnello.seguranca.ChavesAplicacao;
import com.agnello.seguranca.CifraDados;
import com.agnello.seguranca.HashSenha;
import com.agnello.seguranca.IndiceCego;
import com.agnello.seguranca.TokenSeguro;
import com.agnello.service.ClienteService;
import com.agnello.util.Validacao;

/**
 * Etapa 2 de 3 da migração: protege os dados dos clientes que já existem.
 *
 * Para cada cliente que ainda não tem senha_hash:
 *   senha         → senha_hash (PBKDF2)
 *   cpf           → cpf_cifrado (AES-256-GCM) + cpf_indice (HMAC)
 *   telefone      → telefone_cifrado (AES-256-GCM), só dígitos
 *   token_ativacao→ token_ativacao_hash (SHA-256), válido por mais 48 h
 *   email/cnpj    → normalizados (minúsculas / só dígitos)
 * As colunas antigas NÃO são apagadas aqui (isso é o V3).
 *
 * Como executar (Eclipse):
 *   Run As → Run Configurations… → Java Application → nova configuração com esta classe.
 *   Separador "Environment": NEON_DB_URL, NEON_DB_USER, NEON_DB_PASSWORD,
 *   AGNELLO_CHAVE_DADOS, AGNELLO_CHAVE_INDICE (as mesmas do servidor!).
 *   Separador "Arguments": --simular  → mostra o que faria e desfaz tudo no fim.
 *   Sem argumentos → grava de verdade.
 *
 * Pode ser executado várias vezes: só trata quem ainda não foi migrado.
 * Tudo roda numa transação: se algo falhar, nada é gravado.
 */
public final class MigrarDadosSensiveis {

    private MigrarDadosSensiveis() {
    }

    public static void main(String[] args) throws SQLException {
        boolean simular = Arrays.asList(args).contains("--simular");
        ChavesAplicacao.validar();

        try (Connection conn = ConnectionFactory.getConnection()) {
            Set<String> colunas = colunasDeClientes(conn);
            if (!colunas.contains("senha_hash")) {
                sair("A coluna senha_hash não existe. Rode primeiro db/migracoes/V2__clientes_colunas_seguras.sql.");
            }
            if (!colunas.contains("senha")) {
                System.out.println("As colunas antigas já foram removidas (V3 aplicado). Nada a migrar.");
                return;
            }

            conn.setAutoCommit(false);
            try {
                Resumo resumo = migrar(conn);
                List<String> problemas = verificarDuplicados(conn);

                System.out.println();
                System.out.println(resumo);
                if (!problemas.isEmpty()) {
                    System.out.println();
                    System.out.println("PROBLEMAS (corrija antes de continuar; nada foi gravado):");
                    problemas.forEach(p -> System.out.println("  - " + p));
                    conn.rollback();
                    System.exit(1);
                }
                if (simular) {
                    conn.rollback();
                    System.out.println("\nSIMULAÇÃO: nada foi gravado. Rode sem --simular para aplicar.");
                } else {
                    conn.commit();
                    System.out.println("\nMigração gravada com sucesso.");
                    System.out.println("Próximo passo: publicar a nova versão da aplicação e, depois, rodar o V3.");
                }
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private static Resumo migrar(Connection conn) throws SQLException {
        String selecionar = "SELECT id, tipo_cliente, email, senha, cpf, telefone, cnpj, email_verificado, token_ativacao "
                + "FROM clientes WHERE senha_hash IS NULL ORDER BY id FOR UPDATE";
        String atualizar = "UPDATE clientes SET email = ?, senha_hash = ?, cpf_cifrado = ?, cpf_indice = ?, "
                + "telefone_cifrado = ?, cnpj = ?, token_ativacao_hash = ?, "
                + "token_ativacao_expira = CASE WHEN ?::text IS NULL THEN NULL "
                + "ELSE now() + make_interval(hours => ?) END "
                + "WHERE id = ?";

        Resumo resumo = new Resumo();
        try (PreparedStatement sel = conn.prepareStatement(selecionar);
             PreparedStatement upd = conn.prepareStatement(atualizar);
             ResultSet rs = sel.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                resumo.clientes++;

                String senha = rs.getString("senha");
                String senhaHash;
                if (Validacao.vazio(senha)) {
                    // Sem senha conhecida: hash de um valor aleatório (ninguém consegue entrar);
                    // o cliente cria uma senha nova com "Esqueci a senha".
                    senhaHash = HashSenha.gerar(TokenSeguro.gerar());
                    resumo.avisos.add("Cliente id=" + id + " não tinha senha: terá de usar \"Esqueci a senha\".");
                } else {
                    senhaHash = HashSenha.gerar(senha);
                }

                String cpf = Validacao.somenteDigitos(rs.getString("cpf"));
                String cpfCifrado = null;
                String cpfIndice = null;
                if (!Validacao.vazio(cpf)) {
                    if (!Validacao.cpfValido(cpf)) {
                        resumo.avisos.add("Cliente id=" + id + " tem um CPF inválido (" + cpf.length()
                                + " dígitos). Foi cifrado mesmo assim; confira com o cliente.");
                    }
                    cpfCifrado = CifraDados.cifrar(ClienteDAO.CAMPO_CPF, cpf);
                    cpfIndice = IndiceCego.calcular(ClienteDAO.CAMPO_CPF, cpf);
                    resumo.cpfs++;
                }

                String telefone = Validacao.somenteDigitos(rs.getString("telefone"));
                String telefoneCifrado = null;
                if (!Validacao.vazio(telefone)) {
                    telefoneCifrado = CifraDados.cifrar(ClienteDAO.CAMPO_TELEFONE, telefone);
                    resumo.telefones++;
                }

                String token = rs.getString("token_ativacao");
                String tokenHash = (!rs.getBoolean("email_verificado") && !Validacao.vazio(token))
                        ? TokenSeguro.hash(token) : null;
                if (tokenHash != null) {
                    resumo.tokens++;
                }

                String cnpj = Validacao.somenteDigitos(rs.getString("cnpj"));

                upd.setString(1, Validacao.normalizarEmail(rs.getString("email")));
                upd.setString(2, senhaHash);
                upd.setString(3, cpfCifrado);
                upd.setString(4, cpfIndice);
                upd.setString(5, telefoneCifrado);
                upd.setString(6, Validacao.vazio(cnpj) ? null : cnpj);
                upd.setString(7, tokenHash);
                upd.setString(8, tokenHash);
                upd.setInt(9, ClienteService.VALIDADE_ATIVACAO_HORAS);
                upd.setInt(10, id);
                upd.executeUpdate();

                System.out.println("  OK cliente id=" + id + " (" + rs.getString("tipo_cliente") + ")");
            }
        }
        return resumo;
    }

    /** Duplicados impediriam os índices únicos do V3: melhor descobrir já. */
    private static List<String> verificarDuplicados(Connection conn) throws SQLException {
        List<String> problemas = new ArrayList<>();
        String[][] consultas = {
            {"e-mail", "SELECT lower(email), count(*) FROM clientes GROUP BY lower(email) HAVING count(*) > 1"},
            {"CPF", "SELECT cpf_indice, count(*) FROM clientes WHERE cpf_indice IS NOT NULL GROUP BY cpf_indice HAVING count(*) > 1"},
            {"CNPJ", "SELECT cnpj, count(*) FROM clientes WHERE cnpj IS NOT NULL GROUP BY cnpj HAVING count(*) > 1"},
        };
        for (String[] c : consultas) {
            try (PreparedStatement st = conn.prepareStatement(c[1]); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    // Não imprimimos o CPF (nem o índice): só que existe repetição
                    String valor = "CPF".equals(c[0]) ? "(oculto)" : rs.getString(1);
                    problemas.add(c[0] + " repetido em " + rs.getInt(2) + " contas: " + valor);
                }
            }
        }
        return problemas;
    }

    private static Set<String> colunasDeClientes(Connection conn) throws SQLException {
        Set<String> colunas = new HashSet<>();
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, "clientes", null)) {
            while (rs.next()) {
                colunas.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }
        if (colunas.isEmpty()) {
            sair("Tabela clientes não encontrada. Confira NEON_DB_URL.");
        }
        return colunas;
    }

    private static void sair(String mensagem) {
        System.err.println(mensagem);
        System.exit(1);
    }

    private static final class Resumo {
        int clientes;
        int cpfs;
        int telefones;
        int tokens;
        final List<String> avisos = new ArrayList<>();

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("Clientes migrados: ").append(clientes)
             .append(" | CPFs cifrados: ").append(cpfs)
             .append(" | telefones cifrados: ").append(telefones)
             .append(" | links de ativação mantidos: ").append(tokens);
            for (String aviso : avisos) {
                s.append("\n  ! ").append(aviso);
            }
            return s.toString();
        }
    }
}
