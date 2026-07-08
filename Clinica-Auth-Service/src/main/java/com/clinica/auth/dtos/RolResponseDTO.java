package com.clinica.auth.dtos;

import com.clinica.auth.entities.Rol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RolResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public static RolResponseDTO fromEntity(Rol rol) {
        return RolResponseDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.isActivo())
                .build();
    }
}
