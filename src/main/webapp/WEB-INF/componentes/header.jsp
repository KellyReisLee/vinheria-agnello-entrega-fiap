<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header class="navbar">
  <a href="${pageContext.request.contextPath}/home" class="agnello-logo-link" style="text-decoration: none; display: inline-flex; align-items: center;">
    <div class="logo">
      <img class="logo-img" src="${pageContext.request.contextPath}/assets/images/logo-no-bg.png" alt="Ícone Vinheria Agnello">
      <div>
        <span class="logo-title">VINHERIA AGNELLO</span>
        <span class="logo-subtitle">DESDE 1978</span>
      </div>
    </div>
  </a>
  
  <nav class="nav-links">
    <a href="${pageContext.request.contextPath}/home">Nossa História</a>
    <a href="${pageContext.request.contextPath}/catalogo">Nossos Rótulos</a>
    <a href="${pageContext.request.contextPath}/quiz" id="open-quiz">✨ Sommelier Virtual</a>
  </nav>
  
  <div class="nav-icons">
    <button class="icon-btn"><img src="${pageContext.request.contextPath}/assets/icons/search.svg" alt="lupa"></button>

  <a href="${pageContext.request.contextPath}/login" class="icon-btn" aria-label="Entrar">
  <img src="${pageContext.request.contextPath}/assets/icons/user.svg" alt="perfil">
</a>
  
    <button class="icon-btn cart-btn">
      <img src="${pageContext.request.contextPath}/assets/icons/shopping-cart.svg" alt="carrinho">
      <span class="cart-badge" style="display:none;">0</span>
    </button>
  </div>
</header>