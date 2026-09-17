// Renderização dinâmica da vitrine e animações da página principal
document.addEventListener('DOMContentLoaded', () => {
  // 1. Vitrine "Achados da Semana" (Híbrida: Servidor JSTL ou Fallback JS)
  const containerVitrine = document.getElementById('vitrine-achados-container');

  if (containerVitrine) {
    // Se o HTML/JSTL do servidor NÃO preencheu a vitrine, usa o fallback do JS antigo
    if (!containerVitrine.innerHTML.trim() && typeof achadosDaSemana !== 'undefined' && achadosDaSemana.length > 0) {
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
            <button class="agnello-btn-comprar" aria-label="Selecionar" 
                    data-id="${produto.id}" 
                    data-nome="${produto.nome}" 
                    data-preco="${produto.precoNumerico || produto.preco}" 
                    data-origem="${produto.origem || produto.meta || ''}"
                    data-imagem="${produto.imagem}">Selecionar</button>
          </div>
        </article>
      `).join('');
    }

    // Ouvinte de clique unificado para ambos os casos (Servidor ou JS estático)
    containerVitrine.addEventListener('click', (event) => {
      const btn = event.target.closest('.agnello-btn-comprar');
      if (!btn) return;

      const produtoId = btn.getAttribute('data-id');
      const nome = btn.getAttribute('data-nome');
      const precoStr = btn.getAttribute('data-preco');
      const origem = btn.getAttribute('data-origem');
      const imagem = btn.getAttribute('data-imagem');

      // Tenta extrair o valor numérico com segurança para formatar certinho
      let precoNumerico = parseFloat(String(precoStr).replace('R$', '').replace(',', '.').trim());
      if (isNaN(precoNumerico)) precoNumerico = 0;

      // Formata o preço corretamente para o padrão brasileiro com 2 casas decimais (ex: R$ 125,00)
      const precoFormatado = 'R$ ' + precoNumerico.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

      if (typeof adicionarAoCarrinho === 'function') {
        adicionarAoCarrinho({
          id: produtoId,
          nome: nome,
          preco: precoFormatado,
          subtitulo: origem, // Garante que o subtítulo/origem vá para o carrinho no lugar do "undefined"
          imagem: imagem
        });
      } else {
        console.error('Função adicionarAoCarrinho não encontrada.');
      }
    });
  } // <--- Chave fechada corretamente aqui!

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