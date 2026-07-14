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
                "explicitMicroservicePrefix", "/api/ms",
                "auth", properties.getAuthServiceUrl() + "/api/auth/**",
                "notificaciones", properties.getNotificacionesServiceUrl() + "/api/notificaciones/**",
                "citas", Map.of(
                        "enabled", properties.isCitasRouteEnabled(),
                        "explicitPath", "/api/ms/citas/**",
                        "target", properties.getCitasServiceUrl() + "/api/citas/**"),
                "atenciones", Map.of(
                        "enabled", properties.isAtencionRouteEnabled(),
                        "explicitPath", "/api/ms/atenciones/**",
                        "target", properties.getAtencionServiceUrl() + "/api/atenciones/**"),
                "caja", Map.of(
                        "enabled", properties.isCajaRouteEnabled(),
                        "explicitPath", "/api/ms/caja/**",
                        "target", properties.getCajaServiceUrl() + "/api/caja/**"),
                "backendFallback", properties.getBackendUrl() + "/api/**"));
    }
}
