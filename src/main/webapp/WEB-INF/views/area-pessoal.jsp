<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Minha Adega Pessoal — Vinheria Agnello</title>
<link rel="stylesheet" href="<c:url value='/css/variables.css'/>">
<link rel="stylesheet" href="<c:url value='/css/global.css'/>">
<link rel="stylesheet" href="<c:url value='/css/home.css'/>">
<link rel="stylesheet" href="<c:url value='/css/animations.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sections/_chat.css'/>">
<link rel="stylesheet" href="<c:url value='/css/area-pessoal.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sections/_modal-deletar.css'/>">
<link rel="stylesheet" href="<c:url value='/css/sections/_modal-delecao-sucesso.css'/>">
<link
	href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">

<script>
	// Detecta se a página foi carregada do cache do navegador (botão voltar)
	window.addEventListener("pageshow", function(event) {
		if (event.persisted) {
			// Força a recarga da página a partir do servidor
			window.location.reload();
		}
	});
</script>


</head>

<body class="agnello-account-body">

	<!-- HEADER MODULAR (Protegido em WEB-INF) -->
	<jsp:include page="/WEB-INF/componentes/header.jsp" />

	<main class="agnello-account-main">
		<div class="account-container">

			<!-- Sidebar de Navegação Interna -->
			<aside class="account-sidebar">
				<div class="user-profile-card">
					<div class="avatar-wrapper">
						<span class="avatar-initials"> <c:choose>
								<%-- Se for instância de Pessoa Física --%>
								<c:when
									test="${clienteLogado['class'].name eq 'com.agnello.model.ClientePF'}">
									<c:set var="primeiro"
										value="${not empty clienteLogado.nome ? fn:substring(clienteLogado.nome, 0, 1) : 'A'}" />
									<c:set var="segundo"
										value="${not empty clienteLogado.sobrenome ? fn:substring(clienteLogado.sobrenome, 0, 1) : 'G'}" />
                  ${fn:toUpperCase(primeiro)}${fn:toUpperCase(segundo)}
                </c:when>
								<%-- Se for Pessoa Jurídica --%>
								<c:when
									test="${clienteLogado['class'].name eq 'com.agnello.model.ClientePJ'}">
									<c:set var="razao"
										value="${not empty clienteLogado.razaoSocial ? clienteLogado.razaoSocial : 'AG'}" />
                  ${fn:toUpperCase(fn:substring(razao, 0, 2))}
                </c:when>
								<c:otherwise>
                  AG
                </c:otherwise>
							</c:choose>
						</span> <span class="loyalty-badge">Elite</span>
					</div>
					<h3>
						<c:choose>
							<c:when
								test="${clienteLogado['class'].name eq 'com.agnello.model.ClientePF'}">
                ${clienteLogado.nome} ${clienteLogado.sobrenome}
              </c:when>
							<c:when
								test="${clienteLogado['class'].name eq 'com.agnello.model.ClientePJ'}">
                ${clienteLogado.razaoSocial}
              </c:when>
							<c:otherwise>
                Cliente Agnello
              </c:otherwise>
						</c:choose>
					</h3>
					<p>${clienteLogado.email}</p>
				</div>

				<nav class="account-nav-menu">
					<a href="#" class="nav-item active">Visão Geral</a> <a href="#"
						class="nav-item">Meus Pedidos</a> <a href="#" class="nav-item">Minha
						Adega (Desejos)</a> <a href="#" class="nav-item">Clube Agnello</a> <a
						href="#" class="nav-item">Endereços & Pagamento</a> <a href="#"
						id="linkDeletarConta" class="nav-item">Deletar Conta</a> <a
						href="<c:url value='/logout'/>" class="nav-item logout">Encerrar
						Sessão</a>
				</nav>
			</aside>

			<!-- Conteúdo Principal Dinâmico -->
			<section class="account-content">

				<!-- Boas-Vindas & Status de Consumo -->
				<div class="welcome-banner-card">
					<div class="welcome-text">
						<span class="sub-tag">ESPAÇO DO APRECIADOR</span>
						<h1>
							Olá,
							<c:choose>
								<c:when
									test="${clienteLogado['class'].name eq 'com.agnello.model.ClientePF'}">${clienteLogado.nome}</c:when>
								<c:when
									test="${clienteLogado['class'].name eq 'com.agnello.model.ClientePJ'}">${clienteLogado.razaoSocial}</c:when>
								<c:otherwise>Cliente</c:otherwise>
							</c:choose>
							! Sua adega particular está em ordem.
						</h1>
						<p>
							Você possui <strong>12 rótulos</strong> em seu histórico e <strong>R$
								150,00</strong> em créditos de cashback disponíveis.
						</p>
					</div>
					<div class="last-order-preview">
						<span class="preview-label">Último Pedido #7842</span>
						<p class="preview-status">A caminho de São Paulo — Previsão
							amanhã</p>
					</div>
				</div>

				<!-- Atalhos Rápidos (Grid de Ações) -->
				<div class="quick-actions-grid">
					<a href="#" class="action-card">
						<div class="icon-box">
							<img src="<c:url value='/assets/icons/truck.svg'/>" alt="">
						</div>
						<h4>Rastrear Pedidos</h4>
						<p>Acompanhe entregas e notas fiscais</p>
					</a> <a href="#" class="action-card">
						<div class="icon-box">
							<img src="<c:url value='/assets/icons/credit-card.svg'/>" alt="">
						</div>
						<h4>Cartões & Carteira</h4>
						<p>Gerencie formas de pagamento</p>
					</a> <a href="#" class="action-card">
						<div class="icon-box">
							<img src="<c:url value='/assets/icons/wine.svg'/>" alt="">
						</div>
						<h4>Clube de Assinatura</h4>
						<p>Modifique sua caixa mensal</p>
					</a> <a href="#" class="action-card">
						<div class="icon-box">
							<img src="<c:url value='/assets/icons/map-pin.svg'/>" alt="">
						</div>
						<h4>Meus Endereços</h4>
						<p>Locais de entrega salvos</p>
					</a>
				</div>

				<!-- Vitrine de Recomendações e Desejos -->
				<div class="recommendation-section">
					<div class="section-header-flex">
						<h3>Sugestões para o seu paladar</h3>
						<a href="<c:url value='/catalogo'/>" class="see-more-link">Ver
							catálogo completo →</a>
					</div>
					<div class="wine-suggestions-grid">
						<div class="wine-card-mini">
							<span class="tag-rare">Safra Rara</span>
							<h4>Barolo DOCG 2018</h4>
							<p>Piemonte, Itália</p>
							<span class="price">R$ 380,00</span>
						</div>
						<div class="wine-card-mini">
							<span class="tag-rare">Exclusivo</span>
							<h4>Brunello di Montalcino</h4>
							<p>Toscana, Itália</p>
							<span class="price">R$ 490,00</span>
						</div>
					</div>
				</div>

			</section>

		</div>
	</main>


	

	<!-- FOOTER MODULAR (Protegido em WEB-INF) -->
	<jsp:include page="/WEB-INF/componentes/footer.jsp" />
	
	<!-- IMPORTAÇÃO DOS MODAIS DE EXCLUSÃO DE CONTA -->
  <jsp:include page="/WEB-INF/componentes/modalDeletarConta.jsp" />
  <jsp:include page="/WEB-INF/componentes/modalSucessoDelecao.jsp" />

	<!-- CARRINHO DRAWER -->
	<div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
	<div id="agnello-cart-drawer" class="agnello-cart-drawer">
		<div class="agnello-cart-header">
			<button id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
			<h2 id="agnello-cart-title">Carrinho (0)</h2>
		</div>
		<div id="agnello-cart-body" class="agnello-cart-body"></div>
	</div>

	<script src="<c:url value='/js/catalogo.js'/>"></script>
	<script src="<c:url value='/js/components/componentes.js'/>"></script>
	<script src="<c:url value='/js/components/carrinho.js'/>"></script>
	<script src="<c:url value='/js/components/chat.js'/>"></script>
	<script src="<c:url value='/js/components/modal-deletar-conta.js'/>"></script>
</body>

</html>