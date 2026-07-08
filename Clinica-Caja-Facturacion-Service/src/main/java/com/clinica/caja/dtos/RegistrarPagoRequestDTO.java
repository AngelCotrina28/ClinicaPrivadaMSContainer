package com.clinica.caja.dtos;

import com.clinica.caja.entities.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class RegistrarPagoRequestDTO {

    @NotNull
    private Long deudaId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal monto;

    @NotNull
    private MetodoPago metodoPago;

    @Size(max = 300)
    private String observacion;
}
