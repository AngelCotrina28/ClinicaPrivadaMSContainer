package com.clinica.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CorsConfigTest {

    @Test
    void seleccionaLaFuenteCorsPropiaAunqueExistaOtraFuenteMvc() {
        new ApplicationContextRunner()
                .withPropertyValues(
                        "app.cors.allowed-origins=http://localhost:4200",
                        "app.cors.frontend-origin=http://localhost:4200")
                .withUserConfiguration(CorsConfig.class, FuenteCorsMvcAdicional.class)
                .run(context -> {
                    assertNotNull(context.getBean("corsConfigurationSource"));
                    assertNotNull(context.getBean("corsFilter"));
                });
    }

    @Test
    void respondeElPreflightDelFrontendProductivoConCabecerasCors() throws Exception {
        CorsConfig config = new CorsConfig();
        CorsConfigurationSource source = config.corsConfigurationSource(
                "http://localhost:4200",
                "https://clinica-privada-frontend.vercel.app");
        FilterRegistrationBean<CorsFilter> registration = config.corsFilter(source);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new LoginTestController())
                .addFilters(registration.getFilter())
                .build();

        assertEquals(Ordered.HIGHEST_PRECEDENCE, registration.getOrder());
        mockMvc.perform(options("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, "https://clinica-privada-frontend.vercel.app")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "https://clinica-privada-frontend.vercel.app"))
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        containsString("POST")))
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        containsString("authorization")))
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        containsString("content-type")));
    }

    @RestController
    static class LoginTestController {

        @PostMapping("/api/auth/login")
        void login() {
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class FuenteCorsMvcAdicional {

        @Bean
        CorsConfigurationSource mvcHandlerMappingIntrospector() {
            return request -> null;
        }
    }
}
