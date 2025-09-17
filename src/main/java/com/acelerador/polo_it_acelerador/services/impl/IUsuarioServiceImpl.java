package com.acelerador.polo_it_acelerador.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acelerador.polo_it_acelerador.exceptions.UsuarioErrorGeneric;
import com.acelerador.polo_it_acelerador.exceptions.UsuarioNotFound;
import com.acelerador.polo_it_acelerador.exceptions.ValueNull;
import com.acelerador.polo_it_acelerador.models.Usuario;
import com.acelerador.polo_it_acelerador.repositories.IUsuario;
import com.acelerador.polo_it_acelerador.services.interf.IUsuarioService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class IUsuarioServiceImpl implements IUsuarioService<Usuario>{

    private final IUsuario usuarioRepository;

    public IUsuarioServiceImpl(IUsuario usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            if(findById(id) != null){
                usuarioRepository.deleteById(id);
            }
        }catch(RuntimeException e){
            throw new RuntimeException("Hubo un error inesperado");
        }
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAll() {
        try{
            return usuarioRepository.findAll();
        }catch(Exception e){
            throw new RuntimeException("Error al obtener los usuarios: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        try{
            return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFound("Usuario con ID " + id + " no encontrado."));
        }catch(UsuarioErrorGeneric e){
            throw new RuntimeException("Error al obtener el usuario: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Usuario save(Usuario entity) {
        try{
            validate(entity);
            return usuarioRepository.save(entity);
        }catch(ValueNull e){
            throw new ValueNull(e.getMessage());
        }catch(UsuarioErrorGeneric e){
            throw new UsuarioErrorGeneric(e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Usuario update(Usuario entity) {
        try{
            log.info("usuario: " + entity);
            validate(entity);
            return usuarioRepository.save(entity);
        }catch(ValueNull e){
            throw new ValueNull(e.getMessage());
        }catch(UsuarioErrorGeneric e){
            throw new UsuarioErrorGeneric(e.getMessage());
        }
    }

    private void validate(Usuario usuario){
        if(usuario.getName() == null || usuario.getName().isEmpty()){
            throw new ValueNull("El nombre no puede ser nulo o vacio");
        }
        if(usuario.getLastname() == null || usuario.getLastname().isEmpty()){
            throw new ValueNull("El apellido no puede ser nulo o vacio");
        }
    }

}
