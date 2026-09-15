// Script Dinâmico para Carrinho e Frete-- >
document.addEventListener('DOMContentLoaded', function () {
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
});