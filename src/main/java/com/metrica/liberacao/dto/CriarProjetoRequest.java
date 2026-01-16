package com.metrica.liberacao.dto;

public class CriarProjetoRequest {

    private String codigoAcesso;
    private String pinAcesso;
    private String nomeCliente;

    public String getCodigoAcesso() {return codigoAcesso;}
    public void setCodigoAcesso(String codigoAcesso) {this.codigoAcesso = codigoAcesso;}

    public String getPinAcesso() {return pinAcesso;}
    public void setPinAcesso(String pinAcesso) {this.pinAcesso = pinAcesso;}

    public String getNomeCliente() {return nomeCliente;}
    public void setNomeCliente(String nomeCliente) {this.nomeCliente = nomeCliente;}
}

