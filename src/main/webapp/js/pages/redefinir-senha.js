/**
 * Alterna a visibilidade do campo de senha (password/text) e troca o ícone.
 */
function togglePasswordVisibility(fieldId, btn, urlOlhoAberto, urlOlhoFechado) {
  const inputField = document.getElementById(fieldId);
  const iconImg = btn.querySelector('img');

  if (inputField.type === "password") {
    inputField.type = "text";
    iconImg.src = urlOlhoAberto;
    iconImg.alt = "Ocultar Senha";
  } else {
    inputField.type = "password";
    iconImg.src = urlOlhoFechado;
    iconImg.alt = "Mostrar Senha";
  }
}

/**
 * Gerencia o redirecionamento automático após segundos definidos.
 */
function iniciarRedirecionamentoLogin(urlLogin, segundos = 4) {
  setTimeout(function() {
    window.location.href = urlLogin;
  }, segundos * 1000);
}