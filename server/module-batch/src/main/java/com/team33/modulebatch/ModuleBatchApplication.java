package com.team33.modulebatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	"com.team33.modulecore.core.order.domain",
	"com.team33.modulecore.core.item.domain",
	"com.team33.modulebatch"
})
public class ModuleBatchApplication {

	public static void main(String[] args) {

		SpringApplication.run(ModuleBatchApplication.class, args);
	}
}
