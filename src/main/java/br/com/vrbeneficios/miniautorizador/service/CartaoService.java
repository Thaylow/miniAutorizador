package br.com.vrbeneficios.miniautorizador.service;

import br.com.vrbeneficios.miniautorizador.dto.CartaoRequest;
import br.com.vrbeneficios.miniautorizador.dto.TransacaoRequest;

import java.math.BigDecimal;

public interface CartaoService {
    void criarCartao(CartaoRequest request);
    BigDecimal obterSaldo(String numeroCartao);
    void autorizarTransacao(TransacaoRequest request);
}
