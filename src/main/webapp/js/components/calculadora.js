/**
 * Calculadora de Eventos Profissional - Vinheria Agnello
 * Baseada em métricas de sommelier: consumo médio por convidado/hora,
 * fator de ajuste por tipo de evento e proporções ideais.
 */
document.addEventListener('DOMContentLoaded', () => {
  const section = document.querySelector('.unique-events-calc-section');
  if (!section) return;

  // Estado da Calculadora Dinâmico
  let estadoCalculadora = {
    convidados: 12,
    duracao: 4,
    estilo: 'jantar' // 'jantar', 'festa', 'coquetel'
  };

  // Referências dos elementos visuais de controle
  const spanConvidados = document.getElementById('valor-convidados');
  const spanDuracao = document.getElementById('valor-duracao');

  const btnMenosConvidados = document.getElementById('btn-menos-convidados');
  const btnMaisConvidados = document.getElementById('btn-mais-convidados');
  const btnMenosDuracao = document.getElementById('btn-menos-duracao');
  const btnMaisDuracao = document.getElementById('btn-mais-duracao');

  const btnJantar = section.querySelector('[data-estilo="jantar"]');
  const btnFesta = section.querySelector('[data-estilo="festa"]');
  const btnCoquetel = section.querySelector('[data-estilo="coquetel"]');
  const btnMontarKit = section.querySelector('.unique-btn-calc-action');

  // Parâmetros de Sommelier profissionais atualizados
  const metricasSommelier = {
    jantar: {
      proporcao: { tinto: 0.60, branco: 0.30, espumante: 0.10 },
      consumoPorHora: 0.35,
      descricao: "Ideal para harmonizações estruturadas com pratos principais, valorizando vinhos tintos encorpados e brancos elegantes."
    },
    festa: {
      proporcao: { tinto: 0.30, branco: 0.20, espumante: 0.50 },
      consumoPorHora: 0.50,
      descricao: "Foco em celebrações dinâmicas, alta rotação de espumantes para brindar e rótulos refrescantes."
    },
    coquetel: {
      proporcao: { tinto: 0.20, branco: 0.35, espumante: 0.45 },
      consumoPorHora: 0.40,
      descricao: "Foco em recepções sofisticadas, com forte presença de espumantes e vinhos brancos aromáticos que harmonizam com finger foods."
    }
  };

  // Controles de Convidados
  if (btnMenosConvidados && btnMaisConvidados && spanConvidados) {
    btnMenosConvidados.addEventListener('click', () => {
      if (estadoCalculadora.convidados > 1) {
        estadoCalculadora.convidados--;
        spanConvidados.textContent = estadoCalculadora.convidados;
        atualizarUI();
      }
    });

    btnMaisConvidados.addEventListener('click', () => {
      estadoCalculadora.convidados++;
      spanConvidados.textContent = estadoCalculadora.convidados;
      atualizarUI();
    });
  }

  // Controles de Duração (Horas)
  if (btnMenosDuracao && btnMaisDuracao && spanDuracao) {
    btnMenosDuracao.addEventListener('click', () => {
      if (estadoCalculadora.duracao > 1) {
        estadoCalculadora.duracao--;
        spanDuracao.textContent = estadoCalculadora.duracao;
        atualizarUI();
      }
    });

    btnMaisDuracao.addEventListener('click', () => {
      estadoCalculadora.duracao++;
      spanDuracao.textContent = estadoCalculadora.duracao;
      atualizarUI();
    });
  }

  // Função Principal de Cálculo Profissional
  function calcularGarrafas() {
    const { convidados, duracao, estilo } = estadoCalculadora;
    const config = metricasSommelier[estilo];

    const totalLitros = convidados * duracao * config.consumoPorHora;
    const totalGarrafas = Math.ceil(totalLitros / 0.75);

    let qteTinto = Math.round(totalGarrafas * config.proporcao.tinto);
    let qteBranco = Math.round(totalGarrafas * config.proporcao.branco);
    let qteEspumante = totalGarrafas - (qteTinto + qteBranco);

    if (qteEspumante < 0) qteEspumante = 0;

    return {
      total: totalGarrafas,
      tinto: qteTinto,
      branco: qteBranco,
      espumante: qteEspumante,
      detalhes: config.descricao
    };
  }

  // Atualiza a Interface Visual Dinamicamente
  function atualizarUI() {
    const resultado = calcularGarrafas();

    const totalElem = section.querySelector('.unique-result-total');
    if (totalElem) {
      totalElem.innerHTML = `${resultado.total} <span aria-hidden="true">Garrafas</span>`;
    }

    const itensBreakdown = section.querySelectorAll('.unique-breakdown-item strong');
    if (itensBreakdown.length >= 3) {
      itensBreakdown[0].textContent = resultado.tinto;
      itensBreakdown[1].textContent = resultado.branco;
      itensBreakdown[2].textContent = resultado.espumante;
    }
  }

  // Controle de Seleção do Estilo do Evento
  function selecionarEstilo(estiloSelecionado, botaoAtivo) {
    estadoCalculadora.estilo = estiloSelecionado;

    section.querySelectorAll('.unique-type-btn').forEach(btn => btn.classList.remove('active'));
    botaoAtivo.classList.add('active');

    atualizarUI();
  }

  if (btnJantar) btnJantar.addEventListener('click', (e) => selecionarEstilo('jantar', e.currentTarget));
  if (btnFesta) btnFesta.addEventListener('click', (e) => selecionarEstilo('festa', e.currentTarget));
  if (btnCoquetel) btnCoquetel.addEventListener('click', (e) => selecionarEstilo('coquetel', e.currentTarget));

  // Ação do Botão Principal: Exibe o card com o Planejamento Sob Medida e o botão para Consultar Orçamento
  if (btnMontarKit) {
    btnMontarKit.addEventListener('click', () => {
      const resultadoFinal = calcularGarrafas();
      const calculatorBox = section.querySelector('.unique-calculator-box');

      if (!calculatorBox) return;

      calculatorBox.style.opacity = '0';
      calculatorBox.style.transform = 'translateY(10px)';
      calculatorBox.style.transition = 'all 0.3s ease';

      setTimeout(() => {
        calculatorBox.innerHTML = `
          <div class="unique-sommelier-result-card" style="text-align: left; animation: uniqueFadeIn 0.5s ease-out;">
            <span class="unique-result-label" style="text-align: center; display: block;">PARECER TÉCNICO DO SOMMELIER</span>
            <h3 style="font-family: var(--font-heading); font-size: 1.8rem; color: #FFF; margin-bottom: 1rem; text-align: center;">Planejamento Sob Medida</h3>
            
            <p style="color: #D1CDCD; font-size: 0.9rem; line-height: 1.5; margin-bottom: 1.5rem;">
              ${resultadoFinal.detalhes} O volume foi calculado para <strong>${estadoCalculadora.convidados} convidados</strong> ao longo de <strong>${estadoCalculadora.duracao} horas</strong> de evento (${estadoCalculadora.estilo.toUpperCase()}).
            </p>

            <div style="background: rgba(0, 0, 0, 0.3); border: 1px solid rgba(212, 175, 55, 0.3); border-radius: 8px; padding: 1.2rem; margin-bottom: 1.5rem;">
              <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem; font-size: 0.9rem;">
                <span style="color: #AAA;">Volume Recomendado Total:</span>
                <strong style="color: var(--accent-gold);">${resultadoFinal.total} Garrafas (750ml)</strong>
              </div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 0.4rem; font-size: 0.85rem;">
                <span style="color: #CCC;">• Vinhos Tintos:</span>
                <strong style="color: #FFF;">${resultadoFinal.tinto} garrafas</strong>
              </div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 0.4rem; font-size: 0.85rem;">
                <span style="color: #CCC;">• Vinhos Brancos:</span>
                <strong style="color: #FFF;">${resultadoFinal.branco} garrafas</strong>
              </div>
              <div style="display: flex; justify-content: space-between; font-size: 0.85rem;">
                <span style="color: #CCC;">• Espumantes:</span>
                <strong style="color: #FFF;">${resultadoFinal.espumante} garrafas</strong>
              </div>
            </div>

            <div style="display: flex; gap: 1rem;">
              <button type="button" id="unique-btn-voltar-calc" style="flex: 1; background: transparent; border: 1px solid rgba(255,255,255,0.2); color: #FFF; padding: 0.9rem; border-radius: 6px; font-weight: 600; cursor: pointer; transition: background 0.2s;">
                ← Refazer Cálculo
              </button>
              <button type="button" id="unique-btn-abre-form" style="flex: 1; background-color: var(--accent-gold); color: var(--text-charcoal, #1e0a10); border: none; padding: 0.9rem; border-radius: 6px; font-weight: 600; cursor: pointer; transition: opacity 0.2s;">
                Consulte Orçamento →
              </button>
            </div>
          </div>
        `;

        calculatorBox.style.opacity = '1';
        calculatorBox.style.transform = 'translateY(0)';

        // Ação para refazer o cálculo e retornar ao início
        const btnVoltarCalc = document.getElementById('unique-btn-voltar-calc');
        if (btnVoltarCalc) {
          btnVoltarCalc.addEventListener('click', () => {
            location.reload();
          });
        }

        // Ação para abrir o formulário ao clicar em "Consulte Orçamento"
        const btnAbreForm = document.getElementById('unique-btn-abre-form');
        if (btnAbreForm) {
          btnAbreForm.addEventListener('click', () => {
            calculatorBox.innerHTML = `
              <div class="unique-sommelier-result-card" style="text-align: left; animation: uniqueFadeIn 0.5s ease-out;">
                <span class="unique-result-label" style="text-align: center; display: block;">SOLICITAÇÃO DE ORÇAMENTO</span>
                <h3 style="font-family: var(--font-heading); font-size: 1.5rem; color: #FFF; margin-bottom: 0.8rem; text-align: center;">Fale com um Consultor</h3>
                
                <p style="color: #D1CDCD; font-size: 0.8rem; line-height: 1.4; margin-bottom: 1rem; text-align: center;">
                  Baseado no seu evento de ${estadoCalculadora.convidados} convidados (${resultadoFinal.total} garrafas), preencha os dados abaixo:
                </p>

                <form id="unique-form-consultor" style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.8rem; align-items: stretch;">
                  <!-- Coluna da Esquerda: Dados de Contato -->
                  <div style="display: flex; flex-direction: column; gap: 0.7rem;">
                    <input type="text" id="cli-nome" placeholder="Seu Nome" required style="background: rgba(255,255,255,0.07); border: 1px solid rgba(255,255,255,0.15); padding: 0.6rem; border-radius: 6px; color: #FFF; font-size: 0.85rem; width: 100%; box-sizing: border-box;">
                    <input type="email" id="cli-email" placeholder="Seu E-mail" required style="background: rgba(255,255,255,0.07); border: 1px solid rgba(255,255,255,0.15); padding: 0.6rem; border-radius: 6px; color: #FFF; font-size: 0.85rem; width: 100%; box-sizing: border-box;">
                    <input type="text" id="cli-telefone" placeholder="WhatsApp / Telefone" style="background: rgba(255,255,255,0.07); border: 1px solid rgba(255,255,255,0.15); padding: 0.6rem; border-radius: 6px; color: #FFF; font-size: 0.85rem; width: 100%; box-sizing: border-box;">
                    <input type="text" id="cli-orcamento" placeholder="Orçamento Estimado (R$)" style="background: rgba(255,255,255,0.07); border: 1px solid rgba(255,255,255,0.15); padding: 0.6rem; border-radius: 6px; color: #FFF; font-size: 0.85rem; width: 100%; box-sizing: border-box;">
                  </div>

                  <!-- Coluna da Direita: Textarea Ampliado na Vertical -->
                  <div style="display: flex; flex-direction: column;">
                    <textarea id="cli-obs" class="unique-form-textarea" placeholder="Detalhes adicionais do evento (Ex: Cardápio, preferências de vinhos, data e local)..." style="background: rgba(255,255,255,0.07); border: 1px solid rgba(255,255,255,0.15); padding: 0.7rem; border-radius: 6px; color: #FFF; font-size: 0.85rem; resize: none; width: 100%; min-height: 180px; box-sizing: border-box; font-family: inherit;"></textarea>
                  </div>

                  <!-- Botões de Ação na Largura Total -->
                  <div style="grid-column: span 2; display: flex; gap: 0.8rem; margin-top: 0.3rem;">
                    <button type="button" onclick="location.reload()" style="flex: 1; background: transparent; border: 1px solid rgba(255,255,255,0.2); color: #FFF; padding: 0.7rem; border-radius: 6px; font-weight: 600; cursor: pointer; font-size: 0.85rem;">
                      ← Voltar
                    </button>
                    <button type="submit" style="flex: 1; background-color: var(--accent-gold); color: var(--text-charcoal, #1e0a10); border: none; padding: 0.7rem; border-radius: 6px; font-weight: 600; cursor: pointer; font-size: 0.85rem;">
                      Enviar Proposta →
                    </button>
                  </div>
                </form>
              </div>
            `;

            // Submissão do formulário do consultor com captura completa
            const formConsultor = document.getElementById('unique-form-consultor');
            if (formConsultor) {
              formConsultor.addEventListener('submit', (ev) => {
                ev.preventDefault();

                const dadosEnvio = {
                  cliente: {
                    nome: document.getElementById('cli-nome').value,
                    email: document.getElementById('cli-email').value,
                    telefone: document.getElementById('cli-telefone').value,
                    orcamento: document.getElementById('cli-orcamento').value,
                    observacoes: document.getElementById('cli-obs').value
                  },
                  calculoEvento: {
                    estilo: estadoCalculadora.estilo,
                    convidados: estadoCalculadora.convidados,
                    duracaoHoras: estadoCalculadora.duracao,
                    garrafasTotais: resultadoFinal.total,
                    distribuicao: {
                      tintos: resultadoFinal.tinto,
                      brancos: resultadoFinal.branco,
                      espumantes: resultadoFinal.espumante
                    }
                  }
                };

                console.log("Dados consolidados para o consultor:", dadosEnvio);

                calculatorBox.innerHTML = `
                  <div style="text-align: center; padding: 2rem 0; animation: uniqueFadeIn 0.5s ease-out;">
                    <h3 style="font-family: var(--font-heading); font-size: 1.8rem; color: var(--accent-gold); margin-bottom: 1rem;">Solicitação Enviada!</h3>
                    <p style="color: #D1CDCD; font-size: 0.95rem; line-height: 1.6; margin-bottom: 1.5rem;">
                      Obrigado, <strong>${dadosEnvio.cliente.nome}</strong>! Nossa equipe de sommeliers recebeu os parâmetros do seu evento e entrará em contato em breve.
                    </p>
                    <button type="button" onclick="location.reload()" style="background-color: #FFF; color: var(--primary-wine); border: none; padding: 0.8rem 1.5rem; border-radius: 6px; font-weight: 600; cursor: pointer;">
                      Fazer Novo Cálculo
                    </button>
                  </div>
                `;
              });
            }
          });
        }

      }, 300);
    });
  }

  atualizarUI();
});