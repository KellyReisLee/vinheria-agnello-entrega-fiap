package com.agnello.controller;

import java.io.IOException;
import java.math.BigDecimal;

import com.agnello.service.FreteService;
import com.agnello.util.RespostaJson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /frete?cep=01310100&subtotal=379.80 → cotação para o checkout mostrar:
 *   {"sucesso":true,"valor":25.00,"gratis":false,"faltaParaGratis":120.20,"regiao":"…","prazo":"…"}
 * Só serve para exibir; ao gravar o pedido o servidor recalcula tudo.
 */
@WebServlet(name = "FreteController", urlPatterns = {"/frete"})
public class FreteController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        BigDecimal subtotal = BigDecimal.ZERO;
        try {
            String s = request.getParameter("subtotal");
            if (s != null && !s.isBlank()) {
                subtotal = new BigDecimal(s.trim()).max(BigDecimal.ZERO).min(new BigDecimal("1000000"));
            }
        } catch (NumberFormatException e) {
            subtotal = BigDecimal.ZERO;
        }

        FreteService.Cotacao c = FreteService.cotar(request.getParameter("cep"), subtotal);
        if (c == null) {
            response.setStatus(422);
            RespostaJson.enviar(response, "sucesso", false, "mensagem", "Informe um CEP com 8 dígitos.");
            return;
        }
        RespostaJson.enviar(response, "sucesso", true, "valor", c.getValor(), "gratis", c.isGratis(),
                "faltaParaGratis", c.getFaltaParaGratis(), "gratisAPartirDe", FreteService.GRATIS_A_PARTIR_DE,
                "regiao", c.getRegiao().getNome(), "prazo", c.getRegiao().getPrazo());
    }
}
