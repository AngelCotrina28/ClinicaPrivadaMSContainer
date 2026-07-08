package com.clinica.notificaciones.controllers;

import com.clinica.notificaciones.dtos.CrearNotificacionRequestDTO;
import com.clinica.notificaciones.services.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CrearNotificacionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<?> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/destinatario/{destinatarioTipo}/{destinatarioId}")
    public ResponseEntity<?> listarPorDestinatario(
            @PathVariable String destinatarioTipo,
            @PathVariable Long destinatarioId) {
        return ResponseEntity.ok(notificacionService.listarPorDestinatario(destinatarioTipo, destinatarioId));
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> resumen() {
        return ResponseEntity.ok(notificacionService.resumen());
    }

    @PatchMapping("/{id}/leida")
    public ResponseEntity<?> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarLeida(id));
    }
}
