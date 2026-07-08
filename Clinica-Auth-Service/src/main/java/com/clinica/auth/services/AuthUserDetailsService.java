package com.clinica.auth.services;

import com.clinica.auth.entities.Usuario;
import com.clinica.auth.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new DisabledException("Usuario deshabilitado");
        }

        return new User(
                usuario.getUsername(),
                usuario.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())));
    }
}
