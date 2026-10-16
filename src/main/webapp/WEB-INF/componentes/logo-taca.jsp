<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Taça do logo desenhada em SVG (vetor): nítida em qualquer tamanho.
     Uso:
       <jsp:include page="/WEB-INF/componentes/logo-taca.jsp">
         <jsp:param name="id" value="topo" />          (nome único na página)
         <jsp:param name="classe" value="logo-img" />  (classe CSS que define o tamanho)
       </jsp:include>
     A cor vem do CSS (currentColor): vinho no header, branca no rodapé.
     A animação está em global.css (.taca-vinho). --%>
<svg class="taca-svg ${param.classe}" viewBox="366 172 292 690" aria-hidden="true" focusable="false">
  <defs>
    <%-- Interior do bojo: tudo o que estiver dentro do grupo com este
         clip-path só aparece dentro desta forma --%>
    <clipPath id="taca-bojo-${param.id}">
      <path d="M447,194 C432,246 408,320 400,384 C395,428 424,463 466,503 C492,528 504,548 509,566 L515,566 C520,548 532,528 558,503 C600,463 629,428 624,384 C616,320 592,246 577,194 Z" />
    </clipPath>

    <%-- Degradê de cima (0) para baixo (1): a superfície um pouco mais clara,
         o fundo com a cor cheia, para dar profundidade --%>
    <linearGradient id="taca-cor-${param.id}" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0" stop-color="currentColor" stop-opacity=".88" />
      <stop offset=".22" stop-color="currentColor" stop-opacity="1" />
    </linearGradient>
  </defs>

  <g clip-path="url(#taca-bojo-${param.id})">
    <%-- O vinho e o brilho da superfície estão no mesmo grupo:
         a animação inclina os dois juntos --%>
    <g class="taca-vinho">
      <path fill="url(#taca-cor-${param.id})"
            d="M352,386 C420,372 470,374 512,384 C556,394 600,398 672,382 V720 H352 Z" />
      <path fill="none" stroke="#FFFFFF" stroke-opacity=".45" stroke-width="5" stroke-linecap="round"
            d="M404,392 C440,380 474,380 512,388 C552,397 588,400 620,390" />
    </g>

    <%-- Reflexo de luz no vidro (fica parado) --%>
    <path fill="none" stroke="#FFFFFF" stroke-opacity=".28" stroke-width="9" stroke-linecap="round"
          d="M418,410 C416,440 432,470 458,492" />
  </g>

  <%-- Contorno da taça (bojo + haste) --%>
  <g fill="none" stroke="currentColor" stroke-width="10" stroke-linecap="round" stroke-linejoin="round">
    <path d="M438,186 C422,240 395,318 387,384 C382,432 413,471 457,514 C484,541 499,562 504,598 L505,806" />
    <path d="M586,186 C602,240 629,318 637,384 C642,432 611,471 567,514 C540,541 525,562 520,598 L519,806" />
  </g>

  <%-- Base --%>
  <path fill="currentColor" d="M503,800 C502,828 486,840 446,848 C496,855 528,855 578,848 C538,840 522,828 521,800 Z" />
</svg>
