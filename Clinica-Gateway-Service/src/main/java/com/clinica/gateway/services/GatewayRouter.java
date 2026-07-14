package com.clinica.gateway.services;

import com.clinica.gateway.config.GatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayRouter {

    private static final String MICROSERVICE_PREFIX = "/api/ms";

    private final GatewayProperties properties;

    public String resolverDestino(String path) {
        String microservicePath = removerPrefijoMicroservicio(path);
        if (microservicePath != null) {
            return resolverMicroservicioExplicito(microservicePath);
        }

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

    private String resolverMicroservicioExplicito(String path) {
        if (coincidePrefijo(path, "/api/auth")) {
            return properties.getAuthServiceUrl() + path;
        }
        if (coincidePrefijo(path, "/api/notificaciones")) {
            return properties.getNotificacionesServiceUrl() + path;
        }
        if (coincidePrefijo(path, "/api/citas")) {
            return properties.getCitasServiceUrl() + path;
        }
        if (coincidePrefijo(path, "/api/atenciones")) {
            return properties.getAtencionServiceUrl() + path;
        }
        if (coincidePrefijo(path, "/api/caja")) {
            return properties.getCajaServiceUrl() + path;
        }
        return properties.getBackendUrl() + path;
    }

    private String removerPrefijoMicroservicio(String path) {
        if (!coincidePrefijo(path, MICROSERVICE_PREFIX)) {
            return null;
        }
        return "/api" + path.substring(MICROSERVICE_PREFIX.length());
    }

    private boolean coincidePrefijo(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}
