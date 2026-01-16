package com.metrica.liberacao.service;

import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.nio.charset.StandardCharsets;

@Service
public class QrCodeService {

    public String gerarQrCodeBase64(String chavePix, String nomeBeneficiario, String cidade, Double valor) {
        try {
            String payloadPix = construirPayloadSimples(chavePix, nomeBeneficiario, cidade, valor);

            // Usa API externa - muito mais simples!
            String urlQrCode = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" +
                    java.net.URLEncoder.encode(payloadPix, "UTF-8");

            java.net.URL url = new java.net.URL(urlQrCode);
            java.io.InputStream input = url.openStream();
            byte[] qrCodeBytes = input.readAllBytes();
            input.close();

            return java.util.Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code", e);
        }
    }

    private String construirPayloadSimples(String chavePix, String nomeBeneficiario, String cidade, Double valor) {
        StringBuilder sb = new StringBuilder();

        // 00: Payload Format Indicator
        sb.append("000201");

        // 26: Merchant Account Information (PIX)
        // Chaves aleatórias devem conter os hífens.
        String gui = "0014br.gov.bcb.pix";
        String chaveFormatada = "01" + String.format("%02d", chavePix.length()) + chavePix;
        sb.append("26").append(String.format("%02d", gui.length() + chaveFormatada.length()))
                .append(gui).append(chaveFormatada);

        sb.append("52040000"); // Merchant Category Code
        sb.append("5303986"); // Moeda: Real

        // 54: Valor (opcional)
        if (valor != null && valor > 0) {
            String valorStr = String.format("%.2f", valor).replace(",", ".");
            sb.append("54").append(String.format("%02d", valorStr.length())).append(valorStr);
        }

        sb.append("5802BR"); // País

        // 59: Nome (Normalizado e limitado a 25 caracteres para evitar erro de leitura)
        String nomeLimpo = normalizar(nomeBeneficiario);
        if (nomeLimpo.length() > 25) nomeLimpo = nomeLimpo.substring(0, 25);
        sb.append("59").append(String.format("%02d", nomeLimpo.length())).append(nomeLimpo);

        // 60: Cidade (Máx 15 caracteres)
        String cidadeLimpa = normalizar(cidade);
        if (cidadeLimpa.length() > 15) cidadeLimpa = cidadeLimpa.substring(0, 15);
        sb.append("60").append(String.format("%02d", cidadeLimpa.length())).append(cidadeLimpa);

        // 62: Campo adicional (TXID) - Obrigatório ter ao menos o ID 05 (*** para estático)
        sb.append("62070503***");

        // 63: CRC16 (Finalizador do PIX)
        sb.append("6304");
        return sb.toString() + calcularCRC16(sb.toString());
    }

    private String normalizar(String input) {
        if (input == null) return "";
        String n = Normalizer.normalize(input, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}", ""); // remove acentos
        n = n.replaceAll("[^A-Za-z0-9 \\-\\.,'&]", ""); // permite alguns sinais e espaços
        n = n.toUpperCase().trim();
        return n;
    }

    private String calcularCRC16(String input) {
        int crc = 0xFFFF;
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }
}
