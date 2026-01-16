package com.metrica.liberacao.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusProjetoResponse {

    private String statusAnteprojeto;
    private String valorAnteprojeto;
    private String qrcodeAnteprojeto;
    private String pixPayloadAnteprojeto;
    private Boolean anteprojetoDisponivelDownload;
    private Integer diasRestantesAnteprojeto;

    private String statusExecutivo;
    private String valorExecutivo;
    private String qrcodeExecutivo;
    private String pixPayloadExecutivo;
    private Boolean executivoDisponivelDownload;
    private Integer diasRestantesExecutivo;

    private String mensagem;

    public StatusProjetoResponse() {}

    public StatusProjetoResponse(String statusAnteprojeto, String valorAnteprojeto, String qrcodeAnteprojeto,
                                 String pixPayloadAnteprojeto, Boolean anteprojetoDisponivelDownload,
                                 Integer diasRestantesAnteprojeto, String statusExecutivo, String valorExecutivo,
                                 String qrcodeExecutivo, String pixPayloadExecutivo, Boolean executivoDisponivelDownload,
                                 Integer diasRestantesExecutivo, String mensagem) {
        this.statusAnteprojeto = statusAnteprojeto;
        this.valorAnteprojeto = valorAnteprojeto;
        this.qrcodeAnteprojeto = qrcodeAnteprojeto;
        this.pixPayloadAnteprojeto = pixPayloadAnteprojeto;
        this.anteprojetoDisponivelDownload = anteprojetoDisponivelDownload;
        this.diasRestantesAnteprojeto = diasRestantesAnteprojeto;
        this.statusExecutivo = statusExecutivo;
        this.valorExecutivo = valorExecutivo;
        this.qrcodeExecutivo = qrcodeExecutivo;
        this.pixPayloadExecutivo = pixPayloadExecutivo;
        this.executivoDisponivelDownload = executivoDisponivelDownload;
        this.diasRestantesExecutivo = diasRestantesExecutivo;
        this.mensagem = mensagem;
    }

    public String getStatusAnteprojeto() {return statusAnteprojeto;}

    public void setStatusAnteprojeto(String statusAnteprojeto) {this.statusAnteprojeto = statusAnteprojeto;}
    public String getValorAnteprojeto() {return valorAnteprojeto;}

    public void setValorAnteprojeto(String valorAnteprojeto) {this.valorAnteprojeto = valorAnteprojeto;}
    public String getQrcodeAnteprojeto() {return qrcodeAnteprojeto;}

    public void setQrcodeAnteprojeto(String qrcodeAnteprojeto) {this.qrcodeAnteprojeto = qrcodeAnteprojeto;}
    public String getPixPayloadAnteprojeto() {return pixPayloadAnteprojeto;}

    public void setPixPayloadAnteprojeto(String pixPayloadAnteprojeto) {this.pixPayloadAnteprojeto = pixPayloadAnteprojeto;}

    public Boolean getAnteprojetoDisponivelDownload() {return anteprojetoDisponivelDownload;}
    public void setAnteprojetoDisponivelDownload(Boolean anteprojetoDisponivelDownload) {this.anteprojetoDisponivelDownload = anteprojetoDisponivelDownload;}

    public Integer getDiasRestantesAnteprojeto() {return diasRestantesAnteprojeto;}
    public void setDiasRestantesAnteprojeto(Integer diasRestantesAnteprojeto) {this.diasRestantesAnteprojeto = diasRestantesAnteprojeto;}

    public String getStatusExecutivo() {return statusExecutivo;}
    public void setStatusExecutivo(String statusExecutivo) {this.statusExecutivo = statusExecutivo;}

    public String getValorExecutivo() {return valorExecutivo;}
    public void setValorExecutivo(String valorExecutivo) {this.valorExecutivo = valorExecutivo;}

    public String getQrcodeExecutivo() {return qrcodeExecutivo;}
    public void setQrcodeExecutivo(String qrcodeExecutivo) {this.qrcodeExecutivo = qrcodeExecutivo;}

    public String getPixPayloadExecutivo() {return pixPayloadExecutivo;}
    public void setPixPayloadExecutivo(String pixPayloadExecutivo) {this.pixPayloadExecutivo = pixPayloadExecutivo;}

    public Boolean getExecutivoDisponivelDownload() {return executivoDisponivelDownload;}
    public void setExecutivoDisponivelDownload(Boolean executivoDisponivelDownload) {this.executivoDisponivelDownload = executivoDisponivelDownload;}

    public Integer getDiasRestantesExecutivo() {return diasRestantesExecutivo;}
    public void setDiasRestantesExecutivo(Integer diasRestantesExecutivo) {this.diasRestantesExecutivo = diasRestantesExecutivo;}

    public String getMensagem() {return mensagem;}
    public void setMensagem(String mensagem) {this.mensagem = mensagem;}
}
