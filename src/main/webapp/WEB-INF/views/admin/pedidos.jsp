<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="pt_BR" />
<fmt:setTimeZone value="America/Sao_Paulo" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:url var="urlBase" value="/admin/pedidos"><c:if test="${not empty filtro}"><c:param name="status" value="${filtro.codigo}" /></c:if><c:if test="${not empty busca}"><c:param name="busca" value="${busca}" /></c:if></c:url>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>Pedidos | Painel Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-body">
  <jsp:include page="/WEB-INF/componentes/admin-menu.jsp" />

  <main class="adm-main">
    <header class="adm-cabecalho">
      <div>
        <h1>Pedidos</h1>
        <p>Acompanhe e atualize o andamento das compras.</p>
      </div>
      <form method="get" action="${ctx}/admin/pedidos" class="adm-busca" role="search">
        <c:if test="${not empty filtro}"><input type="hidden" name="status" value="${filtro.codigo}"></c:if>
        <label for="busca" class="sr-only">Buscar pedido</label>
        <input type="search" id="busca" name="busca" placeholder="Nº do pedido ou e-mail" value="<c:out value='${busca}' />">
        <button type="submit" class="adm-btn">Buscar</button>
      </form>
    </header>

    <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />

    <%-- Filtro por status, com a quantidade de cada um --%>
    <nav class="adm-filtros" aria-label="Filtrar por status">
      <c:url var="urlTodos" value="/admin/pedidos"><c:if test="${not empty busca}"><c:param name="busca" value="${busca}" /></c:if></c:url>
      <a href="${urlTodos}" class="${empty filtro ? 'ativo' : ''}">Todos</a>
      <c:forEach var="s" items="${statusLista}">
        <c:url var="urlStatus" value="/admin/pedidos"><c:param name="status" value="${s.codigo}" /><c:if test="${not empty busca}"><c:param name="busca" value="${busca}" /></c:if></c:url>
        <a href="${urlStatus}" class="${filtro eq s ? 'ativo' : ''}"><c:out value="${s.descricao}" /> <span class="adm-contador">${contagem[s]}</span></a>
      </c:forEach>
    </nav>

    <section class="adm-cartao">
      <c:choose>
        <c:when test="${empty pedidos}">
          <p class="adm-vazio">Nenhum pedido encontrado<c:if test="${not empty filtro or not empty busca}"> com estes filtros</c:if>.</p>
        </c:when>
        <c:otherwise>
          <div class="adm-tabela-rolagem">
            <table class="adm-tabela">
              <thead>
                <tr><th scope="col">Pedido</th><th scope="col">Data</th><th scope="col">Cliente</th><th scope="col">Garrafas</th>
                  <th scope="col">Pagamento</th><th scope="col">Entrega</th><th scope="col">Status</th><th scope="col" class="num">Total</th></tr>
              </thead>
              <tbody>
                <c:forEach var="p" items="${pedidos}">
                  <tr>
                    <td><a href="${ctx}/admin/pedidos/ver?id=${p.id}" class="adm-link-forte"><c:out value="${p.numero}" /></a></td>
                    <td><fmt:formatDate value="${p.dataCriacao}" pattern="dd/MM/yyyy HH:mm" /></td>
                    <td>
                      <c:choose>
                        <c:when test="${empty p.emailCliente}"><span class="adm-mudo">Conta excluída</span></c:when>
                        <c:otherwise><c:out value="${p.nomeCliente}" /><br><span class="adm-mudo"><c:out value="${p.emailCliente}" /></span></c:otherwise>
                      </c:choose>
                    </td>
                    <td>${p.quantidadeGarrafas}</td>
                    <td><c:out value="${p.formaPagamento.descricao}" /><c:if test="${p.parcelas gt 1}"> (${p.parcelas}x)</c:if></td>
                    <td><c:out value="${p.tipoEntrega eq 'RETIRADA' ? 'Retirada' : 'Entrega'}" /></td>
                    <td><span class="adm-status adm-status--${p.status.codigo}"><c:out value="${p.status.descricao}" /></span></td>
                    <td class="num"><fmt:formatNumber value="${p.total}" type="currency" currencySymbol="R$" /></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>

      <c:if test="${pagina gt 1 or temProxima}">
        <nav class="adm-paginacao" aria-label="Páginas">
          <c:set var="sep" value="${fn:contains(urlBase, '?') ? '&' : '?'}" />
          <c:if test="${pagina gt 1}"><a href="${urlBase}${sep}pagina=${pagina - 1}" class="adm-btn">← Anteriores</a></c:if>
          <span>Página ${pagina}</span>
          <c:if test="${temProxima}"><a href="${urlBase}${sep}pagina=${pagina + 1}" class="adm-btn">Seguintes →</a></c:if>
        </nav>
      </c:if>
    </section>
  </main>
</body>
</html>
