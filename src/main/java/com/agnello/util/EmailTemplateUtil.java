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
            return "Pagamento no cartão de crédito em " + pedido.getParcelas() + "x de "
                    + reais(pedido.getValorParcela()) + " sem juros, na página segura do Mercado Pago. "
                    + "Avisamos por e-mail assim que for aprovado.";
        }
        if (pedido.getFormaPagamento() == FormaPagamento.PIX) {
            return "Pagamento por Pix, na página segura do Mercado Pago: o pedido é liberado assim que o "
                    + "pagamento for confirmado (em geral, em segundos).";
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

    /** E-mail ao cliente quando a loja muda o status do pedido no painel. */
    public static String getCorpoMudancaStatus(Pedido pedido, String nomeCliente) {
        String explicacao;
        switch (pedido.getStatus()) {
            case PAGO:
                explicacao = "Recebemos o seu pagamento. Já vamos preparar as suas garrafas.";
                break;
            case EM_SEPARACAO:
                explicacao = "As suas garrafas estão sendo separadas e embaladas com proteção térmica.";
                break;
            case ENVIADO:
                explicacao = "O seu pedido saiu para entrega e chega em até 48 horas.";
                break;
            case PRONTO_RETIRADA:
                explicacao = "O seu pedido está pronto! Retire na Rua das Videiras, 1978 &mdash; Jardins, São Paulo, "
                        + "de segunda a sábado, das 10h às 20h. Leve um documento com foto.";
                break;
            case ENTREGUE:
                explicacao = "O pedido foi entregue. Esperamos que aprecie! Obrigado por escolher a Vinheria Agnello.";
                break;
            case CANCELADO:
                explicacao = "O seu pedido foi cancelado. Se já tiver pago, o valor será devolvido pela mesma forma "
                        + "de pagamento. Em caso de dúvida, responda a este e-mail.";
                break;
            default:
                explicacao = "O status do seu pedido foi atualizado.";
        }
        return CABECALHO
                + "<h2 style=\"color: #4A1525; font-size: 21px; margin: 0 0 8px; font-family: Georgia, serif;\">"
                + escapar(pedido.getStatus().getDescricao()) + "</h2>"
                + "<p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 18px;\">Olá"
                + (nomeCliente == null || nomeCliente.isBlank() ? "" : ", " + escapar(nomeCliente))
                + ". Há novidades no seu pedido <strong>" + escapar(pedido.getNumero()) + "</strong>.</p>"
                + "<p style=\"font-size: 15px; line-height: 1.7; color: #4A1525; background: #fdfbf7; border-left: 4px solid #c5a059; padding: 12px 16px; margin: 0 0 18px;\">"
                + explicacao + "</p>"
                + "<p style=\"font-size: 13px; line-height: 1.6; color: #777777; margin: 0;\">Total do pedido: <strong>"
                + reais(pedido.getTotal()) + "</strong>. Acompanhe em <strong>Minha conta &rsaquo; Meus pedidos</strong>.</p>"
                + RODAPE;
    }

    /** Resposta da loja a uma mensagem do formulário de contato. */
    public static String getCorpoRespostaContato(String nomeCliente, String mensagemOriginal, String resposta,
            String nomeAtendente) {
        return CABECALHO
                + "<p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 18px;\">Olá, "
                + escapar(nomeCliente) + ".</p>"
                + "<div style=\"font-size: 15px; line-height: 1.7; color: #333333; white-space: pre-wrap; margin: 0 0 22px;\">"
                + escapar(resposta) + "</div>"
                + "<p style=\"font-size: 14px; color: #555555; margin: 0 0 24px;\">"
                + escapar(nomeAtendente) + "<br><span style=\"color: #999999;\">Equipe Vinheria Agnello</span></p>"
                + "<p style=\"font-size: 12px; line-height: 1.6; color: #999999; border-top: 1px solid #eeeeee; padding-top: 14px; margin: 0;\">"
                + "A sua mensagem:<br><span style=\"white-space: pre-wrap;\">" + escapar(mensagemOriginal) + "</span></p>"
                + RODAPE;
    }

    /** Aviso de segurança depois da troca de senha na área pessoal. */
    public static String getCorpoSenhaAlterada(String nomeCliente) {
        return CABECALHO
                + "<h2 style=\"color: #4A1525; font-size: 21px; margin: 0 0 16px; font-family: Georgia, serif;\">Senha alterada</h2>"
                + "<p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 18px;\">Olá, " + escapar(nomeCliente)
                + ". A senha da sua conta na Vinheria Agnello acabou de ser alterada na área pessoal.</p>"
                + "<p style=\"font-size: 14px; line-height: 1.6; color: #4A1525; background: #fdfbf7; border-left: 4px solid #c5a059; padding: 12px 16px; margin: 0;\">"
                + "<strong>Não foi você?</strong> Use \"Esqueci a senha\" na página de login para criar uma nova "
                + "imediatamente e responda a este e-mail para avisar a nossa equipe.</p>"
                + RODAPE;
    }

    // ===================================================================== calculadora de eventos

    /** Quadro com as garrafas por tipo (usado nos dois e-mails de orçamento). */
    private static String quadroGarrafas(com.agnello.model.OrcamentoEvento o) {
        return "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 0 0 24px; border-collapse: collapse;\">"
                + "<tr><td colspan=\"3\" style=\"background: #4A1525; border-radius: 8px 8px 0 0; padding: 18px 20px; text-align: center;\">"
                + "<span style=\"display: block; font-size: 11px; letter-spacing: 2px; text-transform: uppercase; color: #c5a059; font-weight: bold;\">Recomendação do sommelier</span>"
                + "<span style=\"display: block; font-family: Georgia, serif; font-size: 34px; color: #ffffff; margin-top: 6px;\">"
                + o.getGarrafas() + " <span style=\"font-size: 16px;\">" + (o.getGarrafas() == 1 ? "garrafa" : "garrafas") + "</span></span>"
                + "<span style=\"display: block; font-size: 12px; color: #e8d9c0; margin-top: 4px;\">de 750 ml</span></td></tr>"
                + "<tr>"
                + celulaGarrafa("Tintos", o.getTintos(), "#7A1F36")
                + celulaGarrafa("Brancos", o.getBrancos(), "#B08A2E")
                + celulaGarrafa("Espumantes", o.getEspumantes(), "#4F6B4A")
                + "</tr></table>";
    }

    private static String celulaGarrafa(String rotulo, int quantidade, String cor) {
        return "<td width=\"33%\" style=\"background: #fdfbf7; border: 1px solid #efe7da; padding: 14px 8px; text-align: center;\">"
                + "<span style=\"display: block; font-family: Georgia, serif; font-size: 24px; color: " + cor + "; font-weight: bold;\">" + quantidade + "</span>"
                + "<span style=\"display: block; font-size: 12px; color: #777777; text-transform: uppercase; letter-spacing: 1px;\">" + rotulo + "</span></td>";
    }

    private static String linhaDado(String rotulo, String valorHtml) {
        return "<tr><td style=\"padding: 9px 0; width: 38%; color: #888888; font-size: 13px; border-bottom: 1px solid #f0ebe3; vertical-align: top;\">"
                + rotulo + "</td><td style=\"padding: 9px 0; color: #333333; font-size: 14px; border-bottom: 1px solid #f0ebe3;\">"
                + valorHtml + "</td></tr>";
    }

    private static String descricaoEvento(com.agnello.model.OrcamentoEvento o) {
        return escapar(o.getEstilo().getNome()) + " &middot; " + o.getConvidados()
                + (o.getConvidados() == 1 ? " convidado" : " convidados") + " &middot; " + o.getDuracaoHoras()
                + (o.getDuracaoHoras() == 1 ? " hora" : " horas");
    }

    /** E-mail interno: novo pedido de orçamento da Calculadora de Eventos. */
    public static String getCorpoOrcamentoLoja(com.agnello.model.OrcamentoEvento o, int numero) {
        StringBuilder html = new StringBuilder(CABECALHO);
        html.append("<span style=\"display: inline-block; background: #fdf3dc; color: #8a6100; font-size: 11px; font-weight: bold; letter-spacing: 1px; text-transform: uppercase; padding: 5px 10px; border-radius: 999px; margin: 0 0 12px;\">Orçamento #")
            .append(numero).append("</span>")
            .append("<h2 style=\"color: #4A1525; font-size: 22px; margin: 0 0 6px; font-family: Georgia, serif;\">Novo pedido de orçamento</h2>")
            .append("<p style=\"font-size: 15px; line-height: 1.6; color: #555555; margin: 0 0 22px;\">Enviado pela Calculadora de Eventos do site. ")
            .append("<strong>Responda a este e-mail</strong> para falar direto com o cliente.</p>");

        html.append("<h3 style=\"font-size: 13px; letter-spacing: 1px; text-transform: uppercase; color: #c5a059; margin: 0 0 8px;\">Cliente</h3>")
            .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: collapse; margin: 0 0 24px;\">")
            .append(linhaDado("Nome", "<strong>" + escapar(o.getNome()) + "</strong>"))
            .append(linhaDado("E-mail", "<a href=\"mailto:" + escapar(o.getEmail()) + "\" style=\"color: #4A1525;\">"
                    + escapar(o.getEmail()) + "</a>"));
        if (o.getTelefone() != null) {
            html.append(linhaDado("WhatsApp / Telefone", "<a href=\"https://wa.me/55" + o.getTelefone()
                    + "\" style=\"color: #4A1525;\">" + com.agnello.service.OrcamentoService.formatarTelefone(o.getTelefone())
                    + "</a>"));
        }
        if (o.getOrcamentoEstimado() != null) {
            html.append(linhaDado("Orçamento estimado", escapar(o.getOrcamentoEstimado())));
        }
        html.append(linhaDado("Conta no site", o.getClienteId() == null ? "Não (visitante)" : "Sim (cliente #" + o.getClienteId() + ")"))
            .append("</table>");

        html.append("<h3 style=\"font-size: 13px; letter-spacing: 1px; text-transform: uppercase; color: #c5a059; margin: 0 0 8px;\">Evento</h3>")
            .append("<p style=\"font-size: 15px; color: #333333; margin: 0 0 6px;\"><strong>").append(descricaoEvento(o)).append("</strong></p>")
            .append("<p style=\"font-size: 13px; line-height: 1.6; color: #777777; margin: 0 0 16px;\">")
            .append(escapar(o.getEstilo().getDescricao())).append("</p>")
            .append(quadroGarrafas(o));

        if (o.getObservacoes() != null) {
            html.append("<h3 style=\"font-size: 13px; letter-spacing: 1px; text-transform: uppercase; color: #c5a059; margin: 0 0 8px;\">Detalhes do cliente</h3>")
                .append("<p style=\"font-size: 14px; line-height: 1.7; color: #333333; white-space: pre-wrap; background: #fdfbf7; padding: 14px 16px; border-left: 4px solid #c5a059; margin: 0 0 24px;\">")
                .append(escapar(o.getObservacoes())).append("</p>");
        }
        html.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 0 auto;\"><tr><td style=\"background: #4A1525; border-radius: 6px;\">")
            .append("<a href=\"mailto:").append(escapar(o.getEmail())).append("?subject=")
            .append(java.net.URLEncoder.encode("Orçamento do seu evento - Vinheria Agnello", java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20"))
            .append("\" style=\"display: inline-block; padding: 13px 26px; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 14px;\">Responder ao cliente</a>")
            .append("</td></tr></table>")
            .append("<p style=\"font-size: 12px; color: #999999; text-align: center; margin: 14px 0 0;\">O pedido também está no painel, em <strong>Mensagens</strong>.</p>");
        html.append(RODAPE);
        return html.toString();
    }

    /** E-mail ao cliente: confirmação do pedido de orçamento. */
    public static String getCorpoOrcamentoCliente(com.agnello.model.OrcamentoEvento o, int numero) {
        String site = System.getenv("AGNELLO_URL_BASE");
        StringBuilder html = new StringBuilder(CABECALHO);
        html.append("<h2 style=\"color: #4A1525; font-size: 22px; margin: 0 0 8px; font-family: Georgia, serif;\">Recebemos o seu pedido de orçamento!</h2>")
            .append("<p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 22px;\">Olá, ")
            .append(escapar(o.getNome()))
            .append(". Obrigado por planejar o seu evento com a Vinheria Agnello. O Sr. Giulio e a nossa equipe de sommeliers ")
            .append("já receberam os detalhes e respondem em até <strong>1 dia útil</strong> com uma seleção de rótulos e o orçamento.</p>")
            .append("<p style=\"font-size: 13px; letter-spacing: 1px; text-transform: uppercase; color: #c5a059; font-weight: bold; margin: 0 0 6px;\">O seu evento</p>")
            .append("<p style=\"font-size: 16px; color: #333333; margin: 0 0 18px;\"><strong>").append(descricaoEvento(o)).append("</strong></p>")
            .append(quadroGarrafas(o))
            .append("<p style=\"font-size: 14px; line-height: 1.7; color: #4A1525; background: #fdfbf7; border-left: 4px solid #c5a059; padding: 12px 16px; margin: 0 0 22px;\">")
            .append(escapar(o.getEstilo().getDescricao()))
            .append(" A conta considera o consumo médio por convidado e por hora; a nossa equipe ajusta a seleção ao seu cardápio.</p>");
        if (o.getObservacoes() != null) {
            html.append("<p style=\"font-size: 13px; color: #888888; margin: 0 0 6px;\">O que você nos contou:</p>")
                .append("<p style=\"font-size: 14px; line-height: 1.7; color: #555555; white-space: pre-wrap; margin: 0 0 22px;\">")
                .append(escapar(o.getObservacoes())).append("</p>");
        }
        if (site != null && site.startsWith("https://")) {
            html.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 0 auto 18px;\"><tr><td style=\"background: #4A1525; border-radius: 6px;\">")
                .append("<a href=\"").append(escapar(site.replaceAll("/+$", ""))).append("/catalogo\" style=\"display: inline-block; padding: 13px 26px; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 14px;\">Conhecer a adega</a>")
                .append("</td></tr></table>");
        }
        html.append("<p style=\"font-size: 12px; line-height: 1.6; color: #999999; margin: 0;\">Pedido de orçamento nº ")
            .append(numero).append(". Quer acrescentar alguma coisa? Basta responder a este e-mail.</p>");
        html.append(RODAPE);
        return html.toString();
    }

    /** "Avise-me quando chegar": o vinho voltou ao estoque. */
    public static String getCorpoVoltouAoEstoque(com.agnello.model.Produto p) {
        String site = System.getenv("AGNELLO_URL_BASE");
        String base = site == null || site.isBlank() ? null : site.trim().replaceAll("/+$", "");
        StringBuilder html = new StringBuilder(CABECALHO);
        html.append("<span style=\"display: inline-block; background: #e6f4e6; color: #2e6b30; font-size: 11px; font-weight: bold; letter-spacing: 1px; text-transform: uppercase; padding: 5px 10px; border-radius: 999px; margin: 0 0 12px;\">De volta à adega</span>")
            .append("<h2 style=\"color: #4A1525; font-size: 23px; margin: 0 0 8px; font-family: Georgia, serif;\">")
            .append(escapar(p.getNome())).append("</h2>");
        if (p.getOrigem() != null || p.getTipo() != null) {
            html.append("<p style=\"font-size: 12px; letter-spacing: 1px; text-transform: uppercase; color: #999999; margin: 0 0 18px;\">")
                .append(escapar(p.getOrigem() == null ? "" : p.getOrigem()))
                .append(p.getOrigem() != null && p.getTipo() != null ? " &middot; " : "")
                .append(escapar(p.getTipo() == null ? "" : p.getTipo())).append("</p>");
        }
        html.append("<p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 18px;\">")
            .append("Você pediu para ser avisado quando este vinho voltasse. A boa notícia: ele já está disponível na Vinheria Agnello")
            .append(p.getEstoque() <= 5 ? ", mas chegaram <strong>poucas garrafas</strong>." : ".")
            .append("</p>")
            .append("<p style=\"font-family: Georgia, serif; font-size: 26px; color: #4A1525; margin: 0 0 22px;\">")
            .append(reais(BigDecimal.valueOf(p.getPreco()).setScale(2, java.math.RoundingMode.HALF_UP))).append("</p>");
        if (base != null && base.startsWith("https://")) {
            html.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 0 0 22px;\"><tr><td style=\"background: #4A1525; border-radius: 6px;\">")
                .append("<a href=\"").append(escapar(base)).append("/produto?id=").append(p.getId())
                .append("\" style=\"display: inline-block; padding: 13px 26px; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 14px;\">Ver o vinho e comprar</a>")
                .append("</td></tr></table>");
        }
        html.append("<p style=\"font-size: 12px; line-height: 1.6; color: #999999; margin: 0;\">Este é um aviso único: não vamos enviar mais e-mails sobre este vinho. ")
            .append("O seu e-mail é apagado da lista de avisos em até 90 dias.</p>");
        html.append(RODAPE);
        return html.toString();
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
