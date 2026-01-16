package com.metrica.liberacao.service;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.dto.*;
import com.metrica.liberacao.exception.AcessoInvalidoException;
import com.metrica.liberacao.exception.ResourceNotFoundException;
import com.metrica.liberacao.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;

    public ProjetoService(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
    }

    public Projeto criarProjeto(CriarProjetoRequest request) {
        if (projetoRepository.findByCodigoAcesso(request.getCodigoAcesso()).isPresent()) {
            throw new IllegalArgumentException("Código de acesso já existe");
        }

        Projeto projeto = new Projeto();
        projeto.setCodigoAcesso(request.getCodigoAcesso());
        projeto.setPinAcesso(request.getPinAcesso());
        projeto.setNomeCliente(request.getNomeCliente());
        projeto.setValorAnteprojeto(request.getPrecoAnteprojeto());
        projeto.setValorExecutivo(request.getPrecoExecutivo());
        projeto.setStatusAnteprojeto(StatusAnteprojeto.PENDENTE);
        projeto.setStatusExecutivo(StatusExecutivo.PENDENTE);

        return projetoRepository.save(projeto);
    }

    public Projeto buscarProjetoOuFalhar(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + id));
    }

    public Projeto buscarProjetoPorCodigoEPin(String codigoAcesso, String pinAcesso) {
        return projetoRepository.findByCodigoAcessoAndPinAcesso(codigoAcesso, pinAcesso)
                .orElseThrow(() -> new AcessoInvalidoException("Código ou PIN inválidos"));
    }

    public Projeto salvarProjeto(Projeto projeto) {
        return projetoRepository.save(projeto);
    }


    public LiberacaoResponse verificarLiberacao(ValidarAcessoRequest request) {
        Projeto projeto = buscarProjetoPorCodigoEPin(request.getCodigoAcesso(), request.getPinAcesso());

        LiberacaoResponse response = new LiberacaoResponse();
        response.setAcessoValido(true);
        response.setProjetoId(projeto.getId());
        response.setNomeCliente(projeto.getNomeCliente());
        response.setPrecoAnteprojeto(projeto.getValorAnteprojeto());
        response.setPrecoExecutivo(projeto.getValorExecutivo());

        boolean anteprojetoPago = projeto.getStatusAnteprojeto() == StatusAnteprojeto.PAGO;
        boolean executivoPago = projeto.getStatusExecutivo() == StatusExecutivo.PAGO;

        response.setAnteprojetoLiberado(anteprojetoPago);
        response.setExecutivoLiberado(executivoPago);

        if (!anteprojetoPago || !executivoPago) {
            String txId = !anteprojetoPago ? "ANTE" + projeto.getId() : "EXEC" + projeto.getId();
            BigDecimal valor = !anteprojetoPago ? projeto.getValorAnteprojeto() : projeto.getValorExecutivo();
            String payload = gerarPayloadPix("13246316600", "JAQUELINE", "BETIM",
                    valor.setScale(2, RoundingMode.HALF_UP).toString(), txId);
            response.setQrCode(payload);
        }

        if (anteprojetoPago && executivoPago) {
            response.setMensagem("Todos os projetos liberados para download.");
        } else if (anteprojetoPago) {
            response.setMensagem("Apenas o anteprojeto está liberado para download. Escaneie o QR Code para pagar o executivo.");
        } else if (executivoPago) {
            response.setMensagem("Apenas o projeto executivo está liberado para download. Escaneie o QR Code para pagar o anteprojeto.");
        } else {
            response.setMensagem("Nenhum projeto está liberado para download. Escaneie o QR Code para efetuar o pagamento.");
        }

        return response;
    }

    public void salvarPdfAnteprojeto(Long id, MultipartFile file) {
        validarArquivoPdf(file);
        Projeto projeto = buscarProjetoOuFalhar(id);
        try {
            projeto.setPdfAnteprojeto(file.getBytes());
            projetoRepository.save(projeto);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar PDF do anteprojeto", e);
        }
    }

    public void salvarPdfExecutivo(Long id, MultipartFile file) {
        validarArquivoPdf(file);
        Projeto projeto = buscarProjetoOuFalhar(id);
        try {
            projeto.setPdfExecutivo(file.getBytes());
            projetoRepository.save(projeto);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar PDF do executivo", e);
        }
    }

    private void validarArquivoPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }

        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Apenas arquivos PDF são permitidos");
        }

        long tamanhoMaximo = 80 * 1024 * 1024;
        if (file.getSize() > tamanhoMaximo) {
            throw new IllegalArgumentException("Arquivo muito grande (máximo: 80MB)");
        }
    }

    public byte[] baixarPdfAnteprojeto(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);

        if (projeto.getStatusAnteprojeto() != StatusAnteprojeto.PAGO) {
            throw new AcessoInvalidoException("Anteprojeto ainda não foi pago. Status atual: " + projeto.getStatusAnteprojeto());
        }

        if (projeto.getPdfAnteprojeto() == null || projeto.getPdfAnteprojeto().length == 0) {
            throw new AcessoInvalidoException("PDF do anteprojeto não encontrado. Entre em contato com o administrador.");
        }

        return projeto.getPdfAnteprojeto();
    }

    public byte[] baixarPdfExecutivo(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);

        if (projeto.getStatusExecutivo() != StatusExecutivo.PAGO) {
            throw new AcessoInvalidoException("Projeto executivo ainda não foi pago. Status atual: " + projeto.getStatusExecutivo());
        }

        if (projeto.getPdfExecutivo() == null || projeto.getPdfExecutivo().length == 0) {
            throw new AcessoInvalidoException("PDF do executivo não encontrado. Entre em contato com o administrador.");
        }

        return projeto.getPdfExecutivo();
    }


    public StatusProjetoResponse obterStatusProjeto(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);
        StatusProjetoResponse response = new StatusProjetoResponse();

        processarStatusAnteprojeto(projeto, response);
        processarStatusExecutivo(projeto, response);

        return response;
    }

    private void processarStatusAnteprojeto(Projeto projeto, StatusProjetoResponse response) {
        response.setStatusAnteprojeto(projeto.getStatusAnteprojeto().toString());

        if (projeto.getStatusAnteprojeto() == StatusAnteprojeto.AGUARDANDO_PAGAMENTO) {
            configurarPagamentoAnteprojeto(projeto, response);
        } else if (projeto.getStatusAnteprojeto() == StatusAnteprojeto.PAGO) {
            configurarDownloadAnteprojeto(projeto, response);
        }
    }

    private void processarStatusExecutivo(Projeto projeto, StatusProjetoResponse response) {
        if (projeto.getStatusAnteprojeto() != StatusAnteprojeto.PAGO) {
            return;
        }

        response.setStatusExecutivo(projeto.getStatusExecutivo().toString());

        if (projeto.getStatusExecutivo() == StatusExecutivo.AGUARDANDO_PAGAMENTO) {
            configurarPagamentoExecutivo(projeto, response);
        } else if (projeto.getStatusExecutivo() == StatusExecutivo.PAGO) {
            configurarDownloadExecutivo(projeto, response);
        }
    }

    private void configurarPagamentoAnteprojeto(Projeto projeto, StatusProjetoResponse response) {
        response.setValorAnteprojeto(String.format("R$ %.2f", projeto.getValorAnteprojeto()));
        response.setAnteprojetoDisponivelDownload(false);
        response.setMensagem("Realize o pagamento do anteprojeto para ter acesso ao PDF.");

        String txId = "ANTE" + projeto.getId();
        gerarQrCodePix(projeto.getValorAnteprojeto(), txId, response, true);
    }

    private void configurarPagamentoExecutivo(Projeto projeto, StatusProjetoResponse response) {
        response.setValorExecutivo(String.format("R$ %.2f", projeto.getValorExecutivo()));
        response.setExecutivoDisponivelDownload(false);
        response.setMensagem("Anteprojeto pago. Realize o pagamento do executivo para ter acesso ao PDF final.");

        String txId = "EXEC" + projeto.getId();
        gerarQrCodePix(projeto.getValorExecutivo(), txId, response, false);
    }

    private void configurarDownloadAnteprojeto(Projeto projeto, StatusProjetoResponse response) {
        LocalDateTime dataPagamento = projeto.getDataPagamentoAnteprojeto();

        if (dataPagamento == null) {
            throw new RuntimeException("Data de pagamento do anteprojeto não registrada");
        }

        int diasRestantes = calcularDiasRestantes(dataPagamento);

        if (diasRestantes > 0) {
            response.setAnteprojetoDisponivelDownload(true);
            response.setDiasRestantesAnteprojeto(diasRestantes);
            response.setMensagem("Anteprojeto disponível para download por " + diasRestantes + " dias.");
        } else {
            response.setAnteprojetoDisponivelDownload(false);
            response.setMensagem("Prazo de download do anteprojeto expirado.");
        }
    }

    private void configurarDownloadExecutivo(Projeto projeto, StatusProjetoResponse response) {
        LocalDateTime dataPagamento = projeto.getDataPagamentoExecutivo();

        if (dataPagamento == null) {
            throw new RuntimeException("Data de pagamento do executivo não registrada");
        }

        int diasRestantes = calcularDiasRestantes(dataPagamento);

        if (diasRestantes > 0) {
            response.setExecutivoDisponivelDownload(true);
            response.setDiasRestantesExecutivo(diasRestantes);
            response.setMensagem("Projeto executivo disponível para download por " + diasRestantes + " dias.");
        } else {
            response.setExecutivoDisponivelDownload(false);
            response.setMensagem("Prazo de download do executivo expirado.");
        }
    }

    private int calcularDiasRestantes(LocalDateTime dataPagamento) {
        long diasDesdePagemento = ChronoUnit.DAYS.between(dataPagamento, LocalDateTime.now());
        return 7 - (int) diasDesdePagemento;
    }

    private void gerarQrCodePix(BigDecimal valor, String txId, StatusProjetoResponse response, boolean isAnteprojeto) {
        String pixKey = "13246316600";
        String pixName = "JAQUELINE";
        String pixCity = "BETIM";
        String valorStr = valor.setScale(2, RoundingMode.HALF_UP).toString();

        String payload = gerarPayloadPix(pixKey, pixName, pixCity, valorStr, txId);

        if (isAnteprojeto) {
            response.setPixPayloadAnteprojeto(payload);
            response.setQrcodeAnteprojeto(null);
        } else {
            response.setPixPayloadExecutivo(payload);
            response.setQrcodeExecutivo(null);
        }
    }

    private String gerarPayloadPix(String pixKey, String name, String city, String amount, String txId) {
        String merchantAccount = "0014BR.GOV.BCB.PIX01" + String.format("%02d", pixKey.length()) + pixKey;
        String merchantCategory = "52040000";
        String currency = "5303986";
        String transactionAmount = "54" + String.format("%02d", amount.length()) + amount;
        String countryCode = "5802BR";
        String merchantName = "59" + String.format("%02d", name.length()) + name;
        String merchantCity = "60" + String.format("%02d", city.length()) + city;
        String additionalInfo = "62" + String.format("%02d", 8 + txId.length()) + "05" + String.format("%02d", txId.length()) + txId;

        String payload = "00020126" + String.format("%02d", merchantAccount.length()) + merchantAccount
                + merchantCategory + currency + transactionAmount + countryCode
                + merchantName + merchantCity + additionalInfo + "6304";

        String crc = calcularCRC16(payload);
        return payload + crc;
    }

    private String calcularCRC16(String payload) {
        int crc = 0xFFFF;
        byte[] bytes = payload.getBytes();

        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
            }
        }

        return String.format("%04X", crc & 0xFFFF);
    }
}
