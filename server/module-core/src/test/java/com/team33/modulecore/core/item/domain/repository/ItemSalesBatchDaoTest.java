package com.team33.modulecore.core.item.domain.repository;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ItemSalesBatchDaoTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ItemSalesBatchDao itemSalesBatchDao;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(itemSalesBatchDao, "batchSize", 100);
    }

    @DisplayName("배치 업데이트")
    @Test
    void test1() {
        // given
        Map<Long, Long> itemsView = new HashMap<>();
        itemsView.put(1L, 10L);
        itemsView.put(2L, 20L);

        when(jdbcTemplate.batchUpdate(any(String.class), any(BatchPreparedStatementSetter.class)))
                .thenReturn(new int[] { 2 });

        // when
        itemSalesBatchDao.updateAll(itemsView);

        // then
        verify(jdbcTemplate, times(1)).batchUpdate(any(String.class), any(BatchPreparedStatementSetter.class));
    }


    @DisplayName("빈 데이터 처리")
    @Test
    void test3() {
        // given
        Map<Long, Long> itemsView = new HashMap<>();

        // when
        itemSalesBatchDao.updateAll(itemsView);

        // then
        verify(jdbcTemplate, times(0)).batchUpdate(any(String.class), any(BatchPreparedStatementSetter.class));
    }
}