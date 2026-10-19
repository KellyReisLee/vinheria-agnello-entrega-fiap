<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  PÁGINA DO PRODUTO  —  /produto?id=12   (preenchida pelo ProdutoController)
    produto      → com.agnello.model.Produto   (vazio = "não encontrado", status 404)
    relacionados → List<Produto>               (outros vinhos do mesmo tipo, até 4)
--%>
<fmt:setLocale value="pt_BR" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="p" value="${produto}" />

<c:if test="${not empty p}">
  <c:set var="imgVinho" value="${ctx}${fn:startsWith(p.imagem, '.') ? fn:substring(p.imagem, 1, fn:length(p.imagem)) : p.imagem}" />
  <c:set var="esgotado" value="${p.estoque le 0}" />
  <c:set var="tipoMin" value="${fn:toLowerCase(p.tipo)}" />
  <fmt:formatNumber var="precoFmt" value="${p.preco}" minFractionDigits="2" maxFractionDigits="2" />
  <fmt:formatNumber var="precoPixFmt" value="${p.preco * 0.95}" minFractionDigits="2" maxFractionDigits="2" />
  <%-- Mesma regra do checkout: até 6x sem juros, parcela mínima de R$ 50 --%>
  <c:set var="parcelas" value="${p.preco ge 300 ? 6 : (p.preco ge 50 ? fn:substringBefore(p.preco / 50, '.') : 1)}" />
  <fmt:formatNumber var="parcelaFmt" value="${p.preco / parcelas}" minFractionDigits="2" maxFractionDigits="2" />

  <%-- Harmonização e temperatura de serviço dependem do tipo de vinho --%>
  <c:choose>
    <c:when test="${fn:contains(tipoMin, 'espumante') or fn:contains(tipoMin, 'prosecco') or fn:contains(tipoMin, 'cava') or fn:contains(tipoMin, 'champ')}">
      <c:set var="temperatura" value="6 °C a 8 °C" />
      <c:set var="harmoniza" value="Entradas, frutos do mar, frituras leves e brindes" />
      <c:set var="tacaTipo" value="Flûte ou tulipa" />
    </c:when>
    <c:when test="${fn:contains(tipoMin, 'ros')}">
      <c:set var="temperatura" value="8 °C a 10 °C" />
      <c:set var="harmoniza" value="Saladas, peixes grelhados, comida mediterrânea" />
      <c:set var="tacaTipo" value="Taça de vinho branco" />
    </c:when>
    <c:when test="${fn:contains(tipoMin, 'branco')}">
      <c:set var="temperatura" value="8 °C a 12 °C" />
      <c:set var="harmoniza" value="Peixes, frutos do mar, aves e queijos frescos" />
      <c:set var="tacaTipo" value="Taça de vinho branco" />
    </c:when>
    <c:otherwise>
      <c:set var="temperatura" value="16 °C a 18 °C" />
      <c:set var="harmoniza" value="Carnes vermelhas, massas com molho e queijos curados" />
      <c:set var="tacaTipo" value="Taça Bordeaux" />
    </c:otherwise>
  </c:choose>
</c:if>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <c:choose>
    <c:when test="${not empty p}">
      <title><c:out value="${p.nome}" /> | Vinheria Agnello</title>
      <meta name="description" content="<c:out value='${p.nome}' /> (${fn:escapeXml(p.tipo)}, ${fn:escapeXml(p.origem)}) por R$ ${precoFmt}. Selecionado pela família Agnello, com entrega em embalagem térmica.">
      <meta property="og:type" content="product">
      <meta property="og:title" content="<c:out value='${p.nome}' />">
      <meta property="og:image" content="${imgVinho}">
    </c:when>
    <c:otherwise>
      <title>Vinho não encontrado | Vinheria Agnello</title>
      <meta name="robots" content="noindex">
    </c:otherwise>
  </c:choose>
  <jsp:include page="/WEB-INF/componentes/site-head.jsp" />
  <link rel="stylesheet" href="${ctx}/css/produto.css?v=${initParam.versaoArquivos}">
</head>

