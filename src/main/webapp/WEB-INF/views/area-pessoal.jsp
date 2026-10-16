<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<fmt:setTimeZone value="America/Sao_Paulo" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<%-- Dados do cliente (ClientePF ou ClientePJ) preparados uma só vez --%>
<c:set var="ehPF" value="${clienteLogado['class'].simpleName eq 'ClientePF'}" />
<c:set var="ehPJ" value="${clienteLogado['class'].simpleName eq 'ClientePJ'}" />
<c:choose>
  <c:when test="${ehPF}">
    <c:set var="nomeExibicao" value="${clienteLogado.nome} ${clienteLogado.sobrenome}" />
    <c:set var="primeiroNome" value="${clienteLogado.nome}" />
    <c:set var="iniciais" value="${fn:toUpperCase(fn:substring(clienteLogado.nome, 0, 1))}${fn:toUpperCase(fn:substring(clienteLogado.sobrenome, 0, 1))}" />
    <c:set var="documento" value="${clienteLogado.cpf}" />
  </c:when>
  <c:when test="${ehPJ}">
    <c:set var="nomeExibicao" value="${clienteLogado.razaoSocial}" />
    <c:set var="primeiroNome" value="${clienteLogado.razaoSocial}" />
    <c:set var="iniciais" value="${fn:toUpperCase(fn:substring(clienteLogado.razaoSocial, 0, 2))}" />
    <c:set var="documento" value="${clienteLogado.cnpj}" />
  </c:when>
  <c:otherwise>
    <c:set var="nomeExibicao" value="Cliente Agnello" />
    <c:set var="primeiroNome" value="Cliente" />
    <c:set var="iniciais" value="AG" />
  </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Minha conta | Vinheria Agnello</title>
  <meta name="robots" content="noindex">
  <meta name="theme-color" content="#4A1525">
  <link rel="icon" type="image/png" href="${ctx}/assets/images/logo-bg-white.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/variables.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/global.css?v=${initParam.versaoArquivos}">
  <link rel="stylesheet" href="${ctx}/css/area-pessoal.css?v=${initParam.versaoArquivos}">
  <script>
    window.CONTEXT_PATH = '${ctx}';
    // Ao voltar com o botão "Voltar" depois de sair, não mostra a página em cache
    window.addEventListener('pageshow', (e) => { if (e.persisted) location.reload(); });
  </script>
</head>

