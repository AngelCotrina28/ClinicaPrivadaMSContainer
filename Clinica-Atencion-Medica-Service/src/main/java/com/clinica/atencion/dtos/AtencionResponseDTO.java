package com.clinica.atencion.dtos;

import com.clinica.atencion.entities.AtencionMedica;
import com.clinica.atencion.entities.EstadoAtencion;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AtencionResponseDTO {
    private Long id;
    private Long historiaClinicaId;
    private Long pacienteId;
    private Long medicoId;
    private String citaId;
    private LocalDateTime fechaAtencion;
    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;
    private String indicacionesReceta;
    private EstadoAtencion estado;
    private LocalDateTime createdAt;

    public static AtencionResponseDTO fromEntity(AtencionMedica atencion) {
        return AtencionResponseDTO.builder()
                .id(atencion.getId())
                .historiaClinicaId(atencion.getHistoriaClinicaId())
                .pacienteId(atencion.getPacienteId())
                .medicoId(atencion.getMedicoId())
                .citaId(atencion.getCitaId())
                .fechaAtencion(atencion.getFechaAtencion())
                .motivoConsulta(atencion.getMotivoConsulta())
                .diagnostico(atencion.getDiagnostico())
                .tratamiento(atencion.getTratamiento())
                .indicacionesReceta(atencion.getIndicacionesReceta())
                .estado(atencion.getEstado())
                .createdAt(atencion.getCreatedAt())
                .build();
    }
}
