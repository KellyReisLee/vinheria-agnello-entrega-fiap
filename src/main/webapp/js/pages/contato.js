// js/pages/contato.js
// Valida o formulário de contato e envia para o servidor (POST /contato →
// ContatoController), que grava a mensagem e avisa a equipe por e-mail.
(function () {
  const form = document.getElementById('form-contato');
  if (!form) return;

  const mensagem = document.getElementById('contato-mensagem');
  const contador = document.getElementById('contato-contador');
  const sucesso = document.getElementById('contato-sucesso');
  const caixaErro = document.getElementById('contato-erro');
  const botao = document.getElementById('contato-enviar');
  const EMAIL_OK = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

  // ---------- Mensagens de erro junto a cada campo ----------
  function idErro(campo) {
    return campo.id + '-erro';
  }

  function mostrarErro(campo, texto) {
    let erro = document.getElementById(idErro(campo));
    if (!erro) {
      erro = document.createElement('span');
      erro.className = 'form-erro';
      erro.id = idErro(campo);
      // No checkbox o erro vai depois da <label> inteira
      const ancora = campo.type === 'checkbox' ? campo.closest('label') : campo;
      ancora.insertAdjacentElement('afterend', erro);
    }
    erro.textContent = texto;
    campo.setAttribute('aria-invalid', 'true');
    const descritos = (campo.getAttribute('aria-describedby') || '').split(' ').filter(Boolean);
    if (!descritos.includes(erro.id)) campo.setAttribute('aria-describedby', [...descritos, erro.id].join(' '));
  }

  function limparErro(campo) {
    const erro = document.getElementById(idErro(campo));
    if (erro) erro.remove();
    campo.removeAttribute('aria-invalid');
    const descritos = (campo.getAttribute('aria-describedby') || '').split(' ')
      .filter((id) => id && id !== idErro(campo));
    descritos.length ? campo.setAttribute('aria-describedby', descritos.join(' '))
      : campo.removeAttribute('aria-describedby');
  }

  function validarCampo(campo) {
    const valor = campo.type === 'checkbox' ? campo.checked : campo.value.trim();
    const nome = campo.dataset.nome || 'este campo';

    if (campo.required && !valor) {
      mostrarErro(campo, campo.type === 'checkbox' ? `Confirme ${nome}.` : `Informe ${nome}.`);
      return false;
    }
    if (campo.type === 'email' && valor && !EMAIL_OK.test(valor)) {
      mostrarErro(campo, 'Informe um e-mail válido, como nome@exemplo.com.');
      return false;
    }
    if (campo.minLength > 0 && valor && valor.length < campo.minLength) {
      mostrarErro(campo, `Escreva pelo menos ${campo.minLength} caracteres.`);
      return false;
    }
    limparErro(campo);
    return true;
  }

  const obrigatorios = [...form.querySelectorAll('[required]')];

  // Valida ao sair do campo; depois do 1.º erro, também enquanto escreve
  obrigatorios.forEach((campo) => {
    campo.addEventListener('blur', () => { if (campo.value || campo.type === 'checkbox') validarCampo(campo); });
    campo.addEventListener('input', () => { if (campo.hasAttribute('aria-invalid')) validarCampo(campo); });
    campo.addEventListener('change', () => { if (campo.hasAttribute('aria-invalid')) validarCampo(campo); });
  });

  // ---------- Contador de caracteres ----------
  function atualizarContador() {
    contador.textContent = `${mensagem.value.length}/${mensagem.maxLength}`;
  }
  mensagem.addEventListener('input', atualizarContador);
  atualizarContador();

  // ---------- Envio ----------
  async function enviar() {
    caixaErro.hidden = true;
    sucesso.hidden = true;
    botao.disabled = true;
    const textoBotao = botao.textContent;
    botao.textContent = 'Enviando…';

    try {
      const resposta = await fetch(form.action, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'X-Requested-With': 'fetch' // proteção CSRF (ver ProtecaoCsrf.java)
        },
        body: new URLSearchParams(new FormData(form)).toString()
      });
      const dados = await resposta.json().catch(() => ({}));

      if (resposta.ok && dados.sucesso) {
        sucesso.innerHTML = '';
        const forte = document.createElement('strong');
        forte.textContent = 'Mensagem enviada! ';
        sucesso.append(forte, 'Respondemos no seu e-mail em até 1 dia útil.');
        sucesso.hidden = false;
        form.querySelectorAll('textarea, #contato-pedido, select').forEach((c) => { c.value = ''; });
        form.querySelector('#contato-aceite').checked = false;
        atualizarContador();
        sucesso.setAttribute('tabindex', '-1');
        sucesso.focus();
      } else {
        caixaErro.textContent = dados.mensagem || 'Não foi possível enviar agora. Tente novamente.';
        caixaErro.hidden = false;
      }
    } catch (e) {
      caixaErro.textContent = 'Sem ligação com o servidor. Verifique a internet e tente novamente.';
      caixaErro.hidden = false;
    }
    botao.disabled = false;
    botao.textContent = textoBotao;
  }

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const invalidos = obrigatorios.filter((campo) => !validarCampo(campo));
    if (invalidos.length) {
      invalidos[0].focus();
      return;
    }
    enviar();
  });
})();
