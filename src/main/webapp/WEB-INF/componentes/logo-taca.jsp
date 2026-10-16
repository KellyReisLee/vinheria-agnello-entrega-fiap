<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Taça do logo desenhada em SVG (vetor).
     Uso:
       <jsp:include page="/WEB-INF/componentes/logo-taca.jsp">
         <jsp:param name="id" value="topo" />          (nome único na página)
         <jsp:param name="classe" value="logo-img" />  (classe CSS que define o tamanho)
       </jsp:include>
     A cor vem do CSS (currentColor): vinho no header, branca no rodapé.

     Desenhada numa grelha pequena (24 × 48), como um ícone: a ~40 px de altura
     cada unidade vale quase 1 px de tela, por isso as linhas de 1,6 unidades
     ficam com ~1,3 px e saem nítidas. (O desenho anterior usava uma grelha de
     690 unidades e as linhas acabavam com meio pixel: ficavam borradas.) --%>
<svg class="taca-svg ${param.classe}" viewBox="0 0 24 48" aria-hidden="true" focusable="false">
  <defs>
    <%-- Interior do bojo: o vinho só aparece dentro desta forma --%>
    <clipPath id="taca-bojo-${param.id}">
      <path d="M5.2,2.8 C4.4,7 3.8,10.8 3.8,14.6 C3.8,19.8 7.4,23.7 12,24.2 C16.6,23.7 20.2,19.8 20.2,14.6 C20.2,10.8 19.6,7 18.8,2.8 Z" />
    </clipPath>
  </defs>

  <%-- Vinho (um terço da taça, como se serve de verdade) e o brilho dele --%>
  <g clip-path="url(#taca-bojo-${param.id})">
    <rect x="0" y="12.6" width="24" height="14" fill="currentColor" />
    <path d="M6.6,15.4 C6.8,18.2 8.2,20.4 10.2,21.6" fill="none" stroke="#FFFFFF"
          stroke-opacity=".38" stroke-width="1.1" stroke-linecap="round" />
  </g>

  <%-- Contorno do bojo --%>
  <path d="M5.2,2 C4.3,6.6 3,10.6 3,14.6 C3,20.3 7,24.6 12,25 C17,24.6 21,20.3 21,14.6 C21,10.6 19.7,6.6 18.8,2 Z"
        fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round" />

  <%-- Haste --%>
  <path d="M12,25 V41.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />

  <%-- Base --%>
  <path d="M12,40.6 C12,42.6 10.2,43.5 6.8,44 C5.9,44.15 5.9,45.6 6.9,45.6 H17.1 C18.1,45.6 18.1,44.15 17.2,44 C13.8,43.5 12,42.6 12,40.6 Z"
        fill="currentColor" />
</svg>
