package com.agnello.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import com.agnello.model.FormaPagamento;
import com.agnello.model.ItemPedido;
import com.agnello.model.Pedido;
import com.agnello.model.TipoEntrega;

public class EmailTemplateUtil {

    public static String getCorpoRecuperacaoSenha(String linkRedefinicao) {
        return 
            "<div style=\"background-color: #f7f5f0; padding: 50px 0; font-family: Arial, sans-serif; color: #333333;\">" +
            "  <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"background-color: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #e5e0dc; box-shadow: 0 6px 18px rgba(74,21,37,0.06);\">" +
            "    <!-- Cabeçalho com Identidade Visual -->" +
            "    <tr>" +
            "      <td style=\"background-color: #4A1525; padding: 35px 30px; text-align: center;\">" +
            "        <span style=\"font-size: 11px; text-transform: uppercase; letter-spacing: 3px; color: #c5a059; display: block; margin-bottom: 10px; font-weight: bold;\">Clube Agnello &bull; Adega Digital</span>" +
            "        <h1 style=\"color: #ffffff; margin: 0; font-size: 26px; font-weight: normal; font-family: Georgia, serif;\">Vinheria Agnello</h1>" +
            "      </td>" +
            "    </tr>" +
            "    <!-- Corpo da Mensagem -->" +
            "    <tr>" +
            "      <td style=\"padding: 45px 35px;\">" +
            "        <h2 style=\"color: #4A1525; font-size: 21px; margin-top: 0; margin-bottom: 20px; font-family: Georgia, serif;\">Redefinição de senha</h2>" +
            "        <p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin-bottom: 20px;\">Recebemos um pedido para redefinir a senha associada à sua conta na Vinheria Agnello.</p>" +
            "        <p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin-bottom: 30px;\">Para criar uma nova credencial de forma segura, clique no botão abaixo. Por razões de segurança, este link é válido por apenas <strong>10 minutos</strong>:</p>" +
            "        <!-- Botão Principal Centralizado -->" +
            "        <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin: 35px auto;\">" +
            "          <tr>" +
            "            <td align=\"center\" style=\"border-radius: 6px; background-color: #4A1525;\">" +
            "              <a href=\"" + linkRedefinicao + "\" target=\"_blank\" style=\"font-size: 15px; font-family: Arial, sans-serif; color: #ffffff; text-decoration: none; padding: 14px 32px; border-radius: 6px; border: 1px solid #4A1525; display: inline-block; font-weight: bold; letter-spacing: 0.5px;\">Redefinir Minha Senha</a>" +
            "            </td>" +
            "          </tr>" +
            "        </table>" +
            "        <!-- Box de Observação / Aviso de Spam Refinado -->" +
            "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color: #fdfbf7; border: 1px solid #e8e1d5; border-left: 4px solid #c5a059; border-radius: 4px; margin: 30px 0 25px 0;\">" +
            "          <tr>" +
            "            <td style=\"padding: 16px 18px; font-size: 13px; line-height: 1.6; color: #665555; font-family: Arial, sans-serif;\">" +
            "              <strong style=\"color: #4A1525;\">Dica importante:</strong> Se não encontrar esta mensagem na sua caixa de entrada principal, por favor verifique também a pasta de <strong style=\"color: #4A1525;\">Spam</strong> ou <strong style=\"color: #4A1525;\">Lixo Eletrônico</strong>." +
            "            </td>" +
            "          </tr>" +
            "        </table>" +
            "        <p style=\"font-size: 13px; line-height: 1.6; color: #888888; border-top: 1px solid #eeeeee; margin-top: 35px; padding-top: 25px;\">Se não solicitou esta alteração, pode ignorar este e-mail em total segurança. A sua conta continuará protegida.</p>" +
            "      </td>" +
            "    </tr>" +
            "    <!-- Rodapé -->" +
            "    <tr>" +
            "      <td style=\"background-color: #f9f9f9; padding: 25px 35px; text-align: center; border-top: 1px solid #eeeeee;\">" +
            "        <p style=\"font-size: 12px; color: #999999; margin: 0; font-family: Arial, sans-serif;\">&copy; 2026 Vinheria Agnello. Todos os direitos reservados.</p>" +
            "      </td>" +
            "    </tr>" +
            "  </table>" +
            "</div>";
    }
    
