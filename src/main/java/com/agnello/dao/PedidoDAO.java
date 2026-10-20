package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.connection.ConnectionFactory;
import com.agnello.exception.EstoqueInsuficienteException;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.FormaPagamento;
import com.agnello.model.ItemPedido;
import com.agnello.model.Pedido;
import com.agnello.model.RegistroStatus;
import com.agnello.model.StatusPedido;
import com.agnello.model.TipoEntrega;
import com.agnello.seguranca.CifraDados;

/**
 * Acesso às tabelas "pedidos" e "itens_pedido".
 *
 * Gravar um pedido mexe em três tabelas (pedidos, itens_pedido e o estoque em
 * produtos). Tudo acontece numa TRANSAÇÃO: ou as três mudanças são gravadas,
 * ou nenhuma é. Assim nunca fica um pedido sem itens, nem estoque descontado
 * de um pedido que falhou.
 */
public class PedidoDAO {

    private static final Logger LOGGER = Logger.getLogger(PedidoDAO.class.getName());

    /** Nome usado como "dados associados" na cifra do endereço (ver CifraDados). */
    public static final String CAMPO_ENDERECO = "endereco_entrega";

    /**
     * Grava o pedido e os itens e desconta o estoque. Preenche no objeto o id,
     * o número (AG-000123) e a data gerados pelo banco.
     *
     * @throws EstoqueInsuficienteException se algum vinho não tiver garrafas
     *         suficientes (nesse caso nada é gravado)
     */
    public void inserir(Pedido pedido) throws EstoqueInsuficienteException {
        String sqlPedido = "INSERT INTO pedidos (cliente_id, status, forma_pagamento, parcelas, tipo_entrega,"
                + " subtotal, frete, desconto, total, endereco_cifrado)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, numero, criado_em";
        // "AND estoque >= ?" faz a conferência e o desconto num único passo:
        // se duas pessoas comprarem a última garrafa ao mesmo tempo, só uma consegue.
        String sqlEstoque = "UPDATE produtos SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";
        String sqlItem = "INSERT INTO itens_pedido (pedido_id, produto_id, nome_produto, imagem, preco_unitario,"
                + " quantidade) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {
                    if (pedido.getClienteId() == null) {
                        stmt.setNull(1, java.sql.Types.INTEGER);
                    } else {
                        stmt.setInt(1, pedido.getClienteId());
                    }
                    stmt.setString(2, pedido.getStatus().name());
                    stmt.setString(3, pedido.getFormaPagamento().name());
                    stmt.setInt(4, pedido.getParcelas());
                    stmt.setString(5, pedido.getTipoEntrega().name());
                    stmt.setBigDecimal(6, pedido.getSubtotal());
                    stmt.setBigDecimal(7, pedido.getFrete());
                    stmt.setBigDecimal(8, pedido.getDesconto());
                    stmt.setBigDecimal(9, pedido.getTotal());
                    stmt.setString(10, CifraDados.cifrar(CAMPO_ENDERECO, pedido.getEndereco()));
                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        pedido.setId(rs.getInt("id"));
                        pedido.setNumero(rs.getString("numero"));
                        pedido.setCriadoEm(rs.getObject("criado_em", OffsetDateTime.class));
                    }
                }

                try (PreparedStatement estoque = conn.prepareStatement(sqlEstoque);
                        PreparedStatement item = conn.prepareStatement(sqlItem)) {
                    for (ItemPedido i : pedido.getItens()) {
                        estoque.setInt(1, i.getQuantidade());
                        estoque.setInt(2, i.getProdutoId());
                        estoque.setInt(3, i.getQuantidade());
                        if (estoque.executeUpdate() == 0) {
                            throw new EstoqueInsuficienteException(i.getNomeProduto());
                        }

                        item.setInt(1, pedido.getId());
                        item.setInt(2, i.getProdutoId());
                        item.setString(3, i.getNomeProduto());
                        item.setString(4, i.getImagem());
                        item.setBigDecimal(5, i.getPrecoUnitario());
                        item.setInt(6, i.getQuantidade());
                        item.addBatch();
                    }
                    item.executeBatch();
                }

                conn.commit();
            } catch (EstoqueInsuficienteException | SQLException | RuntimeException e) {
                conn.rollback();
                pedido.setId(0);
                pedido.setNumero(null);
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gravar o pedido", e);
        }
    }

    /** Pedidos do cliente, do mais recente para o mais antigo, já com os itens. */
    public List<Pedido> listarPorCliente(int clienteId) {
        String sqlPedidos = "SELECT id, numero, cliente_id, status, forma_pagamento, parcelas, tipo_entrega,"
                + " subtotal, frete, desconto, total, endereco_cifrado, criado_em, pagamento_id, pagamento_status, pago_em"
                + " FROM pedidos WHERE cliente_id = ? ORDER BY criado_em DESC, id DESC";
        Map<Integer, Pedido> pedidos = new LinkedHashMap<>();
        try (Connection conn = ConnectionFactory.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlPedidos)) {
                stmt.setInt(1, clienteId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Pedido p = mapear(rs);
                        pedidos.put(p.getId(), p);
                    }
                }
            }
            if (pedidos.isEmpty()) {
                return new ArrayList<>();
            }

            carregarItens(conn, pedidos);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar os pedidos do cliente id=" + clienteId, e);
        }
        return new ArrayList<>(pedidos.values());
    }

    // ================================================================== painel administrativo

    /** Nome de quem comprou: razão social (PJ) ou nome + sobrenome (PF). */
    private static final String NOME_CLIENTE =
            "COALESCE(c.razao_social, trim(c.nome || ' ' || COALESCE(c.sobrenome, '')))";

    /**
     * Pedidos para o painel, do mais recente para o mais antigo.
     * @param status  filtro opcional (null = todos)
     * @param busca   parte do número do pedido ou do e-mail do cliente (opcional)
     */
    public List<Pedido> listarParaAdmin(StatusPedido status, String busca, int limite, int deslocamento) {
        StringBuilder sql = new StringBuilder("SELECT p.id, p.numero, p.cliente_id, p.status, p.forma_pagamento,"
                + " p.parcelas, p.tipo_entrega, p.subtotal, p.frete, p.desconto, p.total, NULL AS endereco_cifrado,"
                + " p.criado_em, p.pagamento_id, p.pagamento_status, p.pago_em, " + NOME_CLIENTE + " AS nome_cliente, c.email AS email_cliente,"
                + " (SELECT COALESCE(sum(quantidade), 0) FROM itens_pedido i WHERE i.pedido_id = p.id) AS garrafas"
                + " FROM pedidos p LEFT JOIN clientes c ON c.id = p.cliente_id WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (status != null) {
            sql.append(" AND p.status = ?");
            parametros.add(status.name());
        }
        if (busca != null && !busca.isBlank()) {
            sql.append(" AND (p.numero ILIKE ? OR c.email ILIKE ?)");
            String termo = "%" + busca.trim().replace("%", "").replace("_", "") + "%";
            parametros.add(termo);
            parametros.add(termo);
        }
        sql.append(" ORDER BY p.criado_em DESC, p.id DESC LIMIT ? OFFSET ?");
        parametros.add(limite);
        parametros.add(deslocamento);

        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pedido p = mapear(rs);
                    p.setNomeCliente(rs.getString("nome_cliente"));
                    p.setEmailCliente(rs.getString("email_cliente"));
                    // Na lista não carregamos os itens: só a quantidade total de garrafas
                    ItemPedido resumo = new ItemPedido();
                    resumo.setQuantidade(rs.getInt("garrafas"));
                    p.getItens().add(resumo);
                    pedidos.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos para o painel", e);
        }
        return pedidos;
    }

    /** Quantos pedidos há em cada status (os que não têm nenhum ficam com 0). */
    public Map<StatusPedido, Integer> contarPorStatus() {
        Map<StatusPedido, Integer> contagem = new EnumMap<>(StatusPedido.class);
        for (StatusPedido s : StatusPedido.values()) {
            contagem.put(s, 0);
        }
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT status, count(*) FROM pedidos GROUP BY status");
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                contagem.put(StatusPedido.valueOf(rs.getString(1)), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar pedidos", e);
        }
        return contagem;
    }

    /** Soma dos pedidos não cancelados feitos no mês atual (fuso de São Paulo). */
    public java.math.BigDecimal faturamentoDoMes() {
        String sql = "SELECT COALESCE(sum(total), 0) FROM pedidos WHERE status <> 'CANCELADO'"
                + " AND criado_em >= date_trunc('month', now() AT TIME ZONE 'America/Sao_Paulo') AT TIME ZONE 'America/Sao_Paulo'";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return rs.getBigDecimal(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular o faturamento do mês", e);
        }
    }

    /** Um pedido completo para o painel: itens, endereço, cliente e histórico. Null se não existir. */
    public Pedido buscarParaAdmin(int id) {
        String sql = "SELECT p.id, p.numero, p.cliente_id, p.status, p.forma_pagamento, p.parcelas, p.tipo_entrega,"
                + " p.subtotal, p.frete, p.desconto, p.total, p.endereco_cifrado, p.criado_em,"
                + " p.pagamento_id, p.pagamento_status, p.pago_em,"
                + " " + NOME_CLIENTE + " AS nome_cliente, c.email AS email_cliente"
                + " FROM pedidos p LEFT JOIN clientes c ON c.id = p.cliente_id WHERE p.id = ?";
        String sqlHistorico = "SELECT h.status_anterior, h.status_novo, h.criado_em, h.pelo_cliente, h.pelo_gateway,"
                + " a.nome AS nome_admin"
                + " FROM historico_pedidos h LEFT JOIN administradores a ON a.id = h.administrador_id"
                + " WHERE h.pedido_id = ? ORDER BY h.criado_em, h.id";

        try (Connection conn = ConnectionFactory.getConnection()) {
            Pedido pedido;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    pedido = mapear(rs);
                    pedido.setNomeCliente(rs.getString("nome_cliente"));
                    pedido.setEmailCliente(rs.getString("email_cliente"));
                }
            }
            Map<Integer, Pedido> mapa = new LinkedHashMap<>();
            mapa.put(pedido.getId(), pedido);
            carregarItens(conn, mapa);

            try (PreparedStatement stmt = conn.prepareStatement(sqlHistorico)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        RegistroStatus r = new RegistroStatus();
                        r.setAnterior(StatusPedido.valueOf(rs.getString("status_anterior")));
                        r.setNovo(StatusPedido.valueOf(rs.getString("status_novo")));
                        r.setCriadoEm(rs.getObject("criado_em", OffsetDateTime.class));
                        r.setNomeAdministrador(rs.getString("nome_admin"));
                        r.setPeloCliente(rs.getBoolean("pelo_cliente"));
                        r.setPeloGateway(rs.getBoolean("pelo_gateway"));
                        pedido.getHistorico().add(r);
                    }
                }
            }
            return pedido;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o pedido id=" + id, e);
        }
    }

    /**
     * Muda o status numa transação: atualiza o pedido, grava o histórico e, se
     * for cancelamento, devolve as garrafas ao estoque.
     *
     * "WHERE status = ?" garante que ninguém mudou o pedido entre o momento em
     * que a página foi aberta e o clique (duas pessoas no painel ao mesmo tempo).
     */
    public void mudarStatus(int pedidoId, StatusPedido atual, StatusPedido novo, int administradorId)
            throws RegraNegocioException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE pedidos SET status = ? WHERE id = ? AND status = ?")) {
                    stmt.setString(1, novo.name());
                    stmt.setInt(2, pedidoId);
                    stmt.setString(3, atual.name());
                    if (stmt.executeUpdate() == 0) {
                        throw new RegraNegocioException("Este pedido foi alterado por outra pessoa. Recarregue a página.");
                    }
                }

                if (novo == StatusPedido.CANCELADO) {
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE produtos p SET estoque = p.estoque + i.quantidade FROM itens_pedido i"
                                    + " WHERE i.pedido_id = ? AND i.produto_id = p.id")) {
                        stmt.setInt(1, pedidoId);
                        stmt.executeUpdate();
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO historico_pedidos"
                        + " (pedido_id, status_anterior, status_novo, administrador_id) VALUES (?, ?, ?, ?)")) {
                    stmt.setInt(1, pedidoId);
                    stmt.setString(2, atual.name());
                    stmt.setString(3, novo.name());
                    stmt.setInt(4, administradorId);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (RegraNegocioException | SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao mudar o status do pedido id=" + pedidoId, e);
        }
    }

    /**
     * O cliente cancela o próprio pedido. Só funciona se o pedido for dele e
     * ainda estiver "Aguardando pagamento" (as duas condições estão no WHERE).
     * Devolve false se não puder cancelar. As garrafas voltam ao estoque.
     */
    public boolean cancelarPeloCliente(int pedidoId, int clienteId) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE pedidos SET status = 'CANCELADO'"
                        + " WHERE id = ? AND cliente_id = ? AND status = 'AGUARDANDO_PAGAMENTO'")) {
                    stmt.setInt(1, pedidoId);
                    stmt.setInt(2, clienteId);
                    if (stmt.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE produtos p SET estoque = p.estoque + i.quantidade FROM itens_pedido i"
                                + " WHERE i.pedido_id = ? AND i.produto_id = p.id")) {
                    stmt.setInt(1, pedidoId);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO historico_pedidos"
                        + " (pedido_id, status_anterior, status_novo, pelo_cliente)"
                        + " VALUES (?, 'AGUARDANDO_PAGAMENTO', 'CANCELADO', TRUE)")) {
                    stmt.setInt(1, pedidoId);
                    stmt.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar o pedido id=" + pedidoId, e);
        }
    }

    // ================================================================== pagamento (Mercado Pago)

    /**
     * Pagamento aprovado: o pedido passa de "Aguardando pagamento" a "Pago" e
     * o histórico regista que foi o Mercado Pago.
     *
     * O "AND status = 'AGUARDANDO_PAGAMENTO'" torna a operação idempotente: o
     * Mercado Pago avisa várias vezes do mesmo pagamento (e o cliente também
     * volta pela página de retorno), mas só a primeira muda o pedido.
     *
     * @return true se o pedido mudou agora para "Pago"; false se já estava pago
     *         ou noutro status (nesse caso só os dados do pagamento são guardados)
     */
    public boolean confirmarPagamento(int pedidoId, String pagamentoId, String pagamentoStatus) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int alterados;
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE pedidos SET status = 'PAGO',"
                        + " pagamento_id = ?, pagamento_status = ?, pago_em = now()"
                        + " WHERE id = ? AND status = 'AGUARDANDO_PAGAMENTO'")) {
                    stmt.setString(1, pagamentoId);
                    stmt.setString(2, pagamentoStatus);
                    stmt.setInt(3, pedidoId);
                    alterados = stmt.executeUpdate();
                }
                if (alterados == 1) {
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO historico_pedidos"
                            + " (pedido_id, status_anterior, status_novo, pelo_gateway)"
                            + " VALUES (?, 'AGUARDANDO_PAGAMENTO', 'PAGO', TRUE)")) {
                        stmt.setInt(1, pedidoId);
                        stmt.executeUpdate();
                    }
                } else {
                    registrarPagamento(conn, pedidoId, pagamentoId, pagamentoStatus);
                }
                conn.commit();
                return alterados == 1;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao confirmar o pagamento do pedido id=" + pedidoId, e);
        }
    }

    /** Guarda o número e a situação do pagamento sem mudar o status do pedido (ex.: Pix pendente, recusado). */
    public void registrarPagamento(int pedidoId, String pagamentoId, String pagamentoStatus) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            registrarPagamento(conn, pedidoId, pagamentoId, pagamentoStatus);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar o pagamento do pedido id=" + pedidoId, e);
        }
    }

    private static void registrarPagamento(Connection conn, int pedidoId, String pagamentoId, String pagamentoStatus)
            throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE pedidos SET pagamento_id = ?, pagamento_status = ? WHERE id = ?")) {
            stmt.setString(1, pagamentoId);
            stmt.setString(2, pagamentoStatus);
            stmt.setInt(3, pedidoId);
            stmt.executeUpdate();
        }
    }

    /** Carrega numa só consulta os itens de vários pedidos (evita "N+1 consultas"). */
    private void carregarItens(Connection conn, Map<Integer, Pedido> pedidos) throws SQLException {
        String sql = "SELECT id, pedido_id, produto_id, nome_produto, imagem, preco_unitario, quantidade"
                + " FROM itens_pedido WHERE pedido_id = ANY (?) ORDER BY id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setArray(1, conn.createArrayOf("integer", pedidos.keySet().toArray()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemPedido item = new ItemPedido();
                    item.setId(rs.getInt("id"));
                    int produtoId = rs.getInt("produto_id");
                    item.setProdutoId(rs.wasNull() ? null : produtoId);
                    item.setNomeProduto(rs.getString("nome_produto"));
                    item.setImagem(rs.getString("imagem"));
                    item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    pedidos.get(rs.getInt("pedido_id")).getItens().add(item);
                }
            }
        }
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setNumero(rs.getString("numero"));
        int clienteId = rs.getInt("cliente_id");
        p.setClienteId(rs.wasNull() ? null : clienteId);
        p.setStatus(StatusPedido.valueOf(rs.getString("status")));
        p.setFormaPagamento(FormaPagamento.valueOf(rs.getString("forma_pagamento")));
        p.setParcelas(rs.getInt("parcelas"));
        p.setTipoEntrega(TipoEntrega.valueOf(rs.getString("tipo_entrega")));
        p.setSubtotal(rs.getBigDecimal("subtotal"));
        p.setFrete(rs.getBigDecimal("frete"));
        p.setDesconto(rs.getBigDecimal("desconto"));
        p.setTotal(rs.getBigDecimal("total"));
        p.setCriadoEm(rs.getObject("criado_em", OffsetDateTime.class));
        p.setPagamentoId(rs.getString("pagamento_id"));
        p.setPagamentoStatus(rs.getString("pagamento_status"));
        p.setPagoEm(rs.getObject("pago_em", OffsetDateTime.class));
        try {
            p.setEndereco(CifraDados.decifrar(CAMPO_ENDERECO, rs.getString("endereco_cifrado")));
        } catch (IllegalStateException e) {
            // Chave trocada ou dado alterado: mostra o pedido mesmo assim, sem o endereço
            LOGGER.log(Level.WARNING, "Não foi possível decifrar o endereço do pedido id=" + p.getId(), e);
            p.setEndereco("Endereço indisponível");
        }
        return p;
    }
}
