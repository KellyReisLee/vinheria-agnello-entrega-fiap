// js/pages/catalogo.js
// Pesquisa, filtro por tipo e ordenação do catálogo — tudo no browser, sobre os
// cards que o servidor (JSTL) já desenhou. Os filtros ficam no endereço da página
// (ex.: /catalogo?tipo=tinto&q=malbec), por isso dá para partilhar o link.
document.addEventListener('DOMContentLoaded', () => {
  const container = document.getElementById('vinhos-container');
  if (!container) return;

  const cards = Array.from(container.querySelectorAll('.product-card'));
  const inputBusca = document.getElementById('input-busca');
  const btnLimparBusca = document.querySelector('.agnello-search-clear');
  const botoesFiltro = document.querySelectorAll('.agnello-filter-btn');
  const selectOrdenar = document.getElementById('ordenar');
  const totalEl = document.getElementById('total-produtos');
  const rotuloEl = document.getElementById('total-produtos-rotulo');
  const semResultados = document.getElementById('sem-resultados');
  const btnLimparFiltros = document.getElementById('limpar-filtros');

  const estado = { tipo: 'todos', busca: '', ordem: 'relevancia' };

  // "Rosé" -> "rose", "Itália" -> "italia": a pesquisa ignora acentos e maiúsculas
  const normalizar = (texto) => String(texto || '')
    .normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase().trim();

  // Pré-calcula os dados de cada card uma única vez
  const dados = new Map(cards.map((card) => [card, {
    ordem: Number(card.dataset.ordem) || 0,
    tipo: normalizar(card.dataset.tipo),
    texto: normalizar(`${card.dataset.nome} ${card.dataset.origem} ${card.dataset.tipo}`),
    // data-preco vem do Java como número ("189.9"), não como "R$ 189,90"
    preco: parseFloat(card.dataset.preco) || 0,
    // "93 Pts - Descorchados" -> 93
    pontuacao: parseInt(card.dataset.pontuacao, 10) || 0
  }]));

  function passaTipo(tipo) {
    switch (estado.tipo) {
      case 'tinto': return tipo.includes('tinto');
      case 'branco': return tipo.includes('branco');
      case 'espumante': return ['espumante', 'cava', 'rose', 'prosecco'].some((t) => tipo.includes(t));
      default: return true;
    }
  }

  function ordenar(lista) {
    const porOrdem = (a, b) => dados.get(a).ordem - dados.get(b).ordem;
    const criterios = {
      'menor-preco': (a, b) => dados.get(a).preco - dados.get(b).preco || porOrdem(a, b),
      'maior-preco': (a, b) => dados.get(b).preco - dados.get(a).preco || porOrdem(a, b),
      'pontuacao': (a, b) => dados.get(b).pontuacao - dados.get(a).pontuacao || porOrdem(a, b),
      // "Relevância" volta mesmo à ordem original do servidor
      'relevancia': porOrdem
    };
    return [...lista].sort(criterios[estado.ordem] || porOrdem);
  }

  function aplicar() {
    const termo = normalizar(estado.busca);
    // Vários termos: "malbec chile" encontra vinhos com as duas palavras
    const palavras = termo.split(/\s+/).filter(Boolean);
    let visiveis = 0;

    ordenar(cards).forEach((card) => {
      const d = dados.get(card);
      const mostrar = passaTipo(d.tipo) && palavras.every((p) => d.texto.includes(p));
      card.hidden = !mostrar;
      if (mostrar) visiveis++;
      // Reposiciona no DOM (antes da caixa "sem resultados") para refletir a ordem
      container.insertBefore(card, semResultados);
    });

    if (totalEl) totalEl.textContent = visiveis;
    if (rotuloEl) rotuloEl.textContent = visiveis === 1 ? 'rótulo encontrado' : 'rótulos encontrados';
    if (semResultados) semResultados.hidden = visiveis > 0 || cards.length === 0;
    if (btnLimparBusca) btnLimparBusca.hidden = estado.busca === '';

    guardarNoEndereco();
  }

  // ---------- Estado <-> endereço da página ----------
  function guardarNoEndereco() {
    const params = new URLSearchParams();
    if (estado.tipo !== 'todos') params.set('tipo', estado.tipo);
    if (estado.busca) params.set('q', estado.busca);
    if (estado.ordem !== 'relevancia') params.set('ordem', estado.ordem);
    const query = params.toString();
    const url = location.pathname + (query ? `?${query}` : '') + location.hash;
    history.replaceState(null, '', url);
  }

  function lerDoEndereco() {
    const params = new URLSearchParams(location.search);
    const tipo = params.get('tipo');
    if (tipo && document.querySelector(`.agnello-filter-btn[data-filtro="${CSS.escape(tipo)}"]`)) {
      estado.tipo = tipo;
    }
    estado.busca = params.get('q') || '';
    const ordem = params.get('ordem');
    if (ordem && selectOrdenar && selectOrdenar.querySelector(`option[value="${CSS.escape(ordem)}"]`)) {
      estado.ordem = ordem;
    }
  }

  function sincronizarControles() {
    if (inputBusca) inputBusca.value = estado.busca;
    if (selectOrdenar) selectOrdenar.value = estado.ordem;
    botoesFiltro.forEach((b) => {
      const ativo = b.dataset.filtro === estado.tipo;
      b.classList.toggle('agnello-ativo', ativo);
      b.setAttribute('aria-pressed', String(ativo));
    });
  }

  // ---------- Eventos ----------
  let espera;
  if (inputBusca) {
    inputBusca.addEventListener('input', () => {
      clearTimeout(espera);
      // Espera o utilizador parar de escrever (150 ms) antes de filtrar
      espera = setTimeout(() => {
        estado.busca = inputBusca.value.trim();
        aplicar();
      }, 150);
    });
  }

  if (btnLimparBusca) {
    btnLimparBusca.addEventListener('click', () => {
      estado.busca = '';
      sincronizarControles();
      aplicar();
      inputBusca.focus();
    });
  }

  botoesFiltro.forEach((botao) => {
    botao.addEventListener('click', () => {
      estado.tipo = botao.dataset.filtro || 'todos';
      sincronizarControles();
      aplicar();
    });
  });

  if (selectOrdenar) {
    selectOrdenar.addEventListener('change', () => {
      estado.ordem = selectOrdenar.value;
      aplicar();
    });
  }

  if (btnLimparFiltros) {
    btnLimparFiltros.addEventListener('click', () => {
      estado.tipo = 'todos';
      estado.busca = '';
      sincronizarControles();
      aplicar();
      if (inputBusca) inputBusca.focus();
    });
  }

  // ---------- Início ----------
  lerDoEndereco();
  sincronizarControles();
  aplicar();

  // Vindo da lupa do header (/catalogo#input-busca): foca logo a pesquisa
  if (location.hash === '#input-busca' && inputBusca) {
    inputBusca.focus({ preventScroll: true });
  }
});
