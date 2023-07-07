package com.server.modulequartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "team33.modulecore")
public class ModuleQuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleQuartzApplication.class, args);
    }

}
