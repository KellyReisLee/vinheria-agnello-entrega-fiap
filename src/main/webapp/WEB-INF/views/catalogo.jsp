<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%-- Preços sempre no formato brasileiro (1.189,90), seja qual for o servidor --%>
<fmt:setLocale value="pt_BR" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Nossos Rótulos | Vinheria Agnello</title>
<meta name="description"
	content="Explore a adega da Vinheria Agnello: tintos, brancos, espumantes e rosés selecionados pela família desde 1978.">
<meta name="theme-color" content="#4A1525">
<link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">

<!-- Ordem: tokens -> base -> página (catalogo.css importa header, cards, carrinho e chat) -->
<link rel="stylesheet" href="${ctx}/css/variables.css">
<link rel="stylesheet" href="${ctx}/css/global.css">
<link rel="stylesheet" href="${ctx}/css/catalogo.css">

<script>
	window.CONTEXT_PATH = '${ctx}';
</script>
</head>

<body class="agnello-body">
	<a class="skip-link" href="#catalogo-resultados">Pular para os vinhos</a>

	<jsp:include page="/WEB-INF/componentes/header.jsp" />

	<main class="agnello-main" id="conteudo">
		<header class="agnello-hero">
			<span class="agnello-hero-subtitle">Seleção Boutique</span>
			<h1 class="agnello-hero-title">Nossa Adega Exclusiva</h1>
			<p class="agnello-hero-desc">Explore nossa seleção completa de
				rótulos consagrados mundialmente, divididos por regiões e perfis
				sensoriais únicos.</p>
		</header>

		<%-- Mensagem do CatalogoController quando a base de dados falha --%>
		<c:if test="${not empty errorMessage}">
			<div class="agnello-alert" role="alert">${errorMessage}</div>
		</c:if>

		<section class="agnello-toolbar" aria-label="Pesquisar e filtrar vinhos">
			<div class="agnello-search-box">
				<label for="input-busca" class="sr-only">Pesquisar vinhos</label>
				<svg class="agnello-search-icon" viewBox="0 0 24 24" aria-hidden="true">
					<circle cx="11" cy="11" r="7"></circle>
					<line x1="21" y1="21" x2="16.65" y2="16.65"></line>
				</svg>
				<input type="search" id="input-busca" class="agnello-search-input"
					placeholder="Buscar por nome, uva ou país (ex.: Malbec, Chile)"
					autocomplete="off" enterkeyhint="search">
				<button type="button" class="agnello-search-clear" aria-label="Limpar pesquisa" hidden>&times;</button>
			</div>

			<div class="agnello-filters-wrapper" role="group" aria-label="Filtrar por tipo">
				<button type="button" class="agnello-filter-btn agnello-ativo" data-filtro="todos" aria-pressed="true">Todos</button>
				<button type="button" class="agnello-filter-btn" data-filtro="tinto" aria-pressed="false">Tintos</button>
				<button type="button" class="agnello-filter-btn" data-filtro="branco" aria-pressed="false">Brancos</button>
				<button type="button" class="agnello-filter-btn" data-filtro="espumante" aria-pressed="false">Espumantes / Rosé</button>
			</div>

			<div class="agnello-sort">
				<label for="ordenar" class="sr-only">Ordenar por</label>
				<select id="ordenar" class="agnello-sort-select">
					<option value="relevancia">Ordenar por: Relevância</option>
					<option value="menor-preco">Menor preço</option>
					<option value="maior-preco">Maior preço</option>
					<option value="pontuacao">Melhor avaliados</option>
				</select>
			</div>
		</section>

		<p class="agnello-results-counter" id="catalogo-resultados" aria-live="polite">
			<span id="total-produtos">${not empty produtos ? produtos.size() : 0}</span>
			<span id="total-produtos-rotulo">rótulos encontrados</span>
		</p>

		<section id="vinhos-container" class="agnello-wine-grid" aria-label="Vinhos">
			<c:choose>
				<c:when test="${not empty produtos}">
					<c:forEach var="p" items="${produtos}" varStatus="st">
						<c:set var="imgVinho"
							value="${ctx}${p.imagem.startsWith('.') ? p.imagem.substring(1) : p.imagem}" />
						<fmt:formatNumber var="precoFmt" value="${p.preco}"
							minFractionDigits="2" maxFractionDigits="2" />
						<c:set var="esgotado" value="${p.estoque le 0}" />

						<%-- data-* servem para o catalogo.js filtrar e ordenar sem pedir nada ao servidor --%>
						<article class="product-card${esgotado ? ' is-esgotado' : ''}"
							data-ordem="${st.index}" data-tipo="${p.tipo}"
							data-nome="${p.nome}" data-origem="${p.origem}"
							data-preco="${p.preco}" data-pontuacao="${p.pontuacao}">

							<c:choose>
								<c:when test="${esgotado}">
									<span class="agnello-badge-off agnello-badge-esgotado">Esgotado</span>
								</c:when>
								<c:when test="${not empty p.desconto}">
									<span class="agnello-badge-off">${p.desconto}</span>
								</c:when>
							</c:choose>

							<div class="product-img-placeholder">
								<img src="${imgVinho}" alt="Garrafa de ${p.nome}"
									width="160" height="190" decoding="async"
									${st.index ge 8 ? 'loading="lazy"' : ''}
									onerror="this.style.visibility='hidden'">
							</div>

							<div class="product-info">
								<span class="product-meta">${p.origem} • ${p.tipo}</span>
								<h3 class="product-name">${p.nome}</h3>

								<c:if test="${not empty p.pontuacao}">
									<ul class="product-scores">
										<li><img class="score-icon" src="${ctx}/assets/icons/star-check.svg"
											alt="" width="13" height="13"> ${p.pontuacao}</li>
									</ul>
								</c:if>

								<c:if test="${not esgotado and p.estoque le 5}">
									<p class="product-stock">Últimas ${p.estoque} unidades</p>
								</c:if>
							</div>

							<div class="product-footer">
								<div class="price-box">
									<c:if test="${p.precoAntigo > 0}">
										<span class="old-price">
											<span class="sr-only">De</span>
											R$ <fmt:formatNumber value="${p.precoAntigo}"
												minFractionDigits="2" maxFractionDigits="2" />
										</span>
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
						<strong>Nenhum vinho encontrado no momento.</strong>
						<p>Por favor, tente novamente mais tarde.</p>
					</div>
				</c:otherwise>
			</c:choose>

			<%-- Mostrado pelo catalogo.js quando a pesquisa/filtro não encontra nada --%>
			<div class="grid-empty-state" id="sem-resultados" hidden>
				<strong>Nenhum rótulo corresponde à sua pesquisa.</strong>
				<p>Tente outro termo ou veja todos os vinhos da adega.</p>
				<button type="button" class="btn-outline-dark" id="limpar-filtros">Limpar filtros</button>
			</div>
		</section>
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

	<script src="${ctx}/js/pages/catalogo.js"></script>
	<script src="${ctx}/js/components/carrinho.js"></script>
	<script src="${ctx}/js/components/chat.js"></script>
</body>

</html>
