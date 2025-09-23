package com.acelerador.polo_it_acelerador.models.dto.response;

public record TicketResponseDTO(
        Long id,
        String email,
        String asunto,
        String descripcion
) {}