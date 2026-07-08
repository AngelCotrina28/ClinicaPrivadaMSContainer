package com.clinica.caja.dtos;

import java.math.BigDecimal;

public record ResumenCajaDTO(
        long deudasPendientes,
        long deudasPagadas,
        BigDecimal totalPendiente,
        BigDecimal totalPagado) {
}
