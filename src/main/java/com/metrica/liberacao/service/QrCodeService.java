package com.metrica.liberacao.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.Normalizer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

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
        StringBuilder payload = new StringBuilder();
        payload.append("00020126360014br.gov.bcb.pix");
        payload.append("0136").append(chavePix);
        payload.append("5204000053039865402");
        if (valor != null) payload.append(String.format("%.2f", valor));
        payload.append("5802BR5913").append(nomeBeneficiario.substring(0, Math.min(13, nomeBeneficiario.length())));
        payload.append("6009").append(cidade.substring(0, Math.min(9, cidade.length())));
        payload.append("63041D3D");
        return payload.toString();
    }

}
