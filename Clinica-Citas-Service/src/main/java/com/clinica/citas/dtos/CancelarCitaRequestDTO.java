package com.clinica.citas.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelarCitaRequestDTO {

    @Size(max = 300, message = "El motivo de cancelacion no debe superar 300 caracteres")
    private String motivoCancelacion;
}
