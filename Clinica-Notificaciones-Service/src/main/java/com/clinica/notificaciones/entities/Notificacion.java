package com.clinica.notificaciones.entities;

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
        name = "notificaciones",
        indexes = {
                @Index(name = "idx_notif_destinatario", columnList = "destinatario_tipo,destinatario_id"),
                @Index(name = "idx_notif_estado", columnList = "estado"),
                @Index(name = "idx_notif_referencia", columnList = "referencia_tipo,referencia_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destinatario_tipo", nullable = false, length = 40)
    private String destinatarioTipo;

    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoNotificacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(nullable = false, length = 140)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
