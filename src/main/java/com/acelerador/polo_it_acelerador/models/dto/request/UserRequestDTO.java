package com.acelerador.polo_it_acelerador.models.dto.request;

public record UserRequestDTO(
        String username,
        String password,
        ContactRequestDTO contact
) {}
