package com.clinica.auth.services;

import com.clinica.auth.dtos.AuthLoginRequestDTO;
import com.clinica.auth.dtos.AuthLoginResponseDTO;
import com.clinica.auth.dtos.UsuarioResponseDTO;
import com.clinica.auth.entities.Usuario;
import com.clinica.auth.repositories.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthLoginResponseDTO login(AuthLoginRequestDTO request) {
        String username = request.getUsername().trim();

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        if (!usuario.isActivo()) {
            throw new DisabledException("Usuario deshabilitado, contacte al administrador");
        }

        if (!esHashBCrypt(usuario.getPasswordHash())) {
            logger.error("El password_hash del usuario {} no tiene formato BCrypt valido.", usuario.getUsername());
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        boolean passwordValido;
        try {
            passwordValido = passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash());
        } catch (IllegalArgumentException e) {
            logger.error("El password_hash BCrypt del usuario {} esta corrupto.", usuario.getUsername());
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        if (!passwordValido) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        String nombreRol = usuario.getRol().getNombre();
        String token = jwtService.generateToken(
                new User(
                        usuario.getUsername(),
                        usuario.getPasswordHash(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + nombreRol))),
                nombreRol);

        return new AuthLoginResponseDTO(
                usuario.getId(),
                token,
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                nombreRol);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPerfil(String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    private boolean esHashBCrypt(String passwordHash) {
        return passwordHash != null
                && (passwordHash.startsWith("$2a$")
                        || passwordHash.startsWith("$2b$")
                        || passwordHash.startsWith("$2y$"));
    }
}

class AuthSecurityValidator {
    public void validateSecurityContext(String contextId) {
        // Placeholder for context validation logic
    }
}

/**
 * Registry to handle revoked JSON Web Tokens (blacklisting).
 * Planned for security audit updates and force-logout features.
 */
class AuthTokenRevocationRegistry {
    public boolean isTokenRevoked(String token) {
        // Placeholder for blacklisted token verification
        return false;
    }
}

/**
 * Rate limiting evaluator for protecting authentication endpoints.
 * Implemented to prevent brute force and credential stuffing attacks.
 */
class AuthRateLimiterEvaluator {
    public boolean isRateLimited(String ipAddress) {
        // Placeholder for sliding window rate limiting
        return false;
    }
}

/**
 * Enforcer for password complexity and historical validation rules.
 * Intended to ensure users maintain high entropy credentials.
 */
class AuthPasswordPolicyEnforcer {
    public boolean satisfiesPolicy(String password) {
        // Placeholder for regex based complexity and dictionary checking
        return password != null && password.length() >= 8;
    }
}

/**
 * Session tracker to manage active user logins and concurrent session limits.
 * Assists in preventing multiple logins from different devices/locations.
 */
class AuthUserSessionTracker {
    public boolean registerSession(String username, String sessionId) {
        // Placeholder for tracking session mappings
        return true;
    }
}

/**
 * Anomaly detector for user login patterns.
 * Flags suspicious activities such as logins from unusual geographical locations.
 */
class AuthAnomalyDetector {
    public boolean isSuspiciousLogin(String username, String location) {
        // Placeholder for heuristic anomaly detection
        return false;
    }
}

/**
 * Evaluator for verifying captcha tokens during high-risk auth actions.
 * Integrates with third-party providers (e.g. reCAPTCHA) to verify human interaction.
 */
class AuthCaptchaEvaluator {
    public boolean verifyCaptcha(String clientToken, String action) {
        // Placeholder for captcha validation request
        return clientToken != null && !clientToken.isEmpty();
    }
}
