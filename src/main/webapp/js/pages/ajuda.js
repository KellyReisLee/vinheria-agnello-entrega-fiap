// js/pages/ajuda.js
// Central de Ajuda: pesquisa nas perguntas, filtro por tema e abertura
// automática da pergunta indicada no endereço (ex.: /ajuda#prazo-entrega).
(function () {
  const campo = document.getElementById('faq-pesquisa');
  const botoes = [...document.querySelectorAll('.faq-cat')];
  const grupos = [...document.querySelectorAll('.faq-grupo')];
  const vazio = document.getElementById('faq-vazio');
  const termoVazio = document.getElementById('faq-termo');
  const status = document.getElementById('faq-status');
  if (!campo || !grupos.length) return;

  // "Prazo de Entrega" e "prazo de entrega" (e sem acentos) contam como iguais
  const normalizar = (texto) => texto.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();

  // Guarda o texto de cada pergunta uma única vez
  const itens = grupos.flatMap((grupo) =>
    [...grupo.querySelectorAll('.faq-item')].map((item) => {
      const resumo = item.querySelector('summary');
      return {
        item,
        grupo,
        resumo,
        tituloOriginal: resumo.textContent,
        texto: normalizar(item.textContent)
      };
    })
  );

  // Número de perguntas ao lado de cada tema
  botoes.forEach((botao) => {
    const cat = botao.dataset.cat;
    const total = cat === 'todas' ? itens.length : itens.filter((i) => i.grupo.dataset.cat === cat).length;
    botao.querySelector('.faq-cat-total').textContent = total;
  });

  const estado = { cat: 'todas', termo: '' };

  // Destaca o termo pesquisado no título (só texto: sem HTML vindo do utilizador)
  function destacar(info, termo) {
    const { resumo, tituloOriginal } = info;
    resumo.textContent = tituloOriginal;
    if (!termo) return;
    const pos = normalizar(tituloOriginal).indexOf(termo);
    if (pos === -1) return;
    const marca = document.createElement('mark');
    marca.className = 'faq-marca';
    marca.textContent = tituloOriginal.slice(pos, pos + termo.length);
    resumo.textContent = '';
    resumo.append(tituloOriginal.slice(0, pos), marca, tituloOriginal.slice(pos + termo.length));
  }

  function aplicar() {
    const termo = normalizar(estado.termo.trim());
    let visiveis = 0;

    itens.forEach((info) => {
      const temaOk = estado.cat === 'todas' || info.grupo.dataset.cat === estado.cat;
      const textoOk = !termo || info.texto.includes(termo);
      info.item.hidden = !(temaOk && textoOk);
      if (!info.item.hidden) visiveis++;
      destacar(info, termo);
    });

    grupos.forEach((grupo) => {
      grupo.hidden = !grupo.querySelector('.faq-item:not([hidden])');
    });

    vazio.hidden = visiveis > 0;
    termoVazio.textContent = estado.termo.trim();
    status.textContent = termo
      ? `${visiveis} ${visiveis === 1 ? 'pergunta encontrada' : 'perguntas encontradas'}`
      : '';
  }

  function escolherTema(cat) {
    estado.cat = cat;
    botoes.forEach((b) => b.setAttribute('aria-pressed', String(b.dataset.cat === cat)));
    aplicar();
  }

  botoes.forEach((botao) => botao.addEventListener('click', () => escolherTema(botao.dataset.cat)));

  let espera;
  campo.addEventListener('input', () => {
    clearTimeout(espera);
    espera = setTimeout(() => {
      estado.termo = campo.value;
      // Ao pesquisar, procura em todos os temas
      if (estado.termo.trim() && estado.cat !== 'todas') escolherTema('todas');
      else aplicar();
    }, 120);
  });

  // Abre a pergunta indicada no endereço (links de outras páginas)
  function abrirDoEndereco() {
    const id = decodeURIComponent(location.hash.slice(1));
    const alvo = id && document.getElementById(id);
    if (alvo && alvo.classList.contains('faq-item')) {
      alvo.open = true;
      alvo.scrollIntoView({ block: 'start' });
      alvo.querySelector('summary').focus({ preventScroll: true });
    }
  }

  window.addEventListener('hashchange', abrirDoEndereco);
  aplicar();
  abrirDoEndereco();
})();
