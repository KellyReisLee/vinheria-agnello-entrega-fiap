// js/components/favoritos.js
// Coração dos favoritos (catálogo, página do vinho e área pessoal).
// Cada botão .fav-btn tem data-produto="ID" e aria-pressed="true|false".
// O pedido vai para POST /favoritos; sem sessão, leva ao login e volta para cá.
(function () {
  const ctx = window.CONTEXT_PATH || '';

  // Aviso discreto no canto do tela (lido também por leitores de tela)
  let aviso;
  let temporizador;
  function avisar(texto) {
    if (!aviso) {
      aviso = document.createElement('div');
      aviso.className = 'fav-aviso';
      aviso.setAttribute('role', 'status');
      document.body.appendChild(aviso);
    }
    aviso.textContent = texto;
    aviso.classList.add('visivel');
    clearTimeout(temporizador);
    temporizador = setTimeout(() => aviso.classList.remove('visivel'), 2600);
  }

  function pintar(botao, favorito) {
    botao.classList.toggle('is-favorito', favorito);
    botao.setAttribute('aria-pressed', String(favorito));
    const nome = botao.dataset.nome;
    if (nome) {
      botao.setAttribute('aria-label', favorito ? `Remover ${nome} dos favoritos` : `Guardar ${nome} nos favoritos`);
    }
    const texto = botao.querySelector('.fav-texto');
    if (texto) texto.textContent = favorito ? 'Nos favoritos' : 'Guardar nos favoritos';
  }

  function irParaLogin() {
    const aqui = location.pathname.slice(ctx.length) + location.search;
    location.href = `${ctx}/login?voltar=${encodeURIComponent(aqui)}`;
  }

  async function alternar(botao) {
    const quer = botao.getAttribute('aria-pressed') !== 'true';
    // Muda logo no tela (resposta "otimista"); desfaz se o servidor recusar
    pintar(botao, quer);
    botao.setAttribute('aria-busy', 'true');
    if (quer) {
      botao.classList.remove('is-animando');
      void botao.offsetWidth;
      botao.classList.add('is-animando');
    }

    try {
      const resp = await fetch(`${ctx}/favoritos`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'X-Requested-With': 'fetch' // proteção CSRF (ver ProtecaoCsrf.java)
        },
        body: new URLSearchParams({ produto_id: botao.dataset.produto, favorito: String(quer) }).toString()
      });
      const dados = await resp.json().catch(() => ({}));

      if (resp.status === 401 || dados.login) {
        pintar(botao, !quer);
        avisar('Entre na sua conta para guardar favoritos…');
        setTimeout(irParaLogin, 900);
        return;
      }
      if (!resp.ok || !dados.sucesso) throw new Error(dados.mensagem || 'erro');

      pintar(botao, dados.favorito);
      avisar(dados.favorito ? 'Guardado nos seus favoritos ♥' : 'Removido dos favoritos');
      document.dispatchEvent(new CustomEvent('favorito:alterado', {
        detail: { produto: botao.dataset.produto, favorito: dados.favorito, botao }
      }));
    } catch (e) {
      pintar(botao, !quer);
      avisar(e.message && e.message !== 'erro' ? e.message : 'Não foi possível agora. Tente novamente.');
    } finally {
      botao.removeAttribute('aria-busy');
    }
  }

  // Um único "ouvinte" para a página toda (funciona também com cards criados depois)
  document.addEventListener('click', (e) => {
    const botao = e.target.closest('.fav-btn[data-produto]');
    if (!botao || botao.getAttribute('aria-busy') === 'true') return;
    e.preventDefault();
    e.stopPropagation();
    alternar(botao);
  });
})();
