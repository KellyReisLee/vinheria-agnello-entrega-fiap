// Script Dinâmico para Carrinho, Frete e Identificação de Cliente
document.addEventListener('DOMContentLoaded', function () {
  
  // ==========================================
  // PARTE 1: Carrinho, Resumo e Frete
  // ==========================================
  
  // 1. Busca diretamente pela chave correta que vimos no print: 'agnello_carrinho'
  let rawCart = localStorage.getItem('agnello_carrinho') || localStorage.getItem('agnello_cart') || '[]';
  let cartItems = [];

  try {
    cartItems = JSON.parse(rawCart);
  } catch (e) {
    console.error("Erro ao converter o carrinho do localStorage:", e);
    cartItems = [];
  }

  // Fallback caso esteja vazio
  if (!cartItems || cartItems.length === 0) {
    cartItems = [
      { nome: 'Bodega Chacra Barda Pinot Noir', preco: 'R$ 289,00', quantity: 1 }
    ];
  }

  const itemsListContainer = document.querySelector('.summary-items-list');

  function renderCartSummary() {
    if (!itemsListContainer) return;
    
    itemsListContainer.innerHTML = '';
    let subtotal = 0;

    if (cartItems.length === 0) {
      itemsListContainer.innerHTML = '<span class="empty-cart">Sua sacola está vazia.</span>';
    } else {
      cartItems.forEach(item => {
        let itemName = item.name || item.nome || item.titulo || 'Vinho Agnello';
        let itemQty = Number(item.quantity || item.qtd || item.quantidade || 1);

        // Trata o preço caso venha como string "R$ 289,00" ou número
        let rawPrice = item.price || item.preco || 0;
        let numericPrice = 0;

        if (typeof rawPrice === 'string') {
          // Remove "R$", espaços, troca ponto de milhar se houver e substitui vírgula por ponto
          numericPrice = parseFloat(
            rawPrice.replace(/[R$\s]/g, '').replace(/\./g, '').replace(',', '.')
          ) || 0;
        } else {
          numericPrice = Number(rawPrice) || 0;
        }

        let itemTotal = numericPrice * itemQty;
        subtotal += itemTotal;

        const itemDiv = document.createElement('div');
        itemDiv.className = 'summary-item';
        itemDiv.innerHTML = `
          <span class="item-name">${itemName} <small>x${itemQty}</small></span>
          <span class="item-val">R$ ${itemTotal.toFixed(2).replace('.', ',')}</span>
        `;
        itemsListContainer.appendChild(itemDiv);
      });
    }

    updateTotals(subtotal);
  }

  function updateTotals(subtotal) {
    const selectedShippingInput = document.querySelector('input[name="shipping"]:checked');
    let shippingCost = 25.00;

    if (selectedShippingInput) {
      const parentOption = selectedShippingInput.closest('.shipping-option');
      if (parentOption && parentOption.querySelector('.ship-price').classList.contains('free')) {
        shippingCost = 0.00;
      }
    }

    const isPixActive = document.querySelector('.pay-tab[data-target="pix"]') &&
      document.querySelector('.pay-tab[data-target="pix"]').classList.contains('active');
    let discount = isPixActive ? subtotal * 0.05 : 0;

    let total = (subtotal + shippingCost) - discount;

    const calcRows = document.querySelectorAll('.summary-totals .calc-row');
    if (calcRows.length >= 4) {
      calcRows[0].querySelectorAll('span')[1].textContent = `R$ ${subtotal.toFixed(2).replace('.', ',')}`;
      calcRows[1].querySelectorAll('span')[1].textContent = shippingCost === 0 ? 'Grátis' : `R$ ${shippingCost.toFixed(2).replace('.', ',')}`;

      const discountRow = calcRows[2];
      if (isPixActive) {
        discountRow.style.display = 'flex';
        discountRow.querySelectorAll('span')[1].textContent = `- R$ ${discount.toFixed(2).replace('.', ',')}`;
      } else {
        discountRow.style.display = 'none';
      }

      calcRows[3].querySelectorAll('span')[1].textContent = `R$ ${total.toFixed(2).replace('.', ',')}`;
    }
  }

  document.querySelectorAll('input[name="shipping"]').forEach(radio => {
    radio.addEventListener('change', function () {
      document.querySelectorAll('.shipping-option').forEach(opt => opt.classList.remove('selected'));
      this.closest('.shipping-option').classList.add('selected');
      renderCartSummary();
    });
  });

  document.querySelectorAll('.pay-tab').forEach(tab => {
    tab.addEventListener('click', function () {
      document.querySelectorAll('.pay-tab').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.pay-content').forEach(c => c.classList.remove('active'));

      this.classList.add('active');
      const targetId = this.getAttribute('data-target');
      document.getElementById(targetId).classList.add('active');

      renderCartSummary();
    });
  });

  renderCartSummary();


  // ==========================================
  // PARTE 2: Verificação de E-mail (Ajax / Servlet)
  // ==========================================
  
  const btnCheckEmail = document.getElementById('btn-check-email');
  
  if (btnCheckEmail) {
    btnCheckEmail.addEventListener('click', function() {
      const emailInput = document.getElementById('checkout-email');
      const email = emailInput.value.trim();
      const passwordGroup = document.getElementById('password-group');
      
      if (!email || !email.includes('@')) {
        alert('Por favor, informe um e-mail válido.');
        emailInput.focus();
        return;
      }

      // Pega o contexto da aplicação dinamicamente ou usa a rota relativa
      const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

      // Requisição AJAX para o CheckoutController
      fetch(contextPath + '/checkout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'email=' + encodeURIComponent(email)
      })
      .then(response => response.json())
      .then(data => {
        if (data.existe) {
          // CENÁRIO A: Cliente antigo -> Trava o campo e pede a senha
          emailInput.readOnly = true;
          if (passwordGroup) passwordGroup.style.display = 'block';
          btnCheckEmail.textContent = 'Alterar';
          alert('Encontramos uma conta com este e-mail. Digite sua senha para recuperar seus dados salvos.');
        } else {
          // CENÁRIO B: Cliente novo -> E-mail validado para compra rápida (Guest)
          emailInput.readOnly = true;
          btnCheckEmail.textContent = 'Confirmado ✓';
          btnCheckEmail.style.background = '#2e7d32'; // Verde de sucesso
          alert('E-mail verificado! Prossiga para o endereço de entrega.');
        }
      })
      .catch(error => {
        console.error('Erro ao verificar o e-mail:', error);
        alert('Ocorreu um erro ao verificar o e-mail. Tente novamente.');
      });
    });
  }

  // ==========================================
  // PARTE 3: Validação de Senha (Cliente Antigo)
  // ==========================================

  const btnEntrarSenha = document.getElementById('btn-entrar-senha');

  if (btnEntrarSenha) {
    // ADICIONE O 'event' AQUI DENTRO DOS PARÊNTESES:
    btnEntrarSenha.addEventListener('click', function(event) {
      event.preventDefault(); // <--- ESTA LINHA É OBRIGATÓRIA AQUI!
      
      const senhaInput = document.getElementById('checkout-senha');
      const senha = senhaInput ? senhaInput.value.trim() : '';
      
      if (!senha) {
        alert('Por favor, digite sua senha.');
        if (senhaInput) senhaInput.focus();
        return;
      }

      const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

      // Requisição AJAX para validar a senha no CheckoutController
      fetch(contextPath + '/checkout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'senha=' + encodeURIComponent(senha)
      })
      .then(response => response.json())
      .then(data => {
        if (data.sucesso) {
          // SENHA CORRETA: Loga o cliente e dá feedback visual de sucesso
          alert('Login realizado com sucesso! Seus dados foram carregados.');
          btnEntrarSenha.textContent = 'Logado ✓';
          btnEntrarSenha.style.background = '#2e7d32'; // Verde de sucesso
          if (senhaInput) senhaInput.readOnly = true;
          
          const passwordGroup = document.getElementById('password-group');
          if (passwordGroup) passwordGroup.style.opacity = '0.9';
        } else {
          // SENHA ERRADA: Avisa que está incorreto
          alert('Senha incorreta. Tente novamente.');
          if (senhaInput) {
            senhaInput.value = '';
            senhaInput.focus();
          }
        }
      })
      .catch(error => {
        console.error('Erro ao validar a senha:', error);
        alert('Ocorreu um erro ao validar a senha. Tente novamente.');
      });
    });
  }

});