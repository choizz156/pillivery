package com.team33.modulecore.order.application;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.EnableOrderTest;
import com.team33.modulecore.cart.repository.CartRepository;
import com.team33.modulecore.itemcart.repository.ItemCartRepository;
import com.team33.modulecore.item.domain.repository.ItemRepository;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@EnableOrderTest
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class OrderDomainHelper {

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
