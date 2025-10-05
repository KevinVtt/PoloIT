package com.acelerador.polo_it_acelerador.exceptions.email;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException(String message) {
        super(message);
    }
}
