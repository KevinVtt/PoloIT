package com.acelerador.polo_it_acelerador.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.acelerador.polo_it_acelerador.exceptions.ticket.TicketAlreadyExistsException;
import com.acelerador.polo_it_acelerador.exceptions.ticket.TicketErrorException;
import com.acelerador.polo_it_acelerador.exceptions.ticket.TicketNotFoundException;
import com.acelerador.polo_it_acelerador.exceptions.user.UsuarioErrorException;
import com.acelerador.polo_it_acelerador.exceptions.user.UsuarioNotFoundException;
import com.acelerador.polo_it_acelerador.exceptions.user.ValueNullException;

@ControllerAdvice
public class HandlerException {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<HashMap<String,String>> handleUsuarioNotFound(UsuarioNotFoundException ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(404).body(errors);
    }

    @ExceptionHandler(ValueNullException.class)
    public ResponseEntity<HashMap<String,String>> handleValueNull(ValueNullException ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(400).body(errors);
    }

    @ExceptionHandler(UsuarioErrorException.class)
    public ResponseEntity<HashMap<String,String>> handleUsuarioErrorGeneric(UsuarioErrorException ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(400).body(errors);
    }

    @ExceptionHandler(TicketErrorException.class)
    public ResponseEntity<HashMap<String,String>> handleTicketErrorGeneric(TicketErrorException ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(400).body(errors);
    }

    @ExceptionHandler(TicketAlreadyExistsException.class)
    public ResponseEntity<HashMap<String,String>> handleTicketErrorGeneric(TicketAlreadyExistsException ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(401).body(errors);
    }
    
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<HashMap<String,String>> handleTicketErrorGeneric(TicketNotFoundException ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(404).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HashMap<String,String>> handleBadCredentials(BadCredentialsException ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(403).body(errors);
    }
}
