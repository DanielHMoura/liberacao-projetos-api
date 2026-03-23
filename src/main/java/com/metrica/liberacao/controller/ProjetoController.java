package com.metrica.liberacao.controller;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.dto.CriarProjetoRequest;
import com.metrica.liberacao.dto.LiberacaoResponse;
import com.metrica.liberacao.dto.ValidarAcessoRequest;
import com.metrica.liberacao.service.ProjetoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    // ========== ROTAS PARA ADMIN (usa ID interno) ==========

    /**
     * Admin cria novo projeto
     * POST /projetos
     */
    @PostMapping
    public ResponseEntity<Projeto> criar(@RequestBody CriarProjetoRequest request) {
        Projeto projeto = projetoService.criarProjeto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(projeto);
    }

    /**
     * Admin busca projeto por ID interno
     * GET /projetos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarProjetoPorId(@PathVariable Long id) {
        Projeto projeto = projetoService.buscarProjetoOuFalhar(id);
        return ResponseEntity.ok(projeto);
    }

    /**
     * Admin faz upload do PDF Anteprojeto
     * POST /projetos/{id}/upload/anteprojeto
     */
    @PostMapping(value = "/{id}/upload/anteprojeto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAnteprojeto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Arquivo vazio"
            ));
        }

        projetoService.salvarPdfAnteprojeto(id, file);

        return ResponseEntity.ok(Map.of(
                "mensagem", "PDF do anteprojeto enviado com sucesso",
                "projetoId", id.toString()
        ));
    }


    /**
     * Admin faz upload do PDF Executivo
     * POST /projetos/{id}/upload/executivo
     */
    @PostMapping(value = "/{id}/upload/executivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadExecutivo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Arquivo vazio"
            ));
        }

        projetoService.salvarPdfExecutivo(id, file);

        return ResponseEntity.ok(Map.of(
                "mensagem", "PDF do executivo enviado com sucesso",
                "projetoId", id.toString()
        ));
    }

    // ========== ROTAS PARA CLIENTE (usa codigoAcesso + pinAcesso) ==========

    /**
     * Cliente valida acesso e verifica liberação
     * POST /projetos/liberacao
     */
    @PostMapping("/liberacao")
    public ResponseEntity<LiberacaoResponse> verificarLiberacao(@RequestBody ValidarAcessoRequest request) {
        LiberacaoResponse response = projetoService.verificarLiberacao(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Cliente busca projeto por código de acesso
     * GET /projetos/buscar?codigoAcesso=ABC123&pinAcesso=1234
     */
    @GetMapping("/buscar")
    public ResponseEntity<Projeto> buscarProjetoPorCodigo(
            @RequestParam String codigoAcesso,
            @RequestParam String pinAcesso) {

        Projeto projeto = projetoService.buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);
        return ResponseEntity.ok(projeto);
    }

    @GetMapping("/download/anteprojeto")
    public ResponseEntity<StreamingResponseBody> downloadAnteprojeto(
            @RequestParam String codigoAcesso,
            @RequestParam String pinAcesso) {

        InputStream conteudo = projetoService.baixarPdfAnteprojeto(codigoAcesso, pinAcesso);

        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = conteudo) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"anteprojeto.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }

    /**
     * Cliente baixa PDF Executivo
     * GET /projetos/download/executivo?codigoAcesso=ABC123&pinAcesso=1234
     */
    @GetMapping("/download/executivo")
    public ResponseEntity<StreamingResponseBody> downloadExecutivo(
            @RequestParam String codigoAcesso,
            @RequestParam String pinAcesso) {

        InputStream conteudo = projetoService.baixarPdfExecutivo(codigoAcesso, pinAcesso);

        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = conteudo) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"executivo.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }





}
