<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Vinheria Agnello | Desde 1978</title>

  <!-- Favicon -->
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">

  <!-- Folhas de Estilo (Com Context Path) -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animations.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sections/_chat.css">

  <!-- Google Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <script>
    window.CONTEXT_PATH = '${pageContext.request.contextPath}';
  </script>
</head>

<body>
  <!-- HEADER MODULAR (Protegido em WEB-INF) -->
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main>
    <!-- HERO SECTION -->
    <section class="hero" style="background: linear-gradient(rgba(11, 11, 11, 0.6), rgba(39, 3, 14, 0.85)), url('${pageContext.request.contextPath}/assets/images/hero-image.jpg') center/cover no-repeat;">
      <div class="hero-content">
        <span class="hero-tag">⭐ CURADORIA FAMILIAR</span>
        <h1 class="hero-title">Da nossa família <span>para a sua mesa.</span></h1>
        <p class="hero-subtitle">Descubra rótulos selecionados à mão pelo Sr. Giulio e sua filha Bianca, trazendo o calor e a expertise da nossa loja física direto para o seu lar.</p>
        <div class="hero-buttons">
          <a href="${pageContext.request.contextPath}/quiz" class="btn-gold" id="hero-quiz-btn">Iniciar Quiz do Sommelier →</a>
          <a href="${pageContext.request.contextPath}/catalogo" class="btn-outline">Ver Catálogo</a>
        </div>
      </div>
    </section>

    <!-- SEÇÃO FAMÍLIA -->
    <section class="family-section">
      <div class="family-container">
        <div class="family-image-wrapper scroll-animate-left">
          <div class="image-frame-decoration"></div>
          <div class="family-image-box">
            <img src="${pageContext.request.contextPath}/assets/images/home-atendimento-image.jpg" alt="Taça com vinho branco">
          </div>
          <div class="family-mini-badge">
            <img src="${pageContext.request.contextPath}/assets/icons/shield-check.svg" alt="">
            <div>
              <strong>ATENDIMENTO</strong>
              <p>Artesanal & Pessoal</p>
            </div>
          </div>
        </div>

        <div class="family-text scroll-animate-right is-visible">
          <h2>Aqui você não compra <span class="italic-text">de um algoritmo.</span></h2>
          <p class="family-desc-lead">Desde 1978, a Vinheria Agnello é mais que uma loja; é a extensão da sala de estar do Sr. Giulio. Cada garrafa em nosso catálogo não foi escolhida por planilhas de vendas, mas sim degustada e aprovada rigorosamente por nossa família.</p>
          <p>Sabemos que um bom vinho tem o poder de transformar um jantar comum em uma memória inesquecível. Com a ajuda da Bianca, trouxemos a experiência do nosso tradicional balcão de madeira para o mundo digital.</p>

          <div class="founders-profile">
            <div class="founders-avatar">AG</div>
            <div>
              <strong>Giulio & Bianca Agnello</strong>
              <span class="founders-role">FUNDADORES</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- SEÇÃO CALCULADORA DE EVENTOS -->
    <section class="unique-events-calc-section" aria-labelledby="calc-section-title">
      <div class="unique-calc-container">
        <div class="unique-calc-info unique-scroll-left">
          <header class="unique-calc-header">
            <span class="unique-calc-tag">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
                <line x1="12" y1="22.08" x2="12" y2="12"></line>
              </svg>
              FERRAMENTA EXCLUSIVA
            </span>
            <h2 id="calc-section-title">Calculadora de Eventos</h2>
          </header>

          <p class="unique-cal-text">
            Vai receber amigos ou planejar uma festa? Não deixe faltar nem sobrar. Informe os detalhes do seu evento e o Sr. Giulio calculará a quantidade exata e a proporção ideal de rótulos.
          </p>

          <div class="unique-calc-notice" role="note">
            <span>
              <img src="${pageContext.request.contextPath}/assets/icons/icons-home/circle-alert.svg" alt="" aria-hidden="true">
            </span>
            <p>O cálculo baseia-se em métricas de sommelier profissional, ajustando a proporção de tintos, brancos e espumantes conforme o tipo de evento e a estação do ano.</p>
          </div>
        </div>

        <div class="unique-calculator-box unique-scroll-right">
          <div class="unique-inputs-row">
            <!-- Grupo de Convidados -->
            <div class="unique-input-group">
              <label id="label-convidados">CONVIDADOS</label>
              <div class="unique-input-field-control" aria-labelledby="label-convidados">
                <button type="button" class="unique-ctrl-btn" id="btn-menos-convidados" aria-label="Diminuir convidados">-</button>
                <span id="valor-convidados">12</span>
                <button type="button" class="unique-ctrl-btn" id="btn-mais-convidados" aria-label="Aumentar convidados">+</button>
              </div>
            </div>

            <!-- Grupo de Duração -->
            <div class="unique-input-group">
              <label id="label-duracao">DURAÇÃO (HORAS)</label>
              <div class="unique-input-field-control" aria-labelledby="label-duracao">
                <button type="button" class="unique-ctrl-btn" id="btn-menos-duracao" aria-label="Diminuir duração">-</button>
                <span id="valor-duracao">4</span>
                <button type="button" class="unique-ctrl-btn" id="btn-mais-duracao" aria-label="Aumentar duração">+</button>
              </div>
            </div>
          </div>

          <div class="unique-calc-event-type">
            <span class="unique-event-label" id="estilo-evento-label">ESTILO DO EVENTO</span>
            <div class="unique-event-buttons-wrapper" role="group" aria-labelledby="estilo-evento-label">
              <button type="button" class="unique-type-btn active" data-estilo="jantar">Jantar</button>
              <button type="button" class="unique-type-btn" data-estilo="festa">Festa</button>
              <button type="button" class="unique-type-btn" data-estilo="coquetel">Coquetel</button>
            </div>
          </div>

          <div class="unique-calc-result-box">
            <span class="unique-result-label">RECOMENDAÇÃO IDEAL</span>
            <div class="unique-result-total">10 <span aria-hidden="true">Garrafas</span></div>

            <div class="unique-result-breakdown">
              <div class="unique-breakdown-item">
                <span>TINTO</span>
                <strong>6</strong>
              </div>
              <div class="unique-breakdown-item">
                <span>BRANCO</span>
                <strong>3</strong>
              </div>
              <div class="unique-breakdown-item">
                <span>ESPUMANTE</span>
                <strong>1</strong>
              </div>
            </div>

            <button type="button" class="unique-btn-calc-action">Montar Kit Personalizado →</button>
          </div>
        </div>
      </div>
    </section>

    <!-- VITRINE (Híbrida: Suporta JSTL do Servidor ou fallback para JS) -->
    <section class="vitrine-section">
      <div class="section-header">
        <div>
          <h2>Achados da Semana</h2>
          <p>Rótulos de importação própria recém-chegados à nossa adega.</p>
        </div>
      </div>

      <div class="product-grid" id="vitrine-achados-container">
        <c:choose>
          <c:when test="${not empty listaVinhos}">
            <c:forEach var="vinho" items="${listaVinhos}">
              <div class="card">
                <h3>${vinho.nome}</h3>
                <p>Safra: ${vinho.safra}</p>
                <p>Preço: R$ ${vinho.preco}</p>
                <c:if test="${vinho.emPromocao}">
                  <span class="badge-promo">Oferta Exclusiva</span>
                </c:if>
              </div>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <!-- Caso venha vazio, o script JS original de achados-da-semana.js preencherá aqui -->
          </c:otherwise>
        </c:choose>
      </div>

      <div class="center-action">
        <a href="${pageContext.request.contextPath}/catalogo" class="btn-outline-dark">Ver Toda a Adega</a>
      </div>
    </section>
  </main>

  <!-- FOOTER MODULAR (Protegido em WEB-INF) -->
  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

  <!-- CARRINHO DRAWER -->
  <div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
  <div id="agnello-cart-drawer" class="agnello-cart-drawer">
    <div class="agnello-cart-header">
      <button id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
      <h2 id="agnello-cart-title">Carrinho (0)</h2>
    </div>
    <div id="agnello-cart-body" class="agnello-cart-body"></div>
  </div>

  <!-- IMPORTAÇÃO DE SCRIPTS COM CONTEXT PATH -->
  <script src="${pageContext.request.contextPath}/js/catalogo-vinhos.js"></script>
  <script src="${pageContext.request.contextPath}/js/componentes.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/calculadora.js"></script>
  <script src="${pageContext.request.contextPath}/js/data/achados-da-semana.js"></script>
  <script src="${pageContext.request.contextPath}/js/pages/home.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/carrinho.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/chat.js"></script>

</body>

</html>