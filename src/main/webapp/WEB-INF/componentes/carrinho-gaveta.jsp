<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Gaveta lateral do carrinho. O conteúdo é desenhado pelo js/components/carrinho.js
     (os itens ficam no localStorage, chave "agnello_carrinho"). --%>
<div id="agnello-cart-overlay" class="agnello-cart-overlay"></div>
<div id="agnello-cart-drawer" class="agnello-cart-drawer">
  <div class="agnello-cart-header">
    <button type="button" id="agnello-cart-close" class="agnello-cart-back-btn" aria-label="Fechar carrinho">←</button>
    <h2 id="agnello-cart-title">Carrinho (0)</h2>
  </div>
  <div id="agnello-cart-body" class="agnello-cart-body"></div>
</div>
<script src="${pageContext.request.contextPath}/js/components/carrinho.js"></script>
<script src="${pageContext.request.contextPath}/js/components/chat.js"></script>
