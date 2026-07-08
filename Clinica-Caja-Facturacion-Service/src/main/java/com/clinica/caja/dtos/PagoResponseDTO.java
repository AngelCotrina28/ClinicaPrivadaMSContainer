package com.clinica.caja.dtos;

import com.clinica.caja.entities.MetodoPago;
import com.clinica.caja.entities.Pago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagoResponseDTO {
    private Long id;
    private Long deudaId;
    private Long pacienteId;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private String numeroComprobante;
    private String observacion;
    private LocalDateTime fechaPago;

    public static PagoResponseDTO fromEntity(Pago pago) {
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .deudaId(pago.getDeudaId())
                .pacienteId(pago.getPacienteId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .numeroComprobante(pago.getNumeroComprobante())
                .observacion(pago.getObservacion())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}
