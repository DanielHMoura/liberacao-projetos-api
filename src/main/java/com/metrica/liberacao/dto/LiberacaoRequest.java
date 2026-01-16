package com.metrica.liberacao.dto;

public class LiberacaoRequest {

    private Long projetoId;
    private String pinAcesso;
    private String tipoProjeto;

    public Long getProjetoId() {return projetoId;}
    public void setProjetoId(Long projetoId) {this.projetoId = projetoId;}

    public String getPinAcesso() {return pinAcesso;}
    public void setPinAcesso(String pinAcesso) {this.pinAcesso = pinAcesso;}

    public String getTipoProjeto() {return tipoProjeto;}
    public void setTipoProjeto(String tipoProjeto) {this.tipoProjeto = tipoProjeto;}

}
