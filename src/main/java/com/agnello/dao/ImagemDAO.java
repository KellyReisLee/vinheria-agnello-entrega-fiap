package com.agnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.agnello.connection.ConnectionFactory;

/** Fotos dos vinhos enviadas pelo painel, guardadas na tabela "imagens_produto". */
public class ImagemDAO {

    /** Uma imagem lida do banco. */
    public static final class Imagem {
        private final String tipoConteudo;
        private final byte[] dados;

        public Imagem(String tipoConteudo, byte[] dados) {
            this.tipoConteudo = tipoConteudo;
            this.dados = dados;
        }

        public String getTipoConteudo() { return tipoConteudo; }
        public byte[] getDados() { return dados; }
    }

    public int inserir(String tipoConteudo, byte[] dados) {
        String sql = "INSERT INTO imagens_produto (tipo_conteudo, dados) VALUES (?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoConteudo);
            stmt.setBytes(2, dados);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gravar imagem", e);
        }
    }

    public Imagem buscar(int id) {
        String sql = "SELECT tipo_conteudo, dados FROM imagens_produto WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? new Imagem(rs.getString(1), rs.getBytes(2)) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler imagem id=" + id, e);
        }
    }
}
