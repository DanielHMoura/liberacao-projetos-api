package com.metrica.liberacao.controller;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelas operações administrativas de projetos.
 *
 * @author DanielHMoura
 * @version 1.0
 * @since 2026-01-15
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProjetoRepository projetoRepository;

    /**
     * Libera um projeto (anteprojeto ou executivo) para download.
     *
     * @param id o ID do projeto
     * @param tipo o tipo de projeto: "anteprojeto" ou "executivo"
     * @return mensagem de sucesso ou erro
     */
    @PostMapping("/liberar-projeto/{id}")
    public ResponseEntity<?> liberarProjeto(
            @PathVariable Long id,
            @RequestParam String tipo) {

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if ("anteprojeto".equals(tipo)) {
            projeto.setStatusAnteprojeto(StatusAnteprojeto.PAGO);
        } else if ("executivo".equals(tipo)) {
            projeto.setStatusExecutivo(StatusExecutivo.PAGO);
        } else {
            return ResponseEntity.badRequest().body("Tipo inválido. Use 'anteprojeto' ou 'executivo'");
        }

        projetoRepository.save(projeto);
        return ResponseEntity.ok("✅ Projeto liberado com sucesso!");
    }
}
