<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Menu lateral do painel. A seção atual vem de ${secaoAdmin} (definida no Controller). --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<aside class="adm-menu" aria-label="Menu do painel">
  <a href="${ctx}/admin" class="adm-marca">
    <jsp:include page="/WEB-INF/componentes/logo-taca.jsp"><jsp:param name="id" value="admin" /><jsp:param name="classe" value="adm-marca-taca" /></jsp:include>
    <span><strong>Agnello</strong><small>Painel da loja</small></span>
  </a>

  <nav class="adm-nav">
    <a href="${ctx}/admin" class="${secaoAdmin eq 'painel' ? 'ativo' : ''}" ${secaoAdmin eq 'painel' ? 'aria-current="page"' : ''}>
      <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="3" width="7" height="9" rx="1.5"/><rect x="14" y="3" width="7" height="5" rx="1.5"/><rect x="14" y="12" width="7" height="9" rx="1.5"/><rect x="3" y="16" width="7" height="5" rx="1.5"/></svg>
      Visão geral</a>
    <a href="${ctx}/admin/pedidos" class="${secaoAdmin eq 'pedidos' ? 'ativo' : ''}" ${secaoAdmin eq 'pedidos' ? 'aria-current="page"' : ''}>
      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M21 8 12 3 3 8v8l9 5 9-5z"/><path d="M3 8l9 5 9-5M12 13v8"/></svg>
      Pedidos</a>
    <a href="${ctx}/admin/produtos" class="${secaoAdmin eq 'produtos' ? 'ativo' : ''}" ${secaoAdmin eq 'produtos' ? 'aria-current="page"' : ''}>
      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 2h8l-1 7a3 3 0 0 1-6 0z"/><path d="M12 12v8M8 22h8"/></svg>
      Vinhos</a>
    <a href="${ctx}/admin/mensagens" class="${secaoAdmin eq 'mensagens' ? 'ativo' : ''}" ${secaoAdmin eq 'mensagens' ? 'aria-current="page"' : ''}>
      <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 6-10 7L2 6"/></svg>
      Mensagens</a>
  </nav>

  <div class="adm-menu-rodape">
    <a href="${ctx}/home" class="adm-ver-loja" target="_blank" rel="noopener">Ver a loja <span aria-hidden="true">↗</span></a>
    <div class="adm-usuario">
      <span class="adm-avatar" aria-hidden="true"><c:out value="${fn:substring(adminLogado.nome, 0, 1)}" /></span>
      <span class="adm-usuario-nome"><c:out value="${adminLogado.nome}" /></span>
    </div>
    <form method="post" action="${ctx}/admin/sair">
      <input type="hidden" name="csrf" value="${csrf}">
      <button type="submit" class="adm-sair">Sair</button>
    </form>
  </div>
</aside>
