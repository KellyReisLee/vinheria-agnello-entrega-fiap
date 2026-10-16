<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- Preços sempre no formato brasileiro (1.189,90), seja qual for o servidor --%>
<fmt:setLocale value="pt_BR" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sugestões do Sommelier | Vinheria Agnello</title>
  <meta name="theme-color" content="#4A1525">
  <link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link
    href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap"
    rel="stylesheet">

  <link rel="stylesheet" href="${ctx}/css/variables.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/global.css?v=${initParam.versaoArquivos}">
  <%-- catalogo.css traz o header, os cards e a grelha .agnello-wine-grid --%>
  <link rel="stylesheet" href="${ctx}/css/catalogo.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/sugestoes-sommelier.css?v=${initParam.versaoArquivos}">

  <script>
    window.CONTEXT_PATH = '${ctx}';
  </script>
</head>
<body class="sugestoes-page-body">

  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="sugestoes-main" id="conteudo">
    <div class="sugestoes-container">

      <header class="sugestoes-header">
        <span class="sugestoes-eyebrow">Seleção do Sommelier Virtual</span>
        <h1 class="sugestoes-title">Sua Adega Personalizada</h1>
        <%-- Texto e "chips" do perfil preenchidos pelo sugestoes-sommelier.js --%>
        <p class="sugestoes-subtitle" id="perfil-descricao">Preparando suas sugestões…</p>
        <ul class="sugestoes-perfil" id="perfil-resumo" aria-label="As suas respostas" hidden></ul>
      </header>

      <c:if test="${not empty errorMessage}">
        <div class="agnello-alert" role="alert">${errorMessage}</div>
      </c:if>

      <%-- Mostrado quando a pessoa chega aqui sem ter feito o quiz --%>
      <div class="sugestoes-sem-quiz" id="sem-quiz" hidden>
        <p>Ainda não conhecemos o seu paladar. Responda a 3 perguntas rápidas e
          o Sommelier Virtual escolhe os rótulos certos para você.</p>
        <a href="${ctx}/quiz" class="btn-gold">Fazer o quiz <span class="btn-arrow" aria-hidden="true">→</span></a>
      </div>

      <section id="vinhos-container" class="agnello-wine-grid" aria-label="Vinhos sugeridos">
        <c:choose>
          <c:when test="${not empty produtos}">
            <c:forEach var="p" items="${produtos}" varStatus="st">
              <c:set var="imgVinho"
                value="${ctx}${p.imagem.startsWith('.') ? p.imagem.substring(1) : p.imagem}" />
              <fmt:formatNumber var="precoFmt" value="${p.preco}" minFractionDigits="2" maxFractionDigits="2" />
              <c:set var="esgotado" value="${p.estoque le 0}" />

              <article class="product-card${esgotado ? ' is-esgotado' : ''}"
                       data-ordem="${st.index}" data-tipo="${p.tipo}"
                       data-nome="${p.nome}" data-origem="${p.origem}"
                       data-preco="${p.preco}" data-pontuacao="${p.pontuacao}"
                       data-estoque="${p.estoque}">

                <c:choose>
                  <c:when test="${esgotado}">
                    <span class="agnello-badge-off agnello-badge-esgotado">Esgotado</span>
                  </c:when>
                  <c:when test="${not empty p.desconto}">
                    <span class="agnello-badge-off">${p.desconto}</span>
                  </c:when>
                </c:choose>

                <div class="product-img-placeholder">
                  <img src="${imgVinho}" alt="Garrafa de ${p.nome}" width="160" height="190"
                       loading="lazy" decoding="async" onerror="this.style.visibility='hidden'">
                </div>

                <div class="product-info">
                  <span class="product-meta">${p.origem} • ${p.tipo}</span>
                  <h3 class="product-name"><a class="product-link" href="${ctx}/produto?id=${p.id}"><c:out value="${p.nome}" /></a></h3>
                  <c:if test="${not empty p.descricao}">
                    <p class="product-desc">${p.descricao}</p>
                  </c:if>
                  <c:if test="${not empty p.pontuacao}">
                    <ul class="product-scores">
                      <li><img class="score-icon" src="${ctx}/assets/icons/star-check.svg"
                        alt="" width="13" height="13"> ${p.pontuacao}</li>
                    </ul>
                  </c:if>
                </div>

                <div class="product-footer">
                  <div class="price-box">
                    <c:if test="${p.precoAntigo > 0}">
                      <span class="old-price"><span class="sr-only">De</span>
                        R$ <fmt:formatNumber value="${p.precoAntigo}" minFractionDigits="2" maxFractionDigits="2" /></span>
                    </c:if>
                    <span class="product-price">
                      <c:if test="${p.precoAntigo > 0}"><span class="sr-only">por</span></c:if>
                      R$ ${precoFmt}
                    </span>
                  </div>
                  <c:choose>
                    <c:when test="${esgotado}">
                      <button type="button" class="agnello-btn-comprar" disabled>Esgotado</button>
                    </c:when>
                    <c:otherwise>
                      <button type="button" class="agnello-btn-comprar"
                              aria-label="Adicionar ${p.nome} ao carrinho"
                              data-id="${p.id}" data-nome="${p.nome}"
                              data-preco="R$ ${precoFmt}"
                              data-origem="${p.origem} • ${p.tipo}"
                              data-imagem="${imgVinho}">Selecionar</button>
                    </c:otherwise>
                  </c:choose>
                </div>
              </article>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <div class="grid-empty-state">
              <strong>Nenhum vinho disponível no momento.</strong>
              <p>Tente novamente mais tarde ou fale conosco pelo chat.</p>
            </div>
          </c:otherwise>
        </c:choose>
      </section>

      <div class="sugestoes-actions">
        <a href="${ctx}/quiz" class="btn-outline-dark">
          <span aria-hidden="true">↺</span> Refazer o quiz
        </a>
        <a href="${ctx}/catalogo" class="sugestoes-link-catalogo">
          Ver toda a adega <span class="btn-arrow" aria-hidden="true">→</span>
        </a>
      </div>

    </div>
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

  <script src="${ctx}/js/pages/sugestoes-sommelier.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/carrinho.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/chat.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
