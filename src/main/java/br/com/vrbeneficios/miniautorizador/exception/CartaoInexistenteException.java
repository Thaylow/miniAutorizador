package br.com.vrbeneficios.miniautorizador.exception;

public class CartaoInexistenteException extends RuntimeException {
    public CartaoInexistenteException() {
        super("CARTAO_INEXISTENTE");
    }
}
