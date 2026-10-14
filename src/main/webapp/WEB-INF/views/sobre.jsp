<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="heroAtual" scope="request" value="Nossa História" />
<c:set var="heroEyebrow" scope="request" value="Desde 1978" />
<c:set var="heroTitulo" scope="request" value="Uma família, um balcão e muitas garrafas abertas" />
<c:set var="heroTexto" scope="request" value="A Vinheria Agnello nasceu como uma pequena loja de bairro em São Paulo. Quase cinco décadas depois, continua a escolher cada rótulo do mesmo jeito: provando antes de vender." />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Nossa História | Vinheria Agnello</title>
  <meta name="description" content="Conheça a história da Vinheria Agnello: loja de vinhos da família Agnello em São Paulo desde 1978, com curadoria feita por Giulio e Bianca.">
  <jsp:include page="/WEB-INF/componentes/site-head.jsp" />
  <link rel="stylesheet" href="${ctx}/css/institucional.css">
</head>

<body class="inst-body">
  <a class="skip-link" href="#conteudo">Pular para o conteúdo</a>
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="inst-main" id="conteudo">
    <jsp:include page="/WEB-INF/componentes/page-hero.jsp" />

    <%-- 1. Origem --%>
    <section class="inst-secao" aria-labelledby="titulo-origem">
      <div class="inst-container inst-duas-colunas">
        <div class="inst-texto">
          <div class="inst-titulo-secao">
            <span class="inst-eyebrow">Como tudo começou</span>
            <h2 id="titulo-origem">A sala de estar do Sr. Giulio</h2>
          </div>
          <p>Em 1978, Giulio Agnello abriu as portas de uma loja pequena, com um balcão de madeira
            e poucas prateleiras. Quem entrava não encontrava um vendedor, mas um anfitrião: cada cliente
            saía com uma garrafa escolhida para a ocasião e com uma boa história para contar à mesa.</p>
          <p>O tempo passou, a adega cresceu e a clientela também. O que nunca mudou foi a regra da
            casa: nenhum vinho entra no nosso catálogo sem antes ter sido provado e aprovado pela família.</p>
          <blockquote class="inst-citacao">
            “Um bom vinho não se vende. Indica-se, como se indica um amigo.”
            <cite>Giulio Agnello, fundador</cite>
          </blockquote>
        </div>

        <figure class="inst-figura">
          <div class="inst-figura-moldura">
            <img src="${ctx}/assets/images/home-atendimento-image.webp"
                 alt="Taça de vinho branco servida no balcão da Vinheria Agnello"
                 width="1000" height="1500" loading="lazy" decoding="async">
          </div>
          <figcaption>O balcão onde tudo começou, em São Paulo.</figcaption>
        </figure>
      </div>
    </section>

    <%-- 2. Linha do tempo --%>
    <section class="inst-secao inst-secao--branca" aria-labelledby="titulo-trajetoria">
      <div class="inst-container">
        <div class="inst-titulo-secao inst-titulo-secao--centro">
          <span class="inst-eyebrow">Nossa trajetória</span>
          <h2 id="titulo-trajetoria">De loja de bairro a adega online</h2>
        </div>

        <ol class="timeline">
          <li>
            <span class="timeline-ano">1978</span>
            <h3>As portas se abrem</h3>
            <p>Giulio inaugura a vinheria com uma seleção curta, escolhida garrafa a garrafa.</p>
          </li>
          <li>
            <span class="timeline-ano">1990</span>
            <h3>A adega cresce</h3>
            <p>Chegam os primeiros rótulos importados, e a loja vira ponto de encontro de quem gosta de vinho.</p>
          </li>
          <li>
            <span class="timeline-ano">2015</span>
            <h3>A segunda geração</h3>
            <p>Bianca Agnello assume a curadoria ao lado do pai e traz novos produtores para a casa.</p>
          </li>
          <li>
            <span class="timeline-ano">Hoje</span>
            <h3>Do balcão para a sua casa</h3>
            <p>O atendimento da loja chega ao site, com o Sommelier Virtual e entrega com controle de temperatura.</p>
          </li>
        </ol>
      </div>
    </section>

    <%-- 3. Valores --%>
    <section class="inst-secao" aria-labelledby="titulo-valores">
      <div class="inst-container">
        <div class="inst-titulo-secao inst-titulo-secao--centro">
          <span class="inst-eyebrow">O que nos guia</span>
          <h2 id="titulo-valores">Três promessas que mantemos desde o primeiro dia</h2>
        </div>

        <ul class="inst-cards">
          <li class="inst-card">
            <span class="inst-card-icone" aria-hidden="true">
              <svg viewBox="0 0 24 24"><path d="M8 22h8M12 15v7M7 2h10l-1 7a4 4 0 0 1-8 0z"></path></svg>
            </span>
            <h3>Curadoria provada</h3>
            <p>Cada rótulo é degustado pela família antes de chegar à adega. Se não serviríamos em casa, não vendemos.</p>
          </li>
          <li class="inst-card">
            <span class="inst-card-icone" aria-hidden="true">
              <svg viewBox="0 0 24 24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            </span>
            <h3>Atendimento de pessoa para pessoa</h3>
            <p>Na loja, no WhatsApp ou pelo Sommelier Virtual: você fala com quem conhece cada garrafa.</p>
          </li>
          <li class="inst-card">
            <span class="inst-card-icone" aria-hidden="true">
              <svg viewBox="0 0 24 24"><path d="M1 3h15v13H1zM16 8h4l3 3v5h-7z"></path><circle cx="5.5" cy="18.5" r="2.5"></circle><circle cx="18.5" cy="18.5" r="2.5"></circle></svg>
            </span>
            <h3>Entrega com cuidado</h3>
            <p>Embalagem térmica e antichoque. Se a garrafa não chegar perfeita, enviamos outra.</p>
          </li>
        </ul>
      </div>
    </section>

    <%-- 4. Números --%>
    <section class="inst-secao inst-secao--vinho" aria-label="A Vinheria Agnello em números">
      <ul class="inst-container inst-numeros">
        <li><strong>1978</strong><span>ano de fundação</span></li>
        <li><strong>2</strong><span>gerações na curadoria</span></li>
        <li><strong>48h</strong><span>entrega com controle térmico</span></li>
        <li><strong>100%</strong><span>dos rótulos provados pela família</span></li>
      </ul>
    </section>

    <%-- 5. Chamada final --%>
    <section class="inst-secao" aria-label="Próximos passos">
      <div class="inst-container">
        <div class="inst-cta">
          <div>
            <h2>Venha provar a nossa história</h2>
            <p>Explore a adega ou deixe o Sommelier Virtual escolher por você.</p>
          </div>
          <div class="inst-cta-acoes">
            <a href="${ctx}/catalogo" class="btn-gold">Ver a adega <span class="btn-arrow" aria-hidden="true">→</span></a>
            <a href="${ctx}/quiz" class="btn-outline">Sommelier Virtual</a>
          </div>
        </div>
      </div>
    </section>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/carrinho-gaveta.jsp" />
</body>
</html>
