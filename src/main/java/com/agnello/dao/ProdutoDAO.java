package com.agnello.dao;

import com.agnello.connection.ConnectionFactory;


import com.agnello.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public List<Produto> listarTodos() {
    	
    	
    	
    	// Pega todos os produtos do banco de dados:
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, tipo, origem, descricao, preco, preco_antigo, desconto, pontuacao, estoque, imagem FROM produtos";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setTipo(rs.getString("tipo"));
                produto.setOrigem(rs.getString("origem"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setPrecoAntigo(rs.getDouble("preco_antigo"));
                produto.setDesconto(rs.getString("desconto"));
                produto.setPontuacao(rs.getString("pontuacao"));
                produto.setEstoque(rs.getInt("estoque"));
                produto.setImagem(rs.getString("imagem"));
                
                produtos.add(produto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos do banco de dados: " + e.getMessage(), e);
        }

        return produtos;
    }
    
    

    public Produto buscarPorId(int id) {
        String sql = "SELECT id, nome, tipo, origem, descricao, preco, preco_antigo, desconto, pontuacao, estoque, imagem FROM produtos WHERE id = ?";
        Produto produto = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produto = new Produto();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setTipo(rs.getString("tipo"));
                    produto.setOrigem(rs.getString("origem"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setPreco(rs.getDouble("preco"));
                    produto.setPrecoAntigo(rs.getDouble("preco_antigo"));
                    produto.setDesconto(rs.getString("desconto"));
                    produto.setPontuacao(rs.getString("pontuacao"));
                    produto.setEstoque(rs.getInt("estoque"));
                    produto.setImagem(rs.getString("imagem"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID: " + e.getMessage(), e);
        }
        
        
        return produto;
    }
    
    
    public List<Produto> listarUltimos4() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, tipo, origem, descricao, preco, preco_antigo, desconto, pontuacao, estoque, imagem FROM produtos ORDER BY id DESC LIMIT 4";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setTipo(rs.getString("tipo"));
                produto.setOrigem(rs.getString("origem"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setPrecoAntigo(rs.getDouble("preco_antigo"));
                produto.setDesconto(rs.getString("desconto"));
                produto.setPontuacao(rs.getString("pontuacao"));
                produto.setEstoque(rs.getInt("estoque"));
                produto.setImagem(rs.getString("imagem"));
                
                produtos.add(produto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar últimos produtos: " + e.getMessage(), e);
        }

        return produtos;
    }
   
}

