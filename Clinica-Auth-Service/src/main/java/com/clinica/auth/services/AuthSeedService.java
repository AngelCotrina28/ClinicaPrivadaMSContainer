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
                rol("MEDICO", "Gestiona atenciones medicas."),
                rol("FARMACIA", "Gestiona recetas e inventario."),
                rol("CAJERO", "Gestiona pagos, comprobantes y caja."));

        roles.forEach(this::crearRolSiNoExiste);

        if (usuarioRepository.count() == 0) {
            Rol adminRol = rolRepository.findByNombreIgnoreCase("ADMINISTRADOR")
                    .orElseThrow(() -> new IllegalStateException("No se pudo crear el rol administrador."));

            Usuario admin = Usuario.builder()
                    .username(adminUsername.trim())
                    .email(adminEmail.trim())
                    .nombreCompleto(adminName.trim())
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .rol(adminRol)
                    .activo(true)
                    .build();

            usuarioRepository.save(admin);
            logger.info("Usuario administrador inicial creado: {}", admin.getUsername());
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
