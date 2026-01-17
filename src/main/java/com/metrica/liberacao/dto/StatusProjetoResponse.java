package com.metrica.liberacao.dto;


public class StatusProjetoResponse {

    private String statusAnteprojeto;
    private String valorAnteprojeto;

    private String statusExecutivo;
    private String valorExecutivo;

    private String mensagem;

    public StatusProjetoResponse(String statusAnteprojeto, String valorAnteprojeto,String statusExecutivo, String valorExecutivo,String mensagem) {
        this.statusAnteprojeto = statusAnteprojeto;
        this.valorAnteprojeto = valorAnteprojeto;
        this.statusExecutivo = statusExecutivo;
        this.valorExecutivo = valorExecutivo;
        this.mensagem = mensagem;
    }


    public String getStatusAnteprojeto() {return statusAnteprojeto;}
    public void setStatusAnteprojeto(String statusAnteprojeto) {this.statusAnteprojeto = statusAnteprojeto;}

    public String getValorAnteprojeto() {return valorAnteprojeto;}
    public void setValorAnteprojeto(String valorAnteprojeto) {this.valorAnteprojeto = valorAnteprojeto;}

    public String getStatusExecutivo() {return statusExecutivo;}
    public void setStatusExecutivo(String statusExecutivo) {this.statusExecutivo = statusExecutivo;}

    public String getValorExecutivo() {return valorExecutivo;}
    public void setValorExecutivo(String valorExecutivo) {this.valorExecutivo = valorExecutivo;}

    public String getMensagem() {return mensagem;}
    public void setMensagem(String mensagem) {this.mensagem = mensagem;}
}
