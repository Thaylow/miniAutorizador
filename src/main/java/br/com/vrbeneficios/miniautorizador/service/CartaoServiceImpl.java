package br.com.vrbeneficios.miniautorizador.service;

import br.com.vrbeneficios.miniautorizador.dto.CartaoRequest;
import br.com.vrbeneficios.miniautorizador.dto.TransacaoRequest;
import br.com.vrbeneficios.miniautorizador.model.Cartao;
import br.com.vrbeneficios.miniautorizador.repository.CartaoRepository;
import br.com.vrbeneficios.miniautorizador.exception.CartaoExistenteException;
import br.com.vrbeneficios.miniautorizador.exception.CartaoInexistenteException;
import br.com.vrbeneficios.miniautorizador.exception.CartaoNotFoundException;
import br.com.vrbeneficios.miniautorizador.exception.SaldoInsuficienteException;
import br.com.vrbeneficios.miniautorizador.exception.SenhaInvalidaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartaoServiceImpl implements CartaoService {

    private static final BigDecimal SALDO_INICIAL = BigDecimal.valueOf(500).setScale(2);
    private final CartaoRepository repository;

    public CartaoServiceImpl(CartaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void criarCartao(CartaoRequest request) {
        if (repository.existsById(request.getNumeroCartao())) {
            throw new CartaoExistenteException(request.getNumeroCartao(), request.getSenha());
        }
        
        Cartao cartao = Cartao.builder()
            .numeroCartao(request.getNumeroCartao())
            .senha(request.getSenha())
            .saldo(SALDO_INICIAL)
            .build();
        repository.save(cartao);
    }

    @Override
    public BigDecimal obterSaldo(String numeroCartao) {
        return repository.findById(numeroCartao)
            .orElseThrow(() -> new CartaoNotFoundException(numeroCartao))
            .getSaldo();
    }

    @Override
    @Transactional
    public void autorizarTransacao(TransacaoRequest request) {
        Cartao cartao = repository.findById(request.getNumeroCartao())
                .orElseThrow(CartaoInexistenteException::new);

        if (!cartao.getSenha().equals(request.getSenhaCartao())) {
            throw new SenhaInvalidaException();
        }
        if (cartao.getSaldo().compareTo(request.getValor()) < 0) {
            throw new SaldoInsuficienteException();
        }

        cartao.setSaldo(cartao.getSaldo().subtract(request.getValor()));
        repository.save(cartao);
    }
}
