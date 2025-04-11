package com.team33.moduleapi.ui.item;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.team33.moduleapi.ApiTest;
import com.team33.moduleapi.FixtureMonkeyFactory;
import com.team33.moduleapi.api.item.dto.ItemPageRequestDto;
import com.team33.modulecore.core.category.domain.Categories;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Price;
import com.team33.modulecore.core.item.domain.Statistic;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;

class ItemQueryAcceptanceTest extends ApiTest {

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@DisplayName("메인 창의 띄울 아이템을 조회하여 api 응답으로 보낼 수 있다.")
	@Test
	void 메인_아이템() throws Exception {
		//given
		메인_아이템_삽입();

		//when
		given()
			.log().all()
			.when()
			.get("/api/items/main")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.discountRateItem.size()", is(9))
			.body("data.saleItem.size()", is(9));
	}

	@DisplayName("특정 아이템을 상세 조회하여 api 응답으로 보낼 수 있다.")
	@Test
	void 상세_아이템_조회() throws Exception {

		아이템_저장("16종혼합유산균 디에스", 10000, 10.0, CategoryName.INTESTINE, "(주)씨티씨바이오");

		//when
		given()
			.log().all()
			.when()
			.get("/api/items/1")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.itemId", equalTo(1))
			.body("data.thumbnail", equalTo("thumbnailUrl"))
			.body("data.descriptionImage", equalTo("descriptionImage"))
			.body("data.enterprise", equalTo("(주)씨티씨바이오"))
			.body("data.productName", equalTo("16종혼합유산균 디에스"))
			.body("data.statementNumber", equalTo("20040017059225"))
			.body("data.registeredDate", equalTo("20220225"))
			.body("data.distributionPeriod", equalTo("제조일로부터 24개월까지"))
			.body("data.sungsang", equalTo("고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말"))
			.body("data.servingUse", equalTo("건강기능식품 원료로 사용"))
			.body("data.preservePeriod", equalTo("냉장보관(10℃이하)"))
			.body("data.intake", equalTo(
				"- 질환이 있거나 의약품 복용 시 전문가와 상담할 것\n- 알레르기 체질 등은 개인에 따라 과민반응을 나타낼 수 있음\n- 어린이가 함부로 섭취하지 않도록 일일섭취량 방법을 지도할 것\n- 이상사례 발생 시 섭취를 중단하고 전문가와 상담할 것\n- 원료로 사용 시 개봉 후 오염 우려가 있으니 신속하게 사용하고 남은 것은 밀봉 후 냉장보관할 것"))
			.body("data.mainFunction", equalTo("[프로바이오틱스] 유산균 증식 및 유해균 억제･배변활동 원활･장 건강에 도움을 줄 수 있음"))
			.body("data.baseStandard", equalTo(
				"1. 성상 : 고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말\n2. 프로바이오틱스 수 : 200,000,000,000(2000억) CFU/g 이상\n3. 대장균군 : 음성\n4. 납(mg/kg) : 1.0 이하\n5. 카드뮴(mg/kg) : 0.3 이하"))
			.body("data.realPrice", equalTo(9000))
			.body("data.originPrice", equalTo(10000))
			.body("data.discountRate", equalTo(0.1f))
			.body("data.discountPrice", equalTo(1000))
			.body("data.sales", equalTo(0))
			.body("data.starAvg", equalTo(0.0f))
			.body("data.categories.categoryNameSet", contains("INTESTINE"));

	}

