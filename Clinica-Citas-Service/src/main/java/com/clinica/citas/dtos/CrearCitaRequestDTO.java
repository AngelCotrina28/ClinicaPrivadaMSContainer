package com.clinica.citas.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class CrearCitaRequestDTO {

    @NotNull(message = "El paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El medico es obligatorio")
    private Long medicoId;

    private Long especialidadId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @Size(max = 120, message = "El consultorio no debe superar 120 caracteres")
    private String consultorio;

    @Size(max = 300, message = "El motivo no debe superar 300 caracteres")
    private String motivo;
}
