package com.team33;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

@SpringBootApplication(
	scanBasePackages = {
		"com.team33.moduleapi",
		"com.team33.modulecore",
		"com.team33.moduleadmin",
		"com.team33.moduleexternalapi",
		"com.team33.moduleevent"
	},
	exclude = QuartzAutoConfiguration.class
)
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
}
