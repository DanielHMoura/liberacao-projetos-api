package com.metrica.liberacao.service;

import com.metrica.liberacao.repository.ProjetoRepository;
import com.metrica.liberacao.domain.Projeto;
import com.metrica.liberacao.domain.status.StatusAnteprojeto;
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
    void testBaixarPdfAnteprojeto() {
        Long id = 1L;
        String pinValido = "1234";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(id);
        projetoMock.setPinAcesso("1234");
        projetoMock.setStatusAnteprojeto(StatusAnteprojeto.PAGO);
        projetoMock.setPdfAnteprojeto(new byte[]{1, 2, 3});

        when(repositorio.findById(id)).thenReturn(Optional.of(projetoMock));

        var resultado = servico.baixarPdfAnteprojeto(id, pinValido);

        assertNotNull(resultado);
        assertArrayEquals(new byte[]{1, 2, 3}, resultado);
        verify(repositorio, times(1)).findById(id);
    }


    @Test
    void testBaixarPdfAnteprojetoProjetoNaoEncontrado() {
        Long id = 999L;
        String pinValido = "1234";

        when(repositorio.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> servico.baixarPdfAnteprojeto(id, pinValido));
        verify(repositorio, times(1)).findById(id);
    }

    @Test
    void testBaixarPdfAnteprojeto_PinInvalido() {
        Long id = 1L;
        String pinInvalido = "9999";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(id);
        projetoMock.setPinAcesso("1234");
        projetoMock.setStatusAnteprojeto(StatusAnteprojeto.PAGO);

        when(repositorio.findById(anyLong())).thenReturn(Optional.of(projetoMock));

        assertThrows(Exception.class, () -> servico.baixarPdfAnteprojeto(id, pinInvalido));
    }

    @Test
    void testBaixarPdfAnteprojeto_StatusNaoPago() {
        Long id = 1L;
        String pinValido = "1234";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(id);
        projetoMock.setPinAcesso("1234");
        projetoMock.setStatusAnteprojeto(StatusAnteprojeto.PENDENTE);
        projetoMock.setPdfAnteprojeto(new byte[]{1, 2, 3});

        when(repositorio.findById(anyLong())).thenReturn(Optional.of(projetoMock));

        assertThrows(Exception.class, () -> servico.baixarPdfAnteprojeto(id, pinValido));
    }

    @Test
    void testBaixarPdfAnteprojeto_PdfNulo() {
        Long id = 1L;
        String pinValido = "1234";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(id);
        projetoMock.setPinAcesso("1234");
        projetoMock.setStatusAnteprojeto(StatusAnteprojeto.PAGO);
        projetoMock.setPdfAnteprojeto(null);

        when(repositorio.findById(anyLong())).thenReturn(Optional.of(projetoMock));

        assertThrows(Exception.class, () -> servico.baixarPdfAnteprojeto(id, pinValido));
    }

    @Test
    void testFindByCodigoAcesso() {
        String codigo = "ABC123";
        Projeto projetoMock = new Projeto();
        projetoMock.setId(1L);
        projetoMock.setCodigoAcesso(codigo);

        when(repositorio.findByCodigoAcesso(codigo)).thenReturn(Optional.of(projetoMock));

        Optional<Projeto> resultado = repositorio.findByCodigoAcesso(codigo);

        assertTrue(resultado.isPresent());
        assertEquals(codigo, resultado.get().getCodigoAcesso());
    }
}
