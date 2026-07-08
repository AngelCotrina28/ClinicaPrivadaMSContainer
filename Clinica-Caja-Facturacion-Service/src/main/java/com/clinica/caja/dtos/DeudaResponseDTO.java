package com.clinica.caja.dtos;

import com.clinica.caja.entities.Deuda;
import com.clinica.caja.entities.EstadoDeuda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeudaResponseDTO {
    private Long id;
    private Long pacienteId;
    private String concepto;
    private String referenciaTipo;
    private String referenciaId;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal saldo;
    private EstadoDeuda estado;
    private LocalDateTime createdAt;

    public static DeudaResponseDTO fromEntity(Deuda deuda) {
        BigDecimal pagado = deuda.getMontoPagado() == null ? BigDecimal.ZERO : deuda.getMontoPagado();
        return DeudaResponseDTO.builder()
                .id(deuda.getId())
                .pacienteId(deuda.getPacienteId())
                .concepto(deuda.getConcepto())
                .referenciaTipo(deuda.getReferenciaTipo())
                .referenciaId(deuda.getReferenciaId())
                .montoTotal(deuda.getMontoTotal())
                .montoPagado(pagado)
                .saldo(deuda.getMontoTotal().subtract(pagado))
                .estado(deuda.getEstado())
                .createdAt(deuda.getCreatedAt())
                .build();
    }
}
