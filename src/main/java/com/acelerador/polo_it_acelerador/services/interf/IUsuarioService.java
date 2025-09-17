package com.acelerador.polo_it_acelerador.services.interf;

import java.util.List;

public interface IUsuarioService <T>{
    List<T> findAll();
    T findById(Long id);
    T save(T entity);
    T update(T entity);
    void deleteById(Long id);
}
