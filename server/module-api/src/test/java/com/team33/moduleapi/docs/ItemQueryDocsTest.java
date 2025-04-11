package com.team33.moduleapi.docs;

import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.team33.moduleapi.api.item.dto.ItemPageRequestDto;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Price;
import com.team33.modulecore.core.item.domain.Statistic;
import com.team33.modulecore.core.item.domain.entity.Item;
import com.team33.modulecore.core.item.domain.repository.ItemCommandRepository;

class ItemQueryDocsTest extends WebRestDocsSupport {

    @Autowired
    private ItemCommandRepository itemCommandRepository;

    @BeforeEach
    void setUp() {
        // 메인_아이템_삽입();
        아이템_저장("16종혼합유산균 디에스", 10000, 10.0, CategoryName.INTESTINE, "(주)씨티씨바이오");
    }

    @Test
    void 메인_아이템_조회_문서화() {
        given(spec)
            .log().all()
            .filter(document("get-main-items",
                responseFields(
                    fieldWithPath("data.discountRateItem[].itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.discountRateItem[].thumbnail").type(JsonFieldType.STRING).description("상품 썸네일"),
                    fieldWithPath("data.discountRateItem[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.discountRateItem[].enterprise").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data.discountRateItem[].mainFunction").type(JsonFieldType.STRING).description("주요기능"),
                    fieldWithPath("data.discountRateItem[].baseStandard").type(JsonFieldType.STRING).description("기준규격"),
                    fieldWithPath("data.discountRateItem[].realPrice").type(JsonFieldType.NUMBER).description("실제 가격"),
                    fieldWithPath("data.discountRateItem[].discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                    fieldWithPath("data.discountRateItem[].discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data.discountRateItem[].sales").type(JsonFieldType.NUMBER).description("판매량"),
                    fieldWithPath("data.discountRateItem[].starAvg").type(JsonFieldType.NUMBER).description("평균 별점"),
                    fieldWithPath("data.discountRateItem[].reviewSize").type(JsonFieldType.NUMBER).description("리뷰 수"),
                    fieldWithPath("data.discountRateItem[].categories.categoryNameSet").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                    fieldWithPath("createTime").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("data.saleItem[].itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.saleItem[].thumbnail").type(JsonFieldType.STRING).description("상품 썸네일"),
                    fieldWithPath("data.saleItem[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.saleItem[].enterprise").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data.saleItem[].mainFunction").type(JsonFieldType.STRING).description("주요기능"),
                    fieldWithPath("data.saleItem[].baseStandard").type(JsonFieldType.STRING).description("기준규격"),
                    fieldWithPath("data.saleItem[].realPrice").type(JsonFieldType.NUMBER).description("실제 가격"),
                    fieldWithPath("data.saleItem[].discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                    fieldWithPath("data.saleItem[].discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data.saleItem[].sales").type(JsonFieldType.NUMBER).description("판매량"),
                    fieldWithPath("data.saleItem[].starAvg").type(JsonFieldType.NUMBER).description("평균 별점"),
                    fieldWithPath("data.saleItem[].reviewSize").type(JsonFieldType.NUMBER).description("리뷰 수"),
                    fieldWithPath("data.saleItem[].categories.categoryNameSet").type(JsonFieldType.ARRAY).description("카테고리 목록"))))
            .when()
            .get("/api/items/main")
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    void 상품_상세_조회_문서화() {
        given(spec)
            .log().all()
            .filter(document("get-item-detail",
                pathParameters(
                    parameterWithName("itemId").description("상품 ID")),
                responseFields(
                    fieldWithPath("data.itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.thumbnail").type(JsonFieldType.STRING).description("상품 썸네일"),
                    fieldWithPath("data.descriptionImage").type(JsonFieldType.STRING).description("상품 상세 이미지"),
                    fieldWithPath("data.enterprise").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.statementNumber").type(JsonFieldType.STRING).description("품목제조번호"),
                    fieldWithPath("data.registeredDate").type(JsonFieldType.STRING).description("등록일"),
                    fieldWithPath("data.distributionPeriod").type(JsonFieldType.STRING).description("유통기한"),
                    fieldWithPath("data.sungsang").type(JsonFieldType.STRING).description("성상"),
                    fieldWithPath("data.servingUse").type(JsonFieldType.STRING).description("용법용량"),
                    fieldWithPath("data.preservePeriod").type(JsonFieldType.STRING).description("보관방법"),
                    fieldWithPath("data.intake").type(JsonFieldType.STRING).description("섭취방법"),
                    fieldWithPath("data.mainFunction").type(JsonFieldType.STRING).description("주요기능"),
                    fieldWithPath("data.baseStandard").type(JsonFieldType.STRING).description("기준규격"),
                    fieldWithPath("data.realPrice").type(JsonFieldType.NUMBER).description("실제 가격"),
                    fieldWithPath("data.originPrice").type(JsonFieldType.NUMBER).description("원가"),
                    fieldWithPath("data.discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                    fieldWithPath("data.discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data.sales").type(JsonFieldType.NUMBER).description("판매량"),
                    fieldWithPath("data.starAvg").type(JsonFieldType.NUMBER).description("평균 별점"),
                    fieldWithPath("data.categories.categoryNameSet").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                    fieldWithPath("createTime").type(JsonFieldType.STRING).description("생성 시간"))))
            .when()
            .get("/api/items/{itemId}", 1)
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    void 할인_상품_조회_문서화() {
        ItemPageRequestDto pageDto = new ItemPageRequestDto();
        pageDto.setPage(1);
        pageDto.setSize(10);

        given(spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(pageDto)
            .filter(document("get-discount-items",
                requestFields(
                    fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                    fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("sortOption").type(JsonFieldType.STRING).description("정렬 옵션 (SALES, PRICE, DISCOUNT_RATE)"),
                    fieldWithPath("direction").type(JsonFieldType.STRING).description("정렬 방향 (ASC, DESC)")),
                responseFields(
                    fieldWithPath("time").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data[].enterprise").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data[].mainFunction").type(JsonFieldType.STRING).description("주요기능"),
                    fieldWithPath("data[].baseStandard").type(JsonFieldType.STRING).description("기준규격"),
                    fieldWithPath("data[].discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data[].sales").type(JsonFieldType.NUMBER).description("판매량"),
                    fieldWithPath("data[].starAvg").type(JsonFieldType.NUMBER).description("평균 별점"),
                    fieldWithPath("data[].reviewSize").type(JsonFieldType.NUMBER).description("리뷰 수"),
                    fieldWithPath("data[].categories.categoryNameSet").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                    fieldWithPath("data[].itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data[].thumbnail").type(JsonFieldType.STRING).description("상품 썸네일"),
                    fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data[].realPrice").type(JsonFieldType.NUMBER).description("실제 가격"),
                    fieldWithPath("data[].discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                    fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                    fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 상품 수"),
                    fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"))))
            .when()
            .get("/api/items/on-sale")
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    void 카테고리_상품_조회_문서화() {
        ItemPageRequestDto pageDto = new ItemPageRequestDto();
        pageDto.setPage(1);
        pageDto.setSize(10);

        given(spec)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(pageDto)
            .queryParam("categoryName", "INTESTINE")
            .filter(document("get-category-items",
                requestFields(
                    fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                    fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("sortOption").type(JsonFieldType.STRING).description("정렬 옵션 (SALES, PRICE, DISCOUNT_RATE)"),
                    fieldWithPath("direction").type(JsonFieldType.STRING).description("정렬 방향 (ASC, DESC)")),
                requestParameters(
                    parameterWithName("categoryName").description("카테고리명")),
                responseFields(
                    fieldWithPath("time").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data[].enterprise").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data[].mainFunction").type(JsonFieldType.STRING).description("주요기능"),
                    fieldWithPath("data[].baseStandard").type(JsonFieldType.STRING).description("기준규격"),
                    fieldWithPath("data[].realPrice").type(JsonFieldType.NUMBER).description("실제 가격"),
                    fieldWithPath("data[].discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                    fieldWithPath("data[].discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data[].sales").type(JsonFieldType.NUMBER).description("판매량"),
                    fieldWithPath("data[].starAvg").type(JsonFieldType.NUMBER).description("평균 별점"),
                    fieldWithPath("data[].reviewSize").type(JsonFieldType.NUMBER).description("리뷰 수"),
                    fieldWithPath("data[].categories.categoryNameSet").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                    fieldWithPath("data[].itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data[].thumbnail").type(JsonFieldType.STRING).description("상품 썸네일"),
                    fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                    fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 상품 수"),
                    fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"))))
            .when()
            .get("/api/items/categories")
            .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    void 검색_상품_조회_문서화() {
        given(spec)
            .queryParam("keyword", "디에스")
            .filter(document("search-items",
                relaxedRequestParameters(
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("low").optional().description("최소 가격"),
                    parameterWithName("high").optional().description("최대 가격"),
                    parameterWithName("sort").optional().description("정렬 기준 (PRICE_L, PRICE_H, DISCOUNT_RATE_L, DISCOUNT_RATE_H)")),
                responseFields(
                    fieldWithPath("time").type(JsonFieldType.STRING).description("응답 시간"),
                    fieldWithPath("data[].itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data[].thumbnail").type(JsonFieldType.STRING).description("상품 썸네일"),
                    fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data[].enterprise").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data[].mainFunction").type(JsonFieldType.STRING).description("주요기능"),
                    fieldWithPath("data[].baseStandard").type(JsonFieldType.STRING).description("기준규격"),
                    fieldWithPath("data[].realPrice").type(JsonFieldType.NUMBER).description("실제 가격"),
                    fieldWithPath("data[].discountRate").type(JsonFieldType.NUMBER).description("할인율"),
                    fieldWithPath("data[].discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data[].sales").type(JsonFieldType.NUMBER).description("판매량"),
                    fieldWithPath("data[].starAvg").type(JsonFieldType.NUMBER).description("평균 별점"),
                    fieldWithPath("data[].reviewSize").type(JsonFieldType.NUMBER).description("리뷰 수"),
                    fieldWithPath("data[].categories.categoryNameSet").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                    fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지"),
                    fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                    fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 상품 수"),
                    fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"))))
            .when()
            .get("/api/items/search")
            .then()
            .log().all()
            .statusCode(200);
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
}