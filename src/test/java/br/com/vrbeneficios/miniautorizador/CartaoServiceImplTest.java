package br.com.vrbeneficios.miniautorizador;

import br.com.vrbeneficios.miniautorizador.dto.CartaoRequest;
import br.com.vrbeneficios.miniautorizador.dto.TransacaoRequest;
import br.com.vrbeneficios.miniautorizador.exception.*;
import br.com.vrbeneficios.miniautorizador.model.Cartao;
import br.com.vrbeneficios.miniautorizador.repository.CartaoRepository;
import br.com.vrbeneficios.miniautorizador.service.CartaoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CartaoServiceImplTest {

    @Mock
    private CartaoRepository repository;

    @InjectMocks
    private CartaoServiceImpl service;

    private static final String NUMERO = "1234567812345678";
    private static final String SENHA = "1234";

    @Test
    void criarCartao_deveSalvarQuandoNaoExistir() {
        // dado
        when(repository.existsById(NUMERO)).thenReturn(false);
        CartaoRequest req = new CartaoRequest();
        req.setNumeroCartao(NUMERO);
        req.setSenha(SENHA);

        // quando
        service.criarCartao(req);

        // então
        ArgumentCaptor<Cartao> captor = ArgumentCaptor.forClass(Cartao.class);
        verify(repository).save(captor.capture());
        Cartao salvo = captor.getValue();
        assertEquals(NUMERO, salvo.getNumeroCartao());
        assertEquals(SENHA, salvo.getSenha());
        assertEquals(new BigDecimal("500.00"), salvo.getSaldo());
    }

    @Test
    void criarCartao_deveLancarQuandoJaExistir() {
        when(repository.existsById(NUMERO)).thenReturn(true);
        CartaoRequest req = new CartaoRequest();
        req.setNumeroCartao(NUMERO);
        req.setSenha(SENHA);

        CartaoExistenteException ex = assertThrows(
            CartaoExistenteException.class,
            () -> service.criarCartao(req)
        );
        assertTrue(ex.getMessage().contains(NUMERO));
    }

    @Test
    void obterSaldo_deveRetornarQuandoExistir() {
        Cartao cartao = Cartao.builder()
                              .numeroCartao(NUMERO)
                              .senha(SENHA)
                              .saldo(new BigDecimal("250.75"))
                              .build();
        when(repository.findById(NUMERO)).thenReturn(Optional.of(cartao));

        BigDecimal saldo = service.obterSaldo(NUMERO);
        assertEquals(new BigDecimal("250.75"), saldo);
    }

    @Test
    void obterSaldo_deveLancarQuandoNaoExistir() {
        when(repository.findById(NUMERO)).thenReturn(Optional.empty());
        assertThrows(CartaoNotFoundException.class,
                     () -> service.obterSaldo(NUMERO));
    }

    @Test
    void autorizarTransacao_deveDebitarQuandoTudoValido() {
        Cartao cartao = Cartao.builder()
                              .numeroCartao(NUMERO)
                              .senha(SENHA)
                              .saldo(new BigDecimal("100.00"))
                              .build();
        when(repository.findById(NUMERO)).thenReturn(Optional.of(cartao));

        TransacaoRequest tx = new TransacaoRequest();
        tx.setNumeroCartao(NUMERO);
        tx.setSenhaCartao(SENHA);
        tx.setValor(new BigDecimal("40.00"));

        service.autorizarTransacao(tx);

        // saldo final esperado = 60.00
        assertEquals(new BigDecimal("60.00"), cartao.getSaldo());
        verify(repository).save(cartao);
    }

    @Test
    void autorizarTransacao_deveLancarSenhaInvalida() {
        Cartao cartao = Cartao.builder()
                              .numeroCartao(NUMERO)
                              .senha("0000")
                              .saldo(new BigDecimal("100.00"))
                              .build();
        when(repository.findById(NUMERO)).thenReturn(Optional.of(cartao));

        TransacaoRequest tx = new TransacaoRequest();
        tx.setNumeroCartao(NUMERO);
        tx.setSenhaCartao(SENHA);
        tx.setValor(new BigDecimal("10.00"));

        assertThrows(SenhaInvalidaException.class,
                     () -> service.autorizarTransacao(tx));
        verify(repository, never()).save(any());
    }

    @Test
    void autorizarTransacao_deveLancarSaldoInsuficiente() {
        Cartao cartao = Cartao.builder()
                              .numeroCartao(NUMERO)
                              .senha(SENHA)
                              .saldo(new BigDecimal("5.00"))
                              .build();
        when(repository.findById(NUMERO)).thenReturn(Optional.of(cartao));

        TransacaoRequest tx = new TransacaoRequest();
        tx.setNumeroCartao(NUMERO);
        tx.setSenhaCartao(SENHA);
        tx.setValor(new BigDecimal("10.00"));

        assertThrows(SaldoInsuficienteException.class,
                     () -> service.autorizarTransacao(tx));
        verify(repository, never()).save(any());
    }

    @Test
    void autorizarTransacao_deveLancarCartaoInexistente() {
        // dado que não há cartão no repositório
        when(repository.findById(NUMERO)).thenReturn(Optional.empty());

        TransacaoRequest tx = new TransacaoRequest();
        tx.setNumeroCartao(NUMERO);
        tx.setSenhaCartao(SENHA);
        tx.setValor(new BigDecimal("10.00"));

        // agora esperamos a nova exceção
        assertThrows(CartaoInexistenteException.class,
                    () -> service.autorizarTransacao(tx));

        // e não deve chamar save()
        verify(repository, never()).save(any());
    }
}

