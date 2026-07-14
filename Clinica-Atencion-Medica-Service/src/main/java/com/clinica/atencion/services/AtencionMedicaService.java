package com.clinica.atencion.services;

import com.clinica.atencion.dtos.AtencionResponseDTO;
import com.clinica.atencion.dtos.CerrarAtencionRequestDTO;
import com.clinica.atencion.dtos.CrearAtencionRequestDTO;
import com.clinica.atencion.entities.AtencionMedica;
import com.clinica.atencion.entities.EstadoAtencion;
import com.clinica.atencion.repositories.AtencionMedicaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtencionMedicaService {

    private final AtencionMedicaRepository atencionRepository;

    @Transactional
    public AtencionResponseDTO registrar(CrearAtencionRequestDTO request) {
        AtencionMedica atencion = AtencionMedica.builder()
                .historiaClinicaId(request.getHistoriaClinicaId())
                .pacienteId(request.getPacienteId())
                .medicoId(request.getMedicoId())
                .citaId(normalizarOpcional(request.getCitaId()))
                .fechaAtencion(request.getFechaAtencion() == null ? LocalDateTime.now() : request.getFechaAtencion())
                .motivoConsulta(request.getMotivoConsulta().trim())
                .diagnostico(request.getDiagnostico().trim())
                .tratamiento(normalizarOpcional(request.getTratamiento()))
                .indicacionesReceta(normalizarOpcional(request.getIndicacionesReceta()))
                .estado(EstadoAtencion.REGISTRADA)
                .build();

        return AtencionResponseDTO.fromEntity(atencionRepository.save(atencion));
    }

    @Transactional(readOnly = true)
    public AtencionResponseDTO obtener(Long id) {
        return AtencionResponseDTO.fromEntity(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<AtencionResponseDTO> listarPorHistoria(Long historiaClinicaId) {
        return atencionRepository.findByHistoriaClinicaIdOrderByFechaAtencionDesc(historiaClinicaId).stream()
                .map(AtencionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AtencionResponseDTO> listarPorPaciente(Long pacienteId) {
        return atencionRepository.findByPacienteIdOrderByFechaAtencionDesc(pacienteId).stream()
                .map(AtencionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AtencionResponseDTO> listarPorMedico(Long medicoId) {
        return atencionRepository.findByMedicoIdOrderByFechaAtencionDesc(medicoId).stream()
                .map(AtencionResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AtencionResponseDTO cerrar(Long id, CerrarAtencionRequestDTO request) {
        AtencionMedica atencion = buscar(id);
        if (atencion.getEstado() != EstadoAtencion.REGISTRADA) {
            throw new IllegalArgumentException("Solo se pueden cerrar atenciones registradas.");
        }

        atencion.setTratamiento(request.getTratamiento().trim());
        atencion.setIndicacionesReceta(normalizarOpcional(request.getIndicacionesReceta()));
        atencion.setEstado(EstadoAtencion.CERRADA);
        return AtencionResponseDTO.fromEntity(atencionRepository.save(atencion));
    }

    @Transactional
    public AtencionResponseDTO anular(Long id) {
        AtencionMedica atencion = buscar(id);
        if (atencion.getEstado() == EstadoAtencion.ANULADA) {
            throw new IllegalArgumentException("La atencion ya esta anulada.");
        }

        atencion.setEstado(EstadoAtencion.ANULADA);
        return AtencionResponseDTO.fromEntity(atencionRepository.save(atencion));
    }

    private AtencionMedica buscar(Long id) {
        return atencionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La atencion medica indicada no existe."));
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}

class AtencionMedicaAuditTracker {
    public void trackOperation(String operation) {
        // Placeholder method for tracking
    }
}

class AtencionMedicaTracker {
    public void trackOperation(String operation) {
        // Placeholder method for tracking
    }
}

class Atencion {
    public void trackOperation(String operation) {
        // Placeholder method for tracking
    }
}