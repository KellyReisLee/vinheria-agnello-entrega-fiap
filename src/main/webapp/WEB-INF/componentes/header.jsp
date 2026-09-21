<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

   <!-- Verificação se o cliente está logado na Sessão -->
  
    <c:choose>
        <c:when test="${not empty sessionScope.clienteLogado or not empty sessionScope.usuarioLogado}">
            <!-- URL limpa apontando para a área pessoal -->
            <a href="${pageContext.request.contextPath}/area-pessoal" class="icon-btn" aria-label="Área Pessoal" title="Minha Conta">
                <img src="${pageContext.request.contextPath}/assets/icons/user-check.svg" alt="perfil verificado">
            </a>
        </c:when>
        <c:otherwise>
            <!-- Se NÃO estiver logado, redireciona para a página de login -->
            <a href="${pageContext.request.contextPath}/login" class="icon-btn" aria-label="Entrar" title="Entrar / Cadastrar">
                <img src="${pageContext.request.contextPath}/assets/icons/user.svg" alt="perfil">
            </a>
        </c:otherwise>
    </c:choose>
    <button class="icon-btn cart-btn">
      <img src="${pageContext.request.contextPath}/assets/icons/shopping-cart.svg" alt="carrinho">
      <span class="cart-badge" style="display:none;">0</span>
    </button>
  </div>
</header>