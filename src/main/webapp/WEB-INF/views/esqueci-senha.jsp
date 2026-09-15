<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <title>Recuperação de Senha — Vinheria Agnello</title>
  <link rel="stylesheet" href="<c:url value='/css/variables.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/global.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/esqueci-senha.css'/>">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body class="agnello-login-body">

  <!-- HEADER MODULAR -->
  <jsp:include page="/WEB-INF/componentes/header.jsp" />
  
  <main class="login-split-container">
    <div class="login-split-card">
      
      <!-- Coluna da Esquerda: Formulário de Recuperação -->
      <div class="login-form-side">
        <div class="form-header-top">
          <span class="sub-tag">SEGURANÇA DA CONTA</span>
          <h2>Recuperação de Senha</h2>
          <p>Informe seu e-mail cadastrado para receber as instruções de redefinição.</p>
        </div>
        
        <c:if test="${not empty mensagemErro}">
          <div class="alert alert-error">${mensagemErro}</div>
        </c:if>
        
        <c:if test="${not empty mensagemSucesso}">
          <div class="agnello-simulation-box">
            <div class="simulation-icon">✉</div>
            <div class="simulation-content">
              <strong>Instruções Geradas (Simulação)</strong>
              <p>${mensagemSucesso}</p>
            </div>
          </div>
        </c:if>

        <form action="<c:url value='/esqueci-senha'/>" method="POST">
          <div class="input-group">
            <label for="email">E-mail</label>
            <input type="email" id="email" name="email" required placeholder="seu@email.com">
          </div>
          
          <button type="submit" class="btn-primary">Enviar Instruções</button>
        </form>

        <div class="login-footer-back">
          <a href="<c:url value='/login'/>">← Voltar para o Login</a>
        </div>
      </div>

      <!-- Coluna da Direita: Painel Institucional/Visual Agnello -->
      <div class="login-brand-side">
        <span class="brand-badge">CLUBE AGNELLO 1978</span>
        <h3>A curadoria do seu acervo particular.</h3>
        <p>Não se preocupe, recuperar o acesso à sua adega é rápido e seguro. Continue desfrutando de rótulos exclusivos.</p>
        
        <ul class="brand-benefits-list">
          <li>✓ Acesso imediato ao histórico de pedidos</li>
          <li>✓ Segurança avançada de dados</li>
          <li>✓ Suporte especializado para apreciadores</li>
        </ul>
      </div>

    </div>
  </main>
  
  <!-- FOOTER MODULAR -->
  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

</body>
</html>