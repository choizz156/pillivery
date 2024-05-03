package com.team33.modulecore;

import com.team33.modulecore.cart.repository.CartRepository;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.config.QueryDslConfig;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.item.infra.ItemQueryDslDao;
import com.team33.modulecore.itemcart.repository.ItemCartRepository;
import com.team33.modulecore.order.application.OrderItemService;
import com.team33.modulecore.order.application.OrderQueryServiceTest;
import com.team33.modulecore.order.application.OrderService;
import com.team33.modulecore.order.domain.repository.OrderItemRepository;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.order.infra.OrderQueryDslDao;
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
    ItemCartRepository.class,
    CartRepository.class,
    OrderService.class,
    OrderQueryServiceTest.class,
    QueryDslConfig.class,
    UserFindHelper.class,
    OrderQueryDslDao.class,
    ItemCommandRepository.class,
    ItemQueryDslDao.class
})
@EnableJpaRepositories(basePackages = "com.team33.modulecore")
@EntityScan("com.team33.modulecore")
public @interface EnableOrderTest {

}
