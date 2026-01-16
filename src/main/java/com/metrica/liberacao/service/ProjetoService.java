package com.metrica.liberacao.service;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.dto.CriarProjetoRequest;
import com.metrica.liberacao.dto.LiberacaoResponse;
import com.metrica.liberacao.dto.ValidarAcessoRequest;
import com.metrica.liberacao.exception.AcessoInvalidoException;
import com.metrica.liberacao.exception.ArquivoInvalidoException;
import com.metrica.liberacao.exception.ProjetoNaoEncontradoException;
import com.metrica.liberacao.repository.ProjetoRepository;
import com.metrica.liberacao.util.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;

    public ProjetoService(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
    }

    @Autowired
    private ProjetoRepository repositorio;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    public Projeto criarProjeto(CriarProjetoRequest request) {
        if (projetoRepository.findByCodigoAcesso(request.getCodigoAcesso()).isPresent()) {
            throw new IllegalArgumentException("Código de acesso já existe");
        }

        Projeto projeto = new Projeto();
        projeto.setCodigoAcesso(request.getCodigoAcesso());
        projeto.setPinAcesso(request.getPinAcesso());
        projeto.setNomeCliente(request.getNomeCliente());
        projeto.setStatusAnteprojeto(StatusAnteprojeto.PENDENTE);
        projeto.setStatusExecutivo(StatusExecutivo.PENDENTE);

        return projetoRepository.save(projeto);
    }

    public Projeto buscarProjetoOuFalhar(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ProjetoNaoEncontradoException(id));
    }


    public boolean podeLiberarDownloadAnteprojeto(Long id) {
        Optional<Projeto> projeto = projetoRepository.findById(id);
        return projeto.isPresent()
                && projeto.get().getStatusAnteprojeto() == StatusAnteprojeto.PAGO;
    }

    public boolean podeLiberarDownloadExecutivo(Long id) {
        Optional<Projeto> projeto = projetoRepository.findById(id);
        return projeto.isPresent()
                && projeto.get().getStatusExecutivo() == StatusExecutivo.PAGO;
    }

    public Projeto validarAcesso(Long id, String pinAcesso) {
        Projeto projeto = buscarProjetoOuFalhar(id);

        if (!projeto.getPinAcesso().equals(pinAcesso)) {
            throw new AcessoInvalidoException(pinAcesso);
        }

        return projeto;
    }

    public LiberacaoResponse verificarLiberacao(ValidarAcessoRequest request) {
        Projeto projeto = buscarProjetoOuFalhar(request.getProjetoId());
        LiberacaoResponse response = new LiberacaoResponse();

        if (!projeto.getPinAcesso().equals(request.getPinAcesso())) {
            response.setAcessoValido(false);
            response.setMensagem("PIN de acesso inválido.");
            return response;
        }

        response.setAcessoValido(true);

        boolean anteprojetoPago = projeto.getStatusAnteprojeto() == StatusAnteprojeto.PAGO;
        boolean executivoPago = projeto.getStatusExecutivo() == StatusExecutivo.PAGO;

        response.setAnteprojetoLiberado(anteprojetoPago);
        response.setExecutivoLiberado(executivoPago);

        if (!anteprojetoPago || !executivoPago) {
            String qrCodeData = "projeto:" + projeto.getId() + "|pin:" + request.getPinAcesso();
            byte[] qrCodeBytes = qrCodeGenerator.gerar(qrCodeData, 200, 200);
            String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
            response.setQrCode(qrCodeBase64);
        }

        if (anteprojetoPago && executivoPago) {
            response.setMensagem("Todos os projetos liberados para download.");
        } else if (anteprojetoPago) {
            response.setMensagem("Apenas o anteprojeto está liberado para download.");
        } else if (executivoPago) {
            response.setMensagem("Apenas o projeto executivo está liberado para download.");
        } else {
            response.setMensagem("Nenhum projeto está liberado para download. Escaneie o QR Code para efetuar o pagamento.");
        }

        return response;
    }


    public void salvarPdfAnteprojeto(Long id, MultipartFile file) {
        Projeto projeto = buscarProjetoOuFalhar(id);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo enviado está vazio.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new ArquivoInvalidoException("O arquivo enviado não é um PDF.");
        }

        try {
            projeto.setPdfAnteprojeto(file.getBytes());
            projetoRepository.save(projeto);
        } catch (IOException e) {
            throw new ArquivoInvalidoException("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    public void salvarPdfExecutivo(Long id, MultipartFile file) {
        Projeto projeto = buscarProjetoOuFalhar(id);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo enviado está vazio.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new ArquivoInvalidoException("O arquivo enviado não é um PDF.");
        }

        try {
            projeto.setPdfExecutivo(file.getBytes());
            projetoRepository.save(projeto);
        } catch (IOException e) {
            throw new ArquivoInvalidoException("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    public byte[] baixarPdfAnteprojeto(Long id, String pinAcesso) {
        Projeto projeto = validarAcesso(id, pinAcesso);

        if (projeto.getStatusAnteprojeto() != StatusAnteprojeto.PAGO) {
            throw new IllegalArgumentException("Anteprojeto não está liberado para download.");
        }

        if (projeto.getPdfAnteprojeto() == null || projeto.getPdfAnteprojeto().length == 0) {
            throw new ArquivoInvalidoException("Anteprojeto não foi enviado.");
        }

        return projeto.getPdfAnteprojeto();
    }


    public byte[] baixarPdfExecutivo(Long id, String pinAcesso) {
        Projeto projeto = validarAcesso(id, pinAcesso);

        if (projeto.getStatusExecutivo() != StatusExecutivo.PAGO) {
            throw new IllegalArgumentException("Executivo não está liberado para download.");
        }

        if (projeto.getPdfExecutivo() == null || projeto.getPdfExecutivo().length == 0) {
            throw new ArquivoInvalidoException("Executivo não foi enviado.");
        }

        return projeto.getPdfExecutivo();
    }

    public Projeto salvarProjeto(Projeto projeto) {
        return projetoRepository.save(projeto);
    }


}
