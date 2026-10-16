<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<fmt:setTimeZone value="America/Sao_Paulo" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>Mensagens | Painel Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-body">
  <jsp:include page="/WEB-INF/componentes/admin-menu.jsp" />

  <main class="adm-main">
    <header class="adm-cabecalho">
      <div>
        <h1>Mensagens</h1>
        <p>O que os clientes enviaram pela página Contato.</p>
      </div>
    </header>

    <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />

    <nav class="adm-filtros" aria-label="Mostrar">
      <a href="${ctx}/admin/mensagens" class="${todas ? '' : 'ativo'}">Sem resposta</a>
      <a href="${ctx}/admin/mensagens?todas" class="${todas ? 'ativo' : ''}">Todas</a>
    </nav>

    <section class="adm-cartao">
      <c:choose>
        <c:when test="${empty mensagens}">
          <p class="adm-vazio">${todas ? 'Nenhuma mensagem recebida ainda.' : 'Tudo respondido. 🎉'}</p>
        </c:when>
        <c:otherwise>
          <ul class="adm-lista-mensagens">
            <c:forEach var="m" items="${mensagens}">
              <li class="${m.respondida ? 'respondida' : ''}">
                <a href="${ctx}/admin/mensagens/ver?id=${m.id}">
                  <span class="adm-msg-topo">
                    <strong><c:out value="${m.nome}" /></strong>
                    <span class="adm-mudo"><fmt:formatDate value="${m.dataCriacao}" pattern="dd/MM HH:mm" /></span>
                  </span>
                  <span class="adm-msg-assunto"><c:out value="${m.assunto}" /><c:if test="${not empty m.numeroPedido}"> · <c:out value="${m.numeroPedido}" /></c:if></span>
                  <span class="adm-msg-resumo"><c:out value="${m.resumo}" /></span>
                  <span class="adm-situacao ${m.respondida ? 'adm-situacao--ativo' : 'adm-situacao--pendente'}">${m.respondida ? 'Respondida' : 'Pendente'}</span>
                </a>
              </li>
            </c:forEach>
          </ul>
        </c:otherwise>
      </c:choose>
    </section>
  </main>
</body>
</html>
