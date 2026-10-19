// js/pages/catalogo.js
// A busca, os filtros, a ordenação e a paginação são feitos NO SERVIDOR (SQL):
// o formulário #form-catalogo é um GET normal e funciona mesmo sem JavaScript.
// Este arquivo só torna a experiência mais rápida:
//  - escolher um tipo, país, ordenação ou "só disponíveis" aplica logo o filtro;
//  - o "×" limpa a pesquisa;
//  - campos vazios não aparecem no endereço (/catalogo?tipo=tinto e não ?busca=&tipo=tinto&pais=...).
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('form-catalogo');
  if (!form) return;

  const inputBusca = document.getElementById('input-busca');
  const btnLimparBusca = form.querySelector('.agnello-search-clear');
  const botaoAplicar = form.querySelector('.agnello-btn-aplicar');

  // Remove do endereço os campos vazios antes de enviar
  form.addEventListener('submit', () => {
    form.querySelectorAll('input[name], select[name]').forEach((campo) => {
      const vazio = (campo.type === 'radio' || campo.type === 'checkbox')
        ? !campo.checked || campo.value === ''
        : campo.value.trim() === '';
      // Desativado = não vai no GET; volta ao normal se o usuário regressar (bfcache)
      if (vazio) campo.disabled = true;
    });
    if (botaoAplicar) botaoAplicar.setAttribute('aria-busy', 'true');
    form.classList.add('is-carregando');
  });

  window.addEventListener('pageshow', () => {
    form.querySelectorAll('[disabled]').forEach((c) => { c.disabled = false; });
    form.classList.remove('is-carregando');
  });

  const enviar = () => (form.requestSubmit ? form.requestSubmit() : form.submit());

  // Tipo, país, ordenação e "só disponíveis": aplica ao mudar
  form.querySelectorAll('input[name="tipo"], select[name="pais"], select[name="ordem"], input[name="disponiveis"]')
    .forEach((campo) => campo.addEventListener('change', enviar));

  // Botões de tipo: realça o escolhido logo ao clicar (antes da página recarregar)
  form.querySelectorAll('input[name="tipo"]').forEach((radio) => {
    radio.addEventListener('change', () => {
      form.querySelectorAll('.agnello-filter-btn').forEach((l) => {
        l.classList.toggle('agnello-ativo', l.contains(radio));
      });
    });
  });

  if (inputBusca && btnLimparBusca) {
    inputBusca.addEventListener('input', () => {
      btnLimparBusca.hidden = inputBusca.value === '';
    });
    btnLimparBusca.addEventListener('click', () => {
      const tinhaBusca = new URLSearchParams(location.search).has('busca');
      inputBusca.value = '';
      btnLimparBusca.hidden = true;
      // Se a página já mostrava uma busca, recarrega sem ela; senão só limpa o campo
      if (tinhaBusca) enviar(); else inputBusca.focus();
    });
  }

  // Vindo da lupa do header sem termo (/catalogo#input-busca): foca a pesquisa
  if (location.hash === '#input-busca' && inputBusca) {
    inputBusca.focus({ preventScroll: true });
  }

  // Depois de mudar de página/filtro, leva o usuário aos resultados
  // (só quando veio de um clique na própria página, não ao abrir um link compartilhado)
  const params = new URLSearchParams(location.search);
  const resultados = document.getElementById('catalogo-resultados');
  if (resultados && params.toString() && document.referrer.includes('/catalogo')) {
    resultados.scrollIntoView({ block: 'start' });
  }
});
