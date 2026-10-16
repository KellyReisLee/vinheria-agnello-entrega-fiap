<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>Entrar no painel | Vinheria Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-login-body">
  <main class="adm-login">
    <div class="adm-login-marca">
      <jsp:include page="/WEB-INF/componentes/logo-taca.jsp"><jsp:param name="id" value="login" /><jsp:param name="classe" value="adm-login-taca" /></jsp:include>
      <span>Vinheria Agnello</span>
    </div>
    <h1>Painel da loja</h1>
    <p class="adm-login-sub">Acesso restrito à equipe.</p>

    <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />
    <c:if test="${param.saiu ne null and empty erro and empty flash}">
      <div class="adm-aviso adm-aviso--sucesso" role="status">Você saiu do painel.</div>
    </c:if>

    <form method="post" action="${ctx}/admin/login" class="adm-form">
      <input type="hidden" name="csrf" value="${csrf}">
      <div class="adm-campo">
        <label for="email">E-mail</label>
        <input type="email" id="email" name="email" autocomplete="username" required autofocus
               value="<c:out value='${emailDigitado}' />">
      </div>
      <div class="adm-campo">
        <label for="senha">Senha</label>
        <input type="password" id="senha" name="senha" autocomplete="current-password" required>
      </div>
      <button type="submit" class="adm-btn adm-btn--primario adm-btn--largo">Entrar</button>
    </form>
    <a href="${ctx}/home" class="adm-login-voltar">← Voltar à loja</a>
  </main>
</body>
</html>
