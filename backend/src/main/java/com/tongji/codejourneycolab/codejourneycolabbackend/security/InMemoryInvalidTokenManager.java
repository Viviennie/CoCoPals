package com.tongji.codejourneycolab.codejourneycolabbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryInvalidTokenManager implements InvalidTokenManager {
    private final ConcurrentMap<String, Long> invalidTokens = new ConcurrentHashMap<>();

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void addInvalidToken(String token) {
        invalidTokens.put(token,jwtTokenProvider.tryGetExpirationDateFromToken(token).getTime());
    }

    @Override
    public boolean isTokenInvalid(String token) {
        Long expirationTime = invalidTokens.get(token);

        if(expirationTime == null) {
            return false;
        }

        if (System.currentTimeMillis() > expirationTime) {
            invalidTokens.remove(token);
            return false;
        }
        return true;
    }

    // 每10分钟清理一次过期的记录
    @Scheduled(fixedRate = 600000)
    public void removeExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        invalidTokens.entrySet().removeIf(entry -> currentTime > entry.getValue());
    }
}