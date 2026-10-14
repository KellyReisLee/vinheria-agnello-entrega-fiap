<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Cliente com sessão: o formulário já vem preenchido.
     PF tem nome/sobrenome e PJ tem razão social (getters diferentes). --%>
<c:set var="tipoCliente" value="${empty clienteLogado ? '' : clienteLogado['class'].simpleName}" />
<c:choose>
  <c:when test="${tipoCliente eq 'ClientePF'}"><c:set var="nomeContato" value="${clienteLogado.nome} ${clienteLogado.sobrenome}" /></c:when>
  <c:when test="${tipoCliente eq 'ClientePJ'}"><c:set var="nomeContato" value="${clienteLogado.razaoSocial}" /></c:when>
</c:choose>
<c:set var="heroAtual" scope="request" value="Contato" />
<c:set var="heroEyebrow" scope="request" value="Fale conosco" />
<c:set var="heroTitulo" scope="request" value="Estamos aqui para ajudar" />
<c:set var="heroTexto" scope="request" value="Dúvidas sobre um pedido, uma indicação para o jantar de sábado ou vinhos para um evento: escreva para nós e respondemos em até 1 dia útil." />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Contato | Vinheria Agnello</title>
  <meta name="description" content="Fale com a Vinheria Agnello por WhatsApp, e-mail ou na nossa loja em São Paulo. Atendimento de segunda a sábado, das 10h às 20h.">
  <jsp:include page="/WEB-INF/componentes/site-head.jsp" />
  <link rel="stylesheet" href="${ctx}/css/institucional.css">
</head>

<body class="inst-body">
  <a class="skip-link" href="#conteudo">Pular para o conteúdo</a>
  <jsp:include page="/WEB-INF/componentes/header.jsp" />

  <main class="inst-main" id="conteudo">
    <jsp:include page="/WEB-INF/componentes/page-hero.jsp" />

    <section class="inst-secao" aria-label="Formulário e canais de contato">
      <div class="inst-container contato-layout">

        <%-- Formulário. Por enquanto, ao enviar, abre o e-mail do cliente com a
             mensagem pronta (contato.js). Quando existir um ContatoController,
             basta trocar esse passo por um envio ao servidor. --%>
        <form class="contato-form" id="form-contato" novalidate
              data-destino="contato@vinheriaagnello.com.br">
          <h2>Envie uma mensagem</h2>
          <p>Os campos marcados como opcionais podem ficar em branco.</p>

          <div class="form-grade">
            <div class="form-campo">
              <label for="contato-nome">Nome</label>
              <input type="text" id="contato-nome" name="nome" autocomplete="name"
                     required maxlength="80" data-nome="o seu nome"
                     value="<c:out value='${nomeContato}' />">
            </div>

            <div class="form-campo">
              <label for="contato-email">E-mail</label>
              <input type="email" id="contato-email" name="email" autocomplete="email"
                     required maxlength="120" data-nome="o seu e-mail" inputmode="email"
                     value="<c:out value='${clienteLogado.email}' />">
            </div>

            <div class="form-campo">
              <label for="contato-assunto">Assunto</label>
              <select id="contato-assunto" name="assunto" required data-nome="o assunto">
                <option value="">Selecione…</option>
                <option>Dúvida sobre um pedido</option>
                <option>Trocas e devoluções</option>
                <option>Indicação de vinho</option>
                <option>Eventos e empresas</option>
                <option>Outro assunto</option>
              </select>
            </div>

            <div class="form-campo">
              <label for="contato-pedido">Nº do pedido <span class="opcional">(opcional)</span></label>
              <input type="text" id="contato-pedido" name="pedido" maxlength="20"
                     placeholder="Ex.: AG-12345" autocomplete="off">
            </div>

            <div class="form-campo form-campo--largo">
              <label for="contato-mensagem">Mensagem</label>
              <textarea id="contato-mensagem" name="mensagem" required minlength="10"
                        maxlength="1000" data-nome="a sua mensagem"
                        aria-describedby="contato-contador"></textarea>
              <span class="form-contador" id="contato-contador" aria-live="polite">0/1000</span>
            </div>

            <div class="form-campo form-campo--largo">
              <label class="form-check">
                <input type="checkbox" id="contato-aceite" name="aceite" required
                       data-nome="o aceite da Política de Privacidade">
                <span>Li e aceito a <a href="${ctx}/privacidade">Política de Privacidade</a>.
                  Usaremos os seus dados apenas para responder a esta mensagem.</span>
              </label>
            </div>
          </div>

          <button type="submit" class="btn-vinho">Enviar mensagem</button>
          <div class="contato-sucesso" id="contato-sucesso" role="status" hidden></div>
        </form>

        <%-- Canais diretos --%>
        <aside class="contato-lateral" aria-label="Outros canais">
          <a class="inst-card" href="https://wa.me/5511999999999" target="_blank" rel="noopener">
            <span class="inst-card-icone" aria-hidden="true">
              <svg viewBox="0 0 24 24"><path d="M21 11.5a8.4 8.4 0 0 1-12.4 7.4L3 21l1.9-5.7A8.5 8.5 0 1 1 21 11.5z"></path></svg>
            </span>
            <h3>WhatsApp</h3>
            <p>A forma mais rápida de falar com a nossa equipe.</p>
            <span class="inst-card-destaque">(11) 99999-9999 <span class="sr-only">(abre numa nova aba)</span></span>
          </a>

          <a class="inst-card" href="mailto:contato@vinheriaagnello.com.br">
            <span class="inst-card-icone" aria-hidden="true">
              <svg viewBox="0 0 24 24"><rect x="2" y="4" width="20" height="16" rx="2"></rect><path d="m22 6-10 7L2 6"></path></svg>
            </span>
            <h3>E-mail</h3>
            <p>Respondemos em até 1 dia útil.</p>
            <span class="inst-card-destaque">contato@vinheriaagnello.com.br</span>
          </a>

          <div class="inst-card">
            <span class="inst-card-icone" aria-hidden="true">
              <svg viewBox="0 0 24 24"><path d="M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 0 1 16 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
            </span>
            <h3>Nossa loja</h3>
            <p>Rua das Videiras, 1978 — São Paulo, SP</p>
            <dl class="contato-horario">
              <div><dt>Segunda a sábado</dt><dd>10h às 20h</dd></div>
              <div><dt>Domingos e feriados</dt><dd>Fechado</dd></div>
            </dl>
          </div>

          <a class="inst-card" href="${ctx}/ajuda">
            <span class="inst-card-icone" aria-hidden="true">
              <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"></circle><path d="M9.1 9a3 3 0 0 1 5.8 1c0 2-3 3-3 3M12 17h.01"></path></svg>
            </span>
            <h3>Central de Ajuda</h3>
            <p>Prazos, pagamentos e trocas: talvez a sua resposta já esteja lá.</p>
            <span class="inst-card-destaque">Ver perguntas frequentes →</span>
          </a>
        </aside>
      </div>
    </section>
  </main>

  <jsp:include page="/WEB-INF/componentes/footer.jsp" />
  <jsp:include page="/WEB-INF/componentes/carrinho-gaveta.jsp" />
  <script src="${ctx}/js/pages/contato.js"></script>
</body>
</html>
