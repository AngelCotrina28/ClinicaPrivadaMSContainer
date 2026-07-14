package com.clinica.gateway.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class CorsConfigTest {

    @Test
    void permiteElFrontendProductivoAunqueLaListaExternaSoloTengaLocalhost() {
        CorsConfigurationSource source = new CorsConfig().corsConfigurationSource(
                "http://localhost:4200",
                "https://clinica-privada-frontend.vercel.app");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertTrue(configuration.getAllowedOrigins()
                .contains("https://clinica-privada-frontend.vercel.app"));
    }
}
