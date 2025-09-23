package com.acelerador.polo_it_acelerador.exceptions.ticket;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message){
        super(message);
    }
}
