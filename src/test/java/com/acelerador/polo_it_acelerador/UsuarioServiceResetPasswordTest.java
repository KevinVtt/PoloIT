package com.acelerador.polo_it_acelerador;

import com.acelerador.polo_it_acelerador.models.Contact;
import com.acelerador.polo_it_acelerador.models.PasswordResetToken;
import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.repositories.IPasswordResetToken;
import com.acelerador.polo_it_acelerador.repositories.IUsuario;
import com.acelerador.polo_it_acelerador.services.impl.UsuarioServiceImpl;
import com.acelerador.polo_it_acelerador.services.impl.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceResetPasswordTest {

    @Mock
    private IUsuario usuarioRepository;

    @Mock
    private IPasswordResetToken tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private User testUser;
    private Contact testContact;
    private PasswordResetToken validToken;

    @BeforeEach
    void setUp() {
        testContact = new Contact();
        testContact.setId(1L);
        testContact.setEmail("test@example.com");
        testContact.setName("Test User");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$oldHashedPassword");
        testUser.setRole("USER");
        testUser.setContact(testContact);

        validToken = new PasswordResetToken();
        validToken.setId(1L);
        validToken.setToken("valid-token-123");
        validToken.setUser(testUser);
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        validToken.setUsed(false);
    }

    @Test
    void testRequestResetPassword_Success() {
        // Given
        when(usuarioRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(testUser));
        when(tokenRepository.save(any(PasswordResetToken.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        usuarioService.requestResetPassword("test@example.com");

        // Then
        verify(tokenRepository).deleteByUserId(testUser.getId());
        
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getToken());
        assertEquals(testUser, savedToken.getUser());
        assertFalse(savedToken.isUsed());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));

        verify(emailService).enviarEmailRecuperacion(
            eq("test@example.com"),
            eq("Test User"),
            anyString()
        );
    }

    @Test
    void testRequestResetPassword_UserNotFound() {
        // Given
        when(usuarioRepository.findByEmail("nonexistent@example.com"))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            usuarioService.requestResetPassword("nonexistent@example.com")
        );

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).enviarEmailRecuperacion(anyString(), anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        // Given
        String newPassword = "newPassword123";
        String encodedPassword = "$2a$10$newHashedPassword";

        when(tokenRepository.findByToken("valid-token-123"))
            .thenReturn(Optional.of(validToken));
        when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword))
            .thenReturn(encodedPassword);
        when(usuarioRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        usuarioService.resetPassword("valid-token-123", newPassword);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(usuarioRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertEquals(encodedPassword, savedUser.getPassword());

        verify(tokenRepository).save(argThat(token -> 
            token.isUsed() == true
        ));
    }

    @Test
    void testResetPassword_TokenNotFound() {
        // Given
        when(tokenRepository.findByToken("invalid-token"))
            .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            usuarioService.resetPassword("invalid-token", "newPassword")
        );

        assertEquals("Token inválido", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testResetPassword_TokenAlreadyUsed() {
        // Given
        validToken.setUsed(true);
        when(tokenRepository.findByToken("valid-token-123"))
            .thenReturn(Optional.of(validToken));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            usuarioService.resetPassword("valid-token-123", "newPassword")
        );

        assertEquals("El token ya fue utilizado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testResetPassword_TokenExpired() {
        // Given
        validToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken("valid-token-123"))
            .thenReturn(Optional.of(validToken));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            usuarioService.resetPassword("valid-token-123", "newPassword")
        );

        assertEquals("El token ha expirado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testResetPassword_PasswordIsEncoded() {
        // Given
        String newPassword = "plainTextPassword";
        String encodedPassword = "$2a$10$encodedPassword";

        when(tokenRepository.findByToken("valid-token-123"))
            .thenReturn(Optional.of(validToken));
        when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword))
            .thenReturn(encodedPassword);

        // When
        usuarioService.resetPassword("valid-token-123", newPassword);

        // Then
        verify(passwordEncoder).encode(newPassword);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(usuarioRepository).save(userCaptor.capture());
        
        assertEquals(encodedPassword, userCaptor.getValue().getPassword());
        assertNotEquals(newPassword, userCaptor.getValue().getPassword());
    }

    @Test
    void testPasswordResetToken_IsValid() {
        // Token válido
        assertTrue(validToken.isValid());

        // Token usado
        validToken.setUsed(true);
        assertFalse(validToken.isValid());

        // Token expirado
        validToken.setUsed(false);
        validToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        assertFalse(validToken.isValid());

        // Token usado y expirado
        validToken.setUsed(true);
        assertFalse(validToken.isValid());
    }

    @Test
    void testPasswordResetToken_IsExpired() {
        // Token no expirado
        assertFalse(validToken.isExpired());

        // Token expirado
        validToken.setExpiryDate(LocalDateTime.now().minusSeconds(1));
        assertTrue(validToken.isExpired());

        // Token que expira exactamente ahora (edge case)
        validToken.setExpiryDate(LocalDateTime.now());
        // Puede ser true o false dependiendo del timing
        // Solo verificamos que no lance excepción
        assertDoesNotThrow(() -> validToken.isExpired());
    }
}