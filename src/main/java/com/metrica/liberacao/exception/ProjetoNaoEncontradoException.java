package com.metrica.liberacao.exception;

public class ProjetoNaoEncontradoException extends RuntimeException {
    public ProjetoNaoEncontradoException(Long id) {
        super("Projeto não encontrado com id: " + id);
    }

}
