package com.clinica.auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthSeedServiceTest {

    @Test
    void sincronizaLaClaveDelAdministradorExistenteSinCrearUsuariosDemo() {
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
        configurar(service, false, "");

        service.run();

        assertEquals("$2a$hash-nuevo", admin.getPasswordHash());
        assertEquals(adminRole, admin.getRol());
        assertTrue(admin.isActivo());
        verify(usuarioRepository).save(admin);
    }

    @Test
    void creaLosSeisUsuariosDemoCuandoLaBanderaEstaActiva() {
        RolRepository rolRepository = mock(RolRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthSeedService service = new AuthSeedService(rolRepository, usuarioRepository, passwordEncoder);
        Map<String, Rol> roles = roles();

        when(rolRepository.existsByNombreIgnoreCase(anyString())).thenReturn(true);
        when(rolRepository.findByNombreIgnoreCase(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(roles.get(invocation.getArgument(0))));
        when(usuarioRepository.findByUsernameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hash-demo");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        configurar(service, true, "Demo123!");

        service.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, org.mockito.Mockito.times(7)).save(captor.capture());
        List<Usuario> guardados = captor.getAllValues();
        Set<String> usernames = guardados.stream().map(Usuario::getUsername).collect(Collectors.toSet());
        assertTrue(usernames.containsAll(Set.of(
                "admin",
                "recepcionista",
                "jefe_enfermeria",
                "enfermero",
                "medico",
                "tecnico_farmacia",
                "cajero")));
        assertEquals(6, guardados.stream().filter(usuario -> !usuario.getUsername().equals("admin")).count());
    }

    @Test
    void conservaLaClaveDeUnUsuarioDemoExistente() {
        RolRepository rolRepository = mock(RolRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthSeedService service = new AuthSeedService(rolRepository, usuarioRepository, passwordEncoder);
        Map<String, Rol> roles = roles();
        Usuario medicoExistente = Usuario.builder()
                .id(25L)
                .username("medico")
                .email("medico.real@clinica.local")
                .nombreCompleto("Medico existente")
                .passwordHash("$2a$hash-que-no-debe-cambiar")
                .rol(roles.get("MEDICO"))
                .activo(true)
                .build();

        when(rolRepository.existsByNombreIgnoreCase(anyString())).thenReturn(true);
        when(rolRepository.findByNombreIgnoreCase(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(roles.get(invocation.getArgument(0))));
        when(usuarioRepository.findByUsernameIgnoreCase(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            return username.equals("medico") ? Optional.of(medicoExistente) : Optional.empty();
        });
        when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hash-nuevo");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        configurar(service, true, "Demo123!");

        service.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, org.mockito.Mockito.times(6)).save(captor.capture());
        assertFalse(captor.getAllValues().contains(medicoExistente));
        assertEquals("$2a$hash-que-no-debe-cambiar", medicoExistente.getPasswordHash());
    }

    private static void configurar(AuthSeedService service, boolean demoHabilitado, String demoPassword) {
        ReflectionTestUtils.setField(service, "seedEnabled", true);
        ReflectionTestUtils.setField(service, "adminUsername", "admin");
        ReflectionTestUtils.setField(service, "adminPassword", "Admin123");
        ReflectionTestUtils.setField(service, "adminEmail", "admin@clinica.local");
        ReflectionTestUtils.setField(service, "adminName", "Administrador General");
        ReflectionTestUtils.setField(service, "demoDataEnabled", demoHabilitado);
        ReflectionTestUtils.setField(service, "demoUserPassword", demoPassword);
    }

    private static Map<String, Rol> roles() {
        return Set.of(
                        "ADMINISTRADOR",
                        "RECEPCIONISTA",
                        "JEFE_ENFERMERIA",
                        "ENFERMERO",
                        "MEDICO",
                        "TECNICO_FARMACIA",
                        "CAJERO")
                .stream()
                .collect(Collectors.toMap(nombre -> nombre, nombre -> Rol.builder().nombre(nombre).activo(true).build()));
    }
}
