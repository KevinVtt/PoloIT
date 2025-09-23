package com.acelerador.polo_it_acelerador.exceptions.user;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
