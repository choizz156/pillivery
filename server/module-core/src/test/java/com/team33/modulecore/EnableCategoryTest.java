// package com.team33.modulecore;
//
// import com.team33.modulecore.category.application.CategoryService;
// import com.team33.modulecore.category.domain.CategoryRepository;
// import com.team33.modulecore.config.QueryDslConfig;
// import java.lang.annotation.ElementType;
// import java.lang.annotation.Retention;
// import java.lang.annotation.RetentionPolicy;
// import java.lang.annotation.Target;
// import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
// import org.springframework.boot.autoconfigure.domain.EntityScan;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.ContextConfiguration;
//
// @Retention(RetentionPolicy.RUNTIME)
// @Target(value = ElementType.TYPE)
// @ActiveProfiles("test")
// @EnableJpaRepositories(basePackages = "com.team33.modulecore")
// @EntityScan("com.team33.modulecore")
// @ContextConfiguration(classes = {CategoryService.class, CategoryRepository.class, QueryDslConfig.class})
// @EnableAutoConfiguration
// @SpringBootTest(webEnvironment = WebEnvironment.NONE)
// public @interface EnableCategoryTest {
// }
