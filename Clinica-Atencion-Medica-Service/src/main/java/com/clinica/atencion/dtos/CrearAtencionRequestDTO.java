package com.clinica.atencion.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CrearAtencionRequestDTO {

    @NotNull
    private Long historiaClinicaId;

    @NotNull
    private Long pacienteId;

    @NotNull
    private Long medicoId;

    @Size(max = 80)
    private String citaId;

    private LocalDateTime fechaAtencion;

    @NotBlank
    @Size(max = 300)
    private String motivoConsulta;

    @NotBlank
    @Size(max = 500)
    private String diagnostico;

    @Size(max = 700)
    private String tratamiento;

    @Size(max = 700)
    private String indicacionesReceta;
}
