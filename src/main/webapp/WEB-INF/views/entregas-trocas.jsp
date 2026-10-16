<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="heroAtual" scope="request" value="Entregas e Trocas" />
<c:set var="heroEyebrow" scope="request" value="Políticas da loja" />
<c:set var="heroTitulo" scope="request" value="Entregas, trocas e devoluções" />
<c:set var="heroTexto" scope="request" value="Prazos, formas de entrega e o que fazer se algo não sair como esperado. Tudo explicado sem letras miúdas." />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Entregas e Trocas | Vinheria Agnello</title>
  <meta name="description" content="Prazos e formas de entrega da Vinheria Agnello, direito de arrependimento em 7 dias, trocas de produtos danificados ou com defeito e reembolsos.">
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
              <li><a href="#prazos">Prazos de entrega</a></li>
              <li><a href="#formas-entrega">Formas de entrega</a></li>
              <li><a href="#recebimento">Recebimento do pedido</a></li>
              <li><a href="#trocas">Desistência da compra</a></li>
              <li><a href="#danificado">Produto danificado</a></li>
              <li><a href="#defeito">Vinho com defeito</a></li>
              <li><a href="#solicitar">Como solicitar</a></li>
              <li><a href="#reembolso">Reembolso</a></li>
            </ol>
          </nav>
        </details>

        <article class="prose">
          <p class="doc-atualizado">Última atualização: 14 de outubro de 2026</p>

          <h2 id="prazos">Prazos de entrega</h2>
          <p>O prazo começa a contar depois da confirmação do pagamento. Pagamentos por Pix e cartão
            costumam ser confirmados em minutos; por boleto, em até 3 dias úteis.</p>
          <div class="tabela-rolagem">
            <table>
              <caption class="sr-only">Prazo de entrega por região</caption>
              <thead>
                <tr><th scope="col">Região</th><th scope="col">Prazo</th><th scope="col">Frete</th></tr>
              </thead>
              <tbody>
                <tr><td>Cidade de São Paulo</td><td>até 24 horas</td><td>R$ 25,00</td></tr>
                <tr><td>Grande São Paulo</td><td>até 48 horas</td><td>R$ 25,00</td></tr>
                <tr><td>Interior de São Paulo</td><td>2 a 4 dias úteis</td><td>R$ 25,00</td></tr>
                <tr><td>Demais estados</td><td>3 a 8 dias úteis</td><td>R$ 25,00</td></tr>
              </tbody>
            </table>
          </div>

          <h2 id="formas-entrega">Formas de entrega</h2>
          <h3>Frete Térmico Agnello</h3>
          <p>As garrafas viajam em embalagem térmica e antichoque, que protege o vinho do calor e de impactos.
            Você recebe por e-mail a confirmação de envio.</p>
          <h3>Retirada na adega</h3>
          <p>Gratuita. O pedido fica pronto em até 3 horas, e avisamos por e-mail quando puder buscá-lo na
            Rua das Videiras, 1978 — São Paulo, SP, de segunda a sábado, das 10h às 20h. Guardamos o pedido por 7 dias.</p>

          <h2 id="recebimento">Recebimento do pedido</h2>
          <ul>
            <li>Só entregamos a pessoas com <strong>18 anos ou mais</strong>. O entregador pode pedir um documento com foto.</li>
            <li>Fazemos até <strong>3 tentativas</strong> de entrega. Depois disso, o pedido volta para a adega e entramos em contato.</li>
            <li>Antes de assinar o recebimento, confira se a embalagem está íntegra. Se houver avarias, registre na hora.</li>
          </ul>

          <h2 id="trocas">Desistência da compra (arrependimento)</h2>
          <p>Pelo artigo 49 do Código de Defesa do Consumidor, você pode desistir da compra em até
            <strong>7 dias corridos</strong> após o recebimento, sem precisar explicar o motivo.</p>
          <ul>
            <li>A garrafa deve estar <strong>lacrada</strong>, sem sinais de uso e, se possível, na embalagem original.</li>
            <li>A coleta é por nossa conta e devolvemos o valor total pago, <strong>incluindo o frete</strong>.</li>
          </ul>

          <h2 id="danificado">Produto danificado no transporte</h2>
          <p>Se a garrafa chegar quebrada, com vazamento ou com o rótulo danificado, envie fotos da embalagem e
            do produto em até <strong>7 dias</strong> após o recebimento. Enviamos outra garrafa sem custo ou, se preferir,
            devolvemos o valor.</p>

          <h2 id="defeito">Vinho com defeito</h2>
          <p>Vinho é um produto vivo, e às vezes uma garrafa apresenta defeito de rolha (cheiro de mofo) ou oxidação.
            Nesse caso, fale conosco em até <strong>30 dias</strong> após o recebimento e guarde a garrafa com o
            vinho que sobrou: ela pode ser necessária para a análise.</p>
          <div class="prose-aviso">
            <p>Diferenças de gosto pessoal ou de safra (quando a safra recebida é a vigente no momento da compra)
              não são consideradas defeito. Se tiver dúvida, fale conosco: preferimos sempre conversar.</p>
          </div>

          <h2 id="solicitar">Como solicitar uma troca ou devolução</h2>
          <ol>
            <li>Acesse o <a href="${ctx}/contato">Contato</a> e escolha o assunto <strong>Trocas e devoluções</strong>.</li>
            <li>Informe o número do pedido, o motivo e, se for o caso, envie fotos.</li>
            <li>Respondemos em até 1 dia útil com as instruções de coleta ou de envio.</li>
          </ol>

          <h2 id="reembolso">Reembolso</h2>
          <p>O reembolso é feito na mesma forma de pagamento da compra, depois de recebermos o produto de volta
            (ou, em caso de dano no transporte, depois de analisarmos as fotos):</p>
          <div class="tabela-rolagem">
            <table>
              <caption class="sr-only">Prazo de reembolso por forma de pagamento</caption>
              <thead>
                <tr><th scope="col">Pagamento</th><th scope="col">Como devolvemos</th><th scope="col">Prazo</th></tr>
              </thead>
              <tbody>
                <tr><td>Pix</td><td>Pix para a mesma conta que pagou</td><td>até 5 dias úteis</td></tr>
                <tr><td>Cartão de crédito</td><td>Estorno na fatura</td><td>pedido em até 5 dias úteis; aparece em até 2 faturas</td></tr>
                <tr><td>Boleto</td><td>Transferência para a conta do titular do pedido</td><td>até 5 dias úteis</td></tr>
              </tbody>
            </table>
          </div>
          <p>Ainda tem dúvidas? Veja a <a href="${ctx}/ajuda">Central de Ajuda</a> ou <a href="${ctx}/contato">fale com a nossa equipe</a>.</p>
        </article>
      </div>
    </div>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/carrinho-gaveta.jsp" />
  <script src="${ctx}/js/pages/documento.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
