// js/pages/sugestoes-sommelier.js
// Lê o perfil guardado pelo quiz (localStorage 'vinhoQuizResult') e:
//   1) escolhe os TIPOS de vinho certos para o prato (tabela PERFIS abaixo);
//   2) ordena os vinhos desses tipos por afinidade com a 3.ª resposta
//      (taninos / acidez) e depois pela pontuação;
//   3) destaca o 1.º como "Escolha do Sommelier".
//
// Antes, as palavras de TODAS as respostas eram somadas; por exemplo,
// "molho branco com queijos" contém "queijo" e mostrava tintos, e quase todos
// os perfis acabavam por mostrar a adega inteira.
document.addEventListener('DOMContentLoaded', () => {
  const container = document.getElementById('vinhos-container');
  const descricao = document.getElementById('perfil-descricao');
  const listaResumo = document.getElementById('perfil-resumo');
  const semQuiz = document.getElementById('sem-quiz');
  if (!container) return;

  const cards = Array.from(container.querySelectorAll('.product-card'));

  let perfil = null;
  try {
    perfil = JSON.parse(localStorage.getItem('vinhoQuizResult'));
  } catch (e) {
    perfil = null;
  }

  // ---------- Sem quiz: convite em vez de alert() + redirecionamento ----------
  if (!perfil || !perfil.etapa1) {
    if (descricao) descricao.textContent = 'Descubra os rótulos ideais para o seu momento.';
    if (semQuiz) semQuiz.hidden = false;
    container.hidden = true;
    // "Refazer o quiz" não faz sentido se ainda não o fez
    const refazer = document.querySelector('.sugestoes-actions .btn-outline-dark');
    if (refazer) refazer.hidden = true;
    return;
  }

  // ---------- 1) Tipos recomendados por prato ----------
  // A ordem importa: o 1.º tipo é o mais indicado.
  const PERFIS = {
    'corte-gorduroso': { tipos: ['tinto'], frase: 'tintos estruturados, que equilibram a gordura dos cortes nobres' },
    'corte-magro': { tipos: ['tinto'], frase: 'tintos elegantes, que acompanham cortes magros sem os sobrepor' },
    'molho-carne-ragu': { tipos: ['tinto'], frase: 'tintos de boa acidez, perfeitos para molhos de tomate e ragus' },
    'molho-branco-queijos': { tipos: ['branco', 'espumante'], frase: 'brancos cremosos e espumantes, que limpam o paladar dos molhos e queijos' },
    'peixe-grelhado': { tipos: ['branco', 'espumante'], frase: 'brancos frescos e espumantes, que realçam o peixe grelhado' },
    'molho-cremoso': { tipos: ['branco', 'espumante', 'rose'], frase: 'brancos com corpo, espumantes e rosés para frutos do mar e comida japonesa' },
    'sunset-refrescante': { tipos: ['espumante', 'branco'], frase: 'espumantes vibrantes e brancos leves para o fim de tarde' },
    'rose-elegante': { tipos: ['rose', 'tinto'], frase: 'rosés gastronômicos e tintos frutados e leves' }
  };

  // Palavras que aproximam um vinho da 3.ª resposta (procuradas no tipo e na descrição)
  const AFINIDADE = {
    'taninos-potentes': ['encorpado', 'potente', 'estrutur', 'barrica', 'carvalho', 'tanino', 'cabernet', 'malbec', 'tannat'],
    'taninos-macios': ['macio', 'suave', 'frutado', 'redondo', 'merlot', 'pinot', 'leve', 'aveludado'],
    'fresco-mineral': ['mineral', 'fresco', 'acidez', 'citric', 'sauvignon', 'brut', 'vibrante', 'prosecco'],
    'frutado-equilibrado': ['frutado', 'macio', 'versatil', 'aromatico', 'chardonnay', 'rose', 'equilibr']
  };

  const escapar = (t) => String(t ?? '').replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));

  // "Rosé" -> "rose", "Itália" -> "italia"
  const normalizar = (t) => String(t || '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();

  // Classifica o tipo do vinho num dos 4 grupos usados acima
  function grupoDoVinho(tipo) {
    const t = normalizar(tipo);
    if (t.includes('espumante') || t.includes('prosecco') || t.includes('cava') || t.includes('champagne')) return 'espumante';
    if (t.includes('rose')) return 'rose';
    if (t.includes('branco')) return 'branco';
    if (t.includes('tinto')) return 'tinto';
    return '';
  }

  const regra = PERFIS[perfil.etapa2] || { tipos: [], frase: '' };
  const palavras = AFINIDADE[perfil.etapa3] || [];

  // ---------- 2) Pontuar e ordenar ----------
  const avaliados = cards.map((card) => {
    const grupo = grupoDoVinho(card.dataset.tipo);
    const posTipo = regra.tipos.indexOf(grupo);           // 0 = tipo ideal, -1 = fora do perfil
    const texto = normalizar(`${card.dataset.tipo} ${card.dataset.nome} ${card.querySelector('.product-desc')?.textContent || ''}`);
    const afinidade = palavras.filter((p) => texto.includes(p)).length;
    const pontos = parseInt(card.dataset.pontuacao, 10) || 0;
    const esgotado = card.classList.contains('is-esgotado');
    return { card, posTipo, afinidade, pontos, esgotado, ordem: Number(card.dataset.ordem) || 0 };
  });

  let recomendados = avaliados.filter((v) => v.posTipo !== -1);
  const encontrouPerfil = recomendados.length > 0;
  if (!encontrouPerfil) recomendados = avaliados; // segurança: nunca mostrar a página vazia

  recomendados.sort((a, b) =>
    (a.esgotado - b.esgotado) ||                   // disponíveis primeiro
    (a.posTipo - b.posTipo) ||                     // tipo mais indicado primeiro
    (b.afinidade - a.afinidade) ||                 // mais próximo da 3.ª resposta
    (b.pontos - a.pontos) ||                       // melhor pontuação
    (a.ordem - b.ordem));

  // Esconde todos e volta a mostrar só os recomendados, pela nova ordem
  avaliados.forEach((v) => { v.card.hidden = true; });
  recomendados.forEach((v) => {
    v.card.hidden = false;
    container.appendChild(v.card);
  });

  // ---------- 3) Destaque "Escolha do Sommelier" ----------
  const destaque = recomendados.find((v) => !v.esgotado);
  if (destaque && encontrouPerfil) {
    destaque.card.classList.add('is-destaque');
    const selo = document.createElement('span');
    selo.className = 'sugestao-destaque-selo';
    selo.innerHTML = '<span aria-hidden="true">★</span> Escolha do Sommelier';
    destaque.card.prepend(selo);
  }

  // ---------- Texto e resumo do perfil ----------
  if (descricao) {
    const total = recomendados.length;
    descricao.textContent = encontrouPerfil
      ? `Para o seu momento, o Sommelier recomenda ${regra.frase}. Selecionamos ${total} ${total === 1 ? 'rótulo' : 'rótulos'} da nossa adega:`
      : 'Não encontramos um rótulo 100% correspondente à sua escolha, mas estas são as nossas melhores sugestões:';
  }

  if (listaResumo && Array.isArray(perfil.resumo) && perfil.resumo.length) {
    listaResumo.innerHTML = perfil.resumo
      .map((r) => `<li>${escapar(r)}</li>`)
      .join('');
    listaResumo.hidden = false;
  }
});
