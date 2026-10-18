<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<fmt:setTimeZone value="America/Sao_Paulo" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>Visão geral | Painel Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-body">
  <jsp:include page="/WEB-INF/componentes/admin-menu.jsp" />

  <main class="adm-main">
    <header class="adm-cabecalho">
      <div>
        <h1>Olá, <c:out value="${adminLogado.primeiroNome}" /></h1>
        <p>Resumo da loja agora.</p>
      </div>
    </header>

    <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />

    <c:choose>
      <c:when test="${erroPainel}">
        <div class="adm-aviso adm-aviso--erro" role="alert">Não foi possível carregar os indicadores. Confira a ligação com o banco.</div>
      </c:when>
      <c:otherwise>
        <section class="adm-indicadores" aria-label="Indicadores">
          <a class="adm-indicador ${paraPreparar gt 0 ? 'adm-indicador--alerta' : ''}" href="${ctx}/admin/pedidos?status=PAGO">
            <span class="adm-indicador-rotulo">Pedidos para preparar</span>
            <strong>${paraPreparar}</strong>
            <span class="adm-indicador-dica">pagos ou em separação</span>
          </a>
          <a class="adm-indicador" href="${ctx}/admin/pedidos?status=AGUARDANDO_PAGAMENTO">
            <span class="adm-indicador-rotulo">Aguardando pagamento</span>
            <strong>${aguardandoPagamento}</strong>
            <span class="adm-indicador-dica">Pix e boleto</span>
          </a>
          <a class="adm-indicador ${mensagensPendentes gt 0 ? 'adm-indicador--alerta' : ''}" href="${ctx}/admin/mensagens">
            <span class="adm-indicador-rotulo">Mensagens sem resposta</span>
            <strong>${mensagensPendentes}</strong>
            <span class="adm-indicador-dica">formulário de contato</span>
          </a>
          <a class="adm-indicador ${estoqueBaixo gt 0 ? 'adm-indicador--alerta' : ''}" href="${ctx}/admin/produtos?mostrar=ativos">
            <span class="adm-indicador-rotulo">Estoque baixo</span>
            <strong>${estoqueBaixo}</strong>
            <span class="adm-indicador-dica">vinhos com menos de ${limiteEstoqueBaixo} garrafas</span>
          </a>
          <div class="adm-indicador adm-indicador--destaque">
            <span class="adm-indicador-rotulo">Vendas no mês</span>
            <strong><fmt:formatNumber value="${faturamentoMes}" type="currency" currencySymbol="R$" /></strong>
            <span class="adm-indicador-dica">pedidos não cancelados</span>
          </div>
        </section>

        <section class="adm-cartao">
          <div class="adm-cartao-topo">
            <h2>Últimos pedidos</h2>
            <a href="${ctx}/admin/pedidos" class="adm-link">Ver todos →</a>
          </div>
          <c:choose>
            <c:when test="${empty ultimosPedidos}">
              <p class="adm-vazio">Ainda não há pedidos.</p>
            </c:when>
            <c:otherwise>
              <div class="adm-tabela-rolagem">
                <table class="adm-tabela">
                  <thead><tr><th scope="col">Pedido</th><th scope="col">Cliente</th><th scope="col">Data</th><th scope="col">Status</th><th scope="col" class="num">Total</th></tr></thead>
                  <tbody>
                    <c:forEach var="p" items="${ultimosPedidos}">
                      <tr>
                        <td><a href="${ctx}/admin/pedidos/ver?id=${p.id}" class="adm-link-forte"><c:out value="${p.numero}" /></a></td>
                        <td><c:out value="${empty p.nomeCliente ? 'Conta excluída' : p.nomeCliente}" /></td>
                        <td><fmt:formatDate value="${p.dataCriacao}" pattern="dd/MM HH:mm" /></td>
                        <td><span class="adm-status adm-status--${p.status.codigo}"><c:out value="${p.status.descricao}" /></span></td>
                        <td class="num"><fmt:formatNumber value="${p.total}" type="currency" currencySymbol="R$" /></td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
            </c:otherwise>
          </c:choose>
        </section>
      </c:otherwise>
    </c:choose>
  </main>
</body>
</html>
