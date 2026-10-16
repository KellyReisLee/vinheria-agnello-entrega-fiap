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
<title>Vinheria Agnello | Vinhos selecionados pela família desde 1978</title>
<meta name="description"
	content="Rótulos selecionados à mão pela família Agnello desde 1978. Descubra o vinho ideal com o Sommelier Virtual e receba em casa com embalagem térmica.">
<meta name="theme-color" content="#4A1525">

<!-- Pré-visualização ao partilhar o link (WhatsApp, redes sociais) -->
<meta property="og:type" content="website">
<meta property="og:title" content="Vinheria Agnello — Da nossa família para a sua mesa">
<meta property="og:description" content="Rótulos selecionados à mão pela família Agnello desde 1978.">
<meta property="og:image" content="${ctx}/assets/images/hero-image.webp">

<link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">

<!-- Fontes (preconnect acelera o download) -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">

<!-- Estilos: home.css já importa header, secções, carrinho e chat -->
<link rel="stylesheet" href="${ctx}/css/variables.css">
<link rel="stylesheet" href="${ctx}/css/global.css">
<link rel="stylesheet" href="${ctx}/css/home.css">
<link rel="stylesheet" href="${ctx}/css/animations.css">

<!-- A imagem do topo começa a descarregar antes do CSS ser lido -->
<link rel="preload" href="${ctx}/assets/images/hero-image.webp" as="image" type="image/webp">

<script>
	window.CONTEXT_PATH = '${ctx}';
</script>
</head>

