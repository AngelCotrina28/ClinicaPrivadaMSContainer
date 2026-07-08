package com.clinica.citas.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "citas")
@CompoundIndex(name = "idx_citas_medico_fecha", def = "{'medicoId': 1, 'fecha': 1}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    private String id;

    @Indexed
    private Long pacienteId;

    private Long medicoId;

    private Long especialidadId;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private String consultorio;

    private String motivo;

    @Indexed
    @Builder.Default
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    private String motivoCancelacion;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
