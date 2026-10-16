package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.connection.ConnectionFactory;
import com.agnello.exception.EstoqueInsuficienteException;
import com.agnello.model.FormaPagamento;
import com.agnello.model.ItemPedido;
import com.agnello.model.Pedido;
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
                + " subtotal, frete, desconto, total, endereco_cifrado, criado_em"
                + " FROM pedidos WHERE cliente_id = ? ORDER BY criado_em DESC, id DESC";
        String sqlItens = "SELECT id, pedido_id, produto_id, nome_produto, imagem, preco_unitario, quantidade"
                + " FROM itens_pedido WHERE pedido_id = ANY (?) ORDER BY id";

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

            // Uma só consulta para os itens de todos os pedidos (evita "N+1 consultas")
            try (PreparedStatement stmt = conn.prepareStatement(sqlItens)) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar os pedidos do cliente id=" + clienteId, e);
        }
        return new ArrayList<>(pedidos.values());
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
