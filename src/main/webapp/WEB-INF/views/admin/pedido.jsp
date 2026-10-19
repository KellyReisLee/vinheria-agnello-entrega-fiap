<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<fmt:setTimeZone value="America/Sao_Paulo" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="p" value="${pedido}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>${empty p ? 'Pedido não encontrado' : p.numero} | Painel Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-body">
  <jsp:include page="/WEB-INF/componentes/admin-menu.jsp" />

  <main class="adm-main">
    <a href="${ctx}/admin/pedidos" class="adm-voltar">← Pedidos</a>

    <c:choose>
      <c:when test="${empty p}">
        <h1>Pedido não encontrado</h1>
        <p class="adm-vazio">Confira o número e tente de novo pela lista de pedidos.</p>
      </c:when>
      <c:otherwise>
        <header class="adm-cabecalho">
          <div>
            <h1>Pedido <c:out value="${p.numero}" /></h1>
            <p>Feito em <fmt:formatDate value="${p.dataCriacao}" pattern="dd 'de' MMMM 'de' yyyy 'às' HH:mm" /></p>
          </div>
          <span class="adm-status adm-status--grande adm-status--${p.status.codigo}"><c:out value="${p.status.descricao}" /></span>
        </header>

        <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />

        <div class="adm-grade-detalhe">
          <div class="adm-coluna">
            <section class="adm-cartao">
              <h2>Itens</h2>
              <table class="adm-tabela adm-tabela--compacta">
                <thead><tr><th scope="col">Vinho</th><th scope="col" class="num">Qtd.</th><th scope="col" class="num">Preço</th><th scope="col" class="num">Subtotal</th></tr></thead>
                <tbody>
                  <c:forEach var="i" items="${p.itens}">
                    <tr>
                      <td><c:out value="${i.nomeProduto}" /></td>
                      <td class="num">${i.quantidade}</td>
                      <td class="num"><fmt:formatNumber value="${i.precoUnitario}" type="currency" currencySymbol="R$" /></td>
                      <td class="num"><fmt:formatNumber value="${i.total}" type="currency" currencySymbol="R$" /></td>
                    </tr>
                  </c:forEach>
                </tbody>
                <tfoot>
                  <tr><td colspan="3">Subtotal</td><td class="num"><fmt:formatNumber value="${p.subtotal}" type="currency" currencySymbol="R$" /></td></tr>
                  <tr><td colspan="3"><c:out value="${p.tipoEntrega.descricao}" /></td>
                    <td class="num"><c:choose><c:when test="${p.frete.signum() eq 0}">Grátis</c:when><c:otherwise><fmt:formatNumber value="${p.frete}" type="currency" currencySymbol="R$" /></c:otherwise></c:choose></td></tr>
                  <c:if test="${p.desconto.signum() gt 0}">
                    <tr><td colspan="3">Desconto Pix</td><td class="num">− <fmt:formatNumber value="${p.desconto}" type="currency" currencySymbol="R$" /></td></tr>
                  </c:if>
                  <tr class="adm-total"><td colspan="3">Total</td><td class="num"><fmt:formatNumber value="${p.total}" type="currency" currencySymbol="R$" /></td></tr>
                </tfoot>
              </table>
            </section>

            <section class="adm-cartao">
              <h2>Histórico</h2>
              <ol class="adm-linha-tempo">
                <li><strong>Pedido feito</strong> · <fmt:formatDate value="${p.dataCriacao}" pattern="dd/MM/yyyy HH:mm" /></li>
                <c:forEach var="h" items="${p.historico}">
                  <li><strong><c:out value="${h.novo.descricao}" /></strong> · <fmt:formatDate value="${h.data}" pattern="dd/MM/yyyy HH:mm" />
                    <c:choose><c:when test="${h.peloCliente}"><span class="adm-mudo">pelo cliente</span></c:when><c:when test="${not empty h.nomeAdministrador}"><span class="adm-mudo">por <c:out value="${h.nomeAdministrador}" /></span></c:when></c:choose></li>
                </c:forEach>
              </ol>
            </section>
          </div>

          <div class="adm-coluna">
            <section class="adm-cartao">
              <h2>Mudar status</h2>
              <c:choose>
                <c:when test="${empty p.proximosStatus}">
                  <p class="adm-mudo">Este pedido está concluído e não pode mais mudar.</p>
                </c:when>
                <c:otherwise>
                  <p class="adm-mudo">O cliente recebe um e-mail a cada mudança.</p>
                  <div class="adm-acoes-status">
                    <c:forEach var="s" items="${p.proximosStatus}">
                      <form method="post" action="${ctx}/admin/pedidos/status"
                            ${s.codigo eq 'CANCELADO' ? 'data-confirmar="Cancelar o pedido? As garrafas voltam ao estoque e o cliente é avisado."' : ''}>
                        <input type="hidden" name="csrf" value="${csrf}">
                        <input type="hidden" name="id" value="${p.id}">
                        <input type="hidden" name="statusAtual" value="${p.status.codigo}">
                        <input type="hidden" name="statusNovo" value="${s.codigo}">
                        <button type="submit" class="adm-btn ${s.codigo eq 'CANCELADO' ? 'adm-btn--perigo' : 'adm-btn--primario'}">
                          ${s.codigo eq 'CANCELADO' ? 'Cancelar pedido' : 'Marcar como '}<c:if test="${s.codigo ne 'CANCELADO'}">“<c:out value="${s.descricao}" />”</c:if>
                        </button>
                      </form>
                    </c:forEach>
                  </div>
                </c:otherwise>
              </c:choose>
            </section>

            <section class="adm-cartao">
              <h2>Cliente</h2>
              <c:choose>
                <c:when test="${empty p.emailCliente}"><p class="adm-mudo">A conta deste cliente foi excluída.</p></c:when>
                <c:otherwise>
                  <p><c:out value="${p.nomeCliente}" /></p>
                  <p><a href="mailto:${p.emailCliente}" class="adm-link"><c:out value="${p.emailCliente}" /></a></p>
                </c:otherwise>
              </c:choose>
            </section>

            <section class="adm-cartao">
              <h2>Pagamento e entrega</h2>
              <dl class="adm-dados">
                <div><dt>Pagamento</dt><dd><c:out value="${p.formaPagamento.descricao}" /><c:if test="${p.parcelas gt 1}"> em ${p.parcelas}x de <fmt:formatNumber value="${p.valorParcela}" type="currency" currencySymbol="R$" /></c:if></dd></div>
                <div><dt>Entrega</dt><dd><c:out value="${p.tipoEntrega.descricao}" /></dd></div>
                <c:if test="${not empty p.endereco}"><div><dt>Endereço</dt><dd><c:out value="${p.endereco}" /></dd></div></c:if>
              </dl>
            </section>
          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </main>
  <script src="${ctx}/js/pages/admin.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
