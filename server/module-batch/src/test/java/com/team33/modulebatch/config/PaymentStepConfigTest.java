package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.team33.modulebatch.DataCleaner;
import com.team33.modulebatch.FixtureMonkeyFactory;
import com.team33.modulebatch.OrderVO;
import com.team33.modulecore.config.redis.EmbededRedisConfig;

import com.navercorp.fixturemonkey.FixtureMonkey;

// @EnableJpaRepositories(
// 	basePackages = {"com.team33.modulecore"}
// )
// @EntityScan(basePackages = {"com.team33.modulecore"})
@SpringBatchTest
@SpringBootTest(classes = {PaymentStepConfig.class, PaymentJobConfig.class, EmbededRedisConfig.class,
	DataCleaner.class})
@EnableAutoConfiguration
@EnableBatchProcessing
@ActiveProfiles("test")
	// @Sql("classpath:data.sql")
class PaymentStepConfigTest {

	private static final ZonedDateTime REQUEST_DATE = ZonedDateTime.now();
	private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkeyFactory.get();

	@Autowired
	private PaymentStepConfig paymentStepConfig;

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataCleaner dataCleaner;

	@AfterEach
	void tearDown() {
		dataCleaner.execute();
	}

	@BeforeEach
	void setUpEach() throws Exception {
		dataCleaner.afterPropertiesSet();
		jdbcTemplate = new JdbcTemplate(dataSource);
		insertTestData(100);
	}

	private void insertTestData(int count) {
		LongStream.rangeClosed(1, count).forEach(orderId -> {
			boolean isSubscription = orderId % 2 == 0;

			OrderVO order = FIXTURE_MONKEY.giveMeBuilder(OrderVO.class)
				.set("orderId", orderId)
				.set("isSubscription", isSubscription)
				.sample();

			jdbcTemplate.update(
				"INSERT INTO orders (id, is_subscription) VALUES (?, ?)",
				order.getOrderId(),
				order.isSubscription()
			);

			ZonedDateTime paymentDate = orderId % 5 == 0 ?
				FIXTURE_MONKEY.giveMeOne(ZonedDateTime.class) :
				REQUEST_DATE;

			jdbcTemplate.update(
				"INSERT INTO order_item (order_id, payment_date) VALUES (?, ?)",
				order.getOrderId(),
				java.sql.Date.valueOf(paymentDate.toLocalDate())
			);
		});
	}

	@Test
	void testSubscriptionOrderReader() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addDate("paymentDate", Date.from(REQUEST_DATE.toInstant()))
			.toJobParameters();

		jobLauncherTestUtils.launchJob( jobParameters);

		int validCount = 0;
		ItemReader<OrderVO> orderVOItemReader = paymentStepConfig.itemReader(jobParameters.getDate("paymentDate"));
		while ((orderVOItemReader.read()) != null) {
			validCount++;
		}


		long expectedCount = IntStream.rangeClosed(1, 100)
			.filter(orderId -> orderId % 2 == 0) // isSubscription=true (50개)
			.filter(orderId -> orderId % 5 != 0) // paymentDate=REQUEST_DATE (40개)
			.count();

		assertThat(validCount).isEqualTo(expectedCount);

