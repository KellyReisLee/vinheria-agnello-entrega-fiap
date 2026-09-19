document.addEventListener('DOMContentLoaded', () => {
  // Carrega o carrinho salvo no localStorage do navegador ao iniciar
  let carrinho = JSON.parse(localStorage.getItem('agnello_carrinho')) || [];

  // Função auxiliar para salvar o carrinho atualizado no navegador
  function salvarCarrinho() {
    localStorage.setItem('agnello_carrinho', JSON.stringify(carrinho));
  }

  const containerVinhos = document.getElementById('vinhos-container');
  const totalProdutosEl = document.getElementById('total-produtos');
  const inputBusca = document.getElementById('input-busca');
  const botoesFiltro = document.querySelectorAll('.agnello-filter-btn');
  const selectOrdenacao = document.getElementById('select-ordenacao');

  // Elementos do Carrinho
  const drawer = document.getElementById('agnello-cart-drawer');
  const overlay = document.getElementById('agnello-cart-overlay');
  const closeBtn = document.getElementById('agnello-cart-close');
  const cartBody = document.getElementById('agnello-cart-body');
  const cartTitle = document.getElementById('agnello-cart-title');

  let categoriaAtiva = 'todos';

  // Controle de Abertura/Fechamento da Gaveta
  function abrirCarrinho() {
    if (drawer) drawer.classList.add('ativo');
    if (overlay) overlay.classList.add('ativo');
    renderizarConteudoCarrinho();
  }

  function fecharCarrinho() {
    if (drawer) drawer.classList.remove('ativo');
    if (overlay) overlay.classList.remove('ativo');
  }

  // Delegação de evento global para o botão do carrinho injetado pelo componente
  document.addEventListener('click', (event) => {
    if (event.target.closest('.cart-btn')) {
      abrirCarrinho();
    }
  });

 
  // Intercepta o clique no botão de finalizar pedido de forma dinâmica
  document.addEventListener('click', (event) => {
    if (event.target && event.target.id === 'btn-ir-checkout') {
      event.preventDefault();
      
      // Descobre o caminho base do projeto automaticamente (funciona local e no Render)
      const baseUrl = window.location.origin;
      const pathParts = window.location.pathname.split('/');
      const contextPath = pathParts.length > 2 && !pathParts[1].includes('html') && !pathParts[1].includes('jsp') ? '/' + pathParts[1] : '';
      
      window.location.href = baseUrl + contextPath + '/checkout';
    }
  });
  
  
  if (closeBtn) closeBtn.addEventListener('click', fecharCarrinho);
  if (overlay) overlay.addEventListener('click', fecharCarrinho);

  // Renderização do Catálogo de Vinhos
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
          <img src="${vinho.imagem}" alt="${vinho.nome}" style="width: 100%; height: 100%; object-fit: contain;" onerror="this.style.display='none';">
        </div>
        <div>
          <div class="agnello-tags-row">
            <span class="agnello-tag-tipo">${vinho.tipo}</span>
            ${vinho.pontuacao ? `<span class="agnello-tag-pontos">${vinho.pontuacao}</span>` : ''}
          </div>
          <div class="agnello-card-origem">${vinho.origem}</div>
          <h3 class="agnello-card-nome">${vinho.nome}</h3>
          <p class="agnello-card-desc">${vinho.descricao}</p>
        </div>
        <div class="agnello-card-footer">
          <div>
            ${vinho.precoAntigo ? `<span class="agnello-preco-antigo">${vinho.precoAntigo}</span>` : ''}
            <span class="agnello-preco-atual">${vinho.preco}</span>
          </div>
          <button class="agnello-btn-comprar" onclick="window.adicionarAoCarrinho({
            nome: '${vinho.nome.replace(/'/g, "\\'")}',
            origem: '${vinho.origem.replace(/'/g, "\\'")}',
            preco: '${vinho.preco}',
            imagem: '${vinho.imagem}'
          })">Selecionar</button>
        </div>
      </div>
    `).join('');
  }

  // Renderização Dinâmica do Conteúdo do Carrinho
  function renderizarConteudoCarrinho() {
    let totalItens = carrinho.reduce((acc, item) => acc + (item.quantidade || 1), 0);
    const cartBadge = document.querySelector('.cart-badge');

    if (cartTitle) cartTitle.textContent = `Carrinho ( ${totalItens} )`;

    if (cartBadge) {
      cartBadge.textContent = totalItens;
      cartBadge.style.display = totalItens > 0 ? 'inline-block' : 'none';
    }

    if (!cartBody) return;

    if (carrinho.length === 0) {
      cartBody.innerHTML = `
        <div class="agnello-cart-empty" style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; text-align: center; color: #777;">
          <div style="font-size: 3rem; color: #CCC; margin-bottom: 1rem; font-weight: 300;">= (</div>
          <h3 style="font-family: var(--font-heading, 'Playfair Display', serif); font-size: 1.25rem; color: #2C2C2C; margin-bottom: 0.5rem;">Você ainda não escolheu seus produtos</h3>
        </div>
      `;
    } else {
      let totalGeral = carrinho.reduce((acc, item) => {
        let precoNum = parseFloat(item.preco.replace('R$', '').replace('.', '').replace(',', '.').trim());
        return acc + (precoNum * (item.quantidade || 1));
      }, 0);

      let itensHTML = carrinho.map((item, index) => {
        let precoNum = parseFloat(item.preco.replace('R$', '').replace('.', '').replace(',', '.').trim());
        let subtotalItem = precoNum * (item.quantidade || 1);

        return `
          <div style="display: flex; gap: 1rem; padding: 1rem 0; border-bottom: 1px solid #EFECE6; position: relative; align-items: center;">
            <button onclick="window.removerDoCarrinho(${index})" style="position: absolute; top: 10px; right: 0; background: none; border: none; font-size: 1.1rem; color: #999; cursor: pointer;">&times;</button>
            
            <img src="${item.imagem}" alt="${item.nome}" style="width: 50px; height: 90px; object-fit: contain; background: #F4F1EA; border-radius: 4px; padding: 4px;" onerror="this.style.display='none'">
            
            <div style="flex: 1;">
              <div style="font-size: 0.85rem; font-weight: 600; color: #2C2C2C; margin-bottom: 2px; padding-right: 15px; line-height: 1.3;">${item.nome}</div>
              <div style="font-size: 0.75rem; color: #777; margin-bottom: 8px;">${item.origem}</div>
              
              <div style="display: flex; align-items: center; justify-content: space-between;">
                <div style="display: flex; align-items: center; border: 1px solid #DCD6CD; border-radius: 4px; background: #FFF;">
                  <button onclick="window.alterarQtd(${index}, -1)" style="background: none; border: none; padding: 2px 8px; cursor: pointer; color: #555;">-</button>
                  <span style="font-size: 0.85rem; padding: 0 6px; font-weight: 600;">${item.quantidade || 1}</span>
                  <button onclick="window.alterarQtd(${index}, 1)" style="background: none; border: none; padding: 2px 8px; cursor: pointer; color: #555;">+</button>
                </div>
                
                <div style="font-weight: 700; color: #4A1525; font-size: 0.95rem;">
                  R$ ${subtotalItem.toFixed(2).replace('.', ',')}
                </div>
              </div>
            </div>
          </div>
        `;
      }).join('');

      cartBody.innerHTML = `
        <div style="flex: 1; overflow-y: auto; padding-right: 5px;">
          ${itensHTML}
        </div>
        <div style="margin-top: auto; background: #FFFFFF; border-top: 1px solid #EFECE6; padding: 1.2rem 0;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; font-size: 1.1rem; font-weight: 700; color: #2C2C2C;">
            <span>Total</span>
            <span style="color: #4A1525; font-size: 1.3rem;">R$ ${totalGeral.toFixed(2).replace('.', ',')}</span>
          </div>
          <button id="btn-ir-checkout" style="width: 100%; background-color: #da7e05; color: #FFFFFF; border: none; padding: 0.9rem; border-radius: 6px; font-size: 1.5rem; letter-spacing: 1px; font-weight: 700; cursor: pointer;">Finalizar pedido</button>
        </div>
      `;
    }
  }

  // Funções Globais Escutadas pelos Botões HTML
  window.adicionarAoCarrinho = function (vinhoObj) {
    let indexExistente = carrinho.findIndex(item => item.nome === vinhoObj.nome);
    if (indexExistente !== -1) {
      carrinho[indexExistente].quantidade = (carrinho[indexExistente].quantidade || 1) + 1;
    } else {
      vinhoObj.quantidade = 1;
      carrinho.push(vinhoObj);
    }
    salvarCarrinho();
    renderizarConteudoCarrinho();
    abrirCarrinho();
  };

  window.alterarQtd = function (index, delta) {
    if (!carrinho[index].quantidade) carrinho[index].quantidade = 1;
    carrinho[index].quantidade += delta;

    if (carrinho[index].quantidade <= 0) {
      carrinho.splice(index, 1);
    }
    salvarCarrinho();
    renderizarConteudoCarrinho();
  };

  window.removerDoCarrinho = function (index) {
    carrinho.splice(index, 1);
    salvarCarrinho();
    renderizarConteudoCarrinho();
  };

  function converterPrecoParaNumero(precoStr) {
    if (!precoStr) return 0;
    return parseFloat(precoStr.replace('R$', '').replace('.', '').replace(',', '.').trim());
  }

  function aplicarFiltrosEBusca() {
    let resultado = [...catalogoVinhos];

    if (categoriaAtiva === 'tinto') {
      resultado = resultado.filter(v => v.tipo.toLowerCase().includes('tinto'));
    } else if (categoriaAtiva === 'branco') {
      resultado = resultado.filter(v => v.tipo.toLowerCase().includes('branco'));
    } else if (categoriaAtiva === 'espumante') {
      resultado = resultado.filter(v => v.tipo.toLowerCase().includes('espumante') || v.tipo.toLowerCase().includes('rosé') || v.tipo.toLowerCase().includes('cava'));
    }

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

    if (selectOrdenacao) {
      const criterioOrdenacao = selectOrdenacao.value;
      if (criterioOrdenacao === 'menor-preco') {
        resultado.sort((a, b) => converterPrecoParaNumero(a.preco) - converterPrecoParaNumero(b.preco));
      } else if (criterioOrdenacao === 'maior-preco') {
        resultado.sort((a, b) => converterPrecoParaNumero(b.preco) - converterPrecoParaNumero(a.preco));
      } else if (criterioOrdenacao === 'nome') {
        resultado.sort((a, b) => a.nome.localeCompare(b.nome));
      }
    }

    renderizarCatalogo(resultado);
  }

  if (inputBusca) inputBusca.addEventListener('input', aplicarFiltrosEBusca);
  if (selectOrdenacao) selectOrdenacao.addEventListener('change', aplicarFiltrosEBusca);

  botoesFiltro.forEach(botao => {
    botao.addEventListener('click', (e) => {
      botoesFiltro.forEach(b => b.classList.remove('agnello-ativo'));
      e.target.classList.add('agnello-ativo');
      categoriaAtiva = e.target.getAttribute('data-filtro');
      aplicarFiltrosEBusca();
    });
  });

  // Inicialização
    renderizarConteudoCarrinho();

    if (typeof catalogoVinhos !== 'undefined') {
      renderizarCatalogo(catalogoVinhos);
    } else {
      console.error("A lista de produtos (catalogoVinhos) não foi carregada corretamente do banco de dados.");
    }
  });