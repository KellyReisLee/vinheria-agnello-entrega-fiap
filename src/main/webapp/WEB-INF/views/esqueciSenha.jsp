<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Recuperar senha | Vinheria Agnello</title>
  <jsp:include page="/WEB-INF/componentes/auth-head.jsp" />
</head>

<body class="auth-body">
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="auth-main" id="conteudo">
    <div class="auth-card auth-card--narrow">

      <section class="auth-form-side" aria-labelledby="titulo-recuperar">
        <span class="auth-eyebrow">Segurança da conta</span>
        <h1 class="auth-title" id="titulo-recuperar">Recuperação de senha</h1>
        <p class="auth-lead">Informe o e-mail da sua conta. Se ele estiver cadastrado,
          enviaremos um link para criar uma nova senha.</p>

        <c:if test="${not empty mensagemErro}">
          <div class="auth-alert auth-alert--erro" role="alert">
            <span class="auth-alert-icon" aria-hidden="true">!</span>
            <div><c:out value="${mensagemErro}" /></div>
          </div>
        </c:if>

        <c:choose>
          <c:when test="${not empty mensagemSucesso}">
            <div class="auth-alert auth-alert--sucesso" role="status">
              <span class="auth-alert-icon" aria-hidden="true">✓</span>
              <div>
                <strong>Verifique o seu e-mail</strong>
                <c:out value="${mensagemSucesso}" />
                O link é válido por 10 minutos. Confira também a pasta de spam.
              </div>
            </div>
            <a href="${ctx}/login" class="auth-btn-outline">Voltar para o login</a>
          </c:when>

          <c:otherwise>
            <form class="auth-form" action="${ctx}/esqueci-senha" method="POST" novalidate>
              <div class="auth-field">
                <label for="email">E-mail</label>
                <input type="email" id="email" name="email" data-nome="o e-mail"
                       value="<c:out value='${param.email}' />"
                       placeholder="seu@email.com" autocomplete="email" inputmode="email"
                       required autofocus aria-describedby="email-erro">
                <span class="auth-field-erro" id="email-erro" aria-live="polite"></span>
              </div>

              <button type="submit" class="auth-btn-primary" data-texto-enviando="Enviando…">
                <span>Enviar link de recuperação</span>
              </button>
            </form>

            <p class="auth-back"><a href="${ctx}/login">← Voltar para o login</a></p>
          </c:otherwise>
        </c:choose>
      </section>

      <c:set var="brandBadge" scope="request" value="Clube Agnello 1978" />
      <c:set var="brandTitulo" scope="request" value="A curadoria do seu acervo particular." />
      <c:set var="brandTexto" scope="request" value="Não se preocupe: recuperar o acesso à sua adega é rápido e seguro." />
      <c:set var="brandB1" scope="request" value="Acesso imediato ao histórico de pedidos" />
      <c:set var="brandB2" scope="request" value="Link de uso único, válido por 10 minutos" />
      <c:set var="brandB3" scope="request" value="Suporte especializado para apreciadores" />
      <jsp:include page="/WEB-INF/componentes/auth-brand.jsp" />
    </div>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

  <div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
  <div id="agnello-cart-drawer" class="agnello-cart-drawer">
    <div class="agnello-cart-header">
      <button type="button" id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
      <h2 id="agnello-cart-title">Carrinho (0)</h2>
    </div>
    <div id="agnello-cart-body" class="agnello-cart-body"></div>
  </div>

  <script src="${ctx}/js/pages/auth.js"></script>
  <script src="${ctx}/js/components/carrinho.js"></script>
  <script src="${ctx}/js/components/chat.js"></script>
</body>
</html>
