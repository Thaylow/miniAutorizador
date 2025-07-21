package br.com.vrbeneficios.miniautorizador.exception;

import br.com.vrbeneficios.miniautorizador.dto.CartaoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartaoExistenteException.class)
    public ResponseEntity<CartaoResponse> handleCartaoExistente(CartaoExistenteException ex) {
        // 422 + {"numeroCartao":"...","senha":"..."}
        CartaoResponse body = new CartaoResponse(ex.getNumeroCartao(), ex.getSenha());
        return ResponseEntity.unprocessableEntity().body(body);
    }

    @ExceptionHandler(CartaoNotFoundException.class)
    public ResponseEntity<Void> handleCartaoNotFound(CartaoNotFoundException ex) {
        // 404 sem body
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<String> handleSenhaInvalida(SenhaInvalidaException ex) {
        // 422 + "SENHA_INVALIDA"
        return ResponseEntity.unprocessableEntity().body("SENHA_INVALIDA");
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        // 422 + "SALDO_INSUFICIENTE"
        return ResponseEntity.unprocessableEntity().body("SALDO_INSUFICIENTE");
    }

    @ExceptionHandler(CartaoInexistenteException.class)
    public ResponseEntity<String> handleCartaoInexistente(CartaoInexistenteException ex) {
        // 422 + "CARTAO_INEXISTENTE"
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }

}
