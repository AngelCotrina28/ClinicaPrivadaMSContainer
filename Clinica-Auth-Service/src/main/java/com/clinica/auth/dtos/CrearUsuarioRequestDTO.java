package com.clinica.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearUsuarioRequestDTO {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 60, message = "El usuario no debe superar 60 caracteres")
    private String username;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 120, message = "El nombre completo no debe superar 120 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 120, message = "El correo no debe superar 120 caracteres")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, max = 100, message = "La contrasena debe tener entre 6 y 100 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Long rolId;

    private Boolean activo;
}
