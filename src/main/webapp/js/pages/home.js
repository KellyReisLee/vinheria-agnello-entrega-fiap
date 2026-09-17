// Renderização dinâmica da vitrine e animações da página principal
document.addEventListener('DOMContentLoaded', () => {
  // 1. Vitrine "Achados da Semana" (Híbrida: Servidor JSTL ou Fallback JS)
  const containerVitrine = document.getElementById('vitrine-achados-container');

  if (containerVitrine) {
    // Se o HTML/JSTL do servidor NÃO preencheu a vitrine, usa o fallback do JS antigo
    if (!containerVitrine.innerHTML.trim() && typeof achadosDaSemana !== 'undefined' && achadosDaSemana.length > 0) {
      containerVitrine.innerHTML = achadosDaSemana.map((produto) => `
        <div class="agnello-wine-card">
          ${produto.desconto ? `<span class="agnello-badge-off">${produto.desconto}</span>` : ''}
          <div class="agnello-card-img-container">
            <img src="${window.CONTEXT_PATH || ''}${produto.imagem}" alt="${produto.nome}">
          </div>
          <div class="agnello-card-info">
            <div class="agnello-tags-row">
              <span class="agnello-tag-tipo">${produto.tipo || produto.meta}</span>
              ${produto.pontuacao ? `<span class="agnello-tag-pontos">${produto.pontuacao}</span>` : ''}
            </div>
            <span class="agnello-card-origem">${produto.origem || ''}</span>
            <h3 class="agnello-card-nome">${produto.nome}</h3>
            ${produto.descricao ? `<p class="agnello-card-desc">${produto.descricao}</p>` : ''}
            <div class="agnello-card-footer">
              <div class="agnello-card-precos">
                ${produto.precoAntigo ? `<span class="agnello-preco-antigo">${produto.precoAntigo}</span>` : ''}
                <span class="agnello-preco-atual">${produto.preco}</span>
              </div>
              <button class="agnello-btn-comprar" 
                      data-id="${produto.id}" 
                      data-nome="${produto.nome}" 
                      data-preco="${produto.preco}" 
                      data-imagem="${window.CONTEXT_PATH || ''}${produto.imagem}">
                Selecionar
              </button>
            </div>
          </div>
        </div>
      `).join('');
    }

    // Ouvinte de clique unificado para ambos os casos (Servidor ou JS estático)
    containerVitrine.addEventListener('click', (event) => {
      // Suporta tanto o novo botão "Selecionar" quanto o antigo botão circular caso ainda exista
      const btn = event.target.closest('.agnello-btn-comprar, .btn-circle-add');
      if (!btn) return;

      const produtoId = btn.getAttribute('data-id');
      let produtoSelecionado = null;

      // Tenta buscar pelo array estático se ele existir
      if (typeof achadosDaSemana !== 'undefined') {
        produtoSelecionado = achadosDaSemana.find(p => String(p.id) === String(produtoId));
      }

      if (produtoSelecionado) {
        // Se encontrou no array estático (fallback)
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
      } else {
        // Se veio do banco de dados (JSTL), pega direto dos data-attributes do botão
        const nome = btn.getAttribute('data-nome');
        const preco = btn.getAttribute('data-preco');
        const imagem = btn.getAttribute('data-imagem');

        if (typeof adicionarAoCarrinho === 'function') {
          adicionarAoCarrinho({
            id: produtoId,
            nome: nome,
            preco: preco,
            imagem: imagem
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