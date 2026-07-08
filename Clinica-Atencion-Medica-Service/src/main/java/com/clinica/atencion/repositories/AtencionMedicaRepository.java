package com.clinica.atencion.repositories;

import com.clinica.atencion.entities.AtencionMedica;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtencionMedicaRepository extends JpaRepository<AtencionMedica, Long> {

    List<AtencionMedica> findByHistoriaClinicaIdOrderByFechaAtencionDesc(Long historiaClinicaId);

    List<AtencionMedica> findByPacienteIdOrderByFechaAtencionDesc(Long pacienteId);

    List<AtencionMedica> findByMedicoIdOrderByFechaAtencionDesc(Long medicoId);
}
