package com.agnello.util;

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
            "        <h2 style=\"color: #4A1525; font-size: 21px; margin-top: 0; margin-bottom: 20px; font-family: Georgia, serif;\">Redefinição de Palavra-passe</h2>" +
            "        <p style=\"font-size: 15px; line-height: 1.7; color: #555555; margin-bottom: 20px;\">Recebemos um pedido para redefinir a palavra-passe associada à sua conta na Vinheria Agnello.</p>" +
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
}