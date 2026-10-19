// js/pages/area-pessoal.js
// Área pessoal. Tudo o que muda dados (editar, senha, endereços, cancelar)
// são formulários POST normais tratados pelo ContaController; este arquivo só
// acrescenta conforto: navegação entre secções, máscaras, ViaCEP, confirmações
// e o botão "Comprar novamente".
document.addEventListener('DOMContentLoaded', () => {
  const ctx = window.CONTEXT_PATH || '';
  const secoes = [...document.querySelectorAll('.conta-secao')];
  const itensMenu = [...document.querySelectorAll('.conta-nav-item[data-secao]')];
  const so = (v) => String(v || '').replace(/\D/g, '');

  // Antes os pedidos ficavam guardados só no navegador; agora estão no banco.
  try { localStorage.removeItem('agnello_pedidos'); } catch (e) { /* sem localStorage */ }

  // ---------------------------------------------------------------- secções
  // Âncoras dentro de uma secção (ex.: #trocar-senha) abrem a secção e o bloco
  function resolverAlvo(hash) {
    const id = hash.replace(/^#/, '');
    const secao = secoes.find((s) => s.id === id);
    if (secao) return { secao, bloco: null };
    const bloco = id ? document.getElementById(id) : null;
    const dona = bloco && bloco.closest('.conta-secao');
    return dona ? { secao: dona, bloco } : { secao: secoes[0], bloco: null };
  }

  function mostrarSecao(hash, focar) {
    const { secao, bloco } = resolverAlvo(hash);
    secoes.forEach((s) => { s.hidden = s !== secao; });
    itensMenu.forEach((a) => {
      const ativo = a.dataset.secao === secao.id;
      a.classList.toggle('ativo', ativo);
      if (ativo) a.setAttribute('aria-current', 'page');
      else a.removeAttribute('aria-current');
    });
    if (bloco && bloco.tagName === 'DETAILS') {
      bloco.open = true;
      bloco.scrollIntoView({ block: 'start' });
    } else if (focar) {
      secao.focus({ preventScroll: true });
    }
  }

  document.querySelectorAll('[data-secao]').forEach((link) => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      history.replaceState(null, '', `#${link.dataset.secao}`);
      mostrarSecao(link.dataset.secao, true);
      if (window.innerWidth < 900) document.querySelector('.conta-conteudo').scrollIntoView({ behavior: 'smooth' });
    });
  });

  window.addEventListener('hashchange', () => mostrarSecao(location.hash, true));
  mostrarSecao(location.hash || 'visao-geral', false);

  // ---------------------------------------------------------------- aviso (flash)
  const flash = document.querySelector('.conta-flash');
  if (flash) {
    flash.querySelector('.conta-flash-fechar').addEventListener('click', () => flash.remove());
    // Um erro reabre o formulário onde aconteceu, para o cliente corrigir
    if (flash.classList.contains('conta-flash--erro')) {
      const mensagem = flash.textContent.toLowerCase();
      const id = location.hash === '#enderecos' ? 'form-novo-endereco'
        : mensagem.includes('senha') ? 'trocar-senha'
        : location.hash === '#dados' ? 'editar-dados' : null;
      const det = id && document.getElementById(id);
      if (det) det.open = true;
    } else {
      setTimeout(() => flash.classList.add('conta-flash--sumindo'), 6000);
    }
  }

  // ---------------------------------------------------------------- confirmações
  document.querySelectorAll('form[data-confirmar]').forEach((form) => {
    form.addEventListener('submit', (e) => {
      if (!window.confirm(form.dataset.confirmar)) e.preventDefault();
    });
  });

  // Evita duplo envio (duplo clique) em todos os formulários da página
  document.querySelectorAll('.conta-conteudo form').forEach((form) => {
    form.addEventListener('submit', (e) => {
      if (e.defaultPrevented) return;
      const botao = form.querySelector('button[type="submit"]');
      if (botao) {
        // espera um instante para o envio sair antes de desativar
        setTimeout(() => { botao.disabled = true; botao.setAttribute('aria-busy', 'true'); }, 0);
      }
    });
  });

  // ---------------------------------------------------------------- máscaras
  const mascaraTelefone = (v) => {
    const d = so(v).slice(0, 11);
    if (d.length <= 2) return d.length ? `(${d}` : '';
    if (d.length <= 6) return `(${d.slice(0, 2)}) ${d.slice(2)}`;
    if (d.length <= 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`;
    return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`;
  };
  const tel = document.getElementById('dados-telefone');
  if (tel) tel.addEventListener('input', () => { tel.value = mascaraTelefone(tel.value); });

  // ---------------------------------------------------------------- senha
  const formSenha = document.querySelector('.conta-form-senha');
  if (formSenha) {
    const nova = formSenha.querySelector('#senha-nova');
    const confirma = formSenha.querySelector('#senha-confirma');
    const validarConfirmacao = () => {
      confirma.setCustomValidity(confirma.value && confirma.value !== nova.value
        ? 'As senhas não coincidem.' : '');
    };
    nova.addEventListener('input', validarConfirmacao);
    confirma.addEventListener('input', validarConfirmacao);

    formSenha.querySelector('.conta-mostrar-senhas').addEventListener('change', (e) => {
      formSenha.querySelectorAll('input[type="password"], input[data-era-senha]').forEach((i) => {
        i.dataset.eraSenha = '1';
        i.type = e.target.checked ? 'text' : 'password';
      });
    });

    formSenha.addEventListener('submit', (e) => {
      validarConfirmacao();
      if (!formSenha.checkValidity()) {
        e.preventDefault();
        formSenha.reportValidity();
      }
    });
  }

  // ---------------------------------------------------------------- endereço (ViaCEP)
  const formEndereco = document.querySelector('.conta-form-endereco');
  if (formEndereco) {
    const cep = formEndereco.querySelector('#end-cep');
    const status = formEndereco.querySelector('#end-cep-status');
    const campo = (nome) => formEndereco.querySelector(`[name="${nome}"]`);

    cep.addEventListener('input', async () => {
      cep.value = so(cep.value).slice(0, 8).replace(/^(\d{5})(\d)/, '$1-$2');
      const digitos = so(cep.value);
      if (digitos.length !== 8) { status.textContent = ''; return; }
      status.textContent = 'Buscando endereço…';
      try {
        const r = await fetch(`https://viacep.com.br/ws/${digitos}/json/`);
        const d = await r.json();
        if (d.erro) throw new Error('cep');
        if (d.logradouro) campo('logradouro').value = d.logradouro;
        if (d.bairro) campo('bairro').value = d.bairro;
        if (d.localidade) campo('cidade').value = `${d.localidade} - ${d.uf}`;
        status.textContent = 'Endereço encontrado. Confira e informe o número.';
        campo('numero').focus();
      } catch (e) {
        status.textContent = 'Não encontramos este CEP. Preencha o endereço manualmente.';
      }
    });

    formEndereco.addEventListener('submit', (e) => {
      cep.setCustomValidity(so(cep.value).length === 8 ? '' : 'Informe um CEP com 8 dígitos.');
      if (!formEndereco.checkValidity()) {
        e.preventDefault();
        formEndereco.reportValidity();
      }
    });
  }

  // ---------------------------------------------------------------- favoritos
  // Ao tirar o coração aqui, o vinho sai da lista
  document.addEventListener('favorito:alterado', (e) => {
    if (e.detail.favorito) return;
    const item = document.querySelector(`[data-produto-card="${CSS.escape(e.detail.produto)}"]`);
    if (!item) return;
    item.classList.add('conta-favorito--saindo');
    setTimeout(() => {
      item.remove();
      const restantes = document.querySelectorAll('[data-produto-card]').length;
      const vazio = document.getElementById('favoritos-vazio');
      if (!restantes && vazio) vazio.hidden = false;
      const contador = document.querySelector('.conta-nav-contador');
      if (contador) {
        if (restantes) contador.textContent = restantes; else contador.remove();
      }
    }, 250);
  });

  // ---------------------------------------------------------------- comprar novamente
  const avisoRepetir = document.getElementById('aviso-repetir');
  const formatarPreco = (v) => `R$ ${Number(v).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

  function lerCarrinho() {
    try { return JSON.parse(localStorage.getItem('agnello_carrinho')) || []; } catch (e) { return []; }
  }

  function mostrarAvisoRepetir(texto) {
    if (!avisoRepetir) return;
    avisoRepetir.textContent = texto;
    avisoRepetir.hidden = !texto;
  }

  document.querySelectorAll('[data-repetir]').forEach((botao) => {
    botao.addEventListener('click', async () => {
      // Segundo clique depois do aviso de esgotados: segue para o checkout
      if (botao.dataset.pronto) { location.href = `${ctx}/checkout`; return; }
      const textoOriginal = botao.textContent;
      botao.disabled = true;
      botao.textContent = 'Preparando…';
      mostrarAvisoRepetir('');
      try {
        const resp = await fetch(`${ctx}/conta/pedidos/repetir?id=${encodeURIComponent(botao.dataset.repetir)}`, {
          headers: { Accept: 'application/json' }
        });
        const dados = await resp.json().catch(() => ({}));
        if (!resp.ok || !dados.sucesso) throw new Error(dados.mensagem || 'Não foi possível agora. Tente novamente.');

        const itens = dados.itens || [];
        const indisponiveis = dados.indisponiveis || [];
        if (!itens.length) {
          throw new Error(indisponiveis.length
            ? `Os vinhos deste pedido estão esgotados no momento: ${indisponiveis.join(', ')}.`
            : 'Os vinhos deste pedido já não estão disponíveis.');
        }

        // Junta ao carrinho atual (mesmo formato usado por carrinho.js)
        const carrinho = lerCarrinho();
        itens.forEach((novo) => {
          const atual = carrinho.find((c) => String(c.id) === String(novo.id));
          if (atual) {
            atual.quantidade = (atual.quantidade || 1) + novo.quantidade;
            atual.preco = formatarPreco(novo.preco); // preço de hoje
          } else {
            carrinho.push({
              id: novo.id,
              nome: novo.nome,
              preco: formatarPreco(novo.preco),
              origem: novo.origem,
              imagem: novo.imagem,
              quantidade: novo.quantidade
            });
          }
        });
        try {
          localStorage.setItem('agnello_carrinho', JSON.stringify(carrinho));
        } catch (e) {
          throw new Error('O seu navegador não deixou guardar o carrinho (modo privado?).');
        }

        if (indisponiveis.length) {
          // Avisa antes de seguir, para o cliente não ser surpreendido no checkout
          mostrarAvisoRepetir(`Adicionamos ao carrinho os vinhos disponíveis. Ficaram de fora (esgotados): ${indisponiveis.join(', ')}.`);
          botao.textContent = 'Ir para o checkout →';
          botao.disabled = false;
          botao.dataset.pronto = '1';
          return;
        }
        location.href = `${ctx}/checkout`;
      } catch (erro) {
        mostrarAvisoRepetir(erro.message);
        botao.disabled = false;
        botao.textContent = textoOriginal;
      }
    });
  });
});
