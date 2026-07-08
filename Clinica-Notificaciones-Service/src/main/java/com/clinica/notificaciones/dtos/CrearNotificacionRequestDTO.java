package com.clinica.notificaciones.dtos;

import com.clinica.notificaciones.entities.CanalNotificacion;
import com.clinica.notificaciones.entities.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CrearNotificacionRequestDTO {

    @NotBlank(message = "El tipo de destinatario es obligatorio")
    @Size(max = 40, message = "El tipo de destinatario no debe superar 40 caracteres")
    private String destinatarioTipo;

    @NotNull(message = "El id del destinatario es obligatorio")
    private Long destinatarioId;

    @NotNull(message = "El tipo de notificacion es obligatorio")
    private TipoNotificacion tipo;

    @NotNull(message = "El canal es obligatorio")
    private CanalNotificacion canal;

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 140, message = "El titulo no debe superar 140 caracteres")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @Size(max = 50, message = "El tipo de referencia no debe superar 50 caracteres")
    private String referenciaTipo;

    private Long referenciaId;
    private LocalDateTime fechaProgramada;
}
