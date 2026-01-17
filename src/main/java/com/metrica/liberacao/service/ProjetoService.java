package com.metrica.liberacao.service;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.dto.*;
import com.metrica.liberacao.exception.AcessoInvalidoException;
import com.metrica.liberacao.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

        // Adicionar qrCode: definir payload se não pago, senão null
        if (!anteprojetoPago || !executivoPago) {
            response.setQrCode("payload_qr_pix_aqui"); // Substitua pelo payload real do QR Pix
        } else {
            response.setQrCode(null);
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
            throw new AcessoInvalidoException(
                    "Anteprojeto ainda não foi pago. Status atual: " + projeto.getStatusAnteprojeto()
            );
        }

        if (projeto.getPdfAnteprojeto() == null || projeto.getPdfAnteprojeto().length == 0) {
            throw new AcessoInvalidoException(
                    "PDF do anteprojeto não encontrado. Entre em contato com o administrador."
            );
        }

        return projeto.getPdfAnteprojeto();
    }

    public byte[] baixarPdfExecutivo(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);

        if (projeto.getStatusExecutivo() != StatusExecutivo.PAGO) {
            throw new AcessoInvalidoException(
                    "Projeto executivo ainda não foi pago. Status atual: " + projeto.getStatusExecutivo()
            );
        }

        if (projeto.getPdfExecutivo() == null || projeto.getPdfExecutivo().length == 0) {
            throw new AcessoInvalidoException(
                    "PDF do executivo não encontrado. Entre em contato com o administrador."
            );
        }

        return projeto.getPdfExecutivo();
    }


    private int calcularDiasRestantes(LocalDateTime dataPagamento) {
        long diasDesdePagemento = ChronoUnit.DAYS.between(dataPagamento, LocalDateTime.now());
        return 7 - (int) diasDesdePagemento;
    }

    private void configurarDownloadAnteprojeto(Projeto projeto, StatusProjetoResponse response) {
        LocalDateTime dataPagamento = projeto.getDataPagamentoAnteprojeto();

        if (dataPagamento == null) {
            throw new IllegalStateException("Data de pagamento do anteprojeto não registrada");
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
            throw new IllegalStateException("Data de pagamento do executivo não registrada");
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


}
