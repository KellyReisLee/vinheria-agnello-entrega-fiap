// js/pages/checkout.js
// Checkout: resumo do carrinho, identificação (AJAX ao CheckoutController),
// endereço com preenchimento pelo CEP, envio, pagamento e envio do pedido ao
// servidor (POST /pedidos → PedidoController).
//
// Os valores mostrados aqui são só uma prévia: o servidor recalcula tudo com os
// preços do banco, e a confirmação mostra os valores que ele devolveu.
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

  // Na retirada na adega o endereço não é necessário: o formulário fica escondido
  function atualizarEnvio() {
    const envio = document.querySelector('input[name="shipping"]:checked');
    const retirada = !!envio && envio.dataset.tipo === 'RETIRADA';
    $('form-endereco').hidden = retirada;
    if ($('enderecos-salvos')) $('enderecos-salvos').hidden = retirada;
    $('aviso-retirada').hidden = !retirada;
    $('passo2').textContent = retirada ? 'Retirada na adega' : 'Endereço de entrega';
    atualizarTotais();
  }
  document.querySelectorAll('input[name="shipping"]').forEach((r) => r.addEventListener('change', atualizarEnvio));

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

  atualizarEnvio();

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
  // 3b) ENDEREÇOS GUARDADOS NA CONTA
  // Escolher um cartão preenche o formulário; "Usar outro endereço" limpa-o.
  // A opção "Guardar este endereço" só aparece quando se digita um endereço novo.
  // ======================================================
  const CAMPOS_ENDERECO = ['cep', 'logradouro', 'numero', 'complemento', 'bairro', 'cidade'];
  const opcoesSalvas = [...document.querySelectorAll('input[name="endereco-salvo"]')];
  const blocoGuardar = $('guardar-endereco');
  const checkGuardar = $('salvar-endereco');
  const campoApelido = $('campo-apelido');

  function atualizarGuardar() {
    const escolhido = opcoesSalvas.find((o) => o.checked);
    const novo = !escolhido || escolhido.value === 'novo';
    if (blocoGuardar) blocoGuardar.hidden = !novo;
    if (campoApelido) campoApelido.hidden = !(checkGuardar && checkGuardar.checked);
  }

  function usarEndereco(opcao) {
    if (opcao.value === 'novo') {
      CAMPOS_ENDERECO.forEach((id) => { $(id).value = ''; });
      if (cepStatus) cepStatus.textContent = '';
      $('cep').focus();
    } else {
      CAMPOS_ENDERECO.forEach((id) => { $(id).value = opcao.dataset[id] || ''; });
      if (cepStatus) cepStatus.textContent = '';
      // Remove avisos de erro que tenham ficado de uma tentativa anterior
      CAMPOS_ENDERECO.forEach((id) => $(id).removeAttribute('aria-invalid'));
    }
    atualizarGuardar();
  }

  opcoesSalvas.forEach((o) => o.addEventListener('change', () => usarEndereco(o)));
  if (checkGuardar) checkGuardar.addEventListener('change', atualizarGuardar);

  // Ao mudar à mão um campo de um endereço guardado, passa a ser "outro endereço"
  CAMPOS_ENDERECO.forEach((id) => {
    const campo = $(id);
    if (!campo) return;
    campo.addEventListener('input', () => {
      const escolhido = opcoesSalvas.find((o) => o.checked);
      if (escolhido && escolhido.value !== 'novo' && campo.value !== (escolhido.dataset[id] || '')) {
        const novo = opcoesSalvas.find((o) => o.value === 'novo');
        if (novo) novo.checked = true;
        atualizarGuardar();
      }
    });
  });

  // Ao abrir: o endereço principal já vem escolhido e preenchido
  const inicial = opcoesSalvas.find((o) => o.checked);
  if (inicial && !$('cep').value) usarEndereco(inicial);
  else atualizarGuardar();

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
      if ($('novo-senha').value.length < 8) return falhar('A senha deve ter pelo menos 8 caracteres.', $('novo-senha'));

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

    const entrega = document.querySelector('input[name="shipping"]:checked');
    const ehRetirada = entrega && entrega.dataset.tipo === 'RETIRADA';
    const obrigatorios = ehRetirada ? [] : [
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
    if (itens.some((i) => !Number.isInteger(Number(i.id)) || Number(i.id) <= 0)) {
      return ['Há um vinho antigo no carrinho que já não está disponível. Remova-o e adicione de novo pelo catálogo.', null];
    }
    return null;
  }

  const btnConcluir = $('btn-concluir');
  const textoBotao = btnConcluir.querySelector('span').textContent;

  btnConcluir.addEventListener('click', async () => {
    const erroPedido = $('pedido-erro');
    const problema = validarPedido();
    if (problema) {
      mostrarAlerta(erroPedido, problema[0]);
      const campo = problema[1] || erroPedido;
      campo.focus({ preventScroll: true });
      campo.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }
    mostrarAlerta(erroPedido, '');

    btnConcluir.disabled = true;
    btnConcluir.querySelector('span').textContent = 'Processando…';

    try {
      const resposta = await fetch(`${ctx}/pedidos`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'X-Requested-With': 'fetch' // proteção CSRF (ver ProtecaoCsrf.java)
        },
        body: montarDadosPedido().toString()
      });
      const dados = await resposta.json().catch(() => ({}));
      if (resposta.ok && dados.sucesso) {
        confirmarPedido(dados.pedido);
        return;
      }
      mostrarAlerta(erroPedido, dados.mensagem || 'Não foi possível registrar o pedido. Tente novamente.');
    } catch (e) {
      mostrarAlerta(erroPedido, 'Sem ligação com o servidor. Verifique a internet e tente novamente.');
    }
    btnConcluir.disabled = false;
    btnConcluir.querySelector('span').textContent = textoBotao;
    erroPedido.scrollIntoView({ behavior: 'smooth', block: 'center' });
  });

  // Só vai para o servidor o que ele precisa: QUAIS vinhos e QUANTOS.
  // Preços não são enviados (o servidor usa os do banco) e os dados do cartão
  // também não (num site real, iriam direto para o gateway de pagamento).
  function montarDadosPedido() {
    const dados = new URLSearchParams();
    itens.forEach((i) => {
      dados.append('produto_id', String(Number(i.id)));
      dados.append('quantidade', String(i.quantidade || 1));
    });
    const envio = document.querySelector('input[name="shipping"]:checked');
    dados.set('entrega', envio ? envio.dataset.tipo : '');
    dados.set('pagamento', formaPagamento());
    if (formaPagamento() === 'credit') dados.set('parcelas', $('card-parcelas').value);
    ['cep', 'logradouro', 'numero', 'complemento', 'bairro', 'cidade'].forEach((id) => {
      dados.set(id, $(id).value.trim());
    });
    // "Guardar este endereço na minha conta" (o servidor ignora se já existir)
    if (checkGuardar && checkGuardar.checked && blocoGuardar && !blocoGuardar.hidden) {
      dados.set('salvarEndereco', '1');
      const apelido = $('apelido-endereco').value.trim();
      if (apelido) dados.set('apelido', apelido);
    }
    return dados;
  }

  // Mostra a confirmação com os dados devolvidos pelo servidor
  function confirmarPedido(pedido) {
    const forma = pedido.pagamento;
    const numero = pedido.numero;
    const total = Number(pedido.total);

    try {
      localStorage.removeItem('agnello_carrinho'); // esvazia o carrinho
      localStorage.removeItem('agnello_pedidos'); // pedidos antigos guardados só no navegador
    } catch (e) { /* sem localStorage: a confirmação aparece na mesma */ }

    // Instruções conforme a forma de pagamento (valores de demonstração)
    const codigoPix = `00020126580014BR.GOV.BCB.PIX0136agnello-${numero.toLowerCase()}5204000053039865406${total.toFixed(2)}5802BR`;
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
        <p>O pagamento foi aprovado em ${pedido.parcelas}x de ${brl(Number(pedido.valorParcela))} sem juros.
          Você receberá um e-mail quando o pedido for enviado.</p>`,
      boleto: `
        <h2>Boleto gerado</h2>
        <p>O boleto vence em 2 dias úteis. O pedido é liberado após a compensação bancária.</p>`
    };

    const conf = $('checkout-confirmacao');
    conf.innerHTML = `
      <div class="confirmacao-icone" aria-hidden="true">✓</div>
      <span class="confirmacao-eyebrow">Pedido ${numero}</span>
      <h1 id="confirmacao-titulo">Obrigado! O seu pedido foi recebido.</h1>
      <p class="confirmacao-lead">Enviamos a confirmação para <strong>${escapar(emailSessao)}</strong>.</p>

      <div class="confirmacao-pagamento">${instrucoes[forma] || ''}</div>

      <div class="confirmacao-resumo">
        <ul>${pedido.itens.map((i) => `<li><span>${i.quantidade}× ${escapar(i.nome)}</span><span>${brl(Number(i.total))}</span></li>`).join('')}</ul>
        <dl>
          <div><dt>Envio</dt><dd>${escapar(pedido.entrega)} · ${Number(pedido.frete) === 0 ? 'Grátis' : brl(Number(pedido.frete))}</dd></div>
          ${Number(pedido.desconto) ? `<div><dt>Desconto Pix</dt><dd>− ${brl(Number(pedido.desconto))}</dd></div>` : ''}
          <div class="confirmacao-total"><dt>Total</dt><dd>${brl(total)}</dd></div>
          <div><dt>Entrega</dt><dd>${pedido.endereco ? escapar(pedido.endereco) : 'Retirada na adega · Rua das Videiras, 1978 — Jardins, SP'}</dd></div>
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
