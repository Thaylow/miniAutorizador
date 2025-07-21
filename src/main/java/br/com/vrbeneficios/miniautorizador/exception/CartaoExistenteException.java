package br.com.vrbeneficios.miniautorizador.exception;

public class CartaoExistenteException extends RuntimeException {
    private final String numeroCartao;
    private final String senha;

    public CartaoExistenteException(String numeroCartao, String senha) {
        super("Cartão já existe: " + numeroCartao);
        this.numeroCartao = numeroCartao;
        this.senha = senha;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getSenha() {
        return senha;
    }
}
