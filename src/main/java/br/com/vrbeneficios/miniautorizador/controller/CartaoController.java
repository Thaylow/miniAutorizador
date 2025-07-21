package br.com.vrbeneficios.miniautorizador.controller;

import br.com.vrbeneficios.miniautorizador.dto.CartaoRequest;
import br.com.vrbeneficios.miniautorizador.dto.CartaoResponse;
import br.com.vrbeneficios.miniautorizador.service.CartaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CartaoService service;

    public CartaoController(CartaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CartaoResponse> criarCartao(@Valid @RequestBody CartaoRequest request) {
        service.criarCartao(request);
        CartaoResponse response = new CartaoResponse(request.getNumeroCartao(), request.getSenha());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> obterSaldo(@PathVariable String numeroCartao) {
        BigDecimal saldo = service.obterSaldo(numeroCartao);
        return ResponseEntity.ok(saldo);
    }
}
