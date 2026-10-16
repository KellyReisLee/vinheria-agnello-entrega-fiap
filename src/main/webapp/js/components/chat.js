// js/components/chat.js
// Assistente de atendimento (canto da tela). Responde às dúvidas mais comuns
// (frete, pagamento, trocas, pedidos, idade…) a partir de palavras-chave e
// encaminha para a página certa ou para o WhatsApp da equipe.
//
// Não é uma inteligência artificial: são respostas prontas, escritas pela loja.
// Por isso o texto de boas-vindas diz "assistente" e não "IA".
(function () {
  const ctx = window.CONTEXT_PATH || '';
  const WHATSAPP = 'https://wa.me/5511999999999';

  // ---------- Respostas prontas ----------
  // Cada resposta tem palavras-chave (sem acentos, em minúsculas) e um texto em HTML
  // escrito por nós (nunca o texto do cliente, que é sempre tratado como texto puro).
  const RESPOSTAS = [
    {
      chaves: ['frete', 'entrega', 'envio', 'prazo', 'chega', 'transportadora'],
      texto: `O <strong>Frete Térmico Agnello</strong> custa R$ 25,00 e entrega em até 48h, com controle de temperatura.
        Também pode <strong>retirar grátis na adega</strong> (Jardins, SP) em 3 horas.
        <a href="${ctx}/entregas-e-trocas">Ver entregas e trocas</a>`
    },
    {
      chaves: ['pix', 'cartao', 'boleto', 'pagamento', 'pagar', 'parcela', 'parcelar', 'desconto'],
      texto: `Aceitamos <strong>Pix</strong> (5% de desconto), <strong>cartão de crédito</strong> em até 6x sem juros
        (parcela mínima de R$ 50) e <strong>boleto</strong>.`
    },
    {
      chaves: ['troca', 'trocar', 'devolucao', 'devolver', 'defeito', 'quebrad', 'arrependimento'],
      texto: `Você tem <strong>7 dias</strong> após o recebimento para desistir da compra, e garrafas com defeito são trocadas.
        <a href="${ctx}/entregas-e-trocas">Ver a política de trocas</a>`
    },
    {
      chaves: ['pedido', 'rastre', 'status', 'acompanhar', 'compra'],
      texto: `Os seus pedidos e o estado de cada um ficam em
        <a href="${ctx}/area-pessoal#pedidos">Minha conta › Meus pedidos</a>.`
    },
    {
      chaves: ['sommelier', 'indica', 'sugest', 'harmoniz', 'combina', 'qual vinho', 'presente'],
      texto: `O nosso <a href="${ctx}/quiz">Sommelier Virtual</a> faz algumas perguntas sobre o seu paladar
        e sugere os rótulos ideais. Leva menos de 1 minuto!`
    },
    {
      chaves: ['idade', 'menor', '18', 'documento'],
      texto: 'A venda de bebidas alcoólicas é proibida para menores de 18 anos. Na entrega ou retirada, pode ser pedido um documento com foto.'
    },
    {
      chaves: ['senha', 'login', 'entrar', 'conta', 'cadastro', 'ativar'],
      texto: `Para entrar, use <a href="${ctx}/login">Entrar</a>. Esqueceu a senha?
        <a href="${ctx}/esqueci-senha">Recupere aqui</a>. Lembre-se de ativar a conta pelo link enviado por e-mail.`
    },
    {
      chaves: ['horario', 'endereco', 'loja', 'onde fica', 'telefone', 'whatsapp', 'contato'],
      texto: `A adega fica na Rua das Videiras, 1978 — Jardins, São Paulo, e abre de segunda a sábado, das 10h às 20h. Fale conosco pelo
        <a href="${WHATSAPP}" target="_blank" rel="noopener">WhatsApp</a> ou pela página
        <a href="${ctx}/contato">Contato</a>.`
    }
  ];

  const RESPOSTA_PADRAO = `Não tenho certeza se entendi. Posso ajudar com <strong>frete</strong>, <strong>pagamento</strong>,
    <strong>trocas</strong>, <strong>pedidos</strong> ou <strong>indicação de vinhos</strong>.
    Se preferir falar com uma pessoa, chame a nossa equipe no
    <a href="${WHATSAPP}" target="_blank" rel="noopener">WhatsApp</a>.`;

  const semAcentos = (t) => t.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();

  function responder(pergunta) {
    const texto = semAcentos(pergunta);
    const achada = RESPOSTAS.find((r) => r.chaves.some((c) => texto.includes(c)));
    return achada ? achada.texto : RESPOSTA_PADRAO;
  }

  // ---------- Estrutura do widget ----------
  const widgetContainer = document.createElement('div');
  widgetContainer.innerHTML = `
    <div id="chat-widget-container" class="chat-widget-container">
      <button type="button" id="chat-toggle-btn" class="chat-toggle-btn" aria-label="Abrir o chat de atendimento"
              aria-expanded="false" aria-controls="chat-box">💬</button>

      <div id="chat-box" class="chat-box hidden" role="dialog" aria-labelledby="chat-titulo">
        <div class="chat-header">
          <h3 id="chat-titulo">Atendimento Vinheria Agnello</h3>
          <button type="button" id="chat-close-btn" aria-label="Fechar o chat">×</button>
        </div>

        <div class="chat-body" aria-live="polite">
          <div class="chat-timestamp" id="dynamic-timestamp">Hoje</div>
          <div class="chat-terms-notice">
            Respostas automáticas para dúvidas frequentes. Não escreva dados de cartão ou senhas aqui.
            Veja a nossa <a href="${ctx}/privacidade">Política de Privacidade</a>.
          </div>

          <div class="chat-message-wrapper">
            <div class="chat-avatar" aria-hidden="true">AG</div>
            <div class="chat-bubble">
              Olá! Sou o assistente da Vinheria Agnello. Pergunte sobre frete, pagamento, trocas,
              pedidos ou peça uma indicação de vinho.
            </div>
          </div>
          <div class="chat-msg-time" id="dynamic-agent-time">Assistente Agnello</div>
        </div>

        <div class="chat-footer">
          <button type="button" class="chat-action-icon" id="chat-emoji-btn" aria-label="Inserir emoji de taça">
            <img src="${ctx}/assets/icons/face-grinning.svg" alt="" width="20" height="20">
          </button>
          <label for="chat-input" class="sr-only">Mensagem</label>
          <input type="text" id="chat-input" placeholder="Escreva a sua mensagem…" maxlength="300" autocomplete="off">
          <button type="button" id="chat-send-btn">Enviar</button>
        </div>
      </div>
    </div>
  `;
  document.body.appendChild(widgetContainer);

  const toggleBtn = document.getElementById('chat-toggle-btn');
  const chatBox = document.getElementById('chat-box');
  const closeBtn = document.getElementById('chat-close-btn');
  const chatBody = chatBox.querySelector('.chat-body');
  const chatInput = document.getElementById('chat-input');
  const sendBtn = document.getElementById('chat-send-btn');
  const emojiBtn = document.getElementById('chat-emoji-btn');

  // Hora no formato brasileiro: 14:23
  const hora = () => new Date().toLocaleTimeString('pt-BR', {
    timeZone: 'America/Sao_Paulo', hour: '2-digit', minute: '2-digit'
  });

  document.getElementById('dynamic-timestamp').textContent = `Hoje • ${hora()}`;
  document.getElementById('dynamic-agent-time').textContent = `Assistente Agnello • ${hora()}`;

  function abrir() {
    chatBox.classList.remove('hidden');
    setTimeout(() => chatBox.classList.add('active'), 10);
    toggleBtn.setAttribute('aria-expanded', 'true');
    chatInput.focus();
  }

  function fechar() {
    chatBox.classList.remove('active');
    setTimeout(() => chatBox.classList.add('hidden'), 300);
    toggleBtn.setAttribute('aria-expanded', 'false');
    toggleBtn.focus();
  }

  toggleBtn.addEventListener('click', () => (chatBox.classList.contains('active') ? fechar() : abrir()));
  closeBtn.addEventListener('click', fechar);
  chatBox.addEventListener('keydown', (e) => { if (e.key === 'Escape') fechar(); });

  function adicionarMensagemCliente(texto) {
    const wrapper = document.createElement('div');
    wrapper.className = 'chat-message-wrapper user-message';
    wrapper.style.cssText = 'justify-content: flex-end; margin-top: 12px;';
    const bolha = document.createElement('div');
    bolha.className = 'chat-bubble';
    bolha.style.cssText = 'background-color: #5c1d24; color: #fff;';
    bolha.textContent = texto; // textContent: o que o cliente escreve nunca vira HTML
    wrapper.appendChild(bolha);

    const tempo = document.createElement('div');
    tempo.className = 'chat-msg-time';
    tempo.style.cssText = 'text-align: right; margin-left: 0; margin-right: 5px;';
    tempo.textContent = `Você • ${hora()}`;

    chatBody.append(wrapper, tempo);
  }

  function adicionarResposta(html) {
    chatBody.insertAdjacentHTML('beforeend', `
      <div class="chat-message-wrapper" style="margin-top: 12px;">
        <div class="chat-avatar" aria-hidden="true">AG</div>
        <div class="chat-bubble">${html}</div>
      </div>
      <div class="chat-msg-time">Assistente Agnello • ${hora()}</div>`);
    chatBody.scrollTop = chatBody.scrollHeight;
  }

  function enviarMensagem() {
    const texto = chatInput.value.trim();
    if (!texto) return;
    adicionarMensagemCliente(texto);
    chatInput.value = '';
    chatBody.scrollTop = chatBody.scrollHeight;
    setTimeout(() => adicionarResposta(responder(texto)), 600);
  }

  sendBtn.addEventListener('click', enviarMensagem);
  chatInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      enviarMensagem();
    }
  });

  emojiBtn.addEventListener('click', () => {
    chatInput.value += '🍷';
    chatInput.focus();
  });
})();
