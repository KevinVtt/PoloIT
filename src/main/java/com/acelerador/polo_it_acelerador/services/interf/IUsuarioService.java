package com.acelerador.polo_it_acelerador.services.interf;

import java.util.List;

import com.acelerador.polo_it_acelerador.models.User;

public interface IUsuarioService{
    List<User> findAll();
    User findById(Long id);
    User save(User entity);
    User update(User entity);
    void deleteById(Long id);
}
