package com.clinica.atencion.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "atenciones_medicas",
        indexes = {
                @Index(name = "idx_atenciones_historia", columnList = "historia_clinica_id"),
                @Index(name = "idx_atenciones_paciente", columnList = "paciente_id"),
                @Index(name = "idx_atenciones_medico", columnList = "medico_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtencionMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "historia_clinica_id", nullable = false)
    private Long historiaClinicaId;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @Column(name = "cita_id", length = 80)
    private String citaId;

    @Column(name = "fecha_atencion", nullable = false)
    private LocalDateTime fechaAtencion;

    @Column(name = "motivo_consulta", nullable = false, length = 300)
    private String motivoConsulta;

    @Column(nullable = false, length = 500)
    private String diagnostico;

    @Column(length = 700)
    private String tratamiento;

    @Column(name = "indicaciones_receta", length = 700)
    private String indicacionesReceta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoAtencion estado = EstadoAtencion.REGISTRADA;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
