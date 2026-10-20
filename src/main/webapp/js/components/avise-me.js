// js/components/avise-me.js
// "Avise-me quando chegar" na página de um vinho esgotado.
// Envia o e-mail para POST /avise-me (AvisoEstoqueController). Quando o vinho
// voltar ao estoque, a loja manda um único e-mail avisando.
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('avise-me');
  if (!form) return;

  const ctx = window.CONTEXT_PATH || '';
  const email = form.querySelector('input[name="email"]');
  const botao = form.querySelector('button[type="submit"]');
  const status = form.querySelector('.pdp-avise-status');

  function mostrar(texto, tipo) {
    status.textContent = texto;
    status.hidden = !texto;
    status.className = `pdp-avise-status${tipo ? ` pdp-avise-status--${tipo}` : ''}`;
  }

  // Vindo do catálogo (/produto?id=…#avise-me): leva o cliente direto ao campo
  if (location.hash === '#avise-me') {
    form.scrollIntoView({ block: 'center' });
    email.focus({ preventScroll: true });
  }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!email.value.trim() || !email.checkValidity()) {
      mostrar('Informe um e-mail válido, como nome@exemplo.com.', 'erro');
      email.focus();
      return;
    }
    botao.disabled = true;
    const textoBotao = botao.textContent;
    botao.textContent = 'Enviando…';
    try {
      const r = await fetch(`${ctx}/avise-me`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'X-Requested-With': 'fetch' // proteção CSRF (ver ProtecaoCsrf.java)
        },
        body: new URLSearchParams({
          produto_id: form.dataset.produto,
          email: email.value.trim(),
          site: form.querySelector('input[name="site"]').value
        }).toString()
      });
      const d = await r.json().catch(() => ({}));
      if (r.ok && d.sucesso) {
        form.querySelector('.pdp-avise-linha').hidden = true;
        mostrar(d.novo === false
          ? 'Você já está na lista: vamos avisar assim que este vinho chegar.'
          : 'Pronto! Vamos mandar um e-mail assim que este vinho voltar à adega.', 'ok');
        return;
      }
      mostrar(d.mensagem || 'Não foi possível agora. Tente novamente.', 'erro');
    } catch (err) {
      mostrar('Sem conexão com o servidor. Verifique a internet e tente novamente.', 'erro');
    }
    botao.disabled = false;
    botao.textContent = textoBotao;
  });
});
