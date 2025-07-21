package br.com.vrbeneficios.miniautorizador.controller;

import br.com.vrbeneficios.miniautorizador.dto.TransacaoRequest;
import br.com.vrbeneficios.miniautorizador.service.CartaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final CartaoService service;

    public TransacaoController(CartaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> realizarTransacao(@Valid @RequestBody TransacaoRequest request) {
        service.autorizarTransacao(request);
        return ResponseEntity.status(201).body("OK");
    }
}
