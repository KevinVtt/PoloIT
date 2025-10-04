package com.acelerador.polo_it_acelerador;

import com.acelerador.polo_it_acelerador.models.Contact;
import com.acelerador.polo_it_acelerador.models.PasswordResetToken;
import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.repositories.IPasswordResetToken;
import com.acelerador.polo_it_acelerador.repositories.IUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerResetPasswordIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUsuario usuarioRepository;

    @Autowired
    private IPasswordResetToken tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Limpiar datos
        tokenRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario de prueba
        Contact contact = new Contact();
        contact.setEmail("test@example.com");
        contact.setName("Test User");
        contact.setLastname("Test");

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("oldPassword"));
        testUser.setRole("USER");
        testUser.setContact(contact);
        testUser = usuarioRepository.save(testUser);

        // Crear token válido
        validToken = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(validToken);
        resetToken.setUser(testUser);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);
    }

    @Test
    void testRequestResetPassword_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@example.com");

        mockMvc.perform(post("/usuario/reset-password/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                    "Si el correo existe, recibirás instrucciones para restablecer tu contraseña."
                ));

        // Verificar que se creó un token
        assertTrue(tokenRepository.findAll().size() > 0);
    }

    @Test
    void testResetPassword_Success() throws Exception {
        String newPassword = "newSecurePassword123";
        Map<String, String> request = new HashMap<>();
        request.put("newPassword", newPassword);

        mockMvc.perform(post("/usuario/reset-password/confirm")
                .param("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contraseña actualizada exitosamente"));

        // Verificar que la password cambió
        User updatedUser = usuarioRepository.findById(testUser.getId()).get();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));

        // Verificar que el token se marcó como usado
        PasswordResetToken usedToken = tokenRepository.findByToken(validToken).get();
        assertTrue(usedToken.isUsed());
    }

    @Test
    void testResetPassword_InvalidToken() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "newPassword");

        mockMvc.perform(post("/usuario/reset-password/confirm")
                .param("token", "invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token inválido"));
    }

    @Test
    void testResetPassword_ExpiredToken() throws Exception {
        // Crear token expirado
        String expiredToken = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(expiredToken);
        resetToken.setUser(testUser);
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "newPassword");

        mockMvc.perform(post("/usuario/reset-password/confirm")
                .param("token", expiredToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El token ha expirado"));
    }

    @Test
    void testResetPassword_UsedToken() throws Exception {
        // Marcar token como usado
        PasswordResetToken usedToken = tokenRepository.findByToken(validToken).get();
        usedToken.setUsed(true);
        tokenRepository.save(usedToken);

        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "newPassword");

        mockMvc.perform(post("/usuario/reset-password/confirm")
                .param("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El token ya fue utilizado"));
    }

    @Test
    void testCompleteFlowResetAndLogin() throws Exception {
        // 1. Reset password
        String newPassword = "myNewPassword123";
        Map<String, String> resetRequest = new HashMap<>();
        resetRequest.put("newPassword", newPassword);

        mockMvc.perform(post("/usuario/reset-password/confirm")
                .param("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk());

        // 2. Intentar login con nueva password
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", newPassword);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}