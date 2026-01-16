package com.metrica.liberacao.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handler para NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointer(NullPointerException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("tipo", "NullPointerException");
        erro.put("mensagem", "Valor nulo detectado: " + ex.getMessage());

        System.err.println("=== [NULL POINTER] " + ex.getMessage() + " ===");
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    // Handler genérico para qualquer outra exceção
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("tipo", ex.getClass().getSimpleName());
        erro.put("mensagem", ex.getMessage());
        erro.put("causa", ex.getCause() != null ? ex.getCause().toString() : "N/A");

        System.err.println("=== [ERRO GLOBAL] " + ex.getClass().getName() + " ===");
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
