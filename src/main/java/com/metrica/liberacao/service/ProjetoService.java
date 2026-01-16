package com.metrica.liberacao.service;

import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.domain.status.StatusExecutivo;
import com.metrica.liberacao.dto.CriarProjetoRequest;
import com.metrica.liberacao.dto.LiberacaoResponse;
import com.metrica.liberacao.dto.ValidarAcessoRequest;
import com.metrica.liberacao.exception.AcessoInvalidoException;
import com.metrica.liberacao.repository.ProjetoRepository;
import com.metrica.liberacao.util.QRCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final QRCodeGenerator qrCodeGenerator;

    public ProjetoService(ProjetoRepository projetoRepository, QRCodeGenerator qrCodeGenerator) {
        this.projetoRepository = projetoRepository;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    // ========== MÉTODOS DE CRIAÇÃO E BUSCA ==========

    /**
     * Cria um novo projeto (Admin)
     */
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

    /**
     * Busca projeto por ID interno (Admin)
     */
    public Projeto buscarProjetoOuFalhar(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + id));
    }

    /**
     * Busca projeto por código de acesso + PIN (Cliente)
     */
    public Projeto buscarProjetoPorCodigoEPin(String codigoAcesso, String pinAcesso) {
        // Busca código E PIN juntos no banco de dados
        return projetoRepository.findByCodigoAcessoAndPinAcesso(codigoAcesso, pinAcesso)
                .orElseThrow(() -> new AcessoInvalidoException("Código ou PIN inválidos"));
    }


    /**
     * Salva projeto (usado para atualizações)
     */
    public Projeto salvarProjeto(Projeto projeto) {
        return projetoRepository.save(projeto);
    }

    // ========== MÉTODOS DE VALIDAÇÃO DE LIBERAÇÃO ==========

    /**
     * Verifica se pode liberar download do Anteprojeto
     */
    public boolean podeLiberarDownloadAnteprojeto(String codigoAcesso) {
        return projetoRepository.findByCodigoAcesso(codigoAcesso)
                .map(projeto -> projeto.getStatusAnteprojeto() == StatusAnteprojeto.PAGO)
                .orElse(false);
    }

    /**
     * Verifica se pode liberar download do Executivo
     */
    public boolean podeLiberarDownloadExecutivo(String codigoAcesso) {
        return projetoRepository.findByCodigoAcesso(codigoAcesso)
                .map(projeto -> projeto.getStatusExecutivo() == StatusExecutivo.PAGO)
                .orElse(false);
    }

    /**
     * Verifica liberação e gera QR Code se necessário (Cliente)
     */
    public LiberacaoResponse verificarLiberacao(ValidarAcessoRequest request) {
        // ✅ CORRIGIDO: usa codigoAcesso, não pinAcesso
        Projeto projeto = buscarProjetoPorCodigoEPin(request.getCodigoAcesso(), request.getPinAcesso());

        LiberacaoResponse response = new LiberacaoResponse();

        response.setAcessoValido(true);
        response.setProjetoId(projeto.getId());  // ✅ Agora funciona
        response.setNomeCliente(projeto.getNomeCliente());  // ✅ Agora funciona

        boolean anteprojetoPago = projeto.getStatusAnteprojeto() == StatusAnteprojeto.PAGO;
        boolean executivoPago = projeto.getStatusExecutivo() == StatusExecutivo.PAGO;

        response.setAnteprojetoLiberado(anteprojetoPago);
        response.setExecutivoLiberado(executivoPago);

        // Gera QR Code apenas se algum projeto NÃO estiver pago
        if (!anteprojetoPago || !executivoPago) {
            String qrCodeData = String.format("projeto:%s|pin:%s",
                    projeto.getCodigoAcesso(),
                    request.getPinAcesso());

            byte[] qrCodeBytes = qrCodeGenerator.gerar(qrCodeData, 200, 200);
            String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
            response.setQrCode(qrCodeBase64);
        }

        // Define mensagem baseada no status
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


    // ========== MÉTODOS DE UPLOAD DE PDF (Admin) ==========

    /**
     * Salva PDF do Anteprojeto (Admin)
     */
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

    /**
     * Salva PDF do Executivo (Admin)
     */
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

    /**
     * Valida se o arquivo é PDF e respeita o tamanho máximo
     */
    private void validarArquivoPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }

        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Apenas arquivos PDF são permitidos");
        }

        long tamanhoMaximo = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > tamanhoMaximo) {
            throw new IllegalArgumentException("Arquivo muito grande (máximo: 10MB)");
        }
    }

    // ========== MÉTODOS DE DOWNLOAD DE PDF (Cliente) ==========

    /**
     * Baixa PDF do Anteprojeto (Cliente)
     */
    public byte[] baixarPdfAnteprojeto(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);

        if (projeto.getStatusAnteprojeto() != StatusAnteprojeto.PAGO) {
            throw new IllegalStateException("Anteprojeto ainda não foi pago. Status atual: " + projeto.getStatusAnteprojeto());
        }

        if (projeto.getPdfAnteprojeto() == null || projeto.getPdfAnteprojeto().length == 0) {
            throw new IllegalStateException("PDF do anteprojeto não encontrado. Entre em contato com o administrador.");
        }

        return projeto.getPdfAnteprojeto();
    }

    /**
     * Baixa PDF do Executivo (Cliente)
     */
    public byte[] baixarPdfExecutivo(String codigoAcesso, String pinAcesso) {
        Projeto projeto = buscarProjetoPorCodigoEPin(codigoAcesso, pinAcesso);

        if (projeto.getStatusExecutivo() != StatusExecutivo.PAGO) {
            throw new IllegalStateException("Projeto executivo ainda não foi pago. Status atual: " + projeto.getStatusExecutivo());
        }

        if (projeto.getPdfExecutivo() == null || projeto.getPdfExecutivo().length == 0) {
            throw new IllegalStateException("PDF do executivo não encontrado. Entre em contato com o administrador.");
        }

        return projeto.getPdfExecutivo();
    }

    // ========== MÉTODOS DE ATUALIZAÇÃO DE STATUS (Admin) ==========

    /**
     * Atualiza status de pagamento do Anteprojeto (Admin)
     */
    public Projeto atualizarStatusAnteprojeto(Long id, StatusAnteprojeto novoStatus) {
        Projeto projeto = buscarProjetoOuFalhar(id);
        projeto.setStatusAnteprojeto(novoStatus);
        return projetoRepository.save(projeto);
    }

    /**
     * Atualiza status de pagamento do Executivo (Admin)
     */
    public Projeto atualizarStatusExecutivo(Long id, StatusExecutivo novoStatus) {
        Projeto projeto = buscarProjetoOuFalhar(id);
        projeto.setStatusExecutivo(novoStatus);
        return projetoRepository.save(projeto);
    }
}
