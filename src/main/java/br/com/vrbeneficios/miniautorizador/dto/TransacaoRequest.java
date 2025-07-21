package br.com.vrbeneficios.miniautorizador.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransacaoRequest {
    @NotBlank
    private String numeroCartao;

    @NotBlank
    private String senhaCartao;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valor;
}
