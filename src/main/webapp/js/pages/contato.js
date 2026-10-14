// js/pages/contato.js
// Valida o formulário de contato e, por enquanto, abre o programa de e-mail do
// cliente com a mensagem já escrita (mailto:). Quando existir um ContatoController,
// troque a função enviar() por um fetch POST para o servidor.
(function () {
  const form = document.getElementById('form-contato');
  if (!form) return;

  const mensagem = document.getElementById('contato-mensagem');
  const contador = document.getElementById('contato-contador');
  const sucesso = document.getElementById('contato-sucesso');
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
  function enviar(dados) {
    const assunto = `[Site] ${dados.assunto}${dados.pedido ? ' — pedido ' + dados.pedido : ''}`;
    const corpo = `${dados.mensagem}\n\n—\n${dados.nome}\n${dados.email}`;
    const destino = form.dataset.destino;
    window.location.href = `mailto:${destino}?subject=${encodeURIComponent(assunto)}&body=${encodeURIComponent(corpo)}`;

    sucesso.hidden = false;
    sucesso.innerHTML = '';
    const forte = document.createElement('strong');
    forte.textContent = 'Mensagem pronta! ';
    sucesso.append(forte,
      `Abrimos o seu programa de e-mail com o texto preenchido; é só clicar em enviar. Se nada abriu, escreva para ${destino}.`);
  }

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const invalidos = obrigatorios.filter((campo) => !validarCampo(campo));
    if (invalidos.length) {
      invalidos[0].focus();
      return;
    }
    const dados = Object.fromEntries(new FormData(form).entries());
    Object.keys(dados).forEach((k) => { dados[k] = String(dados[k]).trim(); });
    enviar(dados);
  });
})();
