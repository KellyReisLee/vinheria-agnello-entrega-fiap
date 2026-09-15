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

  // Função principal para renderizar os cards na tela
  function renderizarCatalogo(listaDeVinhos) {
    if (!containerVinhos) return;

    if (totalProdutosEl) {
      totalProdutosEl.textContent = listaDeVinhos.length;
    }

    if (listaDeVinhos.length === 0) {
      containerVinhos.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 3rem; color: #666;">
          <h3>Nenhum rótulo encontrado</h3>
          <p>Tente buscar por outro termo ou categoria.</p>
        </div>
      `;
      return;
    }

    containerVinhos.innerHTML = listaDeVinhos.map(vinho => `
      <div class="agnello-wine-card">
        ${vinho.desconto ? `<span class="agnello-badge-off">${vinho.desconto}</span>` : ''}
        <div class="agnello-card-img-container">
          <img src="${vinho.imagem}" alt="${vinho.nome}" onerror="this.style.display='none'; this.parentElement.style.backgroundColor='#f4f4f4';">
        </div>
        <div class="agnello-card-info">
          <div class="agnello-tags-row">
            <span class="agnello-tag-tipo">${vinho.tipo}</span>
            <span class="agnello-tag-pontos">${vinho.pontuacao}</span>
          </div>
          <span class="agnello-card-origem">${vinho.origem}</span>
          <h3 class="agnello-card-nome">${vinho.nome}</h3>
          <p class="agnello-card-desc">${vinho.descricao}</p>
          <div class="agnello-card-footer">
            <div class="agnello-card-precos">
              ${vinho.precoAntigo ? `<span class="agnello-preco-antigo">${vinho.precoAntigo}</span>` : ''}
              <span class="agnello-preco-atual">${vinho.preco}</span>
            </div>
            <button class="agnello-btn-comprar" onclick="alert('Rótulo ${vinho.nome} selecionado com sucesso!')">Selecionar</button>
          </div>
        </div>
      </div>
    `).join('');
  }

  // Função auxiliar para converter o preço em texto em número para ordenação
  function converterPrecoParaNumero(precoStr) {
    if (!precoStr) return 0;
    return parseFloat(precoStr.replace('R$', '').replace('.', '').replace(',', '.').trim());
  }

  // Lógica combinada de Filtragem, Busca e Ordenação
  function aplicarFiltrosEBusca() {
    let resultado = [...catalogoVinhos];

    // 1. Filtrar por categoria selecionada nos botões
    if (categoriaAtiva === 'tinto') {
      resultado = resultado.filter(v => v.tipo.toLowerCase().includes('tinto'));
    } else if (categoriaAtiva === 'branco') {
      resultado = resultado.filter(v => v.tipo.toLowerCase().includes('branco'));
    } else if (categoriaAtiva === 'espumante') {
      resultado = resultado.filter(v => v.tipo.toLowerCase().includes('espumante') || v.tipo.toLowerCase().includes('cava'));
    }

    // 2. Filtrar pelo termo digitado na barra de busca
    if (inputBusca) {
      const termoBusca = inputBusca.value.toLowerCase().trim();
      if (termoBusca !== '') {
        resultado = resultado.filter(v =>
          v.nome.toLowerCase().includes(termoBusca) ||
          v.origem.toLowerCase().includes(termoBusca) ||
          v.tipo.toLowerCase().includes(termoBusca) ||
          v.descricao.toLowerCase().includes(termoBusca)
        );
      }
    }

    // 3. Ordenação baseada no dropdown customizado
    if (criterioOrdenacaoAtual === 'menor-preco') {
      resultado.sort((a, b) => converterPrecoParaNumero(a.preco) - converterPrecoParaNumero(b.preco));
    } else if (criterioOrdenacaoAtual === 'maior-preco') {
      resultado.sort((a, b) => converterPrecoParaNumero(b.preco) - converterPrecoParaNumero(a.preco));
    } else if (criterioOrdenacaoAtual === 'pontuacao') {
      resultado.sort((a, b) => {
        const pA = parseInt(a.pontuacao) || 0;
        const pB = parseInt(b.pontuacao) || 0;
        return pB - pA;
      });
    } else if (criterioOrdenacaoAtual === 'relevancia') {
      // Tratamento explícito para Relevância mantendo o fluxo dinâmico
      resultado = [...resultado];
    }

    renderizarCatalogo(resultado);
  }

  // Event Listeners para Busca
  if (inputBusca) {
    inputBusca.addEventListener('input', aplicarFiltrosEBusca);
  }

  // Event Listeners para Filtros de Categoria
  botoesFiltro.forEach(botao => {
    botao.addEventListener('click', (e) => {
      botoesFiltro.forEach(b => b.classList.remove('agnello-ativo'));
      e.currentTarget.classList.add('agnello-ativo');
      categoriaAtiva = e.currentTarget.getAttribute('data-filtro') || 'todos';
      aplicarFiltrosEBusca();
    });
  });

  // Event Listeners para o Dropdown Customizado de Ordenação
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

  // Carga inicial exibindo todos os vinhos do catalogo.js
  if (typeof catalogoVinhos !== 'undefined') {
    renderizarCatalogo(catalogoVinhos);
  } else {
    console.error("O arquivo catalogo.js não foi carregado corretamente.");
  }
});