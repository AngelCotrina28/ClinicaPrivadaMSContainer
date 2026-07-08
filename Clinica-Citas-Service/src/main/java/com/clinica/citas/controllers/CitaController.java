package com.clinica.citas.controllers;

import com.clinica.citas.dtos.CancelarCitaRequestDTO;
import com.clinica.citas.dtos.CrearCitaRequestDTO;
import com.clinica.citas.services.CitaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CrearCitaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtener(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> listarPorMedico(
            @PathVariable Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.listarPorMedico(medicoId, fecha));
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.disponibilidad(medicoId, fecha));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid CancelarCitaRequestDTO request) {
        return ResponseEntity.ok(citaService.cancelar(id, request));
    }
}
