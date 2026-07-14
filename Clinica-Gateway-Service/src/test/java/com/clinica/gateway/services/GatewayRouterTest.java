package com.clinica.gateway.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.clinica.gateway.config.GatewayProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GatewayRouterTest {

    private GatewayRouter router;

    @BeforeEach
    void setUp() {
        GatewayProperties properties = new GatewayProperties(
                "http://auth:8091/",
                "http://notificaciones:8092/",
                "http://citas:8093/",
                "http://atenciones:8094/",
                "http://caja:8095/",
                "http://backend:8080/",
                false,
                false,
                false);
        router = new GatewayRouter(properties);
    }

    @Test
    void enrutaPrefijosExplicitosAlMicroservicioCorrespondiente() {
        assertEquals("http://citas:8093/api/citas/123", router.resolverDestino("/api/ms/citas/123"));
        assertEquals(
                "http://atenciones:8094/api/atenciones/7/cerrar",
                router.resolverDestino("/api/ms/atenciones/7/cerrar"));
        assertEquals("http://caja:8095/api/caja/deudas", router.resolverDestino("/api/ms/caja/deudas"));
        assertEquals(
                "http://notificaciones:8092/api/notificaciones",
                router.resolverDestino("/api/ms/notificaciones"));
    }

    @Test
    void conservaRutasLegacyEnBackendCuandoLaMigracionEstaDeshabilitada() {
        assertEquals("http://backend:8080/api/citas/medicos", router.resolverDestino("/api/citas/medicos"));
        assertEquals(
                "http://backend:8080/api/atenciones/registro",
                router.resolverDestino("/api/atenciones/registro"));
        assertEquals(
                "http://backend:8080/api/caja/apertura-actual",
                router.resolverDestino("/api/caja/apertura-actual"));
    }

    @Test
    void mantieneAuthComoRutaPrincipalDelFrontend() {
        assertEquals("http://auth:8091/api/auth/login", router.resolverDestino("/api/auth/login"));
    }
}
