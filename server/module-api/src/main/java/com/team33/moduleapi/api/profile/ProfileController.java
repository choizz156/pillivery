package com.team33.moduleapi.api.profile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
    private final Environment environment;

    @GetMapping("/profile")
    public String profile() {
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        List<String> prod = List.of("", "prod1", "prod2");
        String defaultProfile = activeProfiles.isEmpty() ? "local" : activeProfiles.get(0);

        LOGGER.info("Profile: {}", defaultProfile);
        return activeProfiles.stream()
                .filter(prod::contains)
                .findAny()
                .orElse(defaultProfile);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
