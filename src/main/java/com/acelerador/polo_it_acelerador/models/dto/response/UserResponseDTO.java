package com.acelerador.polo_it_acelerador.models.dto.response;

import java.time.LocalDateTime;

// Pronta implementacion
public record UserResponseDTO(
        Long id,
        String username,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ContactResponseDTO contact
) {}