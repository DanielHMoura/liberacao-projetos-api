package com.metrica.liberacao.exception;

public class ArquivoInvalidoException extends RuntimeException {
    public ArquivoInvalidoException(String mensagem) {
        super(mensagem);
    }

    public ArquivoInvalidoException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
