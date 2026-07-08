package com.clinica.notificaciones.dtos;

import com.clinica.notificaciones.entities.CanalNotificacion;
import com.clinica.notificaciones.entities.EstadoNotificacion;
import com.clinica.notificaciones.entities.Notificacion;
import com.clinica.notificaciones.entities.TipoNotificacion;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionResponseDTO {
    private Long id;
    private String destinatarioTipo;
    private Long destinatarioId;
    private TipoNotificacion tipo;
    private CanalNotificacion canal;
    private EstadoNotificacion estado;
    private String titulo;
    private String mensaje;
    private String referenciaTipo;
    private Long referenciaId;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaLectura;
    private LocalDateTime createdAt;

    public static NotificacionResponseDTO fromEntity(Notificacion notificacion) {
        return NotificacionResponseDTO.builder()
                .id(notificacion.getId())
                .destinatarioTipo(notificacion.getDestinatarioTipo())
                .destinatarioId(notificacion.getDestinatarioId())
                .tipo(notificacion.getTipo())
                .canal(notificacion.getCanal())
                .estado(notificacion.getEstado())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .referenciaTipo(notificacion.getReferenciaTipo())
                .referenciaId(notificacion.getReferenciaId())
                .fechaProgramada(notificacion.getFechaProgramada())
                .fechaEnvio(notificacion.getFechaEnvio())
                .fechaLectura(notificacion.getFechaLectura())
                .createdAt(notificacion.getCreatedAt())
                .build();
    }
}
