package com.acelerador.polo_it_acelerador.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acelerador.polo_it_acelerador.exceptions.user.UsuarioErrorException;
import com.acelerador.polo_it_acelerador.exceptions.user.UsuarioNotFoundException;
import com.acelerador.polo_it_acelerador.exceptions.user.ValueNullException;
import com.acelerador.polo_it_acelerador.models.Contact;
import com.acelerador.polo_it_acelerador.models.PasswordResetToken;
import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.repositories.IPasswordResetToken;
import com.acelerador.polo_it_acelerador.repositories.IUsuario;
import com.acelerador.polo_it_acelerador.services.impl.email.EmailService;
import com.acelerador.polo_it_acelerador.services.interf.IUsuarioService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UsuarioServiceImpl implements IUsuarioService, UserDetailsService {

    private final IUsuario usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final IPasswordResetToken tokenRepository;

    public UsuarioServiceImpl(IUsuario usuarioRepository, PasswordEncoder passwordEncoder, EmailService emailService,
            IPasswordResetToken tokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            if (findById(id) != null) {
                usuarioRepository.deleteById(id);
            }
        } catch (UsuarioErrorException e) {
            throw new UsuarioErrorException("Hubo un error inesperado");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        try {
            return usuarioRepository.findAll();
        } catch (UsuarioErrorException e) {
            throw new UsuarioErrorException("Error al obtener los usuarios: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        try {
            return usuarioRepository.findById(id)
                    .orElseThrow(() -> new UsuarioNotFoundException("Usuario con ID " + id + " no encontrado."));
        } catch (UsuarioErrorException e) {
            throw new UsuarioErrorException("Error al obtener el usuario: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public User save(User entity) {
        try {
            validate(entity);
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            return usuarioRepository.save(entity);
        } catch (ValueNullException e) {
            throw new ValueNullException(e.getMessage());
        } catch (UsuarioErrorException e) {
            throw new UsuarioErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public User update(User entity) {
        try {
            log.info("usuario: " + entity);
            validate(entity);
            return usuarioRepository.save(entity);
        } catch (ValueNullException e) {
            throw new ValueNullException(e.getMessage());
        } catch (UsuarioErrorException e) {
            throw new UsuarioErrorException(e.getMessage());
        }
    }

    private void validate(User usuario) {

        Contact contact = usuario.getContact();

        if (contact.getName() == null || contact.getName().isEmpty()) {
            throw new ValueNullException("El nombre no puede ser nulo o vacio");
        }
        if (contact.getLastname() == null || contact.getLastname().isEmpty()) {
            throw new ValueNullException("El apellido no puede ser nulo o vacio");
        }
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new ValueNullException("La contraseña no puede ser nulo o vacio");
        }
        if (contact.getEmail() == null || contact.getEmail().isEmpty()) {
            throw new ValueNullException("La contraseña no puede ser nulo o vacio");
        }
        usuario.setRole("ADMIN");
    }

    @Override
    @Transactional
    public void requestResetPassword(String email) {
        try {
            User user = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Eliminar tokens previos del usuario
            tokenRepository.deleteByUserId(user.getId());

            String token = UUID.randomUUID().toString();

            // Crear y guardar el token (expira en 15 minutos)
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            resetToken.setUsed(false);
            tokenRepository.save(resetToken);

            // Enviar email con el token
            String resetLink = "http://localhost:3000/usuario/reset-password/confirm?token=" + token;
            emailService.enviarEmailRecuperacion(user.getContact().getEmail(), user.getContact().getName(), resetLink);

            log.info("Token de recuperación generado para: {}", email);

        } catch (Exception e) {
            log.error("Error al procesar solicitud de reset password: {}", e.getMessage());
            // No revelar si el email existe o no por seguridad
        }
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("El token ya fue utilizado");
        }

        if (resetToken.isExpired()) {
            throw new RuntimeException("El token ha expirado");
        }

        
        User user = usuarioRepository.findById(resetToken.getUser().getId())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        log.info(user.toString());

        log.info("Password ANTES: {}", user.getPassword());
        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("Password DESPUÉS: {}", user.getPassword());

        usuarioRepository.save(user);

        // Marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Contraseña actualizada exitosamente para usuario ID: {}", user.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Buscamos el usuario en la base de datos
        User usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        log.info("=== USER DETAILS SERVICE ===");
        log.info("Usuario encontrado: {}", usuario.getUsername());
        log.info("Hash en BD: {}", usuario.getPassword());
        log.info("¿Coincide 'kevinvittor'?: {}", 
            passwordEncoder.matches("kevinvittor", usuario.getPassword()));
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRole())
                .build();
    }

}
