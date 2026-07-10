package com.clinica.gateway.controllers;

import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler({ConnectException.class, HttpConnectTimeoutException.class})
    public ResponseEntity<?> handleConnection(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "mensaje", "El servicio destino no esta disponible.",
                "detalle", ex.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIo(IOException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "mensaje", "No se pudo reenviar la peticion al servicio destino.",
                "detalle", ex.getMessage()));
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<?> handleInterrupted(InterruptedException ex) {
        Thread.currentThread().interrupt();
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(Map.of(
                "mensaje", "La peticion al servicio destino fue interrumpida."));
    }
}