<body class="conta-body">
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="conta-main" id="conteudo" data-email="<c:out value='${clienteLogado.email}' />">
    <div class="conta-container">

      <!-- ===================== MENU LATERAL ===================== -->
      <aside class="conta-sidebar" aria-label="Menu da conta">
        <div class="conta-perfil">
          <span class="conta-avatar" aria-hidden="true"><c:out value="${iniciais}" /></span>
          <div>
            <strong class="conta-nome"><c:out value="${nomeExibicao}" /></strong>
            <span class="conta-email"><c:out value="${clienteLogado.email}" /></span>
          </div>
        </div>

        <nav class="conta-nav" aria-label="Seções da conta">
          <a href="#visao-geral" class="conta-nav-item" data-secao="visao-geral">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 10.5 12 3l9 7.5V21H3z"></path></svg>Visão geral</a>
          <a href="#pedidos" class="conta-nav-item" data-secao="pedidos">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M21 8 12 3 3 8v8l9 5 9-5z"></path><path d="M3 8l9 5 9-5M12 13v8"></path></svg>Meus pedidos</a>
          <a href="#dados" class="conta-nav-item" data-secao="dados">
            <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8" r="4"></circle><path d="M4 21a8 8 0 0 1 16 0"></path></svg>Meus dados</a>
          <a href="${ctx}/logout" class="conta-nav-item conta-nav-item--sair">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"></path></svg>Encerrar sessão</a>
        </nav>
      </aside>

      <div class="conta-conteudo">

        <!-- ===================== VISÃO GERAL ===================== -->
        <section class="conta-secao" id="visao-geral" aria-labelledby="titulo-visao" tabindex="-1">
          <div class="conta-boas-vindas">
            <span class="conta-eyebrow">Espaço do apreciador</span>
            <h1 id="titulo-visao">Olá, <c:out value="${primeiroNome}" />!</h1>
            <%-- "pedidos" vem do AreaPessoalController (já ordenados do mais recente) --%>
            <c:set var="qtdPedidos" value="${fn:length(pedidos)}" />
            <p id="resumo-conta">
              <c:choose>
                <c:when test="${qtdPedidos eq 1}">Você tem 1 pedido na sua conta.</c:when>
                <c:when test="${qtdPedidos gt 1}">Você tem ${qtdPedidos} pedidos na sua conta.</c:when>
                <c:otherwise>Bem-vindo à sua adega particular.</c:otherwise>
              </c:choose>
            </p>
            <c:if test="${qtdPedidos gt 0}">
              <c:set var="ultimo" value="${pedidos[0]}" />
              <div class="conta-ultimo-pedido" id="ultimo-pedido">
                <span class="conta-ultimo-label">Último pedido · <c:out value="${ultimo.numero}" /></span>
                <strong><c:out value="${ultimo.status.descricao}" /></strong>
                <a href="#pedidos" data-secao="pedidos">Ver detalhes →</a>
              </div>
            </c:if>
          </div>

          <div class="conta-atalhos">
            <a href="#pedidos" class="conta-atalho" data-secao="pedidos">
              <img src="${ctx}/assets/icons/truck.svg" alt="" width="24" height="24">
              <h2>Meus pedidos</h2>
              <p>Acompanhe o estado das suas compras</p>
            </a>
            <a href="${ctx}/catalogo" class="conta-atalho">
              <img src="${ctx}/assets/icons/wine.svg" alt="" width="24" height="24">
              <h2>Nossa adega</h2>
              <p>Explore todos os rótulos</p>
            </a>
            <a href="${ctx}/quiz" class="conta-atalho">
              <img src="${ctx}/assets/icons/star-check.svg" alt="" width="24" height="24">
              <h2>Sommelier Virtual</h2>
              <p>Receba sugestões para o seu paladar</p>
            </a>
            <a href="#dados" class="conta-atalho" data-secao="dados">
              <img src="${ctx}/assets/icons/user.svg" alt="" width="24" height="24">
              <h2>Meus dados</h2>
              <p>Informações da sua conta</p>
            </a>
          </div>
        </section>

        <!-- ===================== PEDIDOS ===================== -->
        <section class="conta-secao" id="pedidos" aria-labelledby="titulo-pedidos" tabindex="-1" hidden>
          <h1 class="conta-secao-titulo" id="titulo-pedidos">Meus pedidos</h1>
          <div id="lista-pedidos" class="conta-pedidos">
            <c:choose>
              <c:when test="${erroPedidos}">
                <div class="conta-vazio" role="alert">
                  <h2>Não foi possível carregar os pedidos</h2>
                  <p>Tente novamente em alguns instantes.</p>
                  <a href="${ctx}/area-pessoal#pedidos" class="conta-btn-primario">Recarregar</a>
                </div>
              </c:when>
              <c:when test="${empty pedidos}">
                <div class="conta-vazio">
                  <img src="${ctx}/assets/icons/package.svg" alt="" width="40" height="40">
                  <h2>Ainda não tem pedidos</h2>
                  <p>Quando fizer a sua primeira compra, ela aparece aqui.</p>
                  <a href="${ctx}/catalogo" class="conta-btn-primario">Explorar a adega</a>
                </div>
              </c:when>
              <c:otherwise>
                <c:forEach var="pedido" items="${pedidos}">
                  <article class="conta-pedido" aria-labelledby="pedido-${pedido.id}">
                    <header>
                      <div>
                        <h2 id="pedido-${pedido.id}">Pedido <c:out value="${pedido.numero}" /></h2>
                        <span>
                          <fmt:formatDate value="${pedido.dataCriacao}" pattern="dd 'de' MMM 'de' yyyy, HH:mm" />
                          · <c:out value="${pedido.formaPagamento.descricao}" /><c:if test="${pedido.parcelas gt 1}"> (${pedido.parcelas}x)</c:if>
                        </span>
                      </div>
                      <span class="conta-status${pedido.status.positivo ? ' conta-status--ok' : ''}"><c:out value="${pedido.status.descricao}" /></span>
                    </header>
                    <ul>
                      <c:forEach var="item" items="${pedido.itens}">
                        <li>
                          <span>${item.quantidade}×
                            <c:choose>
                              <c:when test="${not empty item.produtoId}"><a href="${ctx}/produto?id=${item.produtoId}"><c:out value="${item.nomeProduto}" /></a></c:when>
                              <c:otherwise><c:out value="${item.nomeProduto}" /></c:otherwise>
                            </c:choose>
                          </span>
                          <span><fmt:formatNumber value="${item.total}" type="currency" currencySymbol="R$" /></span>
                        </li>
                      </c:forEach>
                      <li class="conta-pedido-extra">
                        <span><c:out value="${pedido.tipoEntrega.descricao}" /></span>
                        <span><c:choose><c:when test="${pedido.frete.signum() eq 0}">Grátis</c:when><c:otherwise><fmt:formatNumber value="${pedido.frete}" type="currency" currencySymbol="R$" /></c:otherwise></c:choose></span>
                      </li>
                      <c:if test="${pedido.desconto.signum() gt 0}">
                        <li class="conta-pedido-extra">
                          <span>Desconto Pix</span>
                          <span>− <fmt:formatNumber value="${pedido.desconto}" type="currency" currencySymbol="R$" /></span>
                        </li>
                      </c:if>
                    </ul>
                    <footer>
                      <span class="conta-pedido-endereco">
                        <c:out value="${empty pedido.endereco ? 'Retirada na adega · Rua das Videiras, 1978 — Jardins, SP' : pedido.endereco}" />
                      </span>
                      <strong><fmt:formatNumber value="${pedido.total}" type="currency" currencySymbol="R$" /></strong>
                    </footer>
                  </article>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </section>

        <!-- ===================== DADOS ===================== -->
        <section class="conta-secao" id="dados" aria-labelledby="titulo-dados" tabindex="-1" hidden>
          <h1 class="conta-secao-titulo" id="titulo-dados">Meus dados</h1>
          <dl class="conta-dados">
            <div><dt>${ehPJ ? 'Razão social' : 'Nome'}</dt><dd><c:out value="${nomeExibicao}" /></dd></div>
            <div><dt>E-mail</dt><dd><c:out value="${clienteLogado.email}" /></dd></div>
            <%-- O telefone é guardado só com dígitos; aqui volta a ter o formato (11) 99999-9999 --%>
            <c:set var="tel" value="${clienteLogado.telefone}" />
            <c:choose>
              <c:when test="${fn:length(tel) eq 11}"><c:set var="telFormatado" value="(${fn:substring(tel, 0, 2)}) ${fn:substring(tel, 2, 7)}-${fn:substring(tel, 7, 11)}" /></c:when>
              <c:when test="${fn:length(tel) eq 10}"><c:set var="telFormatado" value="(${fn:substring(tel, 0, 2)}) ${fn:substring(tel, 2, 6)}-${fn:substring(tel, 6, 10)}" /></c:when>
              <c:otherwise><c:set var="telFormatado" value="${empty tel ? '—' : tel}" /></c:otherwise>
            </c:choose>
            <div><dt>Telefone</dt><dd><c:out value="${telFormatado}" /></dd></div>
            <c:if test="${not empty documento}">
              <%-- Mostra só os últimos dígitos do documento (privacidade) --%>
              <div><dt>${ehPJ ? 'CNPJ' : 'CPF'}</dt>
                <dd>•••••••${fn:substring(documento, fn:length(documento) - 5, fn:length(documento))}</dd></div>
            </c:if>
            <div><dt>Tipo de conta</dt><dd>${ehPJ ? 'Pessoa jurídica' : 'Pessoa física'}</dd></div>
          </dl>

          <div class="conta-perigo">
            <div>
              <h2>Excluir conta</h2>
              <p>Apaga a sua conta e os seus dados de forma permanente.</p>
            </div>
            <button type="button" class="conta-btn-perigo" id="linkDeletarConta">Excluir conta</button>
          </div>
        </section>

      </div>
    </div>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/modalDeletarConta.jsp" />

  <div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
  <div id="agnello-cart-drawer" class="agnello-cart-drawer">
    <div class="agnello-cart-header">
      <button type="button" id="agnello-cart-close" class="agnello-cart-back-btn">←</button>
      <h2 id="agnello-cart-title">Carrinho (0)</h2>
    </div>
    <div id="agnello-cart-body" class="agnello-cart-body"></div>
  </div>

  <script src="${ctx}/js/pages/area-pessoal.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/modal-deletar-conta.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/carrinho.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/chat.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
