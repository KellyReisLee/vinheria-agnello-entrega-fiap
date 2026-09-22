document.addEventListener("DOMContentLoaded", function() {
  const modal = document.getElementById("modalDeletarConta");
  const modalSucesso = document.getElementById("modalSucessoDelecao");
  const linkDeletar = document.getElementById("linkDeletarConta");
  const btnCancelar = document.getElementById("btnCancelarModal");
  const inputEmail = document.getElementById("emailConfirmacao");
  const avisoBox = document.getElementById("avisoCopyPaste");
  const formDelecao = document.querySelector(".modal-form");

  // Função auxiliar unificada para exibir mensagens na mesma caixinha inline
  function mostrarAviso(mensagem) {
    if (avisoBox) {
      avisoBox.innerText = mensagem;
      avisoBox.style.display = "block";
    }
  }

  // Controlo de abertura e fecho do modal principal
  if (linkDeletar && modal) {
    linkDeletar.addEventListener("click", function(event) {
      event.preventDefault();
      modal.style.display = "flex";
      if (inputEmail) {
        inputEmail.value = "";
        inputEmail.classList.remove("email-match");
        inputEmail.focus();
      }
      if (avisoBox) avisoBox.style.display = "none";
    });
  }

  if (btnCancelar && modal) {
    btnCancelar.addEventListener("click", function() {
      modal.style.display = "none";
    });
  }

  if (modal) {
    modal.addEventListener("click", function(event) {
      if (event.target === modal) {
        modal.style.display = "none";
      }
    });
  }

  // --- Bloquear Copy/Paste ---
  if (inputEmail) {
    const emailEsperado = inputEmail.getAttribute("data-email-esperado").trim();

    ['copy', 'paste', 'cut'].forEach(function(evento) {
      inputEmail.addEventListener(evento, function(e) {
        e.preventDefault();
        mostrarAviso("Por razões de segurança, não é permitido copiar e colar. Por favor, digite o seu e-mail manualmente.");
      });
    });

    inputEmail.addEventListener("input", function() {
      const valorDigitado = inputEmail.value.trim();

      // Esconde o aviso assim que o utilizador começa a corrigir ou digitar de novo
      if (avisoBox) {
        avisoBox.style.display = "none";
      }

      if (valorDigitado === emailEsperado) {
        inputEmail.classList.add("email-match");
      } else {
        inputEmail.classList.remove("email-match");
      }
    });
  }

  // --- Intercetar o Envio do Formulário (AJAX) ---
  if (formDelecao) {
    formDelecao.addEventListener("submit", function(event) {
      event.preventDefault(); // Impede o envio padrão que recarregava a página

      // Converte os dados para URL-encoded para o request.getParameter() ler no Java
      const formData = new FormData(formDelecao);
      const urlEncodedData = new URLSearchParams(formData);
      const actionUrl = formDelecao.getAttribute("action");

      // Envia os dados para o servidor em segundo plano
      fetch(actionUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: urlEncodedData
      })
      .then(response => {
        if (!response.ok) {
          // Se o servidor retornar erro (ex: e-mail incorreto), captura a mensagem
          return response.text().then(errorMessage => {
            throw new Error(errorMessage || "Erro ao deletar conta.");
          });
        }
        return response.text();
      })
      .then(data => {
        // Sucesso real: Fecha o modal de perigo e exibe o modal de sucesso
        if (modal) modal.style.display = "none";
        if (modalSucesso) modalSucesso.style.display = "flex";

        // Limpa dados locais da sessão do browser
        localStorage.clear();
        sessionStorage.clear();

        // Aguarda 4 segundos e redireciona para a página principal deslogado
        setTimeout(function() {
          if (window.location.hostname.includes("onrender.com")) {
            window.location.href = "/home";
          } else {
            const pathParts = window.location.pathname.split('/');
            const contexto = pathParts.length > 1 ? "/" + pathParts[1] : "";
            window.location.href = contexto + "/home";
          }
        }, 4000);
      })
      .catch(error => {
        console.error("Erro ao processar exclusão:", error);
        
        // Exibe o erro do servidor exatamente na caixinha inline abaixo do input
        const mensagemErro = error.message || "O e-mail digitado não corresponde à sua conta ativa.";
        mostrarAviso("Não foi possível excluir a conta: " + mensagemErro);
      });
    });
  }
});