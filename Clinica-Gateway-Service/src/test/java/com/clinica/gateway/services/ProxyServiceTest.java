package com.clinica.gateway.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ProxyServiceTest {

    @Test
    void noReenviaOriginALosMicroserviciosInternos() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(
                java.util.List.of("Origin", "Content-Type")));
        when(request.getHeaders("Origin")).thenReturn(Collections.enumeration(
                java.util.List.of("https://clinica-privada-frontend.vercel.app")));
        when(request.getHeaders("Content-Type")).thenReturn(Collections.enumeration(
                java.util.List.of("application/json")));

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://auth/api/auth/login"));
        ReflectionTestUtils.invokeMethod(new ProxyService(null), "copiarHeaders", request, builder);
        HttpRequest proxiedRequest = builder.POST(HttpRequest.BodyPublishers.noBody()).build();

        assertTrue(proxiedRequest.headers().firstValue("Origin").isEmpty());
        assertEquals("application/json", proxiedRequest.headers().firstValue("Content-Type").orElseThrow());
    }
}
