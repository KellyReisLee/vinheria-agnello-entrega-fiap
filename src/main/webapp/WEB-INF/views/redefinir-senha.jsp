<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- O token vem do controller (atributo) ou do link do e-mail (?token=...) --%>
<c:set var="tokenAtual" value="${not empty token ? token : param.token}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Nova senha | Vinheria Agnello</title>
  <jsp:include page="/WEB-INF/componentes/auth-head.jsp" />
</head>

<body class="auth-body">
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="auth-main" id="conteudo">
    <div class="auth-card auth-card--narrow">

      <section class="auth-form-side" aria-labelledby="titulo-redefinir">
        <span class="auth-eyebrow">Nova credencial</span>
        <h1 class="auth-title" id="titulo-redefinir">Redefinir senha</h1>
        <p class="auth-lead">Escolha uma nova senha segura para proteger o acesso à sua adega particular.</p>

        <c:if test="${not empty mensagemErro}">
          <div class="auth-alert auth-alert--erro" role="alert">
            <span class="auth-alert-icon" aria-hidden="true">!</span>
            <div><c:out value="${mensagemErro}" /></div>
          </div>
        </c:if>

        <c:choose>
          <c:when test="${not empty mensagemSucesso}">
            <div class="auth-alert auth-alert--sucesso" role="status"
                 data-redirecionar="${ctx}/login" data-segundos="5">
              <span class="auth-alert-icon" aria-hidden="true">✓</span>
              <div>
                <strong>Tudo pronto!</strong>
                <c:out value="${mensagemSucesso}" />
                <br>Abrindo o login em <span data-segundos-restantes>5</span> segundos…
              </div>
            </div>
            <a href="${ctx}/login" class="auth-btn-outline">Entrar agora</a>
          </c:when>

          <c:otherwise>
            <form class="auth-form" action="${ctx}/redefinir-senha" method="POST" novalidate>
              <%-- c:out escapa o token (antes ia direto para o HTML: risco de XSS) --%>
              <input type="hidden" name="token" value="<c:out value='${tokenAtual}' />">

              <div class="auth-field">
                <label for="novaSenha">Nova senha</label>
                <div class="auth-input-wrap">
                  <input type="password" id="novaSenha" name="novaSenha" data-nome="a nova senha"
                         minlength="8" autocomplete="new-password" required autofocus
                         data-forca="forca-senha" aria-describedby="forca-senha-texto novaSenha-erro">
                  <button type="button" class="auth-toggle-senha" aria-controls="novaSenha"
                          aria-label="Mostrar senha" aria-pressed="false">
                    <img src="${ctx}/assets/icons/eye-closed.svg" alt="" width="20" height="20">
                  </button>
                </div>
                <div class="auth-forca" id="forca-senha" data-nivel="0" aria-hidden="true">
                  <span></span><span></span><span></span><span></span>
                </div>
                <span class="auth-hint" id="forca-senha-texto">Mínimo de 8 caracteres.</span>
                <span class="auth-field-erro" id="novaSenha-erro" aria-live="polite"></span>
              </div>

              <div class="auth-field">
                <label for="confirmaSenha">Confirme a nova senha</label>
                <div class="auth-input-wrap">
                  <input type="password" id="confirmaSenha" name="confirmaSenha" data-nome="a confirmação da senha"
                         data-igual-a="novaSenha" autocomplete="new-password" required
                         aria-describedby="confirmaSenha-erro">
                  <button type="button" class="auth-toggle-senha" aria-controls="confirmaSenha"
                          aria-label="Mostrar senha" aria-pressed="false">
                    <img src="${ctx}/assets/icons/eye-closed.svg" alt="" width="20" height="20">
                  </button>
                </div>
                <span class="auth-field-erro" id="confirmaSenha-erro" aria-live="polite"></span>
              </div>

              <button type="submit" class="auth-btn-primary" data-texto-enviando="Atualizando…">
                <span>Atualizar senha</span>
              </button>
            </form>

            <p class="auth-back"><a href="${ctx}/login">← Voltar para o login</a></p>
          </c:otherwise>
        </c:choose>
      </section>

      <c:set var="brandBadge" scope="request" value="Clube Agnello 1978" />
      <c:set var="brandTitulo" scope="request" value="Segurança e exclusividade em cada detalhe." />
      <c:set var="brandTexto" scope="request" value="A sua nova senha garante a privacidade das suas escolhas e do seu histórico de pedidos." />
      <c:set var="brandB1" scope="request" value="Criptografia de ponta a ponta" />
      <c:set var="brandB2" scope="request" value="Acesso protegido ao seu perfil" />
      <c:set var="brandB3" scope="request" value="Atendimento dedicado aos membros" />
      <jsp:include page="/WEB-INF/componentes/auth-brand.jsp" />
    </div>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

  <script src="${ctx}/js/pages/auth.js"></script>
  <script src="${ctx}/js/components/chat.js"></script>
</body>
</html>
