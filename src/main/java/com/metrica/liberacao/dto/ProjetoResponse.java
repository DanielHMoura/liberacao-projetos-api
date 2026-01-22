package com.metrica.liberacao.dto;

import java.math.BigDecimal;

public class ProjetoResponse {
    private Long id;
    private String nomeCliente;
    private BigDecimal precoExecutivo;
    private BigDecimal precoAnteprojeto;

    public ProjetoResponse() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getNomeCliente() {return nomeCliente;}
    public void setNomeCliente(String nomeCliente) {this.nomeCliente = nomeCliente;}

    public BigDecimal getPrecoExecutivo() {return precoExecutivo;}
    public void setPrecoExecutivo(BigDecimal precoExecutivo) {this.precoExecutivo = precoExecutivo;}

    public BigDecimal getPrecoAnteprojeto() {return precoAnteprojeto;}
    public void setPrecoAnteprojeto(BigDecimal precoAnteprojeto) {this.precoAnteprojeto = precoAnteprojeto;}

}