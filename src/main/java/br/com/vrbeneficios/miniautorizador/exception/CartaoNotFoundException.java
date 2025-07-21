package br.com.vrbeneficios.miniautorizador.exception;

public class CartaoNotFoundException extends RuntimeException {
    public CartaoNotFoundException(String numero) {
        super("Cartão não encontrado: " + numero);
    }
}
