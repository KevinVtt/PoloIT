package com.acelerador.polo_it_acelerador.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acelerador.polo_it_acelerador.models.PasswordResetToken;

public interface IPasswordResetToken extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // Opcional: Buscar tokens expirados
    @Query("SELECT p FROM PasswordResetToken p WHERE p.expiryDate < :now")
    List<PasswordResetToken> findExpiredTokens(@Param("now") LocalDateTime now);
    
    // Opcional: Buscar tokens no usados de un usuario
    @Query("SELECT p FROM PasswordResetToken p WHERE p.user.id = :userId AND p.used = false")
    List<PasswordResetToken> findActiveTokensByUserId(@Param("userId") Long userId);
}