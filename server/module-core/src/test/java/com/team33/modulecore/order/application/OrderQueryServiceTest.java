package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.common.OrderPageDto;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.repository.OrderQueryRepository;
import com.team33.modulecore.order.dto.OrderItemSimpleResponse;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.order.mock.FakeOrderQueryDslDao;
import com.team33.modulecore.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Page;

@TestInstance(Lifecycle.PER_CLASS)
public class OrderQueryServiceTest {

    private User user;
    private OrderQueryRepository orderRepository;

    @BeforeAll
    void beforeAll() {
        user = getMockUser();
        orderRepository = new FakeOrderQueryDslDao();
    }

    @DisplayName("유저의 전체 주문 목록을 조회할 수 있다.")
    @Test
    void 주문_목록_조회() throws Exception {
        //given

        var userFindHelper = mock(UserFindHelper.class);
        given(userFindHelper.findUser(anyLong())).willReturn(user);

        OrderQueryService orderQueryService =
            new OrderQueryService(orderRepository, null, userFindHelper);

        var orderPageDto = new OrderPageDto();
        orderPageDto.setPage(1);
        orderPageDto.setSize(10);

        var orderPageRequest = OrderPageRequest.of(orderPageDto);
        //when
        Page<Order> allOrders = orderQueryService.findAllOrders(1L, orderPageRequest);

        //then
        List<Order> content = allOrders.getContent();
        assertThat(allOrders.getSize()).isEqualTo(10);
        assertThat(allOrders.getTotalPages()).isEqualTo(2);
        assertThat(allOrders.getTotalElements()).isEqualTo(14);
        assertThat(allOrders.getNumberOfElements()).isEqualTo(10);
    }

    @DisplayName("유저의 정기 주문(구독) 목록을 조회할 수 있다.")
    @Test
    void 정기_주문_목록_조회() throws Exception {

        //given
        var userFindHelper = mock(UserFindHelper.class);
        given(userFindHelper.findUser(anyLong())).willReturn(user);

        OrderQueryService orderQueryService =
            new OrderQueryService(orderRepository, null, userFindHelper);

        var orderPageDto = new OrderPageDto();
        orderPageDto.setPage(1);
        orderPageDto.setSize(5);

        var orderPageRequest = OrderPageRequest.of(orderPageDto);

        //when
        List<OrderItemSimpleResponse> allSubscriptions1 =
            orderQueryService.findAllSubscriptions(1L, orderPageRequest);

        //then
        assertThat(allSubscriptions1).hasSize(7);

    }

    private User getMockUser() {
        return FixtureMonkeyFactory.get().giveMeBuilder(User.class)
            .set("id", null)
            .set("cart", null)
            .sample();
    }
}
