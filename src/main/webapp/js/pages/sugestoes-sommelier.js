document.addEventListener('DOMContentLoaded', () => {
  const perfilClienteUnique = JSON.parse(localStorage.getItem('vinhoQuizResult'));
  const descricaoPerfilUnique = document.getElementById('perfil-descricao-unique');
  const containerVinhosUnique = document.getElementById('vinhos-container-unique');

  // VALIDAÇÃO: Se não houver dados do quiz salvos, redireciona o cliente para respondê-lo
  if (!perfilClienteUnique || (!perfilClienteUnique.etapa2 && !perfilClienteUnique.etapa3)) {
    if (descricaoPerfilUnique) {
      descricaoPerfilUnique.textContent = "Redirecionando para o Sommelier Virtual...";
    }
    alert("Por favor, responda ao nosso Sommelier Virtual para encontrarmos os rótulos ideais para você.");
    window.location.href = "quiz.html"; // Altere para o nome do seu arquivo de quiz, se necessário
    return; // Interrompe a execução para não travar na tela de carregamento
  }

  if (descricaoPerfilUnique) {
    descricaoPerfilUnique.textContent = `Filtramos nossa adega com base na sua ocasião e nas preferências de estrutura indicadas. Veja abaixo as melhores opções disponíveis:`;
  }

  // Filtra os vinhos de acordo com as respostas do quiz (Etapa 2 ou Etapa 3)
  const vinhosFiltradosUnique = catalogoVinhos.filter(vinho => {
    return vinho.compatibilidade.includes(perfilClienteUnique.etapa2) ||
      vinho.compatibilidade.includes(perfilClienteUnique.etapa3);
  });

  // Se por ventura houver menos correspondências, garante a exibição robusta do catálogo geral
  const resultadosExibirUnique = vinhosFiltradosUnique.length >= 4 ? vinhosFiltradosUnique : catalogoVinhos;

  if (containerVinhosUnique) {
    containerVinhosUnique.innerHTML = resultadosExibirUnique.map(vinho => `
      <div class="vinho-card-unique">
        ${vinho.desconto ? `<span class="vinho-badge-desconto-unique">${vinho.desconto}</span>` : ''}
        <div class="vinho-img-wrapper-unique">
          <img src="${vinho.imagem}" alt="${vinho.nome}" class="vinho-img-unique" onerror="this.style.display='none'; this.parentElement.style.backgroundColor='#f4f4f4';">
        </div>
        <div class="vinho-info-unique">
          <div class="vinho-meta-topo-unique">
            <span class="vinho-tipo-unique">${vinho.tipo}</span>
            <span class="vinho-pontuacao-unique">${vinho.pontuacao}</span>
          </div>
          <span class="vinho-origem-unique">${vinho.origem}</span>
          <h3 class="vinho-nome-unique">${vinho.nome}</h3>
          <p class="vinho-desc-unique">${vinho.descricao}</p>
          <div class="vinho-footer-unique">
            <div class="vinho-precos-unique">
              ${vinho.precoAntigo ? `<span class="preco-antigo-unique">${vinho.precoAntigo}</span>` : ''}
              <span class="vinho-preco-unique">${vinho.preco}</span>
            </div>
            <button class="btn-comprar-unique" onclick='adicionarAoCarrinho(${JSON.stringify(vinho)})'>Selecionar</button>
           
          </div>
        </div>
      </div>
    `).join('');
  }
});