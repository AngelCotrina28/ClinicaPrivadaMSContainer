package com.clinica.auth.dtos;

import com.clinica.auth.entities.Usuario;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private String rol;
    private boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombreCompleto())
                .email(usuario.getEmail())
                .rol(usuario.getRol().getNombre())
                .activo(usuario.isActivo())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
