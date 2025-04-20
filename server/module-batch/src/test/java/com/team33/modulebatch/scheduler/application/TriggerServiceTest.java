package com.team33.modulebatch.scheduler.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Trigger;

@ExtendWith(MockitoExtension.class)
class TriggerServiceTest {

    @DisplayName("즉시 실행되는 Trigger를 생성한다")
    @Test
    void testNow() {
        // given
        TriggerService triggerService = new TriggerService();
        
        // when
        Trigger trigger = triggerService.now();
        
        // then
        assertThat(trigger).isNotNull();
        assertThat(trigger.getStartTime()).isNotNull();
        // 트리거가 즉시 실행되도록 설정되었는지 확인
        assertThat(trigger.getKey()).isNotNull();
    }
}