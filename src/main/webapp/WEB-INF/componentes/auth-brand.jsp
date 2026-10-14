<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Painel da marca (lado direito) das páginas de conta.
     Cada página define antes do include (com <c:set ... scope="request">):
     brandBadge, brandTitulo, brandTexto, brandB1, brandB2, brandB3.
     (Usamos variáveis de request em vez de <jsp:param> porque os parâmetros
      perdem os acentos quando o servidor não está em UTF-8.) --%>
<aside class="auth-brand-side" aria-label="Clube Agnello">
  <span class="auth-brand-badge">${brandBadge}</span>
  <h2>${brandTitulo}</h2>
  <p>${brandTexto}</p>
  <ul class="auth-benefits">
    <li><svg viewBox="0 0 24 24" aria-hidden="true"><polyline points="20 6 9 17 4 12"></polyline></svg>${brandB1}</li>
    <li><svg viewBox="0 0 24 24" aria-hidden="true"><polyline points="20 6 9 17 4 12"></polyline></svg>${brandB2}</li>
    <li><svg viewBox="0 0 24 24" aria-hidden="true"><polyline points="20 6 9 17 4 12"></polyline></svg>${brandB3}</li>
  </ul>
</aside>
