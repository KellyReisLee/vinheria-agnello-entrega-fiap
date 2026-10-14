// js/pages/produto.js
// Página do produto: seletor de quantidade, botão "Adicionar ao carrinho"
// e barra de compra fixa no telemóvel. Os dados do vinho vêm dos data-* do
// <article id="pdp-produto"> (escritos pelo produto.jsp).
(function () {
  const produto = document.getElementById('pdp-produto');
  if (!produto) return;

  const campoQtd = document.getElementById('pdp-quantidade');
  const botao = document.getElementById('pdp-adicionar');
  const barra = document.getElementById('pdp-barra');
  const botaoBarra = document.getElementById('pdp-adicionar-barra');
  if (!campoQtd || !botao) return; // vinho esgotado: não há compra

  const minimo = Number(campoQtd.min) || 1;
  const maximo = Number(campoQtd.max) || 12;
  const botoesQtd = [...produto.querySelectorAll('.pdp-qtd-btn')];

  // ---------- Quantidade ----------
  function definirQtd(valor) {
    const n = Math.min(maximo, Math.max(minimo, Math.round(Number(valor)) || minimo));
    campoQtd.value = n;
    botoesQtd.forEach((b) => {
      b.disabled = Number(b.dataset.qtd) < 0 ? n <= minimo : n >= maximo;
    });
    return n;
  }

  botoesQtd.forEach((b) => b.addEventListener('click', () => {
    definirQtd(Number(campoQtd.value) + Number(b.dataset.qtd));
  }));
  campoQtd.addEventListener('change', () => definirQtd(campoQtd.value));
  definirQtd(campoQtd.value);

  // ---------- Adicionar ao carrinho ----------
  function adicionar() {
    if (typeof window.adicionarAoCarrinho !== 'function') return;
    const qtd = definirQtd(campoQtd.value);
    const vinho = {
      id: produto.dataset.id,
      nome: produto.dataset.nome,
      preco: produto.dataset.preco,
      origem: produto.dataset.origem,
      imagem: produto.dataset.imagem
    };
    // O carrinho.js soma 1 unidade por chamada (e abre a gaveta)
    for (let i = 0; i < qtd; i++) window.adicionarAoCarrinho(vinho);

    // Confirmação rápida no próprio botão
    const textoOriginal = botao.textContent;
    botao.classList.add('adicionado');
    botao.textContent = qtd > 1 ? `${qtd} garrafas adicionadas ✓` : 'Adicionado ✓';
    setTimeout(() => {
      botao.classList.remove('adicionado');
      botao.textContent = textoOriginal;
    }, 2200);
  }

  botao.addEventListener('click', adicionar);
  if (botaoBarra) botaoBarra.addEventListener('click', adicionar);

  // ---------- Barra fixa (só aparece no telemóvel, ver produto.css) ----------
  if (barra && 'IntersectionObserver' in window) {
    const observador = new IntersectionObserver(([entrada]) => {
      // Mostra a barra quando o botão principal já passou (está acima do ecrã)
      const passou = !entrada.isIntersecting && entrada.boundingClientRect.top < 0;
      barra.classList.toggle('visivel', passou);
      barra.setAttribute('aria-hidden', String(!passou));
      if (botaoBarra) botaoBarra.tabIndex = passou ? 0 : -1;
    });
    observador.observe(botao);
  }
})();
