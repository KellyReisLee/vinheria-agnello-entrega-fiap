document.addEventListener('DOMContentLoaded', () => {
  const perfilClienteUnique = JSON.parse(localStorage.getItem('vinhoQuizResult'));
  const descricaoPerfilUnique = document.getElementById('perfil-descricao-unique');
  const containerVinhosUnique = document.getElementById('vinhos-container-unique');

  // Define o context path de forma segura para o projeto Java Web
  const contextPath = window.CONTEXT_PATH || '';

  // VALIDAÇÃO: Se não houver dados do quiz salvos, redireciona o cliente para respondê-lo
  if (!perfilClienteUnique || (!perfilClienteUnique.etapa2 && !perfilClienteUnique.etapa3)) {
    if (descricaoPerfilUnique) {
      descricaoPerfilUnique.textContent = "Redirecionando para o Sommelier Virtual...";
    }
    alert("Por favor, responda ao nosso Sommelier Virtual para encontrarmos os rótulos ideais para você.");
    window.location.href = contextPath + "/quiz"; // Redireciona para a rota correta do Servlet/JSP do Quiz
    return; 
  }

  if (descricaoPerfilUnique) {
    descricaoPerfilUnique.textContent = `Filtramos nossa adega com base na sua ocasião e nas preferências de estrutura indicadas. Veja abaixo as melhores opções disponíveis:`;
  }

  // Certifica-se de que a lista vinda do banco existe, senão usa um array vazio
  const produtosDisponiveis = typeof catalogoBanco !== 'undefined' ? catalogoBanco : [];

  // Logs úteis para depuração (pressione F12 no navegador para ver)
  console.log("=== DIAGNÓSTICO DO SOMMELIER ===");
  console.log("Respostas do Quiz do Cliente:", perfilClienteUnique);
  console.log("Produtos vindos do Banco (catalogoBanco):", produtosDisponiveis);

  // Filtra os vinhos de acordo com as respostas do quiz (Etapa 2 ou Etapa 3)
  const vinhosFiltradosUnique = produtosDisponiveis.filter(vinho => {
    const compatibilidade = vinho.compatibilidade || '';
    const etapa2 = perfilClienteUnique.etapa2 || '';
    const etapa3 = perfilClienteUnique.etapa3 || '';
    
    return compatibilidade.includes(etapa2) || compatibilidade.includes(etapa3);
  });

  console.log("Vinhos filtrados pela compatibilidade:", vinhosFiltradosUnique);

  // Se houver menos de 1 correspondência, exibe todo o catálogo do banco como fallback para evitar tela em branco
  const resultadosExibirUnique = vinhosFiltradosUnique.length > 0 ? vinhosFiltradosUnique : produtosDisponiveis;

  if (containerVinhosUnique) {
    if (resultadosExibirUnique.length === 0) {
      containerVinhosUnique.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #fff;">
          <p>Nenhum rótulo encontrado no momento. Por favor, tente refazer o quiz.</p>
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