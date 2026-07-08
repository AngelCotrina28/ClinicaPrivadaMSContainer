package com.clinica.notificaciones.repositories;

import com.clinica.notificaciones.entities.EstadoNotificacion;
import com.clinica.notificaciones.entities.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatarioTipoIgnoreCaseAndDestinatarioIdOrderByCreatedAtDesc(
            String destinatarioTipo,
            Long destinatarioId);

    List<Notificacion> findByEstadoOrderByCreatedAtAsc(EstadoNotificacion estado);
}
