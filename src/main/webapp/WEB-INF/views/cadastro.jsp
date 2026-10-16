<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Se o servidor devolver o formulário com erro, mantém o tipo escolhido (PF/PJ) --%>
<c:set var="tipo" value="${param.tipo_cliente eq 'PJ' ? 'PJ' : 'PF'}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Criar conta | Vinheria Agnello</title>
  <jsp:include page="/WEB-INF/componentes/auth-head.jsp" />
</head>

<body class="auth-body">
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="auth-main" id="conteudo">
    <div class="auth-card">

      <section class="auth-form-side" aria-labelledby="titulo-cadastro">
        <span class="auth-eyebrow">Nova conta</span>
        <h1 class="auth-title" id="titulo-cadastro">Comece agora a viver o melhor do mundo do vinho</h1>
        <p class="auth-lead">Só aqui você encontra os rótulos ideais para todos os seus momentos.</p>

        <c:if test="${not empty erro}">
          <div class="auth-alert auth-alert--erro" role="alert">
            <span class="auth-alert-icon" aria-hidden="true">!</span>
            <div><c:out value="${erro}" /></div>
          </div>
        </c:if>

        <c:choose>
          <%-- Sucesso: mostra só a confirmação (sem o formulário) e leva ao login --%>
          <c:when test="${not empty sucesso}">
            <div class="auth-alert auth-alert--sucesso" role="status"
                 data-redirecionar="${ctx}/login" data-segundos="8">
              <span class="auth-alert-icon" aria-hidden="true">✓</span>
              <div>
                <strong>Conta criada!</strong>
                <c:out value="${sucesso}" />
                <br>Abrindo o login em <span data-segundos-restantes>8</span> segundos…
              </div>
            </div>
            <a href="${ctx}/login" class="auth-btn-outline">Ir para o login agora</a>
          </c:when>

          <c:otherwise>
            <div class="auth-tabs" role="tablist" aria-label="Tipo de conta">
              <button type="button" class="auth-tab" role="tab" id="aba-pf" data-tipo="PF"
                      aria-controls="painel-pf" aria-selected="${tipo eq 'PF'}">Pessoa física</button>
              <button type="button" class="auth-tab" role="tab" id="aba-pj" data-tipo="PJ"
                      aria-controls="painel-pj" aria-selected="${tipo eq 'PJ'}">Pessoa jurídica</button>
            </div>

            <form id="cadastro-form" class="auth-form" action="${ctx}/cadastro" method="POST" novalidate>
              <input type="hidden" id="tipo_cliente" name="tipo_cliente" value="${tipo}">

              <%-- Campos de Pessoa Física --%>
              <div class="auth-panel" id="painel-pf" role="tabpanel" aria-labelledby="aba-pf" ${tipo eq 'PJ' ? 'hidden' : ''}>
                <div class="auth-grid-2">
                  <div class="auth-field">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" data-nome="o nome" autocomplete="given-name"
                           value="<c:out value='${param.nome}' />" required aria-describedby="nome-erro">
                    <span class="auth-field-erro" id="nome-erro" aria-live="polite"></span>
                  </div>
                  <div class="auth-field">
                    <label for="sobrenome">Sobrenome</label>
                    <input type="text" id="sobrenome" name="sobrenome" data-nome="o sobrenome" autocomplete="family-name"
                           value="<c:out value='${param.sobrenome}' />" required aria-describedby="sobrenome-erro">
                    <span class="auth-field-erro" id="sobrenome-erro" aria-live="polite"></span>
                  </div>
                </div>
                <div class="auth-field">
                  <label for="cpf">CPF</label>
                  <input type="text" id="cpf" name="cpf" data-nome="o CPF" data-mascara="cpf"
                         inputmode="numeric" placeholder="000.000.000-00" maxlength="14"
                         value="<c:out value='${param.cpf}' />" required aria-describedby="cpf-erro">
                  <span class="auth-field-erro" id="cpf-erro" aria-live="polite"></span>
                </div>
              </div>

              <%-- Campos de Pessoa Jurídica --%>
              <div class="auth-panel" id="painel-pj" role="tabpanel" aria-labelledby="aba-pj" ${tipo eq 'PF' ? 'hidden' : ''}>
                <div class="auth-field">
                  <label for="razao_social">Razão social</label>
                  <input type="text" id="razao_social" name="razao_social" data-nome="a razão social"
                         autocomplete="organization" placeholder="Nome da Empresa LTDA"
                         value="<c:out value='${param.razao_social}' />" required aria-describedby="razao_social-erro">
                  <span class="auth-field-erro" id="razao_social-erro" aria-live="polite"></span>
                </div>
                <div class="auth-field">
                  <label for="cnpj">CNPJ</label>
                  <input type="text" id="cnpj" name="cnpj" data-nome="o CNPJ" data-mascara="cnpj"
                         inputmode="numeric" placeholder="00.000.000/0001-00" maxlength="18"
                         value="<c:out value='${param.cnpj}' />" required aria-describedby="cnpj-erro">
                  <span class="auth-field-erro" id="cnpj-erro" aria-live="polite"></span>
                </div>
              </div>

              <%-- Campos comuns --%>
              <div class="auth-grid-2">
                <div class="auth-field">
                  <label for="email">E-mail</label>
                  <input type="email" id="email" name="email" data-nome="o e-mail" autocomplete="email"
                         inputmode="email" placeholder="seu@email.com"
                         value="<c:out value='${param.email}' />" required aria-describedby="email-erro">
                  <span class="auth-field-erro" id="email-erro" aria-live="polite"></span>
                </div>
                <div class="auth-field">
                  <label for="telefone">Telefone</label>
                  <input type="tel" id="telefone" name="telefone" data-nome="o telefone" data-mascara="telefone"
                         autocomplete="tel-national" placeholder="(11) 99999-9999" maxlength="15"
                         value="<c:out value='${param.telefone}' />" required aria-describedby="telefone-erro">
                  <span class="auth-field-erro" id="telefone-erro" aria-live="polite"></span>
                </div>
              </div>

              <div class="auth-field">
                <label for="senha">Senha</label>
                <div class="auth-input-wrap">
                  <input type="password" id="senha" name="senha" data-nome="a senha" minlength="8"
                         autocomplete="new-password" required data-forca="forca-senha"
                         aria-describedby="forca-senha-texto senha-erro">
                  <button type="button" class="auth-toggle-senha" aria-controls="senha"
                          aria-label="Mostrar senha" aria-pressed="false">
                    <img src="${ctx}/assets/icons/eye-closed.svg" alt="" width="20" height="20">
                  </button>
                </div>
                <div class="auth-forca" id="forca-senha" data-nivel="0" aria-hidden="true">
                  <span></span><span></span><span></span><span></span>
                </div>
                <span class="auth-hint" id="forca-senha-texto">Mínimo de 8 caracteres.</span>
                <span class="auth-field-erro" id="senha-erro" aria-live="polite"></span>
              </div>

              <button type="submit" class="auth-btn-primary" data-texto-enviando="Criando conta…">
                <span>Criar conta</span>
              </button>
              <p class="auth-termos">Ao criar a conta, você confirma que tem 18 anos ou mais.</p>
            </form>

            <div class="auth-divider">já possui conta?</div>
            <a href="${ctx}/login" class="auth-btn-outline">Fazer login</a>
          </c:otherwise>
        </c:choose>
      </section>

      <c:set var="brandBadge" scope="request" value="Agnello Box" />
      <c:set var="brandTitulo" scope="request" value="O privilégio de ter uma adega particular em casa." />
      <c:set var="brandTexto" scope="request" value="Faça parte do nosso círculo de apreciadores e receba rótulos selecionados pelo Sr. Giulio e pela Bianca." />
      <c:set var="brandB1" scope="request" value="Cashback e vantagens exclusivas em todas as compras" />
      <c:set var="brandB2" scope="request" value="Acesso antecipado a safras raras e limitadas" />
      <c:set var="brandB3" scope="request" value="Frete especial e embalagem térmica garantida" />
      <jsp:include page="/WEB-INF/componentes/auth-brand.jsp" />
    </div>
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

  <script src="${ctx}/js/pages/auth.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/carrinho.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/chat.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
