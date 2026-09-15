<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Criar Conta — Vinheria Agnello</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cadastro.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>

<body class="agnello-login-body">

  <!-- HEADER MODULAR (Protegido em WEB-INF) -->
  <jsp:include page="/WEB-INF/componentes/header.jsp" />
  
    <main class="agnello-cadastro-main">
        <div class="agnello-cadastro-container">

            <!-- Lado Esquerdo: Formulário de Cadastro -->
            <div class="agnello-cadastro-box">
                <div class="cadastro-header-text">
                    <h1>Comece agora a viver o melhor do mundo do vinho!</h1>
                    <p>Só aqui você encontra os rótulos ideais para todos os seus momentos.</p>
                </div>

                <!-- Exibição de Erro caso ocorra -->
                <c:if test="${not empty erro}">
                    <div class="alert-error" style="background: #f8d7da; color: #721c24; padding: 10px; margin-bottom: 15px; border-radius: 4px;">
                        ${erro}
                    </div>
                </c:if>

                <!-- Exibição de Sucesso ao Cadastrar + Timer de Redirecionamento -->
                <c:if test="${not empty sucesso}">
                    <div class="alert-success" style="background: #d4edda; color: #155724; padding: 10px; margin-bottom: 15px; border-radius: 4px;">
                        ${sucesso}
                    </div>
                    
                    <script>
                        // Redireciona para o login após 4 segundos
                        setTimeout(function() {
                            window.location.href = "${pageContext.request.contextPath}/login";
                        }, 4000);
                    </script>
                </c:if>

                <!-- Abas Pessoa Física / Jurídica -->
                <div class="signup-tabs">
                    <button type="button" class="tab-btn active" data-target="pf-form">PESSOA FÍSICA</button>
                    <button type="button" class="tab-btn" data-target="pj-form">PESSOA JURÍDICA</button>
                </div>

                <!-- Formulário Único Unificado com Senha no Final -->
                <form id="cadastro-form" class="agnello-form" action="${pageContext.request.contextPath}/cadastro" method="POST">
                    <input type="hidden" id="tipo_cliente" name="tipo_cliente" value="PF">

                    <div class="input-group-login">
                        <label for="email">E-mail</label>
                        <input type="email" id="email" name="email" placeholder="seu@email.com" required>
                    </div>

                    <div class="input-group-login">
                        <label for="telefone">Telefone</label>
                        <input type="tel" id="telefone" name="telefone" placeholder="(11) 99999-9999" required>
                    </div>

                    <!-- Campos específicos de Pessoa Física -->
                    <div id="campos-pf" class="tipo-cliente-campos">
                        <div class="input-group-login">
                            <label for="nome">Nome</label>
                            <input type="text" id="nome" name="nome" placeholder="Seu nome" required>
                        </div>

                        <div class="input-group-login">
                            <label for="sobrenome">Sobrenome</label>
                            <input type="text" id="sobrenome" name="sobrenome" placeholder="Seu sobrenome" required>
                        </div>

                        <div class="input-group-login">
                            <label for="cpf">CPF</label>
                            <input type="text" id="cpf" name="cpf" placeholder="000.000.000-00" required>
                        </div>
                    </div>

                    <!-- Campos específicos de Pessoa Jurídica (inicialmente ocultos) -->
                    <div id="campos-pj" class="tipo-cliente-campos" style="display: none;">
                        <div class="input-group-login">
                            <label for="razao_social">Razão Social</label>
                            <input type="text" id="razao_social" name="razao_social" placeholder="Nome da Empresa LTDA">
                        </div>

                        <div class="input-group-login">
                            <label for="cnpj">CNPJ</label>
                            <input type="text" id="cnpj" name="cnpj" placeholder="00.000.000/0001-00">
                        </div>
                    </div>

                    <!-- Senha posicionada por último, antes do botão de envio -->
                    <div class="input-group-login">
                        <label for="senha">Senha</label>
                        <input type="password" id="senha" name="senha" placeholder="Mínimo 6 caracteres" minlength="6" required>
                    </div>

                    <button type="submit" class="btn-agnello-primary">Cadastrar</button>

                    <div class="login-divider">
                        <span>já possui conta?</span>
                    </div>

                    <a href="${pageContext.request.contextPath}/login" class="btn-agnello-outline">Fazer Login</a>
                </form>
            </div>

            <!-- Lado Direito: Banner de Destaque -->
            <div class="agnello-promo-box">
                <div class="promo-content">
                    <span class="promo-badge">AGNELLO BOX</span>
                    <h2>O privilégio de ter uma adega particular em casa.</h2>
                    <p>Faça parte do nosso círculo fechado de apreciadores e receba rótulos selecionados pelo Sr. Giulio e pela Bianca.</p>

                    <ul class="promo-benefits">
                        <li>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
                            Cashback e vantagens exclusivas em todas as compras
                        </li>
                        <li>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
                            Acesso antecipado a safras raras e limitadas
                        </li>
                        <li>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
                            Frete especial e embalagem térmica garantida
                        </li>
                    </ul>
                </div>
            </div>

        </div>
    </main>
    
  <!-- CARRINHO DRAWER -->
  <div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
  <div id="agnello-cart-drawer" class="agnello-cart-drawer">
    <div class="agnello-cart-header">
      <button id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
      <h2 id="agnello-cart-title">Carrinho (0)</h2>
    </div>
    <div id="agnello-cart-body" class="agnello-cart-body"></div>
  </div>

  <!-- FOOTER MODULAR (Protegido em WEB-INF) -->
  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

    <!-- Scripts com Caminhos Dinâmicos -->
    <script src="${pageContext.request.contextPath}/js/catalogo.js"></script>
    <script src="${pageContext.request.contextPath}/js/components/componentes.js"></script>
    <script src="${pageContext.request.contextPath}/js/components/carrinho.js"></script>
    <script src="${pageContext.request.contextPath}/js/components/chat.js"></script>
    <script src="${pageContext.request.contextPath}/js/pages/cadastro.js"></script>

</body>

</html>