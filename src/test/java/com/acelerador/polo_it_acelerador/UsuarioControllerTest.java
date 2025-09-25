package com.acelerador.polo_it_acelerador;

import com.acelerador.polo_it_acelerador.controllers.UsuarioController;
import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.models.dto.request.UserRequestDTO;
import com.acelerador.polo_it_acelerador.services.interf.IUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Assertions;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsuarioService usuarioService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("kvnVtt");
        user.setName("Kevin");
        user.setLastname("Vittor");
        user.setRole("ADMIN");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    @Test
    @DisplayName("✅ Debería devolver lista de usuarios")
    void testGetAllUsuariosOk() throws Exception {
        when(usuarioService.findAll()).thenReturn(Arrays.asList(getMockUser()));

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kevin"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    @DisplayName("❌ Debería devolver vacío/null cuando no encuentra usuario por id")
    void testGetUsuarioByIdNotFound() throws Exception {
        when(usuarioService.findById(99L)).thenReturn(null);

        String response = mockMvc.perform(get("/usuario/99"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertTrue(
                response == null || response.isEmpty(),
                "La respuesta debería ser null o vacía"
        );
    }

    @Test
    @DisplayName("✅ Debería crear un usuario correctamente")
    void testCreateUsuarioOk() throws Exception {
        UserRequestDTO dto = new UserRequestDTO("Juanpe","Juan", "Pérez","1111111","juanperez@gmail.com");
        User saved = getMockUser();
        saved.setName("Juan");
        saved.setLastname("Pérez");
        saved.setRole("USER");

        when(usuarioService.save(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/usuario/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("❌ Debería devolver null al crear usuario con body vacío")
    void testCreateUsuarioFailEmptyBody() throws Exception {
        String response = mockMvc.perform(post("/usuario/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertTrue(
                response == null || response.isEmpty() || response.contains("null"),
                "La respuesta debería ser null o indicar fallo"
        );
    }

    @Test
    @DisplayName("✅ Debería eliminar usuario existente")
    void testDeleteUsuarioOk() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(getMockUser());
        doNothing().when(usuarioService).deleteById(1L);

        mockMvc.perform(delete("/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado con exito! ID: 1"));
    }

    @Test
    @DisplayName("❌ Debería devolver vacío/null al eliminar usuario inexistente")
    void testDeleteUsuarioNotFound() throws Exception {
        when(usuarioService.findById(2L)).thenReturn(null);

        String response = mockMvc.perform(delete("/usuario/2"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertTrue(
                response == null || response.isEmpty() || response.contains("null"),
                "La respuesta debería ser null o vacía"
        );
    }
}
