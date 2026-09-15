document.addEventListener('DOMContentLoaded', () => {
  const perfilClienteUnique = JSON.parse(localStorage.getItem('vinhoQuizResult'));
  const descricaoPerfilUnique = document.getElementById('perfil-descricao-unique');
  const containerVinhosUnique = document.getElementById('vinhos-container-unique');

  const contextPath = window.CONTEXT_PATH || '';

  // VALIDAÇÃO: Se não houver dados do quiz salvos, redireciona
  if (!perfilClienteUnique || (!perfilClienteUnique.etapa2 && !perfilClienteUnique.etapa3)) {
    if (descricaoPerfilUnique) {
      descricaoPerfilUnique.textContent = "Redirecionando para o Sommelier Virtual...";
    }
    alert("Por favor, responda ao nosso Sommelier Virtual para encontrarmos os rótulos ideais para você.");
    window.location.href = contextPath + "/quiz";
    return; 
  }

  if (descricaoPerfilUnique) {
    descricaoPerfilUnique.textContent = `Filtramos nossa adega com base na sua ocasião e nas preferências de estrutura indicadas. Veja abaixo as melhores opções disponíveis:`;
  }

  const produtosDisponiveis = typeof catalogoBanco !== 'undefined' ? catalogoBanco : [];

  console.log("Respostas do Quiz:", perfilClienteUnique);
  console.log("Produtos do Banco:", produtosDisponiveis);

  // Função auxiliar que mapeia o tipo do vinho do banco para as tags de compatibilidade do Quiz
  function obterTagsCompatibilidade(tipoVinho) {
    const t = (tipoVinho || '').toLowerCase();
    
    if (t.includes('tinto')) {
      return [
        'carnes-vermelhas', 'corte-gorduroso', 'corte-magro', 
        'massas-molhos', 'molho-carne-ragu', 'taninos-potentes', 'taninos-macios'
      ];
    } else if (t.includes('branco')) {
      return [
        'peixes-frutos-mar', 'peixe-grelhado', 'molho-cremoso', 
        'molho-branco-queijos', 'fresco-mineral', 'frutado-equilibrado'
      ];
    } else {
      // Espumantes / Rosés / Outros
      return [
        'ao-ar-livre', 'sunset-refrescante', 'rose-elegante', 
        'fresco-mineral', 'frutado-equilibrado'
      ];
    }
  }

  // Filtra os vinhos cruzando o tipo do banco com a Etapa 2 ou Etapa 3 respondidas no quiz
  const vinhosFiltradosUnique = produtosDisponiveis.filter(vinho => {
    const tags = obterTagsCompatibilidade(vinho.tipo);
    const etapa2 = perfilClienteUnique.etapa2 || '';
    const etapa3 = perfilClienteUnique.etapa3 || '';

    return tags.includes(etapa2) || tags.includes(etapa3);
  });

  console.log("Vinhos filtrados para o cliente:", vinhosFiltradosUnique);

  // Se o filtro retornar algo, exibe eles; caso contrário, exibe o catálogo completo como segurança
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