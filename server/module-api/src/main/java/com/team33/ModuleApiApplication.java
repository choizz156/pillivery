package com.team33;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableJpaAuditing
@EnableAsync
@SpringBootApplication(scanBasePackages = {
	"com.team33.moduleapi",
	"com.team33.modulequartz",
	"com.team33.modulecore",
	"com.team33.moduleadmin",
	"com.team33.moduleexternalapi",
	"com.team33.moduleevent",
	"com.team33.modulelogging"
}
)
public class ModuleApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleApiApplication.class, args);
	}
}
