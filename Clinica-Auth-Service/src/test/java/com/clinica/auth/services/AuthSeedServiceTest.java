package com.clinica.auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clinica.auth.entities.Rol;
import com.clinica.auth.entities.Usuario;
import com.clinica.auth.repositories.RolRepository;
import com.clinica.auth.repositories.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthSeedServiceTest {

    @Test
    void sincronizaLaClaveDelAdministradorExistente() {
        RolRepository rolRepository = mock(RolRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthSeedService service = new AuthSeedService(rolRepository, usuarioRepository, passwordEncoder);
        Rol adminRole = Rol.builder().id(1L).nombre("ADMINISTRADOR").activo(true).build();
        Usuario admin = Usuario.builder()
                .id(1L)
                .username("admin")
                .email("anterior@clinica.local")
                .nombreCompleto("Admin anterior")
                .passwordHash("$2a$hash-anterior")
                .activo(false)
                .build();

        when(rolRepository.existsByNombreIgnoreCase(anyString())).thenReturn(true);
        when(rolRepository.findByNombreIgnoreCase("ADMINISTRADOR")).thenReturn(Optional.of(adminRole));
        when(usuarioRepository.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("Admin123", "$2a$hash-anterior")).thenReturn(false);
        when(passwordEncoder.encode("Admin123")).thenReturn("$2a$hash-nuevo");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ReflectionTestUtils.setField(service, "seedEnabled", true);
        ReflectionTestUtils.setField(service, "adminUsername", "admin");
        ReflectionTestUtils.setField(service, "adminPassword", "Admin123");
        ReflectionTestUtils.setField(service, "adminEmail", "admin@clinica.local");
        ReflectionTestUtils.setField(service, "adminName", "Administrador General");

        service.run();

        assertEquals("$2a$hash-nuevo", admin.getPasswordHash());
        assertEquals(adminRole, admin.getRol());
        assertTrue(admin.isActivo());
        verify(usuarioRepository).save(admin);
    }
}
