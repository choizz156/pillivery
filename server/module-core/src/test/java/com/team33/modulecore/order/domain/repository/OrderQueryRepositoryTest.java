package com.team33.modulecore.order.domain.repository;


import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team33.modulecore.MockEntityFactory;
import com.team33.modulecore.common.OrderPageDto;
import com.team33.modulecore.order.domain.Order;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.modulecore.order.domain.OrderStatus;
import com.team33.modulecore.order.dto.OrderFindCondition;
import com.team33.modulecore.order.dto.OrderPageRequest;
import com.team33.modulecore.order.infra.OrderQueryRepositoryImpl;
import com.team33.modulecore.user.domain.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Page;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
class OrderQueryRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private OrderQueryRepository orderQueryRepository;
    private User MOCK_USER;

    @BeforeAll
    void beforeAll() {
        emf = Persistence.createEntityManagerFactory("test");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        orderQueryRepository = new OrderQueryRepositoryImpl(new JPAQueryFactory(em));
        MockEntityFactory mockEntityFactory = MockEntityFactory.of(em);
        mockEntityFactory.persistEntity();
        MOCK_USER = mockEntityFactory.getMockUser();
    }

    @AfterAll
    void afterAll() {
        em.getTransaction().rollback(); // 커넥션 반납용 롤백
        em.close();
        emf.close();
    }



    @DisplayName("유저가 구매한 주문 목록을 조회할 수 있다.")
    @Test
    void 주문_조회() throws Exception {
        //given
        var orderPageDto = new OrderPageDto();
        orderPageDto.setPage(1);
        orderPageDto.setSize(10);

        var orderPageRequest = OrderPageRequest.of(orderPageDto);

        var orderFindCondition =
            OrderFindCondition.to(MOCK_USER, OrderStatus.REQUEST);

        //when
        Page<Order> allOrders =
            orderQueryRepository.searchOrders(orderPageRequest, orderFindCondition);

        //then
        List<Order> content = allOrders.getContent();
        assertThat(content).hasSize(10)
            .isSortedAccordingTo(comparing(Order::getId).reversed());

        assertThat(allOrders.getSize()).isEqualTo(10);
        assertThat(allOrders.getTotalPages()).isEqualTo(2);
    }

    @DisplayName("유저의 정기 주문(구독) 목록을 조회할 수 있다.")
    @Test
    void 정기_주문_목록_조회() throws Exception {
        //given
        var orderPageDto1 = new OrderPageDto();
        orderPageDto1.setPage(1);
        orderPageDto1.setSize(10);

        var orderPageRequest = OrderPageRequest.of(orderPageDto1);

        var orderFindCondition = OrderFindCondition.to(MOCK_USER, OrderStatus.SUBSCRIBE);

        //when
        List<OrderItem> subscriptionOrderItem =
            orderQueryRepository.findSubscriptionOrderItem(orderPageRequest, orderFindCondition);

        //then
        assertThat(subscriptionOrderItem).hasSize(7)
            .extracting("item.title")
            .as("page 1, size 7, offset 0, 내림차순")
            .containsExactly(
                "title16", "title15", "title14", "title13", "title12", "title11", "title10"
            );

    }
}