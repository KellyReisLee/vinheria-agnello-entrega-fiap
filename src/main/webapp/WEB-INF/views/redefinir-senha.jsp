<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <title>Redefinir Senha — Vinheria Agnello</title>
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
      
      <!-- Coluna da Esquerda: Formulário de Nova Senha -->
      <div class="login-form-side">
        <div class="form-header-top">
          <span class="sub-tag">NOVA CREDENCIAL</span>
          <h2>Redefinir Senha</h2>
          <p>Escolha uma nova senha segura para proteger o acesso à sua adega particular.</p>
        </div>
        
        <!-- Mensagem de Erro -->
        <c:if test="${not empty mensagemErro}">
          <div class="alert alert-error" style="background-color: #fdf2f2; border: 1px solid #f5c6cb; color: #721c24; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 0.9rem;">
            ⚠️ ${mensagemErro}
          </div>
        </c:if>
        
        <!-- Mensagem de Sucesso com Estilo Inline Garantido -->
        <c:if test="${not empty mensagemSucesso}">
          <div style="display: flex; align-items: flex-start; gap: 16px; background: linear-gradient(135deg, #f4f9f4 0%, #e8f5e9 100%); border: 1px solid #c8e6c9; border-left: 5px solid #2e7d32; padding: 20px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 4px 12px rgba(46, 125, 50, 0.08);">
            <div style="background-color: #2e7d32; color: #ffffff; width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.1rem; font-weight: bold; flex-shrink: 0; box-shadow: 0 2px 6px rgba(46, 125, 50, 0.2);">✓</div>
            <div style="display: flex; flex-direction: column; gap: 4px;">
              <strong style="color: #1b5e20; font-size: 1.05rem; font-family: 'Playfair Display', serif;">Tudo pronto por aqui!</strong>
              <p style="color: #388e3c; font-size: 0.9rem; margin: 0; line-height: 1.4;">${mensagemSucesso} Você será redirecionado para a página de login em instantes...</p>
            </div>
          </div>
        </c:if>

        <!-- Formulário (Só exibe se não teve sucesso) -->
        <c:if test="${empty mensagemSucesso}">
          <form action="<c:url value='/redefinir-senha'/>" method="POST">
            <input type="hidden" name="token" value="${param.token}">

            <div class="input-group">
              <label for="novaSenha">Nova Senha</label>
              <div style="position: relative; width: 100%; display: block;">
                <input type="password" id="novaSenha" name="novaSenha" required placeholder="••••••••" style="width: 100%; padding-right: 45px; box-sizing: border-box;">
                <button type="button" onclick="togglePasswordVisibility('novaSenha', this, '<c:url value='/assets/icons/eye.svg'/>', '<c:url value='/assets/icons/eye-closed.svg'/>')" title="Mostrar/Ocultar Senha" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; padding: 0; display: flex; align-items: center; justify-content: center; z-index: 5; opacity: 0.7;">
                  <img src="<c:url value='/assets/icons/eye-closed.svg'/>" alt="Mostrar Senha" id="icon-novaSenha" style="width: 20px; height: 20px; object-fit: contain;">
                </button>
              </div>
            </div>

            <div class="input-group">
              <label for="confirmaSenha">Confirme a Nova Senha</label>
              <div style="position: relative; width: 100%; display: block;">
                <input type="password" id="confirmaSenha" name="confirmaSenha" required placeholder="••••••••" style="width: 100%; padding-right: 45px; box-sizing: border-box;">
                <button type="button" onclick="togglePasswordVisibility('confirmaSenha', this, '<c:url value='/assets/icons/eye.svg'/>', '<c:url value='/assets/icons/eye-closed.svg'/>')" title="Mostrar/Ocultar Senha" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; padding: 0; display: flex; align-items: center; justify-content: center; z-index: 5; opacity: 0.7;">
                  <img src="<c:url value='/assets/icons/eye-closed.svg'/>" alt="Mostrar Senha" id="icon-confirmaSenha" style="width: 20px; height: 20px; object-fit: contain;">
                </button>
              </div>
            </div>
            
            <button type="submit" class="btn-primary">Atualizar Senha</button>
          </form>
        </c:if>

        <div class="login-footer-back">
          <a href="<c:url value='/login'/>">← Voltar para o Login</a>
        </div>
      </div>

      <!-- Coluna da Direita: Painel Visual Agnello -->
      <div class="login-brand-side">
        <span class="brand-badge">CLUBE AGNELLO 1978</span>
        <h3>Segurança e exclusividade em cada detalhe.</h3>
        <p>Sua nova senha garantirá a privacidade e o controle total sobre suas escolhas e histórico de pedidos na nossa curadoria.</p>
        
        <ul class="brand-benefits-list">
          <li>✓ Criptografia de ponta a ponta</li>
          <li>✓ Acesso protegido ao seu perfil</li>
          <li>✓ Atendimento dedicado aos membros</li>
        </ul>
      </div>

    </div>
  </main>
  
  <!-- FOOTER MODULAR -->
  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

  <!-- Script Externo -->
  <script src="<c:url value='/js/pages/redefinir-senha.js'/>"></script>

  <!-- Script condicional de redirecionamento para o login após o sucesso -->
  <c:if test="${not empty mensagemSucesso}">
    <script>
      iniciarRedirecionamentoLogin("<c:url value='/login'/>", 4);
    </script>
  </c:if>

</body>
</html>