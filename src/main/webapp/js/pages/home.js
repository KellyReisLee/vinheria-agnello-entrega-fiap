// Renderização dinâmica da vitrine e animações da página principal
document.addEventListener('DOMContentLoaded', () => {
  // 1. Vitrine "Achados da Semana"
  const containerVitrine = document.getElementById('vitrine-achados-container');

  if (containerVitrine) {
    containerVitrine.innerHTML = achadosDaSemana.map((produto) => `
      <article class="${produto.cardClass || 'product-card'}">
        ${produto.tag || ''}
        <div class="product-img-placeholder">
          <img src="${produto.imagem}" alt="${produto.nome}">
        </div>
        <div class="product-info">
          <span class="product-meta">${produto.meta}</span>
          <h3 class="product-name">${produto.nome}</h3>
          <ul class="product-scores">
            ${produto.scores.map(score => `
              <li>
                <img class="score-icon" src="${window.CONTEXT_PATH || ''}/assets/icons/star-check.svg" alt="Estrela de verificação">
                ${score}
              </li>
            `).join('')}
          </ul>
        </div>
        <div class="product-footer">
          <div class="price-box">
            ${produto.precoAntigo ? `<span class="old-price">${produto.precoAntigo}</span>` : ''}
            <span class="product-price">${produto.preco}</span>
          </div>
          <button class="btn-circle-add" aria-label="Selecionar" data-id="${produto.id}">+</button>
        </div>
      </article>
    `).join('');

    containerVitrine.addEventListener('click', (event) => {
      const btn = event.target.closest('.btn-circle-add');
      if (!btn) return;

      const produtoId = btn.getAttribute('data-id');
      const produtoSelecionado = achadosDaSemana.find(p => String(p.id) === String(produtoId));

      if (produtoSelecionado) {
        if (typeof adicionarAoCarrinho === 'function') {
          adicionarAoCarrinho({
            id: produtoSelecionado.id,
            nome: produtoSelecionado.nome,
            preco: produtoSelecionado.preco,
            imagem: produtoSelecionado.imagem
          });
        } else {
          console.error('Função adicionarAoCarrinho não encontrada.');
        }
      }
    });
  }

  // 2. Observer de animação exclusivo da Home (Seção Família e afins)
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible');
        observer.unobserve(entry.target);
      }
    });
  }, { 
	threshold: 0.05, 
	rootMargin: "0px 0px -30px 0px"
	
   });

  document.querySelectorAll('.scroll-animate-left, .scroll-animate-right').forEach(el => {
    observer.observe(el);
  });
});