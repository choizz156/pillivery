package com.team33.moduleapi.api.payment;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.order.dto.OrderPostDto;
import com.team33.moduleapi.api.order.dto.OrderPostListDto;
import com.team33.moduleapi.api.order.mapper.OrderItemMapper;
import com.team33.moduleapi.api.payment.mapper.PaymentDataMapper;
import com.team33.moduleapi.api.payment.mapper.PaymentMapper;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Price;
import com.team33.modulecore.core.item.domain.Statistic;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;
import com.team33.modulecore.core.order.application.OrderCreateService;
import com.team33.modulecore.core.order.application.OrderItemService;
import com.team33.modulecore.core.order.application.OrderPaymentCodeService;
import com.team33.modulecore.core.order.application.OrderStatusService;
import com.team33.modulecore.core.order.application.SubscriptionOrderService;
import com.team33.modulecore.core.order.domain.OrderStatus;
import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;
import com.team33.modulecore.core.order.domain.repository.OrderCommandRepository;
import com.team33.modulecore.core.order.dto.OrderContext;
import com.team33.modulecore.core.order.dto.OrderItemServiceDto;
import com.team33.modulecore.core.payment.kakao.application.KakaoPaymentFacade;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoRequestResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentAcceptanceTest extends ApiTest {

	private KakaoPaymentFacade approveFacade;
	private MockMvcRequestSpecification given;

	@Autowired
	private OrderCommandRepository orderCommandRepository;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentDataMapper paymentDataMapper;

	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private OrderPaymentCodeService paymentCodeService;

	@Autowired
	private SubscriptionOrderService subscriptionOrderService;

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private OrderCreateService orderCreateService;

	@BeforeAll
	void beforeAll() {

		approveFacade = mock(KakaoPaymentFacade.class);

		given = RestAssuredMockMvc.given()
			.mockMvc(
				standaloneSetup(
					new PayController(
						approveFacade,
						paymentMapper,
						paymentDataMapper,
						orderStatusService,
						paymentCodeService,
						subscriptionOrderService
					)
				).build()
			).log().all();

	}

	@BeforeEach
	void setUp() {

		Order order = FixtureMonkeyFactory.get().giveMeBuilder(Order.class)
			.setNull("id")
			.setNull("orderItems")
			.sample();

		orderCommandRepository.save(order);
	}

	@DisplayName("카카오 단건 결제 요청을 보내면 tid, redirect_url 등을 받을 수 있다.")
	@Test
	void 결제_요청() throws Exception {

		//given
		KakaoRequestResponse sample = FixtureMonkeyFactory.get()
			.giveMeBuilder(KakaoRequestResponse.class)
			.set("tid", "tid")
			.set("next_redirect_pc_url", "url")
			.sample();

		given(approveFacade.request(anyLong())).willReturn(sample);

		//@formatter:off
            given
            .when()
                    .post("/api/payments/{orderId}",1)
            .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .expect(jsonPath("$.data.tid").value("tid"))
                    .expect(jsonPath("$.data.next_redirect_pc_url").value("url"))
                    .expect(jsonPath("$.data.createdAt").isNotEmpty())
                    .log().all();
        //@formatter:on
	}

	@DisplayName("카카오 정기 결제 요청을 보내면 tid, redirect_url 등을 받을 수 있다.")
	@Test
	void 결제_요청2() throws Exception {

		//given
		KakaoRequestResponse sample = FixtureMonkeyFactory.get()
			.giveMeBuilder(KakaoRequestResponse.class)
			.set("tid", "tid")
			.set("next_redirect_pc_url", "url")
			.sample();

		given(approveFacade.request(anyLong())).willReturn(sample);

		//@formatter:off
		given
			.when()
			.post("/api/payments/{orderId}",1)
			.then()
			.statusCode(HttpStatus.CREATED.value())
			.expect(jsonPath("$.data.tid").value("tid"))
			.expect(jsonPath("$.data.next_redirect_pc_url").value("url"))
			.expect(jsonPath("$.data.createdAt").isNotEmpty())
			.log().all();
		//@formatter:on
	}

	@DisplayName("카카오 단건, 정기 결제(최초) 승인 시 결제 승인 정보를 받을 수 있다.")
	@Test
	void 결제_승인() throws Exception {
		//given
		KakaoApproveResponse kakaoApproveResponse = FixtureMonkeyFactory.get().giveMeOne(KakaoApproveResponse.class);

		given(approveFacade.approveInitially(any(KakaoApproveRequest.class))).willReturn(kakaoApproveResponse);

		paymentDataMapper.addData(1L, "tid");

		//@formatter:off
            given
                    .param("pg_token", "pgtoken")
            .when()
                    .get("/api/payments/approve/{orderId}",1)
            .then()
                    .log().all()
				.expect(jsonPath("$.data.item_name").exists())
				.expect(jsonPath("$.data.item_code").exists())
				.expect(jsonPath("$.data.created_at").exists())
				.expect(jsonPath("$.data.approved_at").exists())
				.expect(jsonPath("$.data.payload").exists())
				.expect(jsonPath("$.data.amount.total").exists())
				.expect(jsonPath("$.data.amount.tax_free").exists())
				.expect(jsonPath("$.data.amount.vat").exists())
				.expect(jsonPath("$.data.amount.discount").exists())
				.expect(jsonPath("$.data.quantity").exists());
            //@formatter:on
	}

	@DisplayName("정기 결제 승인(두 번째 이후) 응답을 받을 수 있다.")
	@Transactional
	@Test
	void test3() throws Exception {
		//given
		주문_저장(주문_정보(true, 30), OrderStatus.SUBSCRIPTION);
		KakaoApproveResponse kakaoApproveResponse = FixtureMonkeyFactory.get().giveMeOne(KakaoApproveResponse.class);

		given(approveFacade.approveSubscription(any(SubscriptionOrder.class))).willReturn(kakaoApproveResponse);

		//@formatter:off
            given
            .when()
                    .post("/api/payments/approve/subscriptions/{orderId}", 1)
            .then()
					.log().all()
				.statusCode(HttpStatus.OK.value())
				.expect(jsonPath("$.data.item_name").exists())
				.expect(jsonPath("$.data.item_code").exists())
				.expect(jsonPath("$.data.created_at").exists())
				.expect(jsonPath("$.data.approved_at").exists())
				.expect(jsonPath("$.data.payload").exists())
				.expect(jsonPath("$.data.amount.total").exists())
				.expect(jsonPath("$.data.amount.tax_free").exists())
				.expect(jsonPath("$.data.amount.vat").exists())
				.expect(jsonPath("$.data.amount.discount").exists())
				.expect(jsonPath("$.data.quantity").exists());

		//@formatter:on
	}

	private void 주문_저장(OrderPostListDto postListDto, OrderStatus orderStatus) {

		아이템_저장("16종혼합유산균 디에스", CategoryName.INTESTINE);
		아이템_저장("종혼합유산균 디에스2", CategoryName.EYE);

		List<OrderItemServiceDto> orderItemPostDto =
			orderItemMapper.toOrderItemPostDto(postListDto.getOrderPostDtoList());
		OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
		List<OrderItem> orderItems = orderItemService.convertToOrderItems(orderItemPostDto);

		Order order = orderCreateService.callOrder(orderItems, orderContext);
		order.changeOrderStatus(orderStatus);
		orderCommandRepository.save(order);

		if (postListDto.isSubscription()) {
			subscriptionOrderService.create(order);
		}
	}

	private OrderPostListDto 주문_정보(boolean subscription, int period) {

		return OrderPostListDto.builder()
			.userId(1L)
			.subscription(subscription)
			.orderedAtCart(false)
			.city("서울")
			.detailAddress("101 번지")
			.realName("홍길동")
			.phoneNumber("010-1111-1111")
			.orderPostDtoList(List.of(
					OrderPostDto.builder()
						.itemId(1L)
						.period(period)
						.quantity(1)
						.subscription(subscription)
						.build(),
					OrderPostDto.builder()
						.itemId(2L)
						.period(period)
						.quantity(1)
						.subscription(subscription)
						.build()
				)
			)
			.build();
	}

	private void 아이템_저장(
		String productName,
		CategoryName categoryName
	) {

		Information information = Information.builder()
			.enterprise("(주)씨티씨바이오")
			.productName(productName)
			.statementNumber("20040017059225")
			.registeredDate("20220225")
			.distributionPeriod("제조일로부터 24개월까지")
			.sungsang("고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말")
			.servingUse("건강기능식품 원료로 사용")
			.preservePeriod("냉장보관(10℃이하)")
			.intake("- 질환이 있거나 의약품 복용 시 전문가와 상담할 것\n" +
				"- 알레르기 체질 등은 개인에 따라 과민반응을 나타낼 수 있음\n" +
				"- 어린이가 함부로 섭취하지 않도록 일일섭취량 방법을 지도할 것\n" +
				"- 이상사례 발생 시 섭취를 중단하고 전문가와 상담할 것\n" +
				"- 원료로 사용 시 개봉 후 오염 우려가 있으니 신속하게 사용하고 남은 것은 밀봉 후 냉장보관할 것")
			.mainFunction("[프로바이오틱스] 유산균 증식 및 유해균 억제･배변활동 원활･장 건강에 도움을 줄 수 있음")
			.baseStandard("1. 성상 : 고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말\n" +
				"2. 프로바이오틱스 수 : 200,000,000,000(2000억) CFU/g 이상\n" +
				"3. 대장균군 : 음성\n" +
				"4. 납(mg/kg) : 1.0 이하\n" +
				"5. 카드뮴(mg/kg) : 0.3 이하")
			.image(new Image("thumbnailUrl", "descriptionImage"))
			.price(new Price(10000, 0.0))
			.build();

		Item item = Item.builder()
			.information(information)
			.statistics(new Statistic())
			.build();

		item.addIncludedCategories(Set.of(categoryName));
		item.getItemCategory().add(categoryName);

		itemCommandRepository.save(item);
	}
}
