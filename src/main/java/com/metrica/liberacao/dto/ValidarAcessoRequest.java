package com.metrica.liberacao.dto;

public class ValidarAcessoRequest {

    private Long projetoId;
    private String pinAcesso;

    public Long getProjetoId() {return projetoId;}
    public void setProjetoId(Long projetoId) {this.projetoId = projetoId;}

    public String getPinAcesso() {return pinAcesso;}
    public void setPinAcesso(String pinAcesso) {this.pinAcesso = pinAcesso;}


}
