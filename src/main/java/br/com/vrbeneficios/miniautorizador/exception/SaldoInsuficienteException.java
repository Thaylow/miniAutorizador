package br.com.vrbeneficios.miniautorizador.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException() {
        super("SALDO_INSUFICIENTE");
    }
}
