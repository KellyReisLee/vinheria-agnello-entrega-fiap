// js/pages/sugestoes-sommelier.js
// Lê o perfil guardado pelo quiz (localStorage 'vinhoQuizResult') e:
//   1) escolhe os TIPOS de vinho certos para o prato (tabela PERFIS abaixo);
//   2) dá a cada vinho uma nota de afinidade com a 3.ª resposta (taninos /
//      acidez): sobe com palavras que combinam e desce com as que contradizem;
//   3) mostra os 6 melhores (o resto fica em "Mostrar mais");
//   4) destaca o 1.º como "Escolha do Sommelier".
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

  // ---------- 2) Afinidade com a 3.ª resposta ----------
  // "mais": palavras que combinam com a resposta; "menos": palavras que a contradizem.
  // O tipo cadastrado (ex.: "Tinto ENCORPADO", "Tinto SUAVE") pesa 3 vezes mais que
  // a descrição, porque é a classificação feita pela própria loja.
  const AFINIDADE = {
    'taninos-potentes': {
      mais: ['encorpad', 'potente', 'estrutur', 'iconico', 'nobre', 'robust', 'barrica', 'carvalho', 'taninos firmes', 'guarda', 'cabernet', 'malbec', 'tannat', 'gran reserva'],
      menos: ['suave', 'leve', 'frutado', 'versatil', 'macio']
    },
    'taninos-macios': {
      mais: ['suave', 'frutado', 'versatil', 'equilibr', 'macio', 'maciez', 'sedoso', 'redondo', 'aveludado', 'merlot', 'pinot', 'tempranillo', 'chianti', 'crianza', 'ripasso'],
      menos: ['potente', 'encorpad', 'estrutur', 'iconico', 'taninos firmes']
    },
    'fresco-mineral': {
      mais: ['citric', 'mineral', 'fresc', 'organico', 'acidez', 'vibrante', 'vivacidade', 'sauvignon', 'brut', 'cava', 'prosecco'],
      menos: ['untuoso', 'amanteigado', 'manteiga', 'doce', 'adocicado', 'carvalho']
    },
    'frutado-equilibrado': {
      mais: ['frutad', 'fruta', 'aromatic', 'floral', 'elegante', 'sofisticado', 'untuoso', 'equilibr', 'versatil', 'chardonnay', 'rose', 'moscatel', 'cremosidade'],
      menos: ['acidez cortante', 'herbace', 'potente', 'encorpad', 'estrutur', 'iconico', 'taninos firmes']
    }
  };

  // Quantos vinhos mostrar logo de início (os outros ficam atrás de "Mostrar mais")
  const MAX_SUGESTOES = 6;

  const escapar = (t) => String(t ?? '').replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));

  // "Rosé" -> "rose", "Itália" -> "italia"
  const normalizar = (t) => String(t || '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();

  // Grupos do vinho: "Espumante Rosé" pertence a espumante E a rosé
  function gruposDoVinho(tipo) {
    const t = normalizar(tipo);
    const grupos = [];
    if (t.includes('espumante') || t.includes('prosecco') || t.includes('cava') || t.includes('champagne')) grupos.push('espumante');
    if (t.includes('rose')) grupos.push('rose');
    if (t.includes('branco') && !grupos.includes('espumante')) grupos.push('branco');
    if (t.includes('tinto')) grupos.push('tinto');
    return grupos;
  }

  const conta = (texto, lista) => lista.filter((p) => texto.includes(p)).length;

  const regra = PERFIS[perfil.etapa2] || { tipos: [], frase: '' };
  const afinidade = AFINIDADE[perfil.etapa3] || { mais: [], menos: [] };

  // ---------- 3) Pontuar e ordenar ----------
  const avaliados = cards.map((card) => {
    const posicoes = gruposDoVinho(card.dataset.tipo)
      .map((g) => regra.tipos.indexOf(g))
      .filter((i) => i !== -1);
    const posTipo = posicoes.length ? Math.min(...posicoes) : -1; // 0 = tipo ideal, -1 = fora do perfil
    const tipo = normalizar(card.dataset.tipo);
    const resto = normalizar(`${card.dataset.nome} ${card.querySelector('.product-desc')?.textContent || ''}`);
    const nota = 3 * (conta(tipo, afinidade.mais) - conta(tipo, afinidade.menos))
      + conta(resto, afinidade.mais) - conta(resto, afinidade.menos);
    const pontos = parseInt(card.dataset.pontuacao, 10) || 0;
    const esgotado = card.classList.contains('is-esgotado');
    return { card, posTipo, nota, pontos, esgotado, ordem: Number(card.dataset.ordem) || 0 };
  });

  let recomendados = avaliados.filter((v) => v.posTipo !== -1);
  const encontrouPerfil = recomendados.length > 0;
  if (!encontrouPerfil) recomendados = avaliados; // segurança: nunca mostrar a página vazia

  recomendados.sort((a, b) =>
    (a.esgotado - b.esgotado) ||                   // disponíveis primeiro
    (b.nota - a.nota) ||                           // mais próximo da 3.ª resposta
    (a.posTipo - b.posTipo) ||                     // tipo mais indicado primeiro
    (b.pontos - a.pontos) ||                       // melhor pontuação da crítica
    (a.ordem - b.ordem));

  // Vinhos que CONTRADIZEM a resposta (nota negativa) só aparecem se faltarem opções
  const bons = recomendados.filter((v) => v.nota >= 0 && !v.esgotado);
  if (bons.length >= 3) recomendados = recomendados.filter((v) => v.nota >= 0);

  // Esconde todos e volta a mostrar só os recomendados, pela nova ordem
  avaliados.forEach((v) => { v.card.hidden = true; });
  recomendados.forEach((v, i) => {
    v.card.hidden = i >= MAX_SUGESTOES;
    container.appendChild(v.card);
  });

  // "Mostrar mais": revela o resto da seleção
  const restantes = recomendados.length - MAX_SUGESTOES;
  if (restantes > 0) {
    const btnMais = document.createElement('button');
    btnMais.type = 'button';
    btnMais.className = 'btn-outline-dark sugestoes-mais';
    btnMais.textContent = `Mostrar mais ${restantes} ${restantes === 1 ? 'sugestão' : 'sugestões'}`;
    btnMais.addEventListener('click', () => {
      recomendados.forEach((v) => { v.card.hidden = false; });
      btnMais.remove();
      recomendados[MAX_SUGESTOES].card.querySelector('a, button')?.focus();
    });
    container.insertAdjacentElement('afterend', btnMais);
  }

  // ---------- 4) Destaque "Escolha do Sommelier" ----------
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
    const total = Math.min(recomendados.length, MAX_SUGESTOES);
    descricao.textContent = encontrouPerfil
      ? `Para o seu momento, o Sommelier recomenda ${regra.frase}. ${total === 1 ? 'Este é o rótulo' : `Estes são os ${total} rótulos`} da nossa adega que mais combinam com você:`
      : 'Não encontramos um rótulo 100% correspondente à sua escolha, mas estas são as nossas melhores sugestões:';
  }

  if (listaResumo && Array.isArray(perfil.resumo) && perfil.resumo.length) {
    listaResumo.innerHTML = perfil.resumo
      .map((r) => `<li>${escapar(r)}</li>`)
      .join('');
    listaResumo.hidden = false;
  }
});
