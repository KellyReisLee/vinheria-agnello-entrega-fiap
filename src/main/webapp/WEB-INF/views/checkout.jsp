<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- "usuarioLogado" é posto no request pelo CheckoutController quando há sessão --%>
<c:set var="logado" value="${not empty usuarioLogado}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Finalizar compra | Vinheria Agnello</title>
  <meta name="robots" content="noindex">
  <meta name="theme-color" content="#4A1525">
  <link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/variables.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/global.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/checkout.css?v=${initParam.versaoArquivos}">
  <script>window.CONTEXT_PATH = '${ctx}';</script>
</head>

<body class="checkout-body">

  <%-- Estado da sessão para o checkout.js (sem scriptlets; c:out escapa o e-mail) --%>
  <div id="checkout-estado" hidden
       data-logado="${logado}"
       data-email="<c:out value='${usuarioLogado.email}' />"></div>

  <%-- Header reduzido: no checkout tiramos a navegação para a pessoa não se distrair --%>
  <header class="checkout-top-bar">
    <a href="${ctx}/home" class="checkout-logo" aria-label="Vinheria Agnello — voltar à loja">
      <jsp:include page="/WEB-INF/componentes/logo-taca.jsp"><jsp:param name="id" value="checkout" /><jsp:param name="classe" value="checkout-logo-img" /></jsp:include>
      <span>
        <span class="checkout-logo-title">VINHERIA AGNELLO</span>
        <span class="checkout-logo-sub">DESDE 1978</span>
      </span>
    </a>
    <span class="secure-badge">
      <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="4" y="11" width="16" height="10" rx="2"></rect><path d="M8 11V7a4 4 0 0 1 8 0v4"></path></svg>
      Compra segura
    </span>
  </header>

  <main class="checkout-main" id="conteudo">

    <%-- Carrinho vazio (mostrado pelo checkout.js) --%>
    <section class="checkout-vazio" id="checkout-vazio" hidden>
      <img src="${ctx}/assets/icons/wine.svg" alt="" width="56" height="56">
      <h1>O seu carrinho está vazio</h1>
      <p>Escolha os seus rótulos na nossa adega e volte aqui para finalizar.</p>
      <a href="${ctx}/catalogo" class="checkout-btn-primary">Ver os vinhos</a>
    </section>

    <%-- Confirmação do pedido (preenchida pelo checkout.js) --%>
    <section class="checkout-confirmacao" id="checkout-confirmacao" hidden tabindex="-1"
             aria-labelledby="confirmacao-titulo"></section>

    <div class="checkout-container" id="checkout-conteudo">
      <h1 class="checkout-title">Finalizar compra</h1>

      <div class="checkout-steps">

        <!-- ===================== 1. IDENTIFICAÇÃO ===================== -->
        <section class="checkout-card" id="identification-card" aria-labelledby="passo1">
          <div class="step-header">
            <span class="step-number" aria-hidden="true">1</span>
            <h2 id="passo1">Identificação</h2>
            <span class="step-ok" id="passo1-ok" ${logado ? '' : 'hidden'}>✓ Concluído</span>
          </div>

          <c:choose>
            <c:when test="${logado}">
              <div class="checkout-logado">
                <span class="checkout-logado-avatar" aria-hidden="true">✓</span>
                <div>
                  <strong>Você está conectado</strong>
                  <span><c:out value="${usuarioLogado.email}" /></span>
                </div>
                <a href="${ctx}/logout" class="checkout-link">Não é você? Sair</a>
              </div>
            </c:when>

            <c:otherwise>
              <%-- Os "name" destes campos são os que o CheckoutController lê --%>
              <form class="checkout-form" id="form-identification" novalidate>
                <input type="hidden" id="tipo_cliente" name="tipo_cliente" value="PF">

                <div class="checkout-alert checkout-alert--erro" id="email-error-box" role="alert" hidden></div>

                <div class="field">
                  <label for="checkout-email">E-mail para acompanhar o pedido</label>
                  <div class="input-with-action">
                    <input type="email" id="checkout-email" name="email" autocomplete="email"
                           inputmode="email" placeholder="seu@email.com" required>
                    <button type="button" id="btn-check-email" class="checkout-btn-secondary">Continuar</button>
                  </div>
                </div>

                <%-- Já tem conta: pede a senha --%>
                <div id="password-group" class="auth-box" hidden>
                  <p class="auth-msg">Encontramos uma conta com este e-mail. Digite a sua senha para continuar:</p>
                  <div class="checkout-alert checkout-alert--erro" id="senha-error-box" role="alert" hidden></div>
                  <div class="input-with-action">
                    <div class="field field--grow">
                      <label for="checkout-senha" class="sr-only">Senha</label>
                      <input type="password" id="checkout-senha" name="senha"
                             autocomplete="current-password" placeholder="Sua senha">
                    </div>
                    <button type="button" id="btn-entrar-senha" class="checkout-btn-secondary">Entrar</button>
                  </div>
                  <a href="${ctx}/esqueci-senha" class="checkout-link">Esqueceu a sua senha?</a>
                </div>

                <%-- Ainda não tem conta: cadastro rápido --%>
                <div id="register-group" class="auth-box" hidden>
                  <p class="auth-msg"><strong>Ainda não tem conta.</strong> Complete os seus dados para criar a conta e finalizar o pedido:</p>

                  <div class="checkout-alert checkout-alert--sucesso" id="register-success-box" role="status" hidden>
                    <strong>Conta criada!</strong> Você já está conectado. Atualizando a página…
                  </div>
                  <div class="checkout-alert checkout-alert--erro" id="register-error-box" role="alert" hidden>
                    <span id="register-error-msg"></span>
                  </div>

                  <div class="tipo-pessoa-tabs" role="tablist" aria-label="Tipo de conta">
                    <button type="button" class="tipo-tab" role="tab" data-tipo="PF" aria-selected="true" aria-controls="fields-pf">Pessoa física</button>
                    <button type="button" class="tipo-tab" role="tab" data-tipo="PJ" aria-selected="false" aria-controls="fields-pj" tabindex="-1">Pessoa jurídica</button>
                  </div>

                  <div id="fields-pf" class="pessoa-fields" role="tabpanel">
                    <div class="grid-2">
                      <div class="field">
                        <label for="novo-nome">Nome</label>
                        <input type="text" id="novo-nome" name="nome" autocomplete="given-name">
                      </div>
                      <div class="field">
                        <label for="novo-sobrenome">Sobrenome</label>
                        <input type="text" id="novo-sobrenome" name="sobrenome" autocomplete="family-name">
                      </div>
                    </div>
                    <div class="field">
                      <label for="novo-cpf">CPF</label>
                      <input type="text" id="novo-cpf" name="cpf" inputmode="numeric" data-mascara="cpf"
                             placeholder="000.000.000-00" maxlength="14">
                    </div>
                  </div>

                  <div id="fields-pj" class="pessoa-fields" role="tabpanel" hidden>
                    <div class="field">
                      <label for="novo-razao">Razão social</label>
                      <input type="text" id="novo-razao" name="razao_social" autocomplete="organization" disabled>
                    </div>
                    <div class="field">
                      <label for="novo-cnpj">CNPJ</label>
                      <input type="text" id="novo-cnpj" name="cnpj" inputmode="numeric" data-mascara="cnpj"
                             placeholder="00.000.000/0001-00" maxlength="18" disabled>
                    </div>
                  </div>

                  <div class="grid-2">
                    <div class="field">
                      <label for="novo-telefone">Telefone / WhatsApp</label>
                      <input type="tel" id="novo-telefone" name="telefone" autocomplete="tel-national"
                             data-mascara="telefone" placeholder="(11) 99999-9999" maxlength="15">
                    </div>
                    <div class="field">
                      <label for="novo-senha">Crie uma senha</label>
                      <input type="password" id="novo-senha" autocomplete="new-password" placeholder="Mínimo de 8 caracteres" minlength="8">
                    </div>
                  </div>

                  <button type="button" id="btn-finalizar-cadastro" class="checkout-btn-secondary checkout-btn-block">
                    Criar conta e continuar
                  </button>
                </div>
              </form>
            </c:otherwise>
          </c:choose>
        </section>

        <!-- ===================== 2. ENDEREÇO ===================== -->
        <section class="checkout-card" aria-labelledby="passo2">
          <div class="step-header">
            <span class="step-number" aria-hidden="true">2</span>
            <h2 id="passo2">Endereço de entrega</h2>
          </div>

          <%-- Endereços guardados na conta: um toque preenche o formulário (checkout.js) --%>
          <c:if test="${not empty enderecos}">
            <fieldset class="enderecos-salvos" id="enderecos-salvos">
              <legend>Os seus endereços</legend>
              <div class="enderecos-salvos-lista">
                <c:forEach var="e" items="${enderecos}">
                  <label class="endereco-salvo">
                    <input type="radio" name="endereco-salvo" value="${e.id}" ${e.principal ? 'checked' : ''}
                           data-cep="${fn:escapeXml(e.cepFormatado)}" data-logradouro="${fn:escapeXml(e.logradouro)}"
                           data-numero="${fn:escapeXml(e.numero)}" data-complemento="${fn:escapeXml(e.complemento)}"
                           data-bairro="${fn:escapeXml(e.bairro)}" data-cidade="${fn:escapeXml(e.cidade)}">
                    <span class="endereco-salvo-texto">
                      <strong><c:out value="${e.apelido}" /><c:if test="${e.principal}"> <span class="endereco-salvo-tag">Principal</span></c:if></strong>
                      <span><c:out value="${e.logradouro}" />, <c:out value="${e.numero}" /><c:if test="${not empty e.complemento}"> — <c:out value="${e.complemento}" /></c:if></span>
                      <span><c:out value="${e.bairro}" /> · <c:out value="${e.cidade}" /> · <c:out value="${e.cepFormatado}" /></span>
                    </span>
                  </label>
                </c:forEach>
                <label class="endereco-salvo endereco-salvo--novo">
                  <input type="radio" name="endereco-salvo" value="novo">
                  <span class="endereco-salvo-texto"><strong>+ Usar outro endereço</strong></span>
                </label>
              </div>
            </fieldset>
          </c:if>

          <form class="checkout-form" id="form-endereco" novalidate>
            <div class="grid-cep">
              <div class="field">
                <label for="cep">CEP</label>
                <input type="text" id="cep" inputmode="numeric" autocomplete="postal-code"
                       data-mascara="cep" placeholder="00000-000" maxlength="9" required>
                <span class="field-hint" id="cep-status" aria-live="polite"></span>
              </div>
              <div class="field">
                <label for="logradouro">Rua / avenida</label>
                <input type="text" id="logradouro" autocomplete="address-line1" maxlength="120" required>
              </div>
            </div>
            <div class="grid-numero">
              <div class="field">
                <label for="numero">Número</label>
                <input type="text" id="numero" inputmode="numeric" maxlength="20" required>
              </div>
              <div class="field">
                <label for="complemento">Complemento <span class="opcional">(opcional)</span></label>
                <input type="text" id="complemento" autocomplete="address-line2" placeholder="Apto, bloco…" maxlength="60">
              </div>
            </div>
            <div class="grid-2">
              <div class="field">
                <label for="bairro">Bairro</label>
                <input type="text" id="bairro" maxlength="80" required>
              </div>
              <div class="field">
                <label for="cidade">Cidade / UF</label>
                <input type="text" id="cidade" autocomplete="address-level2" maxlength="80" required>
              </div>
            </div>
            <%-- Só para clientes com conta e com espaço para mais um endereço --%>
            <c:if test="${logado and fn:length(enderecos) lt (empty maxEnderecos ? 5 : maxEnderecos)}">
              <div class="guardar-endereco" id="guardar-endereco">
                <label class="guardar-endereco-check">
                  <input type="checkbox" id="salvar-endereco" ${empty enderecos ? 'checked' : ''}>
                  Guardar este endereço na minha conta para as próximas compras
                </label>
                <div class="field guardar-endereco-apelido" id="campo-apelido">
                  <label for="apelido-endereco">Dê um nome <span class="opcional">(opcional)</span></label>
                  <input type="text" id="apelido-endereco" maxlength="40" placeholder="Casa, Trabalho…">
                </div>
              </div>
            </c:if>
          </form>
          <p class="checkout-retirada-aviso" id="aviso-retirada" hidden>
            Você escolheu <strong>retirar na adega</strong> (passo 3): não precisa informar endereço.
            Retire na Rua das Videiras, 1978 — Jardins, SP, levando um documento com foto.
          </p>
        </section>

        <!-- ===================== 3. ENVIO ===================== -->
        <section class="checkout-card" aria-labelledby="passo3">
          <div class="step-header">
            <span class="step-number" aria-hidden="true">3</span>
            <h2 id="passo3">Método de envio</h2>
          </div>
          <fieldset class="shipping-options">
            <legend class="sr-only">Escolha o envio</legend>
            <label class="shipping-option">
              <input type="radio" name="shipping" value="25.00" data-tipo="ENTREGA" data-nome="Frete Térmico Agnello" checked>
              <span class="shipping-info">
                <span class="ship-name">Frete Térmico Agnello</span>
                <span class="ship-time">Entrega em até 48h com controle de temperatura</span>
              </span>
              <span class="ship-price">R$ 25,00</span>
            </label>
            <label class="shipping-option">
              <input type="radio" name="shipping" value="0" data-tipo="RETIRADA" data-nome="Retirada na adega">
              <span class="shipping-info">
                <span class="ship-name">Retirada na adega</span>
                <span class="ship-time">Disponível em 3 horas (Jardins, SP)</span>
              </span>
              <span class="ship-price ship-price--gratis">Grátis</span>
            </label>
          </fieldset>
        </section>

        <!-- ===================== 4. PAGAMENTO ===================== -->
        <section class="checkout-card" aria-labelledby="passo4">
          <div class="step-header">
            <span class="step-number" aria-hidden="true">4</span>
            <h2 id="passo4">Forma de pagamento</h2>
          </div>

          <div class="payment-tabs" role="tablist" aria-label="Forma de pagamento">
            <button type="button" class="pay-tab" role="tab" id="tab-pix" data-target="pix" aria-controls="pix" aria-selected="true">Pix <small>−5%</small></button>
            <button type="button" class="pay-tab" role="tab" id="tab-credit" data-target="credit" aria-controls="credit" aria-selected="false" tabindex="-1">Cartão</button>
            <button type="button" class="pay-tab" role="tab" id="tab-boleto" data-target="boleto" aria-controls="boleto" aria-selected="false" tabindex="-1">Boleto</button>
          </div>

          <div id="pix" class="pay-content" role="tabpanel" aria-labelledby="tab-pix">
            <p class="pay-note pay-note--destaque"><strong>5% de desconto</strong> no pagamento via Pix à vista.
              O código Pix aparece logo após confirmar o pedido.</p>
          </div>

          <div id="credit" class="pay-content" role="tabpanel" aria-labelledby="tab-credit" hidden>
            <form class="checkout-form" id="form-cartao" novalidate>
              <div class="field">
                <label for="card-num">Número do cartão</label>
                <input type="text" id="card-num" inputmode="numeric" autocomplete="cc-number"
                       data-mascara="cartao" placeholder="0000 0000 0000 0000" maxlength="19" required>
              </div>
              <div class="grid-2">
                <div class="field">
                  <label for="card-exp">Validade</label>
                  <input type="text" id="card-exp" inputmode="numeric" autocomplete="cc-exp"
                         data-mascara="validade" placeholder="MM/AA" maxlength="5" required>
                </div>
                <div class="field">
                  <label for="card-cvv">CVV</label>
                  <input type="password" id="card-cvv" inputmode="numeric" autocomplete="cc-csc"
                         placeholder="123" maxlength="4" required>
                </div>
              </div>
              <div class="field">
                <label for="card-name">Nome impresso no cartão</label>
                <input type="text" id="card-name" autocomplete="cc-name" required>
              </div>
              <div class="field">
                <label for="card-parcelas">Parcelas</label>
                <select id="card-parcelas"></select>
              </div>
            </form>
          </div>

          <div id="boleto" class="pay-content" role="tabpanel" aria-labelledby="tab-boleto" hidden>
            <p class="pay-note">O boleto vence em <strong>2 dias úteis</strong>. O pedido é liberado após a compensação bancária.</p>
          </div>
        </section>
      </div>

      <!-- ===================== RESUMO ===================== -->
      <aside class="checkout-summary" aria-labelledby="resumo-titulo">
        <div class="summary-card">
          <h2 id="resumo-titulo">Resumo da compra</h2>
          <ul class="summary-items-list" id="summary-items"></ul>

          <dl class="summary-totals">
            <div class="calc-row"><dt>Subtotal</dt><dd id="total-subtotal">R$ 0,00</dd></div>
            <div class="calc-row"><dt>Envio</dt><dd id="total-frete">R$ 25,00</dd></div>
            <div class="calc-row calc-row--desconto" id="row-discount"><dt>Desconto Pix (5%)</dt><dd id="total-desconto">− R$ 0,00</dd></div>
            <div class="calc-row calc-row--total"><dt>Total</dt><dd id="total-final">R$ 0,00</dd></div>
          </dl>

          <div class="checkout-alert checkout-alert--erro" id="pedido-erro" role="alert" hidden></div>

          <button type="button" class="checkout-btn-primary checkout-btn-block" id="btn-concluir">
            <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="4" y="11" width="16" height="10" rx="2"></rect><path d="M8 11V7a4 4 0 0 1 8 0v4"></path></svg>
            <span>Concluir pedido</span>
          </button>
          <p class="terms-notice">Ao concluir, você concorda com os <a href="${ctx}/termos" target="_blank">Termos de Uso</a>
            e a <a href="${ctx}/privacidade" target="_blank">Política de Privacidade</a> da Vinheria Agnello
            e confirma ter 18 anos ou mais.</p>
        </div>

        <div class="checkout-links">
          <a href="${ctx}/catalogo" class="checkout-link">← Continuar comprando</a>
        </div>
      </aside>
    </div>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />

  <script src="${ctx}/js/components/chat.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/pages/checkout.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
