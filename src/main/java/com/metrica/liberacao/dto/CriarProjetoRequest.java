package com.metrica.liberacao.dto;

import java.math.BigDecimal;

public class CriarProjetoRequest {

    private String codigoAcesso;
    private String pinAcesso;
    private String nomeCliente;
    private BigDecimal precoAnteprojeto;
    private BigDecimal precoExecutivo;

    public BigDecimal getPrecoExecutivo() {return precoExecutivo;}
    public void setPrecoExecutivo(BigDecimal precoExecutivo) {this.precoExecutivo = precoExecutivo;}

    public BigDecimal getPrecoAnteprojeto() {return precoAnteprojeto;}
    public void setPrecoAnteprojeto(BigDecimal precoAnteprojeto) {this.precoAnteprojeto = precoAnteprojeto;}

    public String getCodigoAcesso() {return codigoAcesso;}
    public void setCodigoAcesso(String codigoAcesso) {this.codigoAcesso = codigoAcesso;}

    public String getPinAcesso() {return pinAcesso;}
    public void setPinAcesso(String pinAcesso) {this.pinAcesso = pinAcesso;}

    public String getNomeCliente() {return nomeCliente;}
    public void setNomeCliente(String nomeCliente) {this.nomeCliente = nomeCliente;}
}

