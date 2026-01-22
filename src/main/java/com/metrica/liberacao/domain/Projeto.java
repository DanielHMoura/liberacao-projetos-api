package com.metrica.liberacao.domain;

import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "projetos")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoAcesso;

    @Column(nullable = false)
    private String pinAcesso;

    @Column(nullable = false)
    private String nomeCliente;

    @Column(name = "valor_anteprojeto", nullable = true)
    private BigDecimal valorAnteprojeto;

    @Column(name = "valor_executivo", nullable = true)
    private BigDecimal valorExecutivo;

    @Enumerated(EnumType.STRING)
    private StatusAnteprojeto statusAnteprojeto;

    @Enumerated(EnumType.STRING)
    private StatusExecutivo statusExecutivo;

    @Column(name = "pdf_anteprojeto_conteudo")
    private String pdfAnteprojeto;

    @Column(name = "pdf_executivo_conteudo")
    private String pdfExecutivo;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getCodigoAcesso() {return codigoAcesso;}
    public void setCodigoAcesso(String codigoAcesso) {this.codigoAcesso = codigoAcesso;}

    public String getPinAcesso() {return pinAcesso;}
    public void setPinAcesso(String pinAcesso) {this.pinAcesso = pinAcesso;}

    public String getNomeCliente() {return nomeCliente;}
    public void setNomeCliente(String nomeCliente) {this.nomeCliente = nomeCliente;}

    public StatusAnteprojeto getStatusAnteprojeto() {return statusAnteprojeto;}
    public void setStatusAnteprojeto(StatusAnteprojeto statusAnteprojeto) {this.statusAnteprojeto = statusAnteprojeto;}

    public StatusExecutivo getStatusExecutivo() {return statusExecutivo;}
    public void setStatusExecutivo(StatusExecutivo statusExecutivo) {this.statusExecutivo = statusExecutivo;}

    public String getPdfAnteprojeto() {return pdfAnteprojeto;}
    public void setPdfAnteprojeto(String pdfAnteprojeto) {this.pdfAnteprojeto = pdfAnteprojeto;}

    public String getPdfExecutivo() {return pdfExecutivo;}
    public void setPdfExecutivo(String pdfExecutivo) {this.pdfExecutivo = pdfExecutivo;}

    public BigDecimal getValorAnteprojeto() {return valorAnteprojeto;}
    public void setValorAnteprojeto(BigDecimal valorAnteprojeto) {this.valorAnteprojeto = valorAnteprojeto;}

    public BigDecimal getValorExecutivo() {return valorExecutivo;}
    public void setValorExecutivo(BigDecimal valorExecutivo) {this.valorExecutivo = valorExecutivo;}


}