    public static String getCorpoAtivacaoConta(String linkAtivacao) {
        return 
            "<div style=\"background-color: #f7f5f0; padding: 50px 0; font-family: Arial, sans-serif; color: #333333;\">" +
            "  <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"background-color: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #e5e0dc; box-shadow: 0 6px 18px rgba(74,21,37,0.06);\">" +
            "    <!-- Cabeçalho com Identidade Visual -->" +
            "    <tr>" +
            "      <td style=\"background-color: #4A1525; padding: 35px 30px; text-align: center;\">" +
            "        <span style=\"font-size: 11px; text-transform: uppercase; letter-spacing: 3px; color: #c5a059; display: block; margin-bottom: 10px; font-weight: bold;\">Clube Agnello &bull; Adega Digital</span>" +
            "        <h1 style=\"color: #ffffff; margin: 0; font-size: 26px; font-weight: normal; font-family: Georgia, serif;\">Vinheria Agnello</h1>" +
            "      </td>" +
            "    </tr>" +
            "    <!-- Corpo da Mensagem -->" +
            "    <tr>" +
            "      <td style=\"padding: 45px 35px;\">" +
            "        <h2 style=\"color: #4A1525; font-size: 21px; margin-top: 0; margin-bottom: 20px; font-family: Georgia, serif;\">Bem-vindo(a) à nossa Adega!</h2>" +
            "        <p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin-bottom: 20px;\">Olá, ficamos imensamente felizes pelo seu cadastro na <strong>Vinheria Agnello</strong>. Para garantir a segurança da sua conta e concluir o seu acesso, por favor, confirme o seu endereço de e-mail.</p>" +
            "        <p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin-bottom: 30px;\">Para ativar a sua conta de forma segura, clique no botão abaixo. O link é válido por <strong>48 horas</strong>:</p>" +
            "        <!-- Botão Principal Centralizado -->" +
            "        <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin: 35px auto;\">" +
            "          <tr>" +
            "            <td align=\"center\" style=\"border-radius: 6px; background-color: #4A1525;\">" +
            "              <a href=\"" + linkAtivacao + "\" target=\"_blank\" style=\"font-size: 15px; font-family: Arial, sans-serif; color: #ffffff; text-decoration: none; padding: 14px 32px; border-radius: 6px; border: 1px solid #4A1525; display: inline-block; font-weight: bold; letter-spacing: 0.5px;\">Ativar a Minha Conta</a>" +
            "            </td>" +
            "          </tr>" +
            "        </table>" +
            "        <!-- Box de Link Alternativo -->" +
            "        <p style=\"font-size: 13px; line-height: 1.6; color: #777777; margin-top: 25px;\">" +
            "          Se o botão acima não funcionar, copie e cole o link abaixo diretamente no seu browser:<br>" +
            "          <a href=\"" + linkAtivacao + "\" style=\"color: #4A1525; word-break: break-all; font-size: 12px;\">" + linkAtivacao + "</a>" +
            "        </p>" +
            "        <!-- Box de Observação / Aviso de Spam Refinado -->" +
            "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color: #fdfbf7; border: 1px solid #e8e1d5; border-left: 4px solid #c5a059; border-radius: 4px; margin: 30px 0 25px 0;\">" +
            "          <tr>" +
            "            <td style=\"padding: 16px 18px; font-size: 13px; line-height: 1.6; color: #665555; font-family: Arial, sans-serif;\">" +
            "              <strong style=\"color: #4A1525;\">Dica importante:</strong> Se não encontrar esta mensagem na sua caixa de entrada principal, por favor verifique também a pasta de <strong style=\"color: #4A1525;\">Spam</strong> ou <strong style=\"color: #4A1525;\">Lixo Eletrônico</strong>." +
            "            </td>" +
            "          </tr>" +
            "        </table>" +
            "        <p style=\"font-size: 13px; line-height: 1.6; color: #888888; border-top: 1px solid #eeeeee; margin-top: 35px; padding-top: 25px;\">Caso não tenha efetuado este cadastro, pode ignorar esta mensagem em total segurança.</p>" +
            "      </td>" +
            "    </tr>" +
            "    <!-- Rodapé -->" +
            "    <tr>" +
            "      <td style=\"background-color: #f9f9f9; padding: 25px 35px; text-align: center; border-top: 1px solid #eeeeee;\">" +
            "        <p style=\"font-size: 12px; color: #999999; margin: 0; font-family: Arial, sans-serif;\">&copy; 2026 Vinheria Agnello. Todos os direitos reservados.</p>" +
            "      </td>" +
            "    </tr>" +
            "  </table>" +
            "</div>";
    }

