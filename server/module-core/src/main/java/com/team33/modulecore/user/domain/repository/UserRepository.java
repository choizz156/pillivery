package com.team33.modulecore.user.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.team33.modulecore.user.domain.entity.User;

public interface UserRepository extends Repository<User, Long> {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByDisplayName(String displayName);

    Optional<User> findByPhone(String phoneNumber);
}
