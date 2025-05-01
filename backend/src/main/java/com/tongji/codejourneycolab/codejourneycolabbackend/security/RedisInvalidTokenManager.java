package com.tongji.codejourneycolab.codejourneycolabbackend.security;

// !还没写出来
public class RedisInvalidTokenManager implements InvalidTokenManager {
    @Override
    public void addInvalidToken(String token) {

    }

    @Override
    public boolean isTokenInvalid(String token) {
        return false;
    }
}
