package com.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.team33.modulecore", "com.team33.modulequartz"})
class ModuleQuartzApplicationTests {

    @Test
    void contextLoads() {
    }

}
