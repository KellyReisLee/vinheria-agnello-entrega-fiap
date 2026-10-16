<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Parte comum do <head> das páginas da loja (institucionais e produto).
     Cada página escreve o próprio <title>, a descrição e o CSS dela;
     depois inclui este ficheiro com <jsp:include>. --%>
<meta name="theme-color" content="#4A1525">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css?v=${initParam.versaoArquivos}">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css?v=${initParam.versaoArquivos}">
<script>window.CONTEXT_PATH = '${pageContext.request.contextPath}';</script>
