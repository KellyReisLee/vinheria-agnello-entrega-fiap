// js/components/header.js
// Comportamento do header: menu "hambúrguer" no mobile e sombra ao fazer scroll.
// É carregado pelo próprio header.jsp, por isso funciona em todas as páginas que o incluem.
(function () {
  const navbar = document.getElementById('site-navbar');
  if (!navbar) return;

  const toggle = navbar.querySelector('.menu-toggle');
  const menu = document.getElementById('nav-principal');
  const backdrop = document.querySelector('.nav-backdrop');

  function abrirMenu() {
    navbar.classList.add('menu-aberto');
    toggle.setAttribute('aria-expanded', 'true');
    toggle.setAttribute('aria-label', 'Fechar menu');
    if (backdrop) backdrop.hidden = false;
    document.body.classList.add('no-scroll');
    // Leva o foco para o 1.º link (quem usa teclado ou leitor de tela)
    const primeiro = menu.querySelector('a');
    if (primeiro) {
      void menu.offsetHeight; // força o browser a aplicar o CSS antes de focar
      primeiro.focus({ preventScroll: true });
    }
  }

  function fecharMenu(devolverFoco) {
    navbar.classList.remove('menu-aberto');
    toggle.setAttribute('aria-expanded', 'false');
    toggle.setAttribute('aria-label', 'Abrir menu');
    if (backdrop) backdrop.hidden = true;
    document.body.classList.remove('no-scroll');
    if (devolverFoco) toggle.focus();
  }

  if (toggle && menu) {
    toggle.addEventListener('click', () => {
      navbar.classList.contains('menu-aberto') ? fecharMenu(false) : abrirMenu();
    });

    // Fecha ao clicar num link, no fundo escuro ou ao apertar Esc
    menu.addEventListener('click', (e) => {
      if (e.target.closest('a')) fecharMenu(false);
    });
    if (backdrop) backdrop.addEventListener('click', () => fecharMenu(false));
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && navbar.classList.contains('menu-aberto')) fecharMenu(true);
    });

    // Se o tela crescer (ex.: rodar o tablet) com o menu aberto, fecha-o
    const desktop = window.matchMedia('(min-width: 901px)');
    desktop.addEventListener('change', (e) => {
      if (e.matches) fecharMenu(false);
    });
  }

  // Sombra mais marcada quando a página não está no topo
  const atualizarSombra = () => navbar.classList.toggle('scrolled', window.scrollY > 8);
  atualizarSombra();
  window.addEventListener('scroll', atualizarSombra, { passive: true });
})();

