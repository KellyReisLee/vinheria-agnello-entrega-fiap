document.addEventListener('DOMContentLoaded', () => {
  let currentStep = 1;
  const totalSteps = 3;
  const userAnswers = {};

  // Define a resposta padrão inicial para a etapa 1
  userAnswers['etapa1'] = 'carnes-vermelhas';

  function atualizarProgresso(etapaAtual) {
    const progressFill = document.getElementById('progress-fill');
    const progressText = document.getElementById('progress-text');

    const porcentagens = { 1: 33, 2: 66, 3: 100 };

    if (progressFill) {
      progressFill.style.width = `${porcentagens[etapaAtual]}%`;
    }

    if (progressText) {
      progressText.textContent = `Passo ${etapaAtual} de ${totalSteps}`;
    }
  }

  function mostrarEtapa(etapa) {
    // Esconde todas as etapas rigorosamente
    for (let i = 1; i <= totalSteps; i++) {
      const stepEl = document.getElementById(`step-${i}`);
      if (stepEl) {
        stepEl.classList.remove('active');
      }
    }

    // Mostra exclusivamente a etapa atual
    const activeStepEl = document.getElementById(`step-${etapa}`);
    if (activeStepEl) {
      activeStepEl.classList.add('active');
    }

    atualizarProgresso(etapa);
    vincularEventosPils();
  }

  function prepararEtapasDinamicas(step) {
    const resp1 = userAnswers['etapa1'] || 'carnes-vermelhas';

    if (step === 2) {
      const tituloEtapa2 = document.querySelector('#step-2 .question-title');
      const grupoEtapa2 = document.querySelector('#step-2 .options-list');

      if (resp1 === 'carnes-vermelhas') {
        tituloEtapa2.textContent = 'Qual o corte ou intensidade da carne principal?';
        grupoEtapa2.innerHTML = `
          <div class="type-btn option-pill selected" data-step="2" data-value="corte-gorduroso">
            <span>Cortes gordurosos (Picanha, Ribeye)</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="2" data-value="corte-magro">
            <span>Cortes magros (Filé Mignon, Cordeiro)</span>
            <span class="arrow">›</span>
          </div>
        `;
        userAnswers['etapa2'] = 'corte-gorduroso';
      } else if (resp1 === 'massas-molhos') {
        tituloEtapa2.textContent = 'Como é a estrutura predominante do molho?';
        grupoEtapa2.innerHTML = `
          <div class="type-btn option-pill selected" data-step="2" data-value="molho-carne-ragu">
            <span>Ragu de carne ou molhos encorpados à base de tomate</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="2" data-value="molho-branco-queijos">
            <span>Molhos brancos cremosos ou queijos intensos</span>
            <span class="arrow">›</span>
          </div>
        `;
        userAnswers['etapa2'] = 'molho-carne-ragu';
      } else if (resp1 === 'peixes-frutos-mar') {
        tituloEtapa2.textContent = 'O prato possui molhos leves ou estrutura untuosa?';
        grupoEtapa2.innerHTML = `
          <div class="type-btn option-pill selected" data-step="2" data-value="peixe-grelhado">
            <span>Apenas peixe grelhado com limão e azeite</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="2" data-value="molho-cremoso">
            <span>Frutos do mar ao molho cremoso ou comida japonesa</span>
            <span class="arrow">›</span>
          </div>
        `;
        userAnswers['etapa2'] = 'peixe-grelhado';
      } else {
        tituloEtapa2.textContent = 'Qual é a proposta para este momento ao ar livre?';
        grupoEtapa2.innerHTML = `
          <div class="type-btn option-pill selected" data-step="2" data-value="sunset-refrescante">
            <span>Espumantes vibrantes ou brancos leves para um final de tarde</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="2" data-value="rose-elegante">
            <span>Rosés gastronômicos ou tintos frutados e descomplicados</span>
            <span class="arrow">›</span>
          </div>
        `;
        userAnswers['etapa2'] = 'sunset-refrescante';
      }
    }
    else if (step === 3) {
      const tituloEtapa3 = document.querySelector('#step-3 .question-title');
      const grupoEtapa3 = document.querySelector('#step-3 .options-list');
      const resp2 = userAnswers['etapa2'] || 'corte-gorduroso';

      if (resp2 === 'corte-gorduroso' || resp2 === 'molho-carne-ragu') {
        tituloEtapa3.textContent = 'Qual intensidade de tanino e carvalho você prefere?';
        grupoEtapa3.innerHTML = `
          <div class="type-btn option-pill selected" data-step="3" data-value="taninos-potentes">
            <span>Taninos marcantes e passagem prolongada por barrica</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="3" data-value="taninos-macios">
            <span>Taninos macios, redondos e boa fruta</span>
            <span class="arrow">›</span>
          </div>
        `;
        userAnswers['etapa3'] = 'taninos-potentes';
      } else {
        tituloEtapa3.textContent = 'Qual o nível de acidez e frescor ideal para o seu paladar?';
        grupoEtapa3.innerHTML = `
          <div class="type-btn option-pill selected" data-step="3" data-value="fresco-mineral">
            <span>Alta acidez, toque mineral e refrescante</span>
            <span class="arrow">›</span>
          </div>
          <div class="type-btn option-pill" data-step="3" data-value="frutado-equilibrado">
            <span>Frutado, macio e de perfil mais versátil</span>
            <span class="arrow">›</span>
          </div>
        `;
        userAnswers['etapa3'] = 'fresco-mineral';
      }
    }
  }

  function vincularEventosPils() {
    const activeStepElement = document.getElementById(`step-${currentStep}`);
    if (!activeStepElement) return;

    const activePills = activeStepElement.querySelectorAll('.option-pill');

    activePills.forEach(pill => {
      pill.removeEventListener('click', handlePillClick);
      pill.addEventListener('click', handlePillClick);
    });
  }

  function handlePillClick(e) {
    const pill = e.currentTarget;
    const activeStepElement = document.getElementById(`step-${currentStep}`);

    activeStepElement.querySelectorAll('.option-pill').forEach(p => p.classList.remove('selected'));
    pill.classList.add('selected');

    const step = parseInt(pill.getAttribute('data-step'));
    const value = pill.getAttribute('data-value');
    userAnswers[`etapa${step}`] = value;

    if (step === 1) {
      prepararEtapasDinamicas(2);
    } else if (step === 2) {
      prepararEtapasDinamicas(3);
    }
  }

  // Botão Próximo
  const btnProximo = document.getElementById('btn-proximo');
  if (btnProximo) {
    btnProximo.addEventListener('click', () => {
      if (currentStep < totalSteps) {
        currentStep++;
        prepararEtapasDinamicas(currentStep);
        mostrarEtapa(currentStep);
      } else {
		  localStorage.setItem('vinhoQuizResult', JSON.stringify(userAnswers));
		  window.location.href = '/vinheria-agnello/sugestoes';
		
    }});
  }

  // Botão Voltar
  const btnVoltar = document.getElementById('btn-voltar');
  if (btnVoltar) {
    btnVoltar.addEventListener('click', () => {
      if (currentStep > 1) {
        currentStep--;
        mostrarEtapa(currentStep);
      } else {
        window.location.href = 'index.html';
      }
    });
  }

  // Inicialização
  prepararEtapasDinamicas(2);
  prepararEtapasDinamicas(3);
  mostrarEtapa(currentStep);
});