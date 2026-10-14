// js/components/carrinho.js
// Gaveta do carrinho (partilhada por várias páginas). Os itens ficam guardados no
// localStorage do browser. Toda a aparência está em css/sections/_carrinho.css.
document.addEventListener('DOMContentLoaded', () => {
  const CHAVE = 'agnello_carrinho';

  let carrinho = [];
  try {
    carrinho = JSON.parse(localStorage.getItem(CHAVE)) || [];
  } catch (e) {
    carrinho = []; // localStorage indisponível ou com dados corrompidos
  }

  function salvarCarrinho() {
    try {
      localStorage.setItem(CHAVE, JSON.stringify(carrinho));
    } catch (e) { /* modo privado: o carrinho funciona só nesta página */ }
  }

  // Evita que um nome de produto com "<" ou aspas parta o HTML
  function escapar(texto) {
    return String(texto ?? '').replace(/[&<>"']/g, (c) => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
  }

  // "R$ 1.189,90" -> 1189.9
  function precoParaNumero(preco) {
    return parseFloat(String(preco).replace(/[^\d,]/g, '').replace(',', '.')) || 0;
  }

  function formatarPreco(valor) {
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  // Elementos
  const drawer = document.getElementById('agnello-cart-drawer');
  const overlay = document.getElementById('agnello-cart-overlay');
  const closeBtn = document.getElementById('agnello-cart-close');
  const cartBody = document.getElementById('agnello-cart-body');
  const cartTitle = document.getElementById('agnello-cart-title');

  if (drawer) {
    drawer.setAttribute('role', 'dialog');
    drawer.setAttribute('aria-modal', 'true');
    drawer.setAttribute('aria-labelledby', 'agnello-cart-title');
  }
  if (closeBtn) closeBtn.setAttribute('aria-label', 'Fechar carrinho');

  let ultimoFoco = null;

  function abrirCarrinho() {
    if (!drawer) return;
    ultimoFoco = document.activeElement;
    drawer.classList.add('ativo');
    if (overlay) overlay.classList.add('ativo');
    document.body.classList.add('no-scroll');
    renderizarConteudoCarrinho();
    if (closeBtn) closeBtn.focus();
  }

  function fecharCarrinho() {
    if (!drawer) return;
    drawer.classList.remove('ativo');
    if (overlay) overlay.classList.remove('ativo');
    document.body.classList.remove('no-scroll');
    if (ultimoFoco && typeof ultimoFoco.focus === 'function') ultimoFoco.focus();
  }

  if (closeBtn) closeBtn.addEventListener('click', fecharCarrinho);
  if (overlay) overlay.addEventListener('click', fecharCarrinho);
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && drawer && drawer.classList.contains('ativo')) fecharCarrinho();
  });

  // Um único "ouvinte" para todos os cliques relacionados com o carrinho
  document.addEventListener('click', (event) => {
    // Abrir pelo ícone do header
    if (event.target.closest('.cart-btn')) {
      abrirCarrinho();
      return;
    }

    // Botões "Selecionar" / "Comprar" dos cards (Home, Catálogo, Sugestões)
    const btnComprar = event.target.closest('.agnello-btn-comprar');
    if (btnComprar && !btnComprar.hasAttribute('onclick')) {
      event.preventDefault();
      const vinho = {
        id: btnComprar.dataset.id,
        nome: btnComprar.dataset.nome,
        preco: btnComprar.dataset.preco,
        origem: btnComprar.dataset.origem,
        imagem: btnComprar.dataset.imagem
      };
      if (vinho.nome && vinho.preco) window.adicionarAoCarrinho(vinho);
      return;
    }

    // Botões dentro da gaveta (+, -, remover, finalizar)
    const acao = event.target.closest('[data-cart-acao]');
    if (!acao) return;
    const index = Number(acao.dataset.index);

    switch (acao.dataset.cartAcao) {
      case 'mais': window.alterarQtd(index, 1); break;
      case 'menos': window.alterarQtd(index, -1); break;
      case 'remover': window.removerDoCarrinho(index); break;
      case 'checkout': {
        const ctx = window.CONTEXT_PATH ?? '';
        window.location.href = ctx + '/checkout';
        break;
      }
    }
  });

  function atualizarBadges(totalItens) {
    document.querySelectorAll('.cart-badge').forEach((badge) => {
      badge.textContent = totalItens > 99 ? '99+' : totalItens;
      badge.hidden = totalItens === 0;
      badge.style.removeProperty('display');
    });
    document.querySelectorAll('.cart-btn').forEach((btn) => {
      btn.setAttribute('aria-label', totalItens > 0
        ? `Abrir carrinho (${totalItens} ${totalItens === 1 ? 'item' : 'itens'})`
        : 'Abrir carrinho');
    });
  }

  function renderizarConteudoCarrinho() {
    const totalItens = carrinho.reduce((acc, item) => acc + (item.quantidade || 1), 0);

    if (cartTitle) cartTitle.textContent = `Carrinho (${totalItens})`;
    atualizarBadges(totalItens);

    if (!cartBody) return;

    if (carrinho.length === 0) {
      cartBody.innerHTML = `
        <div class="agnello-cart-empty">
          <img class="agnello-cart-empty-icon" src="${window.CONTEXT_PATH ?? ''}/assets/icons/wine.svg" alt="" width="48" height="48">
          <h3>O seu carrinho está vazio</h3>
          <p>Explore a nossa adega e escolha os seus rótulos.</p>
        </div>
      `;
      return;
    }

    const totalGeral = carrinho.reduce(
      (acc, item) => acc + precoParaNumero(item.preco) * (item.quantidade || 1), 0);

    const itensHTML = carrinho.map((item, index) => {
      const qtd = item.quantidade || 1;
      const subtotal = precoParaNumero(item.preco) * qtd;
      const nome = escapar(item.nome);
      return `
        <li class="agnello-cart-item">
          <img src="${escapar(item.imagem)}" alt="" width="56" height="90" onerror="this.style.visibility='hidden'">
          <div class="agnello-cart-item-details">
            <p class="agnello-cart-item-title">${nome}</p>
            <p class="agnello-cart-item-origin">${escapar(item.origem || item.subtitulo || '')}</p>
            <div class="agnello-cart-item-row">
              <div class="agnello-qty" role="group" aria-label="Quantidade de ${nome}">
                <button type="button" data-cart-acao="menos" data-index="${index}" aria-label="Diminuir quantidade">−</button>
                <span aria-live="polite">${qtd}</span>
                <button type="button" data-cart-acao="mais" data-index="${index}" aria-label="Aumentar quantidade">+</button>
              </div>
              <span class="agnello-cart-item-price">${formatarPreco(subtotal)}</span>
            </div>
          </div>
          <button type="button" class="agnello-cart-remove" data-cart-acao="remover" data-index="${index}" aria-label="Remover ${nome}">&times;</button>
        </li>
      `;
    }).join('');

    cartBody.innerHTML = `
      <ul class="agnello-cart-list">${itensHTML}</ul>
      <div class="agnello-cart-footer-box">
        <div class="agnello-cart-total-row">
          <span>Total</span>
          <span>${formatarPreco(totalGeral)}</span>
        </div>
        <button type="button" id="btn-ir-checkout" class="agnello-btn-finalizar" data-cart-acao="checkout">Finalizar pedido</button>
      </div>
    `;
  }

  // Funções globais (usadas por outras páginas, ex.: catálogo e sugestões)
  window.adicionarAoCarrinho = function (vinhoObj) {
    const i = carrinho.findIndex((item) =>
      (vinhoObj.id && item.id === vinhoObj.id) || item.nome === vinhoObj.nome);
    if (i !== -1) {
      carrinho[i].quantidade = (carrinho[i].quantidade || 1) + 1;
    } else {
      carrinho.push({ ...vinhoObj, quantidade: 1 });
    }
    salvarCarrinho();
    abrirCarrinho();
  };

  window.alterarQtd = function (index, delta) {
    const item = carrinho[index];
    if (!item) return;
    item.quantidade = (item.quantidade || 1) + delta;
    if (item.quantidade <= 0) carrinho.splice(index, 1);
    salvarCarrinho();
    renderizarConteudoCarrinho();
  };

  window.removerDoCarrinho = function (index) {
    carrinho.splice(index, 1);
    salvarCarrinho();
    renderizarConteudoCarrinho();
  };

  renderizarConteudoCarrinho();
});
