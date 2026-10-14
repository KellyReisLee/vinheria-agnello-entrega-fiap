// js/components/verificacao-idade.js
// Confirmação de maioridade (18+), obrigatória em lojas de bebidas alcoólicas.
// Aparece só na primeira visita; a resposta "sim" fica guardada no browser
// (localStorage "agnello_maior_idade"). Carregado pelo header.jsp.
(function () {
  const CHAVE = 'agnello_maior_idade';

  function jaConfirmou() {
    try { return localStorage.getItem(CHAVE) === 'sim'; } catch (e) { return false; }
  }
  if (jaConfirmou() || typeof HTMLDialogElement !== 'function') return;

  const ctx = window.CONTEXT_PATH ?? '';
  const dialogo = document.createElement('dialog');
  dialogo.className = 'idade-dialogo';
  dialogo.setAttribute('aria-labelledby', 'idade-titulo');
  dialogo.setAttribute('aria-describedby', 'idade-texto');
  dialogo.innerHTML = `
    <img class="idade-logo" src="${ctx}/assets/images/logo-no-bg.png" alt="" width="22" height="56">
    <p class="idade-marca">VINHERIA AGNELLO</p>
    <h2 id="idade-titulo">Você tem 18 anos ou mais?</h2>
    <p id="idade-texto">Este site vende bebidas alcoólicas. Para continuar, confirme que você é maior de idade.</p>
    <div class="idade-acoes">
      <button type="button" class="idade-btn idade-btn--sim" data-resposta="sim">Sim, tenho 18 anos ou mais</button>
      <button type="button" class="idade-btn" data-resposta="nao">Não</button>
    </div>
    <p class="idade-recusa" role="alert" hidden>Sentimos muito: a venda de bebidas alcoólicas é proibida para menores de 18 anos.
      Volte quando tiver idade para brindar conosco.</p>
    <p class="idade-legal">Ao entrar, você concorda com os <a href="${ctx}/termos">Termos de Uso</a> e a
      <a href="${ctx}/privacidade">Política de Privacidade</a>. Beba com moderação.</p>
  `;

  function abrir() {
    document.body.appendChild(dialogo);
    dialogo.showModal();
    document.body.classList.add('no-scroll');
  }

  // Esc não fecha: a pergunta precisa de resposta
  dialogo.addEventListener('cancel', (e) => e.preventDefault());

  dialogo.addEventListener('click', (e) => {
    const botao = e.target.closest('[data-resposta]');
    if (!botao) return;
    if (botao.dataset.resposta === 'sim') {
      try { localStorage.setItem(CHAVE, 'sim'); } catch (erro) { /* sem localStorage: pergunta na próxima página */ }
      dialogo.close();
      dialogo.remove();
      document.body.classList.remove('no-scroll');
    } else {
      dialogo.querySelector('.idade-recusa').hidden = false;
      dialogo.querySelector('.idade-acoes').hidden = true;
    }
  });

  document.readyState === 'loading' ? document.addEventListener('DOMContentLoaded', abrir) : abrir();
})();
