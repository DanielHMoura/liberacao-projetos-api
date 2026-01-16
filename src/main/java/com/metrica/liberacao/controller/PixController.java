package com.metrica.liberacao.controller;

import com.metrica.liberacao.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pix")
public class PixController {

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/gerar-qrcode")
    public ResponseEntity<String> gerarQrCode(
            @RequestParam String chavePix,
            @RequestParam String nomeBeneficiario,
            @RequestParam String cidade,
            @RequestParam(required = false) Double valor) {

        String qrCode = qrCodeService.gerarQrCodeBase64(chavePix, nomeBeneficiario, cidade, valor);
        return ResponseEntity.ok(qrCode);
    }
}
