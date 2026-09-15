<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Página não encontrada | Vinheria Agnello</title>

  <!-- Favicon -->
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo-bg-white.png">

  <!-- Folhas de Estilo com Context Path -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/404.css">
</head>

<body>

  <div class="error-wrapper">
    <div class="error-badge">Vinheria Agnello • Adega Digital</div>

    <div class="error-icon-box">
      <svg class="error-icon" viewBox="0 0 24 24">
        <path
          d="M7 3h10v2H7V3m2 4h6v2.5a3 3 0 0 1-2 2.82V16h3v2H8v-2h3v-3.68A3 3 0 0 1 9 9.5V7m1 2v.5a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1V9h-4Z" />
      </svg>
    </div>

    <h1 class="error-code">404</h1>
    <h2 class="error-title">Garrafa não encontrada na adega!</h2>
    <p class="error-message">Parece que o rótulo que você está procurando foi movido, consumido ou nunca existiu. Vamos
      retornar à nossa página principal para continuar a degustação?</p>

    <a href="${pageContext.request.contextPath}/home" class="btn-home">Voltar à Adega</a>
  </div>

</body>

</html>