	@DisplayName("할인 중인 상품을 조회하여 api 응답을 보낼 수 있다.")
	@Test
	void 할인_상품_조회() throws Exception{
		//given
		아이템_저장("16종혼합유산균 디에스", 10000, 0.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
		아이템_저장("16종혼합유산균 디에스1", 11000, 10.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
		아이템_저장("16종혼합유산균 디에스2", 12000, 9.0, CategoryName.INTESTINE, "(주)씨티씨바이오");

		ItemPageRequestDto pageDto = new ItemPageRequestDto();
		pageDto.setPage(1);
		pageDto.setSize(10);

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(pageDto)
			.log().all()
			.when()
			.get("/api/items/on-sale")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.size()", is(2))
			.body("data[0].itemId", equalTo(2))
			.body("data[1].itemId", equalTo(3))
			.body("pageInfo.page", equalTo(1))
			.body("pageInfo.size", equalTo(8))
			.body("pageInfo.totalElements", equalTo(2))
			.body("pageInfo.totalPages", equalTo(1));
	}

	@DisplayName("카테고리 별로 상품을 조회하여 api 응답을 보낼 수 있다.")
	@Test
	void 카테고리_상품_조회() throws Exception{
		//given
		아이템_저장("16종혼합유산균 디에스", 10000, 0.0, CategoryName.EYE, "(주)씨티씨바이오");
		아이템_저장("16종혼합유산균 디에스1", 11000, 10.0, CategoryName.EYE, "(주)씨티씨바이오");
		아이템_저장("16종혼합유산균 디에스2", 12000, 9.0, CategoryName.ETC, "(주)씨티씨바이오");

		ItemPageRequestDto pageDto = new ItemPageRequestDto();
		pageDto.setPage(1);
		pageDto.setSize(10);

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(pageDto)
			.queryParam("categoryName", "EYE")
			.log().all()
			.when()
			.get("/api/items/categories")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.size()", is(2))
			.body("data[0].itemId", equalTo(1))
			.body("data[1].itemId", equalTo(2))
			.body("pageInfo.page", equalTo(1))
			.body("pageInfo.size", equalTo(8))
			.body("pageInfo.totalElements", equalTo(2))
			.body("pageInfo.totalPages", equalTo(1));
	}


	@DisplayName("브랜드 조회를 통한 api 응답을 할 수 있다.")
	@Test
	void 브랜드_조회() throws Exception{
		//given
		아이템_저장("16종혼합유산균 디에스", 10000, 0.0, CategoryName.EYE, "");
		아이템_저장("16종혼합유산균 디에스1", 11000, 10.0, CategoryName.EYE, "");
		아이템_저장("16종혼합유산균 디에스2", 12000, 9.0, CategoryName.ETC, "(주)씨티씨바이오");

		ItemPageRequestDto pageDto = new ItemPageRequestDto();
		pageDto.setPage(1);
		pageDto.setSize(10);

		//when then
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(pageDto)
			.queryParam("brand", "(주)씨티씨바이오")
			.log().all()
			.when()
			.get("/api/items/brands")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.size()", is(1))
			.body("data[0].itemId", equalTo(3))
			.body("pageInfo.page", equalTo(1))
			.body("pageInfo.size", equalTo(8))
			.body("pageInfo.totalElements", equalTo(1))
			.body("pageInfo.totalPages", equalTo(1));
	}

	private void 메인_아이템_삽입() {

		for (int i = 10; i < 21; i++) {
			Item item1 = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
				.setNull("id")
				.setNull("itemCategory")
				.setNull("reviewIds")
				.set("categories", new Categories(Set.of(CategoryName.INTESTINE)))
				.set("information.productName", "testItem" + i + "판매량")
				.set("statistics.starAvg", 0.0)
				.set("statistics.reviewCount", 0)
				.set("statistics.view", 0)
				.set("statistics.sales", i)
				.set("information.price.realPrice", 10000)
				.set("information.price.discountPrice", 1000)
				.set("information.price.discountRate", 10.0)
				.set("information.price.originPrice", 11000)
				.sample();

			Item item2 = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
				.setNull("id")
				.setNull("itemCategory")
				.setNull("reviewIds")
				.set("categories", new Categories(Set.of(CategoryName.EYE)))
				.set("information.productName", "testItem" + i + "할인율")
				.set("statistics.starAvg", 0.0)
				.set("statistics.reviewCount", 0)
				.set("statistics.view", 0)
				.set("statistics.sales", 0)
				.set("information.price.realPrice", 10000)
				.set("information.price.discountPrice", 1000)
				.set("information.price.discountRate", 10.0 + i)
				.set("information.price.originPrice", 11000)
				.sample();

			itemCommandRepository.save(item1);
			itemCommandRepository.save(item2);
		}
	}

	private void 아이템_저장(
		String productName,
		int originPrice,
		double discountRate,
		CategoryName categoryName,
		String enterprise
	) {
		Information information = Information.builder()
			.enterprise(enterprise)
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
			.price(new Price(originPrice, discountRate))
			.build();

		Item item = Item.builder()
			.information(information)
			.statistics(new Statistic())
			.build();

		item.addIncludedCategories(Set.of(categoryName));
		item.getItemCategory().add(categoryName);

		itemCommandRepository.save(item);
	}

	@Nested
	@DisplayName("아이템 조회 필터링 api 테스트")
	class ItemSearch {

		@DisplayName("특정 키워드를 가진 아이템을 검색할 수 있다.")
		@Test
		void 아이템_검색() throws Exception {
			//given
			아이템_저장("16종혼합유산균 디에스", 10000, 10.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스2", 20000, 20.0, CategoryName.INTESTINE, "(주)씨티씨바이오");

			//when
			given()
				.log().all()
				.queryParam("keyword", "디에스")
				.when()
				.get("/api/items/search")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(2))
				.body("data[0].itemId", equalTo(1))
				.body("data[0].thumbnail", equalTo("thumbnailUrl"))
				.body("data[0].productName", equalTo("16종혼합유산균 디에스"))
				.body("data[0].enterprise", equalTo("(주)씨티씨바이오"))
				.body("data[0].mainFunction", equalTo("[프로바이오틱스] 유산균 증식 및 유해균 억제･배변활동 원활･장 건강에 도움을 줄 수 있음"))
				.body("data[0].baseStandard", equalTo(
					"1. 성상 : 고유의 향미가 있고 이미, 이취가 없는 흰노란색의 분말\n2. 프로바이오틱스 수 : 200,000,000,000(2000억) CFU/g 이상\n3. 대장균군 : 음성\n4. 납(mg/kg) : 1.0 이하\n5. 카드뮴(mg/kg) : 0.3 이하"))
				.body("data[0].realPrice", equalTo(9000))
				.body("data[0].discountRate", equalTo(0.1f))
				.body("data[0].discountPrice", equalTo(1000))
				.body("data[0].sales", equalTo(0))
				.body("data[0].starAvg", equalTo(0.0f))
				.body("data[0].reviewSize", equalTo(0))
				.body("data[0].categories.categoryNameSet", contains("INTESTINE"))

				.body("data[1].itemId", equalTo(2))
				.body("data[1].productName", equalTo("16종혼합유산균 디에스2"))
				.body("data[1]", hasKey("enterprise"))
				.body("data[1]", hasKey("mainFunction"))
				.body("data[1]", hasKey("baseStandard"))
				.body("data[1]", hasKey("realPrice"))
				.body("data[1]", hasKey("discountRate"))
				.body("data[1]", hasKey("discountPrice"))
				.body("data[1]", hasKey("sales"))
				.body("data[1]", hasKey("starAvg"))
				.body("data[1]", hasKey("reviewSize"))

				.body("pageInfo.page", equalTo(1))
				.body("pageInfo.size", equalTo(8))
				.body("pageInfo.totalElements", equalTo(2))
				.body("pageInfo.totalPages", equalTo(1));
		}

		@DisplayName("특정 가격대의 아이템을 검색할 수 있다.(1만원 ~ 1만 4999원)")
		@Test
		void 가격_검색() throws Exception {
			//given
			아이템_저장("16종혼합유산균 디에스", 10000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스2", 12000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스3", 13000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스4", 14000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스5", 15000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");

			given()
				.queryParam("low", 10000)
				.queryParam("high", 14999)
				.log().all()
				.when()
				.get("/api/items/search")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(4))
				.body("data[0].itemId", equalTo(1))
				.body("data[0].productName", equalTo("16종혼합유산균 디에스"))
				.body("data[1].itemId", equalTo(2))
				.body("data[1].productName", equalTo("16종혼합유산균 디에스2"))
				.body("data[2].itemId", equalTo(3))
				.body("data[2].productName", equalTo("16종혼합유산균 디에스3"))
				.body("data[3].itemId", equalTo(4))
				.body("data[3].productName", equalTo("16종혼합유산균 디에스4"))

				.body("pageInfo.page", equalTo(1))
				.body("pageInfo.size", equalTo(8))
				.body("pageInfo.totalElements", equalTo(4))
				.body("pageInfo.totalPages", equalTo(1));
		}

		@DisplayName("특정 가격대의 아이템을 검색할 수 있다.(0만원 ~ 9999원)")
		@Test
		void 가격_검색2() throws Exception {
			//given
			아이템_저장("16종혼합유산균 디에스", 10000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");


			given()
				.queryParam("low", 0)
				.queryParam("high", 9999)
				.log().all()
				.when()
				.get("/api/items/search")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data", empty())
				.body("pageInfo.page", equalTo(1))
				.body("pageInfo.size", equalTo(0))
				.body("pageInfo.totalElements", equalTo(0))
				.body("pageInfo.totalPages", equalTo(1));
		}


	}

	@Nested
	@DisplayName("아이템 정렬 api 테스트")
	class ItemSort {

		@DisplayName("가격이 저렴한 아이템 순으로 정렬하여 전달할 수 있다.")
		@Test
		void 가격_낮은_순() throws Exception {
			//given
			아이템_저장("16종혼합유산균 디에스", 10000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스2", 12000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스3", 13000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스4", 14000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스5", 15000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");

			//when
			given()
				.queryParam("page", 1)
				.queryParam("size", 2)
				.queryParam("sort", "PRICE_L")
				.queryParam("keyword", "디에스")
				.log().all()
				.when()
				.get("/api/items/search")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(5))
				.body("data[0].itemId", equalTo(1))
				.body("data[0].productName", equalTo("16종혼합유산균 디에스"))
				.body("data[1].itemId", equalTo(2))
				.body("data[1].productName", equalTo("16종혼합유산균 디에스2"))
				.body("data[2].itemId", equalTo(3))
				.body("data[2].productName", equalTo("16종혼합유산균 디에스3"))
				.body("data[3].itemId", equalTo(4))
				.body("data[3].productName", equalTo("16종혼합유산균 디에스4"))
				.body("data[4].itemId", equalTo(5))
				.body("data[4].productName", equalTo("16종혼합유산균 디에스5"))

				.body("pageInfo.page", equalTo(1))
				.body("pageInfo.size", equalTo(8))
				.body("pageInfo.totalElements", equalTo(5))
				.body("pageInfo.totalPages", equalTo(1));
		}

		@DisplayName("가격이 높은 순으로 정렬하여 전달할 수 있다.")
		@Test
		void 가격_높은_순() throws Exception {
			//given
			아이템_저장("16종혼합유산균 디에스", 10000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스2", 12000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스3", 13000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스4", 14000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스5", 15000, 0, CategoryName.INTESTINE, "(주)씨티씨바이오");


			given()
				.queryParam("page", 1)
				.queryParam("size", 2)
				.queryParam("sort", "PRICE_H")
				.queryParam("keyword", "디에스")
				.log().all()
				.when()
				.get("/api/items/search")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(5))
				.body("data[0].itemId", equalTo(5))
				.body("data[0].productName", equalTo("16종혼합유산균 디에스5"))
				.body("data[1].itemId", equalTo(4))
				.body("data[1].productName", equalTo("16종혼합유산균 디에스4"))
				.body("data[2].itemId", equalTo(3))
				.body("data[2].productName", equalTo("16종혼합유산균 디에스3"))
				.body("data[3].itemId", equalTo(2))
				.body("data[3].productName", equalTo("16종혼합유산균 디에스2"))
				.body("data[4].itemId", equalTo(1))
				.body("data[4].productName", equalTo("16종혼합유산균 디에스"))

				.body("pageInfo.page", equalTo(1))
				.body("pageInfo.size", equalTo(8))
				.body("pageInfo.totalElements", equalTo(5))
				.body("pageInfo.totalPages", equalTo(1));
		}

		@DisplayName("할인률이 높은 순으로 정렬하여 전달할 수 있다.")
		@Test
		void 할인률_높은_순() throws Exception {
			//given
			아이템_저장("16종혼합유산균 디에스", 10000, 10.0, CategoryName.EYE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스2", 12000, 20.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스3", 13000, 30.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스4", 14000, 40.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스5", 15000, 50.0, CategoryName.INTESTINE, "(주)씨티씨바이오");

			given()
				.queryParam("page", 1)
				.queryParam("size", 2)
				.queryParam("sort", "DISCOUNT_RATE_H")
				.log().all()
				.when()
				.get("/api/items/search")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(5))
				.body("data[0].itemId", equalTo(5))
				.body("data[0].productName", equalTo("16종혼합유산균 디에스5"))
				.body("data[1].itemId", equalTo(4))
				.body("data[1].productName", equalTo("16종혼합유산균 디에스4"))
				.body("data[2].itemId", equalTo(3))
				.body("data[2].productName", equalTo("16종혼합유산균 디에스3"))
				.body("data[3].itemId", equalTo(2))
				.body("data[3].productName", equalTo("16종혼합유산균 디에스2"))
				.body("data[4].itemId", equalTo(1))
				.body("data[4].productName", equalTo("16종혼합유산균 디에스"))

				.body("pageInfo.page", equalTo(1))
				.body("pageInfo.size", equalTo(8))
				.body("pageInfo.totalElements", equalTo(5))
				.body("pageInfo.totalPages", equalTo(1));
		}

		@DisplayName("할인률이 낮은 순으로 정렬하여 전달할 수 있다.")
		@Test
		void 할인률_낮은_순() throws Exception {
			//given
			아이템_저장("16종혼합유산균 디에스", 10000, 10.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스2", 12000, 20.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스3", 13000, 30.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스4", 14000, 40.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
			아이템_저장("16종혼합유산균 디에스5", 15000, 50.0, CategoryName.INTESTINE, "(주)씨티씨바이오");


			given()
				.queryParam("page", 1)
				.queryParam("size", 2)
				.queryParam("sort", "DISCOUNT_RATE_L")
				.log().all()
				.when()
				.get("/api/items/search")
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value())
				.body("data.size()", is(5))
				.body("data[4].itemId", equalTo(5))
				.body("data[4].productName", equalTo("16종혼합유산균 디에스5"))
				.body("data[3].itemId", equalTo(4))
				.body("data[3].productName", equalTo("16종혼합유산균 디에스4"))
				.body("data[2].itemId", equalTo(3))
				.body("data[2].productName", equalTo("16종혼합유산균 디에스3"))
				.body("data[1].itemId", equalTo(2))
				.body("data[1].productName", equalTo("16종혼합유산균 디에스2"))
				.body("data[0].itemId", equalTo(1))
				.body("data[0].productName", equalTo("16종혼합유산균 디에스"))

				.body("pageInfo.page", equalTo(1))
				.body("pageInfo.size", equalTo(8))
				.body("pageInfo.totalElements", equalTo(5))
				.body("pageInfo.totalPages", equalTo(1));
		}

	}
}