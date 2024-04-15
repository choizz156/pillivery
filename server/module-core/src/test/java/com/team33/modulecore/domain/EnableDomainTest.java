package com.team33.modulecore.domain;

import com.team33.modulecore.domain.cart.repository.CartRepository;
import com.team33.modulecore.domain.cart.repository.ItemCartRepository;
import com.team33.modulecore.domain.item.repository.ItemRepository;
import com.team33.modulecore.domain.order.repository.OrderItemRepository;
import com.team33.modulecore.domain.order.repository.OrderRepository;
import com.team33.modulecore.domain.order.service.OrderItemService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.user.repository.UserRepository;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {
    UserRepository.class,
    OrderItemRepository.class,
    OrderRepository.class,
    OrderItemService.class,
    ItemRepository.class,
    ItemCartRepository.class,
    CartRepository.class,
    OrderService.class,
    OrderItemService.class
})
@EnableJpaRepositories(basePackages = "com.team33.modulecore.domain")
@EntityScan("com.team33.modulecore.domain")
public @interface EnableDomainTest {

}