<body class="pdp-body">
  <a class="skip-link" href="#conteudo">Pular para o conteúdo</a>
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="pdp-main" id="conteudo">
    <c:choose>
      <%-- ===================== VINHO NÃO ENCONTRADO ===================== --%>
      <c:when test="${empty p}">
        <section class="pdp-vazio">
          <span class="pdp-vazio-icone" aria-hidden="true">🍷</span>
          <h1>Este vinho não está mais na adega</h1>
          <p>Ele pode ter esgotado ou o endereço está incorreto. Veja outros rótulos selecionados pela família.</p>
          <a href="${ctx}/catalogo" class="btn-gold">Ver todos os vinhos <span class="btn-arrow" aria-hidden="true">→</span></a>
        </section>
      </c:when>

      <%-- ===================== PRODUTO ===================== --%>
      <c:otherwise>
        <nav class="pdp-trilha" aria-label="Você está em">
          <ol>
            <li><a href="${ctx}/home">Início</a></li>
            <li><a href="${ctx}/catalogo">Nossos Rótulos</a></li>
            <li aria-current="page"><c:out value="${p.nome}" /></li>
          </ol>
        </nav>

        <article class="pdp-produto" id="pdp-produto"
                 data-id="${p.id}" data-nome="<c:out value='${p.nome}' />"
                 data-preco="R$ ${precoFmt}" data-imagem="${imgVinho}"
                 data-origem="<c:out value='${p.origem} • ${p.tipo}' />"
                 data-estoque="${p.estoque}">

          <%-- Foto --%>
          <div class="pdp-galeria">
            <c:choose>
              <c:when test="${esgotado}"><span class="pdp-selo pdp-selo--esgotado">Esgotado</span></c:when>
              <c:when test="${not empty p.desconto}"><span class="pdp-selo"><c:out value="${p.desconto}" /></span></c:when>
            </c:choose>
            <img src="${imgVinho}" alt="Garrafa de <c:out value='${p.nome}' />"
                 width="420" height="560" fetchpriority="high"
                 onerror="this.style.visibility='hidden'">
          </div>

          <%-- Informações e compra --%>
          <div class="pdp-info">
            <span class="pdp-meta"><c:out value="${p.origem}" /> <span aria-hidden="true">•</span> <c:out value="${p.tipo}" /></span>
            <h1 class="pdp-nome"><c:out value="${p.nome}" /></h1>

            <c:if test="${not empty p.pontuacao}">
              <p class="pdp-pontuacao">
                <img src="${ctx}/assets/icons/star-check.svg" alt="" width="16" height="16">
                <c:out value="${p.pontuacao}" />
              </p>
            </c:if>

            <div class="pdp-precos">
              <c:if test="${p.precoAntigo > p.preco}">
                <span class="pdp-preco-antigo">
                  <span class="sr-only">De</span>
                  R$ <fmt:formatNumber value="${p.precoAntigo}" minFractionDigits="2" maxFractionDigits="2" />
                </span>
              </c:if>
              <span class="pdp-preco">
                <c:if test="${p.precoAntigo > p.preco}"><span class="sr-only">por</span></c:if>
                R$ ${precoFmt}
              </span>
              <span class="pdp-parcelas">
                ou <strong>${parcelas}x de R$ ${parcelaFmt}</strong> sem juros no cartão
              </span>
              <span class="pdp-pix">
                <strong>R$ ${precoPixFmt}</strong> no Pix <span class="pdp-pix-tag">5% off</span>
              </span>
            </div>

            <c:choose>
              <c:when test="${esgotado}">
                <p class="pdp-estoque pdp-estoque--esgotado">Esgotado no momento</p>
                <a href="${ctx}/contato" class="pdp-btn-secundario">Avise-me quando chegar</a>
              </c:when>
              <c:otherwise>
                <p class="pdp-estoque">
                  <span class="pdp-ponto" aria-hidden="true"></span>
                  <c:choose>
                    <c:when test="${p.estoque le 5}">Últimas ${p.estoque} unidades</c:when>
                    <c:otherwise>Em estoque, pronto para envio</c:otherwise>
                  </c:choose>
                </p>

                <div class="pdp-compra">
                  <div class="pdp-qtd" role="group" aria-label="Quantidade">
                    <button type="button" class="pdp-qtd-btn" data-qtd="-1" aria-label="Diminuir quantidade">−</button>
                    <input type="number" id="pdp-quantidade" class="pdp-qtd-valor" value="1" min="1"
                           max="${p.estoque gt 12 ? 12 : p.estoque}" inputmode="numeric" aria-label="Quantidade">
                    <button type="button" class="pdp-qtd-btn" data-qtd="1" aria-label="Aumentar quantidade">+</button>
                  </div>
                  <button type="button" class="pdp-btn-comprar" id="pdp-adicionar">Adicionar ao carrinho</button>
                </div>
              </c:otherwise>
            </c:choose>

            <%-- Favoritos (favoritos.js): sem conta, leva ao login e volta para aqui --%>
            <button type="button" class="fav-btn pdp-fav${favorito ? ' is-favorito' : ''}" data-produto="${p.id}"
                    data-nome="${fn:escapeXml(p.nome)}" aria-pressed="${favorito ? 'true' : 'false'}">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s-7.5-4.6-9.5-9.2C1.2 8.7 3.3 5 7 5c2.1 0 3.6 1.1 5 2.8C13.4 6.1 14.9 5 17 5c3.7 0 5.8 3.7 4.5 6.8C19.5 16.4 12 21 12 21z"/></svg>
              <span class="fav-texto">${favorito ? 'Nos favoritos' : 'Guardar nos favoritos'}</span>
            </button>

            <ul class="pdp-garantias">
              <li>
                <img src="${ctx}/assets/icons/truck.svg" alt="" width="20" height="20">
                <span><strong>Frete Térmico</strong> em até 48h na Grande SP · ou retire grátis na adega</span>
              </li>
              <li>
                <img src="${ctx}/assets/icons/package.svg" alt="" width="20" height="20">
                <span><strong>Chega intacta</strong> ou enviamos outra garrafa</span>
              </li>
              <li>
                <img src="${ctx}/assets/icons/shield-check.svg" alt="" width="20" height="20">
                <span><strong>7 dias</strong> para desistir · <a href="${ctx}/entregas-e-trocas">ver política</a></span>
              </li>
            </ul>
          </div>
        </article>

        <%-- Descrição e ficha técnica --%>
        <section class="pdp-detalhes" aria-label="Sobre este vinho">
          <div class="pdp-descricao">
            <h2>Sobre este vinho</h2>
            <c:choose>
              <c:when test="${not empty p.descricao}"><p><c:out value="${p.descricao}" /></p></c:when>
              <c:otherwise><p>Rótulo provado e aprovado pela família Agnello. Fale com a nossa equipe para saber mais sobre este vinho.</p></c:otherwise>
            </c:choose>

            <div class="pdp-dica">
              <strong>Dica do Sr. Giulio</strong>
              <p>Sirva de <c:out value="${temperatura}" />. Combina com: <c:out value="${fn:toLowerCase(harmoniza)}" />.</p>
            </div>
          </div>

          <div class="pdp-ficha">
            <h2>Ficha técnica</h2>
            <dl>
              <div><dt>Tipo</dt><dd><c:out value="${p.tipo}" /></dd></div>
              <div><dt>Origem</dt><dd><c:out value="${p.origem}" /></dd></div>
              <c:if test="${not empty p.pontuacao}">
                <div><dt>Pontuação</dt><dd><c:out value="${p.pontuacao}" /></dd></div>
              </c:if>
              <div><dt>Temperatura de serviço</dt><dd><c:out value="${temperatura}" /></dd></div>
              <div><dt>Taça indicada</dt><dd><c:out value="${tacaTipo}" /></dd></div>
              <div><dt>Harmonização</dt><dd><c:out value="${harmoniza}" /></dd></div>
            </dl>
          </div>
        </section>

        <%-- Outros vinhos (mesmo visual de card da home e do catálogo) --%>
        <c:if test="${not empty relacionados}">
          <section class="vitrine-section pdp-relacionados" aria-labelledby="titulo-relacionados">
            <div class="section-header">
              <div class="section-header-text">
                <span class="section-eyebrow">Da mesma adega</span>
                <h2 id="titulo-relacionados">Você também pode gostar</h2>
              </div>
              <a href="${ctx}/catalogo" class="section-header-link">Ver toda a adega <span aria-hidden="true">→</span></a>
            </div>

            <ul class="product-grid">
              <c:forEach var="r" items="${relacionados}" end="3">
                <c:set var="imgRel" value="${ctx}${fn:startsWith(r.imagem, '.') ? fn:substring(r.imagem, 1, fn:length(r.imagem)) : r.imagem}" />
                <fmt:formatNumber var="precoRel" value="${r.preco}" minFractionDigits="2" maxFractionDigits="2" />
                <li class="product-card">
                  <a class="product-img-placeholder" href="${ctx}/produto?id=${r.id}" tabindex="-1" aria-hidden="true">
                    <img src="${imgRel}" alt="" width="160" height="190" loading="lazy" decoding="async"
                         onerror="this.style.visibility='hidden'">
                  </a>
                  <div class="product-info">
                    <span class="product-meta"><c:out value="${r.origem}" /> • <c:out value="${r.tipo}" /></span>
                    <h3 class="product-name"><a class="product-link" href="${ctx}/produto?id=${r.id}"><c:out value="${r.nome}" /></a></h3>
                  </div>
                  <div class="product-footer">
                    <div class="price-box"><span class="product-price">R$ ${precoRel}</span></div>
                    <c:choose>
                      <c:when test="${r.estoque le 0}">
                        <button type="button" class="agnello-btn-comprar" disabled>Esgotado</button>
                      </c:when>
                      <c:otherwise>
                      <button type="button" class="agnello-btn-comprar"
                              aria-label="Adicionar <c:out value='${r.nome}' /> ao carrinho"
                              data-id="${r.id}" data-nome="<c:out value='${r.nome}' />"
                              data-preco="R$ ${precoRel}"
                              data-origem="<c:out value='${r.origem} • ${r.tipo}' />"
                              data-imagem="${imgRel}">Selecionar</button>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </li>
              </c:forEach>
            </ul>
          </section>
        </c:if>

        <%-- Barra de compra fixa no telemóvel (aparece quando o botão principal sai do ecrã) --%>
        <c:if test="${not esgotado}">
          <div class="pdp-barra" id="pdp-barra" aria-hidden="true">
            <div class="pdp-barra-info">
              <span class="pdp-barra-nome"><c:out value="${p.nome}" /></span>
              <strong>R$ ${precoFmt}</strong>
            </div>
            <button type="button" class="pdp-btn-comprar" id="pdp-adicionar-barra" tabindex="-1">Adicionar</button>
          </div>
        </c:if>
      </c:otherwise>
    </c:choose>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/carrinho-gaveta.jsp" />
  <script src="${ctx}/js/pages/produto.js?v=${initParam.versaoArquivos}"></script>
  <script src="${ctx}/js/components/favoritos.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
