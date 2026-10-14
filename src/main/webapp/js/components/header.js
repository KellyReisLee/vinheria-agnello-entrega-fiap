// js/components/header.js
// Comportamento do header: menu "hambúrguer" no mobile e sombra ao fazer scroll.
// É carregado pelo próprio header.jsp, por isso funciona em todas as páginas que o incluem.
(function () {
  const navbar = document.getElementById('site-navbar');
  if (!navbar) return;

  const toggle = navbar.querySelector('.menu-toggle');
  const menu = document.getElementById('nav-principal');
  const backdrop = document.querySelector('.nav-backdrop');

  function abrirMenu() {
    navbar.classList.add('menu-aberto');
    toggle.setAttribute('aria-expanded', 'true');
    toggle.setAttribute('aria-label', 'Fechar menu');
    if (backdrop) backdrop.hidden = false;
    document.body.classList.add('no-scroll');
    // Leva o foco para o 1.º link (quem usa teclado ou leitor de ecrã)
    const primeiro = menu.querySelector('a');
    if (primeiro) {
      void menu.offsetHeight; // força o browser a aplicar o CSS antes de focar
      primeiro.focus({ preventScroll: true });
    }
  }

  function fecharMenu(devolverFoco) {
    navbar.classList.remove('menu-aberto');
    toggle.setAttribute('aria-expanded', 'false');
    toggle.setAttribute('aria-label', 'Abrir menu');
    if (backdrop) backdrop.hidden = true;
    document.body.classList.remove('no-scroll');
    if (devolverFoco) toggle.focus();
  }

  if (toggle && menu) {
    toggle.addEventListener('click', () => {
      navbar.classList.contains('menu-aberto') ? fecharMenu(false) : abrirMenu();
    });

    // Fecha ao clicar num link, no fundo escuro ou ao carregar em Esc
    menu.addEventListener('click', (e) => {
      if (e.target.closest('a')) fecharMenu(false);
    });
    if (backdrop) backdrop.addEventListener('click', () => fecharMenu(false));
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && navbar.classList.contains('menu-aberto')) fecharMenu(true);
    });

    // Se o ecrã crescer (ex.: rodar o tablet) com o menu aberto, fecha-o
    const desktop = window.matchMedia('(min-width: 901px)');
    desktop.addEventListener('change', (e) => {
      if (e.matches) fecharMenu(false);
    });
  }

  // Sombra mais marcada quando a página não está no topo
  const atualizarSombra = () => navbar.classList.toggle('scrolled', window.scrollY > 8);
  atualizarSombra();
  window.addEventListener('scroll', atualizarSombra, { passive: true });
})();
