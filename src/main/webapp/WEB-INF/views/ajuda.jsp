<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Central de Ajuda | Vinheria Agnello</title>
  <meta name="description" content="Perguntas frequentes da Vinheria Agnello: pedidos, prazos de entrega, formas de pagamento, trocas e devoluções, conta e Sommelier Virtual.">
  <jsp:include page="/WEB-INF/componentes/site-head.jsp" />
  <link rel="stylesheet" href="${ctx}/css/institucional.css">
</head>

<body class="inst-body">
  <a class="skip-link" href="#conteudo">Pular para o conteúdo</a>
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="inst-main" id="conteudo">
    <%-- Faixa de título própria: tem a caixa de pesquisa dentro --%>
    <header class="inst-hero">
      <div class="inst-hero-inner">
        <nav class="inst-trilha" aria-label="Você está em">
          <ol>
            <li><a href="${ctx}/home">Início</a></li>
            <li aria-current="page">Central de Ajuda</li>
          </ol>
        </nav>
        <span class="inst-eyebrow">Central de Ajuda</span>
        <h1 class="inst-hero-titulo">Como podemos ajudar?</h1>
        <div class="faq-busca" role="search">
          <label for="faq-pesquisa" class="sr-only">Pesquisar nas perguntas frequentes</label>
          <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7"></circle><path d="m21 21-4.35-4.35"></path></svg>
          <input type="search" id="faq-pesquisa" placeholder="Ex.: prazo de entrega, Pix, troca"
                 autocomplete="off" enterkeyhint="search" aria-controls="faq-lista">
        </div>
      </div>
    </header>

    <section class="inst-secao" aria-label="Perguntas frequentes">
      <div class="faq-layout">
        <%-- Filtro por categoria (o faq.js conta as perguntas de cada uma) --%>
        <div class="faq-categorias" role="group" aria-label="Filtrar por tema">
          <button type="button" class="faq-cat" data-cat="todas" aria-pressed="true">Todos os temas <span class="faq-cat-total"></span></button>
          <button type="button" class="faq-cat" data-cat="pedidos" aria-pressed="false">Pedidos <span class="faq-cat-total"></span></button>
          <button type="button" class="faq-cat" data-cat="entrega" aria-pressed="false">Entrega <span class="faq-cat-total"></span></button>
          <button type="button" class="faq-cat" data-cat="pagamento" aria-pressed="false">Pagamento <span class="faq-cat-total"></span></button>
          <button type="button" class="faq-cat" data-cat="trocas" aria-pressed="false">Trocas e devoluções <span class="faq-cat-total"></span></button>
          <button type="button" class="faq-cat" data-cat="conta" aria-pressed="false">Minha conta <span class="faq-cat-total"></span></button>
          <button type="button" class="faq-cat" data-cat="vinhos" aria-pressed="false">Vinhos e Sommelier <span class="faq-cat-total"></span></button>
        </div>

        <div id="faq-lista">
          <p class="sr-only" id="faq-status" role="status" aria-live="polite"></p>

          <section class="faq-grupo" data-cat="pedidos" aria-labelledby="faq-pedidos">
            <h2 id="faq-pedidos">Pedidos</h2>
            <details class="faq-item" id="como-comprar">
              <summary>Como faço um pedido?</summary>
              <div class="faq-resposta">
                <p>Escolha os vinhos na <a href="${ctx}/catalogo">nossa adega</a> e clique em <strong>Selecionar</strong>.
                  Quando terminar, abra o carrinho e clique em <strong>Finalizar pedido</strong>. No checkout você
                  informa o e-mail, o endereço, a forma de entrega e o pagamento.</p>
              </div>
            </details>
            <details class="faq-item" id="acompanhar-pedido">
              <summary>Onde acompanho os meus pedidos?</summary>
              <div class="faq-resposta">
                <p>Entre na sua conta e abra <a href="${ctx}/area-pessoal#pedidos">Meus pedidos</a>.
                  Lá aparecem a data, os vinhos, o valor e a situação de cada compra.</p>
              </div>
            </details>
            <details class="faq-item" id="alterar-pedido">
              <summary>Posso alterar ou cancelar um pedido?</summary>
              <div class="faq-resposta">
                <p>Sim, enquanto ele ainda não tiver saído para entrega. Fale conosco pelo
                  <a href="${ctx}/contato">Contato</a> ou pelo WhatsApp informando o número do pedido.</p>
              </div>
            </details>
            <details class="faq-item" id="idade-minima">
              <summary>Preciso ter 18 anos para comprar?</summary>
              <div class="faq-resposta">
                <p>Sim. A venda de bebidas alcoólicas para menores de 18 anos é proibida por lei. Na entrega,
                  o portador pode pedir um documento com foto da pessoa que recebe o pedido.</p>
              </div>
            </details>
          </section>

          <section class="faq-grupo" data-cat="entrega" aria-labelledby="faq-entrega">
            <h2 id="faq-entrega">Entrega</h2>
            <details class="faq-item" id="prazo-entrega">
              <summary>Qual é o prazo de entrega?</summary>
              <div class="faq-resposta">
                <p>Com o <strong>Frete Térmico Agnello</strong>, entregamos na cidade de São Paulo e na Grande São Paulo
                  em até 48 horas após a confirmação do pagamento. Os prazos das outras regiões estão em
                  <a href="${ctx}/entregas-e-trocas#prazos">Entregas e Trocas</a>.</p>
              </div>
            </details>
            <details class="faq-item" id="valor-frete">
              <summary>Quanto custa o frete?</summary>
              <div class="faq-resposta">
                <p>O Frete Térmico Agnello custa R$ 25,00 por pedido. A retirada na adega é gratuita.</p>
              </div>
            </details>
            <details class="faq-item" id="retirada">
              <summary>Posso retirar o pedido na loja?</summary>
              <div class="faq-resposta">
                <p>Pode. Escolha <strong>Retirada na adega</strong> no checkout. O pedido fica pronto em até 3 horas,
                  e avisamos por e-mail. Traga um documento com foto.</p>
              </div>
            </details>
            <details class="faq-item" id="temperatura">
              <summary>Como o vinho é protegido durante o transporte?</summary>
              <div class="faq-resposta">
                <p>Usamos embalagem térmica e antichoque, que protege a garrafa do calor e de impactos.
                  Se mesmo assim ela chegar danificada, enviamos outra sem custo.</p>
              </div>
            </details>
          </section>

          <section class="faq-grupo" data-cat="pagamento" aria-labelledby="faq-pagamento">
            <h2 id="faq-pagamento">Pagamento</h2>
            <details class="faq-item" id="formas-pagamento">
              <summary>Quais são as formas de pagamento?</summary>
              <div class="faq-resposta">
                <p>Aceitamos <strong>Pix</strong> (com 5% de desconto), <strong>cartão de crédito</strong>
                  em até 6x sem juros (parcela mínima de R$ 50,00) e <strong>boleto bancário</strong>.</p>
              </div>
            </details>
            <details class="faq-item" id="boleto">
              <summary>Quando o pedido pago por boleto é liberado?</summary>
              <div class="faq-resposta">
                <p>O boleto vence em 2 dias úteis. O pedido é separado depois da compensação bancária,
                  que pode levar até 3 dias úteis após o pagamento.</p>
              </div>
            </details>
            <details class="faq-item" id="seguranca-pagamento">
              <summary>O pagamento no site é seguro?</summary>
              <div class="faq-resposta">
                <p>Sim. O site usa conexão segura e não mostramos os dados completos do seu cartão em nenhum momento.
                  Saiba mais na <a href="${ctx}/privacidade">Política de Privacidade</a>.</p>
              </div>
            </details>
          </section>

          <section class="faq-grupo" data-cat="trocas" aria-labelledby="faq-trocas">
            <h2 id="faq-trocas">Trocas e devoluções</h2>
            <details class="faq-item" id="arrependimento">
              <summary>Posso desistir da compra?</summary>
              <div class="faq-resposta">
                <p>Sim. Pelo Código de Defesa do Consumidor, você tem até <strong>7 dias corridos</strong> após o recebimento
                  para desistir da compra, com a garrafa lacrada e na embalagem original. Devolvemos o valor total, frete incluído.</p>
              </div>
            </details>
            <details class="faq-item" id="garrafa-danificada">
              <summary>A garrafa chegou quebrada ou com vazamento. E agora?</summary>
              <div class="faq-resposta">
                <p>Não recuse sem antes registrar: tire uma foto da embalagem e da garrafa e envie pelo
                  <a href="${ctx}/contato">Contato</a> em até 7 dias. Enviamos outra garrafa ou devolvemos o valor, como preferir.</p>
              </div>
            </details>
            <details class="faq-item" id="vinho-defeito">
              <summary>E se o vinho estiver com defeito (rolha, oxidação)?</summary>
              <div class="faq-resposta">
                <p>Guarde a garrafa com o que restou do vinho e fale conosco em até 30 dias. Avaliamos e, se confirmado
                  o defeito, trocamos o produto. Veja todos os detalhes em <a href="${ctx}/entregas-e-trocas#trocas">Entregas e Trocas</a>.</p>
              </div>
            </details>
          </section>

          <section class="faq-grupo" data-cat="conta" aria-labelledby="faq-conta">
            <h2 id="faq-conta">Minha conta</h2>
            <details class="faq-item" id="criar-conta">
              <summary>Preciso de uma conta para comprar?</summary>
              <div class="faq-resposta">
                <p>Sim, mas você pode criá-la no próprio checkout, em poucos segundos. Também pode
                  <a href="${ctx}/cadastro">criar uma conta agora</a>, como pessoa física ou jurídica.</p>
              </div>
            </details>
            <details class="faq-item" id="esqueci-senha">
              <summary>Esqueci a minha senha. Como recupero?</summary>
              <div class="faq-resposta">
                <p>Na página de login, clique em <a href="${ctx}/esqueci-senha">Esqueci a senha</a>. Enviaremos um
                  link para o seu e-mail para criar uma senha nova.</p>
              </div>
            </details>
            <details class="faq-item" id="excluir-conta">
              <summary>Como excluo a minha conta e os meus dados?</summary>
              <div class="faq-resposta">
                <p>Na <a href="${ctx}/area-pessoal#dados">área pessoal</a>, em <strong>Meus dados</strong>, clique em
                  <strong>Excluir conta</strong>. Os seus dados pessoais são apagados, exceto os que a lei nos obriga a guardar
                  (por exemplo, notas fiscais).</p>
              </div>
            </details>
          </section>

          <section class="faq-grupo" data-cat="vinhos" aria-labelledby="faq-vinhos">
            <h2 id="faq-vinhos">Vinhos e Sommelier</h2>
            <details class="faq-item" id="sommelier-virtual">
              <summary>O que é o Sommelier Virtual?</summary>
              <div class="faq-resposta">
                <p>É um quiz rápido sobre o seu gosto e a ocasião. No final, indicamos os rótulos da adega que
                  mais combinam com você. <a href="${ctx}/quiz">Experimente o Sommelier Virtual</a>.</p>
              </div>
            </details>
            <details class="faq-item" id="quantidade-evento">
              <summary>Quantas garrafas preciso para um evento?</summary>
              <div class="faq-resposta">
                <p>Use a nossa <a href="${ctx}/home#calculadora">Calculadora de Eventos</a>: informe convidados,
                  duração e estilo do evento, e ela sugere a quantidade e a proporção de tintos, brancos e espumantes.</p>
              </div>
            </details>
            <details class="faq-item" id="guardar-vinho">
              <summary>Como devo guardar o vinho em casa?</summary>
              <div class="faq-resposta">
                <p>Deite a garrafa (para a rolha não secar), longe da luz e do calor, em um lugar com temperatura estável,
                  de preferência entre 12 °C e 16 °C. Depois de aberto, tampe e consuma em até 3 dias.</p>
              </div>
            </details>
          </section>

          <div class="faq-vazio" id="faq-vazio" hidden>
            <strong>Não encontramos nada sobre “<span id="faq-termo"></span>”.</strong>
            <p>Tente outra palavra ou <a href="${ctx}/contato">fale com a nossa equipe</a>.</p>
          </div>
        </div>
      </div>
    </section>

    <section class="inst-secao inst-secao--sem-topo" aria-label="Ainda precisa de ajuda?">
      <div class="inst-container">
        <div class="inst-cta">
          <div>
            <h2>Não encontrou a sua resposta?</h2>
            <p>A nossa equipe responde de segunda a sábado, das 10h às 20h.</p>
          </div>
          <div class="inst-cta-acoes">
            <a href="${ctx}/contato" class="btn-gold">Falar com a equipe <span class="btn-arrow" aria-hidden="true">→</span></a>
            <a href="https://wa.me/5511999999999" class="btn-outline" target="_blank" rel="noopener">WhatsApp</a>
          </div>
        </div>
      </div>
    </section>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/carrinho-gaveta.jsp" />
  <script src="${ctx}/js/pages/ajuda.js"></script>
</body>
</html>
