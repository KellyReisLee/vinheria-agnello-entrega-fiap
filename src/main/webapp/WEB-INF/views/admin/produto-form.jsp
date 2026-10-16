<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="f" value="${form}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>${f.novo ? 'Novo vinho' : 'Editar vinho'} | Painel Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-body">
  <jsp:include page="/WEB-INF/componentes/admin-menu.jsp" />

  <main class="adm-main">
    <a href="${ctx}/admin/produtos" class="adm-voltar">← Vinhos</a>
    <header class="adm-cabecalho">
      <div>
        <h1>${f.novo ? 'Novo vinho' : 'Editar vinho'}</h1>
        <c:if test="${not f.novo}"><p><c:out value="${f.nome}" /></p></c:if>
      </div>
      <c:if test="${not f.novo and f.ativo}">
        <a href="${ctx}/produto?id=${f.id}" class="adm-link" target="_blank" rel="noopener">Ver na loja ↗</a>
      </c:if>
    </header>

    <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />

    <%-- enctype multipart: necessário para enviar o arquivo da foto --%>
    <form method="post" action="${ctx}/admin/produtos/salvar" enctype="multipart/form-data" class="adm-form adm-grade-detalhe" novalidate>
      <input type="hidden" name="csrf" value="${csrf}">
      <c:if test="${not f.novo}"><input type="hidden" name="id" value="${f.id}"></c:if>
      <input type="hidden" name="imagemAtual" value="<c:out value='${f.imagemAtual}' />">

      <div class="adm-coluna">
        <section class="adm-cartao">
          <h2>Informações</h2>
          <div class="adm-campo">
            <label for="nome">Nome</label>
            <input type="text" id="nome" name="nome" maxlength="150" required value="<c:out value='${f.nome}' />">
          </div>
          <div class="adm-campos-2">
            <div class="adm-campo">
              <label for="tipo">Tipo</label>
              <input type="text" id="tipo" name="tipo" maxlength="50" required list="tipos-existentes"
                     placeholder="Ex.: Tinto Encorpado" value="<c:out value='${f.tipo}' />">
              <datalist id="tipos-existentes">
                <c:forEach var="t" items="${tipos}"><option value="<c:out value='${t}' />"></c:forEach>
              </datalist>
              <span class="adm-dica">Comece por Tinto, Branco, Rosé ou Espumante: o Sommelier Virtual usa esta palavra.</span>
            </div>
            <div class="adm-campo">
              <label for="origem">Origem <span class="adm-opcional">(opcional)</span></label>
              <input type="text" id="origem" name="origem" maxlength="80" placeholder="Ex.: Mendoza, Argentina" value="<c:out value='${f.origem}' />">
            </div>
          </div>
          <div class="adm-campo">
            <label for="descricao">Descrição <span class="adm-opcional">(opcional)</span></label>
            <textarea id="descricao" name="descricao" rows="5" maxlength="2000"><c:out value="${f.descricao}" /></textarea>
          </div>
          <div class="adm-campo">
            <label for="pontuacao">Pontuação da crítica <span class="adm-opcional">(opcional)</span></label>
            <input type="text" id="pontuacao" name="pontuacao" maxlength="80" placeholder="Ex.: 93 Pts - Descorchados" value="<c:out value='${f.pontuacao}' />">
          </div>
        </section>

        <section class="adm-cartao">
          <h2>Preço e estoque</h2>
          <div class="adm-campos-3">
            <div class="adm-campo">
              <label for="preco">Preço (R$)</label>
              <input type="text" id="preco" name="preco" inputmode="decimal" required placeholder="189,90" value="<c:out value='${f.preco}' />">
            </div>
            <div class="adm-campo">
              <label for="precoAntigo">Preço "De" <span class="adm-opcional">(promoção)</span></label>
              <input type="text" id="precoAntigo" name="precoAntigo" inputmode="decimal" placeholder="229,90" value="<c:out value='${f.precoAntigo}' />">
            </div>
            <div class="adm-campo">
              <label for="estoque">Estoque (garrafas)</label>
              <input type="number" id="estoque" name="estoque" min="0" max="100000" required value="<c:out value='${f.estoque}' />">
            </div>
          </div>
          <p class="adm-dica" id="previa-desconto" aria-live="polite">Com o preço "De" preenchido, o selo de desconto (ex.: 17% OFF) é calculado sozinho.</p>
        </section>
      </div>

      <div class="adm-coluna">
        <section class="adm-cartao">
          <h2>Foto</h2>
          <div class="adm-foto">
            <img id="previa-foto" alt="Foto do vinho" width="160" height="210"
                 <c:choose><c:when test="${empty f.imagemAtual}">hidden</c:when><c:otherwise>src="${ctx}${f.imagemUrl}"</c:otherwise></c:choose>>
            <span class="adm-foto-vazia" id="foto-vazia" ${empty f.imagemAtual ? '' : 'hidden'}>Sem foto</span>
          </div>
          <div class="adm-campo">
            <label for="imagem">${empty f.imagemAtual ? 'Escolher foto' : 'Trocar foto'}</label>
            <input type="file" id="imagem" name="imagem" accept="image/png,image/jpeg,image/webp">
            <span class="adm-dica">PNG, JPG ou WebP até 2 MB. Fundo transparente ou claro fica melhor.</span>
          </div>
        </section>

        <section class="adm-cartao">
          <h2>Situação</h2>
          <label class="adm-interruptor">
            <input type="checkbox" name="ativo" ${f.ativo ? 'checked' : ''}>
            <span>À venda na loja</span>
          </label>
          <p class="adm-dica">Desmarque para esconder o vinho da loja sem apagá-lo.</p>
        </section>

        <div class="adm-salvar">
          <button type="submit" class="adm-btn adm-btn--primario adm-btn--largo">${f.novo ? 'Cadastrar vinho' : 'Salvar alterações'}</button>
          <a href="${ctx}/admin/produtos" class="adm-btn adm-btn--discreto adm-btn--largo">Cancelar</a>
        </div>
      </div>
    </form>
  </main>
  <script src="${ctx}/js/pages/admin.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
