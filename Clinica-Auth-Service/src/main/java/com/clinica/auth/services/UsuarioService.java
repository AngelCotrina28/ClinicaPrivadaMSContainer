package com.clinica.auth.services;

import com.clinica.auth.dtos.CrearUsuarioRequestDTO;
import com.clinica.auth.dtos.UsuarioResponseDTO;
import com.clinica.auth.entities.Rol;
import com.clinica.auth.entities.Usuario;
import com.clinica.auth.repositories.RolRepository;
import com.clinica.auth.repositories.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO crear(CrearUsuarioRequestDTO request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username.");
        }

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("El rol indicado no existe."));

        if (!rol.isActivo()) {
            throw new IllegalArgumentException("El rol indicado esta inactivo.");
        }

        Usuario usuario = Usuario.builder()
                .username(username)
                .email(email)
                .nombreCompleto(request.getNombreCompleto().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .activo(request.getActivo() == null || request.getActivo())
                .build();

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }
}
