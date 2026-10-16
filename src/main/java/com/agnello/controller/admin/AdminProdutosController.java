package com.agnello.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Produto;
import com.agnello.service.FormularioProduto;
import com.agnello.service.ProdutoAdminService;
import com.agnello.util.MensagemFlash;
import com.agnello.util.Parametros;
import com.agnello.util.SessaoAdmin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * Vinhos no painel.
 *   GET  /admin/produtos?busca=&mostrar=ativos|inativos  → lista
 *   GET  /admin/produtos/novo                             → formulário vazio
 *   GET  /admin/produtos/editar?id=12                     → formulário preenchido
 *   POST /admin/produtos/salvar                           → cria ou atualiza (com foto)
 *   POST /admin/produtos/ativo                            → ativa / desativa
 *   POST /admin/produtos/estoque                          → ajuste rápido de estoque
 *
 * @MultipartConfig permite receber arquivos (enctype="multipart/form-data").
 * O limite aqui (5 MB) é só uma proteção do servidor; a regra da loja (2 MB)
 * é conferida no ProdutoAdminService, com uma mensagem amigável.
 */
@WebServlet(name = "AdminProdutosController", urlPatterns = {"/admin/produtos", "/admin/produtos/novo",
        "/admin/produtos/editar", "/admin/produtos/salvar", "/admin/produtos/ativo", "/admin/produtos/estoque"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class AdminProdutosController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminProdutosController.class.getName());
    private static final String VIEW_FORM = "/WEB-INF/views/admin/produto-form.jsp";

    private final ProdutoAdminService service = new ProdutoAdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("secaoAdmin", "produtos");
        String rota = request.getServletPath();

        if (rota.endsWith("/novo")) {
            mostrarFormulario(request, response, new FormularioProduto());
        } else if (rota.endsWith("/editar")) {
            Produto p = service.buscar(Parametros.inteiro(request.getParameter("id"), -1));
            if (p == null) {
                MensagemFlash.erro(request, "Vinho não encontrado.");
                response.sendRedirect(request.getContextPath() + "/admin/produtos");
                return;
            }
            mostrarFormulario(request, response, FormularioProduto.de(p));
        } else {
            String mostrar = request.getParameter("mostrar");
            Boolean ativos = "ativos".equals(mostrar) ? Boolean.TRUE : "inativos".equals(mostrar) ? Boolean.FALSE : null;
            request.setAttribute("produtos", service.listar(request.getParameter("busca"), ativos));
            request.setAttribute("busca", request.getParameter("busca"));
            request.setAttribute("mostrar", mostrar == null ? "todos" : mostrar);
            request.setAttribute("limiteEstoqueBaixo", ProdutoAdminService.LIMITE_ESTOQUE_BAIXO);
            request.getRequestDispatcher("/WEB-INF/views/admin/produtos.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String rota = request.getServletPath();
        if (rota.endsWith("/salvar")) {
            salvar(request, response);
        } else if (rota.endsWith("/ativo")) {
            alterarAtivo(request, response);
        } else if (rota.endsWith("/estoque")) {
            alterarEstoque(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FormularioProduto form = new FormularioProduto();
        int id = Parametros.inteiro(request.getParameter("id"), -1);
        form.setId(id > 0 ? id : null);
        form.setNome(request.getParameter("nome"));
        form.setTipo(request.getParameter("tipo"));
        form.setOrigem(request.getParameter("origem"));
        form.setDescricao(request.getParameter("descricao"));
        form.setPreco(request.getParameter("preco"));
        form.setPrecoAntigo(request.getParameter("precoAntigo"));
        form.setPontuacao(request.getParameter("pontuacao"));
        form.setEstoque(request.getParameter("estoque"));
        form.setImagemAtual(request.getParameter("imagemAtual"));
        form.setAtivo(request.getParameter("ativo") != null);

        try {
            int salvo = service.salvar(SessaoAdmin.atual(request), form, lerImagem(request));
            MensagemFlash.sucesso(request, form.isNovo() ? "Vinho cadastrado." : "Alterações salvas.");
            response.sendRedirect(request.getContextPath() + "/admin/produtos/editar?id=" + salvo);
        } catch (RegraNegocioException e) {
            // Volta ao formulário com o que foi digitado e a mensagem de erro
            response.setStatus(422);
            request.setAttribute("erro", e.getMessage());
            request.setAttribute("secaoAdmin", "produtos");
            request.setAttribute("csrf", SessaoAdmin.tokenCsrf(request));
            mostrarFormulario(request, response, form);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar produto", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("erro", "Não foi possível salvar agora. Tente novamente.");
            request.setAttribute("secaoAdmin", "produtos");
            request.setAttribute("csrf", SessaoAdmin.tokenCsrf(request));
            mostrarFormulario(request, response, form);
        }
    }

    /** Bytes da foto enviada, ou null se nenhuma foi escolhida. */
    private static byte[] lerImagem(HttpServletRequest request) throws IOException, ServletException {
        Part parte = request.getPart("imagem");
        if (parte == null || parte.getSize() == 0) {
            return null;
        }
        try (InputStream in = parte.getInputStream()) {
            return in.readAllBytes();
        }
    }

    private void alterarAtivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Parametros.inteiro(request.getParameter("id"), -1);
        boolean ativo = "true".equals(request.getParameter("ativo"));
        try {
            service.definirAtivo(SessaoAdmin.atual(request), id, ativo);
            MensagemFlash.sucesso(request, ativo ? "Vinho reativado: voltou a aparecer na loja."
                    : "Vinho desativado: não aparece mais na loja (os pedidos antigos continuam iguais).");
        } catch (RegraNegocioException e) {
            MensagemFlash.erro(request, e.getMessage());
        }
        voltar(request, response);
    }

    private void alterarEstoque(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Parametros.inteiro(request.getParameter("id"), -1);
        try {
            service.definirEstoque(SessaoAdmin.atual(request), id, request.getParameter("estoque"));
            MensagemFlash.sucesso(request, "Estoque atualizado.");
        } catch (RegraNegocioException e) {
            MensagemFlash.erro(request, e.getMessage());
        }
        voltar(request, response);
    }

    /** Volta à página de onde veio o formulário (lista ou edição). */
    private static void voltar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String voltar = request.getParameter("voltar");
        // Só aceita caminhos do próprio painel (evita "open redirect" para outro site)
        if (voltar == null || !voltar.startsWith("/admin/") || voltar.contains("//")) {
            voltar = "/admin/produtos";
        }
        response.sendRedirect(request.getContextPath() + voltar);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, FormularioProduto form)
            throws ServletException, IOException {
        request.setAttribute("form", form);
        try {
            request.setAttribute("tipos", service.listarTipos());
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Não foi possível carregar os tipos de vinho", e);
        }
        request.getRequestDispatcher(VIEW_FORM).forward(request, response);
    }
}
