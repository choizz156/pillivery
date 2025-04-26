package com.team33.modulecore.security.domain;

public interface RefreshTokenRepository {

	void save(String email, String refreshToken);
	String get(String email);
	void delete(String email);
}
