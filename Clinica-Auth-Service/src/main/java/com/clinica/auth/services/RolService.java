package com.clinica.auth.services;

import com.clinica.auth.dtos.RolResponseDTO;
import com.clinica.auth.repositories.RolRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<RolResponseDTO> listar() {
        return rolRepository.findAll().stream()
                .map(RolResponseDTO::fromEntity)
                .toList();
    }
}
