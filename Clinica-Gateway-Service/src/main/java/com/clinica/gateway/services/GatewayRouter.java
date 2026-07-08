package com.clinica.gateway.services;

import com.clinica.gateway.config.GatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayRouter {

    private final GatewayProperties properties;

    public String resolverDestino(String path) {
        if (path.startsWith("/api/auth")) {
            return properties.getAuthServiceUrl() + path;
        }

        if (path.startsWith("/api/notificaciones")) {
            return properties.getNotificacionesServiceUrl() + path;
        }

        if (path.startsWith("/api/citas") && properties.isCitasRouteEnabled()) {
            return properties.getCitasServiceUrl() + path;
        }

        if (path.startsWith("/api/atenciones") && properties.isAtencionRouteEnabled()) {
            return properties.getAtencionServiceUrl() + path;
        }

        if (path.startsWith("/api/caja") && properties.isCajaRouteEnabled()) {
            return properties.getCajaServiceUrl() + path;
        }

        return properties.getBackendUrl() + path;
    }
}
