/**
 * Calculadora de Eventos — Vinheria Agnello
 * Baseada em métricas de sommelier: consumo médio por convidado/hora,
 * fator por tipo de evento e proporções ideais entre tintos, brancos e espumantes.
 *
 * A caixa tem 4 "ecrãs" que se alternam SEM recarregar a página:
 *   calculadora -> parecer do sommelier -> formulário de orçamento -> confirmação
 * A aparência está em css/sections/_calculator.css (nada de estilos inline).
 */
document.addEventListener('DOMContentLoaded', () => {
  const section = document.querySelector('.unique-events-calc-section');
  if (!section) return;

  const box = section.querySelector('.unique-calculator-box');
  const vistaCalculadora = section.querySelector('.calc-view');
  if (!box || !vistaCalculadora) return;

  // Painel onde aparecem os ecrãs seguintes (criado uma única vez)
  const painel = document.createElement('div');
  painel.className = 'calc-panel';
  painel.hidden = true;
  painel.setAttribute('aria-live', 'polite');
  box.appendChild(painel);

  // ---------- Estado ----------
  const LIMITES = {
    convidados: { min: 1, max: 500 },
    duracao: { min: 1, max: 24 }
  };

  const estado = {
    convidados: 12,
    duracao: 4,
    estilo: 'jantar' // 'jantar' | 'festa' | 'coquetel'
  };

  const metricasSommelier = {
    jantar: {
      nome: 'Jantar',
      proporcao: { tinto: 0.60, branco: 0.30, espumante: 0.10 },
      consumoPorHora: 0.35,
      descricao: 'Ideal para harmonizações estruturadas com pratos principais, valorizando vinhos tintos encorpados e brancos elegantes.'
    },
    festa: {
      nome: 'Festa',
      proporcao: { tinto: 0.30, branco: 0.20, espumante: 0.50 },
      consumoPorHora: 0.50,
      descricao: 'Foco em celebrações dinâmicas, alta rotação de espumantes para brindar e rótulos refrescantes.'
    },
    coquetel: {
      nome: 'Coquetel',
      proporcao: { tinto: 0.20, branco: 0.35, espumante: 0.45 },
      consumoPorHora: 0.40,
      descricao: 'Foco em recepções sofisticadas, com forte presença de espumantes e vinhos brancos aromáticos que harmonizam com finger foods.'
    }
  };

  // ---------- Utilitários ----------
  const escapar = (texto) => String(texto ?? '').replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));

  const plural = (n, um, varios) => `${n} ${n === 1 ? um : varios}`;

  function calcularGarrafas() {
    const { convidados, duracao, estilo } = estado;
    const config = metricasSommelier[estilo];

    const totalLitros = convidados * duracao * config.consumoPorHora;
    const total = Math.ceil(totalLitros / 0.75); // garrafa de 750 ml

    const tinto = Math.round(total * config.proporcao.tinto);
    const branco = Math.round(total * config.proporcao.branco);
    const espumante = Math.max(0, total - (tinto + branco));

    return { total, tinto, branco, espumante, detalhes: config.descricao };
  }

  // ---------- Ecrã 1: calculadora ----------
  const spanConvidados = document.getElementById('valor-convidados');
  const spanDuracao = document.getElementById('valor-duracao');

  function atualizarUI() {
    const r = calcularGarrafas();

    if (spanConvidados) spanConvidados.textContent = estado.convidados;
    if (spanDuracao) spanDuracao.textContent = estado.duracao;

    const totalElem = section.querySelector('.unique-result-total');
    if (totalElem) {
      totalElem.innerHTML = `${r.total} <span>${r.total === 1 ? 'Garrafa' : 'Garrafas'}</span>`;
    }

    const itens = section.querySelectorAll('.unique-breakdown-item strong');
    if (itens.length >= 3) {
      itens[0].textContent = r.tinto;
      itens[1].textContent = r.branco;
      itens[2].textContent = r.espumante;
    }

    // Desativa "−" e "+" quando se atinge o limite
    const ajustarBotoes = (campo, idMenos, idMais) => {
      const menos = document.getElementById(idMenos);
      const mais = document.getElementById(idMais);
      if (menos) menos.disabled = estado[campo] <= LIMITES[campo].min;
      if (mais) mais.disabled = estado[campo] >= LIMITES[campo].max;
    };
    ajustarBotoes('convidados', 'btn-menos-convidados', 'btn-mais-convidados');
    ajustarBotoes('duracao', 'btn-menos-duracao', 'btn-mais-duracao');
  }

  function alterar(campo, delta) {
    const { min, max } = LIMITES[campo];
    estado[campo] = Math.min(max, Math.max(min, estado[campo] + delta));
    atualizarUI();
  }

  const ligar = (id, fn) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener('click', fn);
  };
  ligar('btn-menos-convidados', () => alterar('convidados', -1));
  ligar('btn-mais-convidados', () => alterar('convidados', 1));
  ligar('btn-menos-duracao', () => alterar('duracao', -1));
  ligar('btn-mais-duracao', () => alterar('duracao', 1));

  section.querySelectorAll('.unique-type-btn').forEach((btn) => {
    btn.addEventListener('click', () => {
      estado.estilo = btn.dataset.estilo;
      section.querySelectorAll('.unique-type-btn').forEach((b) => {
        const ativo = b === btn;
        b.classList.toggle('active', ativo);
        b.setAttribute('aria-pressed', String(ativo));
      });
      atualizarUI();
    });
  });

  // ---------- Troca de ecrã com transição suave ----------
  // aoMostrar: função opcional chamada depois de o novo HTML estar na página
  function mostrarPainel(html, focarSeletor, aoMostrar) {
    box.classList.add('is-switching');
    setTimeout(() => {
      vistaCalculadora.hidden = true;
      painel.innerHTML = html;
      painel.hidden = false;
      box.classList.remove('is-switching');
      if (aoMostrar) aoMostrar();
      const alvo = painel.querySelector(focarSeletor || 'h3');
      if (alvo) {
        if (alvo.tagName === 'H3') alvo.setAttribute('tabindex', '-1');
        alvo.focus({ preventScroll: true });
      }
    }, 200);
  }

  function voltarCalculadora() {
    box.classList.add('is-switching');
    setTimeout(() => {
      painel.hidden = true;
      painel.innerHTML = '';
      vistaCalculadora.hidden = false;
      box.classList.remove('is-switching');
      const botao = section.querySelector('.unique-btn-calc-action');
      if (botao) botao.focus({ preventScroll: true });
    }, 200);
  }

  // ---------- Ecrã 2: parecer do sommelier ----------
  function mostrarParecer() {
    const r = calcularGarrafas();
    const nomeEstilo = metricasSommelier[estado.estilo].nome;

    mostrarPainel(`
      <span class="unique-result-label">PARECER TÉCNICO DO SOMMELIER</span>
      <h3 class="calc-panel-title">Planejamento Sob Medida</h3>
      <p class="calc-panel-text">
        ${r.detalhes} O volume foi calculado para
        <strong>${plural(estado.convidados, 'convidado', 'convidados')}</strong> ao longo de
        <strong>${plural(estado.duracao, 'hora', 'horas')}</strong> de evento (${nomeEstilo}).
      </p>

      <dl class="calc-summary">
        <div class="calc-summary-total">
          <dt>Volume recomendado</dt>
          <dd>${plural(r.total, 'garrafa', 'garrafas')} <small>(750 ml)</small></dd>
        </div>
        <div><dt>Vinhos tintos</dt><dd>${plural(r.tinto, 'garrafa', 'garrafas')}</dd></div>
        <div><dt>Vinhos brancos</dt><dd>${plural(r.branco, 'garrafa', 'garrafas')}</dd></div>
        <div><dt>Espumantes</dt><dd>${plural(r.espumante, 'garrafa', 'garrafas')}</dd></div>
      </dl>

      <div class="calc-actions">
        <button type="button" class="calc-btn-secondary" data-calc="voltar">
          <span aria-hidden="true">←</span> Refazer cálculo
        </button>
        <button type="button" class="calc-btn-primary" data-calc="orcamento">
          Pedir orçamento <span class="btn-arrow" aria-hidden="true">→</span>
        </button>
      </div>
    `);
  }

  // ---------- Ecrã 3: formulário de orçamento ----------
  function mostrarFormulario() {
    const r = calcularGarrafas();

    mostrarPainel(`
      <span class="unique-result-label">SOLICITAÇÃO DE ORÇAMENTO</span>
      <h3 class="calc-panel-title">Fale com um Consultor</h3>
      <p class="calc-panel-text calc-panel-text--center">
        Evento de ${plural(estado.convidados, 'convidado', 'convidados')}
        (${plural(r.total, 'garrafa', 'garrafas')}). Preencha seus dados:
      </p>

      <form class="calc-form" id="unique-form-consultor" novalidate>
        <div class="calc-field">
          <label for="cli-nome">Nome</label>
          <input type="text" id="cli-nome" name="nome" autocomplete="name" required>
        </div>
        <div class="calc-field">
          <label for="cli-email">E-mail</label>
          <input type="email" id="cli-email" name="email" autocomplete="email" required>
        </div>
        <div class="calc-field">
          <label for="cli-telefone">WhatsApp / Telefone <span class="calc-optional">(opcional)</span></label>
          <input type="tel" id="cli-telefone" name="telefone" autocomplete="tel" inputmode="tel">
        </div>
        <div class="calc-field">
          <label for="cli-orcamento">Orçamento estimado <span class="calc-optional">(opcional)</span></label>
          <input type="text" id="cli-orcamento" name="orcamento" inputmode="decimal" placeholder="R$">
        </div>
        <div class="calc-field calc-field--full">
          <label for="cli-obs">Detalhes do evento <span class="calc-optional">(opcional)</span></label>
          <textarea id="cli-obs" name="observacoes" rows="3"
            placeholder="Cardápio, preferências de vinhos, data e local..."></textarea>
        </div>
        <p class="calc-form-error" role="alert" hidden></p>
        <div class="calc-actions calc-field--full">
          <button type="button" class="calc-btn-secondary" data-calc="parecer">
            <span aria-hidden="true">←</span> Voltar
          </button>
          <button type="submit" class="calc-btn-primary">
            Enviar pedido <span class="btn-arrow" aria-hidden="true">→</span>
          </button>
        </div>
      </form>
    `, '#cli-nome', () => ligarFormulario(r));
  }

  function ligarFormulario(r) {
    const form = painel.querySelector('#unique-form-consultor');
    if (!form) return;
    form.addEventListener('submit', (ev) => {
      ev.preventDefault();
      const erro = form.querySelector('.calc-form-error');
      const nome = form.nome.value.trim();
      const email = form.email.value.trim();

      if (!nome || !form.email.checkValidity() || !email) {
        erro.textContent = 'Informe seu nome e um e-mail válido.';
        erro.hidden = false;
        (nome ? form.email : form.nome).focus();
        return;
      }

      const dadosEnvio = {
        cliente: {
          nome,
          email,
          telefone: form.telefone.value.trim(),
          orcamento: form.orcamento.value.trim(),
          observacoes: form.observacoes.value.trim()
        },
        calculoEvento: {
          estilo: estado.estilo,
          convidados: estado.convidados,
          duracaoHoras: estado.duracao,
          garrafasTotais: r.total,
          distribuicao: { tintos: r.tinto, brancos: r.branco, espumantes: r.espumante }
        }
      };

      // TODO: enviar para um Servlet (ex.: POST /orcamento) quando existir no back-end
      console.log('Pedido de orçamento (simulação):', dadosEnvio);

      mostrarConfirmacao(nome);
    });
  }

  // ---------- Ecrã 4: confirmação ----------
  function mostrarConfirmacao(nome) {
    mostrarPainel(`
      <div class="calc-success">
        <span class="calc-success-icon" aria-hidden="true">✓</span>
        <h3 class="calc-panel-title">Pedido enviado!</h3>
        <p class="calc-panel-text calc-panel-text--center">
          Obrigado, <strong>${escapar(nome)}</strong>! Nossa equipe de sommeliers recebeu os
          detalhes do seu evento e entrará em contato em breve.
        </p>
        <button type="button" class="calc-btn-light" data-calc="voltar">Fazer novo cálculo</button>
      </div>
    `);
  }

  // ---------- Botões dos painéis (um único ouvinte) ----------
  painel.addEventListener('click', (e) => {
    const acao = e.target.closest('[data-calc]');
    if (!acao) return;
    const destino = acao.dataset.calc;
    if (destino === 'voltar') voltarCalculadora();
    if (destino === 'parecer') mostrarParecer();
    if (destino === 'orcamento') mostrarFormulario();
  });

  const btnMontarKit = section.querySelector('.unique-btn-calc-action');
  if (btnMontarKit) btnMontarKit.addEventListener('click', mostrarParecer);

  atualizarUI();
});
