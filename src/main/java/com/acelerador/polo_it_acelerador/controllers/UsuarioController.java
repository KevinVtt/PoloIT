package com.acelerador.polo_it_acelerador.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acelerador.polo_it_acelerador.models.Usuario;
import com.acelerador.polo_it_acelerador.services.interf.IUsuarioService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/usuario")
public class UsuarioController {

    private final IUsuarioService<Usuario> usuarioService;

    public UsuarioController(IUsuarioService<Usuario> usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Usuario>> getAllUsuarios(){
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping("/crear")
    public ResponseEntity<String> createUsuario(@RequestBody Usuario usuario){
        Usuario newUsuario = usuarioService.save(usuario);
        log.info("Usuario creado: " + newUsuario);
        if(newUsuario != null){
            return ResponseEntity.ok("Usuario creado con exito! ID: " + newUsuario.getId());
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario){
        Usuario updatedUsuario = usuarioService.findById(id);
        if(updatedUsuario != null){
            usuario.setId(id);
            if(usuarioService.update(usuario) != null){
                return ResponseEntity.ok("Usuario actualizado con exito! ID: " + updatedUsuario.getId());
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable Long id){
        if(usuarioService.findById(id) != null){
            usuarioService.deleteById(id);
        }
        return ResponseEntity.ok("Usuario eliminado con exito! ID: " + id);
    }
}
