<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- O servidor guarda o código do erro neste atributo (404, 500…).
     A mesma página serve para "não encontrado" e para erros internos. --%>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}" />
<c:set var="erroInterno" value="${not empty codigo and codigo ge 500}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${erroInterno ? 'Erro inesperado' : 'Página não encontrada'} | Vinheria Agnello</title>
  <meta name="robots" content="noindex">
  <meta name="theme-color" content="#4A1525">
  <link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/variables.css">
  <link rel="stylesheet" href="${ctx}/css/global.css">
  <link rel="stylesheet" href="${ctx}/css/404.css">
</head>

<body class="erro-body">
  <main class="erro-card">
    <a href="${ctx}/home" class="erro-logo" aria-label="Vinheria Agnello — página inicial">
      <jsp:include page="/WEB-INF/componentes/logo-taca.jsp"><jsp:param name="id" value="erro" /><jsp:param name="classe" value="erro-logo-img" /></jsp:include>
      <span>VINHERIA AGNELLO</span>
    </a>

    <svg class="erro-icone" viewBox="0 0 64 64" aria-hidden="true">
      <path d="M20 6h24l-2 16a10 10 0 0 1-20 0z"></path>
      <path d="M32 32v18M22 58h20"></path>
      <path d="M22.5 16h19" class="erro-icone-vinho"></path>
    </svg>

    <c:choose>
      <c:when test="${erroInterno}">
        <p class="erro-codigo">Erro ${codigo}</p>
        <h1>Algo deu errado na nossa adega</h1>
        <p class="erro-texto">Tivemos um problema inesperado ao abrir esta página. Tente novamente
          daqui a alguns instantes. Se o erro continuar, fale conosco.</p>
      </c:when>
      <c:otherwise>
        <p class="erro-codigo">404</p>
        <h1>Garrafa não encontrada na adega</h1>
        <p class="erro-texto">O rótulo que você procura foi movido, já foi degustado ou nunca existiu.
          Que tal procurar outro?</p>
      </c:otherwise>
    </c:choose>

    <%-- A pesquisa abre o catálogo já filtrado (catalogo.js lê o ?q=) --%>
    <form class="erro-busca" action="${ctx}/catalogo" method="GET" role="search">
      <label for="erro-q" class="sr-only">Pesquisar vinhos</label>
      <input type="search" id="erro-q" name="q" placeholder="Buscar por nome, uva ou país">
      <button type="submit">Buscar</button>
    </form>

    <div class="erro-acoes">
      <a href="${ctx}/home" class="erro-btn erro-btn--primario">Voltar à página inicial</a>
      <a href="${ctx}/catalogo" class="erro-btn">Ver todos os vinhos</a>
    </div>

    <p class="erro-ajuda">Ou deixe o <a href="${ctx}/quiz">Sommelier Virtual</a> escolher por você.</p>
  </main>
</body>
</html>
