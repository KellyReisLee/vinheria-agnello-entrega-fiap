package com.agnello.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;

/** Uma linha do histórico do pedido: "Pago → Em separação, por Kelly, em 16/10 14:30". */
public class RegistroStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private StatusPedido anterior;
    private StatusPedido novo;
    private String nomeAdministrador;
    private OffsetDateTime criadoEm;
    /** true quando foi o próprio cliente (ex.: cancelou um pedido não pago). */
    private boolean peloCliente;
    /** true quando foi o Mercado Pago (pagamento aprovado automaticamente). */
    private boolean peloGateway;

    public Date getData() {
        return criadoEm == null ? null : Date.from(criadoEm.toInstant());
    }

    public StatusPedido getAnterior() { return anterior; }
    public void setAnterior(StatusPedido anterior) { this.anterior = anterior; }

    public StatusPedido getNovo() { return novo; }
    public void setNovo(StatusPedido novo) { this.novo = novo; }

    public String getNomeAdministrador() { return nomeAdministrador; }
    public void setNomeAdministrador(String nomeAdministrador) { this.nomeAdministrador = nomeAdministrador; }

    public boolean isPeloCliente() { return peloCliente; }
    public void setPeloCliente(boolean peloCliente) { this.peloCliente = peloCliente; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public boolean isPeloGateway() { return peloGateway; }
    public void setPeloGateway(boolean peloGateway) { this.peloGateway = peloGateway; }
}
