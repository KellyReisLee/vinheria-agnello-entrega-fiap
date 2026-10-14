<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Parte comum do <head> das páginas de conta (login, cadastro, senhas).
     Incluído com <jsp:include> logo a seguir ao <title> de cada página. --%>
<meta name="theme-color" content="#4A1525">
<meta name="robots" content="noindex">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400..900;1,400..900&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
<script>window.CONTEXT_PATH = '${pageContext.request.contextPath}';</script>
