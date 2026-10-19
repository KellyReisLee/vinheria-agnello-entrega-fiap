package com.agnello.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.model.Usuario;
import com.agnello.service.EnderecoService;
import com.agnello.service.FavoritoService;
import com.agnello.service.PedidoService;
import com.agnello.util.MensagemFlash;
import com.agnello.util.SessaoCliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Área pessoal: visão geral, pedidos, favoritos, endereços e dados da conta. */
@WebServlet(name = "AreaPessoalController", urlPatterns = {"/area-pessoal"})
public class AreaPessoalController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AreaPessoalController.class.getName());

    private final PedidoService pedidoService = new PedidoService();
    private final FavoritoService favoritoService = new FavoritoService();
    private final EnderecoService enderecoService = new EnderecoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Página com dados pessoais: o navegador não deve guardar cópia em cache
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Usuario cliente = SessaoCliente.atual(request);
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        MensagemFlash.transferir(request);
        request.setAttribute("csrf", SessaoCliente.tokenCsrf(request));

        // Cada bloco carrega separado: se um falhar, o resto da página abre na mesma
        try {
            request.setAttribute("pedidos", pedidoService.listarDoCliente(cliente));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar os pedidos do cliente id=" + cliente.getId(), e);
            request.setAttribute("erroPedidos", true);
        }
        try {
            request.setAttribute("favoritos", favoritoService.listar(cliente));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar os favoritos do cliente id=" + cliente.getId(), e);
            request.setAttribute("erroFavoritos", true);
        }
        try {
            request.setAttribute("enderecos", enderecoService.listar(cliente));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar os endereços do cliente id=" + cliente.getId(), e);
            request.setAttribute("erroEnderecos", true);
        }
        request.setAttribute("maxEnderecos", EnderecoService.MAX_ENDERECOS);
        request.getRequestDispatcher("/WEB-INF/views/area-pessoal.jsp").forward(request, response);
    }
}
