package com.team33.moduleapi.controller;

import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.repository.UserRepository;
import com.team33.modulecore.domain.user.service.UserService;
import com.team33.modulecore.global.security.jwt.JwtTokenProvider;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({"test", "auth", "quartztest"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();
    }

    protected String getToken() {
        User loginUser = userService.getLoginUser();
        return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
    }

}