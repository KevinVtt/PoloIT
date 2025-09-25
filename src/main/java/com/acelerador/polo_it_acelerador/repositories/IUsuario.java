package com.acelerador.polo_it_acelerador.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acelerador.polo_it_acelerador.models.User;

@Repository
public interface IUsuario extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
