<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Entrar — Vinheria Agnello</title>

  <!-- Recursos Estáticos com Expression Language (EL) -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">

  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>

<body class="agnello-login-body">
  <!-- HEADER MODULAR (Protegido em WEB-INF) -->
  <jsp:include page="/WEB-INF/componentes/header.jsp" />
  
  <main class="agnello-login-main">
    <div class="agnello-login-container">

      <!-- Lado Esquerdo: Formulário de Acesso -->
      <div class="agnello-login-box">
        <div class="login-header-text">
          <span class="login-tag">ÁREA EXCLUSIVA</span>
          <h1>Boas-vindas à Adega</h1>
          <p>Entre com seus dados para acessar sua curadoria pessoal e histórico de pedidos.</p>
        </div>

        <!-- Exibição de Erro Dinâmica do Servlet -->
        <c:if test="${not empty erro}">
          <div style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; font-size: 14px;">
            ${erro}
          </div>
        </c:if>

        <form class="agnello-form-login" action="${pageContext.request.contextPath}/login" method="POST" novalidate>
          <div class="input-group-login">
            <label for="email">E-mail</label>
            <input type="email" id="email" name="email" placeholder="seu@email.com" autocomplete="email" required autofocus>
          </div>

          <div class="input-group-login">
            <div class="label-row">
              <label for="senha">Senha</label>
             
              <a href="<c:url value='/esqueci-senha'/>" class="forgot-link">Esqueceu sua senha?</a>
            </div>
            <input type="password" id="senha" name="senha" placeholder="••••••••" autocomplete="current-password" required>
          </div>

          <button type="submit" class="btn-agnello-primary">
            <span>Acessar Conta</span>
          </button>

          <div class="login-divider">
            <span>ou</span>
          </div>
        </form>
        
        <a href="${pageContext.request.contextPath}/cadastro" class="btn-agnello-outline">Criar nova conta na família</a>
      </div>

      <!-- Lado Direito: Banner de Destaque / Clube Familiar -->
      <div class="agnello-promo-box">
        <div class="promo-content">
          <span class="promo-badge">Clube Agnello 1978</span>
          <h2>O privilégio de ter uma adega particular em casa.</h2>
          <p>Faça parte do nosso círculo fechado de apreciadores e receba rótulos de importação própria selecionados pelo Sr. Giulio e pela Bianca.</p>

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

  <!-- Scripts com Caminho Dinâmico -->
  <script src="${pageContext.request.contextPath}/js/catalogo.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/componentes.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/carrinho.js"></script>
  <script src="${pageContext.request.contextPath}/js/components/chat.js"></script>

</body>

</html>