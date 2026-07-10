package com.clinica.gateway.services;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProxyService {

    private static final Logger logger = LoggerFactory.getLogger(ProxyService.class);

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "connection",
            "content-length",
            "expect",
            "host",
            "keep-alive",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailer",
            "transfer-encoding",
            "upgrade");

    private static final Set<String> REQUEST_HEADERS_PERMITIDOS = Set.of(
            "authorization",
            "content-type",
            "accept",
            "origin",
            "x-requested-with");

    private static final Set<String> RESPONSE_HEADERS_PERMITIDOS = Set.of(
            "authorization",
            "content-type",
            "location");

    private final GatewayRouter router;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public ResponseEntity<byte[]> reenviar(HttpServletRequest request) throws IOException, InterruptedException {
        String destino = construirDestino(request);
        byte[] body = request.getInputStream().readAllBytes();
        logger.info("Gateway proxy {} {}", request.getMethod(), destino);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(destino))
                .timeout(Duration.ofSeconds(30));

        copiarHeaders(request, builder);
        builder.method(request.getMethod(), bodyPublisher(request.getMethod(), body));

        HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        HttpHeaders responseHeaders = copiarResponseHeaders(response);

        return ResponseEntity
                .status(response.statusCode())
                .headers(responseHeaders)
                .body(response.body());
    }

    private String construirDestino(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String destino = router.resolverDestino(path);
        return query == null || query.isBlank() ? destino : destino + "?" + query;
    }

    private HttpRequest.BodyPublisher bodyPublisher(String method, byte[] body) {
        if (body.length == 0 && ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method))) {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.ofByteArray(body);
    }

    private void copiarHeaders(HttpServletRequest request, HttpRequest.Builder builder) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String normalized = headerName.toLowerCase();
            if (HOP_BY_HOP_HEADERS.contains(normalized) || !REQUEST_HEADERS_PERMITIDOS.contains(normalized)) {
                continue;
            }

            Enumeration<String> values = request.getHeaders(headerName);
            while (values.hasMoreElements()) {
                builder.header(headerName, values.nextElement());
            }
        }
    }

    private HttpHeaders copiarResponseHeaders(HttpResponse<byte[]> response) {
        HttpHeaders headers = new HttpHeaders();
        response.headers().map().forEach((name, values) -> {
            String normalized = name.toLowerCase();
            if (!HOP_BY_HOP_HEADERS.contains(normalized) && RESPONSE_HEADERS_PERMITIDOS.contains(normalized)) {
                values.forEach(value -> headers.add(name, value));
            }
        });
        return headers;
    }
}
