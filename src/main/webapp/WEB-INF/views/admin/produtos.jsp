<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="pt_BR" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Endereço desta lista (com filtros) para voltar depois de ativar/desativar ou mudar o estoque --%>
<c:url var="aqui" value="/admin/produtos"><c:if test="${mostrar ne 'todos'}"><c:param name="mostrar" value="${mostrar}" /></c:if><c:if test="${not empty busca}"><c:param name="busca" value="${busca}" /></c:if></c:url>
<c:set var="voltar" value="${fn:substringAfter(aqui, ctx)}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <title>Vinhos | Painel Agnello</title>
  <jsp:include page="/WEB-INF/componentes/admin-head.jsp" />
</head>
<body class="adm-body">
  <jsp:include page="/WEB-INF/componentes/admin-menu.jsp" />

  <main class="adm-main">
    <header class="adm-cabecalho">
      <div>
        <h1>Vinhos</h1>
        <p>Cadastre, edite e controle o estoque da adega.</p>
      </div>
      <a href="${ctx}/admin/produtos/novo" class="adm-btn adm-btn--primario">+ Novo vinho</a>
    </header>

    <jsp:include page="/WEB-INF/componentes/admin-flash.jsp" />

    <div class="adm-barra">
      <nav class="adm-filtros" aria-label="Mostrar">
        <c:url var="uTodos" value="/admin/produtos"><c:if test="${not empty busca}"><c:param name="busca" value="${busca}" /></c:if></c:url>
        <c:url var="uAtivos" value="/admin/produtos"><c:param name="mostrar" value="ativos" /><c:if test="${not empty busca}"><c:param name="busca" value="${busca}" /></c:if></c:url>
        <c:url var="uInativos" value="/admin/produtos"><c:param name="mostrar" value="inativos" /><c:if test="${not empty busca}"><c:param name="busca" value="${busca}" /></c:if></c:url>
        <a href="${uTodos}" class="${mostrar eq 'todos' ? 'ativo' : ''}">Todos</a>
        <a href="${uAtivos}" class="${mostrar eq 'ativos' ? 'ativo' : ''}">À venda</a>
        <a href="${uInativos}" class="${mostrar eq 'inativos' ? 'ativo' : ''}">Desativados</a>
      </nav>
      <form method="get" action="${ctx}/admin/produtos" class="adm-busca" role="search">
        <c:if test="${mostrar ne 'todos'}"><input type="hidden" name="mostrar" value="${mostrar}"></c:if>
        <label for="busca" class="sr-only">Buscar vinho</label>
        <input type="search" id="busca" name="busca" placeholder="Nome, tipo ou origem" value="<c:out value='${busca}' />">
        <button type="submit" class="adm-btn">Buscar</button>
      </form>
    </div>

    <section class="adm-cartao">
      <c:choose>
        <c:when test="${empty produtos}">
          <p class="adm-vazio">Nenhum vinho encontrado.</p>
        </c:when>
        <c:otherwise>
          <div class="adm-tabela-rolagem">
            <table class="adm-tabela adm-tabela--produtos">
              <thead>
                <tr><th scope="col"><span class="sr-only">Foto</span></th><th scope="col">Vinho</th><th scope="col" class="num">Preço</th>
                  <th scope="col">Estoque</th><th scope="col">Situação</th><th scope="col"><span class="sr-only">Ações</span></th></tr>
              </thead>
              <tbody>
                <c:forEach var="p" items="${produtos}">
                  <tr class="${p.ativo ? '' : 'adm-linha-inativa'}">
                    <td><img src="${ctx}${p.imagemUrl}" alt="" width="36" height="48" loading="lazy" class="adm-miniatura" onerror="this.style.visibility='hidden'"></td>
                    <td>
                      <a href="${ctx}/admin/produtos/editar?id=${p.id}" class="adm-link-forte"><c:out value="${p.nome}" /></a><br>
                      <span class="adm-mudo"><c:out value="${p.tipo}" /><c:if test="${not empty p.origem}"> · <c:out value="${p.origem}" /></c:if></span>
                    </td>
                    <td class="num">
                      <fmt:formatNumber value="${p.preco}" type="currency" currencySymbol="R$" />
                      <c:if test="${not empty p.desconto}"><br><span class="adm-selo-desconto"><c:out value="${p.desconto}" /></span></c:if>
                    </td>
                    <td>
                      <%-- Ajuste rápido de estoque sem abrir o formulário --%>
                      <form method="post" action="${ctx}/admin/produtos/estoque" class="adm-estoque ${p.estoque lt limiteEstoqueBaixo ? 'adm-estoque--baixo' : ''}">
                        <input type="hidden" name="csrf" value="${csrf}">
                        <input type="hidden" name="id" value="${p.id}">
                        <input type="hidden" name="voltar" value="${voltar}">
                        <label for="estoque-${p.id}" class="sr-only">Estoque de <c:out value="${p.nome}" /></label>
                        <input type="number" id="estoque-${p.id}" name="estoque" value="${p.estoque}" min="0" max="100000" inputmode="numeric">
                        <button type="submit" class="adm-btn adm-btn--pequeno">Salvar</button>
                      </form>
                      <%-- Clientes que pediram "Avise-me quando chegar" (recebem e-mail quando o estoque passar de 0) --%>
                      <c:set var="aEspera" value="${avisosPendentes[p.id]}" />
                      <c:if test="${aEspera gt 0}">
                        <span class="adm-aviso-espera" title="Recebem um e-mail automático quando o estoque voltar">${aEspera} ${aEspera eq 1 ? 'cliente à espera' : 'clientes à espera'}</span>
                      </c:if>
                    </td>
                    <td><span class="adm-situacao ${p.ativo ? 'adm-situacao--ativo' : ''}">${p.ativo ? 'À venda' : 'Desativado'}</span></td>
                    <td class="adm-acoes">
                      <a href="${ctx}/admin/produtos/editar?id=${p.id}" class="adm-btn adm-btn--pequeno">Editar</a>
                      <form method="post" action="${ctx}/admin/produtos/ativo"
                            ${p.ativo ? 'data-confirmar="Desativar este vinho? Ele deixa de aparecer na loja."' : ''}>
                        <input type="hidden" name="csrf" value="${csrf}">
                        <input type="hidden" name="id" value="${p.id}">
                        <input type="hidden" name="ativo" value="${not p.ativo}">
                        <input type="hidden" name="voltar" value="${voltar}">
                        <button type="submit" class="adm-btn adm-btn--pequeno ${p.ativo ? 'adm-btn--discreto' : ''}">${p.ativo ? 'Desativar' : 'Reativar'}</button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </main>
  <script src="${ctx}/js/pages/admin.js?v=${initParam.versaoArquivos}"></script>
</body>
</html>
