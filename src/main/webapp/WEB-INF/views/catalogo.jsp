<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Catálogo Exclusivo - Vinheria Agnello</title>
  
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quiz.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">

  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;0,700;1,400&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>

<body class="agnello-body">

  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="agnello-main">
    <section class="agnello-hero">
      <span class="agnello-hero-subtitle">Seleção Boutique</span>
      <h1 class="agnello-hero-title">Nossa Adega Exclusiva</h1>
      <p class="agnello-hero-desc">Explore nossa seleção completa de rótulos consagrados mundialmente, divididos por regiões e perfis sensoriais únicos.</p>
    </section>

    <section class="agnello-toolbar">
      <div class="agnello-search-box">
        <input type="text" id="input-busca" class="agnello-search-input" placeholder="Buscar por nome, uva ou país (ex: Malbec, Chile)...">
      </div>

      <div class="agnello-filters-wrapper">
        <button class="agnello-filter-btn agnello-ativo" data-filtro="todos">Todos</button>
        <button class="agnello-filter-btn" data-filtro="tinto">Tintos</button>
        <button class="agnello-filter-btn" data-filtro="branco">Brancos</button>
        <button class="agnello-filter-btn" data-filtro="espumante">Espumantes / Rosé</button>
      </div>

      <div class="agnello-custom-select" id="agnelloSortDropdown">
        <div class="agnello-select-selected">Ordenar por: Relevância</div>
        <div class="agnello-select-items agnello-select-hide">
          <div data-value="relevancia">Ordenar por: Relevância</div>
          <div data-value="menor-preco">Menor Preço</div>
          <div data-value="maior-preco">Maior Preço</div>
          <div data-value="pontuacao">Melhores Avaliados</div>
        </div>
      </div>
    </section>

    <div class="agnello-results-counter">
      Exibindo <span id="total-produtos">0</span> rótulos encontrados
    </div>

    <section id="vinhos-container" class="agnello-wine-grid">
    </section>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

  <div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
  <div id="agnello-cart-drawer" class="agnello-cart-drawer">
    <div class="agnello-cart-header">
      <button id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
      <h2 id="agnello-cart-title">Carrinho (0)</h2>
    </div>
    <div id="agnello-cart-body" class="agnello-cart-body"></div>
  </div>

  <script src="${pageContext.request.contextPath}/js/data/catalogo-vinhos.js"></script>
  <script src="${pageContext.request.contextPath}/js/pages/catalogo.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/carrinho.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/componentes.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/chat.js"></script>

</body>

</html>