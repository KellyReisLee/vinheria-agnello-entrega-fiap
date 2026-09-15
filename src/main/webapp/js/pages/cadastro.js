document.addEventListener('DOMContentLoaded', () => {
  const tabBtns = document.querySelectorAll('.tab-btn');
  const camposPf = document.getElementById('campos-pf');
  const camposPj = document.getElementById('campos-pj');
  const inputTipoCliente = document.getElementById('tipo_cliente');

  tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      tabBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      const target = btn.getAttribute('data-target');
      
      if (target === 'pf-form') {
        camposPf.style.display = 'block';
        camposPj.style.display = 'none';
        inputTipoCliente.value = 'PF';
        toggleRequired(camposPf, true);
        toggleRequired(camposPj, false);
      } else {
        camposPf.style.display = 'none';
        camposPj.style.display = 'block';
        inputTipoCliente.value = 'PJ';
        toggleRequired(camposPj, true);
        toggleRequired(camposPf, false);
      }
    });
  });

  function toggleRequired(container, isRequired) {
    const inputs = container.querySelectorAll('input');
    inputs.forEach(input => {
      if (isRequired) {
        input.setAttribute('required', 'true');
      } else {
        input.removeAttribute('required');
        input.value = '';
      }
    });
  }
});