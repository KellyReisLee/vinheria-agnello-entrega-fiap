<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Entrar | Vinheria Agnello</title>
  <jsp:include page="/WEB-INF/componentes/auth-head.jsp" />
</head>

<body class="auth-body">
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="auth-main" id="conteudo">
    <div class="auth-card">

      <section class="auth-form-side" aria-labelledby="titulo-login">
        <span class="auth-eyebrow">Área exclusiva</span>
        <h1 class="auth-title" id="titulo-login">Boas-vindas à Adega</h1>
        <p class="auth-lead">Entre com seus dados para acessar sua curadoria pessoal e histórico de pedidos.</p>

        <%-- "sucesso" vem do AtivarContaController (conta ativada pelo link do e-mail) --%>
        <c:if test="${not empty sucesso}">
          <div class="auth-alert auth-alert--sucesso" role="status">
            <span class="auth-alert-icon" aria-hidden="true">✓</span>
            <div><c:out value="${sucesso}" /></div>
          </div>
        </c:if>
        <c:if test="${not empty erro}">
          <div class="auth-alert auth-alert--erro" role="alert">
            <span class="auth-alert-icon" aria-hidden="true">!</span>
            <div><c:out value="${erro}" /></div>
          </div>
        </c:if>

        <form class="auth-form" action="${ctx}/login" method="POST" novalidate>
          <c:if test="${not empty voltar}"><input type="hidden" name="voltar" value="<c:out value='${voltar}' />"></c:if>
          <div class="auth-field">
            <label for="email">E-mail</label>
            <%-- Mantém o e-mail digitado se o login falhar (c:out evita injeção de HTML) --%>
            <input type="email" id="email" name="email" data-nome="o e-mail"
                   value="<c:out value='${param.email}' />"
                   placeholder="seu@email.com" autocomplete="email" inputmode="email"
                   required aria-describedby="email-erro" ${empty param.email ? 'autofocus' : ''}>
            <span class="auth-field-erro" id="email-erro" aria-live="polite"></span>
          </div>

          <div class="auth-field">
            <div class="auth-label-row">
              <label for="senha">Senha</label>
              <a href="${ctx}/esqueci-senha" class="auth-link">Esqueceu sua senha?</a>
            </div>
            <div class="auth-input-wrap">
              <input type="password" id="senha" name="senha" data-nome="a senha"
                     placeholder="Sua senha" autocomplete="current-password"
                     required aria-describedby="senha-erro" ${not empty param.email ? 'autofocus' : ''}>
              <button type="button" class="auth-toggle-senha" aria-controls="senha"
                      aria-label="Mostrar senha" aria-pressed="false">
                <img src="${ctx}/assets/icons/eye-closed.svg" alt="" width="20" height="20">
              </button>
            </div>
            <span class="auth-field-erro" id="senha-erro" aria-live="polite"></span>
          </div>

          <button type="submit" class="auth-btn-primary" data-texto-enviando="Entrando…">
            <span>Acessar conta</span>
          </button>
        </form>

        <div class="auth-divider">ou</div>
        <a href="${ctx}/cadastro" class="auth-btn-outline">Criar nova conta na família</a>
      </section>

      <c:set var="brandBadge" scope="request" value="Clube Agnello 1978" />
      <c:set var="brandTitulo" scope="request" value="O privilégio de ter uma adega particular em casa." />
      <c:set var="brandTexto" scope="request" value="Faça parte do nosso círculo de apreciadores e receba rótulos de importação própria selecionados pelo Sr. Giulio e pela Bianca." />
      <c:set var="brandB1" scope="request" value="Cashback e vantagens exclusivas em todas as compras" />
      <c:set var="brandB2" scope="request" value="Acesso antecipado a safras raras e limitadas" />
      <c:set var="brandB3" scope="request" value="Frete especial e embalagem térmica garantida" />
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

  <script src="${ctx}/js/pages/auth.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/carrinho.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/chat.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
