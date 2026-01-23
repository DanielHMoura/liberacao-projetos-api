package com.metrica.liberacao.dto;

import java.math.BigDecimal;

public class LiberacaoResponse {

    private boolean acessoValido;

    // 🔹 STATUS (FONTE DA VERDADE)
    private String statusAnteprojeto;   // AGUARDANDO_PAGAMENTO | PRONTO | PAGO
    private String statusExecutivo;

    // 🔹 Flags auxiliares (opcional, mas útil)
    private boolean anteprojetoLiberado;
    private boolean executivoLiberado;

    private BigDecimal precoAnteprojeto;
    private BigDecimal precoExecutivo;

    private Long projetoId;
    private String nomeCliente;

    private String mensagem;

    // ===== GETTERS / SETTERS =====

    public boolean isAcessoValido() { return acessoValido; }
    public void setAcessoValido(boolean acessoValido) { this.acessoValido = acessoValido; }

    public String getStatusAnteprojeto() { return statusAnteprojeto; }
    public void setStatusAnteprojeto(String statusAnteprojeto) {
        this.statusAnteprojeto = statusAnteprojeto;
    }

    public String getStatusExecutivo() { return statusExecutivo; }
    public void setStatusExecutivo(String statusExecutivo) {
        this.statusExecutivo = statusExecutivo;
    }

    public boolean isAnteprojetoLiberado() { return anteprojetoLiberado; }
    public void setAnteprojetoLiberado(boolean anteprojetoLiberado) {
        this.anteprojetoLiberado = anteprojetoLiberado;
    }

    public boolean isExecutivoLiberado() { return executivoLiberado; }
    public void setExecutivoLiberado(boolean executivoLiberado) {
        this.executivoLiberado = executivoLiberado;
    }

    public BigDecimal getPrecoAnteprojeto() { return precoAnteprojeto; }
    public void setPrecoAnteprojeto(BigDecimal precoAnteprojeto) {
        this.precoAnteprojeto = precoAnteprojeto;
    }

    public BigDecimal getPrecoExecutivo() { return precoExecutivo; }
    public void setPrecoExecutivo(BigDecimal precoExecutivo) {
        this.precoExecutivo = precoExecutivo;
    }

    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
