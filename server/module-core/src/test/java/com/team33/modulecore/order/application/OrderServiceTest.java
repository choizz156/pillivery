package com.team33.modulecore.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.team33.modulecore.MockEntityFactory;
import com.team33.modulecore.common.UserFindHelper;
import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderPrice;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.domain.repository.OrderRepository;
import com.team33.modulecore.order.mock.FakeOrderRepository;
import com.team33.modulecore.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class OrderServiceTest {

    private Item item;
    private User user;
    private MockEntityFactory mockEntityFactory;
    private OrderRepository orderRepository;

    @BeforeAll
    void beforeAll() {
        mockEntityFactory = MockEntityFactory.of();
        item = mockEntityFactory.getMockItem();
        user = mockEntityFactory.getMockUser();
        orderRepository = new FakeOrderRepository();
    }

    @AfterAll
    void afterAll() {
        orderRepository = null;
    }

    @DisplayName("단건 order 객체를 생성할 수 있다.")
    @Test
    void callOrderTest() throws Exception {

        //given
        List<OrderItem> orderItems = mockEntityFactory.getMockOrderItems();
        UserFindHelper userFindHelper = mock(UserFindHelper.class);
        given(userFindHelper.findUser(anyLong())).willReturn(user);
        OrderService orderService =
            new OrderService(orderRepository, null, null, userFindHelper);

        //when
        Order order = orderService.callOrder(orderItems, false, 1L);

        //then

        assertThat(order.getOrderPrice()).isEqualTo(new OrderPrice(orderItems));
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REQUEST);
        assertThat(order.getOrderItems()).hasSize(3);
        assertThat(order.getTotalItems()).isEqualTo(3);
        assertThat(order.getUser()).isEqualTo(user);
    }

    @DisplayName("주문 상태를 구독 중으로 바꿀 수 있다.")
    @Test
    void 주문_상태_변경1() throws Exception {
        //given
        Order order = orderRepository.save(mockEntityFactory.getMockOrder());
        OrderService orderService =
            new OrderService(orderRepository, null, null, mock(UserFindHelper.class));

        //when
        orderService.changeOrderStatusToSubscribe(order.getId());

        //then
        assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.SUBSCRIBE);
    }
//
//    @DisplayName("주문 상태를 주문 완료로 바꿀 수 있다.")
//    @Test
//    void 주문_상태_변경2() throws Exception {
//        //given
//        OrderItem sampleOrderItem = getOrderItem();
//
//        Order order = orderService.callOrder(List.of(sampleOrderItem), true, user.getId());
//        //when
//        orderService.changeOrderStatusToComplete(order.getId());
//
//        //then
//        assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.COMPLETE);
//    }
//
//    @DisplayName("정기 구독 아이템의 수량을 조정할 수 있다.")
//    @Test
//    void 정기_구독_수량_조정() throws Exception {
//        //given
//        OrderItem sampleOrderItem = getOrderItem();
//
//        Order order = orderService.callOrder(List.of(sampleOrderItem), true, user.getId());
//
//        //when
//        orderService.changeSubscriptionItemQuantity(order.getId(),
//            order.getOrderItems().get(0).getId(), 2);
//
//        //then
//        assertThat(order.getOrderItems()).hasSize(1)
//            .extracting("quantity")
//            .containsExactly(2);
//    }
//
//    private OrderItem getOrderItem() {
//        OrderItem sampleOrderItem = fixtureMonkey.giveMeBuilder(OrderItem.class)
//            .set("id", null)
//            .set("item", item)
//            .set("quantity", 3)
//            .sample();
//        return sampleOrderItem;
//    }
//
//    @DisplayName("주문을 취소할 수 있다.")
//    @Test
//    void 주문_취소() throws Exception {
//        //given
//        OrderItem sampleOrderItem = getOrderItem();
//
//        Order order = orderService.callOrder(List.of(sampleOrderItem), true, user.getId());
//
//        //when
//        orderService.cancelOrder(order.getId());
//
//        //then
//        assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.CANCEL);
//    }
//
//    private User getUser() {
//        User userSample = fixtureMonkey.giveMeBuilder(User.class)
//            .set("id", null)
//            .set("wishList", new ArrayList<>())
//            .set("displayName", "test")
//            .set("cart", null)
//            .sample();
//
//        return userRepository.save(userSample);
//    }
//
//    private List<OrderItem> getOrderItems(Item item) {
//        var dto = OrderItemServiceDto.builder()
//            .isSubscription(false)
//            .itemId(item.getId())
//            .period(30)
//            .quantity(3)
//            .build();
//
//        return orderItemService.getOrderItemSingle(dto);
//    }
//
//    private Item getItem() {
//        Item sampleItem = fixtureMonkey.giveMeBuilder(Item.class)
//            .set("id", null)
//            .set("wishList", new ArrayList<>())
//            .set("itemCategories", new HashSet<>())
//            .set("reviews", new ArrayList<>())
//            .set("nutritionFacts", new ArrayList<>())
//            .sample();
//
//        return itemCommandRepository.save(sampleItem);
//    }
}