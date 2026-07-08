package com.clinica.atencion.controllers;

import com.clinica.atencion.dtos.CerrarAtencionRequestDTO;
import com.clinica.atencion.dtos.CrearAtencionRequestDTO;
import com.clinica.atencion.services.AtencionMedicaService;
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
@RequestMapping("/api/atenciones")
@RequiredArgsConstructor
public class AtencionMedicaController {

    private final AtencionMedicaService atencionService;

    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody CrearAtencionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atencionService.registrar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(atencionService.obtener(id));
    }

    @GetMapping("/historia/{historiaClinicaId}")
    public ResponseEntity<?> listarPorHistoria(@PathVariable Long historiaClinicaId) {
        return ResponseEntity.ok(atencionService.listarPorHistoria(historiaClinicaId));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(atencionService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> listarPorMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(atencionService.listarPorMedico(medicoId));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrar(
            @PathVariable Long id,
            @Valid @RequestBody CerrarAtencionRequestDTO request) {
        return ResponseEntity.ok(atencionService.cerrar(id, request));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id) {
        return ResponseEntity.ok(atencionService.anular(id));
    }
}
