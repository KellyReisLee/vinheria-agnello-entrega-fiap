<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Caminho do Servlet que encaminhou para esta página (ex.: /catalogo).
     Usado para marcar o link ativo do menu. --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Páginas que passam por um Servlet chegam aqui por "forward" (ex.: /catalogo);
     as páginas institucionais são abertas direto pelo web.xml (ex.: /sobre). --%>
<c:set var="rotaForward" value="${requestScope['jakarta.servlet.forward.servlet_path']}" />
<c:set var="rotaAtual" value="${empty rotaForward ? pageContext.request.servletPath : rotaForward}" />

<%-- Disponibiliza o context path aos scripts em TODAS as páginas com header --%>
<script>
  window.CONTEXT_PATH = window.CONTEXT_PATH || '${ctx}';
  // Indica ao CSS que há JavaScript (usado nas animações de scroll)
  document.documentElement.classList.add('js');
</script>

<header class="navbar" id="site-navbar">
  <a href="${ctx}/home" class="agnello-logo-link" aria-label="Vinheria Agnello — página inicial">
    <span class="logo">
      <img class="logo-img" src="${ctx}/assets/images/logo-taca-48.png" srcset="${ctx}/assets/images/logo-taca-96.png 2x, ${ctx}/assets/images/logo-taca-144.png 3x" alt="" width="20" height="48">
      <span class="logo-text">
        <span class="logo-title">VINHERIA AGNELLO</span>
        <span class="logo-subtitle">DESDE 1978</span>
      </span>
    </span>
  </a>

  <nav class="nav-links" id="nav-principal" aria-label="Navegação principal">
    <a href="${ctx}/sobre" ${rotaAtual == '/sobre' ? 'class="active" aria-current="page"' : ''}>Nossa História</a>
    <a href="${ctx}/catalogo" ${rotaAtual == '/catalogo' ? 'class="active" aria-current="page"' : ''}>Nossos Rótulos</a>
    <a href="${ctx}/quiz" id="open-quiz" ${rotaAtual == '/quiz' or rotaAtual == '/sugestoes' ? 'class="active" aria-current="page"' : ''}>✨ Sommelier Virtual</a>
    <a href="${ctx}/catalogo#input-busca" class="nav-link-mobile">Pesquisar vinhos</a>
    <a href="${ctx}/ajuda" class="nav-link-mobile" ${rotaAtual == '/ajuda' ? 'aria-current="page"' : ''}>Central de Ajuda</a>
  </nav>

  <div class="nav-icons">
    <a href="${ctx}/catalogo#input-busca" class="icon-btn search-btn" aria-label="Pesquisar vinhos" title="Pesquisar">
      <img src="${ctx}/assets/icons/search.svg" alt="" width="22" height="22">
    </a>

    <%-- Ícone de conta muda conforme o cliente esteja ou não autenticado --%>
    <c:choose>
      <c:when test="${not empty sessionScope.clienteLogado or not empty sessionScope.usuarioLogado}">
        <a href="${ctx}/area-pessoal" class="icon-btn" aria-label="Minha conta" title="Minha conta">
          <img src="${ctx}/assets/icons/user-check.svg" alt="" width="22" height="22">
        </a>
      </c:when>
      <c:otherwise>
        <a href="${ctx}/login" class="icon-btn" aria-label="Entrar ou criar conta" title="Entrar / Cadastrar">
          <img src="${ctx}/assets/icons/user.svg" alt="" width="22" height="22">
        </a>
      </c:otherwise>
    </c:choose>

    <button type="button" class="icon-btn cart-btn" aria-label="Abrir carrinho" title="Carrinho">
      <img src="${ctx}/assets/icons/shopping-cart.svg" alt="" width="22" height="22">
      <span class="cart-badge" hidden>0</span>
    </button>

    <%-- Botão "hambúrguer": só aparece em ecrãs pequenos (ver _header.css) --%>
    <button type="button" class="icon-btn menu-toggle" aria-label="Abrir menu"
            aria-controls="nav-principal" aria-expanded="false">
      <span class="menu-toggle-bar"></span>
      <span class="menu-toggle-bar"></span>
      <span class="menu-toggle-bar"></span>
    </button>
  </div>
</header>
<div class="nav-backdrop" hidden></div>
<script src="${ctx}/js/components/header.js" defer></script>
<script src="${ctx}/js/components/verificacao-idade.js" defer></script>
