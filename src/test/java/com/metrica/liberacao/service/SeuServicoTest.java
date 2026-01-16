package com.metrica.liberacao.service;

import com.metrica.liberacao.repository.ProjetoRepository;
import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
import com.metrica.liberacao.exception.AcessoInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SeuServicoTest {

    @Mock
    private ProjetoRepository repositorio;

    @InjectMocks
    private ProjetoService servico;

    @Test
    void testBaixarPdfAnteprojeto_Sucesso() {
        String codigoAcesso = "ABC123";
        String pinValido = "1234";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(1L);
        projetoMock.setCodigoAcesso(codigoAcesso);
        projetoMock.setPinAcesso(pinValido);
        projetoMock.setStatusAnteprojeto(StatusAnteprojeto.PAGO);
        projetoMock.setPdfAnteprojeto(new byte[]{1, 2, 3});

        when(repositorio.findByCodigoAcessoAndPinAcesso(codigoAcesso, pinValido))
                .thenReturn(Optional.of(projetoMock));

        byte[] resultado = servico.baixarPdfAnteprojeto(codigoAcesso, pinValido);

        assertNotNull(resultado);
        assertArrayEquals(new byte[]{1, 2, 3}, resultado);
    }

    @Test
    void testBaixarPdfAnteprojeto_ProjetoNaoEncontrado() {
        String codigoAcesso = "XYZ999";
        String pinValido = "1234";

        when(repositorio.findByCodigoAcessoAndPinAcesso(codigoAcesso, pinValido))
                .thenReturn(Optional.empty());

        assertThrows(AcessoInvalidoException.class, () ->
                servico.baixarPdfAnteprojeto(codigoAcesso, pinValido));
    }

    @Test
    void testBaixarPdfAnteprojeto_PinInvalido() {
        String codigoAcesso = "ABC123";
        String pinInvalido = "9999";

        when(repositorio.findByCodigoAcessoAndPinAcesso(codigoAcesso, pinInvalido))
                .thenReturn(Optional.empty());

        assertThrows(AcessoInvalidoException.class, () ->
                servico.baixarPdfAnteprojeto(codigoAcesso, pinInvalido));
    }

    @Test
    void testBaixarPdfAnteprojeto_StatusNaoPago() {
        String codigoAcesso = "ABC123";
        String pinValido = "1234";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(1L);
        projetoMock.setCodigoAcesso(codigoAcesso);
        projetoMock.setPinAcesso(pinValido);
        projetoMock.setStatusAnteprojeto(StatusAnteprojeto.PENDENTE);
        projetoMock.setPdfAnteprojeto(new byte[]{1, 2, 3});

        when(repositorio.findByCodigoAcessoAndPinAcesso(codigoAcesso, pinValido))
                .thenReturn(Optional.of(projetoMock));

        assertThrows(AcessoInvalidoException.class, () ->
                servico.baixarPdfAnteprojeto(codigoAcesso, pinValido));
    }

    @Test
    void testBaixarPdfAnteprojeto_PdfNulo() {
        String codigoAcesso = "ABC123";
        String pinValido = "1234";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(1L);
        projetoMock.setCodigoAcesso(codigoAcesso);
        projetoMock.setPinAcesso(pinValido);
        projetoMock.setStatusAnteprojeto(StatusAnteprojeto.PAGO);
        projetoMock.setPdfAnteprojeto(null);

        when(repositorio.findByCodigoAcessoAndPinAcesso(codigoAcesso, pinValido))
                .thenReturn(Optional.of(projetoMock));

        assertThrows(AcessoInvalidoException.class, () ->
                servico.baixarPdfAnteprojeto(codigoAcesso, pinValido));
    }

    @Test
    void testBuscarProjetoPorId_Sucesso() {
        Long id = 1L;
        Projeto projetoMock = new Projeto();
        projetoMock.setId(id);
        projetoMock.setCodigoAcesso("ABC123");

        when(repositorio.findById(id)).thenReturn(Optional.of(projetoMock));

        Projeto resultado = servico.buscarProjetoOuFalhar(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    @Test
    void testBuscarProjetoPorId_NaoEncontrado() {
        Long id = 999L;

        when(repositorio.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                servico.buscarProjetoOuFalhar(id));
    }

    @Test
    void testFindByCodigoAcesso() {
        String codigoAcesso = "ABC123";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(1L);
        projetoMock.setCodigoAcesso(codigoAcesso);

        when(repositorio.findByCodigoAcesso(codigoAcesso)).thenReturn(Optional.of(projetoMock));

        Optional<Projeto> resultado = repositorio.findByCodigoAcesso(codigoAcesso);

        assertTrue(resultado.isPresent());
        assertEquals(codigoAcesso, resultado.get().getCodigoAcesso());
    }
}
