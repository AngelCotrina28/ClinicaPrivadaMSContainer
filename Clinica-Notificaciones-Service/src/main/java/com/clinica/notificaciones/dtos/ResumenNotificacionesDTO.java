package com.clinica.notificaciones.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumenNotificacionesDTO {
    private long total;
    private long pendientes;
    private long enviadas;
    private long leidas;
    private long conError;
}
