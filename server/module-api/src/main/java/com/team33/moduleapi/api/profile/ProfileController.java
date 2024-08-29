package com.team33.moduleapi.api.profile;

import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final Environment environment;

    @GetMapping("/profile")
    public String profile() {
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        List<String> prod = List.of("", "real1", "real2");
        String defaultProfile = activeProfiles.isEmpty() ? "default" : activeProfiles.get(0);

        return activeProfiles.stream()
            .filter(prod::contains)
            .findAny()
            .orElse(defaultProfile);
    }
}
