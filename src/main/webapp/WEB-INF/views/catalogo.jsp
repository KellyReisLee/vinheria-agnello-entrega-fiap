<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
  <link rel="stylesheet" href="<c:url value='/css/catalogo.css'/>">

  <script>
    window.CONTEXT_PATH = '${pageContext.request.contextPath}';
  </script>
</head>
<body class="sugestoes-page-body-unique">

   <!-- HEADER MODULAR -->
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="sugestoes-main-wrapper-unique">
    <div class="sugestoes-container-unique">

      <div class="sugestoes-header-unique">
        <span class="calc-tag-unique">SELEÇÃO EXCLUSIVA DE RÓTULOS</span>
        <h1 class="sugestoes-title-unique">Sua Adega Personalizada</h1>
        <p class="sugestoes-subtitle-unique" id="perfil-descricao-unique">Filtramos nossa adega com base nas suas preferências indicadas no quiz. Veja as melhores opções:</p>
      </div>

      <!-- GRID DE PRODUTOS RENDERIZADO VIA JSTL -->
      <section id="vinhos-container" class="agnello-wine-grid">
        <c:choose>
          <c:when test="${not empty produtos}">
            <c:forEach var="p" items="${produtos}">
              <article class="product-card" 
                       data-tipo="${p.tipo}"
                       data-nome="${p.nome}" 
                       data-origem="${p.origem}"
                       data-preco="${p.preco}" 
                       data-pontuacao="${p.pontuacao}">

                <!-- Selo de Desconto Opcional -->
                <c:if test="${not empty p.desconto}">
                  <span class="agnello-badge-off">${p.desconto}</span>
                </c:if>

                <!-- Imagem com tratamento de caminho -->
                <div class="product-img-placeholder">
                  <img src="${pageContext.request.contextPath}${fn:startsWith(p.imagem, '.') ? fn:substring(p.imagem, 1, fn:length(p.imagem)) : p.imagem}"
                       alt="${p.nome}"
                       onerror="this.style.display='none'; this.parentElement.style.backgroundColor='#F8F7F4';">
                </div>

                <!-- Informações do Vinho -->
                <div class="product-info">
                  <span class="product-meta">${p.origem} • ${p.tipo}</span>
                  <h3 class="product-name">${p.nome}</h3>
                  <p class="product-desc" style="font-size: 0.85rem; color: #736b6d; margin-top: 4px;">${p.descricao}</p>

                  <c:if test="${not empty p.pontuacao}">
                    <ul class="product-scores" style="margin-top: 8px;">
                      <li>
                        <img class="score-icon" src="${pageContext.request.contextPath}/assets/icons/star-check.svg" alt="Estrela"> 
                        ${p.pontuacao}
                      </li>
                    </ul>
                  </c:if>
                </div>

                <!-- Rodapé do Card com Preço e Botão de Ação -->
                <div class="product-footer">
                  <div class="price-box">
                    <c:if test="${p.precoAntigo > 0}">
                      <span class="old-price">R$ <fmt:formatNumber value="${p.precoAntigo}" minFractionDigits="2" maxFractionDigits="2" /></span>
                    </c:if>
                    <span class="product-price">R$ <fmt:formatNumber value="${p.preco}" minFractionDigits="2" maxFractionDigits="2" /></span>
                  </div>
                  <button class="agnello-btn-comprar" aria-label="Selecionar"
                          data-id="${p.id}" 
                          data-nome="${p.nome}"
                          data-preco="R$ <fmt:formatNumber value='${p.preco}' minFractionDigits='2' maxFractionDigits='2' />"
                          data-origem="${p.origem} • ${p.tipo}"
                          data-imagem="${pageContext.request.contextPath}${fn:startsWith(p.imagem, '.') ? fn:substring(p.imagem, 1, fn:length(p.imagem)) : p.imagem}">
                    Selecionar
                  </button>
                </div>

              </article>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <!-- Estado Vazio -->
            <div class="grid-empty-state" style="grid-column: 1/-1; text-align: center; padding: 3rem 1.5rem; background-color: #faf8f6; border: 1px dashed #dcd5d0; border-radius: 8px; margin: 1rem 0;">
              <p style="font-size: 1.1rem; color: #4a1525; font-weight: 600; margin-bottom: 0.5rem;">Nenhum vinho encontrado para o seu perfil.</p>
              <p style="font-size: 0.9rem; color: #736b6d;">Tente refazer o quiz alterando algumas das suas preferências.</p>
            </div>
          </c:otherwise>
        </c:choose>
      </section>

      <div class="sugestoes-actions-unique" style="margin-top: 2rem; text-align: center;">
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

  <!-- SCRIPTS -->
  <script src="<c:url value='/js/pages/sugestoes-sommelier.js'/>"></script>
  <script src="<c:url value='/js/components/carrinho.js'/>"></script>
  <script src="<c:url value='/js/components/componentes.js'/>"></script>
  <script src="<c:url value='/js/components/chat.js'/>"></script>

</body>
</html>