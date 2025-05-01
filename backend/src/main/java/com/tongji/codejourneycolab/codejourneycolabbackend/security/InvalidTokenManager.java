package com.tongji.codejourneycolab.codejourneycolabbackend.security;

public interface InvalidTokenManager {
    // 标记令牌为失效
    void addInvalidToken(String token);

    // 检查令牌是否失效
    boolean isTokenInvalid(String token);
}
