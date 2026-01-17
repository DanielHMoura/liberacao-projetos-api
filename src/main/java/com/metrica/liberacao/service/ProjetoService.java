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
        Projeto projeto = buscarProjetoPorCodigoEPin(
                request.getCodigoAcesso(),
                request.getPinAcesso()
        );

        LiberacaoResponse response = new LiberacaoResponse();
        response.setAcessoValido(true);
        response.setProjetoId(projeto.getId());
        response.setNomeCliente(projeto.getNomeCliente());
        response.setPrecoAnteprojeto(projeto.getValorAnteprojeto());
        response.setPrecoExecutivo(projeto.getValorExecutivo());

        boolean anteprojetoPago =
                projeto.getStatusAnteprojeto() == StatusAnteprojeto.PAGO;

        boolean executivoPago =
                anteprojetoPago &&
                        projeto.getStatusExecutivo() == StatusExecutivo.PAGO;

        response.setAnteprojetoLiberado(anteprojetoPago);
        response.setExecutivoLiberado(executivoPago);

        if (anteprojetoPago && executivoPago) {
            response.setMensagem(
                    "Anteprojeto e projeto executivo liberados para download."
            );
        } else if (anteprojetoPago) {
            response.setMensagem(
                    "Anteprojeto liberado. O projeto executivo ainda não está disponível."
            );
        } else {
            response.setMensagem(
                    "Nenhum projeto está liberado para download."
            );
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


}
