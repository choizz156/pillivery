package com.team33.modulecore.core.item.event;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulecore.core.item.domain.repository.ItemSalesBatchDao;

@ExtendWith(MockitoExtension.class)
class ItemSaleCountedEventHandlerTest {

    @Mock
    private ItemSalesBatchDao itemSalesBatchDao;

    private ItemSaleCountedEventHandler itemSaleCountedEventHandler;

    @BeforeEach
    void setUp() {
        itemSaleCountedEventHandler = new ItemSaleCountedEventHandler(itemSalesBatchDao);

    }

    @DisplayName("아이템 판매 이벤트 발생 시 판매 수량이 정상적으로 집계된다")
    @Test
    void test1() {
        // given
        List<Long> itemIds = Arrays.asList(1L, 2L, 3L);
        ItemSaleCountedEvent event = new ItemSaleCountedEvent(itemIds);

        // when
        itemSaleCountedEventHandler.onItemSaleCounted(event);

        // then

        ConcurrentHashMap<Long, Long> saleCounts = itemSaleCountedEventHandler.getSaleCounts();
        assertThat(saleCounts).hasSize(3);
        assertThat(saleCounts.get(1L)).isEqualTo(1L);
        assertThat(saleCounts.get(2L)).isEqualTo(1L);
        assertThat(saleCounts.get(3L)).isEqualTo(1L);
    }

    @DisplayName("배치 업데이트가 성공적으로 수행되면 집계된 판매 수량이 초기화된다")
    @Test
    void test2() {
        // given
        List<Long> itemIds = List.of(1L, 2L, 3L);
        ItemSaleCountedEvent event = new ItemSaleCountedEvent(itemIds);
        itemSaleCountedEventHandler.onItemSaleCounted(event);

        // when
        itemSaleCountedEventHandler.increaseItemSale();

        // then
        ConcurrentHashMap<Long, Long> saleCounts = itemSaleCountedEventHandler.getSaleCounts();

        verify(itemSalesBatchDao, times(1)).updateAll(anyMap());
        assertThat(saleCounts).isEmpty();
    }

    @DisplayName("배치 업데이트 실패 시 집계된 판매 수량이 유지된다")
    @Test
    void test3() {
        // given
        ConcurrentHashMap<Long, Long> saleCounts = new ConcurrentHashMap<>();
        saleCounts.put(1L, 2L);

        doThrow(new RuntimeException("DB 에러")).when(itemSalesBatchDao).updateAll(any());

        // when
        itemSaleCountedEventHandler.increaseItemSale();

        // then
        verify(itemSalesBatchDao).updateAll(any());
        assertThat(saleCounts).hasSize(1);
        assertThat(saleCounts.get(1L)).isEqualTo(2L);
    }
}