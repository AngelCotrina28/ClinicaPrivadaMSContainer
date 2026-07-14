package com.clinica.auth.services;

import com.clinica.auth.entities.Rol;
import com.clinica.auth.entities.Usuario;
import com.clinica.auth.repositories.RolRepository;
import com.clinica.auth.repositories.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthSeedService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AuthSeedService.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled}")
    private boolean seedEnabled;

    @Value("${app.seed.admin.username}")
    private String adminUsername;

    @Value("${app.seed.admin.password}")
    private String adminPassword;

    @Value("${app.seed.admin.email}")
    private String adminEmail;

    @Value("${app.seed.admin.name}")
    private String adminName;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        List<Rol> roles = List.of(
                rol("ADMINISTRADOR", "Gestiona usuarios, roles y configuracion del sistema."),
                rol("RECEPCIONISTA", "Gestiona admision y citas."),
                rol("JEFE_ENFERMERIA", "Supervisa admision e historias clinicas."),
                rol("ENFERMERO", "Gestiona historias clinicas y emergencias."),
                rol("MEDICO", "Gestiona atenciones medicas."),
                rol("TECNICO_FARMACIA", "Gestiona recetas e inventario."),
                rol("CAJERO", "Gestiona pagos, comprobantes y caja."));

        roles.forEach(this::crearRolSiNoExiste);
        sincronizarAdministrador();
    }

    private void sincronizarAdministrador() {
        Rol adminRol = rolRepository.findByNombreIgnoreCase("ADMINISTRADOR")
                .orElseThrow(() -> new IllegalStateException("No se pudo crear el rol administrador."));

        Usuario admin = usuarioRepository.findByUsernameIgnoreCase(adminUsername.trim())
                .orElseGet(() -> Usuario.builder()
                        .username(adminUsername.trim())
                        .build());

        admin.setEmail(adminEmail.trim());
        admin.setNombreCompleto(adminName.trim());
        admin.setRol(adminRol);
        admin.setActivo(true);

        if (!passwordCoincide(adminPassword, admin.getPasswordHash())) {
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        }

        boolean nuevo = admin.getId() == null;
        usuarioRepository.save(admin);
        logger.info("Usuario administrador {}: {}", nuevo ? "creado" : "sincronizado", admin.getUsername());
    }

    private boolean passwordCoincide(String password, String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        try {
            return passwordEncoder.matches(password, passwordHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private Rol rol(String nombre, String descripcion) {
        return Rol.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .activo(true)
                .build();
    }

    private void crearRolSiNoExiste(Rol rol) {
        if (!rolRepository.existsByNombreIgnoreCase(rol.getNombre())) {
            rolRepository.save(rol);
        }
    }
}