    // ===================================================================== pedidos e contato

    private static final String CABECALHO =
            "<div style=\"background-color: #f7f5f0; padding: 50px 0; font-family: Arial, sans-serif; color: #333333;\">"
          + "  <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"background-color: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #e5e0dc;\">"
          + "    <tr><td style=\"background-color: #4A1525; padding: 35px 30px; text-align: center;\">"
          + "      <span style=\"font-size: 11px; text-transform: uppercase; letter-spacing: 3px; color: #c5a059; display: block; margin-bottom: 10px; font-weight: bold;\">Clube Agnello &bull; Adega Digital</span>"
          + "      <h1 style=\"color: #ffffff; margin: 0; font-size: 26px; font-weight: normal; font-family: Georgia, serif;\">Vinheria Agnello</h1>"
          + "    </td></tr>"
          + "    <tr><td style=\"padding: 40px 35px;\">";

    private static final String RODAPE =
            "    </td></tr>"
          + "    <tr><td style=\"background-color: #f9f9f9; padding: 25px 35px; text-align: center; border-top: 1px solid #eeeeee;\">"
          + "      <p style=\"font-size: 12px; color: #999999; margin: 0;\">Venda proibida para menores de 18 anos. Beba com moderação.<br>&copy; 2026 Vinheria Agnello.</p>"
          + "    </td></tr>"
          + "  </table>"
          + "</div>";

