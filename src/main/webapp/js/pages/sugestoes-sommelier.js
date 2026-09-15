document.addEventListener('DOMContentLoaded', () => {
  const perfilClienteUnique = JSON.parse(localStorage.getItem('vinhoQuizResult'));
  const descricaoPerfilUnique = document.getElementById('perfil-descricao-unique');
  const containerVinhosUnique = document.getElementById('vinhos-container-unique');

  const contextPath = window.CONTEXT_PATH || '';

  // 1. VALIDAÇÃO: Se não houver dados do quiz salvos, redireciona para o quiz
  if (!perfilClienteUnique) {
    if (descricaoPerfilUnique) {
      descricaoPerfilUnique.textContent = "Redirecionando para o Sommelier Virtual...";
    }
    alert("Por favor, responda ao nosso Sommelier Virtual para encontrarmos os rótulos ideais para você.");
    window.location.href = contextPath + "/quiz";
    return; 
  }

  console.log("=== DIAGNÓSTICO DO QUIZ & BANCO ===");
  console.log("Dados do Quiz salvos no LocalStorage:", perfilClienteUnique);
  console.log("Produtos vindos do banco (catalogoBanco):", typeof catalogoBanco !== 'undefined' ? catalogoBanco : "catalogoBanco não definido!");

  if (descricaoPerfilUnique) {
    descricaoPerfilUnique.textContent = `Filtramos nossa adega com base nas suas preferências indicadas no quiz. Veja as melhores opções:`;
  }

  const produtosDisponiveis = typeof catalogoBanco !== 'undefined' ? catalogoBanco : [];

  // 2. SISTEMA DE FILTRAGEM INTELIGENTE
  // Extrai as respostas do quiz (suporta diferentes formatos de chaves que o quiz costuma salvar)
  const escolhaEtapa2 = (perfilClienteUnique.etapa2 || perfilClienteUnique.ocasiao || '').toLowerCase();
  const escolhaEtapa3 = (perfilClienteUnique.etapa3 || perfilClienteUnique.harmonizacao || perfilClienteUnique.perfil || '').toLowerCase();

  console.log("Filtros aplicados -> Etapa 2:", escolhaEtapa2, "| Etapa 3:", escolhaEtapa3);

  const vinhosFiltradosUnique = produtosDisponiveis.filter(vinho => {
    const nomeVinho = (vinho.nome || '').toLowerCase();
    const tipoVinho = (vinho.tipo || '').toLowerCase();
    const descVinho = (vinho.descricao || '').toLowerCase();
    const origemVinho = (vinho.origem || '').toLowerCase();

    // Se o cliente escolheu algo relacionado a carnes / tintos fortes
    if (escolhaEtapa2.includes('carne') || escolhaEtapa3.includes('carne') || escolhaEtapa3.includes('gorduroso') || escolhaEtapa3.includes('ragu') || escolhaEtapa3.includes('tinto')) {
      return tipoVinho.includes('tinto');
    }
    
    // Se escolheu peixes / frutos do mar / pratos leves / brancos
    if (escolhaEtapa2.includes('peixe') || escolhaEtapa3.includes('peixe') || escolhaEtapa3.includes('branco') || escolhaEtapa3.includes('cremoso')) {
      return tipoVinho.includes('branco');
    }

    // Se escolheu momentos ao ar livre / espumante / rosé
    if (escolhaEtapa2.includes('ao-ar-livre') || escolhaEtapa3.includes('rose') || escolhaEtapa3.includes('espumante')) {
      return tipoVinho.includes('espumante') || tipoVinho.includes('rosé') || tipoVinho.includes('rose');
    }

    // Fallback de correspondência por texto caso o quiz envie termos diretos
    return nomeVinho.includes(escolhaEtapa2) || descVinho.includes(escolhaEtapa2) ||
           nomeVinho.includes(escolhaEtapa3) || descVinho.includes(escolhaEtapa3) ||
           tipoVinho.includes(escolhaEtapa2) || tipoVinho.includes(escolhaEtapa3);
  });

  console.log("Vinhos filtrados correspondentes:", vinhosFiltradosUnique);

  // 3. SEGURANÇA: Se o filtro retornar vazio, exibe todo o catálogo para o cliente não ver tela vazia
  const resultadosExibirUnique = vinhosFiltradosUnique.length > 0 ? vinhosFiltradosUnique : produtosDisponiveis;

  if (containerVinhosUnique) {
    if (resultadosExibirUnique.length === 0) {
      containerVinhosUnique.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #fff;">
          <p>Nenhum rótulo encontrado no momento.</p>
        </div>
      `;
      return;
    }

    containerVinhosUnique.innerHTML = resultadosExibirUnique.map(vinho => `
      <div class="vinho-card-unique">
        ${vinho.desconto ? `<span class="vinho-badge-desconto-unique">${vinho.desconto}</span>` : ''}
        <div class="vinho-img-wrapper-unique">
          <img src="${vinho.imagem}" alt="${vinho.nome}" class="vinho-img-unique" onerror="this.style.display='none'; this.parentElement.style.backgroundColor='#f4f4f4';">
        </div>
        <div class="vinho-info-unique">
          <div class="vinho-meta-topo-unique">
            <span class="vinho-tipo-unique">${vinho.tipo}</span>
            <span class="vinho-pontuacao-unique">${vinho.pontuacao || ''}</span>
          </div>
          <span class="vinho-origem-unique">${vinho.origem}</span>
          <h3 class="vinho-nome-unique">${vinho.nome}</h3>
          <p class="vinho-desc-unique">${vinho.descricao}</p>
          <div class="vinho-footer-unique">
            <div class="vinho-precos-unique">
              ${vinho.precoAntigo > 0 ? `<span class="preco-antigo-unique">R$ ${vinho.precoAntigo}</span>` : ''}
              <span class="vinho-preco-unique">R$ ${vinho.preco}</span>
            </div>
            <button class="btn-comprar-unique" onclick='adicionarAoCarrinho(${JSON.stringify(vinho)})'>Selecionar</button>
          </div>
        </div>
      </div>
    `).join('');
  }
});