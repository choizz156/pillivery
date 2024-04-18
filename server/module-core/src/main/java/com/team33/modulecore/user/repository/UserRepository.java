package com.team33.modulecore.user.repository;


import com.team33.modulecore.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDisplayName(String displayName);

    Optional<User> findByPhone(String phoneNumber);

    @Query("SELECT u FROM User u Where u.email = :email OR u.displayName = :displayName OR u.phone = :phoneNumber")
    Optional<User> findExistUser(
        @Param("email") String email,
        @Param("displayName") String displayName,
        @Param("phoneNumber") String phoneNumber
    );
}
