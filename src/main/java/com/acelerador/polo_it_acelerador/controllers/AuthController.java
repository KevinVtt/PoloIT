package com.acelerador.polo_it_acelerador.controllers;

import com.acelerador.polo_it_acelerador.models.dto.request.LoginRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.LoginResponseDTO;
import com.acelerador.polo_it_acelerador.util.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String token = jwtUtil.generateToken(auth.getName());
        return new LoginResponseDTO(token);
    }
    
}