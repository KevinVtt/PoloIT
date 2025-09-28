package com.acelerador.polo_it_acelerador.models.dto.request;

public record UserRequestDTO(
        String username,
        String name,
        String lastname,
        String password,
        String email
) {}
