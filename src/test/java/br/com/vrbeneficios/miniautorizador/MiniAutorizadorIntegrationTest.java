package br.com.vrbeneficios.miniautorizador;

import br.com.vrbeneficios.miniautorizador.dto.CartaoRequest;
import br.com.vrbeneficios.miniautorizador.dto.CartaoResponse;
import br.com.vrbeneficios.miniautorizador.dto.TransacaoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MiniAutorizadorIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void fluxoCompletoDeAutorizacao() {
        String numero = "6549873025634501";
        String senha  = "1234";

        // 1) Verifica se o cartão já existe antes de criar
        ResponseEntity<BigDecimal> respSaldoExistente =
                rest.getForEntity(url("/cartoes/" + numero), BigDecimal.class);

        if (respSaldoExistente.getStatusCode() != HttpStatus.OK) {
            // Cartão não existe, cria ele
            CartaoRequest cartaoReq = new CartaoRequest();
            cartaoReq.setNumeroCartao(numero);
            cartaoReq.setSenha(senha);

            ResponseEntity<CartaoResponse> respCreate =
                    rest.postForEntity(url("/cartoes"), cartaoReq, CartaoResponse.class);

            assertThat(respCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(respCreate.getBody())
                    .extracting("numeroCartao", "senha")
                    .containsExactly(numero, senha);
        }

        // 1.1) Tentar criar o mesmo cartão novamente → deve retornar 422 e "CARTAO_JA_EXISTE"
        CartaoRequest cartaoReqDup = new CartaoRequest();
        cartaoReqDup.setNumeroCartao(numero);
        cartaoReqDup.setSenha(senha);

        ResponseEntity<String> respCartaoExistente =
                rest.postForEntity(url("/cartoes"), cartaoReqDup, String.class);

        assertThat(respCartaoExistente.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        // 2) Consulta de saldo atual
        BigDecimal saldoAtual =
                rest.getForEntity(url("/cartoes/" + numero), BigDecimal.class).getBody();

        assertThat(saldoAtual).isNotNull();
        System.out.println("Saldo atual: " + saldoAtual);

        // 3) Tentar debitar R$ 100,00 se houver saldo
        BigDecimal valorTransacao = new BigDecimal("100.00");

        if (saldoAtual.compareTo(valorTransacao) >= 0) {
            TransacaoRequest tx = new TransacaoRequest();
            tx.setNumeroCartao(numero);
            tx.setSenhaCartao(senha);
            tx.setValor(valorTransacao);

            ResponseEntity<String> respTx =
                    rest.postForEntity(url("/transacoes"), tx, String.class);

            assertThat(respTx.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(respTx.getBody()).isEqualTo("OK");

            // Verifica novo saldo
            BigDecimal novoSaldo =
                    rest.getForEntity(url("/cartoes/" + numero), BigDecimal.class).getBody();

            BigDecimal saldoEsperado = saldoAtual.subtract(valorTransacao);
            assertThat(novoSaldo).isEqualByComparingTo(saldoEsperado);
        }

        // 4) Tentar debitar R$ 500,00 → deve falhar com saldo insuficiente
        TransacaoRequest txFail = new TransacaoRequest();
        txFail.setNumeroCartao(numero);
        txFail.setSenhaCartao(senha);
        txFail.setValor(new BigDecimal("500.00"));

        ResponseEntity<String> respFail =
                rest.postForEntity(url("/transacoes"), txFail, String.class);
        assertThat(respFail.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(respFail.getBody()).isEqualTo("SALDO_INSUFICIENTE");

        // 5) Transação com senha inválida
        TransacaoRequest txSenhaErrada = new TransacaoRequest();
        txSenhaErrada.setNumeroCartao(numero);
        txSenhaErrada.setSenhaCartao("0000");
        txSenhaErrada.setValor(new BigDecimal("10.00"));

        ResponseEntity<String> respSenhaErrada =
                rest.postForEntity(url("/transacoes"), txSenhaErrada, String.class);
        assertThat(respSenhaErrada.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(respSenhaErrada.getBody()).isEqualTo("SENHA_INVALIDA");

        // 6) Transação com cartão inexistente
        TransacaoRequest txCartaoNaoExiste = new TransacaoRequest();
        txCartaoNaoExiste.setNumeroCartao("0000000000000000");
        txCartaoNaoExiste.setSenhaCartao("1234");
        txCartaoNaoExiste.setValor(new BigDecimal("10.00"));

        ResponseEntity<String> respCartaoNaoExiste =
                rest.postForEntity(url("/transacoes"), txCartaoNaoExiste, String.class);
        assertThat(respCartaoNaoExiste.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(respCartaoNaoExiste.getBody()).isEqualTo("CARTAO_INEXISTENTE");
    }

}
