<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="heroAtual" scope="request" value="Política de Privacidade" />
<c:set var="heroEyebrow" scope="request" value="LGPD" />
<c:set var="heroTitulo" scope="request" value="Política de Privacidade" />
<c:set var="heroTexto" scope="request" value="Quais dados pessoais recolhemos, por que precisamos deles e como você pode consultá-los, corrigi-los ou apagá-los, conforme a Lei Geral de Proteção de Dados (Lei nº 13.709/2018)." />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Política de Privacidade | Vinheria Agnello</title>
  <meta name="description" content="Política de Privacidade da Vinheria Agnello: dados recolhidos, finalidades, compartilhamento, armazenamento local e os seus direitos pela LGPD.">
  <jsp:include page="/WEB-INF/componentes/site-head.jsp" />
  <link rel="stylesheet" href="${ctx}/css/institucional.css?v=${initParam.versaoArquivos}">
</head>

<body class="inst-body">
  <a class="skip-link" href="#conteudo">Pular para o conteúdo</a>
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="inst-main" id="conteudo">
    <jsp:include page="/WEB-INF/componentes/page-hero.jsp" />

    <div class="inst-secao">
      <div class="doc-layout">
        <details class="doc-indice" open>
          <summary>Nesta página</summary>
          <nav aria-label="Índice da página">
            <ol>
              <li><a href="#quem-somos">Quem somos</a></li>
              <li><a href="#dados">Dados que recolhemos</a></li>
              <li><a href="#bases-legais">Por que tratamos os dados</a></li>
              <li><a href="#compartilhamento">Com quem compartilhamos</a></li>
              <li><a href="#armazenamento-local">Cookies e armazenamento local</a></li>
              <li><a href="#retencao">Por quanto tempo guardamos</a></li>
              <li><a href="#seguranca">Segurança</a></li>
              <li><a href="#direitos">Os seus direitos</a></li>
              <li><a href="#menores">Menores de idade</a></li>
              <li><a href="#alteracoes">Alterações desta política</a></li>
            </ol>
          </nav>
        </details>

        <article class="prose">
          <p class="doc-atualizado">Última atualização: 20 de outubro de 2026</p>

          <h2 id="quem-somos">Quem somos</h2>
          <p>A <strong>Vinheria Agnello</strong>, com loja na Rua das Videiras, 1978 — São Paulo, SP, é a controladora
            dos dados pessoais tratados neste site. Para qualquer assunto sobre privacidade, fale com o nosso
            encarregado de proteção de dados pelo e-mail
            <a href="mailto:privacidade@vinheriaagnello.com.br">privacidade@vinheriaagnello.com.br</a>.</p>

          <h2 id="dados">Dados que recolhemos</h2>
          <p>Recolhemos apenas o necessário para vender, entregar e atender bem:</p>
          <div class="tabela-rolagem">
            <table>
              <caption class="sr-only">Dados pessoais recolhidos e finalidade</caption>
              <thead>
                <tr><th scope="col">Dado</th><th scope="col">Quando</th><th scope="col">Para quê</th></tr>
              </thead>
              <tbody>
                <tr>
                  <td>Nome e sobrenome, CPF <em>ou</em> razão social e CNPJ, e-mail, telefone e senha</td>
                  <td>No cadastro</td>
                  <td>Criar a sua conta, emitir nota fiscal e falar com você sobre os pedidos</td>
                </tr>
                <tr>
                  <td>Endereço de entrega</td>
                  <td>No checkout</td>
                  <td>Calcular e fazer a entrega</td>
                </tr>
                <tr>
                  <td>Endereços guardados e vinhos favoritos</td>
                  <td>Quando você escolhe guardá-los na área pessoal ou no checkout</td>
                  <td>Agilizar as próximas compras. Pode editá-los ou apagá-los a qualquer momento na sua conta</td>
                </tr>
                <tr>
                  <td>Dados de pagamento</td>
                  <td>No checkout</td>
                  <td>Processar o pagamento pelo Mercado Pago. Guardamos só o número e a situação do pagamento; nunca recebemos o número do cartão nem o código de segurança</td>
                </tr>
                <tr>
                  <td>Mensagens enviadas</td>
                  <td>No contato e no chat</td>
                  <td>Responder às suas dúvidas e pedidos</td>
                </tr>
                <tr>
                  <td>Respostas do Sommelier Virtual</td>
                  <td>No quiz</td>
                  <td>Indicar vinhos de acordo com o seu gosto</td>
                </tr>
              </tbody>
            </table>
          </div>

          <h2 id="bases-legais">Por que tratamos os dados</h2>
          <p>Cada tratamento tem uma base legal prevista no artigo 7º da LGPD:</p>
          <ul>
            <li><strong>Execução de contrato:</strong> cadastro, pedidos, pagamento e entrega.</li>
            <li><strong>Obrigação legal:</strong> emissão e guarda de notas fiscais e verificação da idade mínima para a venda de bebidas alcoólicas.</li>
            <li><strong>Legítimo interesse:</strong> prevenção de fraudes e melhoria do site.</li>
            <li><strong>Consentimento:</strong> envio de novidades e ofertas por e-mail, que você pode cancelar a qualquer momento.</li>
          </ul>

          <h2 id="compartilhamento">Com quem compartilhamos</h2>
          <p>Não vendemos os seus dados. Compartilhamos apenas o necessário com:</p>
          <ul>
            <li>transportadoras, para fazer a entrega;</li>
            <li>o <strong>Mercado Pago</strong>, que processa os pagamentos (Pix, cartão e boleto). Os dados do cartão são digitados diretamente na página segura do Mercado Pago e nunca passam pela Vinheria Agnello;</li>
            <li>fornecedores de hospedagem, banco de dados e envio de e-mails, que tratam os dados apenas sob as nossas instruções;</li>
            <li>autoridades públicas, quando a lei exigir.</li>
          </ul>

          <h2 id="armazenamento-local">Cookies e armazenamento local</h2>
          <p>Usamos um <strong>cookie de sessão</strong>, essencial para manter você conectado enquanto navega.
            Além disso, guardamos algumas informações no próprio navegador (armazenamento local), que não são enviadas para nós:</p>
          <ul>
            <li>os vinhos do seu <strong>carrinho</strong>;</li>
            <li>o resultado do <strong>Sommelier Virtual</strong>;</li>
            <li>a sua confirmação de que tem <strong>18 anos ou mais</strong>.</li>
          </ul>
          <p>Você pode apagar essas informações a qualquer momento nas configurações do navegador. Não usamos cookies de publicidade.</p>

          <h2 id="retencao">Por quanto tempo guardamos</h2>
          <p>Guardamos os dados da conta enquanto ela estiver ativa. Quando você exclui a conta, apagamos os seus dados
            pessoais, exceto os que a lei nos obriga a manter, como as informações fiscais dos pedidos, guardadas pelo prazo legal
            e desligadas da sua identificação.</p>

          <h2 id="seguranca">Segurança</h2>
          <p>Adotamos medidas técnicas e administrativas para proteger os seus dados:</p>
          <ul>
            <li>conexão segura (HTTPS) entre o seu navegador e o site, e conexão cifrada entre o site e o banco de dados;</li>
            <li>a sua <strong>senha nunca é guardada</strong>: guardamos apenas um código irreversível gerado a partir dela
              (hash PBKDF2), que não permite descobrir a senha;</li>
            <li><strong>CPF, telefone, endereço de entrega e endereços guardados ficam cifrados</strong> no banco de dados (AES-256), com chaves guardadas separadamente;</li>
            <li>os <strong>dados do cartão não passam pelos nossos servidores</strong> nem ficam guardados: são digitados na página do Mercado Pago;</li>
            <li>os links de ativação e de recuperação de senha são de uso único e expiram;</li>
            <li>a conta é bloqueada temporariamente depois de várias tentativas de senha erradas.</li>
          </ul>
          <p>Nenhum sistema é totalmente imune a falhas; se acontecer um incidente que possa trazer risco a você,
            vamos avisá-lo e comunicar à ANPD.</p>

          <h2 id="direitos">Os seus direitos</h2>
          <p>Pelo artigo 18 da LGPD, você pode, a qualquer momento:</p>
          <ul>
            <li>confirmar se tratamos os seus dados e ter acesso a eles;</li>
            <li>corrigir dados incompletos, inexatos ou desatualizados (o nome, o telefone e a senha pode mudar você mesmo em <a href="${pageContext.request.contextPath}/area-pessoal#dados">Minha conta</a>);</li>
            <li>pedir a anonimização, o bloqueio ou a eliminação de dados desnecessários;</li>
            <li>pedir a portabilidade dos dados para outro fornecedor;</li>
            <li>saber com quem compartilhamos os seus dados;</li>
            <li>revogar o consentimento dado.</li>
          </ul>
          <div class="prose-aviso">
            <p>Para excluir a conta, use <a href="${ctx}/area-pessoal#dados">Meus dados</a> na área pessoal.
              Para os outros pedidos, escreva para
              <a href="mailto:privacidade@vinheriaagnello.com.br">privacidade@vinheriaagnello.com.br</a>.
              Respondemos em até 15 dias.</p>
          </div>

          <h2 id="menores">Menores de idade</h2>
          <p>Este site vende bebidas alcoólicas e é destinado apenas a maiores de 18 anos. Não recolhemos
            intencionalmente dados de menores. Se identificarmos um cadastro de menor, ele será excluído.</p>

          <h2 id="alteracoes">Alterações desta política</h2>
          <p>Podemos atualizar esta política para refletir mudanças no site ou na lei. A data da última atualização
            fica sempre no topo da página e, se a mudança for importante, avisaremos por e-mail.</p>
          <p>Veja também os <a href="${ctx}/termos">Termos de Uso</a>.</p>
        </article>
      </div>
    </div>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/carrinho-gaveta.jsp" />
  <script src="${ctx}/js/pages/documento.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
