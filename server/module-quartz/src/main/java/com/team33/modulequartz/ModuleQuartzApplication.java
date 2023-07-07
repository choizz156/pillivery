package com.team33.modulequartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.team33.modulecore", "com.team33.modulequartz"})
public class ModuleQuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleQuartzApplication.class, args);
    }

}
