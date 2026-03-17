package com.metrica.liberacao.service;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.dto.*;
import com.metrica.liberacao.exception.AcessoInvalidoException;
import com.metrica.liberacao.repository.ProjetoRepository;
import com.metrica.liberacao.repository.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.regex.Pattern;


@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final StorageService storageService;
    private static final Pattern CODIGO_ACESSO_PATTERN = Pattern.compile("^\\d{3}$");
    private static final Pattern PIN_ACESSO_PATTERN = Pattern.compile("^\\d{4}$");

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private void validarFormatoAcesso(String codigoAcesso, String pinAcesso) {
        if (!CODIGO_ACESSO_PATTERN.matcher(codigoAcesso).matches()) {
            throw new IllegalArgumentException("Código de acesso deve conter exatamente 3 dígitos numéricos");
        }
        if (!PIN_ACESSO_PATTERN.matcher(pinAcesso).matches()) {
            throw new IllegalArgumentException("PIN de acesso deve conter exatamente 4 dígitos numéricos");
        }
    }

    public ProjetoService(ProjetoRepository projetoRepository, @Qualifier("supabaseStorageService") StorageService storageService) {
        this.projetoRepository = projetoRepository;
        this.storageService = storageService;
    }

    public Projeto criarProjeto(CriarProjetoRequest request) {
        String codigoNormalizado = normalizar(request.getCodigoAcesso());
        String pinNormalizado = normalizar(request.getPinAcesso());

        validarFormatoAcesso(codigoNormalizado, pinNormalizado);

        if (projetoRepository.findByCodigoAcesso(codigoNormalizado).isPresent()) {
            throw new IllegalArgumentException("Código de acesso já existe");
        }

        Projeto projeto = new Projeto();
        projeto.setCodigoAcesso(codigoNormalizado);
        projeto.setPinAcesso(pinNormalizado);
        projeto.setNomeCliente(request.getNomeCliente());
        projeto.setValorAnteprojeto(request.getPrecoAnteprojeto());
        projeto.setValorExecutivo(request.getPrecoExecutivo());
        projeto.setStatusAnteprojeto(StatusAnteprojeto.AGUARDANDO_PAGAMENTO);
        projeto.setStatusExecutivo(StatusExecutivo.AGUARDANDO_PAGAMENTO);

        return projetoRepository.save(projeto);
    }

    public Projeto buscarProjetoOuFalhar(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + id));
    }

    public Projeto buscarProjetoPorCodigoEPin(String codigoAcesso, String pinAcesso) {
        String codigoNormalizado = normalizar(codigoAcesso);
        String pinNormalizado = normalizar(pinAcesso);

        validarFormatoAcesso(codigoNormalizado, pinNormalizado);

        Projeto projeto = projetoRepository.findByCodigoAcesso(codigoNormalizado)
                .orElseThrow(() -> new AcessoInvalidoException("Código ou PIN incorreto"));

        String pinProjeto = normalizar(String.valueOf(projeto.getPinAcesso()));
        if (!Objects.equals(pinProjeto, pinNormalizado)) {
            throw new AcessoInvalidoException("Código ou PIN incorreto");
        }

        return projeto;
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
        response.setStatusAnteprojeto(projeto.getStatusAnteprojeto());
        response.setStatusExecutivo(projeto.getStatusExecutivo());

        boolean anteprojetoPago = projeto.getStatusAnteprojeto() == StatusAnteprojeto.PAGO;

        boolean executivoPago = projeto.getStatusExecutivo() == StatusExecutivo.PAGO;

        response.setAnteprojetoLiberado(anteprojetoPago);
        response.setExecutivoLiberado(executivoPago);

        if (anteprojetoPago && executivoPago) {

            response.setMensagem(
                    "Anteprojeto e projeto executivo liberados para download."
            );

        } else if (anteprojetoPago) {

            response.setMensagem(
                    "Anteprojeto liberado. O projeto executivo está pronto, mas ainda não foi liberado."
            );

        } else if (executivoPago) {

            response.setMensagem(
                    "Projeto executivo liberado. O anteprojeto ainda não está disponível."
            );

        } else if (
                projeto.getStatusAnteprojeto() == StatusAnteprojeto.PRONTO ||
                        projeto.getStatusExecutivo() == StatusExecutivo.PRONTO
        ) {

            response.setMensagem(
                    "Seu projeto está finalizado e pronto. Para liberar o download, é necessário concluir o pagamento."
            );

        } else {

            response.setMensagem(
                    "Seu projeto ainda está em andamento."
            );
        }


        return response;
    }


    public void salvarPdfAnteprojeto(Long id, MultipartFile file) {
        validarArquivoPdf(file);
        Projeto projeto = buscarProjetoOuFalhar(id);
        String path = "projetos/" + projeto.getCodigoAcesso() + "/anteprojeto.pdf";
        storageService.upload("projetos", path, file);
        projeto.setPdfAnteprojeto(path);
        projetoRepository.save(projeto);
    }

    public void salvarPdfExecutivo(Long id, MultipartFile file) {
        validarArquivoPdf(file);
        Projeto projeto = buscarProjetoOuFalhar(id);
        String path = "projetos/" + projeto.getCodigoAcesso() + "/executivo.pdf";
        storageService.upload("projetos", path, file);
        projeto.setPdfExecutivo(path);
        projetoRepository.save(projeto);
    }

    private void validarArquivoPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }

        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Apenas arquivos PDF são permitidos");
        }

        long tamanhoMaximo = 50 * 1024 * 1024;
        if (file.getSize() > tamanhoMaximo) {
            throw new IllegalArgumentException("Arquivo muito grande (máximo: 50MB)");
        }
    }

    public byte[] baixarPdfAnteprojeto(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);

        if (projeto.getStatusAnteprojeto() != StatusAnteprojeto.PAGO) {
            throw new AcessoInvalidoException(
                    "Anteprojeto ainda não foi pago. Status atual: " + projeto.getStatusAnteprojeto()
            );
        }

        String path = projeto.getPdfAnteprojeto();
        if (path == null || path.isEmpty()) {
            throw new AcessoInvalidoException(
                    "Caminho do PDF do anteprojeto não encontrado. Entre em contato com o administrador."
            );
        }

        return storageService.download("projetos", path);
    }

    public byte[] baixarPdfExecutivo(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);

        if (projeto.getStatusExecutivo() != StatusExecutivo.PAGO) {
            throw new AcessoInvalidoException(
                    "Anteprojeto ainda não foi pago. Status atual: " + projeto.getStatusAnteprojeto()
            );
        }

        String path = projeto.getPdfExecutivo();
        if (path == null || path.isEmpty()) {
            throw new AcessoInvalidoException(
                    "Caminho do PDF do anteprojeto não encontrado. Entre em contato com o administrador."
            );
        }

        return storageService.download("projetos", path);
    }



}
