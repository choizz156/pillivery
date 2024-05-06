package com.team33.modulecore.item.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.item.domain.mock.FakeItemQuerydslDao;
import com.team33.modulecore.item.domain.repository.ItemQueryRepository;
import com.team33.modulecore.item.dto.ItemPageRequestDto;
import com.team33.modulecore.item.dto.ItemResponseDto;
import com.team33.modulecore.item.dto.query.PriceFilterDto;
import com.team33.modulecore.item.dto.query.ItemPageDto;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Page;

@TestInstance(Lifecycle.PER_CLASS)
class ItemQueryServiceTest {

    ItemQueryRepository itemQueryRepository;

    @BeforeAll
    void beforeAll() {
        itemQueryRepository = new FakeItemQuerydslDao();
    }

    @DisplayName("조회된 아이템을 뷰 dto로 변환할 수 있다.")
    @Test
    void 가격에_따른_조회() throws Exception {
        //given
        ItemQueryService itemQueryService = new ItemQueryService(itemQueryRepository);

        var dto = new ItemPageRequestDto();
        dto.setPage(1);
        dto.setSize(14);
        //when
        Page<ItemResponseDto> filteredItemByPrice =
            itemQueryService.findFilteredItem(null, new PriceFilterDto(), ItemPageDto.from(dto));

        //then
        List<ItemResponseDto> content = filteredItemByPrice.getContent();
        assertThat(content).hasSize(1)
            .extracting("productName")
            .contains("test");
    }

    @DisplayName("조회된 결과를 dto로 변환할 수 있다.")
    @Test
    void 가격에_따른_조회2() throws Exception {
        //given
        ItemQueryService itemQueryService = new ItemQueryService(itemQueryRepository);

        var dto = new ItemPageRequestDto();
        dto.setPage(1);
        dto.setSize(14);
        //when
        Page<ItemResponseDto> filteredItemByPrice =
            itemQueryService.findItemOnSale(ItemPageDto.from(dto));

        //then
        List<ItemResponseDto> content = filteredItemByPrice.getContent();
        assertThat(content).hasSize(1)
            .extracting("productName")
            .contains("test");
    }
}