// js/pages/auth.js
// Comportamentos partilhados pelas páginas de conta (login, cadastro,
// esqueci a senha, redefinir senha). Cada parte só atua se o elemento existir.
// Substitui js/pages/cadastro.js e js/pages/redefinir-senha.js.
document.addEventListener('DOMContentLoaded', () => {
  const ctx = window.CONTEXT_PATH || '';

  // ---------- 1) Mostrar / ocultar senha ----------
  document.querySelectorAll('.auth-toggle-senha').forEach((botao) => {
    const input = document.getElementById(botao.getAttribute('aria-controls'));
    const icone = botao.querySelector('img');
    if (!input) return;

    botao.addEventListener('click', () => {
      const mostrar = input.type === 'password';
      input.type = mostrar ? 'text' : 'password';
      botao.setAttribute('aria-pressed', String(mostrar));
      botao.setAttribute('aria-label', mostrar ? 'Ocultar senha' : 'Mostrar senha');
      if (icone) icone.src = `${ctx}/assets/icons/${mostrar ? 'eye' : 'eye-closed'}.svg`;
    });
  });

  // ---------- 2) Máscaras: telefone, CPF, CNPJ ----------
  const so = (v) => v.replace(/\D/g, '');
  const MASCARAS = {
    telefone: (v) => {
      v = so(v).slice(0, 11);
      if (v.length > 10) return v.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, '($1) $2-$3');
      if (v.length > 6) return v.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3');
      if (v.length > 2) return v.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
      return v.replace(/^(\d*)/, v ? '($1' : '');
    },
    cpf: (v) => so(v).slice(0, 11)
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2'),
    cnpj: (v) => so(v).slice(0, 14)
      .replace(/^(\d{2})(\d)/, '$1.$2')
      .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(/\.(\d{3})(\d)/, '.$1/$2')
      .replace(/(\d{4})(\d)/, '$1-$2')
  };

  document.querySelectorAll('[data-mascara]').forEach((input) => {
    const aplicar = MASCARAS[input.dataset.mascara];
    if (!aplicar) return;
    input.addEventListener('input', () => { input.value = aplicar(input.value); });
    if (input.value) input.value = aplicar(input.value);
  });

  // ---------- 3) Validações ----------
  function cpfValido(cpf) {
    cpf = so(cpf);
    if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;
    for (let t = 9; t < 11; t++) {
      let soma = 0;
      for (let i = 0; i < t; i++) soma += Number(cpf[i]) * (t + 1 - i);
      if (((soma * 10) % 11) % 10 !== Number(cpf[t])) return false;
    }
    return true;
  }

  function cnpjValido(cnpj) {
    cnpj = so(cnpj);
    if (cnpj.length !== 14 || /^(\d)\1+$/.test(cnpj)) return false;
    const calc = (base) => {
      let soma = 0;
      let peso = base.length - 7;
      for (let i = 0; i < base.length; i++) {
        soma += Number(base[i]) * peso--;
        if (peso < 2) peso = 9;
      }
      const r = soma % 11;
      return r < 2 ? 0 : 11 - r;
    };
    return calc(cnpj.slice(0, 12)) === Number(cnpj[12]) && calc(cnpj.slice(0, 13)) === Number(cnpj[13]);
  }

  // Devolve a mensagem de erro do campo (ou '' se estiver bom)
  function validarCampo(input) {
    if (input.closest('[hidden]') || input.disabled) return '';
    const v = input.value.trim();
    // data-nome traz o artigo: "o e-mail", "a senha" -> mensagens com o género certo
    const nome = input.dataset.nome || 'este campo';
    const Nome = nome.charAt(0).toUpperCase() + nome.slice(1);

    if (input.required && !v) return `Informe ${nome}.`;
    if (!v) return '';
    if (input.type === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v)) return 'Informe um e-mail válido.';
    if (input.minLength > 0 && v.length < input.minLength) return `${Nome} deve ter pelo menos ${input.minLength} caracteres.`;
    if (input.dataset.mascara === 'cpf' && !cpfValido(v)) return 'CPF inválido. Confira os números.';
    if (input.dataset.mascara === 'cnpj' && !cnpjValido(v)) return 'CNPJ inválido. Confira os números.';
    if (input.dataset.mascara === 'telefone' && so(v).length < 10) return 'Informe o telefone com DDD.';
    if (input.dataset.igualA) {
      const outro = document.getElementById(input.dataset.igualA);
      if (outro && outro.value !== input.value) return 'As senhas não coincidem.';
    }
    return '';
  }

  function mostrarErro(input, msg) {
    const alvo = document.getElementById(`${input.id}-erro`);
    input.setAttribute('aria-invalid', msg ? 'true' : 'false');
    if (alvo) alvo.textContent = msg;
  }

  document.querySelectorAll('form.auth-form').forEach((form) => {
    const campos = () => [...form.querySelectorAll('input:not([type="hidden"])')];

    // Valida ao sair do campo e limpa o erro enquanto se corrige
    campos().forEach((input) => {
      input.addEventListener('blur', () => { if (input.value) mostrarErro(input, validarCampo(input)); });
      input.addEventListener('input', () => {
        if (input.getAttribute('aria-invalid') === 'true') mostrarErro(input, validarCampo(input));
      });
    });

    form.addEventListener('submit', (e) => {
      let primeiroErro = null;
      campos().forEach((input) => {
        const msg = validarCampo(input);
        mostrarErro(input, msg);
        if (msg && !primeiroErro) primeiroErro = input;
      });

      if (primeiroErro) {
        e.preventDefault();
        primeiroErro.focus();
        return;
      }

      // Evita duplo clique / envio duplicado e mostra que está a processar
      const botao = form.querySelector('.auth-btn-primary');
      if (botao) {
        botao.disabled = true;
        botao.classList.add('is-loading');
        if (botao.dataset.textoEnviando) botao.querySelector('span').textContent = botao.dataset.textoEnviando;
      }
    });
  });

  // ---------- 4) Força da senha ----------
  document.querySelectorAll('[data-forca]').forEach((input) => {
    const barra = document.getElementById(input.dataset.forca);
    const texto = document.getElementById(`${input.dataset.forca}-texto`);
    if (!barra) return;
    const NOMES = ['', 'Fraca', 'Razoável', 'Boa', 'Forte'];

    input.addEventListener('input', () => {
      const v = input.value;
      let nivel = 0;
      if (v.length >= 8) nivel++;
      if (v.length >= 12) nivel++;
      if (/[A-Z]/.test(v) && /[a-z]/.test(v)) nivel++;
      if (/\d/.test(v) && /[^A-Za-z0-9]/.test(v)) nivel++;
      if (v && nivel === 0) nivel = 1;
      barra.dataset.nivel = String(nivel);
      if (texto) texto.textContent = v ? `Força da senha: ${NOMES[nivel]}` : 'Mínimo de 8 caracteres.';
    });
  });

  // ---------- 5) Abas Pessoa Física / Jurídica (cadastro) ----------
  const abas = [...document.querySelectorAll('.auth-tab')];
  const tipoCliente = document.getElementById('tipo_cliente');

  function ativarAba(aba, focar) {
    abas.forEach((a) => {
      const ativa = a === aba;
      a.setAttribute('aria-selected', String(ativa));
      a.tabIndex = ativa ? 0 : -1;
      const painel = document.getElementById(a.getAttribute('aria-controls'));
      if (painel) {
        painel.hidden = !ativa;
        // Campos do painel escondido não são obrigatórios nem enviados
        painel.querySelectorAll('input').forEach((input) => {
          input.disabled = !ativa;
          if (!ativa) mostrarErro(input, '');
        });
      }
    });
    if (tipoCliente) tipoCliente.value = aba.dataset.tipo;
    if (focar) aba.focus();
  }

  abas.forEach((aba, i) => {
    aba.addEventListener('click', () => ativarAba(aba, false));
    aba.addEventListener('keydown', (e) => {
      if (e.key !== 'ArrowRight' && e.key !== 'ArrowLeft') return;
      e.preventDefault();
      const proxima = abas[(i + (e.key === 'ArrowRight' ? 1 : abas.length - 1)) % abas.length];
      ativarAba(proxima, true);
    });
  });

  // Mantém a aba certa se o servidor devolveu o formulário com erro (ex.: PJ)
  if (abas.length) {
    const inicial = abas.find((a) => a.dataset.tipo === (tipoCliente && tipoCliente.value)) || abas[0];
    ativarAba(inicial, false);
  }

  // ---------- 6) Redirecionamento com contagem (após sucesso) ----------
  const contagem = document.querySelector('[data-redirecionar]');
  if (contagem) {
    let segundos = Number(contagem.dataset.segundos) || 5;
    const alvo = contagem.querySelector('[data-segundos-restantes]');
    const timer = setInterval(() => {
      segundos--;
      if (alvo) alvo.textContent = segundos;
      if (segundos <= 0) {
        clearInterval(timer);
        window.location.href = contagem.dataset.redirecionar;
      }
    }, 1000);
  }
});
