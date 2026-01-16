package com.metrica.liberacao.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeService {

    public String gerarQrCodeBase64(String dados) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(dados, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            byte[] qrCodeBytes = pngOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code", e);
        }
    }
}