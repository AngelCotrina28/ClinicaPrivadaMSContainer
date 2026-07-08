package com.clinica.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResponseDTO {
    private Long id;
    private String token;
    private String username;
    private String nombreCompleto;
    private String rol;
}
