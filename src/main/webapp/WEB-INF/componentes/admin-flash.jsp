<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Aviso depois de salvar ("Alterações salvas") ou de um erro. --%>
<c:if test="${not empty flash}">
  <div class="adm-aviso adm-aviso--${flashTipo eq 'erro' ? 'erro' : 'sucesso'}" role="${flashTipo eq 'erro' ? 'alert' : 'status'}">
    <c:out value="${flash}" />
  </div>
</c:if>
<c:if test="${not empty erro}">
  <div class="adm-aviso adm-aviso--erro" role="alert"><c:out value="${erro}" /></div>
</c:if>
