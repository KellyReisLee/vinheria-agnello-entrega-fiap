package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;

import org.postgresql.util.PSQLException;

import com.agnello.connection.ConnectionFactory;
import com.agnello.exception.DadoDuplicadoException;
import com.agnello.model.ClientePF;
import com.agnello.model.ClientePJ;
import com.agnello.model.Usuario;
import com.agnello.seguranca.CifraDados;
import com.agnello.seguranca.IndiceCego;

/**
 * Acesso à tabela "clientes".
 *
 * Esta é a ÚNICA classe que conhece o formato guardado no banco:
 * - ao gravar, cifra CPF e telefone e calcula o índice cego do CPF;
 * - ao ler, decifra — o resto da aplicação trabalha sempre com os valores normais.
 *
 * Senhas e tokens chegam aqui já transformados em hash pelo ClienteService.
 * Todas as consultas usam PreparedStatement (proteção contra SQL Injection).
 */
public class ClienteDAO {

    /** Nomes usados como "dados associados" na cifra e no índice (não mudar depois de ir para produção). */
    public static final String CAMPO_CPF = "cpf";
    public static final String CAMPO_TELEFONE = "telefone";

    private static final String COLUNAS =
            "id, tipo_cliente, email, senha_hash, nome, sobrenome, cpf_cifrado, razao_social, cnpj, "
            + "telefone_cifrado, email_verificado, bloqueado_ate";

    /** Código do PostgreSQL para "violação de valor único". */
    private static final String SQLSTATE_DUPLICADO = "23505";

    // ------------------------------------------------------------------ cadastro

