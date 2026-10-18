<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<fmt:setTimeZone value="America/Sao_Paulo" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="m" value="${mensagem}" />
<%-- Rascunho da resposta guardado quando o envio falhou (lido uma vez e apagado) --%>
<c:set var="rascunho" value="${sessionScope.rascunhoResposta}" />
<c:remove var="rascunhoResposta" scope="session" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>Mensagem | Painel Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-body">
  <jsp:include page="/WEB-INF/componentes/admin-menu.jsp" />

  <main class="adm-main">
    <a href="${ctx}/admin/mensagens" class="adm-voltar">← Mensagens</a>

    <c:choose>
      <c:when test="${empty m}">
        <h1>Mensagem não encontrada</h1>
      </c:when>
      <c:otherwise>
        <header class="adm-cabecalho">
          <div>
            <h1><c:out value="${m.assunto}" /></h1>
            <p>Recebida em <fmt:formatDate value="${m.dataCriacao}" pattern="dd/MM/yyyy 'às' HH:mm" /></p>
          </div>
          <span class="adm-situacao adm-situacao--grande ${m.respondida ? 'adm-situacao--ativo' : 'adm-situacao--pendente'}">${m.respondida ? 'Respondida' : 'Pendente'}</span>
        </header>

        <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />

        <div class="adm-grade-detalhe">
          <div class="adm-coluna">
            <section class="adm-cartao">
              <h2>Mensagem</h2>
              <p class="adm-texto-mensagem"><c:out value="${m.mensagem}" /></p>
            </section>

            <c:choose>
              <c:when test="${m.respondida}">
                <section class="adm-cartao">
                  <h2>${empty m.resposta ? 'Resolvida sem resposta por e-mail' : 'Resposta enviada'}</h2>
                  <c:if test="${not empty m.resposta}"><p class="adm-texto-mensagem"><c:out value="${m.resposta}" /></p></c:if>
                  <p class="adm-mudo">
                    <c:if test="${not empty m.nomeAdministrador}">Por <c:out value="${m.nomeAdministrador}" />, </c:if>
                    em <fmt:formatDate value="${m.dataResposta}" pattern="dd/MM/yyyy 'às' HH:mm" />
                  </p>
                </section>
              </c:when>
              <c:otherwise>
                <section class="adm-cartao">
                  <h2>Responder</h2>
                  <form method="post" action="${ctx}/admin/mensagens/responder" class="adm-form">
                    <input type="hidden" name="csrf" value="${csrf}">
                    <input type="hidden" name="id" value="${m.id}">
                    <div class="adm-campo">
                      <label for="resposta">Resposta para <c:out value="${m.email}" /></label>
                      <textarea id="resposta" name="resposta" rows="8" minlength="10" maxlength="5000" required><c:out value="${rascunho}" /></textarea>
                      <span class="adm-dica">O cliente recebe por e-mail, com a mensagem dele no final. A sua assinatura entra sozinha.</span>
                    </div>
                    <button type="submit" class="adm-btn adm-btn--primario">Enviar resposta</button>
                  </form>
                  <form method="post" action="${ctx}/admin/mensagens/resolver" class="adm-form-secundario"
                        data-confirmar="Marcar como resolvida sem enviar e-mail?">
                    <input type="hidden" name="csrf" value="${csrf}">
                    <input type="hidden" name="id" value="${m.id}">
                    <button type="submit" class="adm-btn adm-btn--discreto">Marcar como resolvida sem responder</button>
                  </form>
                </section>
              </c:otherwise>
            </c:choose>
          </div>

          <div class="adm-coluna">
            <section class="adm-cartao">
              <h2>Remetente</h2>
              <dl class="adm-dados">
                <div><dt>Nome</dt><dd><c:out value="${m.nome}" /></dd></div>
                <div><dt>E-mail</dt><dd><c:out value="${m.email}" /></dd></div>
                <div><dt>Cliente</dt><dd>${empty m.clienteId ? 'Não estava logado' : 'Tem conta na loja'}</dd></div>
                <c:if test="${not empty m.numeroPedido}">
                  <div><dt>Pedido</dt><dd><c:url var="uPedido" value="/admin/pedidos"><c:param name="busca" value="${m.numeroPedido}" /></c:url>
                    <a href="${uPedido}" class="adm-link"><c:out value="${m.numeroPedido}" /></a></dd></div>
                </c:if>
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
