// js/components/modal-deletar-conta.js
// Exclusão de conta: abre a confirmação, exige que o e-mail seja digitado
// (sem colar), envia por fetch ao DeletarContaController e mostra o sucesso.
document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('modalDeletarConta');
  const modalSucesso = document.getElementById('modalSucessoDelecao');
  const abrir = document.getElementById('linkDeletarConta');
  const cancelar = document.getElementById('btnCancelarModal');
  const input = document.getElementById('emailConfirmacao');
  const aviso = document.getElementById('avisoCopyPaste');
  const form = modal ? modal.querySelector('.modal-form') : null;
  const confirmar = document.getElementById('btnConfirmarExclusao');
  if (!modal || !form || !input) return;

  const esperado = (input.dataset.emailEsperado || '').trim().toLowerCase();

  function mostrarAviso(msg) {
    aviso.textContent = msg;
    aviso.hidden = !msg;
  }

  // ---------- Abrir / fechar ----------
  if (abrir) {
    abrir.addEventListener('click', (e) => {
      e.preventDefault();
      input.value = '';
      input.classList.remove('email-match');
      confirmar.disabled = true;
      mostrarAviso('');
      modal.showModal();
      input.focus();
    });
  }
  if (cancelar) cancelar.addEventListener('click', () => modal.close());

  // Clique no fundo escuro fecha
  modal.addEventListener('click', (e) => {
    if (e.target === modal) modal.close();
  });

  // ---------- Digitar o e-mail (sem colar) ----------
  ['paste', 'drop'].forEach((evento) => {
    input.addEventListener(evento, (e) => {
      e.preventDefault();
      mostrarAviso('Por segurança, digite o e-mail em vez de colar.');
    });
  });

  input.addEventListener('input', () => {
    const igual = input.value.trim().toLowerCase() === esperado;
    input.classList.toggle('email-match', igual);
    confirmar.disabled = !igual;   // só ativa o botão quando o e-mail confere
    if (aviso.textContent && !aviso.textContent.startsWith('Por segurança')) mostrarAviso('');
  });

  // ---------- Enviar ----------
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (confirmar.disabled) return;

    confirmar.disabled = true;
    confirmar.textContent = 'Excluindo…';

    try {
      const resposta = await fetch(form.action, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(new FormData(form)).toString()
      });
      const texto = await resposta.text();
      if (!resposta.ok) throw new Error(texto || 'Não foi possível excluir a conta.');

      modal.close();
      try { localStorage.clear(); sessionStorage.clear(); } catch (err) { /* ignora */ }
      modalSucesso.showModal();

      // Contagem e volta para a página inicial (já sem sessão)
      let s = 4;
      const el = document.getElementById('segundos-saida');
      const timer = setInterval(() => {
        s--;
        if (el) el.textContent = s;
        if (s <= 0) {
          clearInterval(timer);
          window.location.href = `${window.CONTEXT_PATH || ''}/home`;
        }
      }, 1000);
    } catch (erro) {
      mostrarAviso(`Não foi possível excluir a conta: ${erro.message}`);
      confirmar.disabled = false;
      confirmar.textContent = 'Excluir conta';
    }
  });

  // O modal de sucesso não deve fechar com Esc (a página vai mudar)
  if (modalSucesso) modalSucesso.addEventListener('cancel', (e) => e.preventDefault());
});
