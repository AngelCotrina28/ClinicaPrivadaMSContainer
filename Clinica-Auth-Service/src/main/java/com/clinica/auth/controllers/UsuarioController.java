package com.clinica.auth.controllers;

import com.clinica.auth.dtos.CrearUsuarioRequestDTO;
import com.clinica.auth.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CrearUsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }
}
