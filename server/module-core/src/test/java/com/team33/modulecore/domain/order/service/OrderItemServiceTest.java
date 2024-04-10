package com.team33.modulecore.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.item.repository.ItemRepository;
import com.team33.modulecore.domain.order.dto.OrderDto.Post;
import com.team33.modulecore.domain.order.entity.OrderItem;
import com.team33.modulecore.domain.order.value.OrderItemInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {OrderItemService.class, ItemRepository.class})
@EnableJpaRepositories(basePackages = "com.team33.modulecore.domain")
@EntityScan("com.team33.modulecore.domain")
@SpringBootTest
class OrderItemServiceTest {

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ItemRepository itemRepository;

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .build();

    @Transactional
    @DisplayName("단품 구매 시 orderItem 정보를 생성할 수 있다.")
    @Test
    void orderItem_생성() throws Exception {
        //given
        Item item = fixtureMonkey.giveMeBuilder(Item.class)
            .set("itemId", null)
            .set("wishList", new ArrayList<>())
            .set("categories", new ArrayList<>())
            .set("reviews", new ArrayList<>())
            .set("talks", new ArrayList<>())
            .set("nutritionFacts", new ArrayList<>())
            .sample();

        Item entityItem = itemRepository.save(item);
        Post post = fixtureMonkey.giveMeBuilder(Post.class)
            .set("itemId", entityItem.getItemId())
            .set("subscription", false)
            .sample();

        OrderItemInfo orderItemInfo = OrderItemInfo.of(post);

        //when
        List<OrderItem> orderItems =
            orderItemService.getOrderItemSingle(entityItem.getItemId(), orderItemInfo);

        //then
        OrderItem orderItem = orderItems.get(0);

        assertThat(orderItems).hasSize(1);
        assertThat(orderItem.getItem()).isEqualTo(item);
        assertThat(post.getItemId()).isEqualTo(entityItem.getItemId());
        assertThat(post.getQuantity()).isEqualTo(orderItem.getQuantity());
        assertThat(post.getPeriod()).isEqualTo(orderItem.getPeriod());
        assertThat(post.isSubscription()).isFalse();
    }

}