// js/pages/quiz.js
// Sommelier Virtual: 3 perguntas em que as opções de cada passo dependem das
// respostas anteriores. No fim, guarda o perfil no localStorage
// ('vinhoQuizResult') e abre /sugestoes, que lê esse perfil.
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('quiz-form');
  if (!form) return;

  const contextPath = window.CONTEXT_PATH || '';
  const TOTAL_PASSOS = 3;

  const elPergunta = document.getElementById('quiz-pergunta');
  const elOpcoes = document.getElementById('quiz-opcoes');
  const elFill = document.getElementById('progress-fill');
  const elTexto = document.getElementById('progress-text');
  const elTrack = document.querySelector('.progress-bar-track');
  const btnVoltar = document.getElementById('btn-voltar');
  const btnVoltarTexto = document.getElementById('btn-voltar-texto');
  const btnProximoTexto = document.getElementById('btn-proximo-texto');

  // ---------- Árvore de perguntas ----------
  // Cada passo devolve { pergunta, opcoes: [{ valor, texto }] } conforme as respostas.
  const PASSOS = {
    1: () => ({
      pergunta: 'Qual é a ocasião ou o prato principal do seu momento?',
      opcoes: [
        { valor: 'carnes-vermelhas', texto: 'Carnes vermelhas & churrasco nobre' },
        { valor: 'massas-molhos', texto: 'Massas com molhos intensos ou ragus' },
        { valor: 'peixes-frutos-mar', texto: 'Peixes, frutos do mar ou comida japonesa' },
        { valor: 'momento-a-dois', texto: 'Sunset ou coquetel de verão' }
      ]
    }),

    2: (r) => ({
      'carnes-vermelhas': {
        pergunta: 'Qual o corte ou a intensidade da carne principal?',
        opcoes: [
          { valor: 'corte-gorduroso', texto: 'Cortes gordurosos (picanha, ribeye)' },
          { valor: 'corte-magro', texto: 'Cortes magros (filé mignon, cordeiro)' }
        ]
      },
      'massas-molhos': {
        pergunta: 'Como é a estrutura predominante do molho?',
        opcoes: [
          { valor: 'molho-carne-ragu', texto: 'Ragu de carne ou molhos encorpados de tomate' },
          { valor: 'molho-branco-queijos', texto: 'Molhos brancos cremosos ou queijos intensos' }
        ]
      },
      'peixes-frutos-mar': {
        pergunta: 'O prato tem molhos leves ou uma estrutura mais untuosa?',
        opcoes: [
          { valor: 'peixe-grelhado', texto: 'Peixe grelhado com limão e azeite' },
          { valor: 'molho-cremoso', texto: 'Frutos do mar ao molho cremoso ou comida japonesa' }
        ]
      },
      'momento-a-dois': {
        pergunta: 'Qual é a proposta para este momento ao ar livre?',
        opcoes: [
          { valor: 'sunset-refrescante', texto: 'Espumantes vibrantes ou brancos leves' },
          { valor: 'rose-elegante', texto: 'Rosés gastronômicos ou tintos frutados e leves' }
        ]
      }
    }[r.etapa1]),

    // Pratos de carne (e ragu) perguntam por taninos; os restantes por acidez.
    // Antes, "cortes magros" (filé mignon) caía na pergunta de acidez por engano.
    3: (r) => (r.etapa1 === 'carnes-vermelhas' || r.etapa2 === 'molho-carne-ragu'
      ? {
        pergunta: 'Que intensidade de tanino e carvalho você prefere?',
        opcoes: [
          { valor: 'taninos-potentes', texto: 'Taninos marcantes e passagem longa por barrica' },
          { valor: 'taninos-macios', texto: 'Taninos macios, redondos e com boa fruta' }
        ]
      }
      : {
        pergunta: 'Qual o nível de acidez e frescor ideal para o seu paladar?',
        opcoes: [
          { valor: 'fresco-mineral', texto: 'Alta acidez, toque mineral e refrescante' },
          { valor: 'frutado-equilibrado', texto: 'Frutado, macio e de perfil versátil' }
        ]
      })
  };

  // ---------- Estado ----------
  let passo = 1;
  const respostas = {};   // { etapa1: 'carnes-vermelhas', ... }
  const resumo = {};      // { etapa1: 'Carnes vermelhas & churrasco nobre', ... }

  // ---------- Desenho do passo atual ----------
  function desenhar(focar) {
    const { pergunta, opcoes } = PASSOS[passo](respostas);
    const chave = `etapa${passo}`;

    // Se a resposta guardada já não existe neste passo (mudou o passo anterior), usa a 1.ª
    if (!opcoes.some((o) => o.valor === respostas[chave])) {
      respostas[chave] = opcoes[0].valor;
    }

    elPergunta.textContent = pergunta;
    elOpcoes.setAttribute('role', 'radiogroup');
    elOpcoes.setAttribute('aria-labelledby', 'quiz-pergunta');
    elOpcoes.innerHTML = opcoes.map((o) => {
      const sel = o.valor === respostas[chave];
      return `
        <button type="button" class="option-pill${sel ? ' selected' : ''}" role="radio"
                aria-checked="${sel}" tabindex="${sel ? 0 : -1}" data-value="${o.valor}">
          <span>${o.texto}</span>
          <span class="option-check" aria-hidden="true"></span>
        </button>`;
    }).join('');
    guardarResumo();

    // Progresso
    const pct = Math.round((passo / TOTAL_PASSOS) * 100);
    elFill.style.width = `${pct}%`;
    elTexto.textContent = `Passo ${passo} de ${TOTAL_PASSOS}`;
    if (elTrack) elTrack.setAttribute('aria-valuenow', passo);

    // Textos dos botões
    btnVoltarTexto.textContent = passo === 1 ? 'Início' : 'Voltar';
    btnProximoTexto.textContent = passo === TOTAL_PASSOS ? 'Ver sugestões' : 'Próximo';

    // Pequena animação ao trocar de pergunta
    const etapa = form.querySelector('.quiz-step');
    etapa.classList.remove('is-entering');
    void etapa.offsetWidth;
    etapa.classList.add('is-entering');

    if (focar) elPergunta.focus({ preventScroll: true });
  }

  function guardarResumo() {
    const { opcoes } = PASSOS[passo](respostas);
    const escolhida = opcoes.find((o) => o.valor === respostas[`etapa${passo}`]);
    if (escolhida) resumo[`etapa${passo}`] = escolhida.texto;
  }

  function selecionar(botao) {
    respostas[`etapa${passo}`] = botao.dataset.value;
    elOpcoes.querySelectorAll('.option-pill').forEach((b) => {
      const sel = b === botao;
      b.classList.toggle('selected', sel);
      b.setAttribute('aria-checked', String(sel));
      b.tabIndex = sel ? 0 : -1;
    });
    guardarResumo();
  }

  // ---------- Eventos ----------
  elOpcoes.addEventListener('click', (e) => {
    const botao = e.target.closest('.option-pill');
    if (botao) selecionar(botao);
  });

  // Setas do teclado percorrem as opções (padrão de "radiogroup")
  elOpcoes.addEventListener('keydown', (e) => {
    const botoes = [...elOpcoes.querySelectorAll('.option-pill')];
    const atual = botoes.indexOf(document.activeElement);
    if (atual === -1) return;
    let novo = null;
    if (e.key === 'ArrowDown' || e.key === 'ArrowRight') novo = (atual + 1) % botoes.length;
    if (e.key === 'ArrowUp' || e.key === 'ArrowLeft') novo = (atual - 1 + botoes.length) % botoes.length;
    if (novo !== null) {
      e.preventDefault();
      selecionar(botoes[novo]);
      botoes[novo].focus();
    }
  });

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    if (passo < TOTAL_PASSOS) {
      passo++;
      desenhar(true);
      return;
    }

    try {
      localStorage.setItem('vinhoQuizResult', JSON.stringify({
        ...respostas,
        resumo: [resumo.etapa1, resumo.etapa2, resumo.etapa3].filter(Boolean),
        data: new Date().toISOString()
      }));
    } catch (erro) {
      // Sem localStorage (modo privado): as sugestões mostram todos os vinhos
    }
    window.location.href = `${contextPath}/sugestoes`;
  });

  btnVoltar.addEventListener('click', () => {
    if (passo > 1) {
      passo--;
      desenhar(true);
    } else {
      window.location.href = `${contextPath}/home`;
    }
  });

  desenhar(false);
});
