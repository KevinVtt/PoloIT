package com.acelerador.polo_it_acelerador.config;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.acelerador.polo_it_acelerador.repositories.IPasswordResetToken;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ResetPasswordJob {

    @Autowired
    private IPasswordResetToken tokenRepository;

    // Limpia tokens expirados o usados cada día a medianoche
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanExpiredTokens(){
        LocalDateTime now = LocalDateTime.now();
        
        // Busca tokens expirados o ya usados
        var expiredTokens = tokenRepository.findAll().stream()
            .filter(token -> token.isExpired() || token.isUsed())
            .toList();
        
        if (!expiredTokens.isEmpty()) {
            tokenRepository.deleteAll(expiredTokens);
            log.info("Se eliminaron {} tokens expirados o usados", expiredTokens.size());
        } else {
            log.info("No hay tokens expirados para limpiar");
        }
    }
    
    // Opcional: Alertar sobre tokens que expirarán pronto
    @Scheduled(cron = "0 0 */6 * * ?") // Cada 6 horas
    @Transactional
    public void checkExpiringTokens(){
        LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
        
        var expiringSoon = tokenRepository.findAll().stream()
            .filter(token -> !token.isUsed() 
                && !token.isExpired() 
                && token.getExpiryDate().isBefore(oneHourFromNow))
            .toList();
        
        if (!expiringSoon.isEmpty()) {
            log.warn("Hay {} tokens que expirarán en menos de 1 hora", expiringSoon.size());
        }
    }
}