// Script Dinâmico para Carrinho, Frete e Identificação de Cliente
document.addEventListener('DOMContentLoaded', function () {
  
  // ==========================================
  // PARTE 1: Carrinho, Resumo e Frete
  // ==========================================
  
  let rawCart = localStorage.getItem('agnello_carrinho') || localStorage.getItem('agnello_cart') || '[]';
  let cartItems = [];

  try {
    cartItems = JSON.parse(rawCart);
  } catch (e) {
    console.error("Erro ao converter o carrinho do localStorage:", e);
    cartItems = [];
  }

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

        let rawPrice = item.price || item.preco || 0;
        let numericPrice = 0;

        if (typeof rawPrice === 'string') {
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
  // CONFIGURAÇÃO DE ROTA (Evita Erro 404 no Render)
  // ==========================================
  const baseUrl = window.location.origin;
  const pathParts = window.location.pathname.split('/');
  const contextPath = pathParts.length > 2 && !pathParts[1].includes('html') && !pathParts[1].includes('jsp') ? '/' + pathParts[1] : '';
  const checkoutUrl = baseUrl + contextPath + '/checkout';

  // ==========================================
  // PARTE 1.5: Verificação de Sessão Ativa ao Carregar (Mantém logado ao retornar)
  // ==========================================
  const serverIsLoggedInput = document.getElementById('server-is-logged');
  const serverUserEmailInput = document.getElementById('server-user-email');

  if (serverIsLoggedInput && serverIsLoggedInput.value === 'true') {
    const emailField = document.getElementById('checkout-email');
    const passwordGroup = document.getElementById('password-group');
    const btnCheckEmail = document.getElementById('btn-check-email');
    const btnEntrarSenha = document.getElementById('btn-entrar-senha');

    if (emailField && serverUserEmailInput) {
      emailField.value = serverUserEmailInput.value;
      emailField.readOnly = true; // Trava o e-mail
    }

    if (btnCheckEmail) {
      btnCheckEmail.textContent = 'Alterar';
      btnCheckEmail.id = 'btn-alterar-email';
      
      // Permite que o cliente clique em "Alterar" caso queira trocar de e-mail/conta
      btnCheckEmail.onclick = () => {
        emailField.readOnly = false;
        emailField.value = '';
        passwordGroup.style.display = 'none';
        btnCheckEmail.textContent = 'Continuar';
        btnCheckEmail.id = 'btn-check-email';
      };
    }

    if (passwordGroup) {
      passwordGroup.style.display = 'block';
      
      const senhaField = document.getElementById('checkout-senha');
      if (senhaField) {
        senhaField.value = '********';
        senhaField.readOnly = true;
      }

      if (btnEntrarSenha) {
        btnEntrarSenha.textContent = 'Logado ✓';
        btnEntrarSenha.style.background = '#6d3240';
        btnEntrarSenha.style.color = '#fff';
        btnEntrarSenha.disabled = true;
      }
    }
  }

  // ==========================================
  // PARTE 2: Verificação de E-mail (Apenas Consulta)
  // ==========================================
  const btnCheckEmail = document.getElementById('btn-check-email');

  if (btnCheckEmail) {
    btnCheckEmail.addEventListener('click', function() {
      const emailInput = document.getElementById('checkout-email');
      const email = emailInput ? emailInput.value.trim() : '';
      const passwordGroup = document.getElementById('password-group');
      const registerGroup = document.getElementById('register-group');
      const inputTipoCliente = document.getElementById('tipo_cliente');
      const emailErrorBox = document.getElementById('email-error-box');
      
      // Oculta o box de erro caso estivesse visível anteriormente
      if (emailErrorBox) emailErrorBox.style.display = 'none';

      // Validação substituindo o alert pelo box estilizado
      if (!email || !email.includes('@')) {
        if (emailErrorBox) {
          emailErrorBox.textContent = 'Por favor, informe um e-mail válido.';
          emailErrorBox.style.display = 'block';
        }
        if (emailInput) emailInput.focus();
        return;
      }

      const tipoClienteVal = inputTipoCliente ? inputTipoCliente.value : 'PF';
      const bodyData = 'email=' + encodeURIComponent(email) + '&tipo_cliente=' + encodeURIComponent(tipoClienteVal);

      fetch(checkoutUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: bodyData
      })
      .then(response => response.json())
      .then(data => {
        emailInput.readOnly = true;
        btnCheckEmail.textContent = 'Alterar';

        if (data.existe) {
          if (passwordGroup) passwordGroup.style.display = 'block';
          if (registerGroup) registerGroup.style.display = 'none';
        } else {
          if (passwordGroup) passwordGroup.style.display = 'none';
          if (registerGroup) registerGroup.style.display = 'block';
        }
      })
      .catch(error => {
        console.error('Erro ao verificar o e-mail:', error);
        if (emailErrorBox) {
          emailErrorBox.textContent = 'Ocorreu um erro ao verificar o e-mail. Tente novamente.';
          emailErrorBox.style.display = 'block';
        }
      });
    });
  }

  // ==========================================
  // PARTE 3: Validação de Senha (Cliente Antigo)
  // ==========================================
  const btnEntrarSenha = document.getElementById('btn-entrar-senha');

  if (btnEntrarSenha) {
    btnEntrarSenha.addEventListener('click', function(event) {
      event.preventDefault();
      
      const senhaInput = document.getElementById('checkout-senha');
      const emailInput = document.getElementById('checkout-email');
      const senhaErrorBox = document.getElementById('senha-error-box');
      
      const senha = senhaInput ? senhaInput.value.trim() : '';
      const email = emailInput ? emailInput.value.trim() : '';
      
      if (!senha) {
        alert('Por favor, digite sua senha.');
        if (senhaInput) senhaInput.focus();
        return;
      }

      if (!email) {
        alert('E-mail não identificado. Por favor, redigite seu e-mail.');
        return;
      }

      const params = new URLSearchParams();
      params.append('email', email);
      params.append('senha', senha);

      fetch(checkoutUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
      })
      .then(response => response.json())
      .then(data => {
        if (data.sucesso) {
          if (senhaErrorBox) senhaErrorBox.style.display = 'none';
          
          btnEntrarSenha.textContent = 'Logado ✓';
          btnEntrarSenha.style.background = '#6d3240';
          if (senhaInput) senhaInput.readOnly = true;
          
          const passwordGroup = document.getElementById('password-group');
          if (passwordGroup) passwordGroup.style.opacity = '0.9';

          // ADICIONADO: Recarrega a página após 1 segundo para atualizar o cabeçalho
          setTimeout(() => {
            location.reload();
          }, 1000);

        } else {
          if (senhaErrorBox) {
            senhaErrorBox.style.display = 'block';
            senhaErrorBox.style.background = '#f8d7da';
            senhaErrorBox.style.color = '#721c24';
            senhaErrorBox.style.border = '1px solid #f5c6cb';
            senhaErrorBox.style.padding = '0.75rem';
            senhaErrorBox.style.borderRadius = '6px';
            senhaErrorBox.style.fontSize = '0.85rem';
          }
          if (senhaInput) {
            senhaInput.value = '';
            senhaInput.focus();
          }
        }
      })
      .catch(error => {
        console.error('Erro na requisição fetch:', error);
        alert('Ocorreu um erro ao validar a senha. Verifique o console do navegador.');
      });
    });
  }

  // ==========================================
  // PARTE 4: Alternância PF e PJ no Cadastro
  // ==========================================
  const tabPf = document.querySelector('.tab-pf');
  const tabPj = document.querySelector('.tab-pj');
  const fieldsPf = document.getElementById('fields-pf');
  const fieldsPj = document.getElementById('fields-pj');
  const inputTipoCliente = document.getElementById('tipo_cliente');

  if (tabPf && tabPj) {
    tabPf.addEventListener('click', function() {
      if (inputTipoCliente) inputTipoCliente.value = 'PF';

      tabPf.classList.add('active');
      tabPf.style.color = '#4a1525';
      tabPf.style.borderBottom = '2px solid #4a1525';
      tabPf.style.paddingBottom = '0.3rem';

      tabPj.classList.remove('active');
      tabPj.style.color = '#999';
      tabPj.style.borderBottom = 'none';

      if (fieldsPf) fieldsPf.style.display = 'block';
      if (fieldsPj) fieldsPj.style.display = 'none';
    });

    tabPj.addEventListener('click', function() {
      if (inputTipoCliente) inputTipoCliente.value = 'PJ';

      tabPj.classList.add('active');
      tabPj.style.color = '#4a1525';
      tabPj.style.borderBottom = '2px solid #4a1525';
      tabPj.style.paddingBottom = '0.3rem';

      tabPf.classList.remove('active');
      tabPf.style.color = '#999';
      tabPf.style.borderBottom = 'none';

      if (fieldsPj) fieldsPj.style.display = 'block';
      if (fieldsPf) fieldsPf.style.display = 'none';
    });
  }

  // ==========================================
  // PARTE 5: Submissão do Cadastro Completo (Com caixas estilizadas)
  // ==========================================
  const btnFinalizarCadastro = document.getElementById('btn-finalizar-cadastro');

  if (btnFinalizarCadastro) {
    btnFinalizarCadastro.addEventListener('click', function(event) {
      event.preventDefault();

      const formEl = document.getElementById('form-identification');
      const senhaInput = document.getElementById('novo-senha');
      const senhaValor = senhaInput ? senhaInput.value.trim() : '';
      
      const successBox = document.getElementById('register-success-box');
      const errorBox = document.getElementById('register-error-box');
      const errorMsgText = document.getElementById('register-error-msg');

      if (successBox) successBox.style.display = 'none';
      if (errorBox) errorBox.style.display = 'none';

      if (!senhaValor || senhaValor.length < 6) {
        if (errorBox && errorMsgText) {
          errorMsgText.textContent = 'A senha deve ter no mínimo 6 caracteres.';
          errorBox.style.display = 'block';
        }
        if (senhaInput) senhaInput.focus();
        return;
      }

      const formData = new URLSearchParams(new FormData(formEl));
      formData.set('senha', senhaValor);

      fetch(checkoutUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData.toString()
      })
      .then(response => response.json())
      .then(data => {
        if (data.sucesso) {
          if (successBox) {
            successBox.style.display = 'block';
          }
          btnFinalizarCadastro.disabled = true;
          btnFinalizarCadastro.style.opacity = '0.6';

          setTimeout(() => {
            location.reload();
          }, 1500);

        } else {
          if (errorBox && errorMsgText) {
            errorMsgText.textContent = data.mensagem || 'Não foi possível concluir o cadastro. Verifique os dados e tente novamente.';
            errorBox.style.display = 'block';
            errorBox.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
          }
        }
      })
      .catch(error => {
        console.error('Erro na requisição de cadastro:', error);
        if (errorBox && errorMsgText) {
          errorMsgText.textContent = 'Ocorreu um erro de conexão com o servidor. Tente novamente.';
          errorBox.style.display = 'block';
        }
      });
    });
  }

});