<body>
	<a class="skip-link" href="#conteudo">Pular para o conteúdo</a>

	<jsp:include page="/WEB-INF/componentes/header.jsp" />

	<main id="conteudo">
		<!-- ===================== HERO ===================== -->
		<section class="hero" aria-labelledby="hero-titulo">
			<div class="hero-content">
				<span class="hero-tag">
					<span aria-hidden="true">★</span> CURADORIA FAMILIAR
				</span>
				<h1 class="hero-title" id="hero-titulo">
					Da nossa família <span>para a sua mesa.</span>
				</h1>
				<p class="hero-subtitle">Descubra rótulos selecionados à mão
					pelo Sr. Giulio e sua filha Bianca, trazendo o calor e a expertise
					da nossa loja física direto para o seu lar.</p>
				<div class="hero-buttons">
					<a href="${ctx}/quiz" class="btn-gold" id="hero-quiz-btn">
						Iniciar Quiz do Sommelier <span class="btn-arrow" aria-hidden="true">→</span>
					</a>
					<a href="${ctx}/catalogo" class="btn-outline">Ver Catálogo</a>
				</div>
			</div>
		</section>

		<!-- ===================== FAMÍLIA ===================== -->
		<section class="family-section" aria-labelledby="familia-titulo">
			<div class="family-container">
				<div class="family-image-wrapper scroll-animate-left">
					<div class="image-frame-decoration" aria-hidden="true"></div>
					<div class="family-image-box">
						<img src="${ctx}/assets/images/home-atendimento-image.webp"
							alt="Taça de vinho branco servida no balcão da Vinheria Agnello"
							width="1000" height="1500" loading="lazy" decoding="async">
					</div>
					<div class="family-mini-badge">
						<img src="${ctx}/assets/icons/shield-check.svg" alt="" width="24" height="24">
						<div>
							<strong>ATENDIMENTO</strong>
							<p>Artesanal &amp; Pessoal</p>
						</div>
					</div>
				</div>

				<div class="family-text scroll-animate-right">
					<h2 id="familia-titulo">
						Aqui você não compra <span class="italic-text">de um algoritmo.</span>
					</h2>
					<p class="family-desc-lead">Desde 1978, a Vinheria Agnello é
						mais que uma loja; é a extensão da sala de estar do Sr. Giulio.
						Cada garrafa em nosso catálogo não foi escolhida por planilhas de
						vendas, mas sim degustada e aprovada rigorosamente por nossa
						família.</p>
					<p>Sabemos que um bom vinho tem o poder de transformar um
						jantar comum em uma memória inesquecível. Com a ajuda da Bianca,
						trouxemos a experiência do nosso tradicional balcão de madeira
						para o mundo digital.</p>

					<div class="founders-profile">
						<div class="founders-avatar" aria-hidden="true">AG</div>
						<div>
							<strong>Giulio &amp; Bianca Agnello</strong>
							<span class="founders-role">FUNDADORES</span>
						</div>
					</div>
				</div>
			</div>
		</section>

		<!-- ===================== CALCULADORA DE EVENTOS ===================== -->
		<section class="unique-events-calc-section" id="calculadora" aria-labelledby="calc-section-title">
			<div class="unique-calc-container">
				<div class="unique-calc-info scroll-animate-left">
					<header class="unique-calc-header">
						<span class="unique-calc-tag">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
								stroke-width="2" aria-hidden="true">
								<path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
								<polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
								<line x1="12" y1="22.08" x2="12" y2="12"></line>
							</svg>
							FERRAMENTA EXCLUSIVA
						</span>
						<h2 id="calc-section-title">Calculadora de Eventos</h2>
					</header>

					<p class="unique-cal-text">Vai receber amigos ou planejar uma
						festa? Não deixe faltar nem sobrar. Informe os detalhes do seu
						evento e o Sr. Giulio calculará a quantidade exata e a proporção
						ideal de rótulos.</p>

					<div class="unique-calc-notice" role="note">
						<img src="${ctx}/assets/icons/circle-alert.svg" alt="" width="20" height="20">
						<p>O cálculo baseia-se em métricas de sommelier profissional,
							ajustando a proporção de tintos, brancos e espumantes conforme o
							tipo de evento.</p>
					</div>
				</div>

				<div class="unique-calculator-box scroll-animate-right">
					<%-- Ecrã inicial. Os ecrãs seguintes (parecer, orçamento) são
					     criados pelo calculadora.js num painel ao lado deste. --%>
					<div class="calc-view">
					<div class="unique-inputs-row">
						<div class="unique-input-group">
							<span class="unique-input-label" id="label-convidados">CONVIDADOS</span>
							<div class="unique-input-field-control" role="group"
								aria-labelledby="label-convidados">
								<button type="button" class="unique-ctrl-btn"
									id="btn-menos-convidados" aria-label="Diminuir convidados">−</button>
								<span id="valor-convidados" aria-live="polite">12</span>
								<button type="button" class="unique-ctrl-btn"
									id="btn-mais-convidados" aria-label="Aumentar convidados">+</button>
							</div>
						</div>

						<div class="unique-input-group">
							<span class="unique-input-label" id="label-duracao">DURAÇÃO (HORAS)</span>
							<div class="unique-input-field-control" role="group"
								aria-labelledby="label-duracao">
								<button type="button" class="unique-ctrl-btn"
									id="btn-menos-duracao" aria-label="Diminuir duração">−</button>
								<span id="valor-duracao" aria-live="polite">4</span>
								<button type="button" class="unique-ctrl-btn"
									id="btn-mais-duracao" aria-label="Aumentar duração">+</button>
							</div>
						</div>
					</div>

					<div class="unique-calc-event-type">
						<span class="unique-event-label" id="estilo-evento-label">ESTILO DO EVENTO</span>
						<div class="unique-event-buttons-wrapper" role="group"
							aria-labelledby="estilo-evento-label">
							<button type="button" class="unique-type-btn active" data-estilo="jantar" aria-pressed="true">Jantar</button>
							<button type="button" class="unique-type-btn" data-estilo="festa" aria-pressed="false">Festa</button>
							<button type="button" class="unique-type-btn" data-estilo="coquetel" aria-pressed="false">Coquetel</button>
						</div>
					</div>

					<div class="unique-calc-result-box" aria-live="polite">
						<span class="unique-result-label">RECOMENDAÇÃO IDEAL</span>
						<div class="unique-result-total">
							10 <span>Garrafas</span>
						</div>

						<div class="unique-result-breakdown">
							<div class="unique-breakdown-item">
								<span>TINTO</span> <strong>6</strong>
							</div>
							<div class="unique-breakdown-item">
								<span>BRANCO</span> <strong>3</strong>
							</div>
							<div class="unique-breakdown-item">
								<span>ESPUMANTE</span> <strong>1</strong>
							</div>
						</div>

						<button type="button" class="unique-btn-calc-action">
							Montar Kit Personalizado <span class="btn-arrow" aria-hidden="true">→</span>
						</button>
					</div>
					</div>
				</div>
			</div>
		</section>

		<!-- ===================== VITRINE: ACHADOS DA SEMANA ===================== -->
		<section class="vitrine-section" aria-labelledby="vitrine-titulo">
			<div class="section-header">
				<div class="section-header-text">
					<span class="section-eyebrow">Novidades na adega</span>
					<h2 id="vitrine-titulo">Achados da Semana</h2>
					<p>Rótulos de importação própria recém-chegados à nossa adega.</p>
				</div>
				<a href="${ctx}/catalogo" class="section-header-link">
					Ver toda a adega <span class="btn-arrow" aria-hidden="true">→</span>
				</a>
			</div>

			<div class="product-grid" id="vitrine-achados-container">
				<c:choose>
					<c:when test="${not empty listaVinhos}">
						<c:forEach var="vinho" items="${listaVinhos}">
							<%-- Caminho da imagem sem o "./" inicial (vem assim da base de dados) --%>
							<c:set var="imgVinho"
								value="${ctx}${vinho.imagem.startsWith('.') ? vinho.imagem.substring(1) : vinho.imagem}" />
							<fmt:formatNumber var="precoFmt" value="${vinho.preco}"
								minFractionDigits="2" maxFractionDigits="2" />

							<article class="product-card">
								<c:if test="${not empty vinho.desconto}">
									<span class="agnello-badge-off">${vinho.desconto}</span>
								</c:if>

								<div class="product-img-placeholder">
									<img src="${imgVinho}" alt="Garrafa de ${vinho.nome}"
										width="160" height="190" loading="lazy" decoding="async"
										onerror="this.style.visibility='hidden'">
								</div>

								<div class="product-info">
									<span class="product-meta">${vinho.origem} • ${vinho.tipo}</span>
									<h3 class="product-name"><a class="product-link" href="${ctx}/produto?id=${vinho.id}"><c:out value="${vinho.nome}" /></a></h3>

									<c:if test="${not empty vinho.pontuacao}">
										<ul class="product-scores">
											<li><img class="score-icon" src="${ctx}/assets/icons/star-check.svg"
												alt="" width="13" height="13"> ${vinho.pontuacao}</li>
										</ul>
									</c:if>
								</div>

								<div class="product-footer">
									<div class="price-box">
										<c:if test="${vinho.precoAntigo > 0}">
											<span class="old-price">
												<span class="sr-only">De</span>
												R$ <fmt:formatNumber value="${vinho.precoAntigo}"
													minFractionDigits="2" maxFractionDigits="2" />
											</span>
										</c:if>
										<span class="product-price">
											<c:if test="${vinho.precoAntigo > 0}"><span class="sr-only">por</span></c:if>
											R$ ${precoFmt}
										</span>
									</div>

									<button type="button" class="agnello-btn-comprar"
										aria-label="Adicionar ${vinho.nome} ao carrinho"
										data-id="${vinho.id}" data-nome="${vinho.nome}"
										data-preco="R$ ${precoFmt}" data-origem="${vinho.origem}"
										data-imagem="${imgVinho}">Selecionar</button>
								</div>
							</article>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="grid-empty-state">
							<strong>Nenhum vinho encontrado no momento.</strong>
							<p>Estamos a atualizar a nossa seleção de rótulos exclusivos.
								Por favor, volte em breve!</p>
						</div>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="center-action">
				<a href="${ctx}/catalogo" class="btn-outline-dark">
					Ver Toda a Adega <span class="btn-arrow" aria-hidden="true">→</span>
				</a>
			</div>
		</section>
	</main>

	<jsp:include page="/WEB-INF/componentes/footer.jsp" />

	<!-- Gaveta do carrinho (preenchida por carrinho.js) -->
	<div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
	<div id="agnello-cart-drawer" class="agnello-cart-drawer">
		<div class="agnello-cart-header">
			<button type="button" id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
			<h2 id="agnello-cart-title">Carrinho (0)</h2>
		</div>
		<div id="agnello-cart-body" class="agnello-cart-body"></div>
	</div>

	<script src="${ctx}/js/components/calculadora.js"></script>
	<script src="${ctx}/js/pages/home.js"></script>
	<script src="${ctx}/js/components/carrinho.js"></script>
	<script src="${ctx}/js/components/chat.js"></script>
</body>

</html>
