package com.team33.modulecore.security.repository;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(String email, String refreshToken);
    Optional<String> get(String email);
}
