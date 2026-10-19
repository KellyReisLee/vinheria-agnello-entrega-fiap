package com.agnello.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.model.Produto;
import com.agnello.service.ProdutoService;
import com.agnello.util.RespostaJson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sugestões enquanto o cliente digita na lupa do cabeçalho:
 *   GET /busca/sugestoes?q=malb → {"sugestoes":[{"id":12,"nome":"…","tipo":"…","preco":189.90,"imagem":"…"}]}
 */
@WebServlet(name = "BuscaSugestoesController", urlPatterns = {"/busca/sugestoes"})
public class BuscaSugestoesController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(BuscaSugestoesController.class.getName());

    private final ProdutoService produtoService = new ProdutoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try {
            for (Produto p : produtoService.sugerir(request.getParameter("q"))) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", p.getId());
                m.put("nome", p.getNome());
                m.put("tipo", p.getTipo());
                m.put("preco", p.getPreco());
                m.put("imagem", request.getContextPath() + p.getImagemUrl());
                m.put("esgotado", p.getEstoque() <= 0);
                lista.add(m);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Erro nas sugestões de busca", e);
        }
        // As sugestões mudam pouco: o navegador pode reaproveitar por 1 minuto
        response.setHeader("Cache-Control", "public, max-age=60");
        RespostaJson.enviar(response, "sugestoes", lista);
    }
}
