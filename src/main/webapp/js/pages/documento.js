// js/pages/documento.js
// Páginas de texto longo (Entregas e Trocas, Privacidade, Termos):
// - no telemóvel o índice começa fechado (no desktop fica sempre aberto);
// - o item do índice da secção que está a ser lida fica destacado.
(function () {
  const indice = document.querySelector('.doc-indice');
  if (!indice) return;

  const links = [...indice.querySelectorAll('a[href^="#"]')];
  const secoes = links
    .map((a) => document.getElementById(decodeURIComponent(a.hash.slice(1))))
    .filter(Boolean);

  // ---------- Índice aberto (desktop) ou fechado (telemóvel) ----------
  const desktop = window.matchMedia('(min-width: 861px)');
  const ajustarIndice = () => { indice.open = desktop.matches; };
  ajustarIndice();
  desktop.addEventListener('change', ajustarIndice);

  // No desktop o índice não pode ser fechado (é só navegação)
  indice.querySelector('summary').addEventListener('click', (e) => {
    if (desktop.matches) e.preventDefault();
  });

  // No telemóvel, depois de escolher uma secção, o índice fecha
  links.forEach((a) => a.addEventListener('click', () => {
    if (!desktop.matches) indice.open = false;
  }));

  // ---------- Destaque da secção atual ----------
  function marcar(id) {
    links.forEach((a) => {
      const ativo = a.hash === '#' + id;
      a.classList.toggle('ativo', ativo);
      ativo ? a.setAttribute('aria-current', 'true') : a.removeAttribute('aria-current');
    });
  }

  if ('IntersectionObserver' in window && secoes.length) {
    const visiveis = new Set();
    const observador = new IntersectionObserver((entradas) => {
      entradas.forEach((e) => (e.isIntersecting ? visiveis.add(e.target) : visiveis.delete(e.target)));
      // A primeira secção visível (de cima para baixo) é a "atual"
      const atual = secoes.find((s) => visiveis.has(s));
      if (atual) marcar(atual.id);
    }, { rootMargin: '-90px 0px -55% 0px' });
    secoes.forEach((s) => observador.observe(s));
  }
})();
