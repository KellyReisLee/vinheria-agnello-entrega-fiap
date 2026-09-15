<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quiz do Sommelier — Vinheria Agnello</title>

  <!-- Favicon -->
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animations.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sections/_chat.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">
   <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quiz.css">
  
  <!-- Importação das fontes do Google Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link
    href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap"
    rel="stylesheet">
    
  <script>
    window.CONTEXT_PATH = '${pageContext.request.contextPath}';
  </script>
</head>

<body class="agnello-quiz-body quiz-page-body">

  <!-- HEADER MODULAR -->
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="agnello-quiz-main quiz-main-wrapper">
    <div id="sommelier-card" class="sommelier-card calculator-box quiz-container">

      <!-- CABEÇALHO DO SOMMELIER -->
      <h2 class="sommelier-title">Sommelier Virtual</h2>

      <!-- BARRA DE PROGRESSO IDÊNTICA À REFERÊNCIA -->
      <div class="progress-container">
        <div class="progress-bar-track">
          <div class="progress-bar-fill" id="progress-fill" style="width: 33%;"></div>
        </div>
        <span class="progress-text" id="progress-text">Passo ${passoAtual} de 3</span>
      </div>

      <!-- ETAPA 1: OCASIÃO E HARMONIZAÇÃO -->
      <div class="quiz-step active" id="step-1">
        <h2 class="quiz-title question-title">Qual é a ocasião ou o prato principal do seu momento?</h2>

        <div class="estilo-evento-group options-list">
          <div class="type-btn option-pill selected" data-step="1" data-value="carnes-vermelhas">
            <span>Carnes Vermelhas & Churrasco Nobre</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="1" data-value="massas-molhos">
            <span>Massas com Molhos Intensos ou Ragus</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="1" data-value="peixes-frutos-mar">
            <span>Peixes, Frutos do Mar ou Comida Japonesa</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="1" data-value="momento-a-dois">
            <span>Sunset ou Coquetel de Verão</span>
            <span class="arrow">›</span>
          </div>
        </div>
      </div>

      <!-- ETAPA 2: DINÂMICA (Preenchida via JavaScript) -->
      <div class="quiz-step" id="step-2">
        <h2 class="quiz-title question-title">Carregando perfil...</h2>
        <div class="estilo-evento-group options-list">
          <!-- Os botões desta etapa são injetados dinamicamente pelo quiz.js -->
        </div>
      </div>

      <!-- ETAPA 3: DINÂMICA (Preenchida via JavaScript) -->
      <div class="quiz-step" id="step-3">
        <h2 class="quiz-title question-title">Carregando estrutura...</h2>
        <div class="estilo-evento-group options-list">
          <!-- Os botões desta etapa são injetados dinamicamente pelo quiz.js -->
        </div>
      </div>

      <!-- RODAPÉ DO CARD (Voltar / Próximo) -->
      <div class="sommelier-footer">
        <button type="button" id="btn-voltar" class="btn-text-voltar">Voltar</button>
        <button type="button" id="btn-proximo" class="btn-proximo-vinho btn-agnello-primary">Próximo</button>
      </div>

    </div>
  </main>

  <!-- FOOTER MODULAR (Protegido em WEB-INF) -->
  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

  <!-- Carrinho -->
  <div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
  <div id="agnello-cart-drawer" class="agnello-cart-drawer">
    <div class="agnello-cart-header">
      <button id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
      <h2 id="agnello-cart-title">Carrinho (0)</h2>
    </div>
    <div id="agnello-cart-body" class="agnello-cart-body"></div>
  </div>

  <!-- Scripts com Context Path -->
  <script src="${pageContext.request.contextPath}/js/pages/quiz.js"></script>
  <script src="${pageContext.request.contextPath}/js/componentes.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/carrinho.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/chat.js"></script>

</body>

</html>