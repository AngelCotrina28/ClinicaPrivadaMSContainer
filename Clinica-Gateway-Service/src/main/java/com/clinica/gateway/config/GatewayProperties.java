package com.clinica.gateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GatewayProperties {

    private final String authServiceUrl;
    private final String notificacionesServiceUrl;
    private final String citasServiceUrl;
    private final String atencionServiceUrl;
    private final String cajaServiceUrl;
    private final String backendUrl;
    private final boolean citasRouteEnabled;
    private final boolean atencionRouteEnabled;
    private final boolean cajaRouteEnabled;

    public GatewayProperties(
            @Value("${gateway.auth-service-url}") String authServiceUrl,
            @Value("${gateway.notificaciones-service-url}") String notificacionesServiceUrl,
            @Value("${gateway.citas-service-url}") String citasServiceUrl,
            @Value("${gateway.atencion-service-url}") String atencionServiceUrl,
            @Value("${gateway.caja-service-url}") String cajaServiceUrl,
            @Value("${gateway.backend-url}") String backendUrl,
            @Value("${gateway.citas-route-enabled}") boolean citasRouteEnabled,
            @Value("${gateway.atencion-route-enabled}") boolean atencionRouteEnabled,
            @Value("${gateway.caja-route-enabled}") boolean cajaRouteEnabled) {
        this.authServiceUrl = normalizar(authServiceUrl);
        this.notificacionesServiceUrl = normalizar(notificacionesServiceUrl);
        this.citasServiceUrl = normalizar(citasServiceUrl);
        this.atencionServiceUrl = normalizar(atencionServiceUrl);
        this.cajaServiceUrl = normalizar(cajaServiceUrl);
        this.backendUrl = normalizar(backendUrl);
        this.citasRouteEnabled = citasRouteEnabled;
        this.atencionRouteEnabled = atencionRouteEnabled;
        this.cajaRouteEnabled = cajaRouteEnabled;
    }

    private String normalizar(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
