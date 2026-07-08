package com.clinica.gateway.controllers;

import com.clinica.gateway.services.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GatewayProxyController {

    private final ProxyService proxyService;

    @RequestMapping("/api/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws IOException, InterruptedException {
        return proxyService.reenviar(request);
    }
}
