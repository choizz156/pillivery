package com.team33.moduleredis.domain;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(String email, String refreshToken);
    Optional<String> get(String email);
    void delete(String email);
} 