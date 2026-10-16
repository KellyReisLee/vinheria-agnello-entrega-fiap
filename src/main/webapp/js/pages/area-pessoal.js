// js/pages/area-pessoal.js
// Área pessoal: navegação entre as secções (#visao-geral, #pedidos, #dados).
// A lista de pedidos vem pronta do servidor (AreaPessoalController + JSP).
document.addEventListener('DOMContentLoaded', () => {
  const secoes = [...document.querySelectorAll('.conta-secao')];
  const itensMenu = [...document.querySelectorAll('.conta-nav-item[data-secao]')];

  // Antes os pedidos ficavam guardados só no navegador; agora estão no banco.
  try { localStorage.removeItem('agnello_pedidos'); } catch (e) { /* sem localStorage */ }

  function mostrarSecao(id, focar) {
    const alvo = secoes.find((s) => s.id === id) || secoes[0];
    secoes.forEach((s) => { s.hidden = s !== alvo; });
    itensMenu.forEach((a) => {
      const ativo = a.dataset.secao === alvo.id;
      a.classList.toggle('ativo', ativo);
      if (ativo) a.setAttribute('aria-current', 'page');
      else a.removeAttribute('aria-current');
    });
    if (focar) alvo.focus({ preventScroll: true });
  }

  document.querySelectorAll('[data-secao]').forEach((link) => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      history.replaceState(null, '', `#${link.dataset.secao}`);
      mostrarSecao(link.dataset.secao, true);
      if (window.innerWidth < 900) document.querySelector('.conta-conteudo').scrollIntoView({ behavior: 'smooth' });
    });
  });

  window.addEventListener('hashchange', () => mostrarSecao(location.hash.slice(1), true));
  mostrarSecao(location.hash.slice(1) || 'visao-geral', false);
});
