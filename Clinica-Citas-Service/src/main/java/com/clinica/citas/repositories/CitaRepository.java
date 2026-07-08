package com.clinica.citas.repositories;

import com.clinica.citas.entities.Cita;
import com.clinica.citas.entities.EstadoCita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteIdOrderByFechaDescHoraInicioDesc(Long pacienteId);

    List<Cita> findByMedicoIdAndFechaOrderByHoraInicioAsc(Long medicoId, LocalDate fecha);

    @Query("""
            select c from Cita c
            where c.medicoId = :medicoId
              and c.fecha = :fecha
              and c.estado = :estado
              and c.horaInicio < :horaFin
              and c.horaFin > :horaInicio
            """)
    List<Cita> buscarCruces(
            @Param("medicoId") Long medicoId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estado") EstadoCita estado);
}
