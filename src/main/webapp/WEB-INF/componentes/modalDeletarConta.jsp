<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Modais de exclusão de conta. Usam o elemento nativo <dialog>:
     fica fechado por padrão, prende o foco do teclado lá dentro e fecha com Esc.
     Comportamento em js/components/modal-deletar-conta.js --%>

<!-- 1) Confirmação -->
<dialog id="modalDeletarConta" class="conta-modal" aria-labelledby="modal-deletar-titulo">
  <form method="dialog" class="conta-modal-fechar-form">
    <button class="conta-modal-x" aria-label="Fechar">&times;</button>
  </form>

  <span class="conta-modal-eyebrow">Zona de perigo</span>
  <h2 id="modal-deletar-titulo">Deseja realmente excluir sua conta?</h2>

  <p>Esta ação é <strong>irreversível</strong>. Ao continuar:</p>
  <ul class="conta-modal-lista">
    <li>Você perde o acesso ao histórico de pedidos e rótulos favoritos.</li>
    <li>O saldo de cashback e as vantagens do Clube Agnello são eliminados.</li>
  </ul>

  <div class="conta-modal-aviso">
    <strong>Pedidos em andamento:</strong> se tiver algum pedido a caminho,
    aguarde a entrega antes de excluir a conta.
  </div>

  <form action="${pageContext.request.contextPath}/deletar-conta" method="POST" class="modal-form" novalidate>
    <label for="emailConfirmacao" class="conta-modal-label">Para confirmar, digite o seu e-mail:</label>
    <span class="conta-modal-sub"><c:out value="${clienteLogado.email}" /></span>
    <input type="email" id="emailConfirmacao" name="emailConfirmacao" class="conta-modal-input"
           autocomplete="off" required placeholder="Digite o seu e-mail"
           data-email-esperado="<c:out value='${clienteLogado.email}' />"
           aria-describedby="avisoCopyPaste">
    <p id="avisoCopyPaste" class="conta-modal-erro" role="alert" hidden></p>

    <div class="conta-modal-acoes">
      <button type="button" id="btnCancelarModal" class="conta-btn-secundario">Cancelar</button>
      <button type="submit" class="conta-btn-perigo" id="btnConfirmarExclusao" disabled>Excluir conta</button>
    </div>
  </form>
</dialog>

<!-- 2) Sucesso -->
<dialog id="modalSucessoDelecao" class="conta-modal conta-modal--sucesso" aria-labelledby="modal-sucesso-titulo">
  <div class="conta-modal-icone" aria-hidden="true">✓</div>
  <h2 id="modal-sucesso-titulo">Conta excluída</h2>
  <p>Lamentamos ver você partir. Enviamos um e-mail de confirmação com os detalhes.</p>
  <p class="conta-modal-redirect"><span class="spinner-dot" aria-hidden="true"></span>
    Voltando para a página inicial em <span id="segundos-saida">4</span> s…</p>
</dialog>
