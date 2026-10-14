// js/pages/area-pessoal.js
// Área pessoal: navegação entre secções (#visao-geral, #pedidos, #dados) e
// lista de pedidos guardados pelo checkout (localStorage 'agnello_pedidos').
document.addEventListener('DOMContentLoaded', () => {
  const ctx = window.CONTEXT_PATH || '';
  const main = document.querySelector('.conta-main');
  const emailCliente = ((main && main.dataset.email) || '').toLowerCase();
  const secoes = [...document.querySelectorAll('.conta-secao')];
  const itensMenu = [...document.querySelectorAll('.conta-nav-item[data-secao]')];

  const brl = (n) => Number(n || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  const escapar = (t) => String(t ?? '').replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));
  const data = (iso) => new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' });

  // ---------- Secções ----------
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

  // ---------- Pedidos ----------
  let pedidos = [];
  try {
    pedidos = (JSON.parse(localStorage.getItem('agnello_pedidos')) || [])
      .filter((p) => !p.email || p.email.toLowerCase() === emailCliente);
  } catch (e) {
    pedidos = [];
  }

  const lista = document.getElementById('lista-pedidos');
  if (lista) {
    if (!pedidos.length) {
      lista.innerHTML = `
        <div class="conta-vazio">
          <img src="${ctx}/assets/icons/package.svg" alt="" width="40" height="40">
          <h2>Ainda não tem pedidos</h2>
          <p>Quando fizer a sua primeira compra, ela aparece aqui.</p>
          <a href="${ctx}/catalogo" class="conta-btn-primario">Explorar a adega</a>
        </div>`;
    } else {
      lista.innerHTML = pedidos.map((p) => `
        <article class="conta-pedido">
          <header>
            <div>
              <h2>Pedido ${escapar(p.numero)}</h2>
              <span>${data(p.data)} · ${escapar(p.pagamento)}</span>
            </div>
            <span class="conta-status ${p.status === 'Pagamento aprovado' ? 'conta-status--ok' : ''}">${escapar(p.status)}</span>
          </header>
          <ul>
            ${p.itens.map((i) => `<li><span>${i.quantidade}× ${escapar(i.nome)}</span><span>${brl(i.preco * i.quantidade)}</span></li>`).join('')}
          </ul>
          <footer>
            <span class="conta-pedido-endereco">${escapar(p.endereco)}</span>
            <strong>${brl(p.total)}</strong>
          </footer>
        </article>`).join('');
    }
  }

  // ---------- Resumo na visão geral ----------
  const resumo = document.getElementById('resumo-conta');
  const ultimo = document.getElementById('ultimo-pedido');
  if (resumo && pedidos.length) {
    resumo.textContent = `Você tem ${pedidos.length} ${pedidos.length === 1 ? 'pedido' : 'pedidos'} na sua conta.`;
  }
  if (ultimo && pedidos.length) {
    const p = pedidos[0];
    ultimo.innerHTML = `
      <span class="conta-ultimo-label">Último pedido · ${escapar(p.numero)}</span>
      <strong>${escapar(p.status)}</strong>
      <a href="#pedidos" data-secao-link>Ver detalhes →</a>`;
    ultimo.hidden = false;
    ultimo.querySelector('[data-secao-link]').addEventListener('click', (e) => {
      e.preventDefault();
      history.replaceState(null, '', '#pedidos');
      mostrarSecao('pedidos', true);
    });
  }
});
