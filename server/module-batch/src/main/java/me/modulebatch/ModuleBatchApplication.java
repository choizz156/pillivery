package me.modulebatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

@SpringBootApplication(
	scanBasePackages = {
		"com.team33.modulecore",
		"com.team33.moduleexternalapi",
	}
)
public class ModuleBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleBatchApplication.class, args);
	}

}
