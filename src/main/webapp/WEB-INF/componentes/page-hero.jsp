<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Faixa de título das páginas institucionais (fundo vinho).
     A página define antes, com scope="request":
       heroEyebrow (texto pequeno dourado), heroTitulo, heroTexto (opcional)
       heroAtual   (nome da página na trilha "Início › …") --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<header class="inst-hero">
  <div class="inst-hero-inner">
    <nav class="inst-trilha" aria-label="Você está em">
      <ol>
        <li><a href="${ctx}/home">Início</a></li>
        <li aria-current="page"><c:out value="${heroAtual}" /></li>
      </ol>
    </nav>
    <span class="inst-eyebrow"><c:out value="${heroEyebrow}" /></span>
    <h1 class="inst-hero-titulo"><c:out value="${heroTitulo}" /></h1>
    <c:if test="${not empty heroTexto}">
      <p class="inst-hero-texto"><c:out value="${heroTexto}" /></p>
    </c:if>
  </div>
</header>
