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

    @Value("${app.seed.demo.enabled:false}")
    private boolean demoDataEnabled;

    @Value("${app.seed.demo.password:}")
    private String demoUserPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "SYSTEM_ADMIN_PASSWORD es obligatorio cuando AUTH_SEED_ENABLED=true.");
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
        if (demoDataEnabled) {
            crearUsuariosDemo();
        }
    }

    private void crearUsuariosDemo() {
        if (demoUserPassword == null || demoUserPassword.isBlank()) {
            throw new IllegalStateException(
                    "DEMO_USER_PASSWORD es obligatorio cuando DEMO_DATA_ENABLED=true.");
        }

        List<UsuarioDemo> usuariosDemo = List.of(
                new UsuarioDemo(
                        "recepcionista",
                        "recepcionista@clinica.local",
                        "Recepcionista Demo",
                        "RECEPCIONISTA"),
                new UsuarioDemo(
                        "jefe_enfermeria",
                        "jefe.enfermeria@clinica.local",
                        "Jefe de Enfermeria Demo",
                        "JEFE_ENFERMERIA"),
                new UsuarioDemo(
                        "enfermero",
                        "enfermero@clinica.local",
                        "Enfermero Demo",
                        "ENFERMERO"),
                new UsuarioDemo(
                        "medico",
                        "medico@clinica.local",
                        "Medico Demo",
                        "MEDICO"),
                new UsuarioDemo(
                        "tecnico_farmacia",
                        "tecnico.farmacia@clinica.local",
                        "Tecnico de Farmacia Demo",
                        "TECNICO_FARMACIA"),
                new UsuarioDemo(
                        "cajero",
                        "cajero@clinica.local",
                        "Cajero Demo",
                        "CAJERO"));

        usuariosDemo.forEach(this::crearUsuarioDemoSiNoExiste);
    }

    private void crearUsuarioDemoSiNoExiste(UsuarioDemo demo) {
        if (usuarioRepository.findByUsernameIgnoreCase(demo.username()).isPresent()) {
            logger.info("Usuario demo conservado sin cambios: {}", demo.username());
            return;
        }
        if (usuarioRepository.existsByEmailIgnoreCase(demo.email())) {
            logger.warn("No se creo {} porque el correo demo ya esta registrado.", demo.username());
            return;
        }

        Rol rol = rolRepository.findByNombreIgnoreCase(demo.rol())
                .orElseThrow(() -> new IllegalStateException("No existe el rol demo " + demo.rol()));
        Usuario usuario = Usuario.builder()
                .username(demo.username())
                .email(demo.email())
                .nombreCompleto(demo.nombreCompleto())
                .passwordHash(passwordEncoder.encode(demoUserPassword))
                .rol(rol)
                .activo(true)
                .build();
        usuarioRepository.save(usuario);
        logger.info("Usuario demo creado: {}", demo.username());
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

    private record UsuarioDemo(String username, String email, String nombreCompleto, String rol) {
    }
}
