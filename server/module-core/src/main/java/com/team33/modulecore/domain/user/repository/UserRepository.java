package com.team33.modulecore.domain.user.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail( String email);
    Optional<User> findByDisplayName( String displayName);
    Optional<User> findByPhone( String phoneNumber);
}
