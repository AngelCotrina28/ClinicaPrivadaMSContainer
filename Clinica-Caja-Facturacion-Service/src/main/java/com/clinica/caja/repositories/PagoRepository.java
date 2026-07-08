package com.clinica.caja.repositories;

import com.clinica.caja.entities.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByPacienteIdOrderByFechaPagoDesc(Long pacienteId);
}
