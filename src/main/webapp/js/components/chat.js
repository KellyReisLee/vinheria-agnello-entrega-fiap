(function () {
  // 1. Cria o container e injeta o HTML automaticamente no DOM
  const widgetContainer = document.createElement('div');
  widgetContainer.innerHTML = `
        <div id="chat-widget-container" class="chat-widget-container">
            <button id="chat-toggle-btn" class="chat-toggle-btn" aria-label="Abrir chat de atendimento">💬</button>

            <div id="chat-box" class="chat-box hidden">
                <div class="chat-header">
                    <h3>Sommelier Vinheria Agnello</h3>
                    <button id="chat-close-btn" aria-label="Fechar chat">×</button>
                </div>

                <div class="chat-body">
                    <div class="chat-timestamp" id="dynamic-timestamp">Hoje • --:--</div>
                    <div class="chat-terms-notice">
                        Chats may be retained and shared with third parties for customer support and other purposes. By continuing, you agree to our Terms of Use and Privacy Policy.
                    </div>

                    <div class="chat-system-join">
                        <strong>Agnello AI Agent</strong> aderiu • 2:23 PM
                    </div>

                    <div class="chat-message-wrapper">
                        <div class="chat-avatar">AA</div>
                        <div class="chat-bubble">
                            Olá, sou Agnello, o assistente digital para os Clientes da Vinheria Agnello! Estou feliz em ajudar você a encontrar o que está procurando.
                        </div>
                    </div>
                    <div class="chat-msg-time" id="dynamic-agent-time">Agnello AI Agent • --:--</div>
                </div>

                <div class="chat-footer">
                    <input type="file" id="chat-file-input" style="display: none;">
                    <button class="chat-action-icon" id="chat-attach-btn" aria-label="Anexar arquivo"><img src="./assets/icons/paperclip.svg" alt="Imagem de Paperclip">
                    </button>
                    <button class="chat-action-icon" id="chat-emoji-btn" aria-label="Inserir emoji">
                     <img src="./assets/icons/face-grinning.svg" alt="Imagem de emoji sorridente">
                    </button>
                    <input type="text" id="chat-input" placeholder="Escreva a sua mensagem...">
                    <button id="chat-send-btn">Enviar</button>
                </div>
            </div>
        </div>
    `;
  document.body.appendChild(widgetContainer);

  // 2. Inicializa toda a lógica e comportamentos após a injeção
  const toggleBtn = document.getElementById('chat-toggle-btn');
  const chatBox = document.getElementById('chat-box');
  const closeBtn = document.getElementById('chat-close-btn');
  const chatBody = document.querySelector('.chat-body');
  const chatInput = document.getElementById('chat-input');
  const sendBtn = document.getElementById('chat-send-btn');
  const fileInput = document.getElementById('chat-file-input');
  const attachBtn = document.getElementById('chat-attach-btn');
  const emojiBtn = document.getElementById('chat-emoji-btn');

  function getFormattedTime() {
    const now = new Date();
    return now.toLocaleTimeString('en-US', {
      timeZone: 'America/Sao_Paulo',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  }

  const timestampEl = document.getElementById('dynamic-timestamp');
  const agentTimeEl = document.getElementById('dynamic-agent-time');

  if (timestampEl) timestampEl.textContent = `Hoje • ${getFormattedTime()}`;
  if (agentTimeEl) agentTimeEl.textContent = `Agnello AI Agent • ${getFormattedTime()}`;

  toggleBtn.addEventListener('click', () => {
    if (chatBox.classList.contains('active')) {
      chatBox.classList.remove('active');
      setTimeout(() => chatBox.classList.add('hidden'), 300);
    } else {
      chatBox.classList.remove('hidden');
      setTimeout(() => chatBox.classList.add('active'), 10);
    }
  });

  closeBtn.addEventListener('click', () => {
    chatBox.classList.remove('active');
    setTimeout(() => chatBox.classList.add('hidden'), 300);
  });

  function handleSendMessage() {
    const messageText = chatInput.value.trim();
    if (messageText === '') return;

    const currentTime = getFormattedTime();

    const userMessageHTML = `
            <div class="chat-message-wrapper user-message" style="justify-content: flex-end; margin-top: 12px;">
                <div class="chat-bubble" style="background-color: #5c1d24; color: #fff;">
                    ${messageText}
                </div>
            </div>
            <div class="chat-msg-time" style="text-align: right; margin-left: 0; margin-right: 5px;">Você • ${currentTime}</div>
        `;

    chatBody.insertAdjacentHTML('beforeend', userMessageHTML);
    chatInput.value = '';
    chatBody.scrollTop = chatBody.scrollHeight;

    setTimeout(() => {
      const botResponseHTML = `
                <div class="chat-message-wrapper" style="margin-top: 12px;">
                    <div class="chat-avatar">AA</div>
                    <div class="chat-bubble">
                        Recebi sua dúvida! Em breve nosso sommelier responderá.
                    </div>
                </div>
                <div class="chat-msg-time">Agnello AI Agent • ${getFormattedTime()}</div>
            `;
      chatBody.insertAdjacentHTML('beforeend', botResponseHTML);
      chatBody.scrollTop = chatBody.scrollHeight;
    }, 1000);
  }

  sendBtn.addEventListener('click', handleSendMessage);
  chatInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      handleSendMessage();
    }
  });

  if (attachBtn && fileInput) {
    attachBtn.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', (e) => {
      const file = e.target.files[0];
      if (file) {
        chatInput.value = `[Arquivo anexado: ${file.name}]`;
      }
    });
  }

  if (emojiBtn) {
    emojiBtn.addEventListener('click', () => {
      chatInput.value += '🍷';
      chatInput.focus();
    });
  }
})();