<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Catálogo Exclusivo - Vinheria Agnello</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/catalogo.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/variables.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/global.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/quiz.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/footer.css">

<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;0,700;1,400&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap"
	rel="stylesheet">

<script>
	window.CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
</head>

<body class="agnello-body">

	<jsp:include page="/WEB-INF/componentes/header.jsp" />

	<main class="agnello-main">
		<section class="agnello-hero">
			<span class="agnello-hero-subtitle">Seleção Boutique</span>
			<h1 class="agnello-hero-title">Nossa Adega Exclusiva</h1>
			<p class="agnello-hero-desc">Explore nossa seleção completa de
				rótulos consagrados mundialmente, divididos por regiões e perfis
				sensoriais únicos.</p>
		</section>

		<section class="agnello-toolbar">
			<div class="agnello-search-box">
				<input type="text" id="input-busca" class="agnello-search-input"
					placeholder="Buscar por nome, uva ou país (ex: Malbec, Chile)...">
			</div>

			<div class="agnello-filters-wrapper">
				<button class="agnello-filter-btn agnello-ativo" data-filtro="todos">Todos</button>
				<button class="agnello-filter-btn" data-filtro="tinto">Tintos</button>
				<button class="agnello-filter-btn" data-filtro="branco">Brancos</button>
				<button class="agnello-filter-btn" data-filtro="espumante">Espumantes
					/ Rosé</button>
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

		<!-- CONTAINER CATALOGO RENDERIZADO VIA JSTL -->
		<section id="vinhos-container" class="agnello-wine-grid">
			<c:choose>
				<c:when test="${not empty produtos}">
					<c:forEach var="p" items="${produtos}">
						<article class="product-card" data-tipo="${p.tipo}"
							data-nome="${p.nome}" data-origem="${p.origem}"
							data-preco="${p.preco}" data-pontuacao="${p.pontuacao}">

							<!-- Selo de Desconto Opcional -->
							<c:if test="${not empty p.desconto}">
								<span class="agnello-badge-off">${p.desconto}</span>
							</c:if>

							<!-- Imagem com tratamento de erro e contextPath -->
							<div class="product-img-placeholder">
								<img
									src="${pageContext.request.contextPath}${p.imagem.startsWith('.') ? p.imagem.substring(1) : p.imagem}"
									alt="${p.nome}"
									onerror="this.style.display='none'; this.parentElement.style.backgroundColor='#F8F7F4';">
							</div>

							<!-- Informações do Vinho -->
							<div class="product-info">
								<span class="product-meta">${p.origem} • ${p.tipo}</span>
								<h3 class="product-name">${p.nome}</h3>

								<c:if test="${not empty p.pontuacao}">
									<ul class="product-scores">
										<li><img class="score-icon"
											src="${pageContext.request.contextPath}/assets/icons/star-check.svg"
											alt="Estrela"> ${p.pontuacao}</li>
									</ul>
								</c:if>
							</div>

							<!-- Rodapé do Card com Preço e Botão de Ação -->
							<div class="product-footer">
								<div class="price-box">
									<c:if test="${p.precoAntigo > 0}">
										<span class="old-price">R$ <fmt:formatNumber
												value="${p.precoAntigo}" minFractionDigits="2"
												maxFractionDigits="2" /></span>
									</c:if>
									<span class="product-price">R$ <fmt:formatNumber
											value="${p.preco}" minFractionDigits="2"
											maxFractionDigits="2" /></span>
								</div>
								<button class="agnello-btn-comprar" aria-label="Selecionar"
									data-id="${p.id}" data-nome="${p.nome}"
									data-preco="R$ <fmt:formatNumber value='${p.preco}' minFractionDigits='2' maxFractionDigits='2' />"
									data-origem="${p.origem} • ${p.tipo}"
									data-imagem="${pageContext.request.contextPath}${p.imagem.startsWith('.') ? p.imagem.substring(1) : p.imagem}">
									Selecionar</button>

							</div>

						</article>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<!-- Estado Vazio -->
					<div class="grid-empty-state"
						style="grid-column: 1/-1; text-align: center; padding: 3rem 1.5rem; background-color: #faf8f6; border: 1px dashed #dcd5d0; border-radius: 8px; margin: 1rem 0;">
						<p
							style="font-size: 1.1rem; color: #4a1525; font-weight: 600; margin-bottom: 0.5rem;">Nenhum
							vinho encontrado no momento.</p>
						<p style="font-size: 0.9rem; color: #736b6d;">Por favor, tente
							novamente mais tarde ou ajuste os seus filtros de busca.</p>
					</div>
				</c:otherwise>
			</c:choose>
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

	<!-- Scripts -->
	<script src="${pageContext.request.contextPath}/js/pages/catalogo.js"></script>
	<script
		src="${pageContext.request.contextPath}/js/components/carrinho.js"></script>
	<script
		src="${pageContext.request.contextPath}/js/components/componentes.js"></script>
	<script src="${pageContext.request.contextPath}/js/components/chat.js"></script>

</body>

</html>