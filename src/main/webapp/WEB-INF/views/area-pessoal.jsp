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
          <a href="#favoritos" class="conta-nav-item" data-secao="favoritos">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s-7.5-4.6-9.5-9.2C1.2 8.7 3.3 5 7 5c2.1 0 3.6 1.1 5 2.8C13.4 6.1 14.9 5 17 5c3.7 0 5.8 3.7 4.5 6.8C19.5 16.4 12 21 12 21z"></path></svg>Favoritos<c:if test="${not empty favoritos}"><span class="conta-nav-contador">${fn:length(favoritos)}</span></c:if></a>
          <a href="#enderecos" class="conta-nav-item" data-secao="enderecos">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s-7-6.1-7-11.5A7 7 0 0 1 19 9.5C19 14.9 12 21 12 21z"></path><circle cx="12" cy="9.5" r="2.5"></circle></svg>Endereços</a>
          <a href="#dados" class="conta-nav-item" data-secao="dados">
            <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8" r="4"></circle><path d="M4 21a8 8 0 0 1 16 0"></path></svg>Meus dados</a>
          <a href="${ctx}/logout" class="conta-nav-item conta-nav-item--sair">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"></path></svg>Encerrar sessão</a>
        </nav>
      </aside>

      <div class="conta-conteudo">

        <%-- Resultado da última ação (ex.: "Senha alterada."), vindo do ContaController --%>
        <c:if test="${not empty flash}">
          <div class="conta-flash conta-flash--${flashTipo eq 'erro' ? 'erro' : 'ok'}" role="${flashTipo eq 'erro' ? 'alert' : 'status'}">
            <span><c:out value="${flash}" /></span>
            <button type="button" class="conta-flash-fechar" aria-label="Fechar aviso">&times;</button>
          </div>
        </c:if>

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
            <a href="#favoritos" class="conta-atalho" data-secao="favoritos">
              <img src="${ctx}/assets/icons/wine.svg" alt="" width="24" height="24">
              <h2>Favoritos</h2>
              <p><c:choose>
                <c:when test="${empty favoritos}">Guarde os rótulos que quer provar</c:when>
                <c:when test="${fn:length(favoritos) eq 1}">1 vinho guardado</c:when>
                <c:otherwise>${fn:length(favoritos)} vinhos guardados</c:otherwise>
              </c:choose></p>
            </a>
            <a href="#enderecos" class="conta-atalho" data-secao="enderecos">
              <img src="${ctx}/assets/icons/package.svg" alt="" width="24" height="24">
              <h2>Endereços</h2>
              <p>Agilize o checkout com endereços guardados</p>
            </a>
            <a href="#dados" class="conta-atalho" data-secao="dados">
              <img src="${ctx}/assets/icons/user.svg" alt="" width="24" height="24">
              <h2>Meus dados</h2>
              <p>Editar dados e trocar a senha</p>
            </a>
          </div>
        </section>

        <!-- ===================== PEDIDOS ===================== -->
        <section class="conta-secao" id="pedidos" aria-labelledby="titulo-pedidos" tabindex="-1" hidden>
          <h1 class="conta-secao-titulo" id="titulo-pedidos">Meus pedidos</h1>
          <p class="conta-secao-desc">Pedidos que ainda aguardam pagamento podem ser cancelados aqui mesmo.</p>
          <p class="conta-aviso-repetir" id="aviso-repetir" role="status" hidden></p>
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
                    <div class="conta-pedido-acoes">
                      <%-- Repete os vinhos no carrinho com o preço e o estoque de HOJE (area-pessoal.js) --%>
                      <button type="button" class="conta-btn-secundario conta-btn-pequeno" data-repetir="${pedido.id}">
                        Comprar novamente
                      </button>
                      <c:if test="${pedido.status.name() eq 'AGUARDANDO_PAGAMENTO'}">
                        <form method="post" action="${ctx}/conta/pedidos/cancelar" class="conta-form-cancelar"
                              data-confirmar="Cancelar o pedido ${fn:escapeXml(pedido.numero)}? As garrafas voltam para a adega e esta ação não pode ser desfeita.">
                          <input type="hidden" name="csrf" value="${csrf}">
                          <input type="hidden" name="id" value="${pedido.id}">
                          <button type="submit" class="conta-btn-texto-perigo">Cancelar pedido</button>
                        </form>
                      </c:if>
                    </div>
                  </article>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </section>

        <!-- ===================== FAVORITOS ===================== -->
        <section class="conta-secao" id="favoritos" aria-labelledby="titulo-favoritos" tabindex="-1" hidden>
          <h1 class="conta-secao-titulo" id="titulo-favoritos">Favoritos</h1>
          <c:choose>
            <c:when test="${erroFavoritos}">
              <div class="conta-vazio" role="alert">
                <h2>Não foi possível carregar os favoritos</h2>
                <p>Tente novamente em alguns instantes.</p>
              </div>
            </c:when>
            <c:otherwise>
              <div class="conta-vazio" id="favoritos-vazio" ${empty favoritos ? '' : 'hidden'}>
                <img src="${ctx}/assets/icons/wine.svg" alt="" width="40" height="40">
                <h2>A sua lista está vazia</h2>
                <p>Toque no coração de um vinho no catálogo para guardá-lo aqui.</p>
                <a href="${ctx}/catalogo" class="conta-btn-primario">Explorar a adega</a>
              </div>
              <ul class="conta-favoritos" id="lista-favoritos">
                <c:forEach var="v" items="${favoritos}">
                  <c:set var="esgotadoFav" value="${v.estoque le 0}" />
                  <fmt:formatNumber var="precoFav" value="${v.preco}" minFractionDigits="2" maxFractionDigits="2" />
                  <li class="conta-favorito" data-produto-card="${v.id}">
                    <a href="${ctx}/produto?id=${v.id}" class="conta-favorito-img">
                      <img src="${ctx}${v.imagemUrl}" alt="" width="64" height="80" loading="lazy" onerror="this.style.visibility='hidden'">
                    </a>
                    <div class="conta-favorito-info">
                      <span class="conta-favorito-meta"><c:out value="${v.origem}" /> · <c:out value="${v.tipo}" /></span>
                      <a href="${ctx}/produto?id=${v.id}" class="conta-favorito-nome"><c:out value="${v.nome}" /></a>
                      <strong class="conta-favorito-preco">R$ ${precoFav}</strong>
                    </div>
                    <div class="conta-favorito-acoes">
                      <c:choose>
                        <c:when test="${esgotadoFav}"><span class="conta-favorito-esgotado">Esgotado</span></c:when>
                        <c:otherwise>
                          <button type="button" class="agnello-btn-comprar conta-btn-pequeno"
                                  data-id="${v.id}" data-nome="<c:out value='${v.nome}' />" data-preco="R$ ${precoFav}"
                                  data-origem="<c:out value='${v.origem} • ${v.tipo}' />" data-imagem="${ctx}${v.imagemUrl}"
                                  aria-label="Adicionar <c:out value='${v.nome}' /> ao carrinho">Adicionar</button>
                        </c:otherwise>
                      </c:choose>
                      <button type="button" class="fav-btn conta-favorito-remover is-favorito" data-produto="${v.id}"
                              data-nome="${fn:escapeXml(v.nome)}" aria-pressed="true"
                              aria-label="Remover ${fn:escapeXml(v.nome)} dos favoritos">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s-7.5-4.6-9.5-9.2C1.2 8.7 3.3 5 7 5c2.1 0 3.6 1.1 5 2.8C13.4 6.1 14.9 5 17 5c3.7 0 5.8 3.7 4.5 6.8C19.5 16.4 12 21 12 21z"></path></svg>
                      </button>
                    </div>
                  </li>
                </c:forEach>
              </ul>
            </c:otherwise>
          </c:choose>
        </section>

        <!-- ===================== ENDEREÇOS ===================== -->
        <section class="conta-secao" id="enderecos" aria-labelledby="titulo-enderecos" tabindex="-1" hidden>
          <h1 class="conta-secao-titulo" id="titulo-enderecos">Endereços</h1>
          <p class="conta-secao-desc">Guarde até ${maxEnderecos} endereços e escolha-os no checkout com um toque.
            Ficam cifrados no nosso banco de dados.</p>

          <c:choose>
            <c:when test="${erroEnderecos}">
              <div class="conta-vazio" role="alert">
                <h2>Não foi possível carregar os endereços</h2>
                <p>Tente novamente em alguns instantes.</p>
              </div>
            </c:when>
            <c:otherwise>
              <c:if test="${not empty enderecos}">
                <ul class="conta-enderecos">
                  <c:forEach var="e" items="${enderecos}">
                    <li class="conta-endereco${e.principal ? ' conta-endereco--principal' : ''}">
                      <div>
                        <strong class="conta-endereco-apelido"><c:out value="${e.apelido}" />
                          <c:if test="${e.principal}"><span class="conta-tag">Principal</span></c:if></strong>
                        <p><c:out value="${e.logradouro}" />, <c:out value="${e.numero}" /><c:if test="${not empty e.complemento}"> — <c:out value="${e.complemento}" /></c:if></p>
                        <p><c:out value="${e.bairro}" /> · <c:out value="${e.cidade}" /> · CEP <c:out value="${e.cepFormatado}" /></p>
                      </div>
                      <div class="conta-endereco-acoes">
                        <c:if test="${not e.principal}">
                          <form method="post" action="${ctx}/conta/enderecos/principal">
                            <input type="hidden" name="csrf" value="${csrf}">
                            <input type="hidden" name="id" value="${e.id}">
                            <button type="submit" class="conta-btn-link">Tornar principal</button>
                          </form>
                        </c:if>
                        <form method="post" action="${ctx}/conta/enderecos/excluir"
                              data-confirmar="Excluir o endereço &quot;${fn:escapeXml(e.apelido)}&quot;?">
                          <input type="hidden" name="csrf" value="${csrf}">
                          <input type="hidden" name="id" value="${e.id}">
                          <button type="submit" class="conta-btn-link conta-btn-link--perigo">Excluir</button>
                        </form>
                      </div>
                    </li>
                  </c:forEach>
                </ul>
              </c:if>

              <c:choose>
                <c:when test="${fn:length(enderecos) ge maxEnderecos}">
                  <p class="conta-nota">Você atingiu o limite de ${maxEnderecos} endereços. Exclua um para adicionar outro.</p>
                </c:when>
                <c:otherwise>
                  <details class="conta-card-form" id="form-novo-endereco" ${empty enderecos ? 'open' : ''}>
                    <summary>+ Adicionar endereço</summary>
                    <form method="post" action="${ctx}/conta/enderecos" class="conta-form conta-form-endereco" novalidate>
                      <input type="hidden" name="csrf" value="${csrf}">
                      <div class="conta-campo conta-campo--meio">
                        <label for="end-apelido">Nome do endereço <span class="conta-opcional">(opcional)</span></label>
                        <input id="end-apelido" name="apelido" maxlength="40" placeholder="Casa, Trabalho…">
                      </div>
                      <div class="conta-campo conta-campo--meio">
                        <label for="end-cep">CEP</label>
                        <input id="end-cep" name="cep" inputmode="numeric" autocomplete="postal-code" maxlength="9"
                               placeholder="00000-000" required aria-describedby="end-cep-status">
                        <small id="end-cep-status" class="conta-ajuda" aria-live="polite"></small>
                      </div>
                      <div class="conta-campo">
                        <label for="end-logradouro">Rua / Avenida</label>
                        <input id="end-logradouro" name="logradouro" maxlength="120" autocomplete="address-line1" required>
                      </div>
                      <div class="conta-campo conta-campo--terco">
                        <label for="end-numero">Número</label>
                        <input id="end-numero" name="numero" maxlength="20" required>
                      </div>
                      <div class="conta-campo conta-campo--doisTercos">
                        <label for="end-complemento">Complemento <span class="conta-opcional">(opcional)</span></label>
                        <input id="end-complemento" name="complemento" maxlength="60" autocomplete="address-line2">
                      </div>
                      <div class="conta-campo conta-campo--meio">
                        <label for="end-bairro">Bairro</label>
                        <input id="end-bairro" name="bairro" maxlength="80" required>
                      </div>
                      <div class="conta-campo conta-campo--meio">
                        <label for="end-cidade">Cidade / UF</label>
                        <input id="end-cidade" name="cidade" maxlength="80" autocomplete="address-level2" placeholder="São Paulo - SP" required>
                      </div>
                      <label class="conta-check">
                        <input type="checkbox" name="principal" ${empty enderecos ? 'checked' : ''}> Usar como endereço principal
                      </label>
                      <div class="conta-form-acoes">
                        <button type="submit" class="conta-btn-primario">Guardar endereço</button>
                      </div>
                    </form>
                  </details>
                </c:otherwise>
              </c:choose>
            </c:otherwise>
          </c:choose>
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

          <%-- Editar dados: e-mail e CPF/CNPJ identificam a conta e não mudam por aqui --%>
          <details class="conta-card-form" id="editar-dados">
            <summary>Editar dados pessoais</summary>
            <form method="post" action="${ctx}/conta/dados" class="conta-form" novalidate>
              <input type="hidden" name="csrf" value="${csrf}">
              <c:choose>
                <c:when test="${ehPJ}">
                  <div class="conta-campo">
                    <label for="dados-razao">Razão social</label>
                    <input id="dados-razao" name="razaoSocial" maxlength="120" required autocomplete="organization"
                           value="<c:out value='${clienteLogado.razaoSocial}' />">
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="conta-campo conta-campo--meio">
                    <label for="dados-nome">Nome</label>
                    <input id="dados-nome" name="nome" maxlength="60" required autocomplete="given-name"
                           value="<c:out value='${clienteLogado.nome}' />">
                  </div>
                  <div class="conta-campo conta-campo--meio">
                    <label for="dados-sobrenome">Sobrenome</label>
                    <input id="dados-sobrenome" name="sobrenome" maxlength="80" required autocomplete="family-name"
                           value="<c:out value='${clienteLogado.sobrenome}' />">
                  </div>
                </c:otherwise>
              </c:choose>
              <div class="conta-campo conta-campo--meio">
                <label for="dados-telefone">Telefone com DDD <span class="conta-opcional">(opcional)</span></label>
                <input id="dados-telefone" name="telefone" type="tel" inputmode="tel" maxlength="15" autocomplete="tel"
                       placeholder="(11) 99999-9999" value="<c:out value='${empty tel ? "" : telFormatado}' />">
              </div>
              <p class="conta-nota conta-form-nota">Para mudar o e-mail ou o ${ehPJ ? 'CNPJ' : 'CPF'}, fale com a nossa equipe em
                <a href="${ctx}/contato">Contato</a>.</p>
              <div class="conta-form-acoes">
                <button type="submit" class="conta-btn-primario">Guardar alterações</button>
              </div>
            </form>
          </details>

          <details class="conta-card-form" id="trocar-senha">
            <summary>Trocar a senha</summary>
            <form method="post" action="${ctx}/conta/senha" class="conta-form conta-form-senha" novalidate>
              <input type="hidden" name="csrf" value="${csrf}">
              <%-- Ajuda os gestores de senhas a associar a nova senha à conta certa --%>
              <input type="email" name="email" value="<c:out value='${clienteLogado.email}' />" autocomplete="username" hidden>
              <div class="conta-campo">
                <label for="senha-atual">Senha atual</label>
                <input id="senha-atual" name="senhaAtual" type="password" required autocomplete="current-password">
              </div>
              <div class="conta-campo conta-campo--meio">
                <label for="senha-nova">Nova senha</label>
                <input id="senha-nova" name="novaSenha" type="password" required minlength="8" maxlength="128"
                       autocomplete="new-password" aria-describedby="senha-nova-ajuda">
                <small id="senha-nova-ajuda" class="conta-ajuda">Mínimo de 8 caracteres. Uma frase longa é mais segura.</small>
              </div>
              <div class="conta-campo conta-campo--meio">
                <label for="senha-confirma">Repita a nova senha</label>
                <input id="senha-confirma" name="confirmaSenha" type="password" required minlength="8" maxlength="128"
                       autocomplete="new-password">
              </div>
              <label class="conta-check">
                <input type="checkbox" class="conta-mostrar-senhas"> Mostrar senhas
              </label>
              <div class="conta-form-acoes">
                <button type="submit" class="conta-btn-primario">Alterar senha</button>
              </div>
            </form>
          </details>

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
  <script src="${ctx}/js/components/favoritos.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/modal-deletar-conta.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/carrinho.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/chat.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
