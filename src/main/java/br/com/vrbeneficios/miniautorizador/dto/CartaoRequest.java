package br.com.vrbeneficios.miniautorizador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CartaoRequest {

    @NotBlank
    @Size(min = 16 , max = 16)
    private String numeroCartao;

    @NotBlank
    @Size(min = 4 , max = 6)
    private String senha;
}

