document.addEventListener('DOMContentLoaded', () => {
  const perfilClienteUnique = JSON.parse(localStorage.getItem('vinhoQuizResult'));
  const descricaoPerfilUnique = document.getElementById('perfil-descricao-unique');
  const containerVinhosUnique = document.getElementById('vinhos-container');

  const contextPath = window.CONTEXT_PATH || '';

  // 1. Validação de segurança do Quiz
  if (!perfilClienteUnique) {
    if (descricaoPerfilUnique) {
      descricaoPerfilUnique.textContent = "A redirecionar para o Sommelier Virtual...";
    }
    alert("Por favor, responda ao nosso Sommelier Virtual para encontrarmos os rótulos ideais para si.");
    window.location.href = contextPath + "/quiz";
    return; 
  }

  if (!containerVinhosUnique) return;

  const cards = Array.from(containerVinhosUnique.querySelectorAll('.product-card'));
  if (cards.length === 0) return;

  console.log("Perfil do Cliente no LocalStorage:", perfilClienteUnique);

  // 2. DICIONÁRIO DE MAPEAMENTO UNIVERSAL
  // Associa qualquer código ou palavra-chave do quiz aos tipos de vinho permitidos na adega
  const regrasFiltro = {
    // Tintos e pratos encorpados
    'carne': ['tinto'],
    'tinto': ['tinto'],
    'intenso': ['tinto'],
    'robusto': ['tinto'],
    'churrasco': ['tinto'],
    'massa': ['tinto'],
    'queijo': ['tinto'],

    // Brancos e momentos frescos/minerais
    'branco': ['branco'],
    'fresco-mineral': ['branco', 'espumante'],
    'sunset-refrescante': ['branco', 'espumante', 'rosé', 'rose'],
    'peixe': ['branco'],
    'salada': ['branco'],
    'marisco': ['branco'],
    'leve': ['branco', 'espumante', 'rosé', 'rose'],

    // Rosés e perfis frutados/elegantes
    'rose-elegante': ['rosé', 'rose', 'espumante'],
    'frutado-equilibrado': ['rosé', 'rose', 'branco', 'frutado'],
    'rose': ['rosé', 'rose'],
    'rosé': ['rosé', 'rose'],
    'espumante': ['espumante'],
    'aperitivo': ['espumante', 'rosé', 'rose']
  };

  // 3. Recolher e traduzir todas as escolhas do quiz do utilizador
  const valoresQuiz = Object.values(perfilClienteUnique).map(v => (v || '').toLowerCase());
  
  let tiposPermitidos = [];

  valoresQuiz.forEach(valor => {
    // Procura exata na chave do dicionário
    if (regrasFiltro[valor]) {
      tiposPermitidos.push(...regrasFiltro[valor]);
    } else {
      // Procura parcial (ex: se a palavra contém "rose" ou "tinto" no meio da string)
      for (const [chave, tipos] of Object.entries(regrasFiltro)) {
        if (valor.includes(chave)) {
          tiposPermitidos.push(...tipos);
        }
      }
    }
  });

  // Remove duplicados da lista de tipos permitidos
  tiposPermitidos = [...new Set(tiposPermitidos)];
  console.log("Tipos de vinho permitidos para este perfil:", tiposPermitidos);

  let cardsVisiveis = 0;
  const aplicarFiltroRigoroso = tiposPermitidos.length > 0;

  if (descricaoPerfilUnique) {
    if (aplicarFiltroRigoroso) {
      descricaoPerfilUnique.textContent = "Filtramos a nossa adega com base na harmonia perfeita para o seu perfil:";
    } else {
      descricaoPerfilUnique.textContent = "A exibir todas as opções disponíveis na nossa adega:";
    }
  }

  // 4. Aplicar o filtro nos cartões de produtos
  cards.forEach(card => {
    const tipoCard = (card.getAttribute('data-tipo') || '').toLowerCase();
    const nomeCard = (card.getAttribute('data-nome') || '').toLowerCase();
    const descElement = card.querySelector('.product-desc');
    const descCard = descElement ? descElement.textContent.toLowerCase() : '';

    let passaFiltro = false;

    if (!aplicarFiltroRigoroso) {
      passaFiltro = true; // Se não houver regras, mostra tudo
    } else {
      // O card só aparece se o tipo do vinho corresponder a um dos tipos permitidos pelas regras do quiz
      passaFiltro = tiposPermitidos.some(tipoPermitido => 
        tipoCard.includes(tipoPermitido) || descCard.includes(tipoPermitido) || nomeCard.includes(tipoPermitido)
      );
    }

    if (passaFiltro) {
      card.style.display = '';
      cardsVisiveis++;
    } else {
      card.style.display = 'none';
    }
  });

  // 5. Mecanismo de segurança (caso o filtro restrinja demais por engano)
  if (cardsVisiveis === 0) {
    if (descricaoPerfilUnique) {
      descricaoPerfilUnique.textContent = "Não encontrámos um rótulo 100% correspondente a essa escolha específica, mas aqui estão as nossas melhores sugestões:";
    }
    cards.forEach(card => card.style.display = '');
  }
});