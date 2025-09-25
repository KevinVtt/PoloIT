package com.acelerador.polo_it_acelerador.models.dto.response;

import java.time.LocalDateTime;

// Pronta implementacion
public record UserResponseDTO(
        Long id,
        String name,
        String lastname,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}