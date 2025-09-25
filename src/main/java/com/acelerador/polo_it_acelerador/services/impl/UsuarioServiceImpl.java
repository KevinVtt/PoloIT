package com.acelerador.polo_it_acelerador.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acelerador.polo_it_acelerador.exceptions.user.UsuarioErrorException;
import com.acelerador.polo_it_acelerador.exceptions.user.UsuarioNotFoundException;
import com.acelerador.polo_it_acelerador.exceptions.user.ValueNullException;
import com.acelerador.polo_it_acelerador.models.User;
import com.acelerador.polo_it_acelerador.repositories.IUsuario;
import com.acelerador.polo_it_acelerador.services.interf.IUsuarioService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UsuarioServiceImpl implements IUsuarioService, UserDetailsService{

    private final IUsuario usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(IUsuario usuarioRepository,PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            if(findById(id) != null){
                usuarioRepository.deleteById(id);
            }
        }catch(UsuarioErrorException e){
            throw new UsuarioErrorException("Hubo un error inesperado");
        }
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        try{
            return usuarioRepository.findAll();
        }catch(UsuarioErrorException e){
            throw new UsuarioErrorException("Error al obtener los usuarios: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        try{
            return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("Usuario con ID " + id + " no encontrado."));
        }catch(UsuarioErrorException e){
            throw new UsuarioErrorException("Error al obtener el usuario: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public User save(User entity) {
        try{
            validate(entity);
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            return usuarioRepository.save(entity);
        }catch(ValueNullException e){
            throw new ValueNullException(e.getMessage());
        }catch(UsuarioErrorException e){
            throw new UsuarioErrorException(e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public User update(User entity) {
        try{
            log.info("usuario: " + entity);
            validate(entity);
            return usuarioRepository.save(entity);
        }catch(ValueNullException e){
            throw new ValueNullException(e.getMessage());
        }catch(UsuarioErrorException e){
            throw new UsuarioErrorException(e.getMessage());
        }
    }

    private void validate(User usuario){
        if(usuario.getName() == null || usuario.getName().isEmpty()){
            throw new ValueNullException("El nombre no puede ser nulo o vacio");
        }
        if(usuario.getLastname() == null || usuario.getLastname().isEmpty()){
            throw new ValueNullException("El apellido no puede ser nulo o vacio");
        }
        if(usuario.getPassword() == null || usuario.getPassword().isEmpty()){
            throw new ValueNullException("La contraseña no puede ser nulo o vacio");
        }
        if(usuario.getEmail() == null || usuario.getEmail().isEmpty()){
            throw new ValueNullException("La contraseña no puede ser nulo o vacio");
        }
        usuario.setRole("ADMIN");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Buscamos el usuario en la base de datos
        User usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRole())
                .build();
    }

    

}
