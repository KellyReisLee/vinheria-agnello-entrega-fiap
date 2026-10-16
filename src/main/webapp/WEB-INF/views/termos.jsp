<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="heroAtual" scope="request" value="Termos de Uso" />
<c:set var="heroEyebrow" scope="request" value="Condições gerais" />
<c:set var="heroTitulo" scope="request" value="Termos de Uso" />
<c:set var="heroTexto" scope="request" value="As regras para usar o site e comprar na Vinheria Agnello. Ao navegar ou fazer um pedido, você concorda com estas condições." />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Termos de Uso | Vinheria Agnello</title>
  <meta name="description" content="Termos de Uso da Vinheria Agnello: idade mínima, cadastro, preços, pagamento, entrega, trocas e responsabilidades.">
  <jsp:include page="/WEB-INF/componentes/site-head.jsp" />
  <link rel="stylesheet" href="${ctx}/css/institucional.css?v=${initParam.versaoArquivos}">
</head>

<body class="inst-body">
  <a class="skip-link" href="#conteudo">Pular para o conteúdo</a>
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="inst-main" id="conteudo">
    <jsp:include page="/WEB-INF/componentes/page-hero.jsp" />

    <div class="inst-secao">
      <div class="doc-layout">
        <details class="doc-indice" open>
          <summary>Nesta página</summary>
          <nav aria-label="Índice da página">
            <ol>
              <li><a href="#aceitacao">Aceitação</a></li>
              <li><a href="#idade">Idade mínima</a></li>
              <li><a href="#conta">Cadastro e conta</a></li>
              <li><a href="#produtos">Produtos, preços e estoque</a></li>
              <li><a href="#pedidos">Pedidos e pagamento</a></li>
              <li><a href="#entrega">Entrega, trocas e devoluções</a></li>
              <li><a href="#recomendacoes">Sommelier Virtual e Calculadora</a></li>
              <li><a href="#propriedade">Propriedade intelectual</a></li>
              <li><a href="#responsabilidade">Consumo responsável</a></li>
              <li><a href="#lei">Lei aplicável</a></li>
            </ol>
          </nav>
        </details>

        <article class="prose">
          <p class="doc-atualizado">Última atualização: 14 de outubro de 2026</p>

          <h2 id="aceitacao">Aceitação</h2>
          <p>Estes Termos regulam o uso do site da <strong>Vinheria Agnello</strong> e as compras feitas nele.
            Ao usar o site, você declara que leu e concorda com estes Termos e com a
            <a href="${ctx}/privacidade">Política de Privacidade</a>.</p>

          <h2 id="idade">Idade mínima</h2>
          <p>A venda de bebidas alcoólicas para menores de 18 anos é proibida (Lei nº 13.106/2015). Para comprar,
            você declara ter <strong>18 anos ou mais</strong>. Podemos pedir um documento com foto na entrega
            e cancelar pedidos quando houver indício de compra por menores.</p>

          <h2 id="conta">Cadastro e conta</h2>
          <ul>
            <li>As informações do cadastro devem ser verdadeiras e estar atualizadas.</li>
            <li>A senha é pessoal. Você é responsável pelo que for feito com a sua conta; se suspeitar de uso indevido,
              troque a senha e avise-nos.</li>
            <li>Você pode excluir a sua conta a qualquer momento na área pessoal.</li>
          </ul>

          <h2 id="produtos">Produtos, preços e estoque</h2>
          <ul>
            <li>As fotos são ilustrativas. A safra enviada é a disponível no momento da compra e pode ser diferente da foto.</li>
            <li>Os preços estão em reais (R$) e podem mudar sem aviso. Vale o preço exibido no momento em que o pedido é concluído.</li>
            <li>Os produtos estão sujeitos à disponibilidade de estoque. Se um item acabar depois da compra,
              avisamos e devolvemos o valor correspondente.</li>
          </ul>

          <h2 id="pedidos">Pedidos e pagamento</h2>
          <p>O pedido é confirmado depois da aprovação do pagamento. Aceitamos Pix (com 5% de desconto), cartão de crédito
            em até 6x sem juros e boleto bancário. Podemos cancelar pedidos com indício de fraude ou com erro evidente
            de preço; nesses casos, devolvemos integralmente o valor pago.</p>

          <h2 id="entrega">Entrega, trocas e devoluções</h2>
          <p>Prazos, formas de entrega, direito de arrependimento em 7 dias, trocas e reembolsos estão descritos em
            <a href="${ctx}/entregas-e-trocas">Entregas e Trocas</a>, que faz parte destes Termos.</p>

          <h2 id="recomendacoes">Sommelier Virtual e Calculadora de Eventos</h2>
          <p>As sugestões do Sommelier Virtual e as quantidades da Calculadora de Eventos são indicações para ajudar
            na escolha. Gosto é pessoal e o consumo varia de pessoa para pessoa, por isso os resultados não são garantia.</p>

          <h2 id="propriedade">Propriedade intelectual</h2>
          <p>A marca, o logotipo, os textos, as fotos e o código deste site pertencem à Vinheria Agnello ou aos seus
            parceiros e não podem ser copiados ou usados sem autorização. As marcas dos vinhos pertencem aos respectivos produtores.</p>

          <h2 id="responsabilidade">Consumo responsável</h2>
          <p>Beba com moderação. Não dirija depois de beber. Não ofereça bebidas alcoólicas a menores de 18 anos
            nem a gestantes.</p>

          <h2 id="lei">Lei aplicável</h2>
          <p>Estes Termos seguem a lei brasileira, em especial o Código de Defesa do Consumidor. Eventuais conflitos
            podem ser resolvidos no foro do domicílio do consumidor. Antes disso, fale conosco pelo
            <a href="${ctx}/contato">Contato</a>: quase tudo se resolve numa boa conversa.</p>
        </article>
      </div>
    </div>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/carrinho-gaveta.jsp" />
  <script src="${ctx}/js/pages/documento.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
