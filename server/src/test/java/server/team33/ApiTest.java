package server.team33;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import server.team33.domain.user.entity.User;
import server.team33.domain.user.repository.UserRepository;
import server.team33.domain.user.service.UserService;
import server.team33.global.auth.security.jwt.JwtTokenProvider;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApiTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    protected String getToken() {
        User loginUser = userService.getLoginUser();
        return "Bearer " + jwtTokenProvider.delegateAccessToken(loginUser);
    }
}
