package com.acelerador.polo_it_acelerador.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandlerException {

    @ExceptionHandler(UsuarioNotFound.class)
    public ResponseEntity<HashMap<String,String>> handleUsuarioNotFound(UsuarioNotFound ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(404).body(errors);
    }

    @ExceptionHandler(ValueNull.class)
    public ResponseEntity<HashMap<String,String>> handleValueNull(ValueNull ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(400).body(errors);
    }

    @ExceptionHandler(UsuarioErrorGeneric.class)
    public ResponseEntity<HashMap<String,String>> handleUsuarioErrorGeneric(UsuarioErrorGeneric ex){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("time", LocalDateTime.now().toString());
        return ResponseEntity.status(400).body(errors);
    }
}
