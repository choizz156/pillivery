package com.team33.moduleapi.security.refreshtoken;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(String email, String refreshToken);
    Optional<String> get(String email);
}
