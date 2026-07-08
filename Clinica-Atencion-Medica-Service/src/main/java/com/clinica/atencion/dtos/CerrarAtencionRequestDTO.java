package com.clinica.atencion.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CerrarAtencionRequestDTO {

    @NotBlank
    @Size(max = 700)
    private String tratamiento;

    @Size(max = 700)
    private String indicacionesReceta;
}
