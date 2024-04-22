package com.team33.modulecore;

import com.team33.modulecore.cart.repository.CartRepository;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.config.QueryDslConfig;
import com.team33.modulecore.item.repository.ItemRepository;
import com.team33.modulecore.itemcart.repository.ItemCartRepository;
import com.team33.modulecore.order.application.OrderQueryService;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.repository.OrderRepository;
import com.team33.modulecore.orderitem.application.OrderItemService;
import com.team33.modulecore.orderitem.repository.OrderItemRepository;
import com.team33.modulecore.user.domain.repository.UserRepository;
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
@ContextConfiguration( classes = {
    UserRepository.class,
    OrderItemRepository.class,
    OrderRepository.class,
    OrderItemService.class,
    ItemRepository.class,
    ItemCartRepository.class,
    CartRepository.class,
    OrderService.class,
    OrderItemService.class,
    OrderQueryService.class,
    QueryDslConfig.class,
    UserFindHelper.class
})
@EnableJpaRepositories(basePackages = "com.team33.modulecore")
@EntityScan("com.team33.modulecore")
public @interface EnableOrderTest {

}