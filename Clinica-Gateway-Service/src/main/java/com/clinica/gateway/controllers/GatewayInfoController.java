package com.clinica.gateway.controllers;

import com.clinica.gateway.config.GatewayProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GatewayInfoController {

    private final GatewayProperties properties;

    @GetMapping("/gateway/routes")
    public ResponseEntity<?> routes() {
        return ResponseEntity.ok(Map.of(
                "auth", properties.getAuthServiceUrl() + "/api/auth/**",
                "notificaciones", properties.getNotificacionesServiceUrl() + "/api/notificaciones/**",
                "citas", Map.of(
                        "enabled", properties.isCitasRouteEnabled(),
                        "target", properties.getCitasServiceUrl() + "/api/citas/**"),
                "backendFallback", properties.getBackendUrl() + "/api/**"));
    }
}
