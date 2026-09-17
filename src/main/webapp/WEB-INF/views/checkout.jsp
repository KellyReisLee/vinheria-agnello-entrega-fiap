<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%
    // Recupera o usuário injetado pelo CheckoutController (se houver)
    com.agnello.model.Usuario userCheckout = (com.agnello.model.Usuario) request.getAttribute("usuarioLogado");
    boolean jaLogado = (userCheckout != null);
    String emailLogado = jaLogado ? userCheckout.getEmail() : "";
%>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Finalizar Compra — Vinheria Agnello</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/variables.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/global.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/checkout.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/footer.css">

<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">
</head>

<body class="agnello-checkout-body">

	<!-- INPUTS OCULTOS PARA O JAVASCRIPT LER O ESTADO DE LOGIN -->
	<input type="hidden" id="server-is-logged" value="<%= jaLogado %>">
	<input type="hidden" id="server-user-email" value="<%= emailLogado %>">

	<!-- Header Simplificado de Checkout -->
	<header class="checkout-top-bar">
		<div class="checkout-brand">
			<a href="${pageContext.request.contextPath}/catalogo">VINHERIA
				AGNELLO</a> <span class="secure-badge">🔒 Ambiente 100% Seguro</span>
		</div>
	</header>

	<main class="agnello-checkout-main">
		<div class="checkout-container">

			<!-- Coluna Esquerda: Formulários de Etapas -->
			<div class="checkout-steps-wrapper">

				<!-- ETAPA: Identificação / Acesso Rápido -->
				<section class="checkout-card" id="identification-card">
					<div class="step-header">
						<span class="step-number">1</span>
						<h2>Identificação</h2>
					</div>
					<form class="checkout-form" id="form-identification"
						onsubmit="event.preventDefault();">

						<!-- ADICIONADO: Input oculto para garantir que o Servlet receba o tipo_cliente -->
						<input type="hidden" id="tipo_cliente" name="tipo_cliente"
							value="PF">

						<div class="input-row-grid single-col">
							<!-- Box estilizado para erros de e-mail -->
							<div id="email-error-box"
								style="display: none; background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 0.75rem; border-radius: 6px; font-size: 0.85rem; margin-top: 10px;">
								Por favor, informe um e-mail válido.</div>
							<div class="input-group">
								<label for="checkout-email">E-mail para acompanhamento
									do pedido</label>
								<div class="input-with-action">
									<input type="email" id="checkout-email" name="email"
										placeholder="seu.email@exemplo.com" required>
									<button type="button" id="btn-check-email"
										class="btn-secondary-action">Continuar</button>
								</div>
							</div>
						</div>
						<!-- Bloco condicional para senha (exibido caso o e-mail já exista ou usuário esteja logado) -->
						<div id="password-group" class="conditional-auth-box"
							style="display: none;">
							<p class="auth-msg">Detectamos uma conta com este e-mail.
								Digite sua senha para recuperar seus dados salvos:</p>

							<!-- Caixa de Erro para Senha Incorreta -->
							<div id="senha-error-box"
								style="display: none; background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 0.75rem; border-radius: 6px; font-size: 0.85rem; margin-bottom: 1rem;">
								<strong>Senha incorreta!</strong> Por favor, verifique sua senha
								e tente novamente.
							</div>

							<div class="input-row-grid">
								<!-- Coluna 1: Label, Input e o Link -->
								<div class="input-group">
									<label for="checkout-senha">Senha</label> <input
										type="password" id="checkout-senha" name="senha"
										placeholder="Sua senha de acesso">
									<div style="text-align: right;">
										<a href="${pageContext.request.contextPath}/esqueci-senha"
											style="font-size: 0.8rem; color: #4a1525; text-decoration: none; font-weight: 500;">Esqueceu
											sua senha?</a>
									</div>
								</div>

								<!-- Coluna 2: Botão alinhado perfeitamente com o input -->
								<div class="input-group"
									style="display: flex; flex-direction: column; justify-content: flex-end;">
									<button type="button" id="btn-entrar-senha"
										class="btn-secondary-action" style="margin-top: 0 !important;">Entrar</button>
								</div>
							</div>

						</div>
						<!-- Bloco condicional para novo cadastro (exibido caso o e-mail NÃO exista) -->
						<div id="register-group" class="conditional-auth-box"
							style="display: none;">
							<div class="alert-box-info"
								style="background: rgba(74, 21, 37, 0.05); border: 1px dashed #4a1525; padding: 1rem; border-radius: 8px; margin-bottom: 1.2rem;">
								<p class="auth-msg"
									style="margin: 0; color: #4a1525; font-weight: 600;">Não
									encontramos cadastro com este e-mail.</p>
								<span style="font-size: 0.85rem; color: #7a6e70;">Complete
									seus dados abaixo para finalizar o pedido e criar sua conta:</span>
							</div>

							<!-- CAIXA DE SUCESSO ESTILIZADA -->
							<div id="register-success-box"
								style="display: none; background: #d4edda; color: #155724; border: 1px solid #c3e6cb; padding: 1rem; border-radius: 8px; margin-bottom: 1.2rem; text-align: center;">
								<h4 style="margin: 0 0 5px 0; font-size: 1.1rem;">🎉
									Cadastro realizado com sucesso!</h4>
								<p style="margin: 0; font-size: 0.9rem;">Sua conta foi
									criada e você já está logado. Redirecionando...</p>
							</div>

							<!-- CAIXA DE ERRO ESTILIZADA -->
							<div id="register-error-box"
								style="display: none; background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 1rem; border-radius: 8px; margin-bottom: 1.2rem;">
								<h4 style="margin: 0 0 5px 0; font-size: 1.05rem;">⚠️
									Atenção ao cadastro</h4>
								<p id="register-error-msg" style="margin: 0; font-size: 0.9rem;">Ocorreu
									um erro. Verifique os dados e tente novamente.</p>
							</div>

							<!-- Abas de Tipo de Pessoa (Física / Jurídica) -->
							<div class="tipo-pessoa-tabs"
								style="display: flex; gap: 2rem; margin-bottom: 1.2rem; border-bottom: 1px solid #e0e0e0; padding-bottom: 0.5rem;">
								<button type="button" class="tab-pf active" data-tipo="pf"
									style="background: none; border: none; font-weight: 600; color: #4a1525; cursor: pointer; font-size: 0.95rem; border-bottom: 2px solid #4a1525; padding-bottom: 0.3rem;">PESSOA
									FÍSICA</button>
								<button type="button" class="tab-pj" data-tipo="pj"
									style="background: none; border: none; font-weight: 600; color: #999; cursor: pointer; font-size: 0.95rem;">PESSOA
									JURÍDICA</button>
							</div>

							<!-- Campos comuns: Telefone -->
							<div class="input-row-grid" style="margin-bottom: 1rem;">
								<div class="input-group">
									<label for="novo-telefone">Telefone / WhatsApp</label> <input
										type="text" id="novo-telefone" name="telefone"
										placeholder="(11) 99999-9999">
								</div>
							</div>

							<!-- Campos de PESSOA FÍSICA -->
							<div id="fields-pf" class="pessoa-fields">
								<div class="input-row-grid"
									style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
									<div class="input-group">
										<label for="novo-nome">Nome</label> <input type="text"
											id="novo-nome" name="nome" placeholder="Seu nome">
									</div>
									<div class="input-group">
										<label for="novo-sobrenome">Sobrenome</label> <input
											type="text" id="novo-sobrenome" name="sobrenome"
											placeholder="Seu sobrenome">
									</div>
								</div>
								<div class="input-row-grid" style="margin-bottom: 1rem;">
									<div class="input-group">
										<label for="novo-cpf">CPF</label> <input type="text"
											id="novo-cpf" name="cpf" placeholder="000.000.000-00">
									</div>
								</div>
							</div>

							<!-- Campos de PESSOA JURÍDICA (Oculto por padrão) -->
							<div id="fields-pj" class="pessoa-fields" style="display: none;">
								<div class="input-row-grid" style="margin-bottom: 1rem;">
									<div class="input-group">
										<label for="novo-razao">Razão Social</label> <input
											type="text" id="novo-razao" name="razao_social"
											placeholder="Nome da Empresa LTDA">
									</div>
								</div>
								<div class="input-row-grid" style="margin-bottom: 1rem;">
									<div class="input-group">
										<label for="novo-cnpj">CNPJ</label> <input type="text"
											id="novo-cnpj" name="cnpj" placeholder="00.000.000/0001-00">
									</div>
								</div>
							</div>

							<!-- Senha e Botão Final de Cadastro -->
							<div class="input-row-grid" style="margin-bottom: 1rem;">
								<div class="input-group">
									<label for="novo-senha">Crie uma Senha</label> <input
										type="password" id="novo-senha" name="senha"
										placeholder="Mínimo de 6 caracteres">
								</div>
							</div>

							<button type="button" id="btn-finalizar-cadastro"
								class="btn-secondary-action mt-2"
								style="width: 100%; background: #4a1525; color: #fff; padding: 0.8rem; border-radius: 6px; border: none; font-weight: 600; cursor: pointer;">Cadastrar
								e Continuar</button>
						</div>
					</form>
				</section>

				<!-- Etapa 2: Endereço de Entrega -->
				<section class="checkout-card">
					<div class="step-header">
						<span class="step-number">2</span>
						<h2>Endereço de Entrega</h2>
					</div>
					<form class="checkout-form" onsubmit="event.preventDefault();">
						<div class="input-row-grid">
							<div class="input-group">
								<label for="cep">CEP</label> <input type="text" id="cep"
									placeholder="00000-000" required>
							</div>
							<div class="input-group span-2">
								<label for="logradouro">Endereço / Rua</label> <input
									type="text" id="logradouro"
									placeholder="Ex: Av. Brigadeiro Luís Antônio" required>
							</div>
						</div>
						<div class="input-row-grid three-cols">
							<div class="input-group">
								<label for="numero">Número</label> <input type="text"
									id="numero" placeholder="1978" required>
							</div>
							<div class="input-group span-2">
								<label for="complemento">Complemento</label> <input type="text"
									id="complemento" placeholder="Apto 42, Bloco B">
							</div>
						</div>
						<div class="input-row-grid">
							<div class="input-group">
								<label for="bairro">Bairro</label> <input type="text"
									id="bairro" placeholder="Jardim Paulista" required>
							</div>
							<div class="input-group">
								<label for="cidade">Cidade / UF</label> <input type="text"
									id="cidade" value="São Paulo - SP" required>
							</div>
						</div>
					</form>
				</section>

				<!-- Etapa 3: Método de Frete -->
				<section class="checkout-card">
					<div class="step-header">
						<span class="step-number">3</span>
						<h2>Método de Envio</h2>
					</div>
					<div class="shipping-options-grid">
						<label class="shipping-option selected"> <input
							type="radio" name="shipping" value="25.00" checked>
							<div class="shipping-info">
								<span class="ship-name">Frete Térmico Agnello (Especial)</span>
								<span class="ship-time">Entrega em até 48h com controle
									de temperatura</span>
							</div> <span class="ship-price">R$ 25,00</span>
						</label> <label class="shipping-option"> <input type="radio"
							name="shipping" value="0.00">
							<div class="shipping-info">
								<span class="ship-name">Retirada na Adega Física</span> <span
									class="ship-time">Disponível em 3 horas (Jardins, SP)</span>
							</div> <span class="ship-price free">Grátis</span>
						</label>
					</div>
				</section>

				<!-- Etapa 4: Forma de Pagamento -->
				<section class="checkout-card">
					<div class="step-header">
						<span class="step-number">4</span>
						<h2>Forma de Pagamento</h2>
					</div>

					<div class="payment-tabs">
						<button type="button" class="pay-tab active" data-target="pix">Pix</button>
						<button type="button" class="pay-tab" data-target="credit">Cartão
							de Crédito</button>
						<button type="button" class="pay-tab" data-target="boleto">Boleto
							Bancário</button>
					</div>

					<div id="pix" class="pay-content active">
						<div class="pix-box-msg">
							<p>
								✨ <strong>5% de desconto extra</strong> aplicado para pagamentos
								via Pix à vista.
							</p>
							<span class="pix-code-hint">O QR Code será gerado
								imediatamente após a confirmação do pedido na próxima tela.</span>
						</div>
					</div>

					<div id="credit" class="pay-content">
						<form class="checkout-form" onsubmit="event.preventDefault();">
							<div class="input-group">
								<label for="card-num">Número do Cartão</label> <input
									type="text" id="card-num" placeholder="4532 •••• •••• 1978">
							</div>
							<div class="input-row-grid">
								<div class="input-group">
									<label for="card-exp">Validade</label> <input type="text"
										id="card-exp" placeholder="MM/AA">
								</div>
								<div class="input-group">
									<label for="card-cvv">CVV</label> <input type="password"
										id="card-cvv" placeholder="123">
								</div>
							</div>
							<div class="input-group">
								<label for="card-name">Nome Impresso no Cartão</label> <input
									type="text" id="card-name" placeholder="PAULO OLIVEIRA">
							</div>
						</form>
					</div>

					<div id="boleto" class="pay-content">
						<div class="boleto-msg">
							<p>
								📄 O boleto bancário terá vencimento em <strong>2 dias
									úteis</strong>. A liberação do pedido ocorre após a compensação
								bancária.
							</p>
						</div>
					</div>
				</section>

			</div>

			<!-- Coluna Direita: Resumo do Pedido -->
			<aside class="checkout-summary-sidebar">
				<div class="summary-card">
					<h3>Resumo da Compra</h3>

					<div class="summary-items-list"></div>

					<div class="summary-totals">
						<div class="calc-row">
							<span>Subtotal</span> <span>R$ 0,00</span>
						</div>
						<div class="calc-row">
							<span>Frete Térmico</span> <span>R$ 25,00</span>
						</div>
						<div class="calc-row discount" id="row-discount">
							<span>Desconto Pix (5%)</span> <span>- R$ 0,00</span>
						</div>
						<div class="calc-row total">
							<span>Total a Pagar</span> <span class="final-price">R$
								0,00</span>
						</div>
					</div>

					<button type="button"
						class="btn-agnello-primary checkout-action-btn"
						onclick="alert('Pedido realizado com sucesso! Bem-vindo à família Agnello.'); window.location.href='${pageContext.request.contextPath}/catalogo';">
						Concluir Pedido com Segurança</button>

					<p class="terms-notice">Ao finalizar, você concorda com os
						termos do Clube de Assinatura e Política de Privacidade da
						Vinheria Agnello.</p>
				</div>
			</aside>

		</div>
	</main>

	<div class="checkout-navigation-footer">
		<div class="nav-footer-inner">
			<%
			com.agnello.model.Usuario userSessao = (com.agnello.model.Usuario) session.getAttribute("clienteLogado");
			String urlAdega = request.getContextPath() + "/login";
			if (userSessao != null) {
				urlAdega = request.getContextPath() + "/area-pessoal?id=" + userSessao.getId();
			}
			%>
			<a href="<%=urlAdega%>" class="return-link">← Voltar para Minha
				Adega</a> <a href="${pageContext.request.contextPath}/catalogo"
				class="catalog-link">Continuar comprando no catálogo</a>
		</div>
	</div>

	<jsp:include page="/WEB-INF/componentes/footer.jsp" />

	<script
		src="${pageContext.request.contextPath}/js/components/componentes.js"></script>
	<script src="${pageContext.request.contextPath}/js/components/chat.js"></script>
	<script src="${pageContext.request.contextPath}/js/pages/checkout.js"></script>
</body>

</html>