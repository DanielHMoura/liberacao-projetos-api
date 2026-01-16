package com.metrica.liberacao.controller;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.dto.CriarProjetoRequest;
import com.metrica.liberacao.dto.LiberacaoResponse;
import com.metrica.liberacao.dto.ValidarAcessoRequest;
import com.metrica.liberacao.service.ProjetoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @PostMapping
    public ResponseEntity<Projeto> criar(@RequestBody CriarProjetoRequest request) {
        Projeto projeto = projetoService.criarProjeto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(projeto);
    }

    @GetMapping("/{id}")
    public Projeto buscarProjetoPorId(@PathVariable Long id) {
        return projetoService.buscarProjetoOuFalhar(id);
    }

    // ✅ NOVO: Retorna lista de projetos com QR Code se estiver PENDENTE/PRONTO
    @PostMapping("/liberacao")
    public ResponseEntity<LiberacaoResponse> verificarLiberacao(@RequestBody ValidarAcessoRequest request) {
        LiberacaoResponse response = projetoService.verificarLiberacao(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/download/anteprojeto")
    public ResponseEntity<byte[]> downloadAnteprojeto(@PathVariable Long id, @RequestParam String pinAcesso) {
        byte[] conteudo = projetoService.baixarPdfAnteprojeto(id, pinAcesso);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"anteprojeto.pdf\"")
                .header("Content-Type", "application/pdf")
                .body(conteudo);
    }

    @GetMapping("/{id}/download/executivo")
    public ResponseEntity<byte[]> downloadExecutivo(@PathVariable Long id, @RequestParam String pinAcesso) {
        byte[] conteudo = projetoService.baixarPdfExecutivo(id, pinAcesso);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"executivo.pdf\"")
                .header("Content-Type", "application/pdf")
                .body(conteudo);
    }

    @PostMapping("/{id}/upload/anteprojeto")
    public String uploadAnteprojeto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        projetoService.salvarPdfAnteprojeto(id, file);
        return "PDF do anteprojeto enviado para o projeto com id: " + id;
    }

    @PostMapping("/{id}/upload/executivo")
    public String uploadExecutivo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        projetoService.salvarPdfExecutivo(id, file);
        return "PDF do executivo enviado para o projeto com id: " + id;
    }

    @PatchMapping("/{id}/admin/status-pagamento/anteprojeto")
    public ResponseEntity<?> adminAtualizarStatusPagamentoAnteprojeto(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Projeto projeto = projetoService.buscarProjetoOuFalhar(id);
        StatusAnteprojeto novoStatus = StatusAnteprojeto.valueOf(request.get("status"));
        projeto.setStatusAnteprojeto(novoStatus);
        projetoService.salvarProjeto(projeto);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Status do anteprojeto atualizado",
                "novoStatus", novoStatus.toString()
        ));
    }

    @PatchMapping("/{id}/admin/status-pagamento/executivo")
    public ResponseEntity<?> adminAtualizarStatusPagamentoExecutivo(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Projeto projeto = projetoService.buscarProjetoOuFalhar(id);
        StatusExecutivo novoStatus = StatusExecutivo.valueOf(request.get("status"));
        projeto.setStatusExecutivo(novoStatus);
        projetoService.salvarProjeto(projeto);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Status do executivo atualizado",
                "novoStatus", novoStatus.toString()
        ));
    }


}
