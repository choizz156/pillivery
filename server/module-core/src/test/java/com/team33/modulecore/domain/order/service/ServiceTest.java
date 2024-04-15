package com.team33.modulecore.domain.order.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.domain.EnableDomainTest;
import com.team33.modulecore.domain.cart.repository.CartRepository;
import com.team33.modulecore.domain.cart.repository.ItemCartRepository;
import com.team33.modulecore.domain.item.repository.ItemRepository;
import com.team33.modulecore.domain.order.repository.OrderRepository;
import com.team33.modulecore.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@EnableDomainTest
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class ServiceTest {

    final FixtureMonkey fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemCartRepository itemCartRepository;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    CartRepository cartRepository;
}
