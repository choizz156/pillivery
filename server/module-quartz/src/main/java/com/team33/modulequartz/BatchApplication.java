package com.team33.modulequartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {
	"com.team33.modulequartz",
	"com.team33.modulecore",
	"com.team33.moduleexternalapi",
}
)
public class BatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}
}
