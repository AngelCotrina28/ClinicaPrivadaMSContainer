package com.clinica.auth.controllers;

import com.clinica.auth.dtos.AuthLoginRequestDTO;
import com.clinica.auth.services.AuthService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequestDTO request) {
        logger.info("Intento de login para el usuario: {}", request.getUsername());

        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", e.getMessage()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "No autenticado."));
        }
        return ResponseEntity.ok(authService.obtenerPerfil(principal.getName()));
    }
}
