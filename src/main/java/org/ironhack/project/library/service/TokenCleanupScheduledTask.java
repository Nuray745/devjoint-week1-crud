package org.ironhack.project.library.service;

import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupScheduledTask.class);

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeExpiredRefreshTokens() {

        log.info("Started cleaning up expired refresh tokens: {}", Instant.now());

        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());

        log.info("Completed cleaning up expired refresh tokens.");
    }
}