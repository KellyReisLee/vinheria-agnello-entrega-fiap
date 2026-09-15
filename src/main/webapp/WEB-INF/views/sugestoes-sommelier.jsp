<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sugestões do Sommelier — Vinheria Agnello</title>

  <link rel="stylesheet" href="<c:url value='/css/variables.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/global.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/quiz.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/sugestoes-sommelier.css'/>">
</head>

<body class="sugestoes-page-body-unique">

   <!-- HEADER MODULAR -->
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="sugestoes-main-wrapper-unique">
    <div class="sugestoes-container-unique">

      <div class="sugestoes-header-unique">
        <span class="calc-tag-unique">SELEÇÃO EXCLUSIVA DE RÓTULOS</span>
        <h1 class="sugestoes-title-unique">Sua Adega Personalizada</h1>
        <p class="sugestoes-subtitle-unique" id="perfil-descricao-unique">Buscando vinhos harmonizados com o seu perfil...</p>
      </div>

      <!-- GRID DE PRODUTOS ESTILO E-COMMERCE -->
      <div class="vinhos-grid-unique" id="vinhos-container-unique">
        <!-- Os cards de vinho com imagem, badges e preços serão injetados aqui via JS -->
      </div>

      <div class="sugestoes-actions-unique">
        <a href="<c:url value='/quiz'/>" class="btn-refazer-unique">Refazer o Quiz</a>
      </div>

    </div>
  </main>

  <div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
  <div id="agnello-cart-drawer" class="agnello-cart-drawer">
    <div class="agnello-cart-header">
      <button id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
      <h2 id="agnello-cart-title">Carrinho (0)</h2>
    </div>
    <div id="agnello-cart-body" class="agnello-cart-body"></div>
  </div>

    <!-- FOOTER MODULAR -->
  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  
  <script src="<c:url value='/js/pages/sugestoes-sommelier.js'/>"></script>
  <script src="<c:url value='/js/data/catalogo-vinhos.js'/>"></script>
  <script src="<c:url value='/js/components/carrinho.js'/>"></script>
  <script src="<c:url value='/js/components/componentes.js'/>"></script>
  <script src="<c:url value='/js/components/chat.js'/>"></script>

</body>
</html>