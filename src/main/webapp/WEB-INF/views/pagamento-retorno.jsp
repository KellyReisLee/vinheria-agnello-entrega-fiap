<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Página para onde o Mercado Pago devolve o cliente (PagamentoController).
     "situacao": aprovado | confirmando | pendente | recusado | nao_concluido | cancelado --%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Pagamento do pedido <c:out value="${pedido.numero}" /> | Vinheria Agnello</title>
  <meta name="robots" content="noindex">
  <meta name="theme-color" content="#4A1525">
  <link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/variables.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/global.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/checkout.css?v=${initParam.versaoArquivos}">
  <c:if test="${situacao eq 'confirmando' or situacao eq 'pendente'}">
    <%-- Pix/boleto ou aprovação a chegar: a página volta a perguntar ao servidor sozinha --%>
    <meta http-equiv="refresh" content="${situacao eq 'confirmando' ? 5 : 20}">
  </c:if>
</head>

<body class="checkout-body">
  <header class="checkout-top-bar">
    <a href="${ctx}/home" class="checkout-logo" aria-label="Vinheria Agnello — voltar à loja">
      <jsp:include page="/WEB-INF/componentes/logo-taca.jsp"><jsp:param name="id" value="retorno" /><jsp:param name="classe" value="checkout-logo-img" /></jsp:include>
      <span>
        <span class="checkout-logo-title">VINHERIA AGNELLO</span>
        <span class="checkout-logo-sub">DESDE 1978</span>
      </span>
    </a>
    <span class="secure-badge">
      <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="4" y="11" width="16" height="10" rx="2"></rect><path d="M8 11V7a4 4 0 0 1 8 0v4"></path></svg>
      Compra segura
    </span>
  </header>

  <main class="checkout-main" id="conteudo">
    <section class="checkout-confirmacao retorno retorno--${situacao}" aria-labelledby="retorno-titulo">
      <c:choose>
        <c:when test="${situacao eq 'aprovado'}">
          <div class="confirmacao-icone" aria-hidden="true">✓</div>
          <span class="confirmacao-eyebrow">Pedido <c:out value="${pedido.numero}" /></span>
          <h1 id="retorno-titulo">Pagamento aprovado!</h1>
          <p class="confirmacao-lead">Obrigado pela compra. Já estamos preparando os seus vinhos e
            enviamos a confirmação para o seu e-mail.</p>
        </c:when>
        <c:when test="${situacao eq 'confirmando'}">
          <div class="confirmacao-icone confirmacao-icone--espera" aria-hidden="true"></div>
          <span class="confirmacao-eyebrow">Pedido <c:out value="${pedido.numero}" /></span>
          <h1 id="retorno-titulo">Confirmando o pagamento…</h1>
          <p class="confirmacao-lead" role="status">O Mercado Pago aprovou o pagamento e estamos registrando-o.
            Esta página atualiza sozinha em instantes.</p>
        </c:when>
        <c:when test="${situacao eq 'pendente'}">
          <div class="confirmacao-icone confirmacao-icone--espera" aria-hidden="true"></div>
          <span class="confirmacao-eyebrow">Pedido <c:out value="${pedido.numero}" /></span>
          <h1 id="retorno-titulo">Aguardando o pagamento</h1>
          <p class="confirmacao-lead" role="status">
            <c:choose>
              <c:when test="${pedido.formaPagamento.codigo eq 'pix'}">Se ainda não pagou o Pix, pague pelo QR Code no Mercado Pago.
                Assim que for aprovado (em geral, segundos), esta página e o seu e-mail mostram a confirmação.</c:when>
              <c:when test="${pedido.formaPagamento.codigo eq 'boleto'}">Pague o boleto até o vencimento. A compensação leva até
                2 dias úteis; avisamos por e-mail quando for confirmada.</c:when>
              <c:otherwise>O Mercado Pago está analisando o pagamento. Avisamos por e-mail assim que houver resposta.</c:otherwise>
            </c:choose>
          </p>
        </c:when>
        <c:when test="${situacao eq 'cancelado'}">
          <div class="confirmacao-icone confirmacao-icone--erro" aria-hidden="true">!</div>
          <span class="confirmacao-eyebrow">Pedido <c:out value="${pedido.numero}" /></span>
          <h1 id="retorno-titulo">Este pedido foi cancelado</h1>
          <p class="confirmacao-lead">Se pagou depois do cancelamento, fale conosco: devolvemos o valor.</p>
        </c:when>
        <c:otherwise>
          <div class="confirmacao-icone confirmacao-icone--erro" aria-hidden="true">!</div>
          <span class="confirmacao-eyebrow">Pedido <c:out value="${pedido.numero}" /></span>
          <h1 id="retorno-titulo">${situacao eq 'recusado' ? 'O pagamento foi recusado' : 'O pagamento não foi concluído'}</h1>
          <p class="confirmacao-lead">
            ${situacao eq 'recusado'
              ? 'Nenhum valor foi cobrado. Confira os dados ou tente outro cartão.'
              : 'Nenhum valor foi cobrado. As garrafas continuam reservadas para você.'}
          </p>
        </c:otherwise>
      </c:choose>

      <div class="confirmacao-resumo">
        <ul>
          <c:forEach var="item" items="${pedido.itens}">
            <li><span>${item.quantidade}× <c:out value="${item.nomeProduto}" /></span>
              <span><fmt:formatNumber value="${item.total}" type="currency" currencySymbol="R$" /></span></li>
          </c:forEach>
        </ul>
        <dl>
          <div><dt>Pagamento</dt><dd><c:out value="${pedido.formaPagamento.descricao}" /><c:if test="${pedido.parcelas gt 1}"> · ${pedido.parcelas}x</c:if></dd></div>
          <div class="confirmacao-total"><dt>Total</dt><dd><fmt:formatNumber value="${pedido.total}" type="currency" currencySymbol="R$" /></dd></div>
        </dl>
      </div>

      <div class="confirmacao-acoes">
        <c:if test="${pagamentoOnline and pedido.status.codigo eq 'AGUARDANDO_PAGAMENTO' and (situacao eq 'recusado' or situacao eq 'nao_concluido' or situacao eq 'pendente')}">
          <form method="post" action="${ctx}/pagamento/pagar">
            <input type="hidden" name="csrf" value="${csrf}">
            <input type="hidden" name="id" value="${pedido.id}">
            <button type="submit" class="checkout-btn-primary">${situacao eq 'pendente' ? 'Abrir o pagamento de novo' : 'Tentar pagar de novo'}</button>
          </form>
        </c:if>
        <a href="${ctx}/area-pessoal#pedidos" class="checkout-btn-secondary">Ver meus pedidos</a>
        <a href="${ctx}/catalogo" class="checkout-btn-secondary">Continuar comprando</a>
      </div>
    </section>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
</body>
</html>
