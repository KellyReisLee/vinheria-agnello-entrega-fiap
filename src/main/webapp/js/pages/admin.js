// js/pages/admin.js
// Pequenas ajudas do painel. Tudo funciona sem JavaScript; isto só melhora:
//  - pede confirmação antes de ações sensíveis (cancelar pedido, desativar vinho);
//  - impede o clique duplo em "Salvar" (que enviaria o formulário duas vezes);
//  - mostra a foto escolhida antes de enviar e a prévia do selo de desconto.
document.addEventListener('DOMContentLoaded', () => {
  // ---------- Confirmação ----------
  document.querySelectorAll('form[data-confirmar]').forEach((form) => {
    form.addEventListener('submit', (e) => {
      if (!window.confirm(form.dataset.confirmar)) e.preventDefault();
    });
  });

  // ---------- Evita envio duplo ----------
  document.querySelectorAll('form[method="post"]').forEach((form) => {
    form.addEventListener('submit', (e) => {
      if (e.defaultPrevented) return;
      if (form.dataset.enviando) {
        e.preventDefault();
        return;
      }
      form.dataset.enviando = 'sim';
      form.querySelectorAll('button[type="submit"]').forEach((b) => { b.disabled = true; });
    });
  });

  // ---------- Prévia da foto ----------
  const arquivo = document.getElementById('imagem');
  const previa = document.getElementById('previa-foto');
  const vazia = document.getElementById('foto-vazia');
  if (arquivo && previa) {
    arquivo.addEventListener('change', () => {
      const f = arquivo.files[0];
      if (!f) return;
      if (f.size > 2 * 1024 * 1024) {
        window.alert('A foto tem mais de 2 MB. Escolha um arquivo menor.');
        arquivo.value = '';
        return;
      }
      previa.src = URL.createObjectURL(f);
      previa.hidden = false;
      if (vazia) vazia.hidden = true;
    });
  }

  // ---------- Prévia do selo de desconto ----------
  const preco = document.getElementById('preco');
  const precoAntigo = document.getElementById('precoAntigo');
  const avisoDesconto = document.getElementById('previa-desconto');
  const paraNumero = (t) => {
    let v = String(t || '').replace(/[R$\s]/g, '');
    if (v.includes(',')) v = v.replace(/\./g, '').replace(',', '.');
    return parseFloat(v);
  };
  function atualizarDesconto() {
    const p = paraNumero(preco.value);
    const a = paraNumero(precoAntigo.value);
    if (p > 0 && a > p) {
      avisoDesconto.textContent = `Selo na loja: ${Math.round((1 - p / a) * 100)}% OFF`;
    } else if (precoAntigo.value.trim() && a <= p) {
      avisoDesconto.textContent = 'O preço "De" tem de ser maior que o preço atual.';
    } else {
      avisoDesconto.textContent = 'Com o preço "De" preenchido, o selo de desconto (ex.: 17% OFF) é calculado sozinho.';
    }
  }
  if (preco && precoAntigo && avisoDesconto) {
    preco.addEventListener('input', atualizarDesconto);
    precoAntigo.addEventListener('input', atualizarDesconto);
    atualizarDesconto();
  }
});
