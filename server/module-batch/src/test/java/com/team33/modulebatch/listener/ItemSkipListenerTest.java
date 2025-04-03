package com.team33.modulebatch.listener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulebatch.domain.ErrorItemRepository;
import com.team33.modulebatch.domain.entity.ErrorItem;
import com.team33.modulebatch.step.SubscriptionOrderVO;

@ExtendWith(MockitoExtension.class)
class ItemSkipListenerTest {

    @Mock
    private ErrorItemRepository errorItemRepository;

    @DisplayName("결제 실패 시 에러 아이템을 저장한다")
    @Test
    void test1() {
        // given
        ItemSkipListener listener = new ItemSkipListener(errorItemRepository);
        
        SubscriptionOrderVO subscriptionOrderVO = new SubscriptionOrderVO();
        subscriptionOrderVO.setIdempotencyKey("test");
        
        RuntimeException exception = new RuntimeException("결제 실패");

        // when
        listener.onSkipInWrite(subscriptionOrderVO, exception);

        // then
        ArgumentCaptor<ErrorItem> captor = ArgumentCaptor.forClass(ErrorItem.class);
        verify(errorItemRepository, times(1)).save(captor.capture());
        
        ErrorItem savedItem = captor.getValue();
        verify(errorItemRepository).save(savedItem);
    }
}