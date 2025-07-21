package br.com.vrbeneficios.miniautorizador.exception;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException() {
        super("SENHA_INVALIDA");
    }
}
