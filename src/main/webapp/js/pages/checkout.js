// js/pages/checkout.js
// Checkout: resumo do carrinho, identificação (AJAX ao CheckoutController),
// endereço com preenchimento pelo CEP, envio, pagamento e confirmação do pedido.
//
// Nota: ainda não há back-end de pedidos. O pedido confirmado fica guardado no
// browser (localStorage 'agnello_pedidos') e aparece em "Meus pedidos" na área pessoal.
document.addEventListener('DOMContentLoaded', () => {
  const ctx = window.CONTEXT_PATH || '';
  const urlCheckout = `${ctx}/checkout`;
  const $ = (id) => document.getElementById(id);

  const estado = $('checkout-estado');
  const logado = estado && estado.dataset.logado === 'true';
  const emailSessao = estado ? estado.dataset.email : '';

  // ---------- Utilitários ----------
  const so = (v) => String(v || '').replace(/\D/g, '');
  const brl = (n) => n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  const precoParaNumero = (p) => (typeof p === 'number' ? p : parseFloat(String(p).replace(/[^\d,]/g, '').replace(',', '.')) || 0);
  const escapar = (t) => String(t ?? '').replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));

  function mostrarAlerta(el, msg) {
    if (!el) return;
    el.textContent = msg;
    el.hidden = !msg;
  }

  async function postar(dados) {
    const resposta = await fetch(urlCheckout, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams(dados).toString()
    });
    if (!resposta.ok) throw new Error(`HTTP ${resposta.status}`);
    return resposta.json();
  }

  // ======================================================
  // 1) CARRINHO E TOTAIS
  // ======================================================
  let itens = [];
  try {
    itens = JSON.parse(localStorage.getItem('agnello_carrinho')) || [];
  } catch (e) {
    itens = [];
  }

  // Antes, com o carrinho vazio, aparecia um vinho de exemplo (R$ 289,00).
  // Agora mostramos o estado "carrinho vazio".
  if (!itens.length) {
    $('checkout-conteudo').hidden = true;
    $('checkout-vazio').hidden = false;
    return;
  }

  const listaResumo = $('summary-items');
  const subtotal = itens.reduce((s, i) => s + precoParaNumero(i.preco) * (i.quantidade || 1), 0);

  listaResumo.innerHTML = itens.map((i) => {
    const qtd = i.quantidade || 1;
    return `
      <li class="summary-item">
        <span class="summary-thumb">
          ${i.imagem ? `<img src="${escapar(i.imagem)}" alt="" width="40" height="60" onerror="this.remove()">` : ''}
          <span class="summary-qtd" aria-label="Quantidade">${qtd}</span>
        </span>
        <span class="summary-nome">${escapar(i.nome)}</span>
        <span class="summary-valor">${brl(precoParaNumero(i.preco) * qtd)}</span>
      </li>`;
  }).join('');

  function formaPagamento() {
    const ativa = document.querySelector('.pay-tab[aria-selected="true"]');
    return ativa ? ativa.dataset.target : 'pix';
  }

  function calcularTotais() {
    const envio = document.querySelector('input[name="shipping"]:checked');
    const frete = envio ? parseFloat(envio.value) || 0 : 0;
    const desconto = formaPagamento() === 'pix' ? subtotal * 0.05 : 0;
    return { subtotal, frete, desconto, total: subtotal + frete - desconto, envio: envio ? envio.dataset.nome : '' };
  }

  function atualizarTotais() {
    const t = calcularTotais();
    $('total-subtotal').textContent = brl(t.subtotal);
    $('total-frete').textContent = t.frete === 0 ? 'Grátis' : brl(t.frete);
    $('row-discount').hidden = t.desconto === 0;
    $('total-desconto').textContent = `− ${brl(t.desconto)}`;
    $('total-final').textContent = brl(t.total);
    atualizarParcelas(t.total);
  }

  // Parcelas do cartão (sem juros até 6x, mínimo R$ 50 por parcela)
  function atualizarParcelas(total) {
    const select = $('card-parcelas');
    if (!select) return;
    const anterior = select.value;
    const max = Math.max(1, Math.min(6, Math.floor(total / 50)));
    select.innerHTML = Array.from({ length: max }, (_, k) => {
      const n = k + 1;
      return `<option value="${n}">${n}x de ${brl(total / n)} sem juros</option>`;
    }).join('');
    if (anterior && Number(anterior) <= max) select.value = anterior;
  }

  document.querySelectorAll('input[name="shipping"]').forEach((r) => r.addEventListener('change', atualizarTotais));

  // Abas de pagamento (com setas do teclado)
  const abasPagamento = [...document.querySelectorAll('.pay-tab')];
  function ativarPagamento(aba, focar) {
    abasPagamento.forEach((a) => {
      const ativa = a === aba;
      a.setAttribute('aria-selected', String(ativa));
      a.tabIndex = ativa ? 0 : -1;
      $(a.dataset.target).hidden = !ativa;
    });
    if (focar) aba.focus();
    atualizarTotais();
  }
  abasPagamento.forEach((aba, i) => {
    aba.addEventListener('click', () => ativarPagamento(aba, false));
    aba.addEventListener('keydown', (e) => {
      if (e.key !== 'ArrowRight' && e.key !== 'ArrowLeft') return;
      e.preventDefault();
      const n = abasPagamento.length;
      ativarPagamento(abasPagamento[(i + (e.key === 'ArrowRight' ? 1 : n - 1)) % n], true);
    });
  });

  atualizarTotais();

  // ======================================================
  // 2) MÁSCARAS
  // ======================================================
  const MASCARAS = {
    cep: (v) => so(v).slice(0, 8).replace(/^(\d{5})(\d)/, '$1-$2'),
    cpf: (v) => so(v).slice(0, 11).replace(/(\d{3})(\d)/, '$1.$2').replace(/(\d{3})(\d)/, '$1.$2').replace(/(\d{3})(\d{1,2})$/, '$1-$2'),
    cnpj: (v) => so(v).slice(0, 14).replace(/^(\d{2})(\d)/, '$1.$2').replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3').replace(/\.(\d{3})(\d)/, '.$1/$2').replace(/(\d{4})(\d)/, '$1-$2'),
    telefone: (v) => {
      v = so(v).slice(0, 11);
      if (v.length > 10) return v.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, '($1) $2-$3');
      if (v.length > 6) return v.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3');
      if (v.length > 2) return v.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
      return v;
    },
    cartao: (v) => so(v).slice(0, 16).replace(/(\d{4})(?=\d)/g, '$1 '),
    validade: (v) => so(v).slice(0, 4).replace(/^(\d{2})(\d)/, '$1/$2')
  };
  document.querySelectorAll('[data-mascara]').forEach((input) => {
    const fn = MASCARAS[input.dataset.mascara];
    if (fn) input.addEventListener('input', () => { input.value = fn(input.value); });
  });
  const cvv = $('card-cvv');
  if (cvv) cvv.addEventListener('input', () => { cvv.value = so(cvv.value).slice(0, 4); });

  // ======================================================
  // 3) ENDEREÇO: preenchimento automático pelo CEP (ViaCEP)
  // ======================================================
  const cep = $('cep');
  const cepStatus = $('cep-status');
  if (cep) {
    cep.addEventListener('input', async () => {
      const digitos = so(cep.value);
      if (digitos.length !== 8) { cepStatus.textContent = ''; return; }
      cepStatus.textContent = 'Buscando endereço…';
      try {
        const r = await fetch(`https://viacep.com.br/ws/${digitos}/json/`);
        const d = await r.json();
        if (d.erro) throw new Error('CEP não encontrado');
        $('logradouro').value = d.logradouro || $('logradouro').value;
        $('bairro').value = d.bairro || $('bairro').value;
        $('cidade').value = d.localidade ? `${d.localidade} - ${d.uf}` : $('cidade').value;
        cepStatus.textContent = 'Endereço encontrado. Confira e informe o número.';
        $('numero').focus();
      } catch (e) {
        cepStatus.textContent = 'Não encontramos este CEP. Preencha o endereço manualmente.';
      }
    });
  }

  // ======================================================
  // 4) IDENTIFICAÇÃO (só quando não há sessão)
  // ======================================================
  const emailInput = $('checkout-email');
  const btnEmail = $('btn-check-email');
  const grupoSenha = $('password-group');
  const grupoCadastro = $('register-group');
  const caixaErroEmail = $('email-error-box');

  function alterarEmail() {
    emailInput.readOnly = false;
    grupoSenha.hidden = true;
    grupoCadastro.hidden = true;
    btnEmail.textContent = 'Continuar';
    emailInput.focus();
  }

  if (btnEmail && emailInput) {
    const verificarEmail = async () => {
      if (emailInput.readOnly) { alterarEmail(); return; }
      const email = emailInput.value.trim();
      mostrarAlerta(caixaErroEmail, '');
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email)) {
        mostrarAlerta(caixaErroEmail, 'Informe um e-mail válido.');
        emailInput.focus();
        return;
      }
      btnEmail.disabled = true;
      try {
        const dados = await postar({ email, tipo_cliente: $('tipo_cliente').value });
        emailInput.readOnly = true;
        btnEmail.textContent = 'Alterar';
        grupoSenha.hidden = !dados.existe;
        grupoCadastro.hidden = !!dados.existe;
        (dados.existe ? $('checkout-senha') : $('novo-nome')).focus();
      } catch (e) {
        mostrarAlerta(caixaErroEmail, 'Não foi possível verificar o e-mail. Tente novamente.');
      } finally {
        btnEmail.disabled = false;
      }
    };
    btnEmail.addEventListener('click', verificarEmail);
    emailInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') { e.preventDefault(); verificarEmail(); }
    });
  }

  // Login de cliente existente
  const btnEntrar = $('btn-entrar-senha');
  if (btnEntrar) {
    const entrar = async () => {
      const senha = $('checkout-senha').value;
      const caixa = $('senha-error-box');
      mostrarAlerta(caixa, '');
      if (!senha) { mostrarAlerta(caixa, 'Digite a sua senha.'); $('checkout-senha').focus(); return; }
      btnEntrar.disabled = true;
      btnEntrar.textContent = 'Entrando…';
      try {
        const dados = await postar({ email: emailInput.value.trim(), senha });
        if (dados.sucesso) {
          btnEntrar.textContent = 'Conectado ✓';
          location.reload(); // recarrega já com a sessão (o servidor passa a enviar usuarioLogado)
          return;
        }
        // Mostra a mensagem real do servidor (ex.: "conta ainda não ativada")
        mostrarAlerta(caixa, dados.mensagem || 'E-mail ou senha inválidos.');
        $('checkout-senha').value = '';
        $('checkout-senha').focus();
      } catch (e) {
        mostrarAlerta(caixa, 'Não foi possível validar a senha. Tente novamente.');
      }
      btnEntrar.disabled = false;
      btnEntrar.textContent = 'Entrar';
    };
    btnEntrar.addEventListener('click', entrar);
    $('checkout-senha').addEventListener('keydown', (e) => {
      if (e.key === 'Enter') { e.preventDefault(); entrar(); }
    });
  }

  // Abas PF / PJ do cadastro rápido
  const abasTipo = [...document.querySelectorAll('.tipo-tab')];
  abasTipo.forEach((aba) => {
    aba.addEventListener('click', () => {
      abasTipo.forEach((a) => {
        const ativa = a === aba;
        a.setAttribute('aria-selected', String(ativa));
        a.tabIndex = ativa ? 0 : -1;
        const painel = $(a.getAttribute('aria-controls'));
        painel.hidden = !ativa;
        // Campos desativados não são enviados (o servidor decide PF/PJ pelo que recebe)
        painel.querySelectorAll('input').forEach((i) => { i.disabled = !ativa; });
      });
      $('tipo_cliente').value = aba.dataset.tipo;
    });
  });

  // Cadastro rápido
  const btnCadastro = $('btn-finalizar-cadastro');
  if (btnCadastro) {
    btnCadastro.addEventListener('click', async () => {
      const erro = $('register-error-box');
      const msgErro = $('register-error-msg');
      const falhar = (m, campo) => {
        msgErro.textContent = m;
        erro.hidden = false;
        if (campo) campo.focus();
      };
      erro.hidden = true;

      const pf = $('tipo_cliente').value === 'PF';
      if (pf && !$('novo-nome').value.trim()) return falhar('Informe o seu nome.', $('novo-nome'));
      if (pf && so($('novo-cpf').value).length !== 11) return falhar('Informe um CPF com 11 dígitos.', $('novo-cpf'));
      if (!pf && !$('novo-razao').value.trim()) return falhar('Informe a razão social.', $('novo-razao'));
      if (!pf && so($('novo-cnpj').value).length !== 14) return falhar('Informe um CNPJ com 14 dígitos.', $('novo-cnpj'));
      if (so($('novo-telefone').value).length < 10) return falhar('Informe o telefone com DDD.', $('novo-telefone'));
      if ($('novo-senha').value.length < 6) return falhar('A senha deve ter pelo menos 6 caracteres.', $('novo-senha'));

      const dados = new URLSearchParams(new FormData($('form-identification')));
      dados.set('senha', $('novo-senha').value);

      btnCadastro.disabled = true;
      btnCadastro.textContent = 'Criando conta…';
      try {
        const r = await postar(dados);
        if (r.sucesso) {
          $('register-success-box').hidden = false;
          setTimeout(() => location.reload(), 1200);
          return;
        }
        falhar(r.mensagem || 'Não foi possível criar a conta. Verifique os dados.');
      } catch (e) {
        falhar('Erro de conexão com o servidor. Tente novamente.');
      }
      btnCadastro.disabled = false;
      btnCadastro.textContent = 'Criar conta e continuar';
    });
  }

  // ======================================================
  // 5) CONCLUIR PEDIDO
  // ======================================================
  function luhn(numero) {
    let soma = 0;
    let dobro = false;
    for (let i = numero.length - 1; i >= 0; i--) {
      let d = Number(numero[i]);
      if (dobro) { d *= 2; if (d > 9) d -= 9; }
      soma += d;
      dobro = !dobro;
    }
    return numero.length >= 13 && soma % 10 === 0;
  }

  function validadeOk(v) {
    const [mm, aa] = v.split('/').map(Number);
    if (!mm || mm > 12 || aa === undefined || Number.isNaN(aa)) return false;
    const fim = new Date(2000 + aa, mm, 0, 23, 59, 59);
    return fim >= new Date();
  }

  // Devolve [mensagem, campo] do primeiro problema, ou null se está tudo certo
  function validarPedido() {
    if (!logado) return ['Identifique-se no passo 1 (entre ou crie a sua conta) para concluir.', emailInput];

    const obrigatorios = [
      ['cep', 'Informe o CEP.', (v) => so(v).length === 8],
      ['logradouro', 'Informe a rua.'],
      ['numero', 'Informe o número.'],
      ['bairro', 'Informe o bairro.'],
      ['cidade', 'Informe a cidade.']
    ];
    for (const [id, msg, teste] of obrigatorios) {
      const v = $(id).value.trim();
      if (!v || (teste && !teste(v))) return [msg, $(id)];
    }

    if (formaPagamento() === 'credit') {
      if (!luhn(so($('card-num').value))) return ['Número do cartão inválido.', $('card-num')];
      if (!validadeOk($('card-exp').value)) return ['Validade do cartão inválida ou expirada.', $('card-exp')];
      if (so($('card-cvv').value).length < 3) return ['Informe o CVV (3 ou 4 dígitos).', $('card-cvv')];
      if ($('card-name').value.trim().length < 3) return ['Informe o nome impresso no cartão.', $('card-name')];
    }
    return null;
  }

  const btnConcluir = $('btn-concluir');
  btnConcluir.addEventListener('click', () => {
    const erroPedido = $('pedido-erro');
    const problema = validarPedido();
    if (problema) {
      mostrarAlerta(erroPedido, problema[0]);
      if (problema[1]) {
        problema[1].focus({ preventScroll: true });
        problema[1].scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      return;
    }
    mostrarAlerta(erroPedido, '');

    btnConcluir.disabled = true;
    btnConcluir.querySelector('span').textContent = 'Processando…';

    // Pequena espera para dar feedback (aqui entraria a chamada ao back-end de pedidos)
    setTimeout(() => confirmarPedido(), 900);
  });

  function confirmarPedido() {
    const t = calcularTotais();
    const forma = formaPagamento();
    const numero = `AG-${Date.now().toString(36).toUpperCase().slice(-6)}`;
    const nomesPagamento = { pix: 'Pix', credit: `Cartão de crédito (${$('card-parcelas').value}x)`, boleto: 'Boleto bancário' };

    const pedido = {
      numero,
      data: new Date().toISOString(),
      email: emailSessao,
      itens: itens.map((i) => ({ nome: i.nome, quantidade: i.quantidade || 1, preco: precoParaNumero(i.preco), imagem: i.imagem })),
      ...t,
      pagamento: nomesPagamento[forma],
      endereco: `${$('logradouro').value}, ${$('numero').value}${$('complemento').value ? ` – ${$('complemento').value}` : ''} · ${$('bairro').value} · ${$('cidade').value} · CEP ${$('cep').value}`,
      status: forma === 'credit' ? 'Pagamento aprovado' : 'Aguardando pagamento'
    };

    try {
      const pedidos = JSON.parse(localStorage.getItem('agnello_pedidos')) || [];
      pedidos.unshift(pedido);
      localStorage.setItem('agnello_pedidos', JSON.stringify(pedidos));
      localStorage.removeItem('agnello_carrinho'); // esvazia o carrinho
    } catch (e) { /* sem localStorage: a confirmação aparece na mesma */ }

    // Instruções conforme a forma de pagamento (valores de demonstração)
    const codigoPix = `00020126580014BR.GOV.BCB.PIX0136agnello-${numero.toLowerCase()}5204000053039865406${t.total.toFixed(2)}5802BR`;
    const instrucoes = {
      pix: `
        <h2>Pague com Pix</h2>
        <p>Copie o código abaixo e cole na área Pix do seu banco. O pedido é liberado assim que o pagamento for confirmado.</p>
        <div class="pix-copia">
          <code id="pix-codigo">${codigoPix}</code>
          <button type="button" class="checkout-btn-secondary" id="btn-copiar-pix">Copiar código</button>
        </div>`,
      credit: `
        <h2>Pagamento aprovado</h2>
        <p>O pagamento no cartão foi aprovado. Você receberá um e-mail quando o pedido for enviado.</p>`,
      boleto: `
        <h2>Boleto gerado</h2>
        <p>O boleto vence em 2 dias úteis. O pedido é liberado após a compensação bancária.</p>`
    };

    const conf = $('checkout-confirmacao');
    conf.innerHTML = `
      <div class="confirmacao-icone" aria-hidden="true">✓</div>
      <span class="confirmacao-eyebrow">Pedido ${numero}</span>
      <h1 id="confirmacao-titulo">Obrigado! O seu pedido foi recebido.</h1>
      <p class="confirmacao-lead">Enviaremos as atualizações para <strong>${escapar(emailSessao)}</strong>.</p>

      <div class="confirmacao-pagamento">${instrucoes[forma]}</div>

      <div class="confirmacao-resumo">
        <ul>${pedido.itens.map((i) => `<li><span>${i.quantidade}× ${escapar(i.nome)}</span><span>${brl(i.preco * i.quantidade)}</span></li>`).join('')}</ul>
        <dl>
          <div><dt>Envio</dt><dd>${escapar(t.envio)} · ${t.frete === 0 ? 'Grátis' : brl(t.frete)}</dd></div>
          ${t.desconto ? `<div><dt>Desconto Pix</dt><dd>− ${brl(t.desconto)}</dd></div>` : ''}
          <div class="confirmacao-total"><dt>Total</dt><dd>${brl(t.total)}</dd></div>
          <div><dt>Entrega</dt><dd>${escapar(pedido.endereco)}</dd></div>
        </dl>
      </div>

      <div class="confirmacao-acoes">
        <a href="${ctx}/area-pessoal#pedidos" class="checkout-btn-primary">Ver meus pedidos</a>
        <a href="${ctx}/catalogo" class="checkout-btn-secondary">Continuar comprando</a>
      </div>`;

    $('checkout-conteudo').hidden = true;
    conf.hidden = false;
    window.scrollTo({ top: 0, behavior: 'smooth' });
    conf.focus({ preventScroll: true });

    const btnCopiar = $('btn-copiar-pix');
    if (btnCopiar) {
      btnCopiar.addEventListener('click', async () => {
        try {
          await navigator.clipboard.writeText(codigoPix);
          btnCopiar.textContent = 'Copiado ✓';
        } catch (e) {
          // Sem permissão de clipboard: seleciona o texto para a pessoa copiar
          const range = document.createRange();
          range.selectNodeContents($('pix-codigo'));
          const sel = window.getSelection();
          sel.removeAllRanges();
          sel.addRange(range);
          btnCopiar.textContent = 'Selecionado — copie com Ctrl/Cmd+C';
        }
      });
    }
  }
});
