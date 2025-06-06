package com.team33.modulebatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = {
	"com.team33.modulecore.core.order.domain",
	"com.team33.modulecore.core.item.domain",
	"com.team33.modulebatch"
})
public class ModuleBatchApplication {

	public static void main(String[] args) {

		SpringApplication.run(ModuleBatchApplication.class, args);
	}
}
