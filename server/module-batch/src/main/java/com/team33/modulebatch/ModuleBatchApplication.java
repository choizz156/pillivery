package com.team33.modulebatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
	scanBasePackages = {
		"com.team33.modulecore.core.order.domain",
		"com.team33.modulebatch"
	}
	// exclude = {
	// 	BatchAutoConfiguration.class,
	// 	JmxAutoConfiguration.class
	// },
	// excludeName = {
	// 	"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
	// }
)
public class ModuleBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleBatchApplication.class, args);
	}

}
