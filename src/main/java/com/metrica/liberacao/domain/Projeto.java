package com.metrica.liberacao.domain;

import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import jakarta.persistence.*;

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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    private StatusAnteprojeto statusAnteprojeto;

    @Enumerated(EnumType.STRING)
    private StatusExecutivo statusExecutivo;

    @Lob
    @Column(name = "pdf_anteprojeto_conteudo")
    private byte[] pdfAnteprojeto;

    @Lob
    @Column(name = "pdf_executivo_conteudo")
    private byte[] pdfExecutivo;


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
    public byte[] getPdfAnteprojeto() {return pdfAnteprojeto;}

    public void setPdfAnteprojeto(byte[] pdfAnteprojeto) {this.pdfAnteprojeto = pdfAnteprojeto;}

    public byte[] getPdfExecutivo() {return pdfExecutivo;}
    public void setPdfExecutivo(byte[] pdfExecutivo) {this.pdfExecutivo = pdfExecutivo;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
}
