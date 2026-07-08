package com.clinica.citas.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisponibilidadResponseDTO {
    private Long medicoId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponible;
}
