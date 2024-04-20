package com.team33.modulecore.domain;

import com.team33.modulecore.user.domain.repository.UserRepository;
import com.team33.modulecore.user.application.DuplicationVerifier;
import com.team33.modulecore.user.application.UserService;
import com.team33.modulecore.config.PasswordConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
@EnableJpaRepositories(basePackages = "com.team33.modulecore")
@EntityScan("com.team33.modulecore")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public @interface EnableUserTest {

}
