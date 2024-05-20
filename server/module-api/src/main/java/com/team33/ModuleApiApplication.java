package com.team33;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.team33.modulecore.item.infra.ItemQueryDslDao;
import com.team33.modulecore.order.infra.OrderQueryDslDao;
import com.team33.modulecore.review.infra.ReviewQueryDslDao;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
	"com.team33.moduleapi",
	"com.team33.modulequartz",
	"com.team33.modulecore",
	"com.team33.moduleadmin",
	"com.team33.moduleexternalapi"
}
)
@Import({ItemQueryDslDao.class, OrderQueryDslDao.class, ReviewQueryDslDao.class})
public class ModuleApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleApiApplication.class, args);
	}
}