    /** E-mail enviado ao cliente logo depois de concluir a compra. */
    public static String getCorpoConfirmacaoPedido(Pedido pedido, String nomeCliente) {
        StringBuilder html = new StringBuilder(CABECALHO);
        html.append("<h2 style=\"color: #4A1525; font-size: 21px; margin: 0 0 8px; font-family: Georgia, serif;\">Obrigado pela sua compra!</h2>")
            .append("<p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 24px;\">Olá, ")
            .append(escapar(nomeCliente)).append(". Recebemos o seu pedido <strong>")
            .append(escapar(pedido.getNumero())).append("</strong>.</p>");

        html.append("<p style=\"font-size: 14px; line-height: 1.6; color: #4A1525; background: #fdfbf7; border-left: 4px solid #c5a059; padding: 12px 16px; margin: 0 0 24px;\">")
            .append(instrucaoPagamento(pedido)).append("</p>");

        html.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size: 14px; color: #333333; border-collapse: collapse;\">");
        for (ItemPedido item : pedido.getItens()) {
            html.append("<tr><td style=\"padding: 8px 0; border-bottom: 1px solid #eeeeee;\">")
                .append(item.getQuantidade()).append("&times; ").append(escapar(item.getNomeProduto()))
                .append("</td><td align=\"right\" style=\"padding: 8px 0; border-bottom: 1px solid #eeeeee;\">")
                .append(reais(item.getTotal())).append("</td></tr>");
        }
        linhaValor(html, "Subtotal", reais(pedido.getSubtotal()), false);
        linhaValor(html, pedido.getTipoEntrega().getDescricao(),
                pedido.getFrete().signum() == 0 ? "Grátis" : reais(pedido.getFrete()), false);
        if (pedido.getDesconto().signum() > 0) {
            linhaValor(html, "Desconto Pix", "&minus; " + reais(pedido.getDesconto()), false);
        }
        linhaValor(html, "Total", reais(pedido.getTotal()), true);
        html.append("</table>");

        String entrega = pedido.getTipoEntrega() == TipoEntrega.RETIRADA
                ? "Retirada na adega: Rua das Videiras, 1978 &mdash; Jardins, São Paulo. Leve um documento com foto."
                : "Entrega em: " + escapar(pedido.getEndereco());
        html.append("<p style=\"font-size: 13px; line-height: 1.6; color: #777777; margin: 24px 0 0;\">")
            .append(entrega).append("</p>")
            .append("<p style=\"font-size: 13px; line-height: 1.6; color: #777777; margin: 12px 0 0;\">")
            .append("Acompanhe o pedido em <strong>Minha conta &rsaquo; Meus pedidos</strong>.</p>");
        html.append(RODAPE);
        return html.toString();
    }

    private static String instrucaoPagamento(Pedido pedido) {
        if (pedido.getFormaPagamento() == FormaPagamento.CARTAO) {
            return "Pagamento aprovado no cartão de crédito em " + pedido.getParcelas() + "x de "
                    + reais(pedido.getValorParcela()) + " sem juros.";
        }
        if (pedido.getFormaPagamento() == FormaPagamento.PIX) {
            return "Pagamento por Pix: o pedido é liberado assim que o pagamento for confirmado.";
        }
        return "Pagamento por boleto: vence em 2 dias úteis. O pedido é liberado após a compensação.";
    }

    private static void linhaValor(StringBuilder html, String rotulo, String valor, boolean destaque) {
        String estilo = destaque ? "font-weight: bold; color: #4A1525; font-size: 16px;" : "color: #666666;";
        html.append("<tr><td style=\"padding: 8px 0; ").append(estilo).append("\">").append(rotulo)
            .append("</td><td align=\"right\" style=\"padding: 8px 0; ").append(estilo).append("\">")
            .append(valor).append("</td></tr>");
    }

    /** E-mail interno para a loja quando alguém usa o formulário de contato. */
    public static String getCorpoMensagemContato(String nome, String email, String assunto, String mensagem) {
        return CABECALHO
                + "<h2 style=\"color: #4A1525; font-size: 20px; margin: 0 0 16px; font-family: Georgia, serif;\">Nova mensagem pelo site</h2>"
                + "<p style=\"font-size: 14px; line-height: 1.6; margin: 0 0 6px;\"><strong>Nome:</strong> " + escapar(nome) + "</p>"
                + "<p style=\"font-size: 14px; line-height: 1.6; margin: 0 0 6px;\"><strong>E-mail:</strong> " + escapar(email) + "</p>"
                + "<p style=\"font-size: 14px; line-height: 1.6; margin: 0 0 18px;\"><strong>Assunto:</strong> " + escapar(assunto) + "</p>"
                + "<p style=\"font-size: 14px; line-height: 1.7; white-space: pre-wrap; background: #fdfbf7; padding: 14px 16px; border-left: 4px solid #c5a059;\">"
                + escapar(mensagem) + "</p>"
                + "<p style=\"font-size: 12px; color: #999999;\">Responda diretamente para o e-mail do cliente.</p>"
                + RODAPE;
    }

    private static String reais(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor).replace('\u00a0', ' ');
    }

    /** Evita que um nome como "<script>" vire HTML dentro do e-mail. */
    static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
