package com.clinica.notificaciones.services;

import com.clinica.notificaciones.dtos.CrearNotificacionRequestDTO;
import com.clinica.notificaciones.dtos.NotificacionResponseDTO;
import com.clinica.notificaciones.dtos.ResumenNotificacionesDTO;
import com.clinica.notificaciones.entities.EstadoNotificacion;
import com.clinica.notificaciones.entities.Notificacion;
import com.clinica.notificaciones.repositories.NotificacionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Transactional
    public NotificacionResponseDTO crear(CrearNotificacionRequestDTO request) {
        Notificacion notificacion = Notificacion.builder()
                .destinatarioTipo(request.getDestinatarioTipo().trim().toUpperCase())
                .destinatarioId(request.getDestinatarioId())
                .tipo(request.getTipo())
                .canal(request.getCanal())
                .estado(EstadoNotificacion.ENVIADA)
                .titulo(request.getTitulo().trim())
                .mensaje(request.getMensaje().trim())
                .referenciaTipo(normalizarOpcional(request.getReferenciaTipo()))
                .referenciaId(request.getReferenciaId())
                .fechaProgramada(request.getFechaProgramada())
                .fechaEnvio(LocalDateTime.now())
                .build();

        return NotificacionResponseDTO.fromEntity(notificacionRepository.save(notificacion));
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarTodas() {
        return notificacionRepository.findAll().stream()
                .map(NotificacionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorDestinatario(String destinatarioTipo, Long destinatarioId) {
        return notificacionRepository
                .findByDestinatarioTipoIgnoreCaseAndDestinatarioIdOrderByCreatedAtDesc(destinatarioTipo, destinatarioId)
                .stream()
                .map(NotificacionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumenNotificacionesDTO resumen() {
        List<Notificacion> notificaciones = notificacionRepository.findAll();
        long pendientes = contar(notificaciones, EstadoNotificacion.PENDIENTE);
        long enviadas = contar(notificaciones, EstadoNotificacion.ENVIADA);
        long leidas = contar(notificaciones, EstadoNotificacion.LEIDA);
        long conError = contar(notificaciones, EstadoNotificacion.ERROR);
        return new ResumenNotificacionesDTO(notificaciones.size(), pendientes, enviadas, leidas, conError);
    }

    @Transactional
    public NotificacionResponseDTO marcarLeida(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La notificacion indicada no existe."));

        notificacion.setEstado(EstadoNotificacion.LEIDA);
        notificacion.setFechaLectura(LocalDateTime.now());
        return NotificacionResponseDTO.fromEntity(notificacionRepository.save(notificacion));
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim().toUpperCase();
    }

    private long contar(List<Notificacion> notificaciones, EstadoNotificacion estado) {
        return notificaciones.stream()
                .filter(notificacion -> notificacion.getEstado() == estado)
                .count();
    }
}

/**
 * Elaborated parser for checking message syntax requirements.
 * Validates lengths and characteristics of messages according to target canal (SMS, EMAIL).
 */
class NotificacionValidationEngine {

    public static class SyntaxReport {
        private final boolean valid;
        private final String failureMessage;

        public SyntaxReport(boolean valid, String failureMessage) {
            this.valid = valid;
            this.failureMessage = failureMessage;
        }

        public boolean isValid() { return valid; }
        public String getFailureMessage() { return failureMessage; }
    }

    public SyntaxReport validateMessagePayload(String canal, String body) {
        if (body == null || body.trim().isEmpty()) {
            return new SyntaxReport(false, "PAYLOAD_EMPTY");
        }

        if ("SMS".equalsIgnoreCase(canal)) {
            if (body.length() > 160) {
                return new SyntaxReport(false, "SMS_EXCEEDS_160_CHARACTERS_LIMIT");
            }
            return new SyntaxReport(true, "SMS_VALID");
        }

        if ("EMAIL".equalsIgnoreCase(canal)) {
            if (!body.contains("<html>") && !body.contains("<body>")) {
                // Warning, email should ideally be HTML
                return new SyntaxReport(true, "EMAIL_PLAINTEXT_WARNING");
            }
            return new SyntaxReport(true, "EMAIL_VALID");
        }

        return new SyntaxReport(true, "CANAL_UNSUPPORTED_BUT_ACCEPTED");
    }
}
