package com.acelerador.polo_it_acelerador.exceptions.ticket;

public class TicketAlreadyExistsException extends RuntimeException {
    public TicketAlreadyExistsException(String message){
        super(message);
    }
}
