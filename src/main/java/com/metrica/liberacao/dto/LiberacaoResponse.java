package com.metrica.liberacao.dto;

public class LiberacaoResponse {

    private boolean acessoValido;
    private boolean anteprojetoLiberado;
    private boolean executivoLiberado;

    private Long projetoId;  // ← ADICIONAR
    private String nomeCliente;

    private String mensagem;
    private String qrCode;

    public boolean isAcessoValido() {return acessoValido;}
    public void setAcessoValido(boolean acessoValido) {this.acessoValido = acessoValido;}

    public boolean isAnteprojetoLiberado() {return anteprojetoLiberado;}
    public void setAnteprojetoLiberado(boolean anteprojetoLiberado) {this.anteprojetoLiberado = anteprojetoLiberado;}

    public boolean isExecutivoLiberado() {
        return executivoLiberado;
    }
    public void setExecutivoLiberado(boolean executivoLiberado) {this.executivoLiberado = executivoLiberado;}

    public String getMensagem() {return mensagem;}
    public void setMensagem(String mensagem) {this.mensagem = mensagem;}

    public String getQrCode() {return qrCode;}
    public void setQrCode(String qrCode) {this.qrCode = qrCode;}

    public Long getProjetoId() {return projetoId;}
    public void setProjetoId(Long projetoId) {this.projetoId = projetoId;}

    public String getNomeCliente() {return nomeCliente;}
    public void setNomeCliente(String nomeCliente) {this.nomeCliente = nomeCliente;}
}
