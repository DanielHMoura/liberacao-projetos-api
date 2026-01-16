package com.metrica.liberacao.exception;

public class AcessoInvalidoException extends RuntimeException {
    public AcessoInvalidoException(String pinAcesso) {
        super("Acesso inválido com pin: " + pinAcesso);
    }
}
