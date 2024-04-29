package com.team33.modulecore.order.domain.repository;


import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;
import com.team33.modulecore.EntityManagerSetting;
import com.team33.modulecore.common.OrderPageDto;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.mock.FakeOrderQueryRepository;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;


/**
 * {@linkplain FakeOrderQueryRepository}는 {@linkplain OrderQueryRepositoryImpl}과 로직이 동일한 가짜 대역
 * 리포지토리입니다. {@code FakeOrderQueryRepository}에서 발견된 버그나 실패한 테스트에 대한 수정 사항은, 로직의 일관성을 유지하기 위해
 * {@code OrderQueryRepositoryImpl}에도 반영되어야 합니다.
 */
class OrderQueryRepositoryTest extends EntityManagerSetting {

    private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .plugin(new JavaxValidationPlugin())
        .build();

    private final User user = getMockUser();

    @Test
    void 아이템_조회 () throws Exception {
        //given
        var orderPageDto = new OrderPageDto();
        orderPageDto.setPage(1);
        orderPageDto.setSize(5);

        var orderPageRequest = OrderPageRequest.of(orderPageDto);

        OrderFindCondition orderFindCondition = OrderFindCondition.to(user, OrderStatus.REQUEST);
        OrderQueryRepository orderQueryRepository = new FakeOrderQueryRepository(user, getEmAtSuperClass());

        //when
        Page<Order> allOrders = orderQueryRepository.searchOrders(orderPageRequest, orderFindCondition);

        //then
        List<Order> content = allOrders.getContent();

        assertThat(content).hasSize(6)
            .isSortedAccordingTo(comparing(Order::getId).reversed())
                .extracting("user")
                    .containsExactlyInAnyOrder(user,user,user,user,user,user);

        assertThat(allOrders.getSize()).isEqualTo(7);
        assertThat(allOrders.getTotalPages()).isEqualTo(1);
        assertThat(allOrders.getNumberOfElements()).isEqualTo(6);
    }

    private User getMockUser() {
        return FIXTURE_MONKEY.giveMeBuilder(User.class)
            .set("id", null)
            .set("wishList", null)
            .set("cart", null)
            .sample();
    }
}