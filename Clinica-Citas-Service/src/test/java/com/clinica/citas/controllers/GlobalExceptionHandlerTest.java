package com.clinica.citas.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProbeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void devuelve400CuandoLaFechaEsInvalida() throws Exception {
        mockMvc.perform(get("/prueba/fecha").param("fecha", "2026-99-99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void devuelve400CuandoFaltaUnParametro() throws Exception {
        mockMvc.perform(get("/prueba/fecha"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void devuelve400CuandoElJsonEstaMalFormado() throws Exception {
        mockMvc.perform(post("/prueba/body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void conserva404ParaUnaRutaInexistente() throws Exception {
        mockMvc.perform(get("/ruta-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void devuelve405CuandoElMetodoHttpNoEstaPermitido() throws Exception {
        mockMvc.perform(post("/prueba/fecha"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void devuelve500SanitizadoParaUnErrorNoControlado() throws Exception {
        mockMvc.perform(get("/prueba/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.error").value("Error interno en Citas Service."));
    }

    @RestController
    static class ProbeController {

        @GetMapping("/prueba/fecha")
        Map<String, String> fecha(@RequestParam LocalDate fecha) {
            return Map.of("fecha", fecha.toString());
        }

        @PostMapping("/prueba/body")
        Map<String, String> body(@RequestBody ProbeRequest request) {
            return Map.of("valor", request.valor());
        }

        @GetMapping("/prueba/error")
        void error() {
            throw new IllegalStateException("detalle que no debe exponerse");
        }
    }

    record ProbeRequest(String valor) {
    }
}
