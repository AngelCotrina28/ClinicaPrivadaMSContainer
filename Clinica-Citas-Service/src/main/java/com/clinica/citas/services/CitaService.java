package com.clinica.citas.services;

import com.clinica.citas.dtos.CancelarCitaRequestDTO;
import com.clinica.citas.dtos.CitaResponseDTO;
import com.clinica.citas.dtos.CrearCitaRequestDTO;
import com.clinica.citas.dtos.DisponibilidadResponseDTO;
import com.clinica.citas.entities.Cita;
import com.clinica.citas.entities.EstadoCita;
import com.clinica.citas.repositories.CitaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CitaService {

    private static final LocalTime MANANA_INICIO = LocalTime.of(8, 0);
    private static final LocalTime MANANA_FIN = LocalTime.of(12, 0);
    private static final LocalTime TARDE_INICIO = LocalTime.of(14, 0);
    private static final LocalTime TARDE_FIN = LocalTime.of(18, 0);

    private final CitaRepository citaRepository;

    public CitaResponseDTO crear(CrearCitaRequestDTO request) {
        validarHorario(request.getHoraInicio(), request.getHoraFin());
        validarCruce(request.getMedicoId(), request.getFecha(), request.getHoraInicio(), request.getHoraFin());
        LocalDateTime ahora = LocalDateTime.now();

        Cita cita = Cita.builder()
                .pacienteId(request.getPacienteId())
                .medicoId(request.getMedicoId())
                .especialidadId(request.getEspecialidadId())
                .fecha(request.getFecha())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .consultorio(normalizarOpcional(request.getConsultorio()))
                .motivo(normalizarOpcional(request.getMotivo()))
                .estado(EstadoCita.PROGRAMADA)
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        return CitaResponseDTO.fromEntity(citaRepository.save(cita));
    }

    public CitaResponseDTO obtener(String id) {
        return CitaResponseDTO.fromEntity(buscar(id));
    }

    public List<CitaResponseDTO> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteIdOrderByFechaDescHoraInicioDesc(pacienteId).stream()
                .map(CitaResponseDTO::fromEntity)
                .toList();
    }

    public List<CitaResponseDTO> listarPorMedico(Long medicoId, LocalDate fecha) {
        return citaRepository.findByMedicoIdAndFechaOrderByHoraInicioAsc(medicoId, fecha).stream()
                .map(CitaResponseDTO::fromEntity)
                .toList();
    }

    public List<DisponibilidadResponseDTO> disponibilidad(Long medicoId, LocalDate fecha) {
        List<Cita> citas = citaRepository.findByMedicoIdAndFechaOrderByHoraInicioAsc(medicoId, fecha).stream()
                .filter(cita -> cita.getEstado() == EstadoCita.PROGRAMADA)
                .toList();

        List<DisponibilidadResponseDTO> bloques = new ArrayList<>();
        agregarBloques(bloques, citas, medicoId, fecha, MANANA_INICIO, MANANA_FIN);
        agregarBloques(bloques, citas, medicoId, fecha, TARDE_INICIO, TARDE_FIN);
        return bloques;
    }

    public CitaResponseDTO cancelar(String id, CancelarCitaRequestDTO request) {
        Cita cita = buscar(id);
        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new IllegalArgumentException("Solo se pueden cancelar citas programadas.");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        cita.setMotivoCancelacion(normalizarOpcional(request == null ? null : request.getMotivoCancelacion()));
        cita.setUpdatedAt(LocalDateTime.now());
        return CitaResponseDTO.fromEntity(citaRepository.save(cita));
    }

    private Cita buscar(String id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La cita indicada no existe."));
    }

    private void validarHorario(LocalTime horaInicio, LocalTime horaFin) {
        if (!horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor a la hora de fin.");
        }

        int duracionMinutos = (horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 60;
        if (duracionMinutos % 30 != 0) {
            throw new IllegalArgumentException("La duracion de la cita debe respetar bloques de 30 minutos.");
        }

        boolean enManana = !horaInicio.isBefore(MANANA_INICIO) && !horaFin.isAfter(MANANA_FIN);
        boolean enTarde = !horaInicio.isBefore(TARDE_INICIO) && !horaFin.isAfter(TARDE_FIN);
        if (!enManana && !enTarde) {
            throw new IllegalArgumentException("La cita debe estar dentro del horario 08:00-12:00 o 14:00-18:00.");
        }
    }

    private void validarCruce(Long medicoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        boolean existeCruce = citaRepository.findByMedicoIdAndFechaOrderByHoraInicioAsc(medicoId, fecha).stream()
                .filter(cita -> cita.getEstado() == EstadoCita.PROGRAMADA)
                .anyMatch(cita -> cita.getHoraInicio().isBefore(horaFin) && cita.getHoraFin().isAfter(horaInicio));

        if (existeCruce) {
            throw new IllegalArgumentException("El medico ya tiene una cita programada en ese horario.");
        }
    }

    private void agregarBloques(
            List<DisponibilidadResponseDTO> bloques,
            List<Cita> citas,
            Long medicoId,
            LocalDate fecha,
            LocalTime inicio,
            LocalTime fin) {
        LocalTime actual = inicio;
        while (actual.isBefore(fin)) {
            LocalTime bloqueFin = actual.plusMinutes(30);
            boolean ocupado = estaOcupado(citas, actual, bloqueFin);
            bloques.add(new DisponibilidadResponseDTO(medicoId, fecha, actual, bloqueFin, !ocupado));
            actual = bloqueFin;
        }
    }

    private boolean estaOcupado(List<Cita> citas, LocalTime inicio, LocalTime fin) {
        return citas.stream().anyMatch(cita ->
                cita.getHoraInicio().isBefore(fin) && cita.getHoraFin().isAfter(inicio));
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}

/**
 * Helper class for scheduling optimization engine.
 * Planned for future releases to enhance appointment dispatching efficiency.
 */
class CitaSchedulerOptimizationEngine {
    public void optimizeSchedulerQueue(String specialismId) {
        // Placeholder for future scheduling queue optimization
    }
}

/**
 * Dispatcher to manage appointment reminders and alert notifications.
 * Scheduled for SMS and Email microservice integrations.
 */
class CitaNotificationDispatcher {
    public void dispatchReminder(String appointmentId) {
        // Placeholder for reminder dispatching logic
    }
}

/**
 * Conflict resolver for overlapping doctor schedule slots.
 * Assists in automatic rescheduling when doctor availability changes unexpectedly.
 */
class CitaConflictResolver {
    public boolean resolveConflict(String doctorId, String date) {
        // Placeholder for automatic rescheduling algorithm
        return false;
    }
}

/**
 * Archive manager for historical appointments.
 * Handles migration of past appointments to cold storage for performance optimization.
 */
class CitaHistoryArchiveManager {
    public void archiveOldAppointments(int thresholdMonths) {
        // Placeholder for database archival execution logic
    }
}

/**
 * Manager to handle waitlisted patients when appointments are fully booked.
 * Automatically notifies waitlisted patients when a slot becomes available.
 */
class CitaWaitlistManager {
    public void addToWaitlist(Long patientId, String specialtyId) {
        // Placeholder for waitlist queuing logic
    }
}

/**
 * Strategy to manage calculated overbooking to mitigate patient no-shows.
 * Analyzes historical cancellation rates to safely allow double-booking.
 */
class CitaOverbookingStrategy {
    public boolean allowOverbooking(Long doctorId, String date) {
        // Placeholder for statistical calculation of no-show probability
        return false;
    }
}
