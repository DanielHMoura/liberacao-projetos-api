package com.metrica.liberacao.controller;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.dto.CriarProjetoRequest;
import com.metrica.liberacao.dto.LiberacaoResponse;
import com.metrica.liberacao.dto.StatusProjetoResponse;
import com.metrica.liberacao.dto.ValidarAcessoRequest;
import com.metrica.liberacao.service.ProjetoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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


    @PatchMapping("/{id}/admin/status-pagamento/anteprojeto")
    public ResponseEntity<Map<String, String>> adminAtualizarStatusPagamentoAnteprojeto(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Projeto projeto = projetoService.buscarProjetoOuFalhar(id);
        StatusAnteprojeto novoStatus = StatusAnteprojeto.valueOf(request.get("status"));
        projeto.setStatusAnteprojeto(novoStatus);

        // Se marcar como pago, salva a data
        if (novoStatus == StatusAnteprojeto.PAGO) {
            projeto.setDataPagamentoAnteprojeto(LocalDateTime.now());
        }

        projetoService.salvarProjeto(projeto);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Status do anteprojeto atualizado",
                "novoStatus", novoStatus.toString()
        ));
    }

    @PatchMapping("/{id}/admin/status-pagamento/executivo")
    public ResponseEntity<Map<String, String>> adminAtualizarStatusPagamentoExecutivo(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Projeto projeto = projetoService.buscarProjetoOuFalhar(id);
        StatusExecutivo novoStatus = StatusExecutivo.valueOf(request.get("status"));
        projeto.setStatusExecutivo(novoStatus);

        // Se marcar como pago, salva a data
        if (novoStatus == StatusExecutivo.PAGO) {
            projeto.setDataPagamentoExecutivo(LocalDateTime.now());
        }

        projetoService.salvarProjeto(projeto);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Status do executivo atualizado",
                "novoStatus", novoStatus.toString()
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

    /**
     * Cliente baixa PDF Anteprojeto
     * GET /projetos/download/anteprojeto?codigoAcesso=ABC123&pinAcesso=1234
     */
    @GetMapping("/download/anteprojeto")
    public ResponseEntity<byte[]> downloadAnteprojeto(
            @RequestParam String codigoAcesso,
            @RequestParam String pinAcesso) {

        byte[] conteudo = projetoService.baixarPdfAnteprojeto(codigoAcesso, pinAcesso);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"anteprojeto.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(conteudo);
    }

    /**
     * Cliente baixa PDF Executivo
     * GET /projetos/download/executivo?codigoAcesso=ABC123&pinAcesso=1234
     */
    @GetMapping("/download/executivo")
    public ResponseEntity<byte[]> downloadExecutivo(
            @RequestParam String codigoAcesso,
            @RequestParam String pinAcesso) {

        byte[] conteudo = projetoService.baixarPdfExecutivo(codigoAcesso, pinAcesso);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"executivo.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(conteudo);
    }

    @GetMapping("/status")
    public ResponseEntity<StatusProjetoResponse> obterStatus(
            @RequestParam String codigoAcesso,
            @RequestParam String pinAcesso) {

        StatusProjetoResponse status = projetoService.obterStatusProjeto(codigoAcesso, pinAcesso);
        return ResponseEntity.ok(status);
    }





}
