document.addEventListener('DOMContentLoaded', () => {
  const containerVinhos = document.getElementById('vinhos-container');
  const totalProdutosEl = document.getElementById('total-produtos');
  const inputBusca = document.getElementById('input-busca');
  const botoesFiltro = document.querySelectorAll('.agnello-filter-btn');

  // Seletores para o Dropdown Customizado Agnello
  const customSelect = document.getElementById('agnelloSortDropdown');
  const selectedDiv = customSelect ? customSelect.querySelector('.agnello-select-selected') : null;
  const itemsContainer = customSelect ? customSelect.querySelector('.agnello-select-items') : null;
  const optionItems = itemsContainer ? itemsContainer.querySelectorAll('div') : [];

  let categoriaAtiva = 'todos';
  let criterioOrdenacaoAtual = 'relevancia';

  // 1. Função para atualizar o contador visível de produtos filtrados
  function atualizarContador(cardsVisiveis) {
    if (totalProdutosEl) {
      totalProdutosEl.textContent = cardsVisiveis.length;
    }
  }

  // 2. Função auxiliar para converter string de preço em número para ordenação
  function converterPrecoParaNumero(precoStr) {
    if (!precoStr) return 0;
    return parseFloat(String(precoStr).replace('R$', '').replace('.', '').replace(',', '.').trim()) || 0;
  }

  // 3. Lógica principal de Filtragem, Busca e Ordenação baseada no DOM
  function aplicarFiltrosEBusca() {
    if (!containerVinhos) return;

    // Seleciona todos os cards de produtos renderizados pelo Servidor (JSTL)
    const cards = Array.from(containerVinhos.querySelectorAll('.product-card'));
    const emptyState = containerVinhos.querySelector('.grid-empty-state');

    let cardsVisiveis = 0;

    cards.forEach(card => {
      const tipo = (card.getAttribute('data-tipo') || '').toLowerCase();
      const nome = (card.getAttribute('data-nome') || '').toLowerCase();
      const origem = (card.getAttribute('data-origem') || '').toLowerCase();
      
      let passaFiltroCategoria = true;
      let passaFiltroBusca = true;

      // Filtro por Categoria
      if (categoriaAtiva === 'tinto') {
        passaFiltroCategoria = tipo.includes('tinto');
      } else if (categoriaAtiva === 'branco') {
        passaFiltroCategoria = tipo.includes('branco');
      } else if (categoriaAtiva === 'espumante') {
        passaFiltroCategoria = tipo.includes('espumante') || tipo.includes('cava') || tipo.includes('rosé');
      }

      // Filtro por Busca (Input)
      if (inputBusca && inputBusca.value.trim() !== '') {
        const termo = inputBusca.value.toLowerCase().trim();
        passaFiltroBusca = nome.includes(termo) || origem.includes(termo) || tipo.includes(termo);
      }

      // Exibe ou oculta o card com base nos filtros
      if (passaFiltroCategoria && passaFiltroBusca) {
        card.style.display = '';
        cardsVisiveis++;
      } else {
        card.style.display = 'none';
      }
    });

    // Atualiza o contador de rótulos encontrados
    atualizarContador(cards.filter(c => c.style.display !== 'none'));

    // Ordenação visual dos cards no DOM
    if (criterioOrdenacaoAtual !== 'relevancia') {
      cards.sort((a, b) => {
        const precoA = converterPrecoParaNumero(a.getAttribute('data-preco'));
        const precoB = converterPrecoParaNumero(b.getAttribute('data-preco'));
        const pontA = parseInt(a.getAttribute('data-pontuacao')) || 0;
        const pontB = parseInt(b.getAttribute('data-pontuacao')) || 0;

        if (criterioOrdenacaoAtual === 'menor-preco') return precoA - precoB;
        if (criterioOrdenacaoAtual === 'maior-preco') return precoB - precoA;
        if (criterioOrdenacaoAtual === 'pontuacao') return pontB - pontA;
        return 0;
      });

      // Reorganiza os elementos no DOM mantendo o empty state no fim se existir
      cards.forEach(card => containerVinhos.appendChild(card));
    }
  }

  // 4. Event Listeners para Busca
  if (inputBusca) {
    inputBusca.addEventListener('input', aplicarFiltrosEBusca);
  }

  // 5. Event Listeners para Filtros de Categoria (Botões)
  botoesFiltro.forEach(botao => {
    botao.addEventListener('click', (e) => {
      botoesFiltro.forEach(b => b.classList.remove('agnello-ativo'));
      e.currentTarget.classList.add('agnello-ativo');
      categoriaAtiva = e.currentTarget.getAttribute('data-filtro') || 'todos';
      aplicarFiltrosEBusca();
    });
  });

  // 6. Event Listeners para o Dropdown Customizado de Ordenação
  if (customSelect && selectedDiv && itemsContainer) {
    selectedDiv.addEventListener('click', (e) => {
      e.stopPropagation();
      itemsContainer.classList.toggle('agnello-select-hide');
    });

    optionItems.forEach(item => {
      item.addEventListener('click', (e) => {
        e.stopPropagation();
        selectedDiv.textContent = item.textContent;
        itemsContainer.classList.add('agnello-select-hide');

        criterioOrdenacaoAtual = item.getAttribute('data-value');
        aplicarFiltrosEBusca();
      });
    });

    document.addEventListener('click', () => {
      itemsContainer.classList.add('agnello-select-hide');
    });
  }

  // Carga inicial: conta os produtos renderizados pelo servidor assim que abre a página
  if (containerVinhos) {
    const cardsIniciais = containerVinhos.querySelectorAll('.product-card');
    atualizarContador(cardsIniciais);
  }
});