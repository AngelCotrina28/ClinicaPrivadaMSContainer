package com.clinica.caja.repositories;

import com.clinica.caja.entities.Deuda;
import com.clinica.caja.entities.EstadoDeuda;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeudaRepository extends JpaRepository<Deuda, Long> {

    List<Deuda> findByPacienteIdOrderByCreatedAtDesc(Long pacienteId);

    List<Deuda> findByEstadoOrderByCreatedAtDesc(EstadoDeuda estado);

    List<Deuda> findByPacienteIdAndEstadoOrderByCreatedAtDesc(Long pacienteId, EstadoDeuda estado);

    long countByEstado(EstadoDeuda estado);
}
