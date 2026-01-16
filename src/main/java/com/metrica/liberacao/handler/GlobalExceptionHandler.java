package com.metrica.liberacao.handler;

import com.metrica.liberacao.exception.AcessoInvalidoException;
import com.metrica.liberacao.exception.ArquivoInvalidoException;
import com.metrica.liberacao.exception.ProjetoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjetoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleProjetoNaoEncontrado(ProjetoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 404,
                        "erro", "Projeto não encontrado",
                        "mensagem", ex.getMessage()
                ));
    }

    @ExceptionHandler(AcessoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleAcessoInvalido(AcessoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 401,
                        "erro", "Acesso inválido",
                        "mensagem", ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "erro", "Requisição inválida",
                        "mensagem", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 500,
                        "erro", "Erro interno do servidor",
                        "mensagem", "Ocorreu um erro inesperado. Tente novamente mais tarde."
                ));
    }

    @ExceptionHandler(ArquivoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleArquivoInvalido(ArquivoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "erro", "Arquivo inválido",
                        "mensagem", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("tipo", ex.getClass().getSimpleName());
        erro.put("mensagem", ex.getMessage());
        erro.put("causa", ex.getCause() != null ? ex.getCause().toString() : "N/A");

        System.err.println("=== [ERRO GLOBAL] ===");
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

}
