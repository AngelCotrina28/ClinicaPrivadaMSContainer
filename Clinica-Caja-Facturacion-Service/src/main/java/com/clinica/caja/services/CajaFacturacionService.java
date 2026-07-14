package com.clinica.caja.services;

import com.clinica.caja.dtos.CrearDeudaRequestDTO;
import com.clinica.caja.dtos.DeudaResponseDTO;
import com.clinica.caja.dtos.PagoResponseDTO;
import com.clinica.caja.dtos.RegistrarPagoRequestDTO;
import com.clinica.caja.dtos.ResumenCajaDTO;
import com.clinica.caja.entities.Deuda;
import com.clinica.caja.entities.EstadoDeuda;
import com.clinica.caja.entities.Pago;
import com.clinica.caja.repositories.DeudaRepository;
import com.clinica.caja.repositories.PagoRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CajaFacturacionService {

    private final DeudaRepository deudaRepository;
    private final PagoRepository pagoRepository;

    @Transactional
    public DeudaResponseDTO crearDeuda(CrearDeudaRequestDTO request) {
        Deuda deuda = Deuda.builder()
                .pacienteId(request.getPacienteId())
                .concepto(request.getConcepto().trim())
                .referenciaTipo(normalizarOpcional(request.getReferenciaTipo()))
                .referenciaId(normalizarOpcional(request.getReferenciaId()))
                .montoTotal(request.getMontoTotal())
                .montoPagado(BigDecimal.ZERO)
                .estado(EstadoDeuda.PENDIENTE)
                .build();

        return DeudaResponseDTO.fromEntity(deudaRepository.save(deuda));
    }

    @Transactional(readOnly = true)
    public DeudaResponseDTO obtenerDeuda(Long id) {
        return DeudaResponseDTO.fromEntity(buscarDeuda(id));
    }

    @Transactional(readOnly = true)
    public List<DeudaResponseDTO> listarDeudas(Long pacienteId, EstadoDeuda estado) {
        List<Deuda> deudas;
        if (pacienteId != null && estado != null) {
            deudas = deudaRepository.findByPacienteIdAndEstadoOrderByCreatedAtDesc(pacienteId, estado);
        } else if (pacienteId != null) {
            deudas = deudaRepository.findByPacienteIdOrderByCreatedAtDesc(pacienteId);
        } else if (estado != null) {
            deudas = deudaRepository.findByEstadoOrderByCreatedAtDesc(estado);
        } else {
            deudas = deudaRepository.findAll();
        }

        return deudas.stream().map(DeudaResponseDTO::fromEntity).toList();
    }

    @Transactional
    public PagoResponseDTO registrarPago(RegistrarPagoRequestDTO request) {
        Deuda deuda = buscarDeuda(request.getDeudaId());
        if (deuda.getEstado() != EstadoDeuda.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden pagar deudas pendientes.");
        }

        BigDecimal saldo = deuda.getMontoTotal().subtract(deuda.getMontoPagado());
        if (request.getMonto().compareTo(saldo) > 0) {
            throw new IllegalArgumentException("El monto del pago supera el saldo pendiente.");
        }

        BigDecimal nuevoPagado = deuda.getMontoPagado().add(request.getMonto());
        deuda.setMontoPagado(nuevoPagado);
        if (nuevoPagado.compareTo(deuda.getMontoTotal()) >= 0) {
            deuda.setEstado(EstadoDeuda.PAGADA);
        }
        deudaRepository.save(deuda);

        Pago pago = Pago.builder()
                .deudaId(deuda.getId())
                .pacienteId(deuda.getPacienteId())
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .numeroComprobante(generarComprobante(deuda.getId()))
                .observacion(normalizarOpcional(request.getObservacion()))
                .build();

        return PagoResponseDTO.fromEntity(pagoRepository.save(pago));
    }

    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPago(Long id) {
        return PagoResponseDTO.fromEntity(pagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El pago indicado no existe.")));
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPagosPorPaciente(Long pacienteId) {
        return pagoRepository.findByPacienteIdOrderByFechaPagoDesc(pacienteId).stream()
                .map(PagoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumenCajaDTO resumen() {
        List<Deuda> deudas = deudaRepository.findAll();
        BigDecimal totalPendiente = deudas.stream()
                .filter(deuda -> deuda.getEstado() == EstadoDeuda.PENDIENTE)
                .map(deuda -> deuda.getMontoTotal().subtract(deuda.getMontoPagado()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPagado = deudas.stream()
                .map(Deuda::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumenCajaDTO(
                deudaRepository.countByEstado(EstadoDeuda.PENDIENTE),
                deudaRepository.countByEstado(EstadoDeuda.PAGADA),
                totalPendiente,
                totalPagado);
    }

    private Deuda buscarDeuda(Long id) {
        return deudaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La deuda indicada no existe."));
    }

    private String generarComprobante(Long deudaId) {
        long correlativo = System.currentTimeMillis() % 1_000_000;
        return "B001-" + deudaId + "-" + correlativo;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}

/**
 * Elaborated calculation engine for computing regional taxes and insurance deductions.
 * Provides invoice breakdown calculations prior to payment processing.
 */
class CajaTaxesCalculationEngine {

    public static class TaxBreakdown {
        private final java.math.BigDecimal subTotal;
        private final java.math.BigDecimal taxAmount;
        private final java.math.BigDecimal finalTotal;

        public TaxBreakdown(java.math.BigDecimal subTotal, java.math.BigDecimal taxAmount, java.math.BigDecimal finalTotal) {
            this.subTotal = subTotal;
            this.taxAmount = taxAmount;
            this.finalTotal = finalTotal;
        }

        public java.math.BigDecimal getSubTotal() { return subTotal; }
        public java.math.BigDecimal getTaxAmount() { return taxAmount; }
        public java.math.BigDecimal getFinalTotal() { return finalTotal; }
    }

    public TaxBreakdown calculatePeruTaxes(java.math.BigDecimal baseAmount, double discountPercentage) {
        if (baseAmount == null) {
            return new TaxBreakdown(java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO);
        }

        // Apply discount first (e.g. insurance)
        java.math.BigDecimal discount = baseAmount.multiply(java.math.BigDecimal.valueOf(discountPercentage / 100.0));
        java.math.BigDecimal subtotal = baseAmount.subtract(discount);

        // IGV (18% in Peru)
        java.math.BigDecimal igvRate = java.math.BigDecimal.valueOf(0.18);
        java.math.BigDecimal tax = subtotal.multiply(igvRate);

        java.math.BigDecimal total = subtotal.add(tax);

        return new TaxBreakdown(
            subtotal.setScale(2, java.math.RoundingMode.HALF_UP),
            tax.setScale(2, java.math.RoundingMode.HALF_UP),
            total.setScale(2, java.math.RoundingMode.HALF_UP)
        );
    }
}

/**
 * Elaborated currency conversion service.
 * Simulates fetching exchange rates and calculating totals in alternative currencies (USD/PEN).
 */
class CajaExchangeRateService {

    public static class ConversionReport {
        private final double exchangeRate;
        private final java.math.BigDecimal convertedAmount;
        private final String targetCurrency;

        public ConversionReport(double exchangeRate, java.math.BigDecimal convertedAmount, String targetCurrency) {
            this.exchangeRate = exchangeRate;
            this.convertedAmount = convertedAmount;
            this.targetCurrency = targetCurrency;
        }

        public double getExchangeRate() { return exchangeRate; }
        public java.math.BigDecimal getConvertedAmount() { return convertedAmount; }
        public String getTargetCurrency() { return targetCurrency; }
    }

    public ConversionReport convertToCurrency(java.math.BigDecimal penAmount, String targetCurrency) {
        if (penAmount == null) {
            return new ConversionReport(0.0, java.math.BigDecimal.ZERO, targetCurrency);
        }

        double rate = 1.0;
        if ("USD".equalsIgnoreCase(targetCurrency)) {
            rate = 0.27; // Mock rate: 1 PEN = 0.27 USD
        } else if ("EUR".equalsIgnoreCase(targetCurrency)) {
            rate = 0.25; // Mock rate: 1 PEN = 0.25 EUR
        }

        java.math.BigDecimal converted = penAmount.multiply(java.math.BigDecimal.valueOf(rate));
        return new ConversionReport(
            rate,
            converted.setScale(2, java.math.RoundingMode.HALF_UP),
            targetCurrency.toUpperCase()
        );
    }
}