    public int inserirPF(ClientePF cliente, String senhaHash, String tokenAtivacaoHash, int validadeTokenHoras)
            throws DadoDuplicadoException {
        String sql = "INSERT INTO clientes (tipo_cliente, email, senha_hash, nome, sobrenome, cpf_cifrado, cpf_indice, "
                + "telefone_cifrado, email_verificado, token_ativacao_hash, token_ativacao_expira) "
                + "VALUES ('PF', ?, ?, ?, ?, ?, ?, ?, false, ?, now() + make_interval(hours => ?))";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getEmail());
            stmt.setString(2, senhaHash);
            stmt.setString(3, cliente.getNome());
            stmt.setString(4, cliente.getSobrenome());
            stmt.setString(5, CifraDados.cifrar(CAMPO_CPF, cliente.getCpf()));
            stmt.setString(6, IndiceCego.calcular(CAMPO_CPF, cliente.getCpf()));
            stmt.setString(7, CifraDados.cifrar(CAMPO_TELEFONE, cliente.getTelefone()));
            stmt.setString(8, tokenAtivacaoHash);
            stmt.setInt(9, validadeTokenHoras);
            stmt.executeUpdate();
            return idGerado(stmt);

        } catch (SQLException e) {
            if (SQLSTATE_DUPLICADO.equals(e.getSQLState())) {
                throw new DadoDuplicadoException(mensagemDuplicado(e));
            }
            throw new RuntimeException("Erro ao cadastrar cliente.", e);
        }
    }

    public int inserirPJ(ClientePJ cliente, String senhaHash, String tokenAtivacaoHash, int validadeTokenHoras)
            throws DadoDuplicadoException {
        String sql = "INSERT INTO clientes (tipo_cliente, email, senha_hash, razao_social, cnpj, telefone_cifrado, "
                + "email_verificado, token_ativacao_hash, token_ativacao_expira) "
                + "VALUES ('PJ', ?, ?, ?, ?, ?, false, ?, now() + make_interval(hours => ?))";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getEmail());
            stmt.setString(2, senhaHash);
            stmt.setString(3, cliente.getRazaoSocial());
            stmt.setString(4, cliente.getCnpj());
            stmt.setString(5, CifraDados.cifrar(CAMPO_TELEFONE, cliente.getTelefone()));
            stmt.setString(6, tokenAtivacaoHash);
            stmt.setInt(7, validadeTokenHoras);
            stmt.executeUpdate();
            return idGerado(stmt);

        } catch (SQLException e) {
            if (SQLSTATE_DUPLICADO.equals(e.getSQLState())) {
                throw new DadoDuplicadoException(mensagemDuplicado(e));
            }
            throw new RuntimeException("Erro ao cadastrar cliente.", e);
        }
    }

    /** Ativa a conta se o token existir e estiver no prazo. O token só funciona uma vez. */
    public boolean ativarPorToken(String tokenHash) {
        String sql = "UPDATE clientes SET email_verificado = true, token_ativacao_hash = NULL, token_ativacao_expira = NULL "
                + "WHERE token_ativacao_hash = ? AND token_ativacao_expira > now()";
        return executarUpdate(sql, "ativar conta", tokenHash) > 0;
    }

    // ------------------------------------------------------------------ consultas

    /** Inclui o hash da senha e o bloqueio: usado apenas pelo login. */
    public Usuario buscarPorEmail(String email) {
        return buscarUm("SELECT " + COLUNAS + " FROM clientes WHERE lower(email) = lower(?)", email);
    }

    public Usuario buscarPorId(int id) {
        return buscarUm("SELECT " + COLUNAS + " FROM clientes WHERE id = ?", id);
    }

    public boolean existeEmail(String email) {
        String sql = "SELECT 1 FROM clientes WHERE lower(email) = lower(?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar e-mail.", e);
        }
    }

    // ------------------------------------------------------------------ login

    /**
     * Soma uma tentativa falhada. À 5.ª seguida, bloqueia a conta por 15 minutos
     * (dificulta adivinhar a senha por tentativa e erro).
     */
    public void registrarFalhaLogin(int id, int maxTentativas, int minutosBloqueio) {
        String sql = "UPDATE clientes SET "
                + "tentativas_login_falhas = CASE WHEN tentativas_login_falhas + 1 >= ? THEN 0 ELSE tentativas_login_falhas + 1 END, "
                + "bloqueado_ate = CASE WHEN tentativas_login_falhas + 1 >= ? THEN now() + make_interval(mins => ?) ELSE bloqueado_ate END "
                + "WHERE id = ?";
        executarUpdate(sql, "registrar falha de login", maxTentativas, maxTentativas, minutosBloqueio, id);
    }

    /** Zera as tentativas e, se for o caso, grava um hash de senha mais forte. */
    public void registrarLoginComSucesso(int id, String novoHashOuNull) {
        String sql = "UPDATE clientes SET tentativas_login_falhas = 0, bloqueado_ate = NULL, ultimo_login_em = now(), "
                + "senha_hash = COALESCE(?, senha_hash) WHERE id = ?";
        executarUpdate(sql, "registrar login", novoHashOuNull, id);
    }

    // ------------------------------------------------------------------ recuperação de senha

    public void salvarTokenRecuperacao(int id, String tokenHash, int validadeMinutos) {
        String sql = "UPDATE clientes SET token_recuperacao_hash = ?, "
                + "token_recuperacao_expira = now() + make_interval(mins => ?) WHERE id = ?";
        executarUpdate(sql, "salvar token de recuperação", tokenHash, validadeMinutos, id);
    }

    public boolean tokenRecuperacaoValido(String tokenHash) {
        String sql = "SELECT 1 FROM clientes WHERE token_recuperacao_hash = ? AND token_recuperacao_expira > now()";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tokenHash);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao validar o token de recuperação.", e);
        }
    }

    /**
     * Troca a senha e invalida o token numa única instrução (não há como usar o
     * mesmo link duas vezes, nem em pedidos simultâneos). Também desbloqueia a conta.
     */
    public boolean redefinirSenhaPorToken(String tokenHash, String novoSenhaHash) {
        String sql = "UPDATE clientes SET senha_hash = ?, token_recuperacao_hash = NULL, token_recuperacao_expira = NULL, "
                + "tentativas_login_falhas = 0, bloqueado_ate = NULL "
                + "WHERE token_recuperacao_hash = ? AND token_recuperacao_expira > now()";
        return executarUpdate(sql, "redefinir senha", novoSenhaHash, tokenHash) > 0;
    }

    // ------------------------------------------------------------------ exclusão

    public boolean deletarPorId(int id) {
        return executarUpdate("DELETE FROM clientes WHERE id = ?", "excluir conta", id) > 0;
    }

    // ------------------------------------------------------------------ auxiliares

    private Usuario buscarUm(String sql, Object parametro) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente.", e);
        }
    }

    /** Converte uma linha do banco num ClientePF/ClientePJ, decifrando os dados pessoais. */
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario;
        if ("PJ".equals(rs.getString("tipo_cliente"))) {
            ClientePJ pj = new ClientePJ();
            pj.setRazaoSocial(rs.getString("razao_social"));
            pj.setCnpj(rs.getString("cnpj"));
            usuario = pj;
        } else {
            ClientePF pf = new ClientePF();
            pf.setNome(rs.getString("nome"));
            pf.setSobrenome(rs.getString("sobrenome"));
            pf.setCpf(CifraDados.decifrar(CAMPO_CPF, rs.getString("cpf_cifrado")));
            usuario = pf;
        }
        usuario.setId(rs.getInt("id"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefone(CifraDados.decifrar(CAMPO_TELEFONE, rs.getString("telefone_cifrado")));
        usuario.setEmailVerificado(rs.getBoolean("email_verificado"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        usuario.setBloqueadoAte(rs.getObject("bloqueado_ate", OffsetDateTime.class));
        return usuario;
    }

    private int executarUpdate(String sql, String operacao, Object... parametros) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao " + operacao + ".", e);
        }
    }

    private static int idGerado(PreparedStatement stmt) throws SQLException {
        try (ResultSet chaves = stmt.getGeneratedKeys()) {
            return chaves.next() ? chaves.getInt(1) : 0;
        }
    }

    /** Descobre qual dado se repetiu pelo nome da restrição única que falhou. */
    private static String mensagemDuplicado(SQLException e) {
        String detalhe = "";
        if (e instanceof PSQLException pg && pg.getServerErrorMessage() != null) {
            detalhe = String.valueOf(pg.getServerErrorMessage().getConstraint()).toLowerCase();
        }
        if (detalhe.contains("cpf")) {
            return "Este CPF já está cadastrado. Se a conta é sua, use \"Esqueci a senha\".";
        }
        if (detalhe.contains("cnpj")) {
            return "Este CNPJ já está cadastrado. Se a conta é sua, use \"Esqueci a senha\".";
        }
        return "Este e-mail já está cadastrado. Se a conta é sua, use \"Esqueci a senha\".";
    }
}
