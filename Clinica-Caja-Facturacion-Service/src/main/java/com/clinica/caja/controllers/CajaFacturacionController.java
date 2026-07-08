package com.clinica.caja.controllers;

import com.clinica.caja.dtos.CrearDeudaRequestDTO;
import com.clinica.caja.dtos.RegistrarPagoRequestDTO;
import com.clinica.caja.entities.EstadoDeuda;
import com.clinica.caja.services.CajaFacturacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
public class CajaFacturacionController {

    private final CajaFacturacionService cajaService;

    @PostMapping("/deudas")
    public ResponseEntity<?> crearDeuda(@Valid @RequestBody CrearDeudaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaService.crearDeuda(request));
    }

    @GetMapping("/deudas")
    public ResponseEntity<?> listarDeudas(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) EstadoDeuda estado) {
        return ResponseEntity.ok(cajaService.listarDeudas(pacienteId, estado));
    }

    @GetMapping("/deudas/{id}")
    public ResponseEntity<?> obtenerDeuda(@PathVariable Long id) {
        return ResponseEntity.ok(cajaService.obtenerDeuda(id));
    }

    @PostMapping("/pagos")
    public ResponseEntity<?> registrarPago(@Valid @RequestBody RegistrarPagoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaService.registrarPago(request));
    }

    @GetMapping("/pagos/{id}")
    public ResponseEntity<?> obtenerPago(@PathVariable Long id) {
        return ResponseEntity.ok(cajaService.obtenerPago(id));
    }

    @GetMapping("/pagos/paciente/{pacienteId}")
    public ResponseEntity<?> listarPagosPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(cajaService.listarPagosPorPaciente(pacienteId));
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> resumen() {
        return ResponseEntity.ok(cajaService.resumen());
    }
}
