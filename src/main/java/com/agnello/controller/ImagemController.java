package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.dao.ImagemDAO;
import com.agnello.util.Parametros;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Entrega as fotos enviadas pelo painel: /imagens/12
 *
 * Uma imagem nunca muda depois de gravada (trocar a foto cria uma imagem nova,
 * com outro número). Por isso o navegador pode guardá-la em cache por um ano
 * ("immutable") e o banco só é consultado na primeira visita.
 */
@WebServlet(name = "ImagemController", urlPatterns = {"/imagens/*"})
public class ImagemController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ImagemController.class.getName());

    private final ImagemDAO imagemDAO = new ImagemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String caminho = request.getPathInfo(); // "/12"
        int id = Parametros.inteiro(caminho == null ? null : caminho.substring(1), -1);
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String etag = "\"img-" + id + "\"";
        if (etag.equals(request.getHeader("If-None-Match"))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        try {
            ImagemDAO.Imagem imagem = imagemDAO.buscar(id);
            if (imagem == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setContentType(imagem.getTipoConteudo());
            response.setContentLength(imagem.getDados().length);
            response.setHeader("Cache-Control", "public, max-age=31536000, immutable");
            response.setHeader("ETag", etag);
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.getOutputStream().write(imagem.getDados());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao entregar a imagem id=" + id, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
