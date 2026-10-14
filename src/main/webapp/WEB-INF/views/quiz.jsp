<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sommelier Virtual | Vinheria Agnello</title>
  <meta name="description" content="Responda a 3 perguntas e o Sommelier Virtual da Vinheria Agnello indica os rótulos ideais para o seu momento.">
  <meta name="theme-color" content="#4A1525">
  <link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link
    href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap"
    rel="stylesheet">

  <link rel="stylesheet" href="${ctx}/css/variables.css">
  <link rel="stylesheet" href="${ctx}/css/global.css">
  <link rel="stylesheet" href="${ctx}/css/quiz.css">

  <script>
    window.CONTEXT_PATH = '${ctx}';
  </script>
</head>

<body class="quiz-page-body">

  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="quiz-main" id="conteudo">
    <div class="quiz-intro">
      <span class="quiz-eyebrow">Sommelier Virtual</span>
      <h1 class="quiz-intro-title">Encontre o vinho ideal em 3 perguntas</h1>
    </div>

    <%-- As perguntas (e as opções de cada passo) são desenhadas pelo quiz.js --%>
    <section class="sommelier-card" aria-labelledby="quiz-pergunta">
      <div class="progress-container">
        <div class="progress-bar-track" role="progressbar" aria-label="Progresso do quiz"
             aria-valuemin="1" aria-valuemax="3" aria-valuenow="1">
          <div class="progress-bar-fill" id="progress-fill"></div>
        </div>
        <span class="progress-text" id="progress-text">Passo 1 de 3</span>
      </div>

      <form id="quiz-form" novalidate>
        <fieldset class="quiz-step">
          <legend class="question-title" id="quiz-pergunta" tabindex="-1">Carregando…</legend>
          <div class="options-list" id="quiz-opcoes"></div>
        </fieldset>

        <div class="sommelier-footer">
          <button type="button" id="btn-voltar" class="btn-text-voltar">
            <span aria-hidden="true">←</span> <span id="btn-voltar-texto">Início</span>
          </button>
          <button type="submit" id="btn-proximo" class="btn-proximo-vinho">
            <span id="btn-proximo-texto">Próximo</span> <span class="btn-arrow" aria-hidden="true">→</span>
          </button>
        </div>
      </form>
    </section>

    <noscript>
      <p class="quiz-noscript">O Sommelier Virtual precisa de JavaScript ativo.
        Você também pode <a href="${ctx}/catalogo">ver todos os nossos rótulos</a>.</p>
    </noscript>
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

  <script src="${ctx}/js/pages/quiz.js"></script>
  <script src="${ctx}/js/components/carrinho.js"></script>
  <script src="${ctx}/js/components/chat.js"></script>
</body>

</html>
