package com.team33.moduleapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.team33.moduleapi.security.infra.JwtTokenProvider;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.user.domain.entity.User;
import com.team33.modulecore.user.domain.repository.UserRepository;

import io.restassured.RestAssured;


@ActiveProfiles({ "auth", "quartztest"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected UserFindHelper userFindHelper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
    }

    // @AfterEach
    // void tearDown() {
    //     SecurityContextHolder.clearContext();
    //     userRepository.deleteAll();
    // }

    protected String getToken() {
        User loginUser = userFindHelper.findUser(1L);
        return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
    }

}