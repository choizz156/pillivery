package com.team33.modulecore.domain;

import com.team33.modulecore.domain.user.repository.UserRepository;
import com.team33.modulecore.domain.user.service.DuplicationVerifier;
import com.team33.modulecore.domain.user.service.UserService;
import com.team33.modulecore.global.config.PasswordConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {
    UserRepository.class,
    UserService.class,
    PasswordConfig.class,
    DuplicationVerifier.class
})
@EnableJpaRepositories(basePackages = "com.team33.modulecore.domain")
@EntityScan("com.team33.modulecore.domain")
public @interface EnableUserTest {

}