		// Integer orderCount = jdbcTemplate.queryForObject(
		// 	"SELECT COUNT(*) FROM orders", Integer.class
		// );
		// assertThat(orderCount).isEqualTo(100);
		// Integer subscriptionCount = jdbcTemplate.queryForObject(
		// 	"SELECT COUNT(*) FROM orders WHERE is_subscription = true",
		// 	Integer.class
		// );
		// assertThat(subscriptionCount).isEqualTo(50);
		//
		// java.sql.Date date = java.sql.Date.valueOf(REQUEST_DATE.toLocalDate());
		// Integer validItemCount = jdbcTemplate.queryForObject(
		// 	" SELECT COUNT(*) FROM order_item oi INNER JOIN orders o ON oi.order_id = o.id WHERE o.is_subscription = true AND oi.payment_date = ? ",
		// 	Integer.class,
		// 	date
		// );
		// assertThat(validItemCount).isEqualTo(40);

	}
	//
	// private void 주문_저장(OrderPostListDto postListDto, OrderStatus orderStatus) {
	// 	아이템_저장("16종혼합유산균 디에스", CategoryName.INTESTINE);
	// 	아이템_저장("종혼합유산균 디에스2", CategoryName.EYE);
	//
	// 	List<OrderItemServiceDto> orderItemPostDto =
	// 		orderItemMapper.toOrderItemPostDto(postListDto.getOrderPostDtoList());
	// 	OrderContext orderContext = orderItemMapper.toOrderContext(postListDto);
	// 	List<OrderItem> orderItems = orderItemService.toOrderItems(orderItemPostDto);
	//
	// 	Order order = orderCreateService.callOrder(orderItems, orderContext);
	//
	// 	order.changeOrderStatus(orderStatus);
	// 	orderCommandRepository.save(order);
	// }
	//
	// private OrderPostListDto 주문_정보(boolean subscription, int period) {
	//
	// 	return OrderPostListDto.builder()
	// 		.userId(1L)
	// 		.subscription(subscription)
	// 		.orderedAtCart(false)
	// 		.city("서울")
	// 		.detailAddress("101 번지")
	// 		.realName("홍길동")
	// 		.phoneNumber("010-1111-1111")
	// 		.orderPostDtoList(List.of(
	// 				OrderPostDto.builder()
	// 					.itemId(1L)
	// 					.period(period)
	// 					.quantity(1)
	// 					.subscription(subscription)
	// 					.build(),
	// 				OrderPostDto.builder()
	// 					.itemId(2L)
	// 					.period(period)
	// 					.quantity(1)
	// 					.subscription(subscription)
	// 					.build()
	// 			)
	// 		)
	// 		.build();
	// }
	//
	// private void 아이템_저장(
	// 	String productName,
	// 	CategoryName categoryName
	// ) {
	// 	Information information = Information.builder()
	// 		.enterprise("(주)씨티씨바이오")
	// 		.productName(productName)
	// 		.statementNumber("20040017059225")
	// 		.registeredDate("20220225")
	// 		.distributionPeriod("제조일로부터 24개월까지")
	// 		.sungsang("고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말")
	// 		.servingUse("건강기능식품 원료로 사용")
	// 		.preservePeriod("냉장보관(10℃이하)")
	// 		.intake("- 질환이 있거나 의약품 복용 시 전문가와 상담할 것\n" +
	// 			"- 알레르기 체질 등은 개인에 따라 과민반응을 나타낼 수 있음\n" +
	// 			"- 어린이가 함부로 섭취하지 않도록 일일섭취량 방법을 지도할 것\n" +
	// 			"- 이상사례 발생 시 섭취를 중단하고 전문가와 상담할 것\n" +
	// 			"- 원료로 사용 시 개봉 후 오염 우려가 있으니 신속하게 사용하고 남은 것은 밀봉 후 냉장보관할 것")
	// 		.mainFunction("[프로바이오틱스] 유산균 증식 및 유해균 억제･배변활동 원활･장 건강에 도움을 줄 수 있음")
	// 		.baseStandard("1. 성상 : 고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말\n" +
	// 			"2. 프로바이오틱스 수 : 200,000,000,000(2000억) CFU/g 이상\n" +
	// 			"3. 대장균군 : 음성\n" +
	// 			"4. 납(mg/kg) : 1.0 이하\n" +
	// 			"5. 카드뮴(mg/kg) : 0.3 이하")
	// 		.image(new Image("thumbnailUrl", "descriptionImage"))
	// 		.price(new Price(10000, 0))
	// 		.build();
	//
	// 	Item item = Item.builder()
	// 		.information(information)
	// 		.statistics(new Statistic())
	// 		.build();
	//
	// 	item.addIncludedCategories(Set.of(categoryName));
	// 	item.getItemCategory().add(categoryName);
	//
	// 	itemCommandRepository.save(item);
	// }

}