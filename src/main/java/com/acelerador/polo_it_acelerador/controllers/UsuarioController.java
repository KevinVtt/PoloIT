package com.acelerador.polo_it_acelerador.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.models.dto.request.UserRequestDTO;
import com.acelerador.polo_it_acelerador.models.dto.response.UserResponseDTO;
import com.acelerador.polo_it_acelerador.models.mappers.UserMapper;
import com.acelerador.polo_it_acelerador.services.interf.IUsuarioService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/usuario")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsuarios(){
        return ResponseEntity.ok(UserMapper.toResponseDTOList(usuarioService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUsuarioById(@PathVariable Long id){
        return ResponseEntity.ok(UserMapper.toResponseDTO(usuarioService.findById(id)));
    }

    @PostMapping("/crear")
    public ResponseEntity<UserResponseDTO> createUsuario(@RequestBody UserRequestDTO usuario){
        User newUsuario = UserMapper.toEntity(usuario);
        User userSave = usuarioService.save(newUsuario);
        return ResponseEntity.ok(UserMapper.toResponseDTO(userSave));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUsuario(@PathVariable Long id, @RequestBody UserResponseDTO usuario){
        User updatedUsuario = validateUser(id,usuario);
        return ResponseEntity.ok(UserMapper.toResponseDTO(updatedUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable Long id){
        if(usuarioService.findById(id) != null){
            usuarioService.deleteById(id);
        }
        return ResponseEntity.ok("Usuario eliminado con exito! ID: " + id);
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<String> requestResetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        usuarioService.requestResetPassword(email);
        return ResponseEntity.ok("Si el correo existe, recibirás instrucciones para restablecer tu contraseña.");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPassword(@RequestParam String token,@RequestBody Map<String, String> passwordJson) {
        try {
            String newPassword = passwordJson.get("newPassword");
            usuarioService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Contraseña actualizada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User validateUser(Long id, UserResponseDTO dto){
        User userDb = usuarioService.findById(id);
        userDb.getContact().setLastname((dto.contact().lastname() != null) && !(dto.contact().lastname().isEmpty()) ? dto.contact().lastname() : userDb.getContact().getLastname());
        userDb.getContact().setName((dto.contact().name() != null) && !(dto.contact().name().isEmpty()) ? dto.contact().name() : userDb.getContact().getName());
        userDb.setRole((dto.role() != null) && !(dto.role().isEmpty()) ? dto.role() : userDb.getRole());
        return usuarioService.update(userDb);
    }
}
