<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- MODAL DE CONFIRMAÇÃO DE EXCLUSÃO DE CONTA -->
<div id="modalDeletarConta" class="agnello-modal-overlay">
  <div class="agnello-modal-card">
    <div class="agnello-modal-header">
      <span class="sub-tag-alert">ZONA DE PERIGO</span>
      <h2>Deseja realmente encerrar sua conta?</h2>
    </div>
    
    <div class="agnello-modal-body">
      <p>Esta ação é <strong>irreversível</strong>. Ao prosseguir com a exclusão:</p>
      <ul class="modal-warning-list">
        <li>Perderá o acesso permanente ao seu histórico de pedidos e rótulos favoritos.</li>
        <li>O saldo de cashback e vantagens do Clube Agnello serão eliminados.</li>
      </ul>

      <!-- Aviso Importante sobre Pedidos em Andamento -->
      <div class="modal-alert-warning">
        <strong>Aviso Importante:</strong> Se você possuir algum pedido em andamento ou a caminho, é estritamente necessário que aguarde o recebimento do produto para finalizar o cancelamento da conta.
      </div>

      <!-- Formulário de Confirmação por E-mail -->
      <form action="<c:url value='/deletar-conta'/>" method="POST" class="modal-form">
        <div class="modal-input-group">
          <label for="emailConfirmacao" class="modal-label-main">Confirme seu e-mail cadastrado:</label>
          <span class="modal-label-sub">Para prosseguir, digite: <strong>${clienteLogado.email}</strong></span>
          
          <input type="email" id="emailConfirmacao" name="emailConfirmacao" 
                 class="modal-input-field" required 
                 placeholder="Digite o seu e-mail aqui"
                 data-email-esperado="${clienteLogado.email}">
                 
          <!-- Caixa de mensagem unificada (serve para Copy/Paste e para Erros do Servidor) -->
          <div id="avisoCopyPaste" class="modal-inline-warning" style="display: none;">
            Por razões de segurança, não é permitido copiar e colar. Por favor, digite o seu e-mail manualmente.
          </div>
        </div>
       
        <div class="modal-actions">
          <button type="button" id="btnCancelarModal" class="btn-secondary-agnello">Cancelar</button>
          <button type="submit" class="btn-danger-agnello">Deletar Conta</button>
        </div>
      </form>
    </div>
  </div>
</div>