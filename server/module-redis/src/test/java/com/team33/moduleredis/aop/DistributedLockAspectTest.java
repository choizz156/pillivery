package com.team33.moduleredis.aop;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.team33.moduleredis.application.aspect.DistributedLockService;
import com.team33.moduleredis.domain.annotation.DistributedLock;

@ExtendWith(MockitoExtension.class)
class DistributedLockAspectTest {

    @Mock
    private DistributedLockService distributedLockService;

    @InjectMocks
    private DistributedLockAspect distributedLockAspect;

    private TestTarget proxy;

    @BeforeEach
    void setUpEach(){
        TestTarget testTarget = new TestTarget();
        AspectJProxyFactory factory = new AspectJProxyFactory(testTarget);
        factory.addAspect(distributedLockAspect);
        proxy = factory.getProxy();
    }

    @DisplayName("lock을 획득할 수 있다.")
    @Test
    void test1() throws Exception {
        // given
        given(distributedLockService.hasLock(anyString(), anyLong(), anyLong()))
            .willReturn(true);

        // when
        proxy.lockTest();

        // then
        then(distributedLockService).should().hasLock("test", 4L, 6L);

    }

    @DisplayName("lock 획득 후 해제할 수 있다.")
    @Test
    void test3() throws Exception {
        //given
        given(distributedLockService.hasLock(anyString(), anyLong(), anyLong()))
            .willReturn(true);

        //when
        proxy.lockTest();
        //then
        then(distributedLockService).should().releaseLock("test");
    }

    @DisplayName("lock 획득 실패시 예외를 던진다.")
    @Test
    void test2() throws Exception {
        // given
        given(distributedLockService.hasLock(anyString(), anyLong(), anyLong()))
            .willReturn(false);

        // when  then
        assertThatThrownBy(proxy::lockTest)
        	.isInstanceOf(IllegalStateException.class);
    }





    public static class TestTarget {
        @DistributedLock(key = "test", tryLockTimeOutSecond = 4L, lockLeaseTimeOutSecond = 6L)
        public void lockTest() {
        }
    }
}