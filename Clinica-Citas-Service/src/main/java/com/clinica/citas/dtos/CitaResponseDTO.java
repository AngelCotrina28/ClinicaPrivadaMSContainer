package com.clinica.citas.dtos;

import com.clinica.citas.entities.Cita;
import com.clinica.citas.entities.EstadoCita;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CitaResponseDTO {
    private String id;
    private Long pacienteId;
    private Long medicoId;
    private Long especialidadId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String consultorio;
    private String motivo;
    private EstadoCita estado;
    private String motivoCancelacion;
    private LocalDateTime createdAt;

    public static CitaResponseDTO fromEntity(Cita cita) {
        return CitaResponseDTO.builder()
                .id(cita.getId())
                .pacienteId(cita.getPacienteId())
                .medicoId(cita.getMedicoId())
                .especialidadId(cita.getEspecialidadId())
                .fecha(cita.getFecha())
                .horaInicio(cita.getHoraInicio())
                .horaFin(cita.getHoraFin())
                .consultorio(cita.getConsultorio())
                .motivo(cita.getMotivo())
                .estado(cita.getEstado())
                .motivoCancelacion(cita.getMotivoCancelacion())
                .createdAt(cita.getCreatedAt())
                .build();
    }
}
