package com.clinica.caja.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CrearDeudaRequestDTO {

    @NotNull
    private Long pacienteId;

    @NotBlank
    @Size(max = 120)
    private String concepto;

    @Size(max = 50)
    private String referenciaTipo;

    @Size(max = 80)
    private String referenciaId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal montoTotal;
}
