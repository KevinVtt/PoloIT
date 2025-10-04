package com.acelerador.polo_it_acelerador.controllers;

import com.acelerador.polo_it_acelerador.models.dto.request.LoginRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.LoginResponseDTO;
import com.acelerador.polo_it_acelerador.util.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, 
                         JwtUtil jwtUtil,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        log.info("=== INICIO LOGIN ===");
        log.info("Username recibido: {}", request.username());
        log.info("Password recibida (texto plano): {}", request.password());
        log.info("Hash de la password recibida: {}", passwordEncoder.encode(request.password()));
        
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            log.info("Autenticación exitosa para: {}", auth.getName());
            String token = jwtUtil.generateToken(auth.getName());
            return new LoginResponseDTO(token);
        } catch (Exception e) {
            log.error("Error en autenticación: {}", e.getMessage());
            throw e;
        }
    }
}