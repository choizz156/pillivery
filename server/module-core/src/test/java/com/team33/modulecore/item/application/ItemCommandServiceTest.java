package com.team33.modulecore.item.application;

import com.team33.modulecore.item.domain.entity.Item;
import com.team33.modulecore.item.domain.mock.FakeItemCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ItemCommandServiceTest {

    @DisplayName("아이템을 조회하면서 view수를 늘릴 수 있다.")
    @Test
    void 아이템_조회수_증가_조회() throws Exception {
        //given
        ItemCommandService itemCommandService = new ItemCommandService(
            null,
            null,
            new FakeItemCommandRepository()
        );

        //when
        Item itemWithAddingView = itemCommandService.findItemWithAddingView(1L);

        //then

    }
}