// ---------------------------------------------------------------------------
// Pesquisa da lupa: abre um painel por baixo do header com sugestões ao vivo.
// Sem JavaScript, a lupa continua a ser um link para o campo do catálogo.
// ---------------------------------------------------------------------------
(function () {
  const painel = document.getElementById('busca-global');
  if (!painel) return;

  const ctx = window.CONTEXT_PATH || '';
  const form = painel.querySelector('form');
  const input = document.getElementById('busca-global-input');
  const lista = document.getElementById('busca-global-sugestoes');
  const status = painel.querySelector('.busca-global-status');
  const btnFechar = painel.querySelector('.busca-global-fechar');
  const gatilhos = document.querySelectorAll('[data-abrir-busca]');
  const lupa = document.querySelector('.search-btn');

  let ultimoFoco = null;
  let pedidoAtual = null;   // AbortController do último pedido
  let espera = null;
  let ativo = -1;           // sugestão destacada com as setas

  const formatarPreco = (v) => Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

  function abrir(e) {
    if (e) e.preventDefault();
    // No mobile, a opção vem do menu: fecha-o primeiro
    const navbar = document.getElementById('site-navbar');
    if (navbar && navbar.classList.contains('menu-aberto')) {
      navbar.querySelector('.menu-toggle').click();
    }
    ultimoFoco = document.activeElement;
    painel.hidden = false;
    if (lupa) lupa.setAttribute('aria-expanded', 'true');
    // Na própria página do catálogo, começa com o termo que já está a ser pesquisado
    const atual = new URLSearchParams(location.search).get('busca');
    if (atual && !input.value) input.value = atual;
    requestAnimationFrame(() => {
      painel.classList.add('aberta');
      input.focus();
      input.select();
    });
  }

  function fechar(devolverFoco) {
    painel.classList.remove('aberta');
    painel.hidden = true;
    limparSugestoes();
    if (lupa) lupa.setAttribute('aria-expanded', 'false');
    if (devolverFoco && ultimoFoco && ultimoFoco.focus) ultimoFoco.focus();
  }

  function limparSugestoes() {
    lista.innerHTML = '';
    lista.hidden = true;
    input.setAttribute('aria-expanded', 'false');
    input.removeAttribute('aria-activedescendant');
    ativo = -1;
  }

  function linkCatalogo(termo) {
    return `${ctx}/catalogo?busca=${encodeURIComponent(termo)}`;
  }

  function mostrar(sugestoes, termo) {
    lista.innerHTML = '';
    ativo = -1;
    sugestoes.forEach((s, i) => {
      const li = document.createElement('li');
      li.setAttribute('role', 'option');
      li.id = `sugestao-${i}`;
      const a = document.createElement('a');
      a.href = `${ctx}/produto?id=${encodeURIComponent(s.id)}`;
      a.className = 'busca-sugestao';
      a.tabIndex = -1;

      const img = document.createElement('img');
      img.src = s.imagem;           // já vem com o context path
      img.alt = '';
      img.width = 36;
      img.height = 44;
      img.loading = 'lazy';
      img.onerror = () => { img.style.visibility = 'hidden'; };

      const texto = document.createElement('span');
      texto.className = 'busca-sugestao-texto';
      const nome = document.createElement('strong');
      nome.textContent = s.nome;          // textContent: nunca interpreta HTML
      const meta = document.createElement('small');
      meta.textContent = s.esgotado ? `${s.tipo} · Esgotado` : s.tipo;
      texto.append(nome, meta);

      const preco = document.createElement('span');
      preco.className = 'busca-sugestao-preco';
      preco.textContent = formatarPreco(s.preco);

      a.append(img, texto, preco);
      li.appendChild(a);
      lista.appendChild(li);
    });

    // Última linha: ver todos os resultados no catálogo
    const li = document.createElement('li');
    li.setAttribute('role', 'option');
    li.id = `sugestao-${sugestoes.length}`;
    const todos = document.createElement('a');
    todos.href = linkCatalogo(termo);
    todos.className = 'busca-sugestao busca-sugestao-todos';
    todos.tabIndex = -1;
    todos.textContent = sugestoes.length
      ? `Ver todos os resultados para “${termo}” →`
      : `Nenhuma sugestão para “${termo}”. Pesquisar no catálogo →`;
    li.appendChild(todos);
    lista.appendChild(li);

    lista.hidden = false;
    input.setAttribute('aria-expanded', 'true');
    status.textContent = sugestoes.length
      ? `${sugestoes.length} sugestões. Use as setas para escolher.`
      : 'Nenhuma sugestão.';
  }

  async function buscar(termo) {
    if (pedidoAtual) pedidoAtual.abort();
    pedidoAtual = new AbortController();
    try {
      const resp = await fetch(`${ctx}/busca/sugestoes?q=${encodeURIComponent(termo)}`, {
        headers: { Accept: 'application/json' },
        signal: pedidoAtual.signal
      });
      if (!resp.ok) throw new Error(resp.status);
      const dados = await resp.json();
      // Só mostra se o texto ainda for o mesmo (o cliente pode ter continuado a escrever)
      if (input.value.trim() === termo) mostrar(dados.sugestoes || [], termo);
    } catch (err) {
      if (err.name !== 'AbortError') limparSugestoes(); // sem sugestões, o "Buscar" continua a funcionar
    }
  }

  function destacar(indice) {
    const opcoes = lista.querySelectorAll('[role="option"]');
    if (!opcoes.length) return;
    ativo = (indice + opcoes.length) % opcoes.length;
    opcoes.forEach((o, i) => o.classList.toggle('ativa', i === ativo));
    input.setAttribute('aria-activedescendant', opcoes[ativo].id);
    opcoes[ativo].scrollIntoView({ block: 'nearest' });
  }

  gatilhos.forEach((g) => g.addEventListener('click', abrir));
  btnFechar.addEventListener('click', () => fechar(true));

  input.addEventListener('input', () => {
    clearTimeout(espera);
    const termo = input.value.trim();
    if (termo.length < 2) {
      if (pedidoAtual) pedidoAtual.abort();
      limparSugestoes();
      return;
    }
    // Espera o cliente parar de escrever (200 ms) para não fazer um pedido por letra
    espera = setTimeout(() => buscar(termo), 200);
  });

  input.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowDown') { e.preventDefault(); destacar(ativo + 1); }
    else if (e.key === 'ArrowUp') { e.preventDefault(); destacar(ativo - 1); }
    else if (e.key === 'Enter' && ativo >= 0) {
      const link = lista.querySelectorAll('[role="option"] a')[ativo];
      if (link) { e.preventDefault(); location.href = link.href; }
    }
  });

  form.addEventListener('submit', (e) => {
    if (!input.value.trim()) { e.preventDefault(); input.focus(); }
  });

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !painel.hidden) fechar(true);
  });

  // Clique fora do painel fecha-o
  document.addEventListener('click', (e) => {
    if (painel.hidden || painel.contains(e.target) || e.target.closest('[data-abrir-busca]')) return;
    fechar(false);
  });

  // Atalho "/" abre a pesquisa (como em muitas lojas), exceto quando já se está a escrever
  document.addEventListener('keydown', (e) => {
    if (e.key !== '/' || e.ctrlKey || e.metaKey || e.altKey) return;
    const alvo = e.target;
    if (alvo.isContentEditable || /^(INPUT|TEXTAREA|SELECT)$/.test(alvo.tagName)) return;
    abrir(e);
  });
})();
