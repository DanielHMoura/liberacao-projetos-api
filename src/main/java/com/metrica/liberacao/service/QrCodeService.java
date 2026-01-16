package com.metrica.liberacao.service;

import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Locale;

@Service
public class QrCodeService {

    public String gerarQrCodeBase64(
            String chavePix,
            String nomeBeneficiario,
            String cidade,
            Double valor
    ) {
        try {
            String payloadPix = gerarPayloadPix(chavePix, nomeBeneficiario, cidade, valor);

            String urlQrCode =
                    "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data="
                            + java.net.URLEncoder.encode(payloadPix, "UTF-8");

            URL url = new URL(urlQrCode);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            byte[] qrBytes = conn.getInputStream().readAllBytes();

            return Base64.getEncoder().encodeToString(qrBytes);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code PIX", e);
        }
    }

    // ================== PAYLOAD PIX ==================

    private String gerarPayloadPix(
            String chavePix,
            String nome,
            String cidade,
            Double valor
    ) {
        StringBuilder sb = new StringBuilder();

        // 00 - Payload Format Indicator
        sb.append("000201");

        // 26 - Merchant Account Information (PIX)
        String gui = "br.gov.bcb.pix";
        String campoGui = "00" + format(gui.length()) + gui;
        String campoChave = "01" + format(chavePix.length()) + chavePix;
        String campo26 = campoGui + campoChave;

        sb.append("26").append(format(campo26.length())).append(campo26);

        // 52 - Merchant Category Code
        sb.append("52040000");

        // 53 - Currency (BRL)
        sb.append("5303986");

        // 54 - Transaction Amount (opcional)
        if (valor != null && valor > 0) {
            String valorStr = String.format(Locale.US, "%.2f", valor);
            sb.append("54").append(format(valorStr.length())).append(valorStr);
        }

        // 58 - Country Code
        sb.append("5802BR");

        // 59 - Merchant Name (máx 25)
        String nomeLimpo = normalizar(nome, 25);
        sb.append("59").append(format(nomeLimpo.length())).append(nomeLimpo);

        // 60 - Merchant City (máx 15)
        String cidadeLimpa = normalizar(cidade, 15);
        sb.append("60").append(format(cidadeLimpa.length())).append(cidadeLimpa);

        // 62 - Additional Data (TXID obrigatório)
        sb.append("62070503***");

        // 63 - CRC16
        sb.append("6304");
        sb.append(calcularCRC16(sb.toString()));

        return sb.toString();
    }

    // ================== UTIL ==================

    private String format(int tamanho) {
        return String.format("%02d", tamanho);
    }

    private String normalizar(String texto, int max) {
        if (texto == null) return "";

        String n = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9 \\-\\.,'&]", "")
                .toUpperCase()
                .trim();

        return n.length() > max ? n.substring(0, max) : n;
    }

    // CRC16-CCITT (ASCII obrigatório)
    private String calcularCRC16(String payload) {
        int crc = 0xFFFF;
        byte[] bytes = payload.getBytes(StandardCharsets.US_ASCII);

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
