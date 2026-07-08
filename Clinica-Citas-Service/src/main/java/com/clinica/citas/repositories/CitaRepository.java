package com.clinica.citas.repositories;

import com.clinica.citas.entities.Cita;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CitaRepository extends MongoRepository<Cita, String> {

    List<Cita> findByPacienteIdOrderByFechaDescHoraInicioDesc(Long pacienteId);

    List<Cita> findByMedicoIdAndFechaOrderByHoraInicioAsc(Long medicoId, LocalDate fecha);
}
