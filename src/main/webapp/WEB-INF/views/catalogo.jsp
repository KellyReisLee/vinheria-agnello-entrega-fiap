<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
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
<link rel="stylesheet" href="${ctx}/css/variables.css?v=${initParam.versaoArquivos}">
<link rel="stylesheet" href="${ctx}/css/global.css?v=${initParam.versaoArquivos}">
<link rel="stylesheet" href="${ctx}/css/catalogo.css?v=${initParam.versaoArquivos}">

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

		<%-- Filtros num formulário GET: funciona sem JavaScript, o endereço guarda
		     a busca (dá para compartilhar) e o banco devolve só a página pedida. --%>
		<c:set var="f" value="${filtro}" />
		<form class="agnello-toolbar" id="form-catalogo" method="get" action="${ctx}/catalogo" role="search"
			aria-label="Pesquisar e filtrar vinhos">
			<div class="agnello-search-box">
				<label for="input-busca" class="sr-only">Pesquisar vinhos</label>
				<svg class="agnello-search-icon" viewBox="0 0 24 24" aria-hidden="true">
					<circle cx="11" cy="11" r="7"></circle>
					<line x1="21" y1="21" x2="16.65" y2="16.65"></line>
				</svg>
				<input type="search" id="input-busca" name="busca" class="agnello-search-input" maxlength="80"
					placeholder="Buscar por nome, uva ou país"
					autocomplete="off" enterkeyhint="search" value="<c:out value='${f.busca}' />">
				<button type="button" class="agnello-search-clear" aria-label="Limpar pesquisa" ${empty f.busca ? 'hidden' : ''}>&times;</button>
			</div>

			<fieldset class="agnello-filters-wrapper">
				<legend class="sr-only">Tipo de vinho</legend>
				<label class="agnello-filter-btn ${empty f.tipo ? 'agnello-ativo' : ''}">
					<input type="radio" name="tipo" value="" ${empty f.tipo ? 'checked' : ''}> Todos</label>
				<c:forEach var="t" items="${f.tipos}">
					<label class="agnello-filter-btn ${f.tipo eq t.key ? 'agnello-ativo' : ''}">
						<input type="radio" name="tipo" value="${t.key}" ${f.tipo eq t.key ? 'checked' : ''}> ${t.value}</label>
				</c:forEach>
			</fieldset>

			<div class="agnello-sort">
				<label for="ordenar" class="sr-only">Ordenar por</label>
				<select id="ordenar" name="ordem" class="agnello-sort-select">
					<c:forEach var="o" items="${f.ordens}">
						<option value="${o.key}" ${f.ordem eq o.key ? 'selected' : ''}>${o.key eq 'relevancia' ? 'Ordenar por: ' : ''}${o.value}</option>
					</c:forEach>
				</select>
			</div>

			<div class="agnello-toolbar-extra">
				<div class="agnello-campo-filtro">
					<label for="filtro-pais">País</label>
					<select id="filtro-pais" name="pais" class="agnello-sort-select">
						<option value="">Todos os países</option>
						<c:forEach var="pais" items="${paises}">
							<option value="<c:out value='${pais}' />" ${f.pais eq pais ? 'selected' : ''}><c:out value="${pais}" /></option>
						</c:forEach>
					</select>
				</div>
				<div class="agnello-campo-filtro agnello-faixa-preco">
					<span class="agnello-rotulo" id="rotulo-preco">Preço (R$)</span>
					<input type="number" name="precoMin" min="0" step="1" inputmode="numeric" placeholder="De"
						aria-labelledby="rotulo-preco" aria-label="Preço mínimo" value="${f.precoMinTexto}">
					<span aria-hidden="true">–</span>
					<input type="number" name="precoMax" min="0" step="1" inputmode="numeric" placeholder="Até"
						aria-label="Preço máximo" value="${f.precoMaxTexto}">
				</div>
				<label class="agnello-check-filtro">
					<input type="checkbox" name="disponiveis" value="1" ${f.soDisponiveis ? 'checked' : ''}> Só disponíveis
				</label>
				<button type="submit" class="agnello-btn-aplicar">Aplicar filtros</button>
			</div>
		</form>

		<%-- Base dos links de paginação: a mesma busca, sem o número da página --%>
		<c:set var="urlBusca" value="${ctx}/catalogo${f.query}" />
		<c:set var="sep" value="${f.separador}" />

		<div class="agnello-results-bar" id="catalogo-resultados">
			<p class="agnello-results-counter" aria-live="polite">
				<c:choose>
					<c:when test="${empty pagina or pagina.total eq 0}">Nenhum rótulo encontrado</c:when>
					<c:otherwise>
						<span id="total-produtos">${pagina.total}</span> ${pagina.total eq 1 ? 'rótulo encontrado' : 'rótulos encontrados'}
						<c:if test="${pagina.totalPaginas gt 1}"><span class="agnello-results-faixa">· mostrando ${pagina.primeiro}–${pagina.ultimo}</span></c:if>
					</c:otherwise>
				</c:choose>
			</p>
			<c:if test="${f.filtrando}">
				<ul class="agnello-filtros-ativos" aria-label="Filtros aplicados">
					<c:forEach var="a" items="${f.ativos}">
						<li><a href="${ctx}/catalogo${a.query}" class="agnello-chip-ativo"
							aria-label="Remover filtro <c:out value='${a.rotulo}' />"><c:out value="${a.rotulo}" /> <span aria-hidden="true">×</span></a></li>
					</c:forEach>
					<li><a href="${ctx}/catalogo" class="agnello-limpar-filtros">Limpar tudo</a></li>
				</ul>
			</c:if>
		</div>

		<section id="vinhos-container" class="agnello-wine-grid" aria-label="Vinhos">
			<c:choose>
				<c:when test="${not empty pagina and not empty pagina.itens}">
					<c:forEach var="p" items="${pagina.itens}" varStatus="st">
						<c:set var="imgVinho" value="${ctx}${p.imagemUrl}" />
						<fmt:formatNumber var="precoFmt" value="${p.preco}" minFractionDigits="2" maxFractionDigits="2" />
						<c:set var="esgotado" value="${p.estoque le 0}" />
						<c:set var="favorito" value="${favoritosIds.contains(p.id)}" />

						<article class="product-card${esgotado ? ' is-esgotado' : ''}">
							<c:choose>
								<c:when test="${esgotado}">
									<span class="agnello-badge-off agnello-badge-esgotado">Esgotado</span>
								</c:when>
								<c:when test="${not empty p.desconto}">
									<span class="agnello-badge-off"><c:out value="${p.desconto}" /></span>
								</c:when>
							</c:choose>

							<%-- Coração: guarda o vinho nos favoritos (favoritos.js) --%>
							<button type="button" class="fav-btn${favorito ? ' is-favorito' : ''}" data-produto="${p.id}"
								aria-pressed="${favorito}" data-nome="${fn:escapeXml(p.nome)}"
								aria-label="${favorito ? 'Remover ' : 'Guardar '}${fn:escapeXml(p.nome)}${favorito ? ' dos favoritos' : ' nos favoritos'}">
								<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s-7.5-4.6-9.5-9.2C1.2 8.7 3.3 5 7 5c2.1 0 3.6 1.1 5 2.8C13.4 6.1 14.9 5 17 5c3.7 0 5.8 3.7 4.5 6.8C19.5 16.4 12 21 12 21z"/></svg>
							</button>

							<div class="product-img-placeholder">
								<img src="${imgVinho}" alt="Garrafa de <c:out value='${p.nome}' />"
									width="160" height="190" decoding="async"
									${st.index ge 8 ? 'loading="lazy"' : ''}
									onerror="this.style.visibility='hidden'">
							</div>

							<div class="product-info">
								<span class="product-meta"><c:out value="${p.origem}" /> • <c:out value="${p.tipo}" /></span>
								<h3 class="product-name"><a class="product-link" href="${ctx}/produto?id=${p.id}"><c:out value="${p.nome}" /></a></h3>

								<c:if test="${not empty p.pontuacao}">
									<ul class="product-scores">
										<li><img class="score-icon" src="${ctx}/assets/icons/star-check.svg"
											alt="" width="13" height="13"> <c:out value="${p.pontuacao}" /></li>
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
											R$ <fmt:formatNumber value="${p.precoAntigo}" minFractionDigits="2" maxFractionDigits="2" />
										</span>
									</c:if>
									<span class="product-price">
										<c:if test="${p.precoAntigo > 0}"><span class="sr-only">por</span></c:if>
										R$ ${precoFmt}
									</span>
								</div>
								<c:choose>
									<c:when test="${esgotado}">
										<%-- Leva ao "Avise-me quando chegar" da página do vinho --%>
										<a href="${ctx}/produto?id=${p.id}#avise-me" class="agnello-btn-avise"
											aria-label="Avise-me quando <c:out value='${p.nome}' /> chegar">Avise-me</a>
									</c:when>
									<c:otherwise>
										<button type="button" class="agnello-btn-comprar"
											aria-label="Adicionar <c:out value='${p.nome}' /> ao carrinho"
											data-id="${p.id}" data-nome="<c:out value='${p.nome}' />"
											data-preco="R$ ${precoFmt}"
											data-origem="<c:out value='${p.origem} • ${p.tipo}' />"
											data-imagem="${imgVinho}">Selecionar</button>
									</c:otherwise>
								</c:choose>
							</div>
						</article>
					</c:forEach>
				</c:when>
				<c:when test="${f.filtrando}">
					<div class="grid-empty-state">
						<strong>Nenhum rótulo corresponde à sua pesquisa.</strong>
						<p>Tente outro termo, afrouxe os filtros ou veja todos os vinhos da adega.</p>
						<a href="${ctx}/catalogo" class="btn-outline-dark">Limpar filtros</a>
					</div>
				</c:when>
				<c:otherwise>
					<div class="grid-empty-state">
						<strong>Nenhum vinho encontrado no momento.</strong>
						<p>Por favor, tente novamente mais tarde.</p>
					</div>
				</c:otherwise>
			</c:choose>
		</section>

		<%-- Paginação: 1 … 4 5 [6] 7 8 … 20 --%>
		<c:if test="${not empty pagina and pagina.totalPaginas gt 1}">
			<c:set var="n" value="${pagina.numero}" />
			<c:set var="ultima" value="${pagina.totalPaginas}" />
			<nav class="agnello-paginacao" aria-label="Páginas do catálogo">
				<c:choose>
					<c:when test="${pagina.temAnterior}"><a href="${urlBusca}${sep}pagina=${n - 1}" class="agnello-pag-seta" rel="prev">← Anterior</a></c:when>
					<c:otherwise><span class="agnello-pag-seta" aria-disabled="true">← Anterior</span></c:otherwise>
				</c:choose>
				<ol>
					<c:if test="${n gt 3}">
						<li><a href="${urlBusca}${sep}pagina=1">1</a></li>
						<c:if test="${n gt 4}"><li aria-hidden="true">…</li></c:if>
					</c:if>
					<c:forEach var="i" begin="${n gt 2 ? n - 2 : 1}" end="${n + 2 lt ultima ? n + 2 : ultima}">
						<li>
							<c:choose>
								<c:when test="${i eq n}"><span class="atual" aria-current="page">${i}</span></c:when>
								<c:otherwise><a href="${urlBusca}${sep}pagina=${i}">${i}</a></c:otherwise>
							</c:choose>
						</li>
					</c:forEach>
					<c:if test="${n lt ultima - 2}">
						<c:if test="${n lt ultima - 3}"><li aria-hidden="true">…</li></c:if>
						<li><a href="${urlBusca}${sep}pagina=${ultima}">${ultima}</a></li>
					</c:if>
				</ol>
				<c:choose>
					<c:when test="${pagina.temProxima}"><a href="${urlBusca}${sep}pagina=${n + 1}" class="agnello-pag-seta" rel="next">Próxima →</a></c:when>
					<c:otherwise><span class="agnello-pag-seta" aria-disabled="true">Próxima →</span></c:otherwise>
				</c:choose>
			</nav>
		</c:if>
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

	<script src="${ctx}/js/pages/catalogo.js?v=${initParam.versaoArquivos}"></script>
	<script src="${ctx}/js/components/favoritos.js?v=${initParam.versaoArquivos}"></script>
	<script src="${ctx}/js/components/carrinho.js?v=${initParam.versaoArquivos}"></script>
	<script src="${ctx}/js/components/chat.js?v=${initParam.versaoArquivos}"></script>
</body>

